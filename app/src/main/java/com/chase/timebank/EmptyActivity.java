package com.chase.timebank;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.chase.timebank.util.ToastUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class EmptyActivity extends AppCompatActivity {
    private LocationManager lm;

    private static final int GPS_CODE = 101;//开启GPS权限
    private static final int LOCATION_CODE = 102;//定位权限请求
    private static final int MYAPP_LOCATION_CODE = 103;//定位权限请求

    private static final String TAG = "EmptyActivity";
    private String mEpUserAccount;
    private String mEpUserRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        Intent intent = getIntent();
        mEpUserAccount = intent.getStringExtra("lg_userAccount");
        mEpUserRole = intent.getStringExtra("lg_userRole");


        initGPS();
//        getLocationPermission();
    }

    private void initGPS() {
        lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (ok) {//已经打开了GPS
            getLocationPermission();
        } else {
            AlertDialog.Builder da = new AlertDialog.Builder(this);
            da.setTitle("注意：");
            da.setMessage("1.部分机型若未开启GPS,地图功能无法正常使用\n" +
                    "2.确保万无一失，请您在使用前打开GPS");
            da.setCancelable(false);
            //设置右边按钮监听
            da.setPositiveButton("去开启",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // 转到手机设置界面，用户设置GPS
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, GPS_CODE); // 设置完成后返回到原来的界面
                        }
                    });

            //设置左边按钮监听
            da.setNeutralButton("取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();
                            ToastUtils.ToastLong(getApplication(), "如需精确定位，可手动开启");
                            getLocationPermission();
                        }
                    });

            da.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_CODE:
                if (grantResults.length > 0) {
                    //存的是两个定位权限
                    Log.i(TAG, "permissions:" + Arrays.toString(permissions));
                    //用户同意都是0，禁止都是-1 PERMISSION_GRANTED = 0 ； PERMISSION_DENIED = -1
                    Log.i(TAG, "grantResults:" + Arrays.toString(grantResults));
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            //有一个-1，就没授权成功

//                            ToastUtils.ToastLong(this, "必须同意定位权限才能正常使用地图功能");
                            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                                    permissions[0])) {
                                showNoAskDialog();
                            } else {
                                new AlertDialog.Builder(this)
                                        .setTitle("提示：")
                                        .setMessage("必须同意定位权限才能正常使用地图功能")
                                        .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                getLocationPermission();
                                            }
                                        })
                                        .setNeutralButton("算了", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                _jumpToHomeACT();
                                                EmptyActivity.this.finish();
                                            }
                                        })
                                        .show();
                            }
                            return;
                        }
                    }
                    ToastUtils.ToastLong(this, "定位授权成功");

                    _jumpToHomeACT();
                    this.finish();
                } else {
                    Log.i(TAG, "用户点击'拒绝'并勾选'不再询问'");
                }
                break;
            default:
        }

    }
    //用户点击"拒绝"并勾选"不再询问"
    private void showNoAskDialog() {
        Log.i(TAG, "弹出不在询问对话框！");
        new AlertDialog.Builder(this)
                .setTitle("打开定位，地图功能才可正常使用")
                .setMessage("1.点击设置，进入应用信息页\n" +
                        "2.选择'定位'\r\n" +
                        "3.选择'始终允许'")
                .setPositiveButton("转至设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //引导用户至设置页手动授权
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent,MYAPP_LOCATION_CODE);
                    }
                })
                .setNeutralButton("不了，谢谢", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        _jumpToHomeACT();
                        EmptyActivity.this.finish();
                    }
                })
                .show();
    }

    public void getLocationPermission() {
        ArrayList<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
//        if (ContextCompat.checkSelfPermission(mActivity,
//                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, LOCATION_CODE);
        } else {
            ToastUtils.ToastLong(this, "定位功能已授权");
            _jumpToHomeACT();
            this.finish();
        }
    }

    private void _jumpToHomeACT() {
        Intent intent = new Intent(EmptyActivity.this, HomeActivity.class);
        intent.putExtra("userAccount", mEpUserAccount);
        intent.putExtra("userRole", mEpUserRole);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GPS_CODE:
                Log.i(TAG, "从手动打开GPS页面返回，目前不知道怎么判断打没打开");
                getLocationPermission();
                break;
            case MYAPP_LOCATION_CODE:
                _jumpToHomeACT();
                this.finish();
                break;
        }
    }


}
