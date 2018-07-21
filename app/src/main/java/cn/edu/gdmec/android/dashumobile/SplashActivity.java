package cn.edu.gdmec.android.dashumobile;


import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.widget.TextView;

public class SplashActivity extends Activity {
    private TextView tv_version_name;
    private int mLocalVersionCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //初始化UI
        initUI();
        //初始化数据
        initData();

        final VersionUpdateUtils versionUpdateUtils = new VersionUpdateUtils(tv_version_name,SplashActivity.this);
        new Thread(){
            @Override
            public void run(){
                super.run();
                versionUpdateUtils.getCloudVersion();

            }
        }.start();
    }


    /*
    *  初始化UI方法
    * */
    private void initUI() {
        tv_version_name = findViewById(R.id.tv_version_name);
    }
    /*
     * 获取数据方法
     * */
    private void initData() {
        //1.应用版本名称
        tv_version_name.setText(getVersionName());
    }
    /*
        * 获取版本名称：清单文件中
        * @return 应用版本名称 放回null代表异常
        * */
    public String getVersionName() {
        //1.包管理者对象packageManager
        PackageManager pm = getPackageManager();
        //2.从包的管理者对象中，获取指定包名大的基本信息(版本名称，版本号);
        //传0代表获取基本信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(this.getPackageName(), 0);
            //3.获取版本名称
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
