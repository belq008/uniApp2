//package com.xinyusoft.xshelllib.plugin;
//
//import android.content.Context;
//
//import com.xinyusoft.xshelllib.application.AppContext;
//import com.xinyusoft.xshelllib.tools.sm2.SM2Utils;
//import com.xinyusoft.xshelllib.utils.Util;
//
//import org.apache.cordova.CallbackContext;
//import org.apache.cordova.CordovaArgs;
//import org.apache.cordova.CordovaPlugin;
//import org.json.JSONException;
//import org.json.JSONObject;
//
///**
// * Created by wn on 2016/5/22.
// */
//public class sm2Plugin extends CordovaPlugin{
//
//    private static Context context = AppContext.CONTEXT;
//    @Override
//    public boolean execute(String action, CordovaArgs args,
//                           CallbackContext callbackContext) throws JSONException {
//        // TODO Auto-generated method stub
//        String result = args.getString(0);
//        try {
//            if ("sm2".equals(action)) { // 获取设备id
//
//                JSONObject ob = new JSONObject(result);
//                String plainText = ob.getString("plainText");
//                String secretkey = ob.getString("secretkey");
//                System.out.println(plainText);
//
//                byte[] sourceData = plainText.getBytes();
//                System.out.println("加密: ");
//                String cipherText = SM2Utils.encrypt(Util.hexToByte(secretkey), sourceData);
//                System.out.println(cipherText);
//
//
//               /* System.out.println("解密: ");
//                plainText = new String(SM2Utils.decrypt(Util.hexToByte(prik), Util.hexToByte(cipherText)));
//                System.out.println(plainText);*/
//
//                JSONObject resultob = new JSONObject();
//                resultob.put("cipherText",cipherText);
//                callbackContext.success(resultob.toString());
//            }
//                return true;
//        }catch(Exception e){
//            // TODO: handle exception
//            return false;
//        }
//    }
//}
