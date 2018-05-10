//package com.xinyusoft.xshelllib.plugin;
//
//import android.content.Intent;
//
//import com.daniulive.smartplayer.SmartPlayer;
//import com.daniulive.smartplayer.SmartPlayerLand;
//
//import org.apache.cordova.CallbackContext;
//import org.apache.cordova.CordovaArgs;
//import org.apache.cordova.CordovaPlugin;
//import org.json.JSONException;
//
//
//public class SmartPlayerPlugin extends CordovaPlugin {
//
//
//    @Override
//    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
//
//        if ("startSmartPlayer".equals(action)) {
//            Intent in = new Intent(cordova.getActivity(),SmartPlayerLand.class);
//            String id = args.getString(0);
//            in.putExtra("id",id);
//            cordova.getActivity().startActivity(in);
//            return true;
//        }else if ("startSmartPlayerVertical".equals(action)){
//            Intent in = new Intent(cordova.getActivity(),SmartPlayer.class);
//            String id = args.getString(0);
//            in.putExtra("id",id);
//            cordova.getActivity().startActivity(in);
//            return true;
//        }
//        return false;
//    }
//
//}
