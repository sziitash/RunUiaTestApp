package com.meizu.alimemtest;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by libinhui on 2016/3/1.
 */
public class getfpsActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fpsmessage);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

