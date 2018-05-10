package com.xshell.xshelllib.plugin;

import android.app.Activity;

import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;


import com.threelibrary.SpeechUtil;

import com.xshell.xshelllib.application.AppConfig;
import com.xshell.xshelllib.logutil.LogUtils;
import com.xshell.xshelllib.utils.AppConsts;
import com.xshell.xshelllib.utils.CalendarUtil;
import com.xshell.xshelllib.utils.CommonUtil;
import com.xshell.xshelllib.utils.PhoneInfo;
import com.xshell.xshelllib.utils.PreferenceUtil;
import com.xshell.xshelllib.utils.TimeUtil;
import com.xshell.xshelllib.utils.VersionUtil;
import com.xshell.xshelllib.utils.XshellEvent;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by DELL on 2017/11/15.
 */

public class AppNativePlugin extends CordovaPlugin {


    private Activity activity;

    CallbackContext mCallback;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        activity = cordova.getActivity();
//        EventBus.getDefault().register(this);
    }


    SpeechUtil instance;

    private void beginBaiduSpeech() {
        if (instance == null) {
            instance = new SpeechUtil(activity);
        }
        instance.start();

    }

    String mCid = "";

    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if ("getWebInfo".equals(action)) { // 获取h5是否更新
            boolean isH5Update = PreferenceUtil.getInstance().isHtmlUpdate();
            JSONObject json = new JSONObject();
            json.put("version", PreferenceUtil.getInstance().getFileUpdateTime());
            json.put("update", isH5Update);
            callbackContext.success(json);
            return true;
        } else if ("appStatus".equals(action)) {
            mCallback = callbackContext;
//            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, AppConsts.AppStatus);
//            pluginResult.setKeepCallback(true);
//            mCallback.sendPluginResult(pluginResult);
            return true;
        } else if ("getPushInfo".equals(action)) {
            if (!TextUtils.isEmpty(AppConsts.PUSH_MSG)) {
                callbackContext.success(AppConsts.PUSH_MSG);
            } else {
                callbackContext.success("error");
            }
            return true;
        } else if ("speechStart".equals(action)) {
            beginBaiduSpeech();
            return true;
        } else if ("addCalendar".equals(action)) {
            try {
                JSONObject myJsonObject = args.getJSONObject(0);
                String time = myJsonObject.getString("time");
                String endTime = myJsonObject.getString("endtime");
                String title = myJsonObject.getString("title");
                String descp = myJsonObject.getString("descrp");
                long cid = CalendarUtil.getInstance().addCalendarEvent(activity, title, descp, TimeUtil.getUpdataTimeLong(time), TimeUtil.getUpdataTimeLong(endTime));
                mCid = cid + "";
                String resultCode = "";
                if (cid == -1) {
                    resultCode = "-1";
                } else {
                    resultCode = "9000";
                }
                JSONObject obj = new JSONObject();
                obj.put("resultCode", resultCode);
                obj.put("resultStr", cid);
                callbackContext.success(obj);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;

        } else if ("deletCalendar".equals(action)) {
            int state = CalendarUtil.deleteCalendarEvent(activity, mCid);
            String resultCode = "";
            if (state <= 0) {
                resultCode = "-1";
            } else {
                resultCode = "9000";
            }
            JSONObject obj = new JSONObject();
            obj.put("resultCode", resultCode);
            obj.put("resultStr", mCid);
            callbackContext.success(obj);
            return true;

        } else if ("getCalendar".equals(action)) {
            JSONArray array = CalendarUtil.queryCalendarList(activity);
            if (array == null || array.length() <= 0) {
                callbackContext.success("没有数据");
            } else {
                callbackContext.success(array);
            }

            return true;
        }
        return false;
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onXshellEvent(XshellEvent event) {
//        if (event.what == AppConsts.APP_STATUS) {
//            event.msg = "2";
//            mCallback.success(event.msg);
//            if (AppConfig.DEBUG)
//                Log.e("amtf", "event.AppStatus" + event.msg);
//        }
//    }


    @Override
    public void onDestroy() {
        if (instance != null) {
            instance.releaseSpeech();
        }
//        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
