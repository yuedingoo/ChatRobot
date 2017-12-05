package com.yueding.chatrobot.application;

import android.app.Application;
import android.content.Context;

import com.iflytek.cloud.SpeechUtility;
import com.yueding.chatrobot.R;

/**
 * Created by yueding on 2017/12/1.
 */

public class MyApplication extends Application {
    private Context context;
    @Override
    public void onCreate() {
        context = getApplicationContext();
        SpeechUtility.createUtility(context, "appid=" + getString(R.string.app_id));
        super.onCreate();
    }
}
