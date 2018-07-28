package com.chase.timebank;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.chase.timebank.bean.ReqMyBean.RowsBean;
import com.chase.timebank.bean.ResultModel;
import com.chase.timebank.global.Constant;
import com.chase.timebank.global.Url;
import com.chase.timebank.util.DateJsonFormatUtil;
import com.chase.timebank.util.JsonResolveUtils;
import com.chase.timebank.util.ToastUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdateReqMyActivity extends AppCompatActivity {
    @BindView(R.id.update_req_avail_start_time)
    TextView mUpdateReqAvailStartTime;
    @BindView(R.id.update_req_avail_end_time)
    TextView mUpdateReqAvailEndTime;
    @BindView(R.id.update_req_address)
    EditText mUpdateReqAddress;
    @BindView(R.id.update_req_title)
    EditText mUpdateReqTitle;
    @BindView(R.id.update_req_desp)
    EditText mUpdateReqDesp;
    @BindView(R.id.update_req_comment)
    EditText mUpdateReqComment;
    @BindView(R.id.update_req_class)
    Spinner mUpdateReqClass;
    @BindView(R.id.update_req_urgency)
    Spinner mUpdateReqUrgency;
    @BindView(R.id.update_req_duration_time)
    EditText mUpdateReqDurTime;
    @BindView(R.id.update_req_num)
    EditText mUpdateReqNum;
    private static final String TAG = "UpdateReqMyActivity";
    private RowsBean mUpdateReqMyData;
    private String availStartTime;
    private String availEndTime;
    private String issueTime;
    /*spinner适配*/
    private ArrayAdapter<String> mReqClassAdapter;
    private ArrayAdapter<String> mReqUrgencyAdapter;

    private SimpleDateFormat mDateFormat;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = (String) msg.obj;
            //解析json数据
            ResultModel resultModel = JsonResolveUtils.parseJsonToBean(result, ResultModel.class);
            Log.i(TAG, "json解析：" + resultModel.toString());
            if (resultModel.getCode() == 1) {
                Log.i(TAG, resultModel.getMsg());
                finish();
            }else {
                Log.i(TAG, "request更新数据库失败！");
                ToastUtils.ToastLong(getApplicationContext(),"数据库异常，请稍后重试！");
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_req_my);
        ButterKnife.bind(this);
        initData();
        initClass();
        initUrgency();
        initView();
    }

    private void initData() {
        mUpdateReqMyData = (RowsBean) getIntent().getSerializableExtra("update_req_my_data");
        Log.i(TAG, mUpdateReqMyData.toString());
    }

    private void initView() {
        String startTime = mUpdateReqMyData.getReqAvailableStartTime();
        String endTime = mUpdateReqMyData.getReqAvailableEndTime();
        String issTime = mUpdateReqMyData.getReqIssueTime();
        if (startTime != null) {
            availStartTime = DateJsonFormatUtil.longToDate(Long.valueOf(startTime));
        }
        if (endTime != null) {
            availEndTime = DateJsonFormatUtil.longToDate(Long.valueOf(endTime));
        }
        if (issTime != null) {
            issueTime = DateJsonFormatUtil.longToDate(Long.valueOf(issTime));
        }
        mUpdateReqAddress.setText(mUpdateReqMyData.getReqAddress());
        mUpdateReqTitle.setText(mUpdateReqMyData.getReqTitle());
        mUpdateReqDesp.setText(mUpdateReqMyData.getReqDesp());
        mUpdateReqComment.setText(mUpdateReqMyData.getReqComment());

        mUpdateReqAvailStartTime.setText(availStartTime);
        mUpdateReqAvailEndTime.setText(availEndTime);

        mUpdateReqDurTime.setText(mUpdateReqMyData.getReqRreDurationTime());
        mUpdateReqNum.setText(mUpdateReqMyData.getReqPersonNum());
    }

    @OnClick({R.id.update_req_avail_start_time, R.id.update_req_avail_end_time, R.id.update_req_my, R.id.back_req_my})
    public void clickCase(View view) {
        switch (view.getId()) {
            case R.id.update_req_avail_start_time:
                // 日期格式为yyyy-MM-dd HH:mm:ss
                TimePickerView TPview1 = new TimePickerBuilder(this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        mUpdateReqAvailStartTime.setText(getTime(date));
                    }
                }).setType(new boolean[]{true, true, true, true, true, true})
                        .build();
                TPview1.setDate(Calendar.getInstance());
                TPview1.show();
                break;
            case R.id.update_req_avail_end_time:
                // 日期格式为yyyy-MM-dd HH:mm:ss
                TimePickerView TPview2 = new TimePickerBuilder(this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        mUpdateReqAvailEndTime.setText(getTime(date));
                    }
                }).setType(new boolean[]{true, true, true, true, true, true})
                        .build();
                TPview2.setDate(Calendar.getInstance());
                TPview2.show();
                break;
            case R.id.update_req_my:
                _updateReq();
                break;
            case R.id.back_req_my:
                finish();
                break;
        }

    }

    private void _updateReq() {
        RequestParams params = new RequestParams(Url.UPDATE_REQ_URL);
        params.addBodyParameter("reqGuid", mUpdateReqMyData.getReqGuid());
        params.addBodyParameter("reqAddress", mUpdateReqAddress.getText().toString());
        params.addBodyParameter("reqTitle", mUpdateReqTitle.getText().toString());
        params.addBodyParameter("reqDesp", mUpdateReqDesp.getText().toString());
        params.addBodyParameter("reqComment", mUpdateReqComment.getText().toString());
        params.addBodyParameter("reqTypeGuidClass", mUpdateReqClass.getSelectedItem().toString());
        params.addBodyParameter("reqTypeGuidUrgency", mUpdateReqUrgency.getSelectedItem().toString());
        params.addBodyParameter("reqAvailableStartTime", mUpdateReqAvailStartTime.getText().toString());
        params.addBodyParameter("reqAvailableEndTime", mUpdateReqAvailEndTime.getText().toString());
        params.addBodyParameter("reqRreDurationTime", mUpdateReqDurTime.getText().toString());
        params.addBodyParameter("reqPersonNum", mUpdateReqNum.getText().toString());
        Log.i(TAG, "-------------------------");
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
            public void onCancelled(CancelledException cex) {
                Log.i(TAG, "onCancelled: " + cex);

            }

            @Override
            public void onFinished() {
                Log.i(TAG, "onFinished");
            }
        });
    }

    private void initClass() {
        if (mReqClassAdapter == null) {
            mReqClassAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Constant.REQ_CLASS_DATA);
        }
        mReqClassAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mUpdateReqClass.setAdapter(mReqClassAdapter);
        //设置请求类别
        for (int i = 0; i < Constant.REQ_CLASS_DATA.length; i++) {
            if (mUpdateReqMyData.getReqTypeGuidClass().equals(Constant.REQ_CLASS_DATA[i])) {
                mUpdateReqClass.setSelection(i);
            }
        }
    }

    private void initUrgency() {
        if (mReqUrgencyAdapter == null) {
            mReqUrgencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Constant.REQ_URGNECY_DATA);
        }
        mReqUrgencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mUpdateReqUrgency.setAdapter(mReqUrgencyAdapter);
        //设置请求紧急程度
        for (int i = 0; i < Constant.REQ_URGNECY_DATA.length; i++) {
            if (mUpdateReqMyData.getReqTypeGuidProcessStatus().equals(Constant.REQ_URGNECY_DATA[i])) {
                mUpdateReqUrgency.setSelection(i);
            }
        }
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        return mDateFormat.format(date);
    }
}
