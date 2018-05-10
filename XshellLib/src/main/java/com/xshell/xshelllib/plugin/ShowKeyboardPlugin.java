//package com.xshell.xshelllib.plugin;
//
//import android.app.Activity;
//import android.inputmethodservice.KeyboardView;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.RelativeLayout;
//
//import com.xinyusofi.xshell.softkeyboarad.BaseKeyboard;
//import com.xinyusofi.xshell.softkeyboarad.KeyboardNumeric;
//import com.xinyusofi.xshell.softkeyboarad.KeyboardPrice;
//import com.xinyusofi.xshell.softkeyboarad.KeyboardStock;
//import com.xinyusofi.xshell.softkeyboarad.OnKeyboardClick;
//import com.xshell.xshelllib.R;
//
//import org.apache.cordova.CallbackContext;
//import org.apache.cordova.CordovaArgs;
//import org.apache.cordova.CordovaInterface;
//import org.apache.cordova.CordovaPlugin;
//import org.apache.cordova.CordovaWebView;
//import org.apache.cordova.PluginResult;
//import org.json.JSONException;
//
///**
// * Created by zzy on 2016/10/26.
// * 显示自定义键盘
// */
//public class ShowKeyboardPlugin extends CordovaPlugin {
//
//    private BaseKeyboard baseKeyboard;
//    private Activity activity;
//    private boolean flag = false;
//
//    @Override
//    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
//        super.initialize(cordova, webView);
//        this.activity = cordova.getActivity();
//    }
//
//    @Override
//    public boolean onDispatchTouchEvent(MotionEvent ev) {
//        if (flag) {
//            if (baseKeyboard != null) {
//                int keyboardY = (int) baseKeyboard.getKeyboardView().getY();
//                int eventY = (int) ev.getY();
//                if (eventY < keyboardY) {
//                    flag = false;
//                    baseKeyboard.hide();
//                }
//            }
//        }
//        return super.onDispatchTouchEvent(ev);
//    }
//
//    @Override
//    public boolean execute(String action, final CordovaArgs args, final CallbackContext callbackContext) throws JSONException {
//        if ("showKeyboard".equals(action)) {
//            flag = true;
//            cordova.getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    int mainId = activity.getResources().getIdentifier("myMain", "id", activity.getPackageName());
//                    RelativeLayout rl_main = (RelativeLayout) activity.findViewById(mainId);
//                    View view = LayoutInflater.from(activity).inflate(R.layout.xinyusoft_keyboard_view, null, false);
//                    RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//                    param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                    rl_main.addView(view,param);
//                    KeyboardView keyboard = (KeyboardView) rl_main.findViewById(R.id.keyboard_view);
//                    if (baseKeyboard != null) {
//                        baseKeyboard = null;
//                    }
//                    try {
//                        switch (args.getInt(0)) {
//                            case 1:
//                                baseKeyboard = new KeyboardNumeric(activity, activity, keyboard);
//                                break;
//                            case 2:
//                                baseKeyboard = new KeyboardStock(activity, activity, keyboard);
//                                baseKeyboard.setDefalutKeyboard(args.getInt(1));
//                                break;
//                            case 3:
//                                baseKeyboard = new KeyboardPrice(activity, activity, keyboard);
//                                break;
//                            default:
//                                break;
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    baseKeyboard.setOnkeyboardClick(new OnKeyboardClick() {
//
//                        @Override
//                        public void onClick(String value) {
//                            if (BaseKeyboard.KEYCODE_CANCEL.equals(value)) {// 点击确定
//                                baseKeyboard.hide();
//                                baseKeyboard = null;
//                            }
//
//                            PluginResult result = new PluginResult(PluginResult.Status.OK, value);
//                            result.setKeepCallback(true);
//                            callbackContext.sendPluginResult(result);
//                        }
//                    });
//                    baseKeyboard.show();
//                }
//            });
//            return true;
//        }
//        return false;
//    }
//}
