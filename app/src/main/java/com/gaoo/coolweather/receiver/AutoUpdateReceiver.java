package com.gaoo.coolweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gaoo.coolweather.service.AutoUpdateService;

public class AutoUpdateReceiver extends BroadcastReceiver {
    public AutoUpdateReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        在 onReceive()方法中去启动 AutoUpdateService，就可以实现后台定时更新的功能
        Intent i = new Intent(context, AutoUpdateService.class);
        context.startService(i);
    }
}
