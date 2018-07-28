package com.chase.timebank.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chase.timebank.R;
import com.chase.timebank.bean.ActListBean.RowsBean;
import com.chase.timebank.util.DateJsonFormatUtil;

import java.util.ArrayList;

/**
 * Created by chase on 2018/4/16.
 */

public class ActListAdapter extends RecyclerView.Adapter<ActListAdapter.MyHolder> implements View.OnClickListener {
    private Context mContext;
    private ArrayList<RowsBean> mDatas;
    private String availStartTime;
    private String availEndTime;
    private String issueTime;

    public ActListAdapter(Activity activity, ArrayList<RowsBean> rows) {
        this.mContext = activity;
        this.mDatas = rows;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 填充布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_activity_list, parent, false);
        MyHolder holder = new MyHolder(view);
        view.setOnClickListener(this);
        return holder;

    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        String startTime = mDatas.get(position).getActivityStartTime();
        String endTime = mDatas.get(position).getActivityEndTime();
        if (startTime != null) {
            startTime = DateJsonFormatUtil.longToDate(Long.valueOf(startTime));
        }
        if (endTime != null) {
            endTime = DateJsonFormatUtil.longToDate(Long.valueOf(endTime));
        }
        holder.ActTitle.setText(mDatas.get(position).getActivityTitle());
        holder.ActNum.setText("需 " + mDatas.get(position).getActivityPersonNum() + " 人");
        holder.ActDesp.setText("描述：" + mDatas.get(position).getActivityDesp());
        holder.ActAddr.setText("地点：" + mDatas.get(position).getActivityAddress());
        holder.ActCommunity.setText("小区：" + mDatas.get(position).getActivityFromCommGuid());
        holder.ActStartEndTime.setText("从 " + startTime + " 到 " + endTime);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v,(ArrayList<RowsBean>)v.getTag());
        }
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, ArrayList<RowsBean> data);
    }
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    class MyHolder extends RecyclerView.ViewHolder {
        private TextView ActTitle;
        private TextView ActNum;
        private TextView ActDesp;
        private TextView ActAddr;
        private TextView ActCommunity;
        private TextView ActStartEndTime;

        public MyHolder(View itemView) {
            super(itemView);
            ActTitle = itemView.findViewById(R.id.act_title);
            ActNum = itemView.findViewById(R.id.act_num);
            ActDesp = itemView.findViewById(R.id.act_desp);
            ActAddr = itemView.findViewById(R.id.act_addr);
            ActCommunity = itemView.findViewById(R.id.act_community);
            ActStartEndTime = itemView.findViewById(R.id.act_start_end_time);

        }
    }

}
