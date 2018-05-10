package com.xshell.xshelllib.plugin;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import com.xshell.xshelllib.application.AppContext;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Administrator on 2016/5/8.
 */
public class setClipBoardPlugin extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args,
                           CallbackContext callbackContext) throws JSONException {
        // TODO Auto-generated method stub
            try{
                String result = args.getString(0);
                if ("clipboard".equals(action)) { //粘贴板
                    JSONObject jos = args.getJSONObject(0);
                    ClipboardManager clip = (ClipboardManager) AppContext.CONTEXT.getSystemService(Context.CLIPBOARD_SERVICE);
                    clip.setPrimaryClip(ClipData.newPlainText(null, jos.getString("paste")));
                    callbackContext.success();
                }
                return true;
            } catch (Exception e) {
                // TODO: handle exception
                return false;
            }
        }
}
