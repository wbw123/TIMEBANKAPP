package com.chase.timebank.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chase.timebank.R;
import com.chase.timebank.bean.ReqMyBean.RowsBean;
import com.chase.timebank.fragment.detail.RequestMyFragment;
import com.chase.timebank.util.DateJsonFormatUtil;

import java.util.ArrayList;

/**
 * Created by chase on 2018/4/16.
 */

public class RequestMyAdapter extends RecyclerView.Adapter<RequestMyAdapter.MyHolder> implements View.OnClickListener {
    private Context mContext;
    private ArrayList<RowsBean> mDatas;
    private String availStartTime;
    private String availEndTime;
    private String issueTime;
    private int mCount;//一次性展示的数量

    public RequestMyAdapter(Activity activity, ArrayList<RowsBean> rows) {
        this.mContext = activity;
        this.mDatas = rows;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 填充布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_req_my_list, parent, false);
        MyHolder holder = new MyHolder(view);
        view.setOnClickListener(this);
        return holder;

    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        String startTime = mDatas.get(position).getReqAvailableStartTime();
        String endTime = mDatas.get(position).getReqAvailableEndTime();
        String issTime = mDatas.get(position).getReqIssueTime();
        if (startTime != null) {
            availStartTime = DateJsonFormatUtil.longToDate(Long.valueOf(startTime));
        }
        if (endTime != null) {
            availEndTime = DateJsonFormatUtil.longToDate(Long.valueOf(endTime));
        }
        if (issTime != null) {
            issueTime = DateJsonFormatUtil.longToDate(Long.valueOf(issTime));
        }
        String reqAddress = mDatas.get(position).getReqAddress();
        if (reqAddress != null) {
            String[] split = reqAddress.split(",");
            holder.ReqMyAddr.setText("地址：" + split[2]);
        } else {
            holder.ReqMyAddr.setText("地址：" + reqAddress);
        }
        holder.ReqMyTitle.setText(mDatas.get(position).getReqTitle());
        holder.ReqMyNum.setText("需 " + mDatas.get(position).getReqPersonNum() + " 人");
        holder.ReqMyClass.setText("分类：" + mDatas.get(position).getReqTypeGuidClass());
        holder.ReqMyStartEndTime.setText("从 " + availStartTime + " 到 " + availEndTime);
        holder.ReqMyIssueTime.setText("时间：" + issueTime);
        holder.ReqMyUrgency.setText("紧急程度：" + mDatas.get(position).getReqTypeGuidUrgency());
        holder.ReqMyApprStatus.setText("批准状态：" + mDatas.get(position).getReqTypeApproveStatus());
        holder.ReqMyProcStatus.setText("处理状态：" + mDatas.get(position).getReqTypeGuidProcessStatus());
        if (mDatas.get(position).getReqTypeGuidProcessStatus().equals("撤销")) {
            holder.ReqMyTitle.setTextColor(Color.parseColor("#afb4b4"));
            holder.ReqMyNum.setTextColor(Color.parseColor("#afb4b4"));
            holder.ReqMyAddr.setTextColor(Color.parseColor("#afb4b4"));
            holder.ReqMyClass.setTextColor(Color.parseColor("#afb4b4"));
            holder.ReqMyStartEndTime.setTextColor(Color.parseColor("#afb4b4"));
            holder.ReqMyIssueTime.setTextColor(Color.parseColor("#afb4b4"));
            holder.ReqMyUrgency.setTextColor(Color.parseColor("#afb4b4"));
            holder.ReqMyApprStatus.setTextColor(Color.parseColor("#afb4b4"));
            holder.ReqMyProcStatus.setTextColor(Color.parseColor("#afb4b4"));
        }
    }

    //获取item的数量
    @Override
    public int getItemCount() {
        mCount = RequestMyFragment.getCount();//mCount为一次性展示的数量
        //如果一次性展示的数量大于数据的数量，返回数据的数量
        if (mCount >= mDatas.size()){
            return mDatas.size();
        }else {
            return mCount;
        }
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v,(ArrayList<RowsBean>)v.getTag());
        }
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , ArrayList<RowsBean> data);
    }
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    class MyHolder extends RecyclerView.ViewHolder {
        private TextView ReqMyTitle;
        private TextView ReqMyNum;
        private TextView ReqMyAddr;
        private TextView ReqMyClass;
        private TextView ReqMyStartEndTime;
        private TextView ReqMyIssueTime;
        private TextView ReqMyUrgency;
        private TextView ReqMyApprStatus;
        private TextView ReqMyProcStatus;

        public MyHolder(View itemView) {
            super(itemView);
            ReqMyTitle = itemView.findViewById(R.id.req_my_title);
            ReqMyNum = itemView.findViewById(R.id.req_my_num);
            ReqMyAddr = itemView.findViewById(R.id.req_my_addr);
            ReqMyClass = itemView.findViewById(R.id.req_my_class);
            ReqMyStartEndTime = itemView.findViewById(R.id.req_my_start_end_time);
            ReqMyIssueTime = itemView.findViewById(R.id.req_my_issue_time);
            ReqMyUrgency = itemView.findViewById(R.id.req_my_urgency);
            ReqMyApprStatus = itemView.findViewById(R.id.req_my_approve_status);
            ReqMyProcStatus = itemView.findViewById(R.id.req_my_process_status);
        }
    }

}
