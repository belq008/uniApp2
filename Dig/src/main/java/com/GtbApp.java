package com;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.xshell.xshelllib.utils.*;
import com.xshell.xshelllib.utils.AppConsts;


import org.greenrobot.eventbus.EventBus;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * Created by Administrator on 2017/11/27.
 */

public class GtbApp extends com.xshell.xshelllib.application.AppContext {
    @Override
    public void onCreate() {
        super.onCreate();
        activityLifeCycle();
        initPush();
    }


    private void initPush() {
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "5b3bd9865ccf52498b3979e91593afe0");
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                Log.e("amtf", "DigTest deviceToken:" + deviceToken);
                PreferenceUtil.getInstance().saveDeviceToken(deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
            }
        });
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                String pushStr = msg.custom;
                Map<String, String> extra = msg.extra;
                if (AppConsts.isAppFrount == 0) {
                    Intent intent1 = new Intent(GtbApp.this, TestActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    JSONObject jsonObj = map2Json(extra);
                    intent1.putExtra("UMengPush", pushStr);
                    intent1.putExtra("extra", jsonObj.toJSONString());
                    startActivity(intent1);
                } else {
                    if (extra != null && extra.size() > 0) {
                        JSONObject jsonObj = map2Json(extra);
                        Intent intent = new Intent();
                        intent.setAction("UMengPush");
                        intent.putExtra("UMengPush", pushStr);
                        intent.putExtra("extra", jsonObj.toJSONString());
                        sendBroadcast(intent);
                    }
                }


                // Log.e("amtf", "推送的消息" + msg.custom);
            }

        };
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
        PushAgent.getInstance(this).onAppStart();

    }


    private void activityLifeCycle() {

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (AppConsts.isAppFrount == 0) {
                    AppConsts.AppStatus = 2;
                    XshellEvent event = new XshellEvent(com.xshell.xshelllib.utils.AppConsts.APP_STATUS);
                    event.msg = "2";
                    EventBus.getDefault().post(event);

                }
                AppConsts.isAppFrount++;

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                AppConsts.isAppFrount--;
                if (AppConsts.isAppFrount == 0) {
                    AppConsts.AppStatus = 1;
                    XshellEvent event = new XshellEvent(com.xshell.xshelllib.utils.AppConsts.APP_STATUS);
                    event.msg = "1";
                    EventBus.getDefault().post(event);
                }

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });


    }

    public static JSONObject map2Json(Map<String, String> map) {
        JSONObject json = new JSONObject();
        Set<String> set = map.keySet();
        for (Iterator<String> it = set.iterator(); it.hasNext(); ) {
            String key = it.next();
            json.put(key, map.get(key));
        }
        return json;
    }


}


