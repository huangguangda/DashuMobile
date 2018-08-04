package cn.edu.gdmec.android.dashumobile.m2theftguard.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import cn.edu.gdmec.android.dashumobile.R;

/**
 * Created by 达叔小生 on 2018/8/4.
 * email:2397923107@qq.com
 * version: 1.0
 */

public class SetUpPasswordDialog extends Dialog implements View.OnClickListener{
    //标题栏
    private TextView mTitleTV;
    //首次输入密码文本框
    public EditText mFirstPWDET;
    //确认密码文本框
    public EditText mAffirmET;
    //回调接口
    private MyCallBack myCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        setContentView(R.layout.setup_password_dialog);
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        mTitleTV = findViewById(R.id.tv_setuppwd_title);
        mFirstPWDET = findViewById(R.id.et_firstpwd);
        mAffirmET = findViewById(R.id.et_affirm_password);
        findViewById(R.id.btn_ok).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
    }

    public void setTitle(String title){
        if(!TextUtils.isEmpty(title)){
            mTitleTV.setText(title);
        }
    }

    public void setCallBack(MyCallBack myCallBack){
        this.myCallBack = myCallBack;
    }
    public SetUpPasswordDialog(Context context){
        super(context,R.style.dialog_custom);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ok:
                System.out.print("SetupPasswordDialog");
                myCallBack.ok();
                break;
            case R.id.btn_cancel:
                myCallBack.cancel();
                break;
        }
    }
    public interface MyCallBack{
        void ok();
        void cancel();
    }
}
