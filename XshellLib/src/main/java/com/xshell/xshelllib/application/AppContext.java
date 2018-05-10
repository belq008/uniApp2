package com.xshell.xshelllib.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.xshell.xshelllib.utils.PreferenceUtil;
import com.xshell.xshelllib.utils.Write2SDCard;
import com.xshell.xshelllib.view.LockPatternUtils;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class AppContext extends Application {
    @SuppressWarnings("unused")
    private static final String TAG = "LCApplication";
    public static Context CONTEXT;
    public static AppContext APPCONTEXT;
    private LockPatternUtils mLockPatternUtils;


    @Override
    public void onCreate() {
        super.onCreate();
        CONTEXT = this;
        APPCONTEXT = this;
        mLockPatternUtils = new LockPatternUtils(this);
        init();
        Log.e("huanghu", "AppContext初始化几次。。。。。。");
    }


    public LockPatternUtils getLockPatternUtils() {
        return mLockPatternUtils;
    }

    private void init() {
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }


    // 打印log
    public static void log(String tag, String msg) {
        Log.d(tag, msg);
    }


}
