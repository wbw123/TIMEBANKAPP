package com.chase.timebank.home;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chase.timebank.R;
import com.chase.timebank.global.Url;
import com.chase.timebank.home.EMCData.DataBean.EMCDataBean;
import com.chase.timebank.news.NewsActivity;
import com.chase.timebank.util.PrefUtils;

import java.util.ArrayList;

/**
 * Created by chase on 2017/10/23.
 */

public class EMCAdapter extends BaseAdapter {
    private ArrayList<EMCDataBean> mEMCList;
    private Context mContext;
    private int mCount;

    public EMCAdapter(Activity mActivity, ArrayList<EMCDataBean> mEMCListData) {
        this.mEMCList = mEMCListData;
        this.mContext = mActivity;
    }


    @Override
    public int getCount() {
        mCount = NewsActivity.getCount();
        if (mCount >= mEMCList.size()){
            return mEMCList.size();
        }else {
            return mCount;
        }
    }

    @Override
    public EMCDataBean getItem(int position) {
        System.out.println("getItem:"+position);
        return mEMCList.get(position);
    }

    @Override
    public long getItemId(int position) {
        System.out.println("getItemId:"+position);
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_emc_list, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        EMCDataBean item = getItem(position);
//        holder.iv_image.setImageResource();
        Glide.with(mContext)
                .load(Url.BASE_URL+"/img/compatt6.jpg")
//                .fitCenter()//指定图片缩放类型为fitCenter
                .centerCrop()// 指定图片缩放类型为centerCrop
                .placeholder(R.mipmap.loading)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)//缓存转换后的最终图像
                .into(holder.iv_emc_image1);
        Glide.with(mContext)
                .load(Url.BASE_URL+item.imgUrl2)
//                .fitCenter()//指定图片缩放类型为fitCenter
                .centerCrop()// 指定图片缩放类型为centerCrop
                .placeholder(R.mipmap.loading)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)//缓存转换后的最终图像
                .into(holder.iv_emc_image2);
        Glide.with(mContext)
                .load(Url.BASE_URL+item.imgUrl3)
//                .fitCenter()//指定图片缩放类型为fitCenter
                .centerCrop()// 指定图片缩放类型为centerCrop
                .placeholder(R.mipmap.loading)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)//缓存转换后的最终图像
                .into(holder.iv_emc_image3);
        holder.tv_emc_title.setText(item.title);
        holder.tv_emc_from.setText("来源:"+item.from);
        holder.tv_emc_author.setText("作者:"+item.author);
        holder.tv_emc_date.setText(item.date);
        System.out.println("glide bottom："+Url.BASE_URL+item.imgUrl1);
        /*
        * 标记已读或未读
        * */
        String readIds = PrefUtils.getString("read_ids","",mContext);
        EMCDataBean mEMCDataBean = mEMCList.get(position);
        if (readIds.contains(mEMCDataBean.id)){
            //已读
            holder.tv_emc_title.setTextColor(Color.argb(255,155,155,155));
            holder.tv_emc_from.setTextColor(Color.argb(255,155,155,155));
            holder.tv_emc_author.setTextColor(Color.argb(255,155,155,155));
            holder.tv_emc_date.setTextColor(Color.argb(255,155,155,155));
        }else {
            //未读
            holder.tv_emc_title.setTextColor(Color.argb(170,0,0,0));
            holder.tv_emc_from.setTextColor(Color.argb(170,0,0,0));
            holder.tv_emc_author.setTextColor(Color.argb(170,0,0,0));
            holder.tv_emc_date.setTextColor(Color.argb(170,0,0,0));
        }

        return convertView;
    }

    class ViewHolder {

        public TextView tv_emc_title;
        public ImageView iv_emc_image1;
        public ImageView iv_emc_image2;
        public ImageView iv_emc_image3;
        public TextView tv_emc_from;
        public TextView tv_emc_author;
        public TextView tv_emc_date;

        public ViewHolder(View view) {
            tv_emc_title = view.findViewById(R.id.tv_emc_title);
            iv_emc_image1 = view.findViewById(R.id.iv_emc_image1);
            iv_emc_image2 = view.findViewById(R.id.iv_emc_image2);
            iv_emc_image3 = view.findViewById(R.id.iv_emc_image3);
            tv_emc_from = view.findViewById(R.id.tv_emc_from);
            tv_emc_author = view.findViewById(R.id.tv_emc_author);
            tv_emc_date = view.findViewById(R.id.tv_emc_date);
        }
    }
}
