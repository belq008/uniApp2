package com.xshell.xshelllib.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import com.githang.statusbar.StatusBarCompat;
import com.xshell.xshelllib.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static android.content.Context.APP_OPS_SERVICE;

/**
 * Created by DELL on 2018/1/11.
 */

public class CommonUtil {

    public static boolean isNotificationEnable(Context context) {
        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        Class appOpsClass = null;
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION");
            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static String getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        String versionCode = "";
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode + "";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }


    /**
     * 设置状态栏颜色
     *
     * @param mCtx
     * @param textColor
     */
    public static void setStatusCustomBar(Activity mCtx, String textColor) {
        //设置字体 颜色
        boolean isDark = textColor.equals("dark") ? true : false;
        if (isDark) {
            StatusBarCompat.setStatusBarColor(mCtx, mCtx.getResources().getColor(R.color.black));
        } else {
            StatusBarCompat.setStatusBarColor(mCtx, mCtx.getResources().getColor(R.color.white));
        }


//        boolean isDark = textColor.equals("dark") ? true : false;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isDark) {//android6.0以后可以对状态栏文字颜色和图标进行修改
//            mCtx.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            //5.0及以上
//            View decorView = mCtx.getWindow().getDecorView();
//            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//            decorView.setSystemUiVisibility(option);
//            //根据上面设置是否对状态栏单独设置颜色
//            if (isDark) {
//                mCtx.getWindow().setStatusBarColor(mCtx.getResources().getColor(R.color.colorful_status_bar));
//            } else {
//                mCtx.getWindow().setStatusBarColor(Color.TRANSPARENT);
//            }
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            //4.4到5.0
//            WindowManager.LayoutParams localLayoutParams = mCtx.getWindow().getAttributes();
//            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
//        }
    }


}
