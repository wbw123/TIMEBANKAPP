package com.chase.timebank.adapter.tabAdapter;

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
import com.chase.timebank.bean.TransferGatherBean.GatherBean;
import com.chase.timebank.util.DateJsonFormatUtil;

import java.util.ArrayList;

/**
 * Created by chase on 2018/4/16.
 */

public class TransferGatherAdapter extends RecyclerView.Adapter<TransferGatherAdapter.MyHolder> implements View.OnClickListener {
    private Context mContext;
    private ArrayList<GatherBean> mDatas;
    private String issueTime;

    public TransferGatherAdapter(Activity activity, ArrayList<GatherBean> rows) {
        this.mContext = activity;
        this.mDatas = rows;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 填充布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_transfer_gather, parent, false);
        MyHolder holder = new MyHolder(view);
        view.setOnClickListener(this);
        return holder;

    }

    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {
        long transIssueTime = mDatas.get(position).getTransIssueTime();
        if (transIssueTime != 0) {
            issueTime = DateJsonFormatUtil.longToDate(transIssueTime);
        }
        String transTypeGuidProcessStatus = mDatas.get(position).getTransTypeGuidProcessStatus();
        holder.gatherFromGuid.setText("转账者账号名："+mDatas.get(position).getTransFromUserGuid());
        holder.gatherToAccount.setText("接受者账号名: " + mDatas.get(position).getTransToUserGuid());
        holder.gatherTime.setText("转账时间：" + issueTime);
        holder.gatherDesp.setText("备注：" + mDatas.get(position).getTransDesp());
        holder.gatherCurrency.setText("转账货币：" + mDatas.get(position).getTransCurrency());
        holder.gatherProcess.setText("转账进程：" + transTypeGuidProcessStatus);
        if ("已完成".equals(transTypeGuidProcessStatus) || "拒绝".equals(transTypeGuidProcessStatus)) {
            holder.gatherCancel.setTextColor(Color.parseColor("#afb4b4"));
            holder.gatherCancel.setEnabled(false);
            holder.gatherOk.setTextColor(Color.parseColor("#afb4b4"));
            holder.gatherOk.setEnabled(false);
        }
        holder.gatherOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnButtonClickListener != null) {
                    mOnButtonClickListener.onBtnclick(v,position);
                }
            }
        });
        holder.gatherCancel.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v,(ArrayList<GatherBean>)v.getTag());
        }
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, ArrayList<GatherBean> data);
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
        private TextView gatherFromGuid;
        private TextView gatherToAccount;
        private TextView gatherTime;
        private TextView gatherDesp;
        private TextView gatherCurrency;
        private TextView gatherProcess;
        private Button gatherOk;
        private Button gatherCancel;

        public MyHolder(View itemView) {
            super(itemView);
            gatherFromGuid = itemView.findViewById(R.id.tran_gather_from_guid);
            gatherToAccount = itemView.findViewById(R.id.tran_gather_to_account);
            gatherTime = itemView.findViewById(R.id.tran_gather_time);
            gatherDesp = itemView.findViewById(R.id.tran_gather_desp);
            gatherCurrency = itemView.findViewById(R.id.tran_gather_currency);
            gatherProcess = itemView.findViewById(R.id.tran_gather_process);
            gatherOk = itemView.findViewById(R.id.tran_gather_ok);
            gatherCancel = itemView.findViewById(R.id.tran_gather_cancel);

        }
    }

}
