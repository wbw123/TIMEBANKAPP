package com.chase.timebank.fragment.detail;

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

import com.baidu.navisdk.ui.routeguide.mapmode.subview.P;
import com.chase.timebank.R;
import com.chase.timebank.adapter.tabAdapter.TransferRemitAdapter;
import com.chase.timebank.bean.ActListBean;
import com.chase.timebank.bean.TransferGatherBean;
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
 * 描述	      汇款列表
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */

public class TransferRemitFragment extends BaseFragment implements OnRefreshListener, OnLoadmoreListener, TransferRemitAdapter.OnRecyclerViewItemClickListener, TransferRemitAdapter.ButtonInterface {
    private static final int SUCCESS_CODE = 101;
    private static final int ERROR_CODE = 102;
    private SmartRefreshLayout mSrlTranRemit;
    private RecyclerView mRvTranRemitRecyView;
    private RelativeLayout mRlError;
    private RelativeLayout mRlBlank;
    private Button mBtnReload;
    private Button mBtnBlank;
//    private ArrayList<ActListBean.RowsBean> mActRows;

    private static final String TAG = "TransferRemitFragment";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = (String) msg.obj;
            switch (msg.what){
                case SUCCESS_CODE:
                    //解析json数据
                    _processJson(result);
                    break;
                case ERROR_CODE://加载失败，跳转到失败页面
                    mRvTranRemitRecyView.setVisibility(View.GONE);
                    mRlError.setVisibility(View.VISIBLE);
                    //失败页面重新加载按钮
                    mBtnReload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //触发自动刷新
                            mSrlTranRemit.autoRefresh();
                        }
                    });
                    break;
            }

        }
    };


    private ArrayList<TransferGatherBean.GatherBean> mGatherRows;
    private String mUserAccount;
    private String mCache;//缓存
    private static int count;//一次性展示的数量

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_transfer_remit, null);
        mSrlTranRemit = view.findViewById(R.id.srl_tran_remit);
        mRvTranRemitRecyView = view.findViewById(R.id.rv_tran_remit);
        mRlError = view.findViewById(R.id.rl_error);
        mRlBlank = view.findViewById(R.id.rl_blank);
        mBtnReload = view.findViewById(R.id.btn_reload);
        mBtnBlank = view.findViewById(R.id.btn_blank);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//设置为垂直布局，这也是默认的
        mRvTranRemitRecyView.setLayoutManager(layoutManager);// 设置布局管理器
        mRvTranRemitRecyView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
        //下拉刷新 上拉加载
        mSrlTranRemit.setOnRefreshListener(this);
        mSrlTranRemit.setOnLoadmoreListener(this);
        mBtnBlank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //触发自动刷新
                mSrlTranRemit.autoRefresh();
            }
        });
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        mUserAccount = mActivity.getUserAccount();
        //获取缓存
        mCache = CacheUtils.getCache(mUserAccount + Url.QUERY_TRANSFER_REMIT_URL,mActivity);
        if (!TextUtils.isEmpty(mCache)){
            Log.i(TAG, "带缓存，不自动刷新");
            _processJson(mCache);
        }else {
            //触发自动刷新
            mSrlTranRemit.autoRefresh();
            Log.i(TAG, "无缓存，自动刷新");
        }
    }

    @Override
    public void onRefresh(final RefreshLayout refreshlayout) {
        refreshlayout.getLayout().postDelayed(new Runnable() {
            @Override
            public void run() {
                count = 10;
                if (!TextUtils.isEmpty(mCache)){
                    //有缓存，解析json数据
                    Log.i(TAG, "发现缓存。。。");
                    _processJson(mCache);
                }
                //通过网络获取数据
                _remitTranData();
                refreshlayout.finishRefresh();//完成刷新
                refreshlayout.setLoadmoreFinished(false);//可以出发加载更多事件
//                refreshlayout.setLoadmoreFinished(true);//不可以出发加载更多事件
            }
        }, 1500);
    }

    private void _processJson(String result) {
        TransferGatherBean transferGatherBean = JsonResolveUtils.parseJsonToBean(result, TransferGatherBean.class);
        Log.i(TAG, "json解析：" + transferGatherBean.toString());
        mGatherRows = transferGatherBean.getRows();
        if (mGatherRows.size() >0){
            //写缓存，将成功读取的json字符串写入XML中保存
            CacheUtils.setCache(mUserAccount + Url.QUERY_TRANSFER_REMIT_URL,result,mActivity);
            TransferRemitAdapter transferRemitAdapter = new TransferRemitAdapter(mActivity, mGatherRows);
            mRvTranRemitRecyView.setAdapter(transferRemitAdapter);
            transferRemitAdapter.setOnItemClickListener(this);
        }else {
            mRlBlank.setVisibility(View.VISIBLE);
        }

//        transferRemitAdapter.setOnButtonClickListener(this);
    }


    @Override
    public void onLoadmore(final RefreshLayout refreshlayout) {
        refreshlayout.getLayout().postDelayed(new Runnable() {
            @Override
            public void run() {
                count +=10;
                if (!TextUtils.isEmpty(mCache)){
                    //有缓存，解析json数据
                    _processJson(mCache);
                }else {
                    //通过网络获取数据
                    _remitTranData();
                }
                refreshlayout.finishLoadmore();//完成加载更多
                if (count > mGatherRows.size()){
                    Toast.makeText(mActivity, "数据全部加载完毕", Toast.LENGTH_SHORT).show();
                    refreshlayout.setLoadmoreFinished(true);//将不会再次触发加载更多事件
                }
            }
        }, 1500);
    }

    @Override
    public void onItemClick(View view, ArrayList<TransferGatherBean.GatherBean> data) {

    }

    @Override
    public void onBtnclick(View view, int position) {

    }

    private void _remitTranData() {
        RequestParams params = new RequestParams(Url.QUERY_TRANSFER_REMIT_URL);
//        params.addBodyParameter("transGuid", mGatherRows.get(position).getTransGuid());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "onSuccess: result=" + result);
                Message msg = new Message();
                msg.what = SUCCESS_CODE;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "onError: " + ex + "  ," + isOnCallback);
//                ToastUtils.ToastShort(mActivity, "onError: " + ex + "  ," + isOnCallback);
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
            }
        });
    }
    /*@Override
    public void onItemClick(View view, ArrayList<ActListBean.RowsBean> data) {
        int position = mRvTranRemitRecyView.getLayoutManager().getPosition(view);
        Log.i(TAG, "当前点击了第" + position + "个item");
        Intent intent = new Intent(mActivity, ActDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("act_list_data", mActRows.get(position));
        intent.putExtras(bundle);
        startActivity(intent);
    }*/

    public static int getCount(){
        return count;
    }
}


