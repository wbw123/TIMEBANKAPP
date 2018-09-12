package com.chase.timebank;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chase.timebank.adapter.RequestMyAdapter;
import com.chase.timebank.bean.ReqMyBean;
import com.chase.timebank.global.Url;
import com.chase.timebank.util.JsonResolveUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

public class MeRequestActivity extends AppCompatActivity implements View.OnClickListener, OnRefreshListener, RequestMyAdapter.OnRecyclerViewItemClickListener {

    private static final int SUCCESS_CODE = 101;
    private static final int ERROR_CODE = 102;
    private ImageView mIvback;
    private TextView mTvTitle;
    private SmartRefreshLayout mSrlReqMy;
    private RecyclerView mRvReqMyRecyView;
    private RelativeLayout mRlError;
    private RelativeLayout mRlBlank;
    private Button mBtnReload;
    private Button mBtnBlank;

    private static final String TAG = "MeRequestActivity";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            result = (String) msg.obj;
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
    private ArrayList<ReqMyBean.RowsBean> mRows;
    private static int count;//一次性展示数量
    private String mUserAccount;
    private Button mBtnAll;
    private Button mBtnDsh;
    private Button mBtnDqd;
    private Button mBtnBh;
    private String result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_request);
        initView();
        initData();
    }

    public void initView() {
        mIvback = (ImageView) findViewById(R.id.iv_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);

        /*button*/
        mBtnAll = (Button) findViewById(R.id.btn_all);
        mBtnDsh = (Button) findViewById(R.id.btn_dsh);
        mBtnDqd = (Button) findViewById(R.id.btn_dqd);
        mBtnBh = (Button) findViewById(R.id.btn_bh);
        mBtnAll.setOnClickListener(this);
        mBtnDsh.setOnClickListener(this);
        mBtnDqd.setOnClickListener(this);
        mBtnBh.setOnClickListener(this);

        mSrlReqMy = (SmartRefreshLayout) findViewById(R.id.srl_req_my);
        mRvReqMyRecyView = (RecyclerView) findViewById(R.id.rv_req_my_list);
        mRlError = (RelativeLayout) findViewById(R.id.rl_error);
        mRlBlank = (RelativeLayout) findViewById(R.id.rl_blank);
        mBtnReload = (Button) findViewById(R.id.btn_reload);
        mBtnBlank = (Button) findViewById(R.id.btn_blank);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//设置为垂直布局，这也是默认的
        mRvReqMyRecyView.setLayoutManager(layoutManager);// 设置布局管理器
        mRvReqMyRecyView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //下拉刷新 上拉加载
        mSrlReqMy.setOnRefreshListener(this);
//        mSrlReqMy.setOnLoadmoreListener(this);

        mTvTitle.setText("我的需求");
        mIvback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBtnBlank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //触发自动刷新
                mSrlReqMy.autoRefresh();
            }
        });
    }

    public void initData() {
        mUserAccount = getIntent().getStringExtra("me_userAccount");
        Log.i(TAG, mUserAccount);
            mSrlReqMy.autoRefresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_all:
                _processJson(result);
                break;
            case R.id.btn_dsh:
                _processJson1(result, "待审核");
                break;
            case R.id.btn_bh:
                _processJson1(result, "驳回");
                break;
            case R.id.btn_dqd:
                _processJson2(result, "待启动");
                break;
        }
    }

    @Override
    public void onRefresh(final RefreshLayout refreshlayout) {
        refreshlayout.getLayout().postDelayed(new Runnable() {
            @Override
            public void run() {
                count = 100;
                //通过网络获取数据
                _queryMyReq();
                refreshlayout.finishRefresh();//完成刷新
//                refreshlayout.setLoadmoreFinished(false);//可以出发加载更多事件
                refreshlayout.setLoadmoreFinished(true);//不可以出发加载更多事件
            }
        }, 1500);
    }

    /*@Override
    public void onLoadmore(final RefreshLayout refreshlayout) {
        refreshlayout.getLayout().postDelayed(new Runnable() {
            @Override
            public void run() {
                count += 10;
                    _queryMyReq();
                refreshlayout.finishLoadmore();//完成加载更多
                if (count > mRows.size()) {
                    Toast.makeText(getApplication(), "数据全部加载完毕", Toast.LENGTH_SHORT).show();
                    refreshlayout.setLoadmoreFinished(true);//将不会再次触发加载更多事件
                }
            }
        }, 1500);
    }*/

    private void _processJson1(String result, String states) {
        ReqMyBean reqMyBean = JsonResolveUtils.parseJsonToBean(result, ReqMyBean.class);
        Log.i(TAG, "json解析：" + reqMyBean.toString());
        mRows = reqMyBean.getRows();

        if (mRows.size() > 0) {
            ArrayList<ReqMyBean.RowsBean> collection = new ArrayList<>();
            for (int i = 0; i < mRows.size(); i++) {
                if (mRows.get(i).getReqTypeApproveStatus().equals(states)) {
                    collection.add(mRows.get(i));
                }
            }
            RequestMyAdapter reqMyAdapter = new RequestMyAdapter(this, collection);
            mRvReqMyRecyView.setAdapter(reqMyAdapter);
            reqMyAdapter.setOnItemClickListener(this);
        } else {
            mRlBlank.setVisibility(View.VISIBLE);
        }
    }
    private void _processJson2(String result, String states) {
        ReqMyBean reqMyBean = JsonResolveUtils.parseJsonToBean(result, ReqMyBean.class);
        Log.i(TAG, "json解析：" + reqMyBean.toString());
        mRows = reqMyBean.getRows();

        if (mRows.size() > 0) {
            ArrayList<ReqMyBean.RowsBean> collection = new ArrayList<>();
            for (int i = 0; i < mRows.size(); i++) {
                if (mRows.get(i).getReqTypeGuidProcessStatus().equals(states)) {
                    collection.add(mRows.get(i));
                }
            }
            //写缓存 将成功读取的json字符串写入XML中保存
//            CacheUtils.setCache(mUserAccount + Url.QUERY_REQ_MY_URL, result, MeRequestActivity.this);
            RequestMyAdapter reqMyAdapter = new RequestMyAdapter(this, collection);
            mRvReqMyRecyView.setAdapter(reqMyAdapter);
            reqMyAdapter.setOnItemClickListener(this);
        } else {
            mRlBlank.setVisibility(View.VISIBLE);
        }
    }

    private void _processJson(String result) {
        ReqMyBean reqMyBean = JsonResolveUtils.parseJsonToBean(result, ReqMyBean.class);
        Log.i(TAG, "json解析：" + reqMyBean.toString());
        mRows = reqMyBean.getRows();

        if (mRows.size() > 0) {
            //写缓存 将成功读取的json字符串写入XML中保存
//            CacheUtils.setCache(mUserAccount + Url.QUERY_REQ_MY_URL, result, MeRequestActivity.this);
            RequestMyAdapter reqMyAdapter = new RequestMyAdapter(this, mRows);
            mRvReqMyRecyView.setAdapter(reqMyAdapter);
            reqMyAdapter.setOnItemClickListener(this);
        } else {
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
            }

            @Override
            public void onFinished() {
                Log.i(TAG, "onFinished");
            }
        });
    }


    @Override
    public void onItemClick(View view, ArrayList<ReqMyBean.RowsBean> data) {
        int position = mRvReqMyRecyView.getLayoutManager().getPosition(view);
        Log.i(TAG, "当前点击了第" + position + "个item");
        Intent intent = new Intent(this, RequestDetailActivity.class);
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
