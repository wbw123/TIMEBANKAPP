package com.chase.timebank;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ReqMapActivity extends AppCompatActivity implements BaiduMap.OnMapClickListener{
    private static final String TAG = "ReqMapActivity";
    @BindView(R.id.map_view)
    MapView mMapView;
    private LocationClient mLocationClient;
    private BaiduMap mBaiduMap;
    private float latx;
    private float laty;
    private String req_addr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        Log.i(TAG, "oncreate");
        setContentView(R.layout.activity_bdmap);
        ButterKnife.bind(this);

        //获取检索到的经纬度
        Intent intent1 = getIntent();
        latx = (float) intent1.getDoubleExtra("latitude", 0.0);
        laty = (float) intent1.getDoubleExtra("longitude", 0.0);
        req_addr = intent1.getStringExtra("req_addr");

        // 获取LocationClient
        mLocationClient = new LocationClient(this);
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        mLocationClient.setLocOption(option);
        // 获取BaiduMap
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setOnMapClickListener(this);
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
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, f-8);//设置缩放比例
        mBaiduMap.animateMapStatus(u);

        /*定位，构造地图数据*/
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //开启交通图
        mBaiduMap.setTrafficEnabled(true);
        // 开发者可根据自己实际的业务需求，利用标注覆盖物，在地图指定的位置上添加标注信息。具体实现方法如下：
        //定义Maker坐标点
        mark();


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

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出时销毁定位
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mBaiduMap.clear();//这个方法清除地图上所有的mark点
        latx = (float) latLng.latitude;
        laty = (float) latLng.longitude;
        mark();
        Intent intent2 = new Intent();
        intent2.putExtra("back_latitude", latx);
        intent2.putExtra("back_longitude", laty);
        intent2.putExtra("back_req_addr", req_addr);
        intent2.putExtra("is_first_check", false);
        setResult(1,intent2);
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        mBaiduMap.clear();//这个方法清除地图上所有的mark点
        String mapPoiName = mapPoi.getName();
        LatLng mapPoiPosition = mapPoi.getPosition();
        latx = (float)mapPoiPosition.latitude;
        laty = (float)mapPoiPosition.longitude;

        //定义Maker坐标点
        mark();
        Intent intent3 = new Intent();
        intent3.putExtra("back_latitude", latx);
        intent3.putExtra("back_longitude", laty);
        intent3.putExtra("back_req_addr", mapPoiName);
        intent3.putExtra("is_first_check", false);
        setResult(1,intent3);
        return false;
    }
    private void mark() {
        //定义Maker坐标点
        LatLng point = new LatLng(latx, laty);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.marker);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions options = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(options);
    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent4 = new Intent();
        intent4.putExtra("back_latitude", latx);
        intent4.putExtra("back_longitude", laty);
        intent4.putExtra("back_req_addr", req_addr);
        intent4.putExtra("is_first_check", false);
        setResult(1,intent4);
    }*/
}
