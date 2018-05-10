//package com.daniulive.smartplayer;
//
//import android.app.Activity;
//import android.app.ActivityManager;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.Editable;
//import android.text.Selection;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.MotionEvent;
//import android.view.SurfaceView;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.AdapterView;
//import android.widget.Button;
//import android.widget.CompoundButton;
//import android.widget.EditText;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListAdapter;
//import android.widget.ListView;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.RelativeLayout;
//import android.widget.ScrollView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.bokecc.live.adapter.RedpacketDetailAdapter;
//import com.bokecc.live.adapter.SayAdapter;
//import com.bokecc.live.adapter.redpacketListAdapter;
//import com.bokecc.live.demo.BackgroundMaterial;
//import com.bokecc.live.demo.RedPacketaLlocation;
//import com.bokecc.live.demo.detailmode;
//import com.bokecc.live.util.CustomDialog;
//import com.bokecc.live.util.HeartService;
//import com.bokecc.live.util.MyRoundImageview;
//import com.bokecc.live.util.OnPushWtTextMessage;
//import com.bokecc.live.util.OnResultMessage;
//import com.bokecc.live.util.PushUtil;
//import com.bokecc.live.util.SocketUtil;
//import com.bokecc.live.util.talkBean;
//import com.bokecc.live.view.BarrageLayout;
//import com.daasuu.bl.BubbleLayout;
//import com.eventhandle.SmartEventCallback;
//import com.nostra13.universalimageloader.core.DisplayImageOptions;
//import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
//import com.videoengine.NTRenderer;
//import com.xinyusoft.xshelllib.R;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class SmartPlayer extends Activity implements OnPushWtTextMessage, View.OnClickListener {
//
//    private SurfaceView sSurfaceView = null;
//
//    private long playerHandle = 0;
//
//    private static final int PORTRAIT = 1;        //竖屏
//    private static final int LANDSCAPE = 2;        //横屏
//    private static final String TAG = "SmartPlayer";
//
//    private SmartPlayerJni libPlayer = null;
//
//    private int currentOrigentation = PORTRAIT;
//
//    private boolean isPlaybackViewStarted = false;
//
//    private String playbackUrl = null;
//    private BubbleLayout bubblelayout;
//    private RadioGroup radiogroup;
//    private RadioButton radiopay, radioweixin, radiolian;
//    private BarrageLayout mBarrageLayout;
//
//    Button btnSay, btnSendRedpacket, chooseButtom, btnFullscreenSendMsg;
//    TextView txtCopyright, tvCount, tvPlayMsg, totalMoney, redpacketSeeotherDetail, redpacketGoneDetail,
//            tvGrapDetail, tvgrapMoney, tvgrapText, chooseCenterTotal;
//
//    LinearLayout lLayout = null;
//    FrameLayout fFrameLayout = null;
//    private ImageView ivBack, ivRedrose, ivClose, ivLoudspeaker, ivPresentbox, ivOther, ivSay, ivPresent,
//            ivOthernone, ivRedpackage, rangseekbarClose, personalClose, redpacketShowClose, chooseClose,
//            clickOpen, redpacketDetailClose, redpacketGoneClose, ivGone;
//    private RelativeLayout rlPlayTop, presentRelative, buttomcontrl, buttonSay, rlPlay,
//           backgroundMaterialRelative, redpacketShowRelative, choosepayRelative, personalRelative,
//            rangseekbarRelative, redpacketDetailRelative, redpacketHasRelative, redpacketGoneRelative
//            ,bulletScreenRelative;
//    private LinearLayout llBottomLayout, buttonInfo;
//    EditText etSay, etMoneyCenter, etCenter, etMessage, etFullscreen;
//    private ScrollView scrollview;
//    private MyRoundImageview iv_people_photo;
//
//    private Context myContext;
//    SayAdapter sayadapter;
//    List<talkBean> mlist = new ArrayList<talkBean>();
//    private ListView listview, redpacketlistview, detailListview;
//    private List<String> redpacketlist;
//    private String roomid = "";
//    private View head;
//    private RedpacketDetailAdapter madapter;
//    private redpacketListAdapter redpacketListadapter;
//    DisplayImageOptions options;
//    List<detailmode> detaillist = new ArrayList<detailmode>();
//    private int grapredpacketnum = 0;
//    private String graptotalmoney = "0";
//    private int setNum = 0;
//    private int setMoney = 0;
//
//    private static boolean isActive = true;
//    Intent serverIntent = null;
//
//    private Handler handler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//
//            }
//        }
//    };
//
//    static {
//        System.loadLibrary("SmartPlayer");
//    }
//
//    @SuppressWarnings("deprecation")
//    @Override
//    protected void onCreate(Bundle icicle) {
//        super.onCreate(icicle);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
//        setContentView(R.layout.smartplayer_activity);
//        Log.i(TAG, "Run into OnCreate++");
//        roomid = getIntent().getStringExtra("id");
//        libPlayer = new SmartPlayerJni();
//        myContext = this.getApplicationContext();
//        serverIntent = new Intent(SmartPlayer.this, HeartService.class);
//        boolean bViewCreated = CreateView();
//
//        if (bViewCreated) {
//            inflateLayout();
//        }
//
//        PushUtil.setRoomid(roomid);
//        SocketUtil.isConnected();
//        //startService(serverIntent);
//        PushUtil.setOnWtTextMessages("C5294E13BF60D950", this);
//    }
//
//    /* For smartplayer demo app, the url is based on: baseURL + inputID
//     * For example:
//     * baseURL: rtmp://daniulive.com:1935/hls/stream
//     * inputID: 123456
//     * playbackUrl: rtmp://daniulive.com:1935/hls/stream123456
//     * */
//    private void GenerateURL(String id) {
//        if (id == null)
//            return;
//        String baseURL = "rtmp://daniulive.com:1935/hls/stream";
//        //String baseURL = "rtmp://daniulive.com:1935/hls/streamtest1";
//        playbackUrl = baseURL + id;
//    }
//
//    /* Generate basic layout */
//    private void inflateLayout() {
//
//        options = new DisplayImageOptions.Builder()
//                .showStubImage(R.drawable.logo)            // 设置图片下载期间显示的图片
//                .showImageForEmptyUri(R.drawable.logo)    // 设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.drawable.logo)        // 设置图片加载或解码过程中发生错误显示的图片
//                .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中
//                .cacheOnDisc(true)                            // 设置下载的图片是否缓存在SD卡中
//                .displayer(new RoundedBitmapDisplayer(20))    // 设置成圆角图片
//                .build();
//
//        head = View.inflate(this, R.layout.redpacket_listview_headview, null);
//
//        sSurfaceView = (SurfaceView) findViewById(R.id.sv);
//        fFrameLayout = (FrameLayout) findViewById(R.id.framelayout);
//        scrollview = (ScrollView) findViewById(R.id.scrollview);
//        rlPlayTop = (RelativeLayout) findViewById(R.id.rl_play_top);
//        buttonInfo = (LinearLayout) findViewById(R.id.button_info_botton);
//        buttomcontrl = (RelativeLayout) findViewById(R.id.button_info);
//        buttonSay = (RelativeLayout) findViewById(R.id.button_say);
//        presentRelative = (RelativeLayout) findViewById(R.id.present_relative);
//        ivBack = (ImageView) findViewById(R.id.iv_back);
//        ivSay = (ImageView) findViewById(R.id.iv_say);
//        ivPresent = (ImageView) findViewById(R.id.iv_present);
//        ivRedrose = (ImageView) findViewById(R.id.redrose);
//        ivClose = (ImageView) findViewById(R.id.close);
//        ivLoudspeaker = (ImageView) findViewById(R.id.loudspeaker);
//        ivPresentbox = (ImageView) findViewById(R.id.presentbox);
//        etSay = (EditText) findViewById(R.id.et_say);
//        btnSay = (Button) findViewById(R.id.btn_say);
//        tvCount = (TextView) findViewById(R.id.tv_onlinepeople);
//        ivOthernone = (ImageView) findViewById(R.id.iv_othernone);
//        ivOthernone.setOnClickListener(SmartPlayer.this);
//        ivOther = (ImageView) findViewById(R.id.iv_other);
//        rlPlay = (RelativeLayout) findViewById(R.id.rl_play);
//        tvPlayMsg = (TextView) findViewById(R.id.tv_play_msg);
//        ivRedrose = (ImageView) findViewById(R.id.redrose);
//        ivClose = (ImageView) findViewById(R.id.close);
//        ivLoudspeaker = (ImageView) findViewById(R.id.loudspeaker);
//        ivPresentbox = (ImageView) findViewById(R.id.presentbox);
//        ivRedpackage = (ImageView) findViewById(R.id.iv_redpackage);
//        rangseekbarClose = (ImageView) findViewById(R.id.rangseekbar_close);
//        personalClose = (ImageView) findViewById(R.id.personal_close);
//        redpacketShowClose = (ImageView) findViewById(R.id.redpacket_show_close);
//        chooseClose = (ImageView) findViewById(R.id.choosepay_close);
//        clickOpen = (ImageView) findViewById(R.id.click_open);
//        redpacketDetailClose = (ImageView) findViewById(R.id.redpacket_detail_close);
//        redpacketGoneClose = (ImageView) findViewById(R.id.redpacket_gone_close);
//        ivGone = (ImageView) findViewById(R.id.iv_gone);
//        iv_people_photo = (MyRoundImageview) findViewById(R.id.iv_people_photo);
//        totalMoney = (TextView) findViewById(R.id.total_money);
//        tvPlayMsg = (TextView) findViewById(R.id.tv_play_msg);
//        redpacketSeeotherDetail = (TextView) findViewById(R.id.redpacket_seeother_detail);
//        redpacketGoneDetail = (TextView) findViewById(R.id.redpacket_gone_detail);
//        tvGrapDetail = (TextView) head.findViewById(R.id.tv_grap_detail);
//        tvgrapMoney = (TextView) head.findViewById(R.id.tv_grap_money);
//        tvgrapText = (TextView) head.findViewById(R.id.tv_grap_text);
//        chooseCenterTotal = (TextView) findViewById(R.id.choose_center_total);
//        llBottomLayout = (LinearLayout) findViewById(R.id.ll_bottom_layout);
//        etSay = (EditText) findViewById(R.id.et_say);
//        etMoneyCenter = (EditText) findViewById(R.id.et_money_center);
//        etCenter = (EditText) findViewById(R.id.et_center);
//        etMessage = (EditText) findViewById(R.id.et_message);
//        btnSay = (Button) findViewById(R.id.btn_say);
//        btnSendRedpacket = (Button) findViewById(R.id.btn_send_redpacket);
//        chooseButtom = (Button) findViewById(R.id.choose_buttom);
//        scrollview = (ScrollView) findViewById(R.id.scrollview);
//        bubblelayout = (BubbleLayout) findViewById(R.id.bubblelayout);
//        ivOther = (ImageView) findViewById(R.id.iv_other);
//        radiogroup = (RadioGroup) findViewById(R.id.radiogroup);
//        radiopay = (RadioButton) findViewById(R.id.radio_pay);
//        radioweixin = (RadioButton) findViewById(R.id.radio_weixin);
//        radiolian = (RadioButton) findViewById(R.id.radio_lian);
//        redpacketlistview = (ListView) findViewById(R.id.redpacket_list);
//        backgroundMaterialRelative = (RelativeLayout) findViewById(R.id.personal_background_material_relative);
//        redpacketShowRelative = (RelativeLayout) findViewById(R.id.redpacket_show_relative);
//        personalRelative = (RelativeLayout) findViewById(R.id.personal_relative);
//        redpacketShowRelative = (RelativeLayout) findViewById(R.id.redpacket_show_relative);
//        choosepayRelative = (RelativeLayout) findViewById(R.id.choosepay_relative);
//        rangseekbarRelative = (RelativeLayout) findViewById(R.id.rangseekbar_relative);
//        redpacketDetailRelative = (RelativeLayout) findViewById(R.id.redpacket_detail_relative);
//        redpacketHasRelative = (RelativeLayout) findViewById(R.id.redpacket_has_relative);
//        redpacketGoneRelative = (RelativeLayout) findViewById(R.id.redpacket_gone_relative);
//        bulletScreenRelative = (RelativeLayout) findViewById(R.id.bullet_screen_relative);
//
//        ivBack.setOnClickListener(this);
//        rlPlay.setClickable(true);
//        ivOther.setOnClickListener(this);
//        ivSay.setOnClickListener(this);
//        btnSay.setOnClickListener(this);
//        ivPresent.setOnClickListener(this);
//        ivClose.setOnClickListener(this);
//        ivBack.setOnClickListener(this);
//        ivPresent.setOnClickListener(this);
//        ivRedrose.setOnClickListener(this);
//        ivClose.setOnClickListener(this);
//        ivLoudspeaker.setOnClickListener(this);
//        ivPresentbox.setOnClickListener(this);
//        ivOther.setOnClickListener(this);
//        ivRedpackage.setOnClickListener(this);
//        rangseekbarClose.setOnClickListener(this);
//        personalClose.setOnClickListener(this);
//        iv_people_photo.setOnClickListener(this);
//        btnSendRedpacket.setOnClickListener(this);
//        redpacketShowClose.setOnClickListener(this);
//        chooseClose.setOnClickListener(this);
//        chooseButtom.setOnClickListener(this);
//        clickOpen.setOnClickListener(this);
//        redpacketSeeotherDetail.setOnClickListener(this);
//        redpacketGoneDetail.setOnClickListener(this);
//        redpacketDetailClose.setOnClickListener(this);
//        backgroundMaterialRelative.setOnClickListener(this);
//        redpacketGoneClose.setOnClickListener(this);
//        ivGone.setOnClickListener(this);
//
//        mBarrageLayout = (BarrageLayout) findViewById(R.id.bl_barrage);
//        tvCount = (TextView) findViewById(R.id.tv_onlinepeople);
//        ivOthernone = (ImageView) findViewById(R.id.iv_othernone);
//        ivOthernone.setOnClickListener(this);
//        ivGone = (ImageView) findViewById(R.id.iv_gone);
//        rlPlay = (RelativeLayout) findViewById(R.id.rl_play);
//        listview = (ListView) findViewById(R.id.list_say);
//        detailListview = (ListView) findViewById(R.id.detail_listview);
//        detailListview.addHeaderView(head);
//        rlPlay.setClickable(true);
//        ivGone.setOnClickListener(this);
//        ivSay.setOnClickListener(this);
//        btnSay.setOnClickListener(this);
//
//        bubblelayout.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });
//        redpacketShowRelative.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });
//        choosepayRelative.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });
//        rangseekbarRelative.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });
//        personalRelative.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });
//        redpacketDetailRelative.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });
//
//
//        etSay.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//
//                } else {
//                    dispatchEditView();
//                    closeInputMethod();
//                }
//
//            }
//        });
//        etSay.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                dispatchEditView();
//                return false;
//            }
//        });
//
//        rlPlay.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                dispatchEditView();
//                layoutGone();
//                return false;
//            }
//        });
//
//        listview.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                dispatchEditView();
//                layoutGone();
//                return false;
//            }
//        });
//
//        rlPlayTop.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                dispatchEditView();
//                layoutGone();
//                return false;
//            }
//        });
//
//        bubblelayout.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });
//        redpacketShowRelative.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });
//        choosepayRelative.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });
//        rangseekbarRelative.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });
//        personalRelative.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });
//        redpacketDetailRelative.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });
//
//        etMoneyCenter.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                String str = etMoneyCenter.getText().toString();
//                if (str.trim().isEmpty()) {
//                    totalMoney.setText("0.00");
//                }
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Selection.setSelection(etMoneyCenter.getText(), etMoneyCenter.getText().length());
//                String str = etMoneyCenter.getText().toString();
//                if (str.trim().isEmpty()) {
//                    totalMoney.setText("0.00");
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                String str = etMoneyCenter.getText().toString();
//                String str2 = etCenter.getText().toString();
//                if (!str.trim().isEmpty()) {
//                    int num = Integer.parseInt(str);
//                    totalMoney.setText(keepTwoNum(num));
//                    if (num > 0) {
//                        btnSendRedpacket.setEnabled(true);
//                        btnSendRedpacket.setClickable(true);
//                        btnSendRedpacket.setBackgroundResource(R.drawable.red_packet_buttom_round_bg2);
//                    }
//                } else {
//                    btnSendRedpacket.setEnabled(false);
//                    btnSendRedpacket.setClickable(false);
//                    btnSendRedpacket.setBackgroundResource(R.drawable.red_packet_buttom_round_bg);
//                }
//
//            }
//        });
//
//        etCenter.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                Selection.setSelection(etCenter.getText(), etCenter.getText().length());
//            }
//        });
//
//        etMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                Selection.setSelection(etMessage.getText(), etMessage.getText().length());
//            }
//        });
//
//        radiopay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    radioweixin.setChecked(false);
//                    radiolian.setChecked(false);
//                }
//            }
//        });
//
//        radioweixin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    radiopay.setChecked(false);
//                    radiolian.setChecked(false);
//                }
//            }
//        });
//
//        radiolian.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    radioweixin.setChecked(false);
//                    radiopay.setChecked(false);
//                }
//            }
//        });
//
//        madapter = new RedpacketDetailAdapter(SmartPlayer.this, options, detaillist);
//        detailListview.setAdapter(madapter);
//        sayadapter = new SayAdapter(SmartPlayer.this, mlist);
//        listview.setAdapter(sayadapter);
//        redpacketListadapter = new redpacketListAdapter(SmartPlayer.this, redpacketlist, redpacketlistview);
//        redpacketlistview.setAdapter(redpacketListadapter);
//        setListViewHeightBasedOnChildren(redpacketlistview);
//        redpacketlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String monry = RedPacketaLlocation.moneymap.get(grapredpacketnum);
//                int sizes = RedPacketaLlocation.moneymap.size();
//                if (null != monry && !"".equals(monry)) {
//                    redpacketGoneRelative.setVisibility(View.GONE);
//                    redpacketHasRelative.setVisibility(View.VISIBLE);
//                } else {
//                    redpacketHasRelative.setVisibility(View.GONE);
//                    redpacketGoneRelative.setVisibility(View.VISIBLE);
//                }
//                redpacketShowRelative.setVisibility(View.VISIBLE);
//                redpacketShowRelative.startLayoutAnimation();
//            }
//        });
//
//        sayadapter = new SayAdapter(SmartPlayer.this, mlist);
//        listview = (ListView) findViewById(R.id.list_say);
//        listview.setAdapter(sayadapter);
//
//        GenerateURL(roomid);
//
//        if (isPlaybackViewStarted) {
//            Log.i(TAG, "Stop playback stream++");
//            startSmartPlay();
//            Log.i(TAG, "Stop playback stream--");
//        } else {
//            Log.i(TAG, "Start playback stream++");
//
//            playerHandle = libPlayer.SmartPlayerInit(myContext);
//
//            if (playerHandle == 0) {
//                Log.e(TAG, "surfaceHandle with nil..");
//                return;
//            }
//
//            libPlayer.SetSmartPlayerEventCallback(playerHandle, new EventHande());
//
//            libPlayer.SmartPlayerSetSurface(playerHandle, sSurfaceView);    //if set the second param with null, it means it will playback audio only..
//
//            //libPlayer.SmartPlayerSetSurface(surfaceHandle, null);
//
//            libPlayer.SmartPlayerSetAudioOutputType(playerHandle, 0);
//
//
//            if (playbackUrl == null) {
//                Log.e(TAG, "playback URL with NULL...");
//                return;
//            }
//
//            int iPlaybackRet = libPlayer.SmartPlayerStartPlayback(playerHandle, playbackUrl);
//
//            //int iPlaybackRet = libPlayer.SmartPlayerStartPlayback(surfaceHandle, strAudioOnlyURL);
//
//            if (iPlaybackRet != 0) {
//                Log.e(TAG, "StartPlayback strem failed..");
//                return;
//            }
//
//            isPlaybackViewStarted = true;
//            Log.i(TAG, "Start playback stream--");
//        }
//
//        redpacketlist = new ArrayList<>();
//
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (!isAppOnForeground()) {
//            isActive = false;
//        }
//    }
//
//    public boolean isAppOnForeground() {
//        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
//        String packageName = getApplicationContext().getPackageName();
//
//        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
//        if (appProcesses == null)
//            return false;
//
//        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
//            // The name of the process that this object is associated with.
//            if (appProcess.processName.equals(packageName) || appProcess.processName.equals(packageName + ":xinyu_remote") || appProcess.processName.contains("com.tencent.")) {
//                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                    return true;
//                }
//
//            }
//        }
//
//        return false;
//
//    }
//
//    private boolean startplay() {
//        if (isPlaybackViewStarted) {
//            Log.i(TAG, "Stop playback stream++");
//            startSmartPlay();
//            Log.i(TAG, "Stop playback stream--");
//        } else {
//            Log.i(TAG, "Start playback stream++");
//
//            playerHandle = libPlayer.SmartPlayerInit(myContext);
//
//            if (playerHandle == 0) {
//                Log.e(TAG, "surfaceHandle with nil..");
//                return true;
//            }
//
//            libPlayer.SetSmartPlayerEventCallback(playerHandle, new EventHande());
//
//            libPlayer.SmartPlayerSetSurface(playerHandle, sSurfaceView);    //if set the second param with null, it means it will playback audio only..
//
//            //libPlayer.SmartPlayerSetSurface(surfaceHandle, null);
//
//            libPlayer.SmartPlayerSetAudioOutputType(playerHandle, 0);
//
//
//            if (playbackUrl == null) {
//                Log.e(TAG, "playback URL with NULL...");
//                return true;
//            }
//
//            int iPlaybackRet = libPlayer.SmartPlayerStartPlayback(playerHandle, playbackUrl);
//
//            //int iPlaybackRet = libPlayer.SmartPlayerStartPlayback(surfaceHandle, strAudioOnlyURL);
//
//            if (iPlaybackRet != 0) {
//                Log.e(TAG, "StartPlayback strem failed..");
//                return true;
//            }
//
//            isPlaybackViewStarted = true;
//            Log.i(TAG, "Start playback stream--");
//        }
//        return false;
//    }
//
//
//    private void bubblelayoutIsVisiable() {
//        if (bubblelayout.getVisibility() == View.VISIBLE) {
//            bubblelayout.setVisibility(View.GONE);
//        } else {
//            bubblelayout.setVisibility(View.VISIBLE);
//        }
//    }
//
//    public static void setListViewHeightBasedOnChildren(ListView listView) {
//        ListAdapter listAdapter = listView.getAdapter();
//        if (listAdapter == null) {
//            return;
//        }
//        int totalHeight = 0;
//        if (listAdapter.getCount() != 0) {
//            View listItem = listAdapter.getView(0, null, listView);
//            listItem.measure(0, 0);
//            totalHeight = listItem.getMeasuredHeight();
//        }
//
//        ViewGroup.LayoutParams params = listView.getLayoutParams();
//
//        params.height = totalHeight * 2;
//        listView.setLayoutParams(params);
//    }
//
//    private void layoutGone() {
//        if (bubblelayout.getVisibility() == View.VISIBLE) {
//            bubblelayout.setVisibility(View.GONE);
//        }
//        if (redpacketShowRelative.getVisibility() == View.VISIBLE) {
//            redpacketShowRelative.setVisibility(View.GONE);
//        }
//        if (choosepayRelative.getVisibility() == View.VISIBLE) {
//            //choosepayRelative.setVisibility(View.GONE);
//        }
//        if (rangseekbarRelative.getVisibility() == View.VISIBLE) {
//            rangseekbarRelative.setVisibility(View.GONE);
//        }
//        if (personalRelative.getVisibility() == View.VISIBLE) {
//            personalRelative.setVisibility(View.GONE);
//        }
//        if (redpacketDetailRelative.getVisibility() == View.VISIBLE) {
//            redpacketDetailRelative.setVisibility(View.GONE);
//        }
//        if (presentRelative.getVisibility() == View.VISIBLE) {
//            presentRelative.setVisibility(View.GONE);
//        }
//
//    }
//
//    private void dispatchEditView() {
//        etSay.clearFocus();
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
//            }
//        });
//        buttonSay.setVisibility(View.GONE);
//        buttonInfo.setVisibility(View.VISIBLE);
//    }
//
//
//    private String keepTwoNum(int num) {
//        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
//        String ss = df.format(num);
//        return ss;
//    }
//
//    private void makeshuju() {
//        detaillist.clear();
//        detailmode m = new detailmode();
//        m.setIconurl("http://img15.3lian.com/2015/f2/116/d/3.jpg");
//        m.setId("0");
//        m.setName("zzz");
//        m.setMoney("0.05");
//        m.setTime("12:12:12");
//        detaillist.add(m);
//        m = new detailmode();
//        m.setIconurl("http://image.tianjimedia.com/uploadImages/2011/318/7VC5MPD84273.jpg");
//        m.setId("1");
//        m.setName("aaa");
//        m.setMoney("0.01");
//        m.setTime("12:12:13");
//        detaillist.add(m);
//        m = new detailmode();
//        m.setIconurl("http://img3.redocn.com/20101213/20101211_0e830c2124ac3d92718fXrUdsYf49nDl.jpg");
//        m.setId("1");
//        m.setName("aaa");
//        m.setMoney("0.01");
//        m.setTime("12:12:13");
//        detaillist.add(m);
//        m = new detailmode();
//        m.setIconurl("http://pic1a.nipic.com/2008-11-13/200811139126243_2.jpg");
//        m.setId("1");
//        m.setName("aaa");
//        m.setMoney("0.01");
//        m.setTime("12:12:13");
//        detaillist.add(m);
//        madapter.setData(detaillist);
//        madapter.notifyDataSetChanged();
//    }
//
//
//    private void sendScoketMessage(String content) {
//        if (!TextUtils.isEmpty(content.trim())) {
//            String str = "cmd=user.RecordMessage&fromuser=77&topic=" + roomid + "&subjecttype=groupTalk&contenttype=text&content=" + content;
//            SocketUtil.sendTextMessage(str, roomid, new OnResultMessage() {
//                @Override
//                public void resultMessage(String result) {
//                    try {
//                        JSONObject jo = new JSONObject(result);
//                        talkBean talkbean = new talkBean();
//                        if (jo.has("f")) {
//                            talkbean.setUserid(jo.getString("f"));
//                        }
//                        if (jo.has("content")) {
//                            talkbean.setContent(jo.getString("content"));
//                        }
//                        if (null != talkbean.getContent() && !"".equals(talkbean.getContent())) {
//                            mlist.add(talkbean);
//                            sayadapter.setData(mlist);
//                            sayadapter.notifyDataSetChanged();
//                            if (talkbean.getContent().contains("<红包>")) {
//                                String str = talkbean.getContent();
//                                str = str.substring(str.indexOf("<红包>") + 4, str.indexOf("赠送了一个红包"));
//                                redpacketlist.add(str);
//                                redpacketListadapter.setData(redpacketlist);
//                                setListViewHeightBasedOnChildren(redpacketlistview);
//                                redpacketListadapter.notifyDataSetChanged();
//                                redpacketlistview.setSelection(redpacketlistview.getAdapter().getCount() - 1);
//                                /*if (redpacketlist.size()==0) {
//                                    tvredpacketName3.setText(str.trim() + "发了一个红包");
//                                    startAnim1(redpacketOut3, true);
//                                    addToListhead(redpacketlist,str.trim());
//                                }else if (redpacketlist.size()>1){
//                                    //relativeOutAnim(redpacketOut3);
//                                    tvredpacketName3.setText(str.trim() + "发了一个红包");
//                                    startAnim1(redpacketOut3, true);
//                                    //startAnim1(redpacketOut3, true);
//                                    addToListhead(redpacketlist,str.trim());
//                                }else if (redpacketlist.size()==2){
//                                    relativeOutAnim(redpacketOut3);
//                                    tvredpacketName3.setText(str.trim() + "发了一个红包");
//                                    redpacketOut3.setVisibility(View.VISIBLE);
//                                    startAnim1(redpacketOut3, true);
//                                    //startAnim1(redpacketOut2, false);
//                                    addToListhead(redpacketlist,str.trim());
//                                }*/
//                            }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            });
//            etSay.setText("");
//        }
//    }
//
//    @Override
//    public void onWtTextMessage(String result) {
//        try {
//            JSONObject jo = new JSONObject(result);
//            talkBean talkbean = new talkBean();
//            talkbean.setUserid(jo.getString("f"));
//            talkbean.setContent(jo.getString("content"));
//            mlist.add(talkbean);
//            sayadapter.setData(mlist);
//            sayadapter.notifyDataSetChanged();
//            if (talkbean.getContent().contains("<红包>")) {
//                String str = talkbean.getContent();
//                str = str.substring(str.indexOf("<红包>") + 4, str.indexOf("赠送了一个红包"));
//                redpacketlist.add(str);
//                redpacketListadapter.setData(redpacketlist);
//                setListViewHeightBasedOnChildren(redpacketlistview);
//                redpacketListadapter.notifyDataSetChanged();
//                redpacketlistview.setSelection(redpacketlistview.getAdapter().getCount() - 1);
//                /*if (redpacketlist.size() == 0) {
//                    tvredpacketName3.setText(str.trim() + "发了一个红包");
//                    redpacketOut3.setVisibility(View.VISIBLE);
//                    startAnim1(redpacketOut3, true);
//                    addToListhead(redpacketlist, str.trim());
//                } else if (redpacketlist.size() > 1) {
//                   // relativeOutAnim(redpacketOut3);
//                    tvredpacketName3.setText(str.trim() + "发了一个红包");
//                    startAnim1(redpacketOut3, true);
//                    addToListhead(redpacketlist, str.trim());
//                } else if (redpacketlist.size() == 2) {
//                    relativeOutAnim(redpacketOut3);
//                    tvredpacketName3.setText(str.trim() + "发了一个红包");
//                    redpacketOut3.setVisibility(View.VISIBLE);
//                    startAnim1(redpacketOut3, true);
//                    //startAnim1(redpacketOut2, false);
//                    addToListhead(redpacketlist, str.trim());
//                }*/
//            }
//        } catch (Exception e) {
//        }
//
//    }
//
//
//    private void startSmartPlay() {
//        libPlayer.SmartPlayerClose(playerHandle);
//        playerHandle = 0;
//        isPlaybackViewStarted = false;
//
//        tvPlayMsg.setVisibility(View.GONE);
//    }
//
//
//    private void openSoftKeyBoard() {
//        etSay.setFocusable(true);
//        etSay.setFocusableInTouchMode(true);
//        etSay.requestFocus();
//        InputMethodManager inputManager =
//                (InputMethodManager) etSay.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputManager.showSoftInput(etSay, 0);
//    }
//
//    private void closeInputMethod() {
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        boolean isOpen = imm.isActive();
//        if (isOpen) {
//            // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
//            imm.hideSoftInputFromWindow(etSay.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//        }
//    }
//
//    public void showDialog(final String title, final String contentstr, String butleft) {
//        CustomDialog.Builder builder = new CustomDialog.Builder(SmartPlayer.this);
//        builder.setMessage(contentstr);
//        builder.setTitle(title);
//        builder.setPositiveButton(butleft, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                if (title.contains("温馨提示")) {
//                    dialog.dismiss();
//                } else {
//                    dialog.dismiss();
//                    SmartPlayer.this.finish();
//                }
//            }
//        });
//        builder.create().show();
//    }
//
//
//    @Override
//    public void onClick(View view) {
//        int id = view.getId();
//        if (id == R.id.iv_back) {
//            finish();
//            /*if (isPortrait()) {
//                finish();
//            } else {
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            }*/
//        } else if (id == R.id.iv_othernone) {
//            setPlayControllerVisible(true);
//        } else if (id == (R.id.iv_present)) {
//            presentRelative.setVisibility(View.VISIBLE);
//            presentRelative.startLayoutAnimation();
//        } else if (id == (R.id.close)) {
//            presentRelative.setVisibility(View.GONE);
//        } else if (id == (R.id.redrose)) {
//
//        } else if (id == (R.id.loudspeaker)) {
//
//        } else if (id == (R.id.presentbox)) {
//
//        } else if (id == R.id.boss_rela) {
//
//        } else if (id == R.id.iv_gone) {
//            if (ivGone.getVisibility() == View.VISIBLE) {
//                setPlayControllerVisible(false);
//            } else {
//                setPlayControllerVisible(true);
//            }
//        } else if (id == R.id.iv_say) {
//            buttonInfo.setVisibility(View.GONE);
//            buttonSay.setVisibility(View.VISIBLE);
//            openSoftKeyBoard();
//        } else if (id == R.id.btn_say) {
//            buttonSay.setVisibility(View.GONE);
//            buttonInfo.setVisibility(View.VISIBLE);
//            closeInputMethod();
//            String content = etSay.getText() + "";
//            if (!content.contains("<红包>")) {
//                content = "娜娜:" + content;
//            }
//            sendScoketMessage(content);
//        } else if (id == R.id.iv_other) {
//            bubblelayoutIsVisiable();
//        } else if (id == R.id.iv_redpackage) {
//            rangseekbarRelative.setVisibility(View.VISIBLE);
//            rangseekbarRelative.startLayoutAnimation();
//        } else if (id == R.id.rangseekbar_close) {
//            rangseekbarRelative.setVisibility(View.GONE);
//        } else if (id == R.id.personal_close) {
//            personalRelative.setVisibility(View.GONE);
//        } else if (id == R.id.iv_people_photo) {
//            if (personalRelative.getVisibility() == View.VISIBLE) {
//                personalRelative.setVisibility(View.GONE);
//            } else {
//                personalRelative.setVisibility(View.VISIBLE);
//                personalRelative.startLayoutAnimation();
//            }
//        } else if (id == R.id.btn_send_redpacket) {
//            String strnum = etCenter.getText().toString();
//            if (strnum.trim().isEmpty()) {
//                showDialog("温馨提示", "请输入完整的红包信息", "确定");
//                return;
//            }
//            if (btnSendRedpacket.isEnabled()) {
//                //发红包按钮
//                setNum = Integer.parseInt(strnum);
//                setMoney = Integer.parseInt(etMoneyCenter.getText().toString());
//                if ((setMoney * 0.1) / setNum < 0.01) {
//                    showDialog("温馨提示", "单个红包金额不可低于0.01元,/请重新填写金额", "确定");
//                    return;
//                }
//
//                chooseCenterTotal.setText(setMoney + "");
//                choosepayRelative.setVisibility(View.VISIBLE);
//                choosepayRelative.startLayoutAnimation();
//
//
//                rangseekbarRelative.setVisibility(View.GONE);
//                etMoneyCenter.setText("");
//                etCenter.setText("");
//                etMessage.setText("");
//                totalMoney.setText("0.00");
//                btnSendRedpacket.setBackgroundResource(R.drawable.red_packet_buttom_round_bg);
//                RedPacketaLlocation.moneymap.clear();
//                graptotalmoney = "0";
//                grapredpacketnum = 0;
//                RedPacketaLlocation loca = new RedPacketaLlocation(setNum, setMoney + "");
//            }
//        } else if (id == R.id.redpacket_out3) {
//            //小红包浮现点击事件
//            String monry = RedPacketaLlocation.moneymap.get(grapredpacketnum);
//            int sizes = RedPacketaLlocation.moneymap.size();
//            if (null != monry && !"".equals(monry)) {
//                redpacketGoneRelative.setVisibility(View.GONE);
//                redpacketHasRelative.setVisibility(View.VISIBLE);
//            } else {
//                redpacketHasRelative.setVisibility(View.GONE);
//                redpacketGoneRelative.setVisibility(View.VISIBLE);
//            }
//            redpacketShowRelative.setVisibility(View.VISIBLE);
//            redpacketShowRelative.startLayoutAnimation();
//        } else if (id == R.id.redpacket_show_close) {
//            redpacketShowRelative.setVisibility(View.GONE);
//        } else if (id == R.id.choosepay_close) {
//            choosepayRelative.setVisibility(View.GONE);
//        } else if (id == R.id.choose_buttom) {
//            choosepayRelative.setVisibility(View.GONE);
//            sendScoketMessage("<红包> 娜娜 赠送了一个红包");
//        } else if (id == R.id.click_open) {
//            String money = RedPacketaLlocation.moneymap.get(grapredpacketnum);
//            if (null == money || "".equals(money)) {
//                tvgrapMoney.setText("手太慢，没抢到");
//                tvgrapMoney.setTextSize(14);
//                tvgrapText.setVisibility(View.GONE);
//                redpacketDetailRelative.setVisibility(View.VISIBLE);
//                redpacketGoneRelative.setVisibility(View.VISIBLE);
//                redpacketDetailRelative.startLayoutAnimation();
//            } else {
//                makeshuju();
//                tvgrapMoney.setText(money);
//                tvgrapMoney.setTextSize(23);
//                graptotalmoney = (Double.parseDouble(graptotalmoney) + Double.parseDouble(money)) + "";
//                int yilinqu = grapredpacketnum + 1;
//                tvGrapDetail.setText("已领取" + yilinqu + "/" + setNum + "个，共" + graptotalmoney + "/" + setMoney + "元");
//                redpacketDetailRelative.setVisibility(View.VISIBLE);
//                redpacketHasRelative.setVisibility(View.VISIBLE);
//                redpacketDetailRelative.startLayoutAnimation();
//                RedPacketaLlocation.moneymap.remove(grapredpacketnum);
//                grapredpacketnum++;
//            }
//            redpacketShowRelative.setVisibility(View.GONE);
//        } else if (id == R.id.redpacket_seeother_detail) {
//            makeshuju();
//            redpacketShowRelative.setVisibility(View.GONE);
//            redpacketDetailRelative.setVisibility(View.VISIBLE);
//            redpacketDetailRelative.startLayoutAnimation();
//        } else if (id == R.id.redpacket_gone_detail) {
//            makeshuju();
//            redpacketShowRelative.setVisibility(View.GONE);
//            redpacketDetailRelative.setVisibility(View.VISIBLE);
//            redpacketDetailRelative.startLayoutAnimation();
//        } else if (id == R.id.redpacket_detail_close) {
//            redpacketDetailRelative.setVisibility(View.GONE);
//        } else if (id == R.id.personal_background_material_relative) {
//            Intent in = new Intent(SmartPlayer.this, BackgroundMaterial.class);
//            SmartPlayer.this.startActivity(in);
//        } else if (id == R.id.redpacket_gone_close) {
//            redpacketShowRelative.setVisibility(View.GONE);
//        }
//    }
//
//    private boolean isExit = false;
//
//    /**
//     * 硬返回键按键触发事件 处理一些退出activity前的任务
//     */
//    public void onExitActivity() {
//        if (isExit == false) {
//            isExit = true;
//            Toast.makeText(this, "再次点击，确认退出直播", Toast.LENGTH_SHORT).show();
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    isExit = false;
//                }
//            }, 1000);
//        } else {
//            finish();
//        }
//    }
//
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        if (keyCode == KeyEvent.KEYCODE_BACK
//                && event.getRepeatCount() == 0) {
//            onExitActivity();
//            return false;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    private void setPlayControllerVisible(boolean isVisible) {
//        int visibility = 0;
//        if (isVisible) {
//            visibility = View.VISIBLE;
//            ivOthernone.setVisibility(View.GONE);
//        } else {
//            visibility = View.GONE;
//            ivOthernone.setVisibility(View.VISIBLE);
//        }
//        rlPlayTop.setVisibility(visibility);
//        buttomcontrl.setVisibility(visibility);
//        bubblelayout.setVisibility(visibility);
//        redpacketlistview.setVisibility(visibility);
//    }
//
//    class EventHande implements SmartEventCallback {
//        @Override
//        public void onCallback(int code, long param1, long param2, String param3, String param4, Object param5) {
//            switch (code) {
//                case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_STARTED:
//                    Log.i(TAG, "开始。。");
//                    break;
//                case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_CONNECTING:
//                    Log.i(TAG, "连接中。。");
//                    break;
//                case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_CONNECTION_FAILED:
//                    Log.i(TAG, "连接失败。。");
//                    break;
//                case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_CONNECTED:
//                    Log.i(TAG, "连接成功。。");
//                    break;
//                case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_DISCONNECTED:
//                    Log.i(TAG, "连接断开。。");
//                    break;
//                case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_STOP:
//                    Log.i(TAG, "关闭。。");
//                    break;
//                case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_RESOLUTION_INFO:
//                    Log.i(TAG, "分辨率信息: width: " + param1 + ", height: " + param2);
//                    break;
//                case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_NO_MEDIADATA_RECEIVED:
//                    Log.i(TAG, "收不到媒体数据，可能是url错误。。");
//                    /*if (isPlaybackViewStarted) {
//                        Log.i(TAG, "Stop playback stream++");
//                        startSmartPlay();
//                        Log.i(TAG, "Stop playback stream--");
//                    } else {
//                        Log.i(TAG, "Start playback stream++");
//
//                        playerHandle = libPlayer.SmartPlayerInit(myContext);
//
//                        if (playerHandle == 0) {
//                            Log.e(TAG, "surfaceHandle with nil..");
//                            return;
//                        }
//
//                        libPlayer.SetSmartPlayerEventCallback(playerHandle, new EventHande());
//
//                        libPlayer.SmartPlayerSetSurface(playerHandle, sSurfaceView);    //if set the second param with null, it means it will playback audio only..
//
//                        //libPlayer.SmartPlayerSetSurface(surfaceHandle, null);
//
//                        libPlayer.SmartPlayerSetAudioOutputType(playerHandle, 0);
//
//
//                        if (playbackUrl == null) {
//                            Log.e(TAG, "playback URL with NULL...");
//                            return;
//                        }
//
//                        int iPlaybackRet = libPlayer.SmartPlayerStartPlayback(playerHandle, playbackUrl);
//
//                        //int iPlaybackRet = libPlayer.SmartPlayerStartPlayback(surfaceHandle, strAudioOnlyURL);
//
//                        if (iPlaybackRet != 0) {
//                            Log.e(TAG, "StartPlayback strem failed..");
//                            return;
//                        }
//
//                        isPlaybackViewStarted = true;
//                        Log.i(TAG, "Start playback stream--");
//                    }
//                    isActive = true;*/
//
//            }
//            // Log.i(TAG, "onCallback end..");
//        }
//    }
//
//    /* Create rendering */
//    private boolean CreateView() {
//
//        if (sSurfaceView == null) {
//             /*
//             *  useOpenGLES2:
//             *  If with true: Check if system supports openGLES, if supported, it will choose openGLES.
//             *  If with false: it will set with default surfaceView;
//             */
//            sSurfaceView = NTRenderer.CreateRenderer(this, true);
//        }
//
//        if (sSurfaceView == null) {
//            Log.i(TAG, "Create render failed..");
//            return false;
//        }
//
//        return true;
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//
//           /* Log.i(TAG, "Run into onConfigurationChanged++");
//
//            if (null != fFrameLayout)
//            {
//            	fFrameLayout.removeAllViews();
//            	fFrameLayout = null;
//            }
//
//            if (null != lLayout)
//            {
//                lLayout.removeAllViews();
//                lLayout = null;
//            }*/
//
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Log.i(TAG, "onConfigurationChanged, with LANDSCAPE。。");
//
//            inflateLayout();
//
//            currentOrigentation = LANDSCAPE;
//        } else {
//            Log.i(TAG, "onConfigurationChanged, with PORTRAIT。。");
//
//            inflateLayout();
//
//            currentOrigentation = PORTRAIT;
//        }
//
//        if (!isPlaybackViewStarted)
//            return;
//
//        libPlayer.SmartPlayerSetOrientation(playerHandle, currentOrigentation);
//
//        Log.i(TAG, "Run out of onConfigurationChanged--");
//    }
//
//    @Override
//    protected void onDestroy() {
//        Log.i(TAG, "Run into activity destory++");
//
//        if (playerHandle != 0) {
//            libPlayer.SmartPlayerClose(playerHandle);
//            playerHandle = 0;
//        }
//        super.onDestroy();
//        finish();
//        System.exit(0);
//        //stopService(serverIntent);
//    }
//
//}