package com.xshell.xshelllib.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.xshell.xshelllib.application.AppConstants;
import com.xshell.xshelllib.receiver.NetWorkBroadCastReceiver;
import com.xshell.xshelllib.utils.IpUtil;
import com.xshell.xshelllib.utils.XLogUpload;

import org.apache.cordova.CordovaActivity;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 主界面
 *
 * @author zzy
 */
public abstract class XinyuHomeActivity extends CordovaActivity {

    private static final String TAG = "XinyuHomeActivity";
    private InnerReceiver receiver;
    private NetWorkBroadCastReceiver netReceiver;
    private Activity xinyuHomeContext;

    final int REQUEST_Permission_Code = 1;


    String[] permissions = new String[]{Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};
    List<String> mdenyList = new ArrayList<>();


    private void checkMyPermission() {
        mdenyList.clear();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(xinyuHomeContext, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mdenyList.add(permissions[i]);
            }
        }
        if (mdenyList.isEmpty()) {//未授予的权限为空，表示都授予了

        } else {//请求权限方法
            String[] permissions = mdenyList.toArray(new String[mdenyList.size()]);//将List转为数组
            ActivityCompat.requestPermissions(xinyuHomeContext, permissions, REQUEST_Permission_Code);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_Permission_Code:
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "您拒绝了" + permissions[i] + "权限", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            default:
                break;

        }
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        xinyusoftInit();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkMyPermission();
        }
    }

    private void xinyusoftInit() {
        if (IpUtil.getIp().equals("127.0.0.1")) {
            IpUtil.requestIp();
        }
        xinyuHomeContext = XinyuHomeActivity.this;
    }


    private void regReceiver() {
        if (receiver == null) {
            receiver = new InnerReceiver();
            IntentFilter filter = new IntentFilter();
            // 开启一个新的Activity
            filter.addAction(AppConstants.ACTION_NEW_BROSER);
            //爱贝支付
            filter.addAction("iaaapay");
            LocalBroadcastManager.getInstance(XinyuHomeActivity.this).registerReceiver(receiver, filter);
        }
        if (netReceiver == null) {
            netReceiver = new NetWorkBroadCastReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        registerReceiver(netReceiver, filter);
    }


    /**
     * 开启原生界面想和此界面交互的广播接收器(无关子进程)
     */

    /**
     * 广播接收器
     *
     * @author zzy, wn
     */
    private class InnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context mContext, Intent intent) {


            String action = intent.getAction();
            if (AppConstants.ACTION_NEW_BROSER.equals(action)) { // 开启一个新的Activity
                Log.e("amtf", "xinyuNewBrowser:接收到新的Activity");
                String localUrl = intent.getStringExtra("url");
                Intent in;
                in = new Intent(XinyuHomeActivity.this, NewBrowserActivity.class);
                in.putExtra("newBrowserUrl", localUrl);
                if (XinyuHomeActivity.this.getIntent().hasExtra("projectListUrl")) {
                    String listUrl = XinyuHomeActivity.this.getIntent().getStringExtra("projectListUrl");
                    in.putExtra("projectListUrl", listUrl);
                }
                startActivity(in);
                //overridePendingTransition(R.anim.xinyusoft_activity_right_in, R.anim.xinyusoft_activity_left_out);
            } else if ("textJumpUrl".equals(action)) {
                String url = null;
                url = "file:///" + getFilesDir().getAbsolutePath() + File.separator + "uufpBase.html";
                loadUrl(url);
            }

        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.i(TAG, "onConfigurationChanged:");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (NewBrowserCollector.onlyNames.size() == 0) {
            if (receiver != null) {
                LocalBroadcastManager.getInstance(xinyuHomeContext).unregisterReceiver(receiver);
                receiver = null;


            }
        } else {
            if (NewBrowserCollector.onlyNames.get(NewBrowserCollector.onlyNames.size() - 1).contains("NewBrowserActivity")) {
                LocalBroadcastManager.getInstance(xinyuHomeContext).unregisterReceiver(receiver);
                receiver = null;
            }
        }
        unregisterReceiver(netReceiver);
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        synchronized (TAG) {
            XLogUpload.getInstance(this).uploadXLog();

        }
        regReceiver();// 注册广播

        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(101010);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(101010);

    }


}
