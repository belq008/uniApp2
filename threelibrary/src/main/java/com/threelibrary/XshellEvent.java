package com.threelibrary;

import org.json.JSONObject;

/**
 * Created by DELL on 2018/3/6.
 */

public class XshellEvent {
    public int what;

    public String msg;

    public JSONObject object;

    public void setJson(JSONObject obj) {
        object = obj;
    }

    public XshellEvent(int tag) {
        this.what = tag;
    }
}
