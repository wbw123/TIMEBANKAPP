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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiAddrInfo;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
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
import java.util.List;
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
    AutoCompleteTextView mReqAddress;
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
    @BindView(R.id.btn_check)
    Button mBtnCheck;

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
    private String mAddress;
    //检索地址返回的经纬度
    private double longitude;
    private double latitude;

    private String[] arrAddr = new String[3];//存储经纬度地址

    private boolean isChecked = false;//发布订单时，查看是否进行了地址检查的指针

    private boolean isFirstCheck = true;//用来判断是否为第一次检查
    private String mCurrentAddr;//当前地址
    private String mCurrentCity;//当前城市

    /*绑定AutoCompleteTextView的adapter进行地址检索*/
    private PoiSearch mPoiSearch;
    private SuggestionSearch mSuggestionSearch;
    private ArrayAdapter<String> mSugAdapter;
    private double mCurrentLat;
    private double mCurrentLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBDloaction();
        getLocation();
        setContentView(R.layout.activity_post_request);
        ButterKnife.bind(this);
//        initPoiSearch();//初始化poi搜索模块
        initDatePicker();
        initClass();
        initUrgency();
    }

    /*PoiSearch好像没啥用*/
    private void initPoiSearch() {
        mPoiSearch = PoiSearch.newInstance();
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSugAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line);
        mReqAddress.setAdapter(mSugAdapter);
        mPoiSearch.searchInCity(new PoiCitySearchOption()
                .city(mCurrentCity)//定位的城市
                .keyword(mReqAddress.getText().toString())
                .isReturnAddr(true)
                .pageCapacity(8)
                .pageNum(1));
        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult result) {
                if (result == null
                        || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                    ToastUtils.ToastShort(InsertRequestActivity.this, "未找到结果");
                    return;
                }
                if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                    //成功在传入的搜索city中搜索到POI
                    //对result进行一些应用
                    //一般都是添加到地图中，然后绑定一些点击事件
                    //官方Demo的处理如下：
//                    mBaiduMap.clear();
//                    PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
//                    mBaiduMap.setOnMarkerClickListener(overlay);
//                    //MyPoiOverlayextends PoiOverlay;PoiOverlay extends OverlayManager
//                    //看了这三个class之间的关系后瞬间明白咱自己也可以写overlay，重写OverlayManager中的一些方法就可以了
//                    //比如重写了点击事件，这个方法真的太好，对不同类型的图层可能有不同的点击事件，百度地图3.4.0之后就支持设置多个监听对象了，只是本人还没把这个方法彻底掌握...
//                    overlay.setData(result);//图层数据
//                    overlay.addToMap();//添加到地图中(添加的都是marker)
//                    overlay.zoomToSpan();//保证能显示所有marker
                    List<PoiAddrInfo> allAddr = result.getAllAddr();
                    String name = allAddr.get(0).name;
                    Log.i(TAG, "poiSearch:" + name);
                    return;
                }
                if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

                    // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
//                    String strInfo = "在";
//                    for (CityInfo cityInfo : result.getSuggestCityList()) {
//                        strInfo += cityInfo.city;
//                        strInfo += ",";
//                    }
//                    strInfo += "找到结果";
//                    Toast.makeText(PoiSearchDemo.this, strInfo, Toast.LENGTH_LONG)
//                            .show();
                }

            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        });
        mSuggestionSearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {
            @Override
            public void onGetSuggestionResult(SuggestionResult res) {
                if (res == null || res.getAllSuggestions() == null) {
                    return;
                }

                mSugAdapter.clear();
                for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
                    if (info.key != null) {
                        mSugAdapter.add(info.key);
                    }
                }
                mSugAdapter.notifyDataSetChanged();

            }
        });
        mReqAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence cs, int start, int count, int after) {
                if (cs.length() <= 0) {
                    return;
                }
                /**
                 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                 */
                mSuggestionSearch
                        .requestSuggestion((new SuggestionSearchOption())
                                .keyword(cs.toString()).city(mCurrentCity));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
                mCurrentLat = bdLocation.getLatitude();
                mCurrentLon = bdLocation.getLongitude();
                mCurrentAddr = bdLocation.getAddrStr();
                mCurrentCity = bdLocation.getCity();
                initPoiSearch();//初始化poi搜索模块
                Log.i(TAG, "address:" + mCurrentAddr + " latitude:" + mCurrentLat
                        + " longitude:" + mCurrentLon);
                if (mLocationClient.isStarted()) {
                    // 获得位置之后停止定位
                    mLocationClient.stop();
                }
            }
        }


    }

    @OnClick({R.id.tv_req_avail_start_time, R.id.tv_req_avail_end_time, R.id.btn_req_save, R.id.btn_req_back
            , R.id.btn_bd_location, R.id.btn_check})
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
                mAddress = arrAddr[0] + "," + arrAddr[1] + "," + arrAddr[2];
                if (isChecked) {
                    _insertReq();
                } else {
                    ToastUtils.ToastShort(this, "请点击‘跳转到百度地图页面，确认地址准确性’");
                }
                break;
            case R.id.btn_req_back:
                finish();
                break;
            case R.id.btn_bd_location:
                Log.d(TAG, "自动获取当前位置信息");
//                getLocationPermission();
                mReqAddress.setText(mCurrentAddr);
                break;
            case R.id.btn_check:
                isChecked = true;
                String addr = mReqAddress.getText().toString();

                GeoCoder mSearch = GeoCoder.newInstance();
                mSearch.setOnGetGeoCodeResultListener(listener);
                mSearch.geocode(new GeoCodeOption()
                        .city(mCurrentCity)
                        .address(addr));
                break;
        }

    }

    OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {

        public void onGetGeoCodeResult(GeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有检索到结果,使用当前位置经纬度
                latitude = mCurrentLat;
                longitude = mCurrentLon;
            }
            if (result.getLocation() != null && isFirstCheck) {
                //获取地理编码结果
                latitude = result.getLocation().latitude;
                longitude = result.getLocation().longitude;
            }

            //将经纬度地址保存到数组里
            arrAddr[0] = String.valueOf(latitude);
            arrAddr[1] = String.valueOf(longitude);
            String s = mReqAddress.getText().toString();
            if (s.replaceAll(" ", "").equals("")) {//当用户没有输入地址时，填写当前位置地址信息
                mReqAddress.setText(mCurrentAddr);
            }
            arrAddr[2] = mReqAddress.getText().toString();

            ToastUtils.ToastShort(getApplicationContext(), "latitude:" + latitude + ";longitude" + longitude);
            Intent intent = new Intent(InsertRequestActivity.this, ReqMapActivity.class);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            intent.putExtra("req_addr", mReqAddress.getText().toString());
            startActivityForResult(intent, 1);

//            mAddress = latitude + "," + longitude + "," + mReqAddress.getText().toString();

        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有找到检索结果
            }
            //获取反向地理编码结果
            ToastUtils.ToastShort(getApplicationContext(), result.toString());
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            float back_latitude = data.getFloatExtra("back_latitude", 0);
            float back_longitude = data.getFloatExtra("back_longitude", 0);
            String back_req_addr = data.getStringExtra("back_req_addr");
            isFirstCheck = data.getBooleanExtra("is_first_check", false);

            //检索地址返回的经纬度赋值
            latitude = back_latitude;
            longitude = back_longitude;

            mReqAddress.setText(back_req_addr);

            //将经纬度地址保存到数组里
            arrAddr[0] = String.valueOf(latitude);
            arrAddr[1] = String.valueOf(longitude);
            arrAddr[2] = back_req_addr;
        }
    }

    private void _insertReq() {
        RequestParams params = new RequestParams(Url.INSERT_REQ_URL);
//        params.addBodyParameter("reqAddress", mReqAddress.getText().toString());
        params.addBodyParameter("reqAddress", mAddress);
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
