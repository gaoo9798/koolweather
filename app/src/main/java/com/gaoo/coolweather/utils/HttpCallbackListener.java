package com.gaoo.coolweather.utils;

/**
 * HttpUtil 类中使用到了 HttpCallbackListener 接口来回调方法返回的结果
 * 添加 该接口
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);

}
