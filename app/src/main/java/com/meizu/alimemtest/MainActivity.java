package com.meizu.alimemtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by libinhui on 2016/3/1.
 */
public class MainActivity extends Activity{
    Button stabilityBtn;
    Button activityPerformanceBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String localip = getwifiip();
        Log.i("benlee", "ip:"+localip);
        Log.i("benlee","sn:"+getSN());
        setContentView(R.layout.mainactivity);
        getWidget();
        regiestListener();
    }

    public void getWidget(){
        stabilityBtn = (Button)findViewById(R.id.button);
        activityPerformanceBtn = (Button)findViewById(R.id.button2);
    }

    public void regiestListener(){
        stabilityBtn.setOnClickListener(stability);
        activityPerformanceBtn.setOnClickListener(activityPerformance);
    }

    public Button.OnClickListener stability = new Button.OnClickListener(){
        public void onClick(View view){
            Intent intent = new Intent(MainActivity.this,Ex_checkboxActivity.class);
            startActivity(intent);
        }
    };

    public Button.OnClickListener activityPerformance = new Button.OnClickListener(){
        public void onClick(View view){
            Intent intent = new Intent(MainActivity.this,getfpsActivity.class);
            startActivity(intent);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("hello", "main_onDestroy");
    }

    public String getwifiip() {
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);
        return ip;
    }

    private String intToIp(int i) {
        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }

    private String getSN(){
        String sn = ShellUtils.execCommand("getprop ro.serialno",false).successMsg;
        return sn;
    }

}
