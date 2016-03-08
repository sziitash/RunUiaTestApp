package com.meizu.alimemtest;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Debug;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.csvreader.CsvWriter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dalvik.system.DexClassLoader;

//
///**
// * Created by libinhui on 2016/1/12.
// */
public class runUIAService extends Service {
    MyAsyncTask testcase = new MyAsyncTask();

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        Log.d("LBH", "onBind");
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
//        Log.i("benlee", "onCreate");
        if (testcase != null && testcase.getStatus() == AsyncTask.Status.RUNNING) {
            testcase.cancel(true); // 如果Task还在运行，则先取消它
        }
        super.onCreate();
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, runUIAService.class), 0);
        Notification noti = new Notification.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("点我没惊喜")
                .setContentText("内存性能测试运行中")
                .setContentIntent(contentIntent)
                .build();

        startForeground(1, noti);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent==null){ return START_STICKY_COMPATIBILITY; }
//        MyAsyncTask testcase = new MyAsyncTask();
//        ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
//        testcase.executeOnExecutor(mExecutorService);
        testcase.executeOnExecutor(MyAsyncTask.SERIAL_EXECUTOR);
        flags = START_REDELIVER_INTENT;
        return super.onStartCommand(intent, flags, startId);
//        return START_REDELIVER_INTENT;
    }

    private void createResultcsv(CsvWriter wr,String jarname){
        String[] csvtitle = {jarname};
        try {
        wr.writeRecord(csvtitle);
        } catch (IOException e) {
        e.printStackTrace();
        }
        String[] resultinfo = {"Count","Native_pss","Total_pss"};
        try {
        wr.writeRecord(resultinfo);
        } catch (IOException e) {
        e.printStackTrace();
        }
        }

    private static ArrayList<Integer> hasSelecteds() {
        ArrayList<Integer> selecteds = new ArrayList<Integer>();
        //获取已勾选的jar包，放到selecteds
        ArrayList<String> list = Ex_checkboxActivity.getJarList();
//        Log.i("benlee", String.valueOf(list.size()));
        for (int i = 0; i < list.size(); i++) {
            boolean isselecting = MyAdapter.getIsSelected().get(i);
            if (isselecting) {
                selecteds.add(i);
                }
        }
        return selecteds;
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            if (isCancelled()){
                return null;
            }
            ArrayList<String> list = Ex_checkboxActivity.getJarList();
//            Log.i("benlee", list.toString());
            ArrayList<Integer> selecteds = hasSelecteds();
//            Log.i("benlee", selecteds.toString());
            for (int x = 0; x < selecteds.size(); x++) {
                String ischeckmodel = list.get(selecteds.get(x));
                runUIAThread uia = new runUIAThread(ischeckmodel,getApplicationContext());
                uia.run();
                publishProgress(x);
            }
            return "Done";
        }


        @Override
        protected void onPostExecute(String result) {
            Log.i("benlee", result);
        }


        //该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
        @Override
        protected void onPreExecute() {
//            Log.i("benlee","Start");
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            if(isCancelled()){
                return;
            }
            int value = values[0];
//            Log.i("benlee","onProgressUpdate:"+String.valueOf(value));
        }
    }

    private class runUIAThread extends Thread {
        private final String ischeckmodel;
        private final Context mContext;
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        public runUIAThread(String checkedmodel,Context mContext){
            this.ischeckmodel = checkedmodel;
            this.mContext = mContext;
        }

    @Override
    public void run() {
        Object z = Ex_checkboxActivity.testcasemap.get(ischeckmodel);
        String[] result = (String[]) z;
        final String pname = result[0].toString();
        final String cname = result[1].toString();

        //直接运行jar
//        String commandstr = "uiautomator runtest " + ischeckmodel + " -c " + cname;
//        Log.i("benlee", commandstr);
//        String errorstr = ShellUtils.execCommand(commandstr, false).errorMsg;
//            //打印一下uiautomator启动信息
//        Log.i("benlee", errorstr);
//        if(errorstr.contains("Segmentation")){
//            ShellUtils.execCommand(commandstr, false);
//        }
//      -------------------------------------------------------------------------------
        //获取jar的testcase,逐条运行
        try {
            List<String> testcaseList = getTestClassesFromJars(mContext,ischeckmodel,cname);
            for(int tccount = 0;tccount < testcaseList.size();tccount++){
                String commandstr = "uiautomator runtest " + ischeckmodel + " -c " + cname +"#"+testcaseList.get(tccount);
                Log.i("benlee",cname +"#"+testcaseList.get(tccount));
                String errorstr = ShellUtils.execCommand(commandstr, true).errorMsg;
//                Log.i("benlee", errorstr);
                if(errorstr.contains("Segmentation")){
                    ShellUtils.execCommand(commandstr, true);
                }
            }
//            String commandstr = "uiautomator runtest " + ischeckmodel + " -c " + cname +"#test002LoginAndStartSync";
//            ShellUtils.execCommand(commandstr, true);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        SystemClock.sleep(2000);
        Log.i("benlee", pname);
        String tpid = getProcessId(am,pname);
        Log.i("benlee",tpid);
        if (tpid == null||tpid.equals("")||tpid.equals("-1")){
            Log.i("benlee", "Not Found The pid!");
        }
        else{
            int[] pidint = {Integer.valueOf(tpid)};
            SystemClock.sleep(10000);
            //Debug.MemoryInfo获取进程内存当前大小
            Debug.MemoryInfo[] pidinfo = am.getProcessMemoryInfo(pidint);
            String pjar = ischeckmodel.replace(Ex_checkboxActivity.jardir, "");
            //删除、新建csv文件
            ShellUtils.execCommand("rm -f /sdcard/jars/" + pjar + ".csv", true);
            ShellUtils.execCommand("touch /sdcard/jars/" + pjar + ".csv", true);
            SystemClock.sleep(1000);
            CsvWriter wr = new CsvWriter("/sdcard/jars/" + pjar + ".csv", ',', Charset.forName("GBK"));
            createResultcsv(wr, pjar);
            //获取10次内存信息
            for (int a = 0; a < 10; a++) {
                SystemClock.sleep(1000);
                String totalpss = String.valueOf(pidinfo[0].getTotalPss());
                String nativepss = String.valueOf(pidinfo[0].nativePss);
                String[] numresult = {String.valueOf(a + 1), nativepss, totalpss};
                try {
                    wr.writeRecord(numresult);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            wr.close();
        }
//        String uiapid = ShellUtils.execCommand("/system/bin/ps|grep uiautomator|/data/busybox awk '{print $2}'", false, true).successMsg;
//        if (uiapid.equals("")){
//            Log.i("benlee","uiautomator is dead");
//        }
//        else{
//            ShellUtils.execCommand("/system/bin/kill "+uiapid,false);
//        }
    }
    }

    //	@SuppressWarnings("deprecation")


    @Override
    public void onDestroy() {
//        manager.cancel(0);
//        stopSelfResult(1);
        Log.i("benlee","onDestroy");
        testcase.cancel(true);
        stopForeground(true);
        super.onDestroy();
    }

    private String getProcessId(ActivityManager am, String processStr){
        List<ActivityManager.RunningAppProcessInfo> procList = null;
        int result=-1;

        procList = am.getRunningAppProcesses();
        for (Iterator<ActivityManager.RunningAppProcessInfo> iterator = procList.iterator(); iterator.hasNext();)
        {
            ActivityManager.RunningAppProcessInfo procInfo = iterator.next();
            if(procInfo.processName.equals(processStr))
            {
                result=procInfo.pid;
                break;
            }
        }
        return String.valueOf(result);
    }


    public static List<String> getTestClassesFromJars(Context context, String jarPath, String testCase) throws ClassNotFoundException {
        String dexPath = jarPath + File.pathSeparator + "/system/framework/android.test.runner.jar" + File.pathSeparator + "/system/framework/uiautomator.jar";
//        Log.i("dexPath",dexPath);
        String dexOutputDir = context.getApplicationInfo().dataDir;
//        Log.i("dexOutputDir",dexOutputDir);
        DexClassLoader classLoader = new DexClassLoader(dexPath, dexOutputDir, null, context.getClass().getClassLoader());
        List<String> caseList = new ArrayList<String>();
        Class cls = classLoader.loadClass(testCase);
        Method[] methods = cls.getMethods();
        for (Method m : methods) {
            if(m.getName().startsWith("test")){
                caseList.add(m.getName());
            }
        }
        return caseList;
    }


}