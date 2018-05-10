package com;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.xshell.xshelllib.R;
import com.xshell.xshelllib.application.AppConstants;

import com.xshell.xshelllib.tools.cordovasqlite.SQLiteAndroidDatabase;
import com.xshell.xshelllib.ui.XinyuHomeActivity;

import com.xshell.xshelllib.utils.*;
import com.xshell.xshelllib.view.UpdateContentDialog;


import org.apache.cordova.engine.SystemWebView;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;


public class TestActivity extends XinyuHomeActivity {

    private MYInnerReceiver homePageReceiver;
    private TestActivity mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        //----------无状态栏，全屏----------TODO 去掉设置了状态栏颜色

        setWindowStatus();

        mContext = this;
        String url = null;
        url = "file:///" + getFilesDir().getAbsolutePath() + File.separator + "index.html";

        mRegReceiver();
        loadUrl(url);
        new UiHelper(this).start();
        // appView.getView().setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        RequestWebViewURL.getInstance(mContext).requestURl(appView);

        //PermissionUtil.getInstance(this).checkPermission(mContext);


    }


    /**
     * 设置状态栏样式
     */
    private void setWindowStatus() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //如果是小米手机
        if (android.os.Build.MANUFACTURER.equals("Xiaomi")) {
            CommonUtil.setStatusBarDarkMode(false, this);
        } else {
            setcolorfulStatusBar();
        }


    }


    private void mRegReceiver() {
        homePageReceiver = new MYInnerReceiver();
        IntentFilter filter = new IntentFilter();
        // 刷新页面
        filter.addAction(AppConstants.RELOAD_HOME_PAGE);
        filter.addAction("openNOTIFICATION");
        registerReceiver(homePageReceiver, filter);
    }


    /**
     * 广播接收器
     *
     * @author zzy, wn
     */
    private class MYInnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (AppConstants.RELOAD_HOME_PAGE.equals(action)) {
                SystemWebView v = (SystemWebView) appView.getEngine().getView();
                v.reload();
            } else if ("openNOTIFICATION".equals(action)) {
                if (CommonUtil.isNotificationEnable(mContext)) {
                    if (VERSION.SDK_INT >= VERSION_CODES.BASE) {
                        // 进入设置系统应用权限界面
                        intent = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(intent);
                        return;
                    } else if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                        // 运行系统在5.x环境使用
                        // 进入设置系统应用权限界面
                        intent = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(intent);
                        return;
                    }
                    return;
                }
            }

        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onXshellEvent(XshellEvent event) {
        if (event.what == com.xshell.xshelllib.utils.AppConsts.APP_Bar_Style) {
            Log.e("amtf", "进入到testAActivity 的Event");
            if (!TextUtils.isEmpty(event.msg)) {
                if (event.msg.equals("dark")) {
                    // TextBarColorUtil.StatusBarLightMode(mContext);
                    TextBarColorUtil.setDarkStatusIcon(mContext, true);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        TextBarColorUtil.setDarkStatusIcon(mContext, false);
                    }
                }

            }
        }
    }

    /**
     * 设置沉浸式状态栏
     */

    private void setcolorfulStatusBar() {
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setTintColor(getResources().getColor(com.xshell.xshelllib.R.color.white));

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        com.xshell.xshelllib.utils.AppConsts.PUSH_MSG = intent.getStringExtra("extra");
    }

    public int getContentViewRes() {
        return com.xshell.xshelllib.R.layout.xinyusoft_main;
    }

    @Override
    public int getLinearLayoutId() {
        return com.xshell.xshelllib.R.id.linearLayout;
    }

    @Override
    public boolean isShowGuidePage() {
        if (PreferenceUtil.getInstance().hadFirstRun()) {
            PreferenceUtil.getInstance().setFirstRun(false);
            return false;
        }
        return true;


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (homePageReceiver != null) {
            unregisterReceiver(homePageReceiver);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PermissionUtil.MY_PERMISSIONS_CODE) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    //判断是否勾选禁止后不再询问
                    boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(mContext, permissions[i]);
                    if (showRequestPermission) {
                        Toast.makeText(mContext, permissions[i] + "权限未申请", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
