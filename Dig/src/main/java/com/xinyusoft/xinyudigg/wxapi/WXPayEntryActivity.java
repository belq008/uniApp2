package com.xinyusoft.xinyudigg.wxapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.xshell.xshelllib.tools.weixin.WeixinUtil;
import com.xshell.xshelllib.utils.ParseConfig;

import java.util.Map;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "WXPayEntryActivity";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.pay_result);
        try {
            if (WeixinUtil.getInstance().getWeixinApi() == null) {
                Map<String, String> configInfo = ParseConfig.getInstance(this).getConfigInfo();
                WeixinUtil.getInstance().initWeixinApi(this, configInfo.get("wxapp-id"));
            }
            WeixinUtil.getInstance().getWeixinApi().handleIntent(getIntent(), this);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "分享异常！", Toast.LENGTH_SHORT).show();
        }
//        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
//        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        WeixinUtil.getInstance().getWeixinApi().handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.i(TAG, "onPayFinish, errCode = " + resp.errCode);

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("支付成功" );
//			builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
            builder.setMessage("微信支付结果：" + String.valueOf(resp.errCode));
            builder.show();

        }
    }
}