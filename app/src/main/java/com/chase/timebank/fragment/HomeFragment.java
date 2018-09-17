package com.chase.timebank.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.chase.timebank.R;
import com.chase.timebank.RequestDetailActivity;
import com.chase.timebank.bean.ReqMyBean;
import com.chase.timebank.global.Url;
import com.chase.timebank.util.JsonResolveUtils;
import com.chase.timebank.util.ToastUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

/**
 * 创建者     Vincent
 * 创建时间   2016/7/8 23:49
 * 描述	     综合模块
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class HomeFragment extends BaseFragment {

    private static final String TAG = "HomeFragment";
    private MapView mMapView;
    private LocationClient mLocationClient;
    private MyBDLocationListener mBDLocationListener;
    private BaiduMap mBaiduMap;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = (String) msg.obj;
            //解析json数据
            _processJson(result);
        }
    };
    private ArrayList<ReqMyBean.RowsBean> mRows;
    private BitmapDescriptor bitmap;
    private double mLatitude;
    private double mLongitude;

    @Override
    public View initView() {
        SDKInitializer.initialize(mActivity.getApplicationContext());
        // 获取LocationClient
        mLocationClient = new LocationClient(mActivity);
//        getLocationPermission();
        //发送定位请求
        _requestLocation();
        View view = View.inflate(mActivity, R.layout.fragment_home, null);
        mMapView = view.findViewById(R.id.bd_map_view);
        return view;
    }


    @Override
    public void initData() {


        mBDLocationListener = new MyBDLocationListener();
        mLocationClient.registerLocationListener(mBDLocationListener);

        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        mLocationClient.setLocOption(option);
        // 获取BaiduMap
        mBaiduMap = mMapView.getMap();


        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int id = marker.getExtraInfo().getInt("id");
                Intent intent = new Intent(mActivity, RequestDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("req_my_data", mRows.get(id));
                intent.putExtra("show_servicelist_or_requestmy", 1);
                intent.putExtras(bundle);
                startActivity(intent);
                ToastUtils.ToastShort(mActivity, String.valueOf(id));
                return false;
            }
        });

//        _queryNearbyReq();


        //文字，在地图中也是一种覆盖物，开发者可利用相关的接口，快速实现在地图上书写文字的需求。实现方式如下：
        //定义文字所显示的坐标点
        /*LatLng llText = new LatLng(latx, laty);
        //构建文字Option对象，用于在地图上添加文字
        OverlayOptions textOption = new TextOptions()
                .bgColor(0xAAFFFF00)
                .fontSize(28)
                .fontColor(0xFFFF00FF)
                .text(lableName)
                .rotate(0)
                .position(llText);
        //在地图上添加该文字对象并显示
        mBaiduMap.addOverlay(textOption);*/

    }

    private void _processJson(String result) {
        ReqMyBean reqMyBean = JsonResolveUtils.parseJsonToBean(result, ReqMyBean.class);
        Log.i(TAG, "json解析：" + reqMyBean.toString());
        mRows = reqMyBean.getRows();
        ToastUtils.ToastShort(mActivity, mRows.toString());
        if (mRows != null) {
            for (int i = 0; i < mRows.size(); i++) {
                String[] splits = mRows.get(i).getReqAddress().split(",");
                float latx = Float.valueOf(splits[0]);
                float laty = Float.valueOf(splits[1]);
                //定义Maker坐标点
                LatLng point = new LatLng(latx, laty);
                //构建MarkerOption，用于在地图上添加Marker
                OverlayOptions options = new MarkerOptions()
                        .position(point)
                        .zIndex(i)
                        .icon(bitmap);
                //在地图上添加Marker，并显示
                Overlay overlay = mBaiduMap.addOverlay(options);
                Bundle bundle = new Bundle();
                bundle.putInt("id", i);
                overlay.setExtraInfo(bundle);
            }
        }

    }

    private void _queryNearbyReq(double latitude, double longitude, String address) {
        RequestParams params = new RequestParams(Url.QUERY_NEARBY_REQ);
        params.addBodyParameter("userCurrentaddr", latitude + "," + longitude + "," + address);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Message msg = new Message();
                msg.obj = result;
                mHandler.sendMessage(msg);
//                ToastUtils.ToastShort(mActivity, result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });
    }


    //定位请求
    private void _requestLocation() {
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

    //定位监听
    private class MyBDLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            // 非空判断
            if (bdLocation != null) {
                // 根据BDLocation 对象获得经纬度以及详细地址信息
                mLatitude = bdLocation.getLatitude();
                mLongitude = bdLocation.getLongitude();
                String address = bdLocation.getAddrStr();
                //获取当前用户最近一次登录位置信息（经纬度和地名）
                ToastUtils.ToastShort(mActivity, "address:" + address + " latitude:" + mLatitude
                        + " longitude:" + mLongitude);
                _locMyself(mLatitude, mLongitude);
                _queryNearbyReq(mLatitude, mLongitude, address);
                if (mLocationClient.isStarted()) {
                    // 获得位置之后停止定位
                    mLocationClient.stop();
                }
            }
        }


    }

    private void _locMyself(double latitude, double longitude) {
        float latx = (float) latitude;
        float laty = (float) longitude;


        // 显示出当前位置的小图标
        mBaiduMap.setMyLocationEnabled(true);
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(100)
                .direction(90.0f)
                .latitude(latx)
                .longitude(laty).build();
        float f = mBaiduMap.getMaxZoomLevel();//19.0 最小比例尺
        mBaiduMap.setMyLocationData(locData);
        mBaiduMap.setMyLocationEnabled(true);
        LatLng ll = new LatLng(latx, laty);
        //MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll,f);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, f - 8);//设置缩放比例
        mBaiduMap.animateMapStatus(u);

        /*定位，构造地图数据*/
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //开启交通图
        mBaiduMap.setTrafficEnabled(true);
        // 开发者可根据自己实际的业务需求，利用标注覆盖物，在地图指定的位置上添加标注信息。具体实现方法如下：

        //构建Marker图标
        bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.marker);
    }


    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 退出时销毁定位
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
    }

}
