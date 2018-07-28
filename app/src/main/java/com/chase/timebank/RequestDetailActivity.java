package com.chase.timebank;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.chase.timebank.bean.ReqMyBean.RowsBean;
import com.chase.timebank.bean.ResultModel;
import com.chase.timebank.global.Url;
import com.chase.timebank.util.DateJsonFormatUtil;
import com.chase.timebank.util.JsonResolveUtils;
import com.chase.timebank.util.ToastUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RequestDetailActivity extends AppCompatActivity {
    @BindView(R.id.req_detail_id)
    TextView mReqDetailID;
    @BindView(R.id.req_detail_time)
    TextView mReqDetailTime;
    @BindView(R.id.req_detail_addr)
    TextView mReqDetailAddr;
    @BindView(R.id.req_detail_title)
    TextView mReqDetailTitle;
    @BindView(R.id.req_detail_des)
    TextView mReqDetailDes;
    @BindView(R.id.req_detail_com)
    TextView mReqDetailCom;
    @BindView(R.id.req_detail_class)
    TextView mReqDetailClass;
    @BindView(R.id.req_detail_ava_start_time)
    TextView mReqDetailAcaStartTime;
    @BindView(R.id.req_detail_ava_end_time)
    TextView mReqDetailAvaEndTime;
    @BindView(R.id.req_detail_dur_time)
    TextView mReqDetailDurTime;
    @BindView(R.id.req_detail_num)
    TextView mReqDetailNum;
    @BindView(R.id.req_detail_urgency)
    TextView mReqDetailUrgency;
    @BindView(R.id.req_detail_appr_status)
    TextView mReqDetailApprStatus;
    @BindView(R.id.req_detail_pro_status)
    TextView mReqDetailProStatus;
    @BindView(R.id.btn_show_map)
    Button mBtnShowMap;

    /*我的请求 使用*/
    @BindView(R.id.ll_req_my)
    LinearLayout mLlReqMy;
    @BindView(R.id.req_detail_query_vol)
    Button mReqDetailQueryVol;
    @BindView(R.id.req_detail_cancel)
    Button mReqDetailCancel;
    @BindView(R.id.req_detail_start)
    Button mReqDetailStart;
    @BindView(R.id.req_detail_update)
    Button mReqDetailUpdate;
    @BindView(R.id.req_detail_complete)
    Button mReqDetailComplete;
    @BindView(R.id.req_detail_incomplete)
    Button mReqDetailIncomplete;
    /*服务列表 使用*/
    @BindView(R.id.ll_ser_list)
    LinearLayout mLlSerList;
    @BindView(R.id.req_apply_service)
    Button mReqApplyService;
    @BindView(R.id.req_back)
    Button mReqBack;
    /*我的服务 使用*/
    @BindView(R.id.ll_ser_my)
    LinearLayout mLlSerMy;
    @BindView(R.id.res_update)
    Button mResUpdate;
    @BindView(R.id.res_back)
    Button mResBack;
    /*服务列表和我的服务 都使用*/
    @BindView(R.id.ll_res_addr)
    LinearLayout mLlResAddr;
    @BindView(R.id.btn_res_location)
    Button mResLocation;
    @BindView(R.id.res_addr)
    EditText mResAddr;
    private static final String TAG = "RequestDetailActivity";
    private RowsBean mReqMyData;
    private String availStartTime;
    private String availEndTime;
    private String issueTime;

    private static final int CANCEL_REQ_URL = 101;
    private static final int START_REQ_URL = 102;
    private static final int COMPLETED_REQ_URL = 103;
    private static final int INCOMPLETED_REQ_URL = 104;
    private static final int INSERT_RES_URL = 105;
    private static final int UPDATE_RES_URL = 106;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CANCEL_REQ_URL:
                    handlerMethod(msg);
                    break;
                case START_REQ_URL:
                    handlerMethod(msg);
                    break;
                case COMPLETED_REQ_URL:
                    handlerMethod(msg);
                    break;
                case INCOMPLETED_REQ_URL:
                    handlerMethod(msg);
                    break;
                case INSERT_RES_URL:
                    handlerMethod(msg);
                    break;
                case UPDATE_RES_URL:
                    handlerMethod(msg);
                    break;
            }

        }
    };
    private int intExtra;
    private String resGuid;

    private void handlerMethod(Message msg) {
        String result = (String) msg.obj;
        //解析json数据
        ResultModel resultModel = JsonResolveUtils.parseJsonToBean(result, ResultModel.class);
        Log.i(TAG, "json解析：" + resultModel.toString());
        if (resultModel.getCode() == 1) {
            Log.i(TAG, resultModel.getMsg());
            ToastUtils.ToastLong(getApplicationContext(), resultModel.getMsg());
            finish();
        } else {
            Log.i(TAG, "数据库更新失败！");
            ToastUtils.ToastLong(getApplicationContext(), "数据库异常，请稍后重试！");
        }
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBDloaction();
        setContentView(R.layout.activity_request_detail);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        mReqMyData = (RowsBean) getIntent().getSerializableExtra("req_my_data");
        intExtra = getIntent().getIntExtra("show_servicelist_or_requestmy", 0);
        String resAddr = getIntent().getStringExtra("res_addr");
        resGuid = getIntent().getStringExtra("res_guid");
        Log.i(TAG, "=======================" + resAddr);
        if (intExtra == 1) {
            mLlSerList.setVisibility(View.VISIBLE);
            mLlResAddr.setVisibility(View.VISIBLE);
            mLlReqMy.setVisibility(View.GONE);
        } else if (intExtra == 3) {
            mLlSerMy.setVisibility(View.VISIBLE);
            mLlResAddr.setVisibility(View.VISIBLE);
            mResAddr.setText(resAddr);//已经写过一次地址了，自然要写上
            mLlReqMy.setVisibility(View.GONE);
        }
    }

    private void initView() {
        String startTime = mReqMyData.getReqAvailableStartTime();
        String endTime = mReqMyData.getReqAvailableEndTime();
        String issTime = mReqMyData.getReqIssueTime();
        if (startTime != null) {
            availStartTime = DateJsonFormatUtil.longToDate(Long.valueOf(startTime));
        }
        if (endTime != null) {
            availEndTime = DateJsonFormatUtil.longToDate(Long.valueOf(endTime));
        }
        if (issTime != null) {
            issueTime = DateJsonFormatUtil.longToDate(Long.valueOf(issTime));
        }
        mReqDetailID.setText(mReqMyData.getReqGuid());
        mReqDetailTime.setText(issueTime);
        mReqDetailAddr.setText(mReqMyData.getReqAddress());
        mReqDetailTitle.setText(mReqMyData.getReqTitle());
        mReqDetailDes.setText(mReqMyData.getReqDesp());
        mReqDetailCom.setText(mReqMyData.getReqComment());
        mReqDetailClass.setText(mReqMyData.getReqTypeGuidClass());
        mReqDetailAcaStartTime.setText(availStartTime);
        mReqDetailAvaEndTime.setText(availEndTime);
        mReqDetailDurTime.setText(mReqMyData.getReqRreDurationTime() + " 分钟");
        mReqDetailNum.setText(mReqMyData.getReqPersonNum() + " 人");
        mReqDetailUrgency.setText(mReqMyData.getReqTypeGuidUrgency());
        mReqDetailApprStatus.setText(mReqMyData.getReqTypeApproveStatus());
        mReqDetailProStatus.setText(mReqMyData.getReqTypeGuidProcessStatus());
    }

    @OnClick({R.id.req_detail_update, R.id.req_detail_cancel, R.id.req_detail_start, R.id.req_detail_query_vol
            , R.id.req_detail_complete, R.id.req_detail_incomplete, R.id.req_apply_service, R.id.req_back
            , R.id.res_update, R.id.res_back,R.id.btn_show_map,R.id.btn_res_location})
    public void clickCase(View view) {
        switch (view.getId()) {
            case R.id.req_detail_update:
                Log.i(TAG, "update clicked");
                Log.i(TAG, "待审核".equals(mReqDetailApprStatus.getText()) + "");
                if ("待审核".equals(mReqDetailApprStatus.getText())) {
                    _updateReqDetail();
                } else {
                    ToastUtils.ToastLong(this, "不能更新审核状态为" + mReqMyData.getReqTypeApproveStatus() + "的请求");
                }
                break;
            case R.id.req_detail_cancel:
                Log.i(TAG, "cancel clicked");
                _updateReqProcess(Url.CANCEL_REQ_URL, CANCEL_REQ_URL);
                break;
            case R.id.req_detail_start:
                _updateReqProcess(Url.START_REQ_URL, START_REQ_URL);
                break;
            case R.id.req_detail_query_vol:
                _queryVolunteer();
                break;
            case R.id.req_detail_complete:
                _updateReqProcess(Url.COMPLETED_REQ_URL, COMPLETED_REQ_URL);
                break;
            case R.id.req_detail_incomplete:
                _updateReqProcess(Url.INCOMPLETED_REQ_URL, INCOMPLETED_REQ_URL);
                break;
            //服务列表
            case R.id.req_apply_service:
                _updateReqProcess(Url.INSERT_RES_URL, INSERT_RES_URL);
                break;
            case R.id.req_back:
                finish();
                break;
            //我的服务
            case R.id.res_update:
                _updateReqProcess(Url.UPDATE_RES_URL, UPDATE_RES_URL);
                break;
            case R.id.res_back:
                finish();
                break;
            //服务列表 我的服务
            case R.id.btn_res_location:
                getLocation();
                break;
            //服务列表 我的请求 我的服务
            case R.id.btn_show_map:
                Log.i(TAG, "跳到百度地图页面");
                startActivity(new Intent(this,BDMapActivity.class));
                break;
        }
    }



    private void _updateReqProcess(String url, final int what) {
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("reqGuid", mReqMyData.getReqGuid());
        if (intExtra == 1 || intExtra == 3) {
            params.addBodyParameter("resAcceptAddress", mResAddr.getText().toString());
        }
        if (intExtra == 3) {
            params.addBodyParameter("resGuid", resGuid);
        }
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, result);
                Message msg = new Message();
                msg.obj = result;
                msg.what = what;
                mHandler.sendMessage(msg);
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

    private void _queryVolunteer() {
        Intent intent = new Intent(this, QueryVolunteerListActivity.class);
        intent.putExtra("query_volunteer_reqGuid", mReqMyData.getReqGuid());
        startActivity(intent);
        finish();
    }


    private void _updateReqDetail() {
        Intent intent = new Intent(this, UpdateReqMyActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("update_req_my_data", mReqMyData);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    /*百度地图自动定位*/
    private LocationClient mLocationClient;
    private MyBDLocationListener mBDLocationListener;
        private void initBDloaction() {
        mLocationClient = new LocationClient(this);
        mBDLocationListener = new MyBDLocationListener();
        mLocationClient.registerLocationListener(mBDLocationListener);
    }

    private void requestLocation() {
        // 声明定位参数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式 高精度
        option.setCoorType("bd09ll");// 设置返回定位结果是百度经纬度 默认gcj02
        option.setScanSpan(5000);// 设置发起定位请求的时间间隔 单位ms 每5s更新一次位置信息
        option.setIsNeedAddress(true);// 设置定位结果包含地址信息
        option.setNeedDeviceDirect(true);// 设置定位结果包含手机机头 的方向
        // 设置定位参数
        mLocationClient.setLocOption(option);
        //启动定位
        mLocationClient.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            ToastUtils.ToastShort(this, "必须同意所有权限才能定位");
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    ToastUtils.ToastShort(this, "发生未知错误");
                }
                break;
            default:
        }
    }

    public void getLocation() {
        ArrayList<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, 1);
        } else {
            requestLocation();
        }
    }

    private class MyBDLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            // 非空判断
            if (bdLocation != null) {
                // 根据BDLocation 对象获得经纬度以及详细地址信息
                double latitude = bdLocation.getLatitude();
                double longitude = bdLocation.getLongitude();
                String address = bdLocation.getAddrStr();
                Log.i(TAG, "address:" + address + " latitude:" + latitude
                        + " longitude:" + longitude);
                mResAddr.setText(address);
                if (mLocationClient.isStarted()) {
                    // 获得位置之后停止定位
                    mLocationClient.stop();
                }
            }
        }


    }
}
