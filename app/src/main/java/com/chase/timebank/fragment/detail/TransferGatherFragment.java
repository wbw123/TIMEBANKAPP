package com.chase.timebank.fragment.detail;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chase.timebank.R;
import com.chase.timebank.adapter.tabAdapter.TransferGatherAdapter;
import com.chase.timebank.bean.ResultModel;
import com.chase.timebank.bean.TransferGatherBean;
import com.chase.timebank.bean.TransferGatherBean.GatherBean;
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
public class TransferGatherFragment extends BaseFragment implements OnRefreshListener, OnLoadmoreListener, TransferGatherAdapter.OnRecyclerViewItemClickListener, TransferGatherAdapter.ButtonInterface {
    private SmartRefreshLayout mSrlTranGather;
    private RecyclerView mRvTranGatherRecyView;

    private static final String TAG = "TransferGatherFragment";
    private static final int QUERY_TRANSFER_GATHER_URL = 401;
    private static final int TRANSFER_GATHER_OK_URL = 402;
    private static final int TRANSFER_GATHER_CANCEL_URL = 403;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case QUERY_TRANSFER_GATHER_URL:
                    String result = (String) msg.obj;
                    //解析json数据
                    _processJson(result);
                    break;
                case TRANSFER_GATHER_OK_URL:
                    _processData(msg);
                    break;
                case TRANSFER_GATHER_CANCEL_URL:
                    _processData(msg);
                    break;
            }

        }
    };

    private void _processData(Message msg) {
        String result1 = (String) msg.obj;
        //解析json数据
        ResultModel resultModel = JsonResolveUtils.parseJsonToBean(result1, ResultModel.class);
        Log.i(TAG, "json解析：" + resultModel.toString());
        if (resultModel.getCode() == 1) {
            Log.i(TAG, resultModel.getMsg());
            ToastUtils.ToastLong(getContext(), resultModel.getMsg());
            mSrlTranGather.autoRefresh();//刷新
        } else {
            Log.i(TAG, "数据库更新失败！");
            ToastUtils.ToastLong(getContext(), "数据库异常，请稍后重试！");
        }
    }

    private ArrayList<GatherBean> mGatherRows;


    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_transfer_gather, null);
        mSrlTranGather = view.findViewById(R.id.srl_tran_gather);
        mRvTranGatherRecyView = view.findViewById(R.id.rv_tran_gather);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//设置为垂直布局，这也是默认的
        mRvTranGatherRecyView.setLayoutManager(layoutManager);// 设置布局管理器
        mRvTranGatherRecyView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
        //下拉刷新 上拉加载
        mSrlTranGather.setOnRefreshListener(this);
        mSrlTranGather.setOnLoadmoreListener(this);

        return view;
    }

    @Override
    public void initData() {
        //触发自动刷新
        mSrlTranGather.autoRefresh();
    }

    @Override
    public void onRefresh(final RefreshLayout refreshlayout) {
        refreshlayout.getLayout().postDelayed(new Runnable() {
            @Override
            public void run() {
                //通过网络获取数据
                _updateORcancelResMy(0, Url.QUERY_TRANSFER_GATHER_URL, QUERY_TRANSFER_GATHER_URL,false);
                refreshlayout.finishRefresh();//完成刷新
//                refreshlayout.setLoadmoreFinished(false);//可以出发加载更多事件
                refreshlayout.setLoadmoreFinished(true);//不可以出发加载更多事件
            }
        }, 1500);
    }

    private void _processJson(String result) {
        TransferGatherBean transferGatherBean = JsonResolveUtils.parseJsonToBean(result, TransferGatherBean.class);
        Log.i(TAG, "json解析：" + transferGatherBean.toString());
        mGatherRows = transferGatherBean.getRows();
        TransferGatherAdapter transferGatherAdapter = new TransferGatherAdapter(mActivity, mGatherRows);
        mRvTranGatherRecyView.setAdapter(transferGatherAdapter);
        transferGatherAdapter.setOnItemClickListener(this);
        transferGatherAdapter.setOnButtonClickListener(this);
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
    public void onItemClick(View view, ArrayList<GatherBean> data) {

    }

    @Override
    public void onBtnclick(View view, int position) {
        switch (view.getId()) {
            case R.id.tran_gather_ok:
                _updateORcancelResMy(position, Url.TRANSFER_GATHER_OK_URL, TRANSFER_GATHER_OK_URL, true);
                break;
            case R.id.tran_gather_cancel:
                _updateORcancelResMy(position, Url.TRANSFER_GATHER_CANCEL_URL, TRANSFER_GATHER_CANCEL_URL,true);
                break;
        }
    }

    private void _updateORcancelResMy(int position, String url, final int what, boolean b) {
        RequestParams params = new RequestParams(url);
        if (b) {
            params.addBodyParameter("transGuid", mGatherRows.get(position).getTransGuid());
        }
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "onSuccess: result=" + result);
                Message msg = new Message();
                msg.obj = result;
                msg.what = what;
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
            }
        });
    }

    /*@Override
    public void onItemClick(View view, ArrayList<ActListBean.RowsBean> data) {
        int position = mRvTranGatherRecyView.getLayoutManager().getPosition(view);
        Log.i(TAG, "当前点击了第" + position + "个item");
        Intent intent = new Intent(mActivity, ActDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("act_list_data", mActRows.get(position));
        intent.putExtras(bundle);
        startActivity(intent);
    }*/


}

