package com.chase.timebank;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.chase.timebank.bean.ResultModel;
import com.chase.timebank.global.Constant;
import com.chase.timebank.global.Url;
import com.chase.timebank.util.JsonResolveUtils;
import com.chase.timebank.util.ToastUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InsertRequestActivity extends AppCompatActivity {
    @BindView(R.id.tv_req_avail_start_time)
    TextView mReqAvailStartTime;
    @BindView(R.id.tv_req_avail_end_time)
    TextView mReqAvailEndTime;
    @BindView(R.id.et_req_address)
    EditText mReqAddress;
    @BindView(R.id.et_req_title)
    EditText mReqTitle;
    @BindView(R.id.et_req_desp)
    EditText mReqDesp;
    @BindView(R.id.et_req_comment)
    EditText mReqComment;
    @BindView(R.id.sp_req_class)
    Spinner mSpReqClass;
    @BindView(R.id.sp_req_urgency)
    Spinner mSpReqUrgency;
    @BindView(R.id.et_req_duration_time)
    EditText mReqDurTime;
    @BindView(R.id.et_req_num)
    EditText mReqNum;
    @BindView(R.id.btn_bd_location)
    Button mBDlocation;

    /*spinner适配*/
    private ArrayAdapter<String> mReqClassAdapter;
    private ArrayAdapter<String> mReqUrgencyAdapter;

    private SimpleDateFormat mDateFormat;
    private static final String TAG = "InsertRequestActivity";

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
            } else {
                Log.i(TAG, "request插入数据库失败！");
                ToastUtils.ToastLong(getApplicationContext(), "数据库异常，请稍后重试！");
            }
        }
    };
    private LocationClient mLocationClient;
    private MyBDLocationListener mBDLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBDloaction();
        setContentView(R.layout.activity_post_request);
        ButterKnife.bind(this);
        initDatePicker();
        initClass();
        initUrgency();
    }

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
                mReqAddress.setText(address);
                if (mLocationClient.isStarted()) {
                    // 获得位置之后停止定位
                    mLocationClient.stop();
                }
            }
        }


    }

    @OnClick({R.id.tv_req_avail_start_time, R.id.tv_req_avail_end_time, R.id.btn_req_save, R.id.btn_req_back,R.id.btn_bd_location})
    public void clickCase(View view) {
        switch (view.getId()) {
            case R.id.tv_req_avail_start_time:
                // 日期格式为yyyy-MM-dd HH:mm:ss
                TimePickerView TPview1 = new TimePickerBuilder(this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        mReqAvailStartTime.setText(getTime(date));
                    }
                }).setType(new boolean[]{true, true, true, true, true, true})
                        .build();
                TPview1.setDate(Calendar.getInstance());
                TPview1.show();
                break;
            case R.id.tv_req_avail_end_time:
                // 日期格式为yyyy-MM-dd HH:mm:ss
                TimePickerView TPview2 = new TimePickerBuilder(this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        mReqAvailEndTime.setText(getTime(date));
                    }
                }).setType(new boolean[]{true, true, true, true, true, true})
                        .build();
                TPview2.setDate(Calendar.getInstance());
                TPview2.show();
                break;
            case R.id.btn_req_save:
                _insertReq();
                break;
            case R.id.btn_req_back:
                finish();
                break;
            case R.id.btn_bd_location:
                Log.d(TAG, "定位功能被点击了");
                getLocation();
                break;
        }

    }

    private void _insertReq() {
        RequestParams params = new RequestParams(Url.INSERT_REQ_URL);
        params.addBodyParameter("reqAddress", mReqAddress.getText().toString());
        params.addBodyParameter("reqTitle", mReqTitle.getText().toString());
        params.addBodyParameter("reqDesp", mReqDesp.getText().toString());
        params.addBodyParameter("reqComment", mReqComment.getText().toString());
        params.addBodyParameter("reqTypeGuidClass", mSpReqClass.getSelectedItem().toString());
        params.addBodyParameter("reqTypeGuidUrgency", mSpReqUrgency.getSelectedItem().toString());
        params.addBodyParameter("reqAvailableStartTime", mReqAvailStartTime.getText().toString());
        params.addBodyParameter("reqAvailableEndTime", mReqAvailEndTime.getText().toString());
        params.addBodyParameter("reqRreDurationTime", mReqDurTime.getText().toString());
        params.addBodyParameter("reqPersonNum", mReqNum.getText().toString());
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
        mSpReqClass.setAdapter(mReqClassAdapter);
        //设置请求类别默认选中值
        mSpReqClass.setSelection(0);
    }

    private void initUrgency() {
        if (mReqUrgencyAdapter == null) {
            mReqUrgencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Constant.REQ_URGNECY_DATA);
        }
        mReqUrgencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpReqUrgency.setAdapter(mReqUrgencyAdapter);
        //设置请求紧急程度默认选中值
        mSpReqUrgency.setSelection(0);
    }

    private void initDatePicker() {
        mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String now = mDateFormat.format(new Date());
        mReqAvailStartTime.setText(now);
        mReqAvailEndTime.setText(now);
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        return mDateFormat.format(date);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消监听函数
        if (mLocationClient != null) {
            mLocationClient.unRegisterLocationListener(mBDLocationListener);
        }
    }
}
