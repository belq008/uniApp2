package com.xshell.xshelllib.plugin;

import android.app.Activity;
import android.content.pm.ActivityInfo;

import android.widget.Toast;


import com.xshell.xshelllib.R;
import com.xshell.xshelllib.logutil.LogUtils;
import com.xshell.xshelllib.utils.AppConsts;
import com.xshell.xshelllib.utils.CommonUtil;
import com.xshell.xshelllib.utils.PhoneInfo;
import com.xshell.xshelllib.utils.PreferenceUtil;
import com.xshell.xshelllib.utils.SharedPreferencesUtils;
import com.xshell.xshelllib.utils.VersionUtil;
import com.xshell.xshelllib.utils.XshellEvent;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DELL on 2017/11/15.
 */

public class DevicePlugin extends CordovaPlugin {


    private Activity activity;


    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        activity = cordova.getActivity();
    }


    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String token = PreferenceUtil.getInstance().getDeviceToken();
        if ("getInfo".equals(action)) { // 获取设备id
            LogUtils.e("huanghu", "获取设备id");
            PhoneInfo phoneInfo = PhoneInfo.getInstance(activity);
            JSONObject json = new JSONObject();
            json.put("version", VersionUtil.getVersionName(activity));
            json.put("deviceid", phoneInfo.getDeviceID());
            json.put("devicetoken", token);
            json.put("model", phoneInfo.getModel());
            json.put("release", "Android" + phoneInfo.getSystemVersion());
            json.put("pixels", phoneInfo.getPixels());
            json.put("result", 1);
            callbackContext.success(json);
            return true;
        } else if ("touchid".equals(action)) {//指纹识别
            LogUtils.e("huanghu", "指纹识别");
            Toast.makeText(cordova.getActivity(), "指纹识别", Toast.LENGTH_SHORT).show();
            return true;
        } else if ("changeOrientation".equals(action)) {//横竖屏切换
            LogUtils.e("huanghu", "横竖屏切换:" + args.getString(0));
            if ("1".equals(args.getString(0))) {
                cordova.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            } else {
                cordova.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            }
            return true;
        } else if ("statusBarStyle".equals(action)) {
            XshellEvent event = new XshellEvent(AppConsts.APP_Bar_Style);
            event.msg = args.getString(0);
            EventBus.getDefault().post(event);
        }
        return false;
    }

}
