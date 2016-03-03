package com.meizu.alimemtest;

import android.app.Activity;
import android.content.Intent;
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

}
