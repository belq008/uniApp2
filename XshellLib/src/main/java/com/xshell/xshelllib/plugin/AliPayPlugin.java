package com.xshell.xshelllib.plugin;

import android.annotation.SuppressLint;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.alipay.sdk.app.PayTask;
import com.xshell.xshelllib.aliPay.PayResult;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AliPayPlugin extends CordovaPlugin {


    private static final int SDK_PAY_FLAG = 1;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    try {
                        @SuppressWarnings("unchecked")
                        PayResult payResult = new PayResult((String) msg.obj);
                        JSONObject  resultInfo = new JSONObject();
                        Log.e("huanghu","payResult:"+msg.obj.toString());
                        // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                        String result = payResult.getResult();
                        String resultStatus = payResult.getResultStatus();
                        String memo = payResult.getMemo();
                        resultInfo.put("memo",memo);
                        resultInfo.put("result",result);
                        resultInfo.put("resultStatus",resultStatus);
                        Log.e("huanghu","payResult:"+payResult.toString());
                        // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                        if ("9000".equals(resultStatus)) {
                            mCallbackContext.success("支付成功");
                        } else {
                            mCallbackContext.error(resultInfo);
                            Log.e("huanghu","resultInfo:"+resultInfo.toString());
//							Toast.makeText(mContext, resultInfo.toString(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };
    String payInfo;
    private CallbackContext mCallbackContext;
    private Activity mContext;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        mContext = cordova.getActivity();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        // TODO Auto-generated method stub
        Log.e("huang","-------------");
        try {
            if ("alipay".equals(action)) {
                String data = args.getString(0);
                payInfo = data;
                mCallbackContext = callbackContext;
                Log.e("huanghu","payInfo55555："+payInfo);
                Runnable payRunnable = new Runnable() {
                    @Override
                    public void run() {
                        // 构造PayTask 对象
                        PayTask alipay = new PayTask(mContext);
                        // 调用支付接口，获取支付结果
                        String result = alipay.pay(payInfo, true);
                        Message msg = new Message();
                        msg.what = SDK_PAY_FLAG;
                        msg.obj = result;
                        mHandler.sendMessage(msg);
                    }
                };

                // 必须异步调用
                Thread payThread = new Thread(payRunnable);
                payThread.start();
                return true;
            }

        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return false;
    }
}
