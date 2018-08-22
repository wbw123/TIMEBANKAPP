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

import com.chase.timebank.BDNavActivity;
import com.chase.timebank.R;
import com.chase.timebank.RequestDetailActivity;
import com.chase.timebank.adapter.ServiceMyAdapter;
import com.chase.timebank.bean.QueryResVolunteer;
import com.chase.timebank.bean.ReqMyBean.RowsBean;
import com.chase.timebank.bean.ResultModel;
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
public class ServiceMyFragment extends BaseFragment implements OnRefreshListener, OnLoadmoreListener, ServiceMyAdapter.OnRecyclerViewItemClickListener, ServiceMyAdapter.ButtonInterface {
    private SmartRefreshLayout mSrlSerMy;
    private RecyclerView mRvSerMyRecyView;

    private static final String TAG = "ServiceMyFragment";
    private static final int QUERY_SERVICE_MY_URL = 301;
    private static final int CANCEL_RES_MY_URL = 302;
    private static final int UPDATE_RES_MY_URL = 303;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case QUERY_SERVICE_MY_URL:
                    String result = (String) msg.obj;
                    //解析json数据
                    _processJson(result);
                    break;
                case CANCEL_RES_MY_URL:
                    String result1 = (String) msg.obj;
                    //解析json数据
                    ResultModel resultModel = JsonResolveUtils.parseJsonToBean(result1, ResultModel.class);
                    Log.i(TAG, "json解析：" + resultModel.toString());
                    if (resultModel.getCode() == 1) {
                        Log.i(TAG, resultModel.getMsg());
                        ToastUtils.ToastLong(getContext(), resultModel.getMsg());
                        mSrlSerMy.autoRefresh();//刷新
                    } else {
                        Log.i(TAG, "数据库更新失败！");
                        ToastUtils.ToastLong(getContext(), "数据库异常，请稍后重试！");
                    }
                    break;
                case UPDATE_RES_MY_URL:
                    String result2 = (String) msg.obj;
                    RowsBean rowsBean = JsonResolveUtils.parseJsonToBean(result2, RowsBean.class);
                    Log.i(TAG, rowsBean.toString());
                    Intent intent = new Intent(mActivity, RequestDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("req_my_data", rowsBean);
                    intent.putExtra("show_servicelist_or_requestmy", 3);//按钮的显示隐藏
                    intent.putExtra("res_addr", mResAcceptAddress);
                    intent.putExtra("res_guid", mResGuid);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
            }

        }
    };
    private ArrayList<QueryResVolunteer.VolunteerBean> mRows;
    private String mResAcceptAddress;
    private String mResGuid;

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_service_my, null);
        mSrlSerMy = view.findViewById(R.id.srl_ser_my);
        mRvSerMyRecyView = view.findViewById(R.id.rv_ser_my);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//设置为垂直布局，这也是默认的
        mRvSerMyRecyView.setLayoutManager(layoutManager);// 设置布局管理器
        mRvSerMyRecyView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
        //下拉刷新 上拉加载
        mSrlSerMy.setOnRefreshListener(this);
        mSrlSerMy.setOnLoadmoreListener(this);

        return view;
    }

    @Override
    public void initData() {
        super.initData();
        //触发自动刷新
        mSrlSerMy.autoRefresh();
    }

    @Override
    public void onRefresh(final RefreshLayout refreshlayout) {
        refreshlayout.getLayout().postDelayed(new Runnable() {
            @Override
            public void run() {
                //通过网络获取数据
                _queryMySer();
                refreshlayout.finishRefresh();//完成刷新
//                refreshlayout.setLoadmoreFinished(false);//可以出发加载更多事件
                refreshlayout.setLoadmoreFinished(true);//不可以出发加载更多事件
            }
        }, 1500);
    }

    private void _processJson(String result) {
        QueryResVolunteer serMyBean = JsonResolveUtils.parseJsonToBean(result, QueryResVolunteer.class);
        Log.i(TAG, "json解析：" + serMyBean.toString());
        mRows = serMyBean.getRows();
        ServiceMyAdapter serMyAdapter = new ServiceMyAdapter(mActivity, mRows);
        mRvSerMyRecyView.setAdapter(serMyAdapter);
        serMyAdapter.setOnItemClickListener(this);
        serMyAdapter.setOnButtonClickListener(this);
    }

    private void _queryMySer() {
        RequestParams params = new RequestParams(Url.QUERY_SERVICE_MY_URL);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "onSuccess: result=" + result);
                Message msg = new Message();
                msg.obj = result;
                msg.what = QUERY_SERVICE_MY_URL;
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
    public void onItemClick(View view, ArrayList<QueryResVolunteer.VolunteerBean> data) {
        int position = mRvSerMyRecyView.getLayoutManager().getPosition(view);
        Log.i(TAG, "当前点击了第" + position + "个item");
        ToastUtils.ToastShort(getContext(), "item click");
//        Intent intent = new Intent(mActivity, RequestDetailActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("req_my_data", mRows.get(position));
//        intent.putExtras(bundle);
//        startActivity(intent);

    }

    @Override
    public void onBtnclick(View view, int position) {
        switch (view.getId()) {
            case R.id.res_my_update:
                mResAcceptAddress = mRows.get(position).getResAcceptAddress();
                mResGuid = mRows.get(position).getResGuid();
                _updateORcancelResMy(position, Url.UPDATE_RES_MY_URL, UPDATE_RES_MY_URL);
                break;
            case R.id.res_my_cancel:
                _updateORcancelResMy(position, Url.CANCEL_RES_MY_URL, CANCEL_RES_MY_URL);
                break;
            case R.id.res_my_nav:
                Intent intent = new Intent(mActivity, BDNavActivity.class);
                //TODO:这里intent传递的是服务订单位置数据（纬度，经度，名称），先用模拟数据代替
//                "36.022914146409775,120.12915616792814,黄岛区辛安派出所"
                String resReqAddr = mRows.get(position).getResReqAddr();
                String[] splits = resReqAddr.split(",");
                intent.putExtra("service_my_latitude", Float.valueOf(splits[0]));
                intent.putExtra("service_my_longitude", Float.valueOf(splits[1]));
                intent.putExtra("service_my_location_name", splits[2]);
                startActivity(intent);
                break;
        }
    }

    private void _updateORcancelResMy(int position, String url, final int what) {
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("resGuid", mRows.get(position).getResGuid());
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
}


