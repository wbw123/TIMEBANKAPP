package com.chase.timebank.fragment.detail;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chase.timebank.R;
import com.chase.timebank.adapter.tabAdapter.TransferRemitAdapter;
import com.chase.timebank.bean.ActListBean;
import com.chase.timebank.bean.TransferGatherBean;
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
public class TransferRemitFragment extends BaseFragment implements OnRefreshListener, OnLoadmoreListener, TransferRemitAdapter.OnRecyclerViewItemClickListener, TransferRemitAdapter.ButtonInterface {
    private SmartRefreshLayout mSrlTranRemit;
    private RecyclerView mRvTranRemitRecyView;
    private ArrayList<ActListBean.RowsBean> mActRows;

    private static final String TAG = "TransferRemitFragment";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = (String) msg.obj;
            _processJson(result);
        }
    };


    private ArrayList<TransferGatherBean.GatherBean> mGatherRows;


    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_transfer_remit, null);
        mSrlTranRemit = view.findViewById(R.id.srl_tran_remit);
        mRvTranRemitRecyView = view.findViewById(R.id.rv_tran_remit);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//设置为垂直布局，这也是默认的
        mRvTranRemitRecyView.setLayoutManager(layoutManager);// 设置布局管理器
        mRvTranRemitRecyView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
        //下拉刷新 上拉加载
        mSrlTranRemit.setOnRefreshListener(this);
        mSrlTranRemit.setOnLoadmoreListener(this);

        return view;
    }

    @Override
    public void initData() {
        //触发自动刷新
        mSrlTranRemit.autoRefresh();
    }

    @Override
    public void onRefresh(final RefreshLayout refreshlayout) {
        refreshlayout.getLayout().postDelayed(new Runnable() {
            @Override
            public void run() {
                //通过网络获取数据
                _remitTranData();
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
        TransferRemitAdapter transferRemitAdapter = new TransferRemitAdapter(mActivity, mGatherRows);
        mRvTranRemitRecyView.setAdapter(transferRemitAdapter);
        transferRemitAdapter.setOnItemClickListener(this);
        transferRemitAdapter.setOnButtonClickListener(this);
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


}


