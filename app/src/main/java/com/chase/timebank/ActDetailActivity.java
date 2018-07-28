package com.chase.timebank;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chase.timebank.bean.ActListBean.RowsBean;
import com.chase.timebank.util.DateJsonFormatUtil;

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

    private static final String TAG = "RequestDetailActivity";
    private RowsBean mActData;
    private String mStartTime;
    private String mEndTime;
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
        mActDetailNum.setText(mActData.getActivityPersonNum()+" 人");
    }
    @OnClick({R.id.act_detail_apply, R.id.act_detail_cancel})
    public void clickCase(View view) {
        switch (view.getId()) {
            case R.id.act_detail_apply:
                Log.i(TAG, "apply activity clicked");

                break;
            case R.id.act_detail_cancel:
                finish();
                break;
        }
    }
}
