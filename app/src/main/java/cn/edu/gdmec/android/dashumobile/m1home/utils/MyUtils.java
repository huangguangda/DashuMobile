package cn.edu.gdmec.android.dashumobile.m1home.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by 达叔小生 on 2018/7/18.
 * email:2397923107@qq.com
 * version: 1.0
 */

public class MyUtils {
    public static String getVersion(Context context) {
        PackageManager packageManager = context.getPackageManager();

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
