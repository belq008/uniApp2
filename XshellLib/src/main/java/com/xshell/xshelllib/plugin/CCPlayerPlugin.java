//package com.xinyusoft.xshelllib.plugin;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//
//import com.bokecc.live.demo.LiveReplayActivity;
//import com.bokecc.live.demo.LiveReplayLandActivity;
//import com.bokecc.live.demo.LiveRoomActivity;
//import com.bokecc.live.demo.LiveRoomLandscapeActivity;
//import com.bokecc.sdk.mobile.live.DWLive;
//import com.bokecc.sdk.mobile.live.DWLiveLoginListener;
//import com.bokecc.sdk.mobile.live.Exception.DWLiveException;
//import com.bokecc.sdk.mobile.live.pojo.RoomInfo;
//import com.bokecc.sdk.mobile.live.pojo.TemplateInfo;
//import com.bokecc.sdk.mobile.live.pojo.Viewer;
//import com.bokecc.sdk.mobile.live.replay.DWLiveReplay;
//import com.bokecc.sdk.mobile.live.replay.DWLiveReplayLoginListener;
//
//import org.apache.cordova.CallbackContext;
//import org.apache.cordova.CordovaArgs;
//import org.apache.cordova.CordovaPlugin;
//import org.json.JSONException;
//
//
//public class CCPlayerPlugin extends CordovaPlugin {
//
//    private String replayRoomId = "";
//    private String liveRoomId = "";
//
//    @Override
//    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
//        if ("CCVideoPlayBack".equals(action)) {
//            String password = null;
//            String userId = "C5294E13BF60D950";
//            String viewerName = "";
//            String roomId = args.getString(0);
//            replayRoomId = roomId;
//            String currentLiveId = args.getString(1);
//            DWLiveReplay dwLiveReplay = DWLiveReplay.getInstance();
//            if (password == null || "".equals(password)) {
//                dwLiveReplay.setLoginParams(dwLiveReplayLoginListener, userId, roomId, currentLiveId, viewerName);
//            } else {
//                dwLiveReplay.setLoginParams(dwLiveReplayLoginListener, userId, roomId, currentLiveId, viewerName, password);
//            }
//            dwLiveReplay.startLogin();
//            return true;
//        } else if ("CCVideoPlayBackVertical".equals(action)) {
//            String password = null;
//            String userId = "C5294E13BF60D950";
//            String viewerName = "";
//            String roomId = args.getString(0);
//            replayRoomId = roomId;
//            String currentLiveId = args.getString(1);
//            DWLiveReplay dwLiveReplay = DWLiveReplay.getInstance();
//            if (password == null || "".equals(password)) {
//                dwLiveReplay.setLoginParams(dwLiveReplayVerticalLoginListener, userId, roomId, currentLiveId, viewerName);
//            } else {
//                dwLiveReplay.setLoginParams(dwLiveReplayVerticalLoginListener, userId, roomId, currentLiveId, viewerName, password);
//            }
//            dwLiveReplay.startLogin();
//            return true;
//        } else if ("CCVideoLive".equals(action)) {
//            String password = null;
//            String userId = "C5294E13BF60D950";
//            String viewerName = "";
//            String roomId = args.getString(0);
//            liveRoomId = roomId;
//            DWLive dwLive = DWLive.getInstance();
//            Log.i("zzy", "roomId:" + args.getString(0));
//
//            if (password == null || "".equals(password)) {
//                dwLive.setDWLiveLoginParams(dwLiveLoginListener, userId, roomId, viewerName);
//            } else {
//                dwLive.setDWLiveLoginParams(dwLiveLoginListener, userId, roomId, viewerName, password);
//            }
//            dwLive.startLogin();
//            return true;
//        } else if ("CCVideoLiveVertical".equals(action)) {
//            String password = null;
//            String userId = "C5294E13BF60D950";
//            String viewerName = "";
//            String roomId = args.getString(0);
//            liveRoomId = roomId;
//            DWLive dwLive = DWLive.getInstance();
//            Log.i("zzy", "roomId:" + args.getString(0));
//
//            if (password == null || "".equals(password)) {
//                dwLive.setDWLiveLoginParams(dwLiveVerticalLoginListener, userId, roomId, viewerName);
//            } else {
//                dwLive.setDWLiveLoginParams(dwLiveVerticalLoginListener, userId, roomId, viewerName, password);
//            }
//            dwLive.startLogin();
//            return true;
//        }
//        return false;
//    }
//
//    private DWLiveReplayLoginListener dwLiveReplayLoginListener = new DWLiveReplayLoginListener() {
//
//        @Override
//        public void onLogin(TemplateInfo templateInfo) {
//            Intent intent = new Intent(cordova.getActivity(), LiveReplayLandActivity.class);
//
//            Bundle bundle = new Bundle();
//            bundle.putString("chat", templateInfo.getChatView());
//            bundle.putString("pdf", templateInfo.getPdfView());
//            bundle.putString("qa", templateInfo.getQaView());
//            bundle.putString("roomid",replayRoomId);
//            intent.putExtras(bundle);
////
//           cordova.getActivity().startActivity(intent);
//            Log.i("zzy", "------LiveListActivity---------:");
//        }
//
//        @Override
//        public void onException(DWLiveException exception) {
////            Message msg = new Message();
////            msg.what = -2;
////            msg.obj = exception.getMessage();
////            mHandler.sendMessage(msg);
//        }
//    };
//
//    private DWLiveReplayLoginListener dwLiveReplayVerticalLoginListener = new DWLiveReplayLoginListener() {
//
//        @Override
//        public void onLogin(TemplateInfo templateInfo) {
//            Intent intent = new Intent(cordova.getActivity(), LiveReplayActivity.class);
//
//            Bundle bundle = new Bundle();
//            bundle.putString("chat", templateInfo.getChatView());
//            bundle.putString("pdf", templateInfo.getPdfView());
//            bundle.putString("qa", templateInfo.getQaView());
//            bundle.putString("roomid", replayRoomId);
//            intent.putExtras(bundle);
//
//            cordova.getActivity().startActivity(intent);
//        }
//
//        @Override
//        public void onException(DWLiveException exception) {
////            Message msg = new Message();
////            msg.what = -2;
////            msg.obj = exception.getMessage();
////            mHandler.sendMessage(msg);
//        }
//    };
//
//
//    private DWLiveLoginListener dwLiveVerticalLoginListener = new DWLiveLoginListener() {
//
//        @Override
//        public void onLogin(TemplateInfo info, Viewer viewer, RoomInfo roomInfo) {
//            Intent intent = new Intent(cordova.getActivity(), LiveRoomActivity.class);
//
//            Bundle bundle = new Bundle();
//
//            bundle.putString("chat", info.getChatView());
//            bundle.putString("pdf", info.getPdfView());
//            bundle.putString("qa", info.getQaView());
//            bundle.putString("roomid", liveRoomId);
//            intent.putExtras(bundle);
//
//            cordova.getActivity().startActivity(intent);
//        }
//
//        @Override
//        public void onException(DWLiveException exception) {
//        }
//    };
//
//
//    private DWLiveLoginListener dwLiveLoginListener = new DWLiveLoginListener() {
//
//        @Override
//        public void onLogin(TemplateInfo info, Viewer viewer, RoomInfo roomInfo) {
//            Intent intent = new Intent(cordova.getActivity(), LiveRoomLandscapeActivity.class);
//
//            Bundle bundle = new Bundle();
//
//            bundle.putString("chat", info.getChatView());
//            bundle.putString("pdf", info.getPdfView());
//            bundle.putString("qa", info.getQaView());
//            bundle.putString("roomid", liveRoomId);
//            intent.putExtras(bundle);
//
//            cordova.getActivity().startActivity(intent);
//        }
//
//        @Override
//        public void onException(DWLiveException exception) {
//        }
//    };
//
//
//}
