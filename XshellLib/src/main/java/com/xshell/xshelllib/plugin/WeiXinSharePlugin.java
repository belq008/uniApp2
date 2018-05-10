//package com.xshell.xshelllib.plugin;
//
//import com.xshell.xshelllib.tools.weixin.WeixinUtil;
//import com.xshell.xshelllib.utils.ParseConfig;
//
//import org.apache.cordova.CallbackContext;
//import org.apache.cordova.CordovaPlugin;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.UnsupportedEncodingException;
//import java.util.Map;
//
///**
// * 微信分享的插件
// * s
// *
// * @author zzy
// */
//public class WeiXinSharePlugin extends CordovaPlugin {
//
//    /**
//     * 微信工具类
//     */
//    private WeixinUtil weixinInstance;
//
//    @Override
//    public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
//        if ("share".equals(action)) { // 分享功能
//            JSONObject jo = args.getJSONObject(0);
//            try {
//
//                Map<String, String> configInfo = ParseConfig.getInstance(cordova.getActivity()).getConfigInfo();
//                String WX_APP_ID = configInfo.get("wxapp-id");
//                weixinInstance = WeixinUtil.getInstance();
//                if (WX_APP_ID != null && !"".equals(WX_APP_ID)) {
//                    // 初始化微信登录
//                    if (weixinInstance.getWeixinApi() == null) {
//                        weixinInstance.initWeixinApi(cordova.getActivity().getApplicationContext(), WX_APP_ID);
//                    }
//                }
//                weixinInstance.weixinShare(jo, cordova.getActivity());
//                JSONObject json = new JSONObject();
//                json.put("result", 1);
//                callbackContext.success(json);
//                return true;
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
//        return false;
//    }
//
//}
