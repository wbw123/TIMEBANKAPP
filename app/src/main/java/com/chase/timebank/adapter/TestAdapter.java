package com.chase.timebank.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chase.timebank.R;

import java.util.ArrayList;

/**
 * Created by chase on 2018/4/16.
 */

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.MyHolder> {
    private Context mContext;
    private ArrayList<String> mDatas;

    public TestAdapter(Activity activity, ArrayList<String> lists) {
        this.mContext = activity;
        this.mDatas = lists;
    }
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 填充布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_test, null);
        MyHolder holder = new MyHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.textView.setText(mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        public MyHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text);
        }
    }

}
