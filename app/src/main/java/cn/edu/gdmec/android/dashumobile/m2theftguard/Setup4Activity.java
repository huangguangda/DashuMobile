package cn.edu.gdmec.android.dashumobile.m2theftguard;

import android.os.Bundle;
import android.widget.RadioButton;

import cn.edu.gdmec.android.dashumobile.R;

/**
 * Created by 达叔小生 on 2018/8/5.
 * email:2397923107@qq.com
 * version: 1.0
 */

public class Setup4Activity extends BaseSetUpActivity{
    @Override
    public void showNext() {
        startActivityAndFinishSelf(LostFindActivity.class);
    }

    @Override
    public void showPre() {
        startActivityAndFinishSelf(Setup3Activity.class);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_4);
        //设置第4个小圆点的颜色
        ((RadioButton)findViewById(R.id.rb_four)).setChecked(true);
    }
}
