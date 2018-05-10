package com.xshell.xshelllib.plugin;

import android.content.Intent;
import android.os.Bundle;

import com.xshell.xshelllib.logutil.LogUtils;
import com.xshell.xshelllib.tools.PictureBrowser.ImagePagerActivity;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;


public class PictureBrowserPlugin extends CordovaPlugin {


    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {

        if ("pictureBrowser".equals(action)) {
            LogUtils.e("zzy", "====================:" + args.getJSONArray(0));
//            JSONObject urls = args.getJSONObject(0);
            JSONArray jsonArray = args.getJSONArray(0);
            String[] strs = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                strs[i] = jsonArray.getString(i);
            }
            String start = args.getString(1);
            Bundle bundle = new Bundle();
            bundle.putStringArray(ImagePagerActivity.IMAGES,strs);
            Intent in = new Intent(cordova.getActivity(), ImagePagerActivity.class);
            in.putExtras(bundle);
            in.putExtra(ImagePagerActivity.START_URL, start);
            cordova.getActivity().startActivity(in);
            return true;
        }
        return false;
    }

}
