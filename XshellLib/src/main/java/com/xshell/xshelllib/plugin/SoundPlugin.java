package com.xshell.xshelllib.plugin;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.xshell.xshelllib.application.AppConstants;
import com.xshell.xshelllib.application.AppContext;
import com.xshell.xshelllib.utils.PlaySoundsUtil;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URLDecoder;
import java.util.HashMap;

/**
 * Created by wn on 2016/5/8.
 */
public class SoundPlugin extends CordovaPlugin {

    private static Context context = AppContext.CONTEXT;
    private static HashMap<String, Integer> sounds = new HashMap<String, Integer>();// 音乐播放
    private static File ROOT_FILE = new File(context.getFilesDir().getAbsolutePath());
    private static final String TAG = "SoundPlugin";
    @Override
    public boolean execute(String action, CordovaArgs args,
                           CallbackContext callbackContext) throws JSONException {
        try {
            if ("sound".equals(action)) { // 播放声音
                JSONObject json = new JSONObject();
                String name = json.getString("soundfilename");
                name = URLDecoder.decode(name, "UTF-8");
                if (sounds.containsKey(name)) {
                    PlaySoundsUtil.getInstance().play(sounds.get(name), 0);
                } else {
                    try {
                        int id = -1;
                        synchronized (TAG) {
                            File file = new File(ROOT_FILE, name);
                            if (file.exists() && file.isFile())
                                id = PlaySoundsUtil.getInstance().loadSound(file.getAbsolutePath());
                        }
                        if (id != -1) {
                            sounds.put(name, id);
                            Intent intent = new Intent(AppConstants.ACTION_WEBVIEW_PLAY_SOUND);
                            intent.putExtra("soundId", id);
                            LocalBroadcastManager.getInstance(cordova.getActivity()).sendBroadcast(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                json.put("result", 1);
                callbackContext.success(json);
            }
            return true;
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
    }
}
