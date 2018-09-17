package com.chase.timebank.fragment.detail;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chase.timebank.R;
import com.chase.timebank.RequestDetailActivity;
import com.chase.timebank.adapter.RequestMyAdapter;
import com.chase.timebank.bean.ReqMyBean;
import com.chase.timebank.bean.ReqMyBean.RowsBean;
import com.chase.timebank.fragment.BaseFragment;
import com.chase.timebank.global.Url;
import com.chase.timebank.util.CacheUtils;
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
public class RequestMyFragment extends BaseFragment implements OnRefreshListener, OnLoadmoreListener, RequestMyAdapter.OnRecyclerViewItemClickListener {
    private static final int SUCCESS_CODE = 101;
    private static final int ERROR_CODE = 102;
    private SmartRefreshLayout mSrlReqMy;
    private RecyclerView mRvReqMyRecyView;
    private RelativeLayout mRlError;
    private RelativeLayout mRlBlank;
    private Button mBtnReload;
    private Button mBtnBlank;

    private static final String TAG = "RequestMyFragment";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = (String) msg.obj;
            switch (msg.what) {
                case SUCCESS_CODE:
                    //解析json数据
                    _processJson(result);
                    break;
                case ERROR_CODE://加载失败,跳转到失败页面，
                    mRvReqMyRecyView.setVisibility(View.GONE);
                    mRlError.setVisibility(View.VISIBLE);
                    //失败页面重新加载按钮
                    mBtnReload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //触发自动刷新
                            mSrlReqMy.autoRefresh();
                        }
                    });
                    break;
            }

        }
    };
    private ArrayList<RowsBean> mRows;
    private String mCache;//缓存   String类型
    private static int count;//一次性展示数量
    private String mUserAccount;


    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_request_my, null);
        mSrlReqMy = view.findViewById(R.id.srl_req_my);
        mRvReqMyRecyView = view.findViewById(R.id.rv_req_my_list);
        mRlError = view.findViewById(R.id.rl_error);
        mRlBlank = view.findViewById(R.id.rl_blank);
        mBtnReload = view.findViewById(R.id.btn_reload);
        mBtnBlank = view.findViewById(R.id.btn_blank);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//设置为垂直布局，这也是默认的
        mRvReqMyRecyView.setLayoutManager(layoutManager);// 设置布局管理器
        mRvReqMyRecyView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
        //下拉刷新 上拉加载
        mSrlReqMy.setOnRefreshListener(this);
        mSrlReqMy.setOnLoadmoreListener(this);

        mBtnBlank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //触发自动刷新
                mSrlReqMy.autoRefresh();
            }
        });
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        mUserAccount = mActivity.getUserAccount();
        Log.i(TAG, mUserAccount);
        //获取缓存
        mCache = CacheUtils.getCache(mUserAccount+Url.QUERY_REQ_MY_URL, mActivity);
        if (!TextUtils.isEmpty(mCache)) {
            Log.i(TAG, "有缓存 不自动刷新");
            _processJson(mCache);//mCache为json类型的数据，解析缓存
        } else {
            //触发自动刷新
            mSrlReqMy.autoRefresh();
            Log.i(TAG, "无缓存 自动刷新");
        }

    }

    //下拉刷新
    @Override
    public void onRefresh(final RefreshLayout refreshlayout) {
        refreshlayout.getLayout().postDelayed(new Runnable() {
            @Override
            public void run() {
                count = 10;
                if (!TextUtils.isEmpty(mCache)) {
                    // 有缓存 解析json缓存
                    Log.i(TAG, "发现缓存....");
                    _processJson(mCache);
                }
                //通过网络获取数据---我的请求列表
                _queryMyReq();
                refreshlayout.finishRefresh();//完成刷新
                refreshlayout.setLoadmoreFinished(false);//可以出发加载更多事件
            }
        }, 1500);
    }

    //上拉加载
    @Override
    public void onLoadmore(final RefreshLayout refreshlayout) {
        refreshlayout.getLayout().postDelayed(new Runnable() {
            @Override
            public void run() {
                count += 10;
                if (!TextUtils.isEmpty(mCache)) {
                    // 有缓存 解析json缓存
                    _processJson(mCache);
                } else {
                    //通过网络获取数据---我的请求列表
                    _queryMyReq();
                }
                refreshlayout.finishLoadmore();//完成加载更多
                if (count > mRows.size()) {
                    Toast.makeText(mActivity, "数据全部加载完毕", Toast.LENGTH_SHORT).show();
                    refreshlayout.setLoadmoreFinished(true);//将不会再次触发加载更多事件
                }
            }
        }, 1500);
    }

    //解析数据
    private void _processJson(String result) {
        ReqMyBean reqMyBean = JsonResolveUtils.parseJsonToBean(result, ReqMyBean.class);
        Log.i(TAG, "json解析：" + reqMyBean.toString());
        mRows = reqMyBean.getRows();
        if (mRows.size() > 0) {
            //写缓存 将成功读取的json字符串写入XML中保存
            CacheUtils.setCache(mUserAccount+Url.QUERY_REQ_MY_URL, result, mActivity);
            RequestMyAdapter reqMyAdapter = new RequestMyAdapter(mActivity, mRows);
            mRvReqMyRecyView.setAdapter(reqMyAdapter);
            reqMyAdapter.setOnItemClickListener(this);
        } else {//数据为空时，设置空页面可见
            mRlBlank.setVisibility(View.VISIBLE);
        }
    }

    private void _queryMyReq() {
        RequestParams params = new RequestParams(Url.QUERY_REQ_MY_URL);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "onSuccess: result=" + result);
                Message msg = new Message();
                msg.obj = result;
                msg.what = SUCCESS_CODE;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "onError: " + ex + "  ," + isOnCallback);
                Message msg = new Message();
                msg.what = ERROR_CODE;
                mHandler.sendMessage(msg);
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
    public void onItemClick(View view, ArrayList<RowsBean> data) {
        int position = mRvReqMyRecyView.getLayoutManager().getPosition(view);
        Log.i(TAG, "当前点击了第" + position + "个item");
        Intent intent = new Intent(mActivity, RequestDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("req_my_data", mRows.get(position));
        intent.putExtra("show_servicelist_or_requestmy", 2);//按钮的显示隐藏
        intent.putExtras(bundle);
        startActivity(intent);

    }
    public static int getCount() {
        return count;
    }
}
