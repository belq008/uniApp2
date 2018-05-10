//package com;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.text.format.DateUtils;
//import android.view.View;
//import android.widget.Toast;
//
//import com.xshell.xshelllib.utils.CalendarUtil;
//import com.xshell.xshelllib.utils.TimeUtil;
//
///**
// * Created by DELL on 2018/5/7.
// */
//
//public class CalendarActiviry extends Activity implements View.OnClickListener {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        initView();
//
//    }
//
//    private void initView() {
//        this.findViewById(R.id.tv_add_calendar).setOnClickListener(this);
//        this.findViewById(R.id.tv_delet_calendar).setOnClickListener(this);
//
//    }
//
//    @Override
//    public void onClick(View v) {
//        int id = v.getId();
//
//        switch (id) {
//            case R.id.tv_add_calendar:
//                addCalendar();
//                break;
//
//            case R.id.tv_delet_calendar:
//                deletCalendar();
//                break;
//        }
//    }
//
//    private void deletCalendar() {
////        CalendarUtil.getInstance().deleteCalendarEvent(CalendarActiviry.this, "我是中国人");
//    }
//
//    private void addCalendar() {
//        CalendarUtil.getInstance().addCalendarEvent(CalendarActiviry.this, "我是中国人", "今天世界杯决赛", TimeUtil.getUpdataTimeLong("201805071630"), TimeUtil.getUpdataTimeLong("201805071730"));
//    }
//}
