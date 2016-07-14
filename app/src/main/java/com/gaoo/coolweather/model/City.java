package com.gaoo.coolweather.model;

/**
 * City类
 */
public class City {
    private int id;
    private String cityName; //城市名称
    private String cityCode; //城市代号
    private int provinceId; //省份 id province_id 是City 表关联 Province表的外键

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
