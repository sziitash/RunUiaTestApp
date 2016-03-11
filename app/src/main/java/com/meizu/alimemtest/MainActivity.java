package com.meizu.alimemtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by libinhui on 2016/3/1.
 */
public class MainActivity extends Activity{
    private String TAG = "benlee";

    private String fn_dropbear;
    private String fn_dropbearconvert;
    private String fn_dropbearkey;
    private String fn_scp;
    private String fn_ssh;
    private String fn_sftp_server;
    private String fn_busybox;
    private String fn_auth_keys;

    Button stabilityBtn;
    Button activityPerformanceBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        make_dirs_for_dropbear();
        extract_asset();
        new SshdDeamonTask().start();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.mainactivity);
        getWidget();
        regiestListener();
        String ipaddr = getwifiip();
        TextView ipa = (TextView) findViewById(R.id.connect);
        ipa.setText("连接地址和端口：" + ipaddr);
        Toast.makeText(getApplicationContext(), "sshd服务初始中", Toast.LENGTH_SHORT).show();
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
//        if(is_dropbear_running()){
//            Util.exec(fn_busybox + " killall -9 dropbear");
//            android.os.Process.killProcess(android.os.Process.myPid()); // clean myself all
//            System.exit(0);
//        }
        Log.i(TAG, "main_onDestory");
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

    public String get_dropbear_home_dir()
    {
        return MainActivity.this.getFilesDir().getParent() + File.separator + "home";
    }

    public String get_dropbear_conf_dir()
    {
        return MainActivity.this.getFilesDir().getParent() + File.separator + "home/.ssh";
    }

    public String get_dropbear_bin_dir()
    {
        return MainActivity.this.getFilesDir().getParent() + File.separator + "dropbear";
    }

    public void make_dirs_for_dropbear()
    {
        File f = new File(this.getApplicationContext().getFilesDir() + "/");
        f.mkdirs();

        sshdUtil.mkdirs(get_dropbear_home_dir());
        sshdUtil.mkdirs(get_dropbear_bin_dir());
        sshdUtil.mkdirs(get_dropbear_conf_dir());
    }

    private boolean is_dropbear_running()
    {
        String txt = sshdUtil.exec_out(fn_busybox + " ps");
        if (txt.isEmpty()||txt==null) {
            return false;
        }
        else if(txt.contains(fn_dropbear)){
            return true;
        }
        return false;
    }

    public String get_config_sshd_port() {
        SharedPreferences sp = getSharedPreferences("config", 0);
        return sp.getString("sshd_port", "22022");
    }

    public String get_config_sshd_passwd() {
        SharedPreferences sp = getSharedPreferences("config", 0);
        return sp.getString("sshd_passwd", "123123");
    }

    public void extract_asset()
    {
        String dir_conf = get_dropbear_conf_dir();
        String dir_bin = get_dropbear_bin_dir();

        fn_dropbear = sshdUtil.extractAssetToDir(this, "dropbear", dir_bin, "dropbear", false);
        Log.i("benlee-extract_asset", fn_dropbear);
        sshdUtil.exec("chmod 777 " + fn_dropbear);

        fn_dropbearconvert = sshdUtil.extractAssetToDir(this, "dropbearconvert", dir_bin, "dropbearconvert", false);
        sshdUtil.exec("chmod 777 " + fn_dropbearconvert);

        fn_dropbearkey = sshdUtil.extractAssetToDir(this, "dropbearkey", dir_bin, "dropbearkey", false);
        sshdUtil.exec("chmod 777 " + fn_dropbearkey);

        fn_scp = sshdUtil.extractAssetToDir(this, "scp", dir_bin, "scp", false);
        sshdUtil.exec("chmod 777 " + fn_scp);

        fn_ssh = sshdUtil.extractAssetToDir(this, "ssh", dir_bin, "ssh", false);
        sshdUtil.exec("chmod 777 " + fn_ssh);

        fn_sftp_server = sshdUtil.extractAssetToDir(this, "sftp-server", dir_bin, "sftp-server", false);
        sshdUtil.exec("chmod 777 " + fn_sftp_server);

        // curl -O http://www.busybox.net/downloads/binaries/latest/busybox-armv6l <-- OLD
        // curl -O http://www.busybox.net/downloads/binaries/latest/busybox-armv7l
        fn_busybox = sshdUtil.extractAssetToDir(this, "busybox-armv7l", dir_bin, "busybox", false);
        sshdUtil.exec("chmod 777 " + fn_busybox);

//        File id_dss = new File(dir_conf, "id_dss"); // dropbear_dss_host_key
//        if (!id_dss.exists()) {
//            Log.i(TAG, "to make dss key ...");
//            Util.exec(fn_dropbearkey + " -t dss -f " + id_dss.getAbsolutePath());
//            Log.i("benlee",fn_dropbearkey + " -t dss -f " + id_dss.getAbsolutePath());
//        }
//
//        File id_rsa = new File(dir_conf, "id_rsa"); // dropbear_rsa_host_key
//        if (!id_rsa.exists()) {
//            Log.i(TAG, "to make rsa key ...");
//            Util.exec(fn_dropbearkey + " -t rsa -f " + id_rsa.getAbsolutePath());
//            Log.i("benlee", fn_dropbearkey + " -t dss -f " + id_dss.getAbsolutePath());
//        }

//        fn_auth_keys = Util.extractAssetToDir(this, "authorized_keys", dir_conf, "authorized_keys", false);
//        Util.exec("chmod 600 " + fn_auth_keys);

        String dir_home = get_dropbear_home_dir();
        String tmp = sshdUtil.extractAssetToDir(this, "profile", dir_home, ".profile", false);
        sshdUtil.exec("chmod 600 " + tmp);
    }

    private class SshdDeamonTask extends Thread {

        @Override
        public void run() {
            String dir_conf = get_dropbear_conf_dir();
            Log.i(TAG,"dir_conf:"+dir_conf);
//            String dir_home = get_dropbear_home_dir();
//            String dir_bin = get_dropbear_bin_dir();

            File id_dss = new File(dir_conf, "id_dss"); // dropbear_dss_host_key
            if (!id_dss.exists()) {
                Log.i(TAG, "to make dss key ...");
                sshdUtil.exec(fn_dropbearkey + " -t dss -f " + id_dss.getAbsolutePath());
                Log.i("benlee",fn_dropbearkey + " -t dss -f " + id_dss.getAbsolutePath());
            }

            File id_rsa = new File(dir_conf, "id_rsa"); // dropbear_rsa_host_key
            if (!id_rsa.exists()) {
                Log.i(TAG, "to make rsa key ...");
                sshdUtil.exec(fn_dropbearkey + " -t rsa -f " + id_rsa.getAbsolutePath());
                Log.i("benlee", fn_dropbearkey + " -t dss -f " + id_dss.getAbsolutePath());
            }
//
//            File authorized_keys = new File(dir_conf, "authorized_keys");
//            if(authorized_keys.exists()){
//                Log.i(TAG,"authorized_keys is exists!");
//                fn_auth_keys = Util.extractAssetToDir(getApplicationContext(), "authorized_keys", dir_conf, "authorized_keys", false);
//                Util.exec("chmod 600 " + fn_auth_keys);
//            }
//            else{
//                Log.i(TAG,"authorized_keys is not exists!");
//                SystemClock.sleep(3000);
//                File newauthorized_keys = new File(dir_conf, "authorized_keys");
//                Log.i(TAG, String.valueOf(newauthorized_keys.exists()));
//            }

            SharedPreferences sp = getSharedPreferences("config", 0);
            String sshd_passwd = sp.getString("sshd_passwd", "123123");
            String sshd_port = sp.getString("sshd_port", "22022");

            Log.i(TAG, "to start dropbear in background ...");

            int uid = getApplicationInfo().uid;
            int gid = uid;
            String uname = "shell"; //  UidNames.getUidName(uid);

            String cli = fn_dropbear + " -A -N " + uname
                    + " -U " + uid
                    + " -G " + gid
                    + " -r " + id_dss.getAbsolutePath()
//                    + " -r " + "/data/data/com.benlee.FatAutoTester/home/.ssh/id_dss"
                    + " -r " + id_rsa.getAbsolutePath()
//                    + " -r " + "/data/data/com.benlee.FatAutoTester/home/.ssh/id_rsa"
//                    + " -f " + fn_auth_keys
                    + " -C " + get_config_sshd_passwd()
                    + " -p " + get_config_sshd_port()
                    //+ " -F "
                    //+ " -v -v -v -v -v -v"
                    ;

            Log.i(TAG, cli);
            sshdUtil.exec(cli);
//            ShellUtils.execCommand(cli,true);
        }
    }

}
