package com.threelibrary;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class SpeechUtil implements EventListener {

    private static final String TAG = "SpeechUtil";

    private EventManager asr;

    private String final_result;

    static Context mCtx;


    public SpeechUtil(Context context) {
        this.mCtx = context;
        asr = EventManagerFactory.create(context, "asr");
        asr.registerListener(this);
    }


    /*
    * EventListener  回调方法
    * name:回调事件
    * params: JSON数据，其格式如下：
    *
    * */
    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        String result = "";

        if (length > 0 && data.length > 0) {
            result += ", 语义解析结果：" + new String(data, offset, length);
        }

        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)) {
            // 引擎准备就绪，可以开始说话
            result += "引擎准备就绪，可以开始说话";

        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_BEGIN)) {
            // 检测到用户的已经开始说话
            result += "检测到用户的已经开始说话";

        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_END)) {
            // 检测到用户的已经停止说话
            result += "检测到用户的已经停止说话";
            if (params != null && !params.isEmpty()) {
                result += "params :" + params + "\n";
            }
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
            // 临时识别结果, 长语音模式需要从此消息中取出结果
            result += "识别临时识别结果";
            if (params != null && !params.isEmpty()) {
                result += "params :" + params + "\n";
            }
//            Log.d(TAG, "Temp Params:"+params);
            parseAsrPartialJsonData(params);
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)) {
            // 识别结束， 最终识别结果或可能的错误
            result += "识别结束";

            asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
            if (params != null && !params.isEmpty()) {
                result += "params :" + params + "\n";
            }
            result = parseAsrFinishJsonData(params);

            XshellEvent event = new XshellEvent(0x1122);
            event.msg = result;
            EventBus.getDefault().post(event);
            Toast.makeText(mCtx, result, Toast.LENGTH_LONG).show();
        }

    }


    String speechResult = "";

    public void start() {
        speechResult = "";
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        String event = null;
        event = SpeechConstant.ASR_START;
        params.put(SpeechConstant.PID, 1536); // 默认1536
        params.put(SpeechConstant.DECODER, 0); // 纯在线(默认)
        params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN); // 语音活动检测
        params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 2000); // 不开启长语音。开启VAD尾点检测，即静音判断的毫秒数。建议设置800ms-3000ms
        params.put(SpeechConstant.ACCEPT_AUDIO_DATA, false);// 是否需要语音音频数据回调
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);// 是否需要语音音量数据回调

        String json = null; //可以替换成自己的json
        json = new JSONObject(params).toString(); // 这里可以替换成你需要测试的json
        asr.send(event, json, null, 0, 0);
    }

    private void stop() {
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
    }

    public void releaseSpeech() {
        stop();
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
    }


    private void parseAsrPartialJsonData(String data) {
        Log.d(TAG, "parseAsrPartialJsonData data:" + data);
        Gson gson = new Gson();
        AsrPartialJsonData jsonData = gson.fromJson(data, AsrPartialJsonData.class);
        String resultType = jsonData.getResult_type();
        Log.d(TAG, "resultType:" + resultType);
        if (resultType != null && resultType.equals("final_result")) {
            final_result = jsonData.getBest_result();
        }
    }

    private String parseAsrFinishJsonData(String data) {
        Log.d(TAG, "parseAsrFinishJsonData data:" + data);
        Gson gson = new Gson();
        AsrFinishJsonData jsonData = gson.fromJson(data, AsrFinishJsonData.class);
        String desc = jsonData.getDesc();
        if (desc != null && desc.equals("Speech Recognize success.")) {
            return final_result;
        } else {
            String errorCode = "\n错误码:" + jsonData.getError();
            String errorSubCode = "\n错误子码:" + jsonData.getSub_error();
            String errorResult = errorCode + errorSubCode;
            return "解析错误" + desc + "\n" + errorResult;
        }
    }
}