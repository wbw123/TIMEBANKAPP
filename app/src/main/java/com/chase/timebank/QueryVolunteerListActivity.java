package com.chase.timebank;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chase.timebank.adapter.ResQueryVolAdapter;
import com.chase.timebank.bean.QueryResVolunteer;
import com.chase.timebank.bean.QueryResVolunteer.VolunteerBean;
import com.chase.timebank.bean.ReqMyBean;
import com.chase.timebank.global.Url;
import com.chase.timebank.util.JsonResolveUtils;
import com.chase.timebank.util.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QueryVolunteerListActivity extends AppCompatActivity implements OnRefreshListener, OnLoadmoreListener, ResQueryVolAdapter.OnRecyclerViewItemClickListener {
    @BindView(R.id.srl_res_volunteer)
    SmartRefreshLayout mSrlResVol;
    @BindView(R.id.res_query_vol_header)
    ClassicsHeader mResQueryVolHeader;
    @BindView(R.id.res_query_vol_footer)
    ClassicsFooter mResQueryVolFooter;
    @BindView(R.id.res_query_vol_list)
    RecyclerView mResQueryVolList;
    private String mReqGuid;
    private static final String TAG = "QueryVolListActivity";
    private ArrayList<VolunteerBean> volunteerBeen;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = (String) msg.obj;
            //解析json数据
            _processJson(result);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_volunteer_list);
        ButterKnife.bind(this);
        mReqGuid = getIntent().getStringExtra("query_volunteer_reqGuid");
        initView();
        initData();
    }

    private void initData() {
        mSrlResVol.autoRefresh();
    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//设置为垂直布局，这也是默认的
        mResQueryVolList.setLayoutManager(layoutManager);// 设置布局管理器
        mResQueryVolList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //下拉刷新 上拉加载
        mSrlResVol.setOnRefreshListener(this);
        mSrlResVol.setOnLoadmoreListener(this);
    }

    @Override
    public void onRefresh(final RefreshLayout refreshlayout) {
        refreshlayout.getLayout().postDelayed(new Runnable() {
            @Override
            public void run() {
                //通过网络获取数据
                _queryResVolList();
                refreshlayout.finishRefresh();//完成刷新
//                refreshlayout.setLoadmoreFinished(false);//可以出发加载更多事件
                refreshlayout.setLoadmoreFinished(true);//不可以出发加载更多事件
            }
        }, 1500);
    }


    @Override
    public void onItemClick(View view, ArrayList<ReqMyBean.RowsBean> data) {

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

    private void _processJson(String result) {
        QueryResVolunteer queryResVolunteer = JsonResolveUtils.parseJsonToBean(result, QueryResVolunteer.class);
        Log.i(TAG, "志愿者json解析：" + queryResVolunteer.toString());
        volunteerBeen = queryResVolunteer.getRows();
        ResQueryVolAdapter resQueryVolAdapter = new ResQueryVolAdapter(this, volunteerBeen);
        mResQueryVolList.setAdapter(resQueryVolAdapter);
        resQueryVolAdapter.setOnItemClickListener(this);
    }

    private void _queryResVolList() {
        RequestParams params = new RequestParams(Url.QUERY_VOLUNTEER_URL);
        params.addBodyParameter("reqGuid", mReqGuid);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, result);
                Message msg = new Message();
                msg.obj = result;
                mHandler.sendMessage(msg);
                ToastUtils.ToastShort(getApplicationContext(), result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "onError: " + ex + "  ," + isOnCallback);

            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {
                Log.i(TAG, "onCancelled: " + cex);

            }

            @Override
            public void onFinished() {
                Log.i(TAG, "onFinished");
            }
        });
    }

}
