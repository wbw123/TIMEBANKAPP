package com.chase.timebank;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBNRoutePlanManager;
import com.baidu.navisdk.adapter.IBNTTSManager;
import com.baidu.navisdk.adapter.IBaiduNaviManager;
import com.chase.timebank.bean.LocationBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BDNavActivity extends AppCompatActivity {
    @BindView(R.id.nav_map_view)
    MapView mNavMapView;
    @BindView(R.id.nav_my_location)
    Button mNavMyLocation;
    @BindView(R.id.nav_back)
    Button mNavBack;
    @BindView(R.id.nav_relnav)
    Button mNavRelnav;



    private BaiduMap mBaiduMap;
    //定位相关
    private LocationClient mLocationClient;
    private boolean isFirstLocation = true;
    private LatLng mStartLocationData;//起点
    private LatLng mDestLocationData;//终点
    private LocationBean mEndLocBean;
    private LocationBean mStartlocBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_nav);
        ButterKnife.bind(this);
        //获取服务订单位置数据
        Intent intent = getIntent();
        Float latitude = intent.getFloatExtra("service_my_latitude",39.920884f);
        Float longitude = intent.getFloatExtra("service_my_longitude",116.408042f);
        String locationName = intent.getStringExtra("service_my_location_name");
        mEndLocBean = new LocationBean(latitude,longitude,locationName);
        mDestLocationData = new LatLng(latitude, longitude);//终点经纬度
        System.out.println("latitude:"+latitude+"   longitude:"+longitude+"   name:"+locationName);
        //定位相关
        initLocation();
        //更新地图设置
        mBaiduMap = mNavMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18.0f);
        mBaiduMap.setMapStatus(msu);



        //定位终点
        initDestLocation();
//        initListener();
        //初始化导航相关
        if (initDirs()) {
            initNavi();
        }
    }

    private String mSDCardPath = null;
    private static final String APP_FOLDER_NAME = "BD_nav";
    private static final String[] authBaseArr = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int authBaseRequestCode = 1;
    private boolean hasInitSuccess = false;
    private BNRoutePlanNode mStartNode = null;
    static final String ROUTE_PLAN_NODE = "routePlanNode";
    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    private boolean hasBasePhoneAuth() {
        PackageManager pm = this.getPackageManager();
        for (String auth : authBaseArr) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void initNavi() {
        // 申请权限
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (!hasBasePhoneAuth()) {
                this.requestPermissions(authBaseArr, authBaseRequestCode);
                return;
            }
        }

        BaiduNaviManagerFactory.getBaiduNaviManager().init(this,
                mSDCardPath, APP_FOLDER_NAME, new IBaiduNaviManager.INaviInitListener() {

                    @Override
                    public void onAuthResult(int status, String msg) {
                        String result;
                        if (0 == status) {
                            result = "key校验成功!";
                        } else {
                            result = "key校验失败, " + msg;
                        }
                        Toast.makeText(BDNavActivity.this, result, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void initStart() {
                        Toast.makeText(BDNavActivity.this, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void initSuccess() {
                        Toast.makeText(BDNavActivity.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                        hasInitSuccess = true;
                        // 初始化tts
                        initTTS();
                    }

                    @Override
                    public void initFailed() {
                        Toast.makeText(BDNavActivity.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }
    private void initTTS() {
        // 使用内置TTS
        BaiduNaviManagerFactory.getTTSManager().initTTS(getApplicationContext(),
                getSdcardDir(), APP_FOLDER_NAME, "11423702");

        // 不使用内置TTS
//         BaiduNaviManagerFactory.getTTSManager().initTTS(mTTSCallback);

        // 注册同步内置tts状态回调
        BaiduNaviManagerFactory.getTTSManager().setOnTTSStateChangedListener(
                new IBNTTSManager.IOnTTSPlayStateChangedListener() {
                    @Override
                    public void onPlayStart() {
                        Log.e("BNSDKDemo", "ttsCallback.onPlayStart");
                    }

                    @Override
                    public void onPlayEnd(String speechId) {
                        Log.e("BNSDKDemo", "ttsCallback.onPlayEnd");
                    }

                    @Override
                    public void onPlayError(int code, String message) {
                        Log.e("BNSDKDemo", "ttsCallback.onPlayError");
                    }
                }
        );

        // 注册内置tts 异步状态消息
        BaiduNaviManagerFactory.getTTSManager().setOnTTSStateChangedHandler(
                new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        Log.e("BNSDKDemo", "ttsHandler.msg.what=" + msg.what);
                    }
                }
        );
    }
    // 外置tts时需要实现的接口回调
    private IBNTTSManager.IBNOuterTTSPlayerCallback mTTSCallback = new IBNTTSManager.IBNOuterTTSPlayerCallback() {

        @Override
        public int getTTSState() {
//            /** 播放器空闲 */
//            int PLAYER_STATE_IDLE = 1;
//            /** 播放器正在播报 */
//            int PLAYER_STATE_PLAYING = 2;
            return PLAYER_STATE_IDLE;
        }

        @Override
        public int playTTSText(String text, String s1, int i, String s2) {
            Log.e("BNSDKDemo", "playTTSText:" + text);
            return 0;
        }

        @Override
        public void stopTTS() {
            Log.e("BNSDKDemo", "stopTTS");
        }
    };
    private void routeplanToNavi(LocationBean mStartlocBean, LocationBean mEndLocBean) {
        if (!hasInitSuccess) {
            Toast.makeText(BDNavActivity.this, "还未初始化!", Toast.LENGTH_SHORT).show();
        }
        int coType = BNRoutePlanNode.CoordinateType.BD09LL;
        BNRoutePlanNode sNode = new BNRoutePlanNode(mStartlocBean.getLongitude(),
                mStartlocBean.getLatitude(), mStartlocBean.getLocationName(), null, coType);
        BNRoutePlanNode eNode = new BNRoutePlanNode(mEndLocBean.getLongitude(),
                mEndLocBean.getLatitude(), mEndLocBean.getLocationName(), null, coType);
//        switch (coType) {
//            case BNRoutePlanNode.CoordinateType.GCJ02: {
//                sNode = new BNRoutePlanNode(116.30142, 40.05087, "百度大厦", "百度大厦", coType);
//                eNode = new BNRoutePlanNode(116.39750, 39.90882, "北京天安门", "北京天安门", coType);
//                break;
//            }
//            case BNRoutePlanNode.CoordinateType.WGS84: {
//                sNode = new BNRoutePlanNode(116.300821, 40.050969, "百度大厦", "百度大厦", coType);
//                eNode = new BNRoutePlanNode(116.397491, 39.908749, "北京天安门", "北京天安门", coType);
//                break;
//            }
//            case BNRoutePlanNode.CoordinateType.BD09_MC: {
//                sNode = new BNRoutePlanNode(12947471, 4846474, "百度大厦", "百度大厦", coType);
//                eNode = new BNRoutePlanNode(12958160, 4825947, "北京天安门", "北京天安门", coType);
//                break;
//            }
//            case BNRoutePlanNode.CoordinateType.BD09LL: {
//                sNode = new BNRoutePlanNode(116.30784537597782, 40.057009624099436, "百度大厦", "百度大厦", coType);
//                eNode = new BNRoutePlanNode(116.40386525193937, 39.915160800132085, "北京天安门", "北京天安门", coType);
//                break;
//            }
//            default:
//                break;
//        }

        mStartNode = sNode;

        List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
        list.add(sNode);
        list.add(eNode);

        BaiduNaviManagerFactory.getRoutePlanManager().routeplanToNavi(
                list,
                IBNRoutePlanManager.RoutePlanPreference.ROUTE_PLAN_PREFERENCE_DEFAULT,
                null,
                new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_START:
                                Toast.makeText(BDNavActivity.this, "算路开始", Toast.LENGTH_SHORT)
                                        .show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_SUCCESS:
                                Toast.makeText(BDNavActivity.this, "算路成功", Toast.LENGTH_SHORT)
                                        .show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_FAILED:
                                Toast.makeText(BDNavActivity.this, "算路失败", Toast.LENGTH_SHORT)
                                        .show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_TO_NAVI:
                                Toast.makeText(BDNavActivity.this, "算路成功准备进入导航", Toast.LENGTH_SHORT)
                                        .show();
                                Intent intent = new Intent(BDNavActivity.this,
                                        BDGuideActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(ROUTE_PLAN_NODE, mStartNode);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                break;
                            default:
                                // nothing
                                break;
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == authBaseRequestCode) {
            for (int ret : grantResults) {
                if (ret == 0) {
                    continue;
                } else {
                    Toast.makeText(BDNavActivity.this, "缺少导航基本的权限!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            initNavi();
        }
    }






    private void initDestLocation() {
        OverlayOptions options = new MarkerOptions().position(mDestLocationData)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                .zIndex(5);
        mBaiduMap.addOverlay(options);
    }

    private void initLocation() {
        //声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        intiLocationOps();
        //注册监听函数
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                //map view销毁后不再处理新接收位置
                if (bdLocation == null || mNavMapView == null)
                    return;
                //构造定位数据
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(bdLocation.getRadius())
                        //此处设置开发者获取到的方向信息，顺时针0-360
                        .latitude(bdLocation.getLatitude())
                        .longitude(bdLocation.getLongitude()).build();
                //设置定位数据
                mBaiduMap.setMyLocationData(locData);
                //第一次定位时，将地图位置移动到当前位置
                LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                String address = bdLocation.getAddrStr();//地址名称
                mStartLocationData = ll;
                mStartlocBean = new LocationBean((float) ll.latitude, (float) ll.longitude, address);
                if (isFirstLocation) {
                    isFirstLocation = false;
                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                    mBaiduMap.animateMapStatus(u);
                }
            }
        });
    }

    private void intiLocationOps() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms
        option.setIsNeedAddress(false);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false，设置是否使用gps
        mLocationClient.setLocOption(option);
    }

    @OnClick({R.id.nav_my_location,R.id.nav_back,R.id.nav_relnav})
    public void clickCase(View view) {
        switch (view.getId()) {
            case R.id.nav_my_location:
                if (mStartLocationData != null) {
                    MapStatusUpdate u = MapStatusUpdateFactory
                            .newLatLng(mStartLocationData);
                    mBaiduMap.animateMapStatus(u);
                }
                break;
            case R.id.nav_relnav:
                if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
                    routeplanToNavi(mStartlocBean,mEndLocBean);
                }
                break;
            case R.id.nav_back:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy(),实现地图生命周期管理
        mNavMapView.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBaiduMap.setMyLocationEnabled(true);
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNavMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNavMapView.onPause();
    }
}
