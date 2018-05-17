package com.xshell.xshelllib.ui;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.xshell.xshelllib.R;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.util.LogUtils;
import com.umeng.analytics.MobclickAgent;
import com.xshell.xshelllib.application.AppConfig;
import com.xshell.xshelllib.application.AppConstants;
import com.xshell.xshelllib.application.AppContext;
import com.xshell.xshelllib.utils.AppConsts;
import com.xshell.xshelllib.utils.Assets2DataCardUtil;
import com.xshell.xshelllib.utils.FileUtil;
import com.xshell.xshelllib.utils.FulStatusBarUtil;
import com.xshell.xshelllib.utils.NetUtil;
import com.xshell.xshelllib.utils.ParseConfig;
import com.xshell.xshelllib.utils.PreferenceUtil;
import com.xshell.xshelllib.utils.TimeUtil;
import com.xshell.xshelllib.utils.VersionUtil;
import com.xshell.xshelllib.utils.Write2SDCard;
import com.xshell.xshelllib.utils.ZIPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import okhttp3.Call;

/**
 * 初始界面（包括下载，更新等初始化）
 *
 * @author zzy
 */
public class LoadingActivity extends Activity {
    private static final String TAG = "LoadingActivity";
    private Context context;
    private static Handler mHandler;

    /**
     * 是否退出
     */
    private boolean isExit = false;
    /**   */
    private TextView showMessage;
    /**
     * 更新
     */
    private static final int UPDATE_MESSAGE = 222;
    private static final int SHOW_MESSAGE = 223;
    private static final int HIDE_MESSAGE = 224;

    private final static int SWITCH_TWOACTIVITY = 1000; // 主页
    private final static int SWITCH_GUIDACTIVITY = 1001; // 滑动手势

    /**
     * 解析的配置文件集合
     */
    private Map<String, String> configInfo;
    /**
     * 主线程
     */
    private Thread mainThread;

    // 程序的开始时间
    private long startTime;
    private static int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1;
    private String update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!this.isTaskRoot()) { //判断该Activity是不是任务空间的源Activity
            if (getIntent().hasCategory(Intent.CATEGORY_LAUNCHER) && getIntent().getAction().equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }
        //TODO 设置状态栏颜色
        //FulStatusBarUtil.setcolorfulStatusBar(this,R.color.actionsheet_blue);
        startTime = System.currentTimeMillis();
        setContentView(R.layout.xinyusoft_activity_splash);
        // 删除前台的检测更新包
        String[] temp = AppContext.CONTEXT.getPackageName().split("\\.");
        String fileName = FileUtil.getInstance().getFilePathInSDCard(AppConstants.XINYUSOFT_CACHE, temp[temp.length - 1] + "backgroundupdatehtml5.zip");
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        init();

        mainThread = new Thread() {
            @Override
            public void run() {
                // -1解析appconfig.xml
                docheckFile();
            }
        };

        mainThread.start();


    }

    private void docheckFile() {
        configInfo = ParseConfig.getInstance(LoadingActivity.this).getConfigInfo();
        //TODO 判断是否清理缓存了，如果是拿到以前的包进行解压
        try {
            // 判断是否覆盖安装了，是的话，重新解压并且重新设置时间
            if (VersionUtil.getVersionCode(context) > PreferenceUtil.getInstance().getAppThisCode()) {
                //-1 先把引导页的图片都删除完（如果有的话）
                File mImageFile = new File(getFilesDir() + "/imagepage/android");
                if (mImageFile.exists()) {
                    File[] files = mImageFile.listFiles();
                    if (files.length > 0) {
                        for (File mFile : files) {
                            mFile.delete();
                        }
                    }

                }
                // 设置app的code
                PreferenceUtil.getInstance().setAppThisCode(VersionUtil.getVersionCode(context));
                // 只需要第一次设置更新时间（app的还有页面的更新时间）
                PreferenceUtil.getInstance().setAppUpdateTime(configInfo.get("app-update-time"));
                PreferenceUtil.getInstance().setFileUpdateTime(configInfo.get("html-update-time"));
                // 保存homeActivity的path，由此获得它的Class
                PreferenceUtil.getInstance().setHomeActivityPath(configInfo.get("class-home"));

                File zip = Assets2DataCardUtil.write2DataFromInput("www.zip", "www.zip", AppContext.CONTEXT);
                ZIPUtils.unzip(zip.getAbsolutePath(), AppContext.CONTEXT.getFilesDir().getAbsolutePath());
                File cordovaFile = Assets2DataCardUtil.write2DataFromInput("cordova_android.zip", "cordova_android.zip", AppContext.CONTEXT);
                //解压html5文件

                //解压cordova需要的文件
                ZIPUtils.unzipTest(cordovaFile.getAbsolutePath(), getFilesDir().getAbsolutePath());

                // 解压之后就删除
                if (zip.exists()) {
                    zip.delete();
                }

                if (AppConfig.DEBUG)
                    Log.e("zzy", "第一次解压完成！!!!!!!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        //申请权限(用来使得sd卡获得写的权限)
        if (ContextCompat.checkSelfPermission(AppContext.CONTEXT, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(LoadingActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        } else {
            isAppUpdate();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (permissions.length == 0) {
                isAppUpdate();
            }
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isAppUpdate();
            } else {
                // Permission Denied
                Toast.makeText(context, "由于您拒绝了授权，更新将无法进行！", Toast.LENGTH_SHORT).show();
                jump();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!NetUtil.isNetworkConnected(LoadingActivity.this)) {
            Toast.makeText(context, "网络连连接异常", Toast.LENGTH_LONG).show();
            return;
        }
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onResume(context);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(context);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle result = data.getBundleExtra("result");
        resultCode++;
        if (result != null && !TextUtils.isEmpty(result.getString(AccountManager.KEY_AUTHTOKEN))) {
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 检查html5更新
     */
    private void checkH5() {
        // 没有写下载列表就直接跳过这个
        if (configInfo.get("xversion-html-name") == null || "".equals(configInfo.get("xversion-html-name"))) {
            Write2SDCard.getInstance().writeMsg("checkH5 is false, start jump()");
            jump();
            return;
        }
        String url = configInfo.get("xversion-update-url") + "&appname=" + configInfo.get("xversion-html-name") + "&time=" + PreferenceUtil.getInstance().getFileUpdateTime() + "&platform=Android" + "&xshllversion="
                + VersionUtil.getVersionCode(context);

        Write2SDCard.getInstance().writeMsg("html5-url is------------------ " + url);


        OkHttpUtils.get().url(url).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                Toast.makeText(LoadingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                jump();
            }

            @Override
            public void onResponse(String s, int i) {
                try {
                    if (AppConfig.DEBUG) {
                        Log.i("amtf", "开始检查html5:" + s);
                    }
                    showMessageUsehandler("请等待...");
                    toJsonFile(s);
                    if (AppConfig.WIRTE_SDCARD)
                        Write2SDCard.getInstance().writeMsg("start checked html5  end:" + s);
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (AppConfig.WIRTE_SDCARD)
                        Write2SDCard.getInstance().writeMsg("start checked html5 error！" + e.toString());
                    jump();
                }
            }
        });
    }

    /**
     * 下载html5
     *
     * @param res json的字符串
     * @throws JSONException
     */
    private void toJsonFile(String res) throws JSONException {
        JSONObject json = new JSONObject(res);
        JSONObject op = json.getJSONObject("op");
        String code = op.getString("code");

        int count = json.getInt("count");
        final String changezip = json.getString("changezip");

        JSONArray array = json.getJSONArray("changelist");
        for (int i = 0; i < array.length(); i++) {
            JSONObject html = array.getJSONObject(i);
            String path = html.getString("path");
            String status = html.getString("status");
            if ("DELETE".equals(status)) {  //找到带有删除状态的并且删除
                File file = new File(getFilesDir() + path.replaceAll("\\\\", "/"));
                if (file.exists()) {
                    file.delete();
                }
            }
        }

        //执行完之后，设置可以显示引导页，等待下次安装覆盖的时候来显示
        PreferenceUtil.getInstance().setShowGuidePage(true);

        if (code.equals("Y")) {// 检查更新成功
            if (count != 0) {// 有更新
                LogUtils.d("changeFileDao.addItem" + TimeUtil.now());
                if (AppConfig.WIRTE_SDCARD) {
                    Write2SDCard.getInstance().writeMsg("update html count :" + count);
                    Write2SDCard.getInstance().writeMsg("更新前的时间 last time:" + PreferenceUtil.getInstance().getFileUpdateTime());
                }
                zipUpdateH5(changezip);
                //是第一次安装，不需要改变状态。


            } else {// 没有更新
                PreferenceUtil.getInstance().setHtmlUpdate(false);
                if (AppConfig.DEBUG)
                    Log.e("amtf", "没有更新，直接跳转:" + PreferenceUtil.getInstance().getFileUpdateTime());
                Write2SDCard.getInstance().writeMsg(" no html update jump :" + PreferenceUtil.getInstance().getFileUpdateTime());
                jump();
            }
        } else {
            if (AppConfig.DEBUG)
                Log.e("amtf", "数据异常！code不为Y！");
            // 就算数据异常都是跳过
            jump();
        }
    }

    /**
     * 下载app
     */
    private void downloadApp(final String zipName) {
        mHandler.sendEmptyMessage(SHOW_MESSAGE);
        PreferenceUtil.getInstance().setDownAppDir(FileUtil.getInstance().getFilePathInSDCard(AppConstants.XINYUSOFT_CACHE, AppConstants.APP_APK_NAME));
        final String file = FileUtil.getInstance().getFilePathInSDCard(AppConstants.XINYUSOFT_CACHE, AppConstants.APP_APK_NAME);
        String url = configInfo.get("xversion-download-url") + configInfo.get("xversion-app-name") + "/" + configInfo.get("xversion-apk-name");
        OkHttpUtils.get().url(url).build().execute(new FileCallBack(Environment.getExternalStorageDirectory() + "/" + AppConstants.XINYUSOFT_CACHE, AppConstants.APP_APK_NAME) {
            @Override
            public void onError(Call call, Exception e, int i) {
                Toast.makeText(context, "下载失败，请重试！", Toast.LENGTH_SHORT).show();
                if (AppConfig.WIRTE_SDCARD)
                    Write2SDCard.getInstance().writeMsg("下载app失败 error:" + e.toString());
                checkH5();
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                //super.inProgress(progress, total, id);

                float allFileSize = (float) (Math.round(((float) (total * 1.0 / 1000000)) * 10) / 10.0);
                float currentFileSize = (float) (Math.round(((float) (progress * total * 1.0 / 1000000)) * 10) / 10.0);
                showMessageUsehandler("APP下载进度：" + currentFileSize + "M /" + allFileSize + "M");
            }

            @Override
            public void onResponse(File file, int i) {
                if (AppConfig.WIRTE_SDCARD)
                    Write2SDCard.getInstance().writeMsg("下载app");

                // 设置xversion的最新更新的时间
                String time = zipName.split("-")[1].split("\\.")[0];
                // 设置应当升级
                PreferenceUtil.getInstance().setNextToInstall(true);
                PreferenceUtil.getInstance().setAppUpdateTime(time);
                openApp();
                LoadingActivity.this.finish();
            }

        });
    }

    private void showMessageUsehandler(String showString) {
        Message msg = Message.obtain();
        msg.what = UPDATE_MESSAGE;
        msg.obj = showString;
        mHandler.sendMessage(msg);

    }

    private void init() {
        this.context = LoadingActivity.this;
        showMessage = (TextView) findViewById(R.id.showLoadingMessage);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case UPDATE_MESSAGE:
                        showMessage.setText((String) msg.obj);
                        break;
                }
            }
        };

    }

    private void openApp() {
        Intent openIntent = new Intent();
        openIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        openIntent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(PreferenceUtil.getInstance().getDownAppDir()));
        openIntent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(openIntent);
    }

    /**
     * 跳转
     */
    private void jump() {
        PreferenceUtil.getInstance().setDownloadingFile(false);// 文件下载中状态
        // mHandler.sendEmptyMessage(HIDE_MESSAGE);
        // 获取当前时间，然后进行2秒之后进入
        long endTime = System.currentTimeMillis();
        long dTime = endTime - startTime;
        Log.i("zzy", "dTime:" + dTime);
        if (dTime < 2000) {
            mmHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startJump();
                }
            }, (2000 - dTime));
        } else {
            startJump();
        }


    }

    private void startJump() {
        //判断这个目录下有没有引导页，没有就直接进入主界面
        File file = new File(LoadingActivity.this.getFilesDir().getAbsolutePath() + "/imagepage/android");
        if (file.exists()) {
            checkIsFirst();
        } else {
            try {
                Intent intent = new Intent(LoadingActivity.this, Class.forName(configInfo.get("class-home")));
                intent.putExtra("isdelayupdate", update);
                startActivity(intent);
                LoadingActivity.this.finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    // 检测是否是第一次开启app
    public void checkIsFirst() {
        String imageurl = this.getFilesDir().getAbsolutePath() + "/imagepage/android";
        // 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
        if (PreferenceUtil.getInstance().hadFirstRun() && PreferenceUtil.getInstance().isShowGuidePage())
            mmHandler.sendEmptyMessageDelayed(SWITCH_GUIDACTIVITY, 0);
        else
            mmHandler.sendEmptyMessageDelayed(SWITCH_TWOACTIVITY, 0);
    }

    private Handler mmHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SWITCH_TWOACTIVITY:
                    Intent intent;
                    try {
                        intent = new Intent(LoadingActivity.this, Class.forName(configInfo.get("class-home")));
                        startActivity(intent);

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case SWITCH_GUIDACTIVITY:
                    PreferenceUtil.getInstance().setShowGuidePage(false);
                    Intent intents = new Intent();
                    intents.setClass(LoadingActivity.this, GuideActivity.class);
                    LoadingActivity.this.startActivity(intents);
                    break;
            }
            LoadingActivity.this.finish();

        }


    };

    @Override
    public void onBackPressed() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            File file = new File(FileUtil.getInstance().getFilePathInSDCard(AppConstants.XINYUSOFT_CACHE, AppConstants.APP_APK_NAME));
            if (file.exists()) {
                file.delete();
            }
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    /**
     * 显示更新dialog
     */
    protected void showAppDialog(final String zip) throws Exception {
        String url = configInfo.get("xversion-update-content-url") + "&appname=" + configInfo.get("xversion-app-name");
        OkHttpUtils.get().url(url).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                checkH5();
            }

            @Override
            public void onResponse(String s, int i) {
                JSONObject json;
                try {
                    json = new JSONObject(s);
                    JSONObject op = json.getJSONObject("op");
                    String code = op.getString("code");
                    String content = json.getString("content");
                    JSONObject contentJson = new JSONObject(content);
                    content = contentJson.getString("info");
                    content = content.replaceAll("///", "\n");
                    if (code.equals("Y")) {// 检查更新成功
                        Builder builder = new Builder(LoadingActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                        builder.setMessage(content);
                        builder.setTitle("更新提示");
                        builder.setCancelable(false);
                        builder.setPositiveButton("立即更新", new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                downloadApp(zip);
                            }
                        });
                        builder.setNegativeButton("下次再说", new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                checkH5();
                            }
                        });
                        builder.create().show();
                    } else {
                        checkH5();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    checkH5();
                }
            }
        });
    }


    /**
     * 检查app更新
     */
    private void isAppUpdate() {
        if (!PreferenceUtil.getInstance().isNeedInstall()) {
            // 是否更新app
            String list = configInfo.get("xversion-update-url") + "&appname=" + configInfo.get("xversion-app-name") + "&time=";
            if ("".equals(PreferenceUtil.getInstance().getAppUpdateTime()) || (configInfo.get("xversion-app-name") == null)) {
                Write2SDCard.getInstance().writeMsg("isAppUpdate is false, start checkH5()");
                checkH5();
                return;
            }
            String url = list + PreferenceUtil.getInstance().getAppUpdateTime();
            Log.e("amtf", " AppUpdate更新 url--:" + url);
            Write2SDCard.getInstance().writeMsg("appUrl" + url);

            OkHttpUtils.get().url(url).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int i) {
                    //Write2SDCard.getInstance().writeMsg(e+"");
                    Toast.makeText(LoadingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    checkH5();
                }

                @Override
                public void onResponse(String s, int i) {
                    JSONObject json;
                    try {
                        json = new JSONObject(s);
                        //Write2SDCard.getInstance().writeMsg("appJson" + json);
                        JSONObject op = json.getJSONObject("op");
                        String code = op.getString("code");
                        if (code.equals("Y")) {// 检查更新成功
                            String changezip = json.getString("changezip");
                            int count = json.getInt("count");
                            if (count != 0) {// 有更新
                                //显示下dialog
                                showAppDialog(changezip);
                            } else {
                                checkH5();
                            }
                        } else {
                            //Toast.makeText(LoadingActivity.this, "网络异常，请稍候再试！", Toast.LENGTH_SHORT).show();
                            checkH5();
                        }
                    } catch (Exception e) {
                        Write2SDCard.getInstance().writeMsg("isAppUpdate error：" + e.toString());
                        e.printStackTrace();
                        checkH5();
                    }
                }
            });
        } else {
            checkH5();
        }


    }

    /**
     * 用zip的方式更新html5
     *
     * @param changezip zip的名字
     */
    private void zipUpdateH5(String changezip) {
        try {
            final String time = changezip.split("-")[1].split("\\.")[0];

            if (AppConfig.DEBUG) {
                Log.e("amtf", "changezip:" + changezip);
                Log.e("amtf", "time:" + time);
            }

            if (!"".equals(changezip)) {
                changezip = changezip.replaceAll("\\\\", "/");
                if (changezip.startsWith("/")) {
                    changezip = changezip.substring(1);
                }
            } else {
                if (AppConfig.WIRTE_SDCARD) {
                    Write2SDCard.getInstance().writeMsg("changezip error!!!!!!!!--" + changezip + "--");
                }
                jump();
                return;
            }
            //拼接下载html的zip包的url
            String url = configInfo.get("xversion-download-url") + changezip;
            OkHttpUtils.get().url(url).build().execute(new FileCallBack(FileUtil.getInstance().getSDCardRoot() + AppConstants.XINYUSOFT_CACHE, "updatehtml5.zip") {
                @Override
                public void onError(Call call, Exception e, int i) {
                    Toast.makeText(context, "下载失败，请稍候再试！", Toast.LENGTH_SHORT).show();
                    PreferenceUtil.getInstance().setHtmlUpdate(false);
                    jump();
                }

                @Override
                public void inProgress(float progress, long total, int id) {
                    float allFileSize = (float) (Math.round(((float) (total * 1.0 / 1000000)) * 10) / 10.0);
                    float currentFileSize = (float) (Math.round(((float) (progress * total * 1.0 / 1000000)) * 10) / 10.0);
                    showMessageUsehandler("请稍等..." + currentFileSize + "M /" + allFileSize + "M");
                }

                @Override
                public void onResponse(File file, int i) {
                    try {
                        ZIPUtils.unzipTest(file.getAbsolutePath(), AppContext.CONTEXT.getFilesDir().getAbsolutePath());
                        PreferenceUtil.getInstance().setFileUpdateTime(time);// 设置上次更新时间（设置最新版本号）
                        // 修改所有的文件为只读权限 （zip下载不能单个的设置只读）
                        // OfficeFileUtil.getInstance().setReadOnlyFiles(getFilesDir().getAbsolutePath());
                        if (AppConfig.DEBUG)
                            Log.e("zzy", "downdoading Html5 success time=" + time);
                        if (AppConfig.WIRTE_SDCARD) {
                            Write2SDCard.getInstance().writeMsg("downDoading Html5 success time=" + time);
                        }
                        PreferenceUtil.getInstance().setHtmlUpdate(true);
                        if (AppConfig.DEBUG) {
                            Log.e("amtf", "loading解压:" + true);
                        }
                        jump();
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (AppConfig.WIRTE_SDCARD) {
                            Write2SDCard.getInstance().writeMsg("unzip Html5 error!!!");
                            Write2SDCard.getInstance().writeMsg(e.toString());
                        }

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            if (AppConfig.WIRTE_SDCARD) {
                Write2SDCard.getInstance().writeMsg("zipUpdateH5() error!!!");
                Write2SDCard.getInstance().writeMsg(e.toString());
            }
            if (AppConfig.DEBUG) {
                Log.e("amtf", "解压异常：" + e.toString());
            }
            PreferenceUtil.getInstance().setHtmlUpdate(false);
            jump();
        }

    }


}
