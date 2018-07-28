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
import com.chase.timebank.util.DateJsonFormatUtil;

import java.util.ArrayList;

/**
 * Created by chase on 2018/4/16.
 */

public class ServiceMyAdapter extends RecyclerView.Adapter<ServiceMyAdapter.MyHolder> implements View.OnClickListener {
    private Context mContext;
    private ArrayList<VolunteerBean> mDatas;
    private String availAcceptTime;
    private String availStartTime;
    private String availEndTime;

    public ServiceMyAdapter(Activity activity, ArrayList<VolunteerBean> rows) {
        this.mContext = activity;
        this.mDatas = rows;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 填充布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_res_my, parent, false);
        MyHolder holder = new MyHolder(view);
        view.setOnClickListener(this);
        return holder;

    }

    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {
        String resTypeGuidProcessStatus = mDatas.get(position).getResTypeGuidProcessStatus();
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
        holder.ResMyAddr.setText("接收地址：" + mDatas.get(position).getResAcceptAddress());
        holder.ResMyAcceptTime.setText("接收时间：" + availAcceptTime);
        holder.ResMyStartEndTime.setText("从 " + availStartTime + " 到 " + availEndTime);
        holder.ResMyProcess.setText("处理状态：" + resTypeGuidProcessStatus);
        holder.ResMyEvaluate.setText("服务打分：" + mDatas.get(position).getResEvaluate());

        if ("撤销".equals(resTypeGuidProcessStatus)) {
            holder.ResMyCancel.setTextColor(Color.parseColor("#afb4b4"));
            holder.ResMyCancel.setEnabled(false);
            holder.ResMyUpdate.setTextColor(Color.parseColor("#afb4b4"));
            holder.ResMyUpdate.setEnabled(false);
            holder.ResMyNav.setTextColor(Color.parseColor("#afb4b4"));
            holder.ResMyNav.setEnabled(false);
        }
        holder.ResMyCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnButtonClickListener != null) {
                    mOnButtonClickListener.onBtnclick(v,position);
                }
            }
        });
        holder.ResMyUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnButtonClickListener != null) {
                    mOnButtonClickListener.onBtnclick(v,position);
                }
            }
        });
        holder.ResMyNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnButtonClickListener != null) {
                    mOnButtonClickListener.onBtnclick(v,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    /*item点击事件*/
    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v,(ArrayList<VolunteerBean>)v.getTag());
        }
    }
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, ArrayList<VolunteerBean> data);
    }
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    /*button点击事件*/
    /**
     * 按钮点击事件对应的接口
     */
    public interface ButtonInterface{
        void onBtnclick(View view, int position);
    }
    private ButtonInterface mOnButtonClickListener = null;
    /**
     *按钮点击事件需要的方法
     */
    public void setOnButtonClickListener(ButtonInterface buttonInterface){
        this.mOnButtonClickListener=buttonInterface;
    }



    class MyHolder extends RecyclerView.ViewHolder {
        private TextView ResMyAddr;
        private TextView ResMyAcceptTime;
        private TextView ResMyStartEndTime;
        private TextView ResMyProcess;
        private TextView ResMyEvaluate;
        private Button ResMyCancel;
        private Button ResMyUpdate;
        private Button ResMyNav;

        public MyHolder(View itemView) {
            super(itemView);
            ResMyAddr = itemView.findViewById(R.id.res_my_addr);
            ResMyAcceptTime = itemView.findViewById(R.id.res_my_accept_time);
            ResMyStartEndTime = itemView.findViewById(R.id.res_my_start_end_time);
            ResMyProcess = itemView.findViewById(R.id.res_my_process);
            ResMyEvaluate = itemView.findViewById(R.id.res_my_evaluate);
            ResMyCancel = itemView.findViewById(R.id.res_my_cancel);
            ResMyUpdate = itemView.findViewById(R.id.res_my_update);
            ResMyNav = itemView.findViewById(R.id.res_my_nav);

        }
    }

}
