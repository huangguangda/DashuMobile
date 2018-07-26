package cn.edu.gdmec.android.dashumobile.m1home.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.edu.gdmec.android.dashumobile.R;

/**
 * Created by 达叔小生 on 2018/7/27.
 * email:2397923107@qq.com
 * version: 1.0
 */

public class HomeAdapter extends BaseAdapter{
    int[] imageId = {R.drawable.safe,R.drawable.callmsgsafe,R.drawable.app,R.drawable.trojan,R.drawable.sysoptimize,R.drawable.taskmanager,R.drawable.netmanager,R.drawable.atools,R.drawable.settings};
    String[] names = {"手机防盗","通讯卫士","软件管家","手机杀毒","缓存清理","进程管理","流量统计","高级工具","设置中心"};
    private Context context;

    public HomeAdapter(Context context){
        this.context=context;
    }
    @Override
    public int getCount() {
        return 9;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view1 = View.inflate(context,R.layout.item_home,null);
        ImageView iv_icon = view1.findViewById(R.id.iv_home);
        TextView tv_name = view1.findViewById(R.id.tv_home);
        iv_icon.setImageResource(imageId[position]);
        tv_name.setText(names[position]);
        return view1;
    }
}
