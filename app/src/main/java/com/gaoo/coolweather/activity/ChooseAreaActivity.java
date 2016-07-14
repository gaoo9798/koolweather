package com.gaoo.coolweather.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gaoo.coolweather.R;
import com.gaoo.coolweather.db.CoolWeatherDB;
import com.gaoo.coolweather.model.City;
import com.gaoo.coolweather.model.County;
import com.gaoo.coolweather.model.Province;
import com.gaoo.coolweather.utils.HttpCallbackListener;
import com.gaoo.coolweather.utils.HttpUtil;
import com.gaoo.coolweather.utils.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * 编写用于遍历省市县数据的 activity
 */
public class ChooseAreaActivity extends AppCompatActivity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> mAdapter;
    private CoolWeatherDB mCoolWeaterDB;
    private List<String> dataList = new ArrayList<>();

    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;

    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的城市
     */
    private City selectedCity;
    /**
     * 当前选中的级别
     */
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_area);

        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);

        listView.setAdapter(mAdapter);
        mCoolWeaterDB = CoolWeatherDB.getInstance(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
            }
        });
        queryProvinces(); // 加载省级数据
    }

    /**
     * 加载省级数据
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryProvinces() {
        provinceList = mCoolWeaterDB.loadProvinces(); //从数据库中读取省级数据
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            mAdapter.notifyDataSetChanged(); //刷新界面

            listView.setAdapter(mAdapter);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            //从服务器加载数据
            queryFromServer(null, "province");
        }
    }


    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCities() {
        cityList = mCoolWeaterDB.loadCities(selectedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            mAdapter.notifyDataSetChanged(); //刷新界面
            listView.setSelection(0); //默认选择第一个条目

            titleText.setText(selectedProvince.getProvinceName()); //设置标题栏名称
            currentLevel = LEVEL_CITY;
        } else {
            //从服务器查找
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCounties() {
        countyList = mCoolWeaterDB.loadCounties(selectedCity.getId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            mAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
        } else {
            //从服务器查询数据
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }

    /**
     * 从服务器获取数据
     * 根据传入的代号和类型从服务器上查询 省 市 县 数据
     *
     * @param code
     * @param type
     */
    private void queryFromServer(String code, final String type) {
        String address;
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        Log.d("ChooseAreaActivity-----", address);
        showProgressDialog(); //TODO

        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvincesResponse(mCoolWeaterDB, response);
                } else if ("city".equals(type)) {
                    result = Utility.handleCitiesResponse(mCoolWeaterDB, response, selectedProvince.getId());

                } else if ("county".equals(type)) {
                    result = Utility.handleCountiesResponse(mCoolWeaterDB, response, selectedCity.getId());
                }
                if (result) {
                    // 通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog(); //关闭对话框
                            if ("province".equals(type)) {
                                /**
                                 * 由于 queryProvinces()方法牵扯到了 UI 操作，因此必须要在主线程中调用，
                                 * 这里借助了 runOnUiThread()方法来实现从子线程切换到主线程
                                 */
                                queryProvinces(); //再次调用了 queryProvinces()方法来重新加载省级数据
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
// 通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog(); //关闭对话框
                        Toast.makeText(ChooseAreaActivity.this, "加载失败 onError", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }

    //显示对话框
    private void showProgressDialog() {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(ChooseAreaActivity.this);
                    progressDialog.setMessage("正在加载中...");
                    progressDialog.setCanceledOnTouchOutside(false);
                }
                progressDialog.show();
    }

    //关闭对话框
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * 根据Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出。
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            finish();
        }
    }
}
