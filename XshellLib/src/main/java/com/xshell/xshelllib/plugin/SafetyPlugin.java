package com.xshell.xshelllib.plugin;

import android.content.Context;

import com.xshell.xshelllib.application.AppContext;
import com.xshell.xshelllib.tools.sm4.SM4Utils;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by huang on 2017/11/16.
 */

public class SafetyPlugin extends CordovaPlugin {

    private static Context context = AppContext.CONTEXT;

    @Override
    public boolean execute(String action, CordovaArgs args,
                           CallbackContext callbackContext) throws JSONException {
        // TODO Auto-generated method stub
        try {
            if ("sm4Encrypt".equals(action)) { // 获取设备id
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                }).start();
                String model = args.getString(0);
                String plainText = args.getString(1);
                String secretkey = args.getString(2);
                String iv = args.getString(3);
                System.out.println(plainText);

                String newStr = new String(plainText.getBytes(), "UTF-8");
                System.out.println(newStr);

                SM4Utils sm4 = new SM4Utils();
                sm4.secretKey = secretkey;
                sm4.hexString = false;
                String cipherText = "";
                if (model.equals("ECB")) {
                    cipherText = sm4.encryptData_ECB(newStr);
                    System.out.println("miwen: " + cipherText);
                    //明文
                    plainText = sm4.decryptData_ECB(cipherText);
                    System.out.println("ming wen: " + plainText);
                } else {
                    System.out.println("CBC模式");
                    sm4.iv = iv;
                    cipherText = sm4.encryptData_CBC(plainText);
                    System.out.println("密文: " + cipherText);
                    plainText = sm4.decryptData_CBC(cipherText);
                    System.out.println("明文: " + plainText);
                }
                JSONObject resultob = new JSONObject();
                resultob.put("cipherText", cipherText);
                callbackContext.success(resultob);
            }
            return true;
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
    }
}
