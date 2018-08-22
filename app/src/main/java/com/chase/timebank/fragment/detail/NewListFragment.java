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

import com.chase.timebank.R;
import com.chase.timebank.RequestDetailActivity;
import com.chase.timebank.adapter.RequestMyAdapter;
import com.chase.timebank.bean.ReqMyBean;
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
public class NewListFragment extends BaseFragment implements OnRefreshListener, OnLoadmoreListener, RequestMyAdapter.OnRecyclerViewItemClickListener {
    private SmartRefreshLayout mSrlNewList;
    private RecyclerView mRvNewListRecyView;

    private static final String TAG = "NewListFragment";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = (String) msg.obj;
            //解析json数据
            _processJson(result);
        }
    };
    private ArrayList<ReqMyBean.RowsBean> mRows;

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_new_list, null);
        mSrlNewList = view.findViewById(R.id.srl_new_list);
        mRvNewListRecyView = view.findViewById(R.id.rv_new_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//设置为垂直布局，这也是默认的
        mRvNewListRecyView.setLayoutManager(layoutManager);// 设置布局管理器
        mRvNewListRecyView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
        //下拉刷新 上拉加载
        mSrlNewList.setOnRefreshListener(this);
        mSrlNewList.setOnLoadmoreListener(this);

        return view;
    }

    @Override
    public void initData() {
        super.initData();
        //触发自动刷新
        mSrlNewList.autoRefresh();
    }

    @Override
    public void onRefresh(final RefreshLayout refreshlayout) {
        refreshlayout.getLayout().postDelayed(new Runnable() {
            @Override
            public void run() {
                //通过网络获取数据
                _queryMyReq();
                refreshlayout.finishRefresh();//完成刷新
//                refreshlayout.setLoadmoreFinished(false);//可以出发加载更多事件
                refreshlayout.setLoadmoreFinished(true);//不可以出发加载更多事件
            }
        }, 1500);
    }

    private void _processJson(String result) {
        ReqMyBean reqMyBean = JsonResolveUtils.parseJsonToBean(result, ReqMyBean.class);
        Log.i(TAG, "json解析：" + reqMyBean.toString());
        mRows = reqMyBean.getRows();
        RequestMyAdapter reqMyAdapter = new RequestMyAdapter(mActivity, mRows);
        mRvNewListRecyView.setAdapter(reqMyAdapter);
        reqMyAdapter.setOnItemClickListener(this);
    }

    private void _queryMyReq() {
        RequestParams params = new RequestParams(Url.QUERY_NEW_LIST_URL);
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
                //以后在捣鼓
                refreshlayout.finishLoadmore();//完成加载更多
            }
        }, 1500);
    }

    @Override
    public void onItemClick(View view, ArrayList<ReqMyBean.RowsBean> data) {
        int position = mRvNewListRecyView.getLayoutManager().getPosition(view);
        Log.i(TAG, "当前点击了第" + position + "个item");
        Intent intent = new Intent(mActivity, RequestDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("req_my_data", mRows.get(position));
        intent.putExtra("show_servicelist_or_requestmy", 1);
        intent.putExtras(bundle);
        startActivity(intent);

    }
}

