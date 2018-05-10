package com.xshell.xshelllib.plugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.xshell.xshelllib.application.AppConfig;
import com.xshell.xshelllib.tools.weixin.WeixinUtil;
import com.xshell.xshelllib.utils.ParseConfig;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * 微信相关的插件
 *
 * @author zzy
 */
public class WeiXinPlugin extends CordovaPlugin {

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
    private CordovaInterface mcordova;
    private CallbackContext mcallbackContext;
    private boolean iSLoginWeiXin = true;
    private Handler handler = new Handler();

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        api = WXAPIFactory.createWXAPI(cordova.getActivity(), "wx25eb1dc94a1234cf");
        // 将该app注册到微信
        api.registerApp("wx2b9f2f9b22d859a3");
        mcordova = cordova;

        //动态注册广播
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction("ErrCode");
        cordova.getActivity().registerReceiver(dynamicReceiver, mFilter);
    }


    /*********
     * 微信登录 end
     ******************/

    @Override
    public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String callBackName = args.getString(0);
        //  Log.e("huanghu","进入微信");

        if ("login".equals(action)) {
            if (iSLoginWeiXin) {
                iSLoginWeiXin = false;
//            Log.e("huanghu","进入微信=========");
                cordova.getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(cordova.getActivity(), "正在登录，请稍等！", Toast.LENGTH_SHORT).show();
                    }
                });
                Map<String, String> configInfo = ParseConfig.getInstance(cordova.getActivity()).getConfigInfo();
                WX_APP_ID = configInfo.get("wxapp-id");
                WX_SECRET = configInfo.get("wxapp-secret");
                Log.i("zzy", "WX_APP_ID:" + WX_APP_ID + "----WX_SECRET:" + WX_SECRET);
                weixinInstance = WeixinUtil.getInstance();
                if (WX_APP_ID != null && !"".equals(WX_APP_ID)) {
                    // 初始化微信登录
                    if (weixinInstance.getWeixinApi() == null) {
                        weixinInstance.initWeixinApi(cordova.getActivity().getApplicationContext(), WX_APP_ID);
                    }
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        iSLoginWeiXin = true;
                    }
                }, 500);
                boolean isSuccess = WeixinUtil.getInstance().weixinSendReq(callBackName);
                if (isSuccess) {
                    callbackContext.success();
                    return true;
                } else {
                    cordova.getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(cordova.getActivity(), "您可能没有安装微信，登录失败！", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return false;
                }

            }
        } else if ("wxpay".equals(action)) {
            this.mcallbackContext = callbackContext;
            //调起微信支付页面
            sendPayReq(args, callbackContext);

            return true;
        }
        return false;
    }


    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        if (AppConfig.DEBUG) {
            Log.i("zzy", "xinyuplugin onresume:");
//            Log.i("zzy", "isLogin:" + weixinInstance.isWXLogin());
//            Log.i("zzy", "getWXCode:" + weixinInstance.getWXCode());
        }
        if (weixinInstance != null && weixinInstance.isWXLogin() && weixinInstance.getWXCode() != null) {
            // 设置不再再次登录微信
            weixinInstance.setIsWXLogin(false);
            weixinInstance.loadWXUserInfo(WX_SECRET, weixinInstance.getWXCode(), new WeixinUtil.GetUserInfoListener() {

                @Override
                public void onResp(final String userInfo) {
                    weixinInstance.saveWXCode(null);
                    cordova.getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            webView.loadUrl("javascript:" + WeixinUtil.getInstance().getCallBackJavascriptName() + "('" + userInfo + "')");
                        }
                    });
                    if (AppConfig.DEBUG)
                        Log.e("zzy", "myhome:" + userInfo);
                }
            });
        }
    }

    private void sendPayReq(JSONArray result, CallbackContext callbackContext) throws JSONException {
        PayReq req = new PayReq();
        String object = result.getString(0);
        if (!TextUtils.isEmpty(object)) {
            try {
                JSONObject jsonObject = new JSONObject(object);
                String appid = jsonObject.getString("appid");
                String mch_id = jsonObject.getString("mch_id");
                String prepay_id = jsonObject.getString("prepay_id");
                String nonce_str = jsonObject.getString("nonce_str");
                String package1 = jsonObject.getString("package");
                String sign = jsonObject.getString("sign");
                String timestamp = jsonObject.getString("timestamp");

//                 Log.e("huanghu", appid);
//                 Log.e("huanghu", mch_id);
//                 Log.e("huanghu", prepay_id);
//                 Log.e("huanghu", nonce_str);
//                 Log.e("huanghu", package1);
//                 Log.e("huanghu", sign);
//                 Log.e("huanghu", timestamp);

                req.appId = appid;
                req.nonceStr = nonce_str;
                // req.packageValue = "Sign=WXPay";//
                req.packageValue = package1;//
                req.partnerId = mch_id;//商户id
                req.prepayId = prepay_id;
                //req.timeStamp = String.valueOf( genTimeStamp());
                req.timeStamp = timestamp;
//        req.sign = signNum(req.appId,req.partnerId,req.prepayId,req.packageValue,req.nonceStr,req.timeStamp,APP_KEY);
                req.sign = sign;
                // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                api.sendReq(req);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public BroadcastReceiver dynamicReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("ErrCode")) {    //动作检测
                String errcode = intent.getStringExtra("err");
                JSONObject jsonObject = new JSONObject();

                if (errcode != null) {
                    if ("0".equals(errcode)) {
                        mcallbackContext.success("成功");
                    } else if ("-1".equals(errcode)) {
                        try {
                            jsonObject.put("errCode", errcode);
                            jsonObject.put("errStr", "支付失败");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mcallbackContext.error(jsonObject);
                    } else if ("-2".equals(errcode)) {

                        try {
                            jsonObject.put("errCode", errcode);
                            jsonObject.put("errStr", "用户取消支付");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mcallbackContext.error(jsonObject);
                        Log.e("huanghu", jsonObject + "-----");
//                        AlertDialog.Builder builder = new AlertDialog.Builder(mcordova.getActivity());
//                        builder.setTitle("提示");
//                        builder.setMessage(jsonObject.toString());
//                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//                        builder.show();
                    }
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        cordova.getActivity().unregisterReceiver(dynamicReceiver);
    }

}
