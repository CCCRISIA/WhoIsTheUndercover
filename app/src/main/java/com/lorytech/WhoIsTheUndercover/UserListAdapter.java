package com.lorytech.WhoIsTheUndercover;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ZhangChen on 2017/12/4 10:51
 */

public class UserListAdapter extends BaseAdapter {
    private MyClickListener mListener;

    //数据源
    private List<Map<String, Object>> codedata = new ArrayList<Map<String, Object>>();

    // 上下文对象
    private Context context;

    // 初始化自定义适配器
    public UserListAdapter(Context context, List<Map<String, Object>> codedata, MyClickListener mListener) {
        this.context = context;
        this.codedata = codedata;
        this.mListener = mListener;
    }

    @Override
    public int getCount() {
        return codedata.size();
    }

    @Override
    public Object getItem(int position) {
        return codedata.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //初始化ListView的子项布局
        ViewHolder viewHolder = null;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(context).inflate(R.layout.activity_userlist_item, null);

            viewHolder.btnOperate = (Button) convertView.findViewById(R.id.btn_userName);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 装配代码
        viewHolder.btnOperate.setText(codedata.get(position).get("userNo").toString().trim() + "号玩家");

        viewHolder.btnOperate.setOnClickListener(mListener);

        viewHolder.btnOperate.setEnabled(true);


        if(null != codedata.get(position).get("clicked") && codedata.get(position).get("clicked").toString().equals("true")){
            viewHolder.btnOperate.setEnabled(false);
            viewHolder.btnOperate.setText(codedata.get(position).get("userNo").toString().trim() + "号玩家（已查看）");
        }

        if(null != codedata.get(position).get("out") && codedata.get(position).get("out").toString().equals("true")){
            viewHolder.btnOperate.setEnabled(false);
            viewHolder.btnOperate.setText(codedata.get(position).get("userNo").toString().trim() + "号玩家（平民）");
        } else if(null != codedata.get(position).get("out") && codedata.get(position).get("out").toString().equals("false")){
            viewHolder.btnOperate.setEnabled(true);
            viewHolder.btnOperate.setText(codedata.get(position).get("userNo").toString().trim() + "号玩家");
        }

        if(null != codedata.get(position).get("isBlank") && codedata.get(position).get("isBlank").toString().equals("true")){
            viewHolder.btnOperate.setEnabled(false);
            viewHolder.btnOperate.setText(codedata.get(position).get("userNo").toString().trim() + "号玩家（白板）");
        }

        if(null != codedata.get(position).get("isUndercover") && codedata.get(position).get("isUndercover").toString().equals("true")){
            viewHolder.btnOperate.setEnabled(false);
            viewHolder.btnOperate.setText(codedata.get(position).get("userNo").toString().trim() + "号玩家（卧底）");
        }

        if(null != codedata.get(position).get("gameover") && codedata.get(position).get("gameover").toString().equals("true")){
            viewHolder.btnOperate.setEnabled(false);
            if(null != codedata.get(position).get("isBlank") && codedata.get(position).get("isBlank").toString().equals("true")){
                viewHolder.btnOperate.setText(codedata.get(position).get("userNo").toString().trim() + "号玩家（白板）");

            }else if(null != codedata.get(position).get("isUndercover") && codedata.get(position).get("isUndercover").toString().equals("true")){
                viewHolder.btnOperate.setText(codedata.get(position).get("userNo").toString().trim() + "号玩家（卧底）");

            } else {
                viewHolder.btnOperate.setText(codedata.get(position).get("userNo").toString().trim() + "号玩家");
            }
        }


        viewHolder.btnOperate.setTag(position);

        return convertView;

    }

    final static class ViewHolder {

        Button btnOperate;

    }

    /**
     * 用于回调的抽象类
     */
    public static abstract class MyClickListener implements View.OnClickListener {
        /**
         * 基类的onClick方法
         */
        @Override
        public void onClick(View v) {
            myOnClick((Integer) v.getTag(), v);
        }
        public abstract void myOnClick(int position, View v);
    }
}
