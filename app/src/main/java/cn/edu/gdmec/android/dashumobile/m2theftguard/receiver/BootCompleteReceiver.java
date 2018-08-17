package cn.edu.gdmec.android.dashumobile.m2theftguard.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.edu.gdmec.android.dashumobile.App;

/**
 * Created by 达叔小生 on 2018/8/15.
 * email:2397923107@qq.com
 * version: 1.0
 */

public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ((App) (context.getApplicationContext())).correctSIM();
    }
}
