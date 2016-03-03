package com.meizu.alimemtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by libinhui on 2016/3/1.
 */
public class fpsBroadcast extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.meizu.alimemtest.fpsservice")){
            //创建一个新的intent对象，并设置相关参数等启动service
            Intent serviceIntent = new Intent();
            //设置intent的action
            serviceIntent.setAction("com.meizu.alimemtest.fpsservice");
            //设置intent的参数
            serviceIntent.setPackage("com.meizu.alimemtest");
            //通过context启动service
            context.startService(serviceIntent);
        }
    }
 }

