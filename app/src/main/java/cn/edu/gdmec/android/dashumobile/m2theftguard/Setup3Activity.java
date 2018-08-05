package cn.edu.gdmec.android.dashumobile.m2theftguard;

import android.os.Bundle;
import android.widget.RadioButton;

import cn.edu.gdmec.android.dashumobile.R;

/**
 * Created by 达叔小生 on 2018/8/5.
 * email:2397923107@qq.com
 * version: 1.0
 */

public class Setup3Activity extends BaseSetUpActivity{
    @Override
    public void showNext() {
        startActivityAndFinishSelf(Setup4Activity.class);
    }

    @Override
    public void showPre() {
        startActivityAndFinishSelf(Setup2Activity.class);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_3);
        //设置第3个小圆点的颜色
        ((RadioButton)findViewById(R.id.rb_third)).setChecked(true);
    }
}
