package com.xshell.xshelllib.plugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.iapppay.interfaces.callback.IPayResultCallback;
import com.iapppay.sdk.main.IAppPay;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.xshell.xshelllib.tools.weixin.WeixinUtil;
import com.xshell.xshelllib.ui.NewBrowserActivity;
import com.xshell.xshelllib.utils.PayConfig;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 爱贝支付插件
 *
 * @author huang
 */
public class IappayPlugin extends CordovaPlugin {

    /*********
     * 微信登录 start
     ******************/
    // 自己微信应用的 appId
    private String WX_APP_ID;
    // 自己微信应用的 appSecret
    private String WX_SECRET;
    /**
     * 微信工具类
     */
    private WeixinUtil weixinInstance;
    private IWXAPI api;
    private NewBrowserActivity mcordova;
    private CallbackContext mcallbackContext;
    private MyReceiver mReceiver;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        //动态注册广播
        mReceiver = new MyReceiver();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction("iaaapayback");
        cordova.getActivity().registerReceiver(mReceiver, mFilter);
        Log.e("huanghu","SDK初始化");
    }

    @Override
    public boolean execute( String action,  JSONArray args, final CallbackContext callbackContext) throws JSONException {
        Log.e("huanghu","爱贝支付插件");
        if("iapppay".equals(action)) {
            String transid = args.getString(0);
            this.mcallbackContext = callbackContext;
            //  通过Intent类的构造方法指定广播的ID
            Intent intent = new Intent("iaaapay");
            //  将要广播的数据添加到Intent对象中
            intent.putExtra("transid", transid);
           // intent.putExtra("mcallbackContext", (Parcelable) mcallbackContext);
            //  发送广播
            cordova.getActivity().sendBroadcast(intent);

            Log.e("huanghu","===="+"transid="+transid+"&appid="+PayConfig.appid);
//            IAppPay.startPay(cordova.getActivity(), "transid="+transid+"&appid="+PayConfig.appid, iPayResultCallback);
            return true;
        }
       return false;
    }


    /**
     * 支付结果回调
     */
    IPayResultCallback iPayResultCallback = new IPayResultCallback() {

        @Override
        public void onPayResult(int resultCode, String signValue, String resultInfo) {

            switch (resultCode) {
                case IAppPay.PAY_SUCCESS:
                    try {
                        JSONObject jsonObject = new JSONObject(resultInfo);
                        mcallbackContext.success(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case IAppPay.PAY_ERROR:
                    try {
                        JSONObject jsonObject = new JSONObject(resultInfo);
                        mcallbackContext.error(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
            Log.e("MainDemoActivity", "requestCode:" + resultCode + ",signvalue:" + signValue + ",resultInfo:" + resultInfo);
        }
    };


    public class MyReceiver extends BroadcastReceiver {
        //  当sendbroadcast发送广播时，系统会调用onReceive方法来接收广播
        @Override
        public void onReceive(Context context, Intent intent) {
         //   Toast.makeText(context, "成功接收广播======：" , Toast.LENGTH_LONG).show();
            //  判断是否为sendbroadcast发送的广播
            if ("iaaapayback".equals(intent.getAction())) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    JSONObject jsonObject = new JSONObject();
                    String resultInfo = bundle.getString("resultInfo");
                    String resultCode = bundle.getString("resultCode");
              //   Toast.makeText(context, "成功接收广播88888："+resultInfo+"===:"+resultCode, Toast.LENGTH_SHORT).show();
                    if("0".equals(resultCode)) {
                        try {
                            jsonObject.put("RetCode",resultCode);
                            jsonObject.put("ErrorMsg",resultInfo);
                            mcallbackContext.success(jsonObject);
                            Log.e("huang","jsonObject:"+jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        try {
                            jsonObject.put("RetCode",resultCode);
                            jsonObject.put("ErrorMsg",resultInfo);
                            mcallbackContext.error(jsonObject);
                            Log.e("huang","jsonObject:"+jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                 //   Toast.makeText(context, "成功接收广播pppppp："+resultInfo+"===:"+resultCode, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        cordova.getActivity().unregisterReceiver(mReceiver);
    }

}
