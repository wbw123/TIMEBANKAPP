package com.chase.timebank.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.chase.timebank.R;
import com.chase.timebank.bean.QueryResVolunteer.VolunteerBean;
import com.chase.timebank.bean.ReqMyBean.RowsBean;
import com.chase.timebank.util.DateJsonFormatUtil;

import java.util.ArrayList;

/**
 * Created by chase on 2018/4/16.
 */

public class ResQueryVolAdapter extends RecyclerView.Adapter<ResQueryVolAdapter.MyHolder> implements View.OnClickListener {
    private Context mContext;
    private ArrayList<VolunteerBean> mDatas;
    private String availAcceptTime;
    private String availStartTime;
    private String availEndTime;

    public ResQueryVolAdapter(Activity activity, ArrayList<VolunteerBean> rows) {
        this.mContext = activity;
        this.mDatas = rows;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 填充布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_res_query_vol_list, parent, false);
        MyHolder holder = new MyHolder(view);
        view.setOnClickListener(this);
        return holder;

    }

    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {
        String acceptTime = mDatas.get(position).getResAcceptTime();
        String startTime = mDatas.get(position).getResServiceStartTime();
        String endTime = mDatas.get(position).getResServiceEndTime();
        if (acceptTime != null) {
            availAcceptTime = DateJsonFormatUtil.longToDate(Long.valueOf(acceptTime));
        }
        if (startTime != null) {
            availStartTime = DateJsonFormatUtil.longToDate(Long.valueOf(startTime));
        }
        if (endTime != null) {
            availEndTime = DateJsonFormatUtil.longToDate(Long.valueOf(endTime));
        }
        holder.ResVolName.setText("志愿者姓名："+mDatas.get(position).getResUserGuid());
        holder.ResVolAddr.setText("接收地址：" + mDatas.get(position).getResAcceptAddress());
        holder.ResVolAcceptTime.setText("接收时间：" + availAcceptTime);
//        holder.ResVolStartEndTime.setText("从 " + availStartTime + " 到 " + availEndTime);
        holder.ResVolProcess.setText("处理状态：" + mDatas.get(position).getResTypeGuidProcessStatus());
        holder.ResVolEvaluate.setText("星级评价：" + mDatas.get(position).getResEvaluate()+"星");

        if (mDatas.get(position).getResEvaluate() != null) {//评价过得不能再评价
            holder.BtnVolEvaluate.setTextColor(Color.parseColor("#afb4b4"));
            holder.BtnVolEvaluate.setEnabled(false);
        }

        holder.BtnVolEvaluate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnButtonClickListener != null) {
                    mOnButtonClickListener.onBtnclick(v, position);
                }
            }
        });
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

    /*button点击事件*/

    /**
     * 按钮点击事件对应的接口
     */
    public interface ButtonInterface {
        void onBtnclick(View view, int position);
    }

    private ButtonInterface mOnButtonClickListener = null;

    /**
     * 按钮点击事件需要的方法
     */
    public void setOnButtonClickListener(ButtonInterface buttonInterface) {
        this.mOnButtonClickListener = buttonInterface;
    }

    class MyHolder extends RecyclerView.ViewHolder {
        private TextView ResVolName;
        private TextView ResVolAddr;
        private TextView ResVolAcceptTime;
//        private TextView ResVolStartEndTime;
        private TextView ResVolProcess;
        private TextView ResVolEvaluate;
        private Button BtnVolEvaluate;

        public MyHolder(View itemView) {
            super(itemView);
            ResVolName = itemView.findViewById(R.id.res_vol_name);
            ResVolAddr = itemView.findViewById(R.id.res_vol_addr);
            ResVolAcceptTime = itemView.findViewById(R.id.res_vol_accept_time);
//            ResVolStartEndTime = itemView.findViewById(R.id.res_vol_start_end_time);
            ResVolProcess = itemView.findViewById(R.id.res_vol_process);
            ResVolEvaluate = itemView.findViewById(R.id.res_vol_evaluate);
            BtnVolEvaluate = itemView.findViewById(R.id.btn_evaluate);

        }
    }

}
