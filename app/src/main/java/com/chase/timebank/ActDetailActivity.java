package com.chase.timebank;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chase.timebank.bean.ActListBean.RowsBean;
import com.chase.timebank.bean.ResultModel;
import com.chase.timebank.global.Url;
import com.chase.timebank.util.DateJsonFormatUtil;
import com.chase.timebank.util.JsonResolveUtils;
import com.chase.timebank.util.ToastUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActDetailActivity extends AppCompatActivity {

    @BindView(R.id.act_detail_id)
    TextView mActDetailID;
    @BindView(R.id.act_detail_title)
    TextView mActDetailTitle;
    @BindView(R.id.act_detail_des)
    TextView mActDetailDes;
    @BindView(R.id.act_detail_com)
    TextView mActDetailCom;
    @BindView(R.id.act_detail_addr)
    TextView mActDetailAddr;
    @BindView(R.id.act_detail_community)
    TextView mActDetailCommunity;
    @BindView(R.id.act_detail_start_time)
    TextView mActDetailStartTime;
    @BindView(R.id.act_detail_end_time)
    TextView mActDetailEndTime;
    @BindView(R.id.act_detail_num)
    TextView mActDetailNum;
    @BindView(R.id.act_detail_apply)
    Button mActDetailApply;
    @BindView(R.id.act_detail_cancel)
    Button mActDetailCancel;

    private static final String TAG = "ActDetailActivity";
    private RowsBean mActData;
    private String mStartTime;
    private String mEndTime;

    //TODO：用弱引用，防止内存溢出，程序崩溃 有时间的话之前的都得改
    private static class ActDetailHandler extends Handler {
        private final WeakReference<ActDetailActivity> mActivity;

        ActDetailHandler(ActDetailActivity mActivity) {
            super();
            this.mActivity = new WeakReference<>(mActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ActDetailActivity activity = mActivity.get();
            if (activity == null)
                return;
            //主线程展示数据
            ResultModel resultModel = (ResultModel) msg.obj;
            if (resultModel.getCode() == 1) {
                ToastUtils.ToastLong(activity, resultModel.getMsg());
                activity.finish();
            } else if (resultModel.getCode() == 11) {
                ToastUtils.ToastLong(activity, resultModel.getMsg());
            } else {
                Log.i(TAG, "数据库更新失败！");
                ToastUtils.ToastLong(activity, "数据库异常，请稍后重试！");
            }
        }
    }

    ActDetailHandler actDetailHandler = new ActDetailHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acti_detail);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        mActData = (RowsBean) getIntent().getSerializableExtra("act_list_data");
        Log.i(TAG, mActData.toString());
    }

    private void initView() {
        String startTime = mActData.getActivityStartTime();
        String endTime = mActData.getActivityEndTime();
        if (startTime != null) {
            mStartTime = DateJsonFormatUtil.longToDate(Long.valueOf(startTime));
        }
        if (endTime != null) {
            mEndTime = DateJsonFormatUtil.longToDate(Long.valueOf(endTime));
        }
        mActDetailID.setText(mActData.getActivityGuid());
        mActDetailTitle.setText(mActData.getActivityTitle());
        mActDetailDes.setText(mActData.getActivityDesp());
        mActDetailCom.setText(mActData.getActivityComment());
        mActDetailAddr.setText(mActData.getActivityAddress());
        mActDetailCommunity.setText(mActData.getActivityFromCommGuid());
        mActDetailStartTime.setText(mStartTime);
        mActDetailEndTime.setText(mEndTime);
        mActDetailNum.setText(mActData.getActivityPersonNum() + " 人");
    }

    @OnClick({R.id.act_detail_apply, R.id.act_detail_cancel})
    public void clickCase(View view) {
        switch (view.getId()) {
            case R.id.act_detail_apply:
                Log.i(TAG, "apply activity clicked");
                _applyActivity(mActData.getActivityGuid());
                break;
            case R.id.act_detail_cancel:
                finish();
                break;
        }
    }

    private void _applyActivity(String activityGuid) {
        RequestParams params = new RequestParams(Url.INSERT_ACTPART_URL);
        params.addBodyParameter("activityGuid", activityGuid);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, result);
                Message msg = new Message();
                //解析json数据
                msg.obj = JsonResolveUtils.parseJsonToBean(result, ResultModel.class);
                actDetailHandler.sendMessage(msg);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "onError: " + ex + "  ," + isOnCallback);

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
}
