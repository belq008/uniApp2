package com;

import android.content.Context;
import android.view.View;


import com.xinyusoft.xinyudigg.R;
import com.xshell.xshelllib.utils.PreferenceUtil;
import com.xshell.xshelllib.utils.RequestWebViewURL;
import com.xshell.xshelllib.view.UpdateContentDialog;

import org.apache.cordova.CordovaWebView;

import java.io.File;

/**
 * Created by Administrator on 2017/11/28.
 */

public class UiHelper extends Thread {

    private Context context;


    public UiHelper(Context mCtx) {
        this.context = mCtx;

    }


    @Override
    public void run() {
        super.run();
        doTask();
    }

    private void doTask() {
        File file = new File(context.getFilesDir().getAbsolutePath() + File.separator + "upgradeinfo.html");
        if (!PreferenceUtil.getInstance().hadFirstRun()) {
            if (PreferenceUtil.getInstance().isHtmlUpdate()) {
                // PreferenceUtil.getInstance().setHtmlUpdate(false);
                if (file.exists()) {
                    String updateUrl = "file:///" + context.getFilesDir().getAbsolutePath() + File.separator + "upgradeinfo.html";
                    UpdateContentDialog dialog = new UpdateContentDialog(context, R.style.xinyusoft_dialog_fullscreen, updateUrl);
                    dialog.show();
                }
            }
        }
        if (PreferenceUtil.getInstance().hadFirstRun()) {
            PreferenceUtil.getInstance().setFirstRun(false);
        }


    }
}
