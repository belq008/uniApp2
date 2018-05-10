package com.xinyusoft.xinyudigg.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.xshell.xshelllib.tools.weixin.WeixinUtil;
import com.xshell.xshelllib.utils.ParseConfig;

import java.util.Map;


public class WXEntryActivity extends Activity implements IWXAPIEventHandler {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    @Override
    public void onReq(BaseReq req) {
        finish();
    }

    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (WeixinUtil.getInstance().isWXLogin()) {
                    SendAuth.Resp sendResp = (SendAuth.Resp) resp;
                    WeixinUtil.getInstance().saveWXCode(sendResp.code);
                } else {
                    Toast.makeText(this, "分享成功!!", Toast.LENGTH_LONG).show();
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                WeixinUtil.getInstance().setIsWXLogin(false);
                Toast.makeText(this, "取消!", Toast.LENGTH_LONG).show();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                WeixinUtil.getInstance().setIsWXLogin(false);
                Toast.makeText(this, "被拒绝！", Toast.LENGTH_LONG).show();
                break;
            default:
                WeixinUtil.getInstance().setIsWXLogin(true);
//			Toast.makeText(this, "失败!!", Toast.LENGTH_LONG).show();
                break;
        }
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        WeixinUtil.getInstance().getWeixinApi().handleIntent(intent, this);
        finish();
    }

}
