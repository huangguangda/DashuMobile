package cn.edu.gdmec.android.dashumobile.m2theftguard;

import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.RadioButton;

import cn.edu.gdmec.android.dashumobile.R;

/**
 * Created by 达叔小生 on 2018/8/5.
 * email:2397923107@qq.com
 * version: 1.0
 */

public class Setup2Activity extends BaseSetUpActivity{
    @Override
    public void showNext() {
         startActivityAndFinishSelf(Setup3Activity.class);
    }

    @Override
    public void showPre() {
        startActivityAndFinishSelf(Setup1Activity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_2);
        //设置第2个圆点的颜色
        ((RadioButton)findViewById(R.id.rb_second)).setChecked(true);
    }
}
