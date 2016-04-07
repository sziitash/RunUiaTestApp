package com.meizu.alimemtest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.meizu.alimemtest.Utils.utils;

import java.util.List;

/**
 * Created by libinhui on 2016/3/1.
 */
public class getFPSService extends Service{
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        runTestThread rt = new runTestThread();
        rt.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class runTestThread extends Thread{
        @Override
        public void run() {
            String currentActivity = getCurrentActivity();
            String[] x = currentActivity.split("/\\.");
            String pcname = x[0];
            String actname = x[1];
            String newactname = pcname+"/"+pcname+"."+actname;
            Log.i("benlee",newactname);
            int xMax = 1080;
//            int yMax = 1920;
            int xstart= xMax*3/10;
//            int ystart = yMax*3/10;
            int xEnd = xMax*8/10;
//            int yEnd = yMax*8/10;
            ShellUtils.execCommand("dumpsys SurfaceFlinger --latency-clear", true);
            SystemClock.sleep(1000);
            String command = "input swipe " + String.valueOf(xEnd) + " 960 " + String.valueOf(xstart) + " 960 100";
            ShellUtils.execCommand(command, true);
            SystemClock.sleep(1000);
            ShellUtils.execCommand("rm -f /sdcard/1fps.log", true);
            SystemClock.sleep(1000);
            String[] commandstr = {"dumpsys SurfaceFlinger --latency "+newactname+" >> /sdcard/1fps.log"};
            ShellUtils.execCommandutf(commandstr,true,true);
            SystemClock.sleep(1000);
            List<String> strs = utils.readFileByLines("/sdcard/1fps.log");
//            for (String res:strs){
//                Log.i("benlee",res);
//            }
            String refreshPeriod = strs.get(0);
            Log.i("benlee","refreshPeriod:"+refreshPeriod);
            Log.i("benlee","size:"+String.valueOf(strs.size()));
            int a = 1;
            while(true){
                if(a==strs.size()-2){
                    break;
                }
                long res = Long.valueOf(strs.get(a+1)) - Long.valueOf(strs.get(a));
                long fps = 1000/(res/1000000);
//                Log.i("benlee",strs.get(a+1)+"-"+strs.get(a)+"="+String.valueOf(res));
                Log.i("benlee", String.valueOf(fps));
                a++;
            }
        }
    }

//    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String getCurrentActivity(){
        String res = ShellUtils.execCommand("dumpsys activity top|grep ACTIVITY",true).successMsg;
        String actname = res.split(" ")[3];
        return actname;
    }

//    private void getCommandStr(String newactname){
//        Process process = null;
//        DataOutputStream os = null;
//        FileOutputStream fo = null;
//        try {
//            process = Runtime.getRuntime().exec("su dumpsys SurfaceFlinger --latency "+newactname);
//            os = new DataOutputStream(process.getOutputStream());
//            os.writeBytes("\n");
//            os.flush();
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}
