package com.chase.timebank.fragment.detail;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chase.timebank.ActDetailActivity;
import com.chase.timebank.R;
import com.chase.timebank.adapter.ActListAdapter;
import com.chase.timebank.bean.ActListBean;
import com.chase.timebank.bean.ActListBean.RowsBean;
import com.chase.timebank.fragment.BaseFragment;
import com.chase.timebank.global.Url;
import com.chase.timebank.util.JsonResolveUtils;
import com.chase.timebank.util.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

/**
 * 创建者     Vincent
 * 创建时间   2016/7/28 19:41
 * 描述	      ${TODO}
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class ActivityListFragment extends BaseFragment implements OnRefreshListener, OnLoadmoreListener,ActListAdapter.OnRecyclerViewItemClickListener {
    private SmartRefreshLayout mSrlAct;
    private RecyclerView mRvActListRecyView;
    private ArrayList<RowsBean> mActRows;

    private static final String TAG = "ActivityListFragment";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = (String) msg.obj;
            //解析json数据
            _processJson(result);
        }
    };


    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_activity_list, null);
        mSrlAct = view.findViewById(R.id.srl_act);
        mRvActListRecyView = view.findViewById(R.id.rv_act_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//设置为垂直布局，这也是默认的
        mRvActListRecyView.setLayoutManager(layoutManager);// 设置布局管理器
        mRvActListRecyView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
        //下拉刷新 上拉加载
        mSrlAct.setOnRefreshListener(this);
        mSrlAct.setOnLoadmoreListener(this);

        return view;
    }

    @Override
    public void initData() {
        //触发自动刷新
        mSrlAct.autoRefresh();
    }

    @Override
    public void onRefresh(final RefreshLayout refreshlayout) {
        refreshlayout.getLayout().postDelayed(new Runnable() {
            @Override
            public void run() {
                //通过网络获取数据
                _queryActList();
                refreshlayout.finishRefresh();//完成刷新
//                refreshlayout.setLoadmoreFinished(false);//可以出发加载更多事件
                refreshlayout.setLoadmoreFinished(true);//不可以出发加载更多事件
            }
        }, 1500);
    }

    private void _processJson(String result) {
        ActListBean actListBean = JsonResolveUtils.parseJsonToBean(result, ActListBean.class);
        Log.i(TAG, "json解析：" + actListBean.toString());
        mActRows = actListBean.getRows();
        ActListAdapter actListAdapter = new ActListAdapter(mActivity, mActRows);
        mRvActListRecyView.setAdapter(actListAdapter);
        actListAdapter.setOnItemClickListener(this);
    }

    private void _queryActList() {
        RequestParams params = new RequestParams(Url.QUERY_ACTIVITY_URL);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "onSuccess: result=" + result);
                Message msg = new Message();
                msg.obj = result;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "onError: " + ex + "  ," + isOnCallback);
                ToastUtils.ToastShort(mActivity, "onError: " + ex + "  ," + isOnCallback);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i(TAG, "onCancelled: " + cex);
                ToastUtils.ToastShort(mActivity, "onCancelled: " + cex);
            }

            @Override
            public void onFinished() {
                Log.i(TAG, "onFinished");
                ToastUtils.ToastShort(mActivity, "onFinished");
            }
        });
    }

    @Override
    public void onLoadmore(final RefreshLayout refreshlayout) {
        refreshlayout.getLayout().postDelayed(new Runnable() {
            @Override
            public void run() {
                //以后再捣鼓
                refreshlayout.finishLoadmore();//完成加载更多
            }
        }, 1500);
    }


    @Override
    public void onItemClick(View view, ArrayList<RowsBean> data) {
        int position = mRvActListRecyView.getLayoutManager().getPosition(view);
        Log.i(TAG, "当前点击了第" + position + "个item");
        Intent intent = new Intent(mActivity, ActDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("act_list_data", mActRows.get(position));
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
