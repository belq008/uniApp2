package com.xshell.xshelllib.plugin;

import android.util.Log;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;


import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by DELL on 2016/12/9.
 */

public class WeiXinPayEntryPlugin extends CordovaPlugin {

    private IWXAPI api;

//    @Override
//    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
//        super.initialize(cordova, webView);
//        api = WXAPIFactory.createWXAPI(cordova.getActivity(), Constants.APP_ID);
//        // 将该app注册到微信
//        api.registerApp(Constants.APP_ID);
//    }

    @Override
    public boolean execute(String action, JSONArray args,
                           CallbackContext callbackContext) throws JSONException {
        if("weiXinPayEntry".equals(action)){
            Log.e("huanghu","我进来了！");
            //调起微信支付页面
            sendPayReq(args);
        }

        callbackContext.success();
        return true;
    }

    private void sendPayReq(JSONArray result) throws JSONException {

        PayReq req = new PayReq();
        req.appId = result.getString(0);
        // String APP_KEY = result.getString(1);
        req.nonceStr = result.getString(2);
        req.packageValue = result.getString(3);
        req.partnerId = result.getString(4);
        req.prepayId = result.getString(5);
        req.timeStamp = result.getString(6);

//        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
//        signParams.add(new BasicNameValuePair("appid", req.appId));
//        signParams.add(new BasicNameValuePair("appkey", APP_KEY));
//        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
//        signParams.add(new BasicNameValuePair("package", req.packageValue));
//        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
//        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
//        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
        Log.e("huanghu", "req.appId = " + req.appId  + " req.nonceStr = " + req.nonceStr
                + " req.packageValue = " + req.packageValue + " req.partnerId = " + req.partnerId + " req.prepayId = " + req.prepayId + " req.timeStamp = " + req.timeStamp);
        // req.sign = genSign(signParams);
        req.sign = "d5272ed7de90d7f59b705d1db3c1748089b6c749";

        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
        api.sendReq(req);

    }

//    private String genSign(List<NameValuePair> params) {
//        StringBuilder sb = new StringBuilder();
//
//        int i = 0;
//        for (; i < params.size() - 1; i++) {
//            sb.append(params.get(i).getName());
//            sb.append('=');
//            sb.append(params.get(i).getValue());
//            sb.append('&');
//        }
//        sb.append(params.get(i).getName());
//        sb.append('=');
//        sb.append(params.get(i).getValue());
//
//        String sha1 = Util.sha1(sb.toString());
//        Log.e("huanghu", "genSign, sha1 = " + sha1);
//        return sha1;
//    }




}
