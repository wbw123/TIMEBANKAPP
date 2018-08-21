package com.chase.timebank;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.chase.timebank.bean.Community;
import com.chase.timebank.bean.PCABean;
import com.chase.timebank.bean.ResultModel;
import com.chase.timebank.bean.Users;
import com.chase.timebank.dialog.BottomDialog;
import com.chase.timebank.dialog.MyDialog;
import com.chase.timebank.global.Constant;
import com.chase.timebank.global.Url;
import com.chase.timebank.util.CropImageUtils;
import com.chase.timebank.util.GlobalVariables;
import com.chase.timebank.util.JsonResolveUtils;
import com.chase.timebank.util.LogUtils;
import com.chase.timebank.util.PCAJsonUtil;
import com.chase.timebank.util.SpUtil;
import com.chase.timebank.util.ToastUtils;
import com.chase.timebank.view.CleanEditText;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoEditActivity extends AppCompatActivity implements BottomDialog.OnBottomMenuItemClickListener {


    /*include layout*/
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_right)
    TextView mTvSave;

    /*控件*/
    @BindView(R.id.iv_head_picture)
    CircleImageView mIvHeadPic;
    @BindView(R.id.iv_camera)
    ImageView mIvCamera;

    @BindView(R.id.sp_gender)
    Spinner mSpGender;
    @BindView(R.id.sp_com)
    Spinner mSpCom;
    @BindView(R.id.tv_edituserAccount)
    TextView mTvUserAccount;
    @BindView(R.id.cet_editUserAddr)
    CleanEditText mUserAddr;

    @BindView(R.id.cet_edituserName)
    CleanEditText mUserName;
    @BindView(R.id.cet_edituserMail)
    CleanEditText mUserMail;
    @BindView(R.id.cet_edituserPhone)
    CleanEditText mUserPhone;
    @BindView(R.id.cet_edituserIdNum)
    CleanEditText mUserIdNum;
    @BindView(R.id.cet_edituserEmerPerson)
    CleanEditText mUserEmerPerson;
    @BindView(R.id.cet_edituserEmerContact)
    CleanEditText mUserEmerContact;
    @BindView(R.id.tv_edituserBirthdate)
    TextView mTvUserBirth;
    @BindView(R.id.tv_edituserPCA)
    TextView mTvUserPCA;

    @BindView(R.id.cb_register_agree)
    CheckBox mCbAgree;
    @BindView(R.id.tv_protocol)
    TextView mProtocol;
    private static final String TAG = "UserInfoEditActivity";

    /*spinner适配*/
    private ArrayAdapter<String> mGenderAdapter;
    private ArrayAdapter<String> mComAdapter;

    /*省市区*/
    private ArrayList<PCABean> options1Items = new ArrayList<>();//省
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();//市
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();//区
    private boolean isPCALoaded = false;//省市区解析数据是否完成
    //handler constant
    private static final int GET_COM_URL = 101;
    private static final int UPDATE_USER_INFO_URL = 102;
    private static final int USER_INFO_URL = 103;

    //存储
    public static final int REQUEST_CODE_PERMISSION_STORAGE = 104;
    //相机
    public static final int REQUEST_CODE_PERMISSION_CAMERA = 105;
    //相机相册底部弹窗dialog
    private BottomDialog bottomDialog;

    /*handler弱引用----------------------------------------start*/
    private static class UserInfoEditHandler extends Handler {
        private final WeakReference<UserInfoEditActivity> mActivity;

        UserInfoEditHandler(UserInfoEditActivity mActivity) {
            super();
            this.mActivity = new WeakReference<>(mActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            UserInfoEditActivity activity = mActivity.get();
            if (activity == null)
                return;
            //主线程展示数据
            switch (msg.what) {
                case GET_COM_URL:
                    String[] comTitle = (String[]) msg.obj;
                    activity._initCom(comTitle);//初始化小区spinner
                    break;
                case UPDATE_USER_INFO_URL:
                    ResultModel resultModel = (ResultModel) msg.obj;
                    switch (resultModel.getCode()) {
                        case 1:
                            ToastUtils.ToastShort(activity, resultModel.getMsg());
                            //信息回传到UserInfoActivity
                            Intent intent = new Intent(activity, UserInfoActivity.class);
                            intent.putExtra("selectedGender", activity.mSpGender.getSelectedItem().toString());
                            intent.putExtra("selectedCom", activity.mSpCom.getSelectedItem().toString());
                            intent.putExtra("editAddress", activity.mUserAddr.getText().toString());
                            activity.startActivity(intent);
                            activity.finish();
                            activity.overridePendingTransition(R.anim.anim_left_to_right_in, R.anim.anim_left_to_right_out);
                            break;
                        case 11:
                        case 12:
                            ToastUtils.ToastShort(activity, resultModel.getMsg());
                            break;
                        case 0:
                            ToastUtils.ToastShort(activity, "服务器异常，请稍后重试！");
                            break;
                    }

                    break;
                case USER_INFO_URL:
                    //主线程展示数据
                    Users users = (Users) msg.obj;
                    activity._setData(users);
                    break;
            }
        }
    }

    UserInfoEditHandler userInfoEditHandler = new UserInfoEditHandler(this);
    /*handler弱引用----------------------------------------end*/
    //Intent变量
    private String mUserGender;
    private String mUserCom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_edit);
        ButterKnife.bind(this);
        //初始化布局和数据
        initViewAndData();
        //联网获取community数据
        getComFromServer();
        //初始化日期选择控件
        initDatePicker();
        //初始化省市区控件(子线程中解析省市区json)
        new Thread() {
            @Override
            public void run() {
                super.run();
                //省市区三级联动初始化
                initPCAData();
            }
        }.start();
        initGender();//初始化性别spinner
        getUserInfoFromServer();//联网获取数据

    }

    private void _setData(Users users) {
        mTvUserAccount.setText(users.getUserAccount());
        mUserAddr.setText(users.getUserAddress());
        mUserName.setText(users.getUserName());
        mUserMail.setText(users.getUserMail());
        mUserPhone.setText(users.getUserPhone());
        mUserIdNum.setText(users.getUserIdnum());
        mTvUserBirth.setText(users.getUserBirthdate());
        mUserEmerPerson.setText(users.getUserEmerperson());
        mUserEmerContact.setText(users.getUserEmercontact());
        mTvUserPCA.setText(users.getUserProvince() + "-" + users.getUserCity() + "-" + users.getUserDistrict());
    }

    private void initViewAndData() {
        mTvSave.setVisibility(View.VISIBLE);
        mIvBack.setVisibility(View.VISIBLE);
        mTvTitle.setText("完善信息");
        //因性别、小区等部分字段存储的为GUID，方便起见，单独获取
        mUserGender = getIntent().getStringExtra("userGender");
        mUserCom = getIntent().getStringExtra("userCom");
        //只能输入电话号码
        mUserPhone.setInputType(InputType.TYPE_CLASS_PHONE);
        mUserEmerContact.setInputType(InputType.TYPE_CLASS_PHONE);
        //只能输入邮箱
        mUserMail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        //用户协议
        toProtocol();
        //相机相册底部弹窗
        bottomDialog = new BottomDialog(this, R.layout.dialog_bottom_layout, new int[]{R.id.pick_photo_album, R.id.pick_photo_camera, R.id.pick_photo_cancel});
        bottomDialog.setOnBottomMenuItemClickListener(this);
    }

    private void toProtocol() {
        SpannableStringBuilder builder = new SpannableStringBuilder(mProtocol.getText().toString());
        ForegroundColorSpan blueSpan = new ForegroundColorSpan(Color.BLUE);
        UnderlineSpan lineSpan = new UnderlineSpan();
        builder.setSpan(lineSpan, 8, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //下划线
        builder.setSpan(blueSpan, 8, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //字体颜色

        mProtocol.setText(builder);
    }

    private void initPCAData() {
        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String PCAData = PCAJsonUtil.getJson(this, "province.json");//获取assets目录下的json文件数据
        ArrayList<PCABean> PCABean = PCAJsonUtil.parseData(PCAData);//用Gson 转成实体
        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = PCABean;
        for (int i = 0; i < PCABean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）
            for (int c = 0; c < PCABean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = PCABean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市
                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表
                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (PCABean.get(i).getCityList().get(c).getArea() == null
                        || PCABean.get(i).getCityList().get(c).getArea().size() == 0) {
                    City_AreaList.add("");
                } else {
                    for (int d = 0; d < PCABean.get(i).getCityList().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                        String AreaName = PCABean.get(i).getCityList().get(c).getArea().get(d);
                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }
            /**
             * 添加城市数据
             */
            options2Items.add(CityList);
            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
        }
        isPCALoaded = true;
    }

    private void getComFromServer() {
        RequestParams params = new RequestParams(Url.GET_COM_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                List<Community> community = JsonResolveUtils.parseJsonToList(result, Community.class);
                Log.i(TAG, "json解析:" + community);
                String[] comTitle = new String[community.size()];
                for (int i = 0; i < community.size(); i++) {
                    comTitle[i] = community.get(i).getCommTitle();
                }
                Log.i(TAG, "comTitle" + Arrays.toString(comTitle));
                Message msg = new Message();
                msg.obj = comTitle;
                msg.what = GET_COM_URL;
                userInfoEditHandler.sendMessage(msg);
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

    private void initGender() {
        if (mGenderAdapter == null) {
            mGenderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Constant.USER_GENDER_DATA);
        }
        mGenderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpGender.setAdapter(mGenderAdapter);
        //设置性别默认选中值
        int position = mGenderAdapter.getPosition(mUserGender);
        mSpGender.setSelection(position);
    }

    private void _initCom(String[] comTitle) {
        if (mComAdapter == null) {
            mComAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, comTitle);
        }
        mComAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpCom.setAdapter(mComAdapter);
        //设置小区默认选中值
        int position = mComAdapter.getPosition(mUserCom);
        mSpCom.setSelection(position);
    }

    @OnClick({R.id.tv_edituserBirthdate, R.id.tv_edituserPCA, R.id.iv_back,
            R.id.tv_right, R.id.tv_protocol, R.id.iv_camera})
    public void clickCase(View view) {
        switch (view.getId()) {
            case R.id.tv_edituserBirthdate:
                // 日期格式为yyyy-MM-dd
                TimePickerView TPview1 = new TimePickerBuilder(this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        mTvUserBirth.setText(getTime(date));
                    }
                }).setType(new boolean[]{true, true, true, false, false, false})
                        .build();
                TPview1.setDate(Calendar.getInstance());
                TPview1.show();
                break;
            case R.id.tv_edituserPCA:
                if (isPCALoaded) {
                    showPCAPicker();
                } else {
                    ToastUtils.ToastShort(UserInfoEditActivity.this, "请等待省市区数据解决完成");
                }
                break;
            case R.id.iv_back:
                finish();
                overridePendingTransition(R.anim.anim_left_to_right_in, R.anim.anim_left_to_right_out);
                break;
            case R.id.tv_right:
                if (mCbAgree.isChecked()) {
                    saveUserInfo();
                } else {
                    ToastUtils.ToastShort(this, "请先同意《用户协议》！");
                }
                break;
            case R.id.tv_protocol:
                MyDialog myDialog = new MyDialog(this);
                myDialog.setTitle("山海时间银行服务协议");
                myDialog.show();
                break;

            case R.id.iv_camera:
//                showPhotoSelectDialog();
                bottomDialog.show();
                break;
        }

    }

    private void saveUserInfo() {
        Log.i(TAG, "保存用户信息");
        //更新users数据库
        _updateUserInfo();

    }


    private SimpleDateFormat mDateFormat;

    private void initDatePicker() {
        mDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String now = mDateFormat.format(new Date());
        mTvUserBirth.setText(now);
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        return mDateFormat.format(date);
    }

    private void showPCAPicker() {// 弹出选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1).getPickerViewText() + "-" +
                        options2Items.get(options1).get(options2) + "-" +
                        options3Items.get(options1).get(options2).get(options3);
                mTvUserPCA.setText(tx);
            }
        })
                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .build();
        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

    private void _updateUserInfo() {
        String pca = mTvUserPCA.getText().toString();
        String[] splitPCA = pca.split("-");
        RequestParams params = new RequestParams(Url.UPDATE_USER_INFO_URL);
        params.addBodyParameter("userTypeGuidGender", mSpGender.getSelectedItem().toString());
        params.addBodyParameter("userCommGuid", mSpCom.getSelectedItem().toString());
        params.addBodyParameter("userAddress", mUserAddr.getText().toString());
        params.addBodyParameter("userName", mUserName.getText().toString());
        params.addBodyParameter("userMail", mUserMail.getText().toString());
        params.addBodyParameter("userPhone", mUserPhone.getText().toString());
        params.addBodyParameter("userIdnum", mUserIdNum.getText().toString());
        params.addBodyParameter("userBirthdate", mTvUserBirth.getText().toString());
        params.addBodyParameter("userEmerperson", mUserEmerPerson.getText().toString());
        params.addBodyParameter("userEmercontact", mUserEmerContact.getText().toString());
        params.addBodyParameter("userProvince", splitPCA[0]);
        params.addBodyParameter("userCity", splitPCA[1]);
        params.addBodyParameter("userDistrict", splitPCA[2]);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "==================" + result);
                ResultModel resultModel = JsonResolveUtils.parseJsonToBean(result, ResultModel.class);
                Message msg = new Message();
                msg.obj = resultModel;
                msg.what = UPDATE_USER_INFO_URL;
                userInfoEditHandler.sendMessage(msg);
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

    public void getUserInfoFromServer() {
        RequestParams params = new RequestParams(Url.USER_INFO_URL);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, result);
                //子线程解析json数据
                Users users = JsonResolveUtils.parseJsonToBean(result, Users.class);
                Message msg = new Message();
                msg.what = USER_INFO_URL;
                msg.obj = users;
                userInfoEditHandler.sendMessage(msg);
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


    @Override
    public void onResume() {
        super.onResume();
        showIcon();
    }

    //从本地获取
    private void showIcon() {
        //显示用户头像
        String icon_path = SpUtil.getString(this, GlobalVariables.USER_ICON_FILE_PATH);
        if (icon_path != null && isFileExist(icon_path)) {
            Bitmap bitmap = BitmapFactory.decodeFile(icon_path);
            mIvHeadPic.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onBottomMenuItemClick(BottomDialog dialog, View view) {
        switch (view.getId()) {
            case R.id.pick_photo_album: //从相册选
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    checkPermission(REQUEST_CODE_PERMISSION_STORAGE);
                } else {
                    CropImageUtils.getInstance().openAlbum(this);
                }
//                fromXianche();
                break;
            case R.id.pick_photo_camera: //拍照
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    checkPermission(REQUEST_CODE_PERMISSION_CAMERA);
                } else {
                    CropImageUtils.getInstance().takePhoto(this);
                }
//                fromPaizhao();
                break;
        }
    }

    /**
     * 检测权限
     */
    public void checkPermission(int permissionType) {
        if (Build.VERSION.SDK_INT >= 23) {
            switch (permissionType) {
                //调用单个权限
                case REQUEST_CODE_PERMISSION_STORAGE:
                    if (!AndPermission.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        AndPermission.with(this)
                                .requestCode(REQUEST_CODE_PERMISSION_STORAGE)
                                .permission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                .callback(permissionListener)
                                .start();
                    } else {
                        CropImageUtils.getInstance().openAlbum(this);
                    }
                    break;
                //调用多个权限，相机和存储(拍照)
                case REQUEST_CODE_PERMISSION_CAMERA:
                    //如果没有申请权限
                    if (!AndPermission.hasPermission(this, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        AndPermission.with(this)
                                .requestCode(REQUEST_CODE_PERMISSION_CAMERA)
                                .permission(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                                .callback(permissionListener)
                                .start();
                    } else {
                        CropImageUtils.getInstance().takePhoto(this);
                    }
                    break;
            }
        }
    }

    /**
     * 回调监听。
     */
    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode) {
                case REQUEST_CODE_PERMISSION_STORAGE: {
                    CropImageUtils.getInstance().openAlbum(UserInfoEditActivity.this);
                    break;
                }
                case REQUEST_CODE_PERMISSION_CAMERA: {
                    CropImageUtils.getInstance().takePhoto(UserInfoEditActivity.this);
                    break;
                }
            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            switch (requestCode) {
                case REQUEST_CODE_PERMISSION_STORAGE: {
                    Toast.makeText(UserInfoEditActivity.this, "获取读取sd卡权限失败", Toast.LENGTH_SHORT).show();
                    break;
                }
                case REQUEST_CODE_PERMISSION_CAMERA: {
                    Toast.makeText(UserInfoEditActivity.this, "获取拍照权限失败", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CropImageUtils.getInstance().onActivityResult(this, requestCode, resultCode, data, new CropImageUtils.OnResultListener() {
            @Override
            public void takePhotoFinish(String path) {
                //拍照回调，去裁剪
                CropImageUtils.getInstance().cropPicture(UserInfoEditActivity.this, path);
            }

            @Override
            public void selectPictureFinish(String path) {
                //相册回调，去裁剪
                CropImageUtils.getInstance().cropPicture(UserInfoEditActivity.this, path);
            }

            @Override
            public void cropPictureFinish(String path) {
                LogUtils.d("path", path + " .....");
                //TODO 上传图片
                SpUtil.putString(UserInfoEditActivity.this, GlobalVariables.USER_ICON_FILE_PATH, path);
                upload(path);
            }

        });
    }

    private void upload(String path) {
        RequestParams params = new RequestParams(Url.IMAGE_UPLOAD_URL);
        params.setMultipart(true);//设置表单传送
        params.addBodyParameter("userIdimage", new File(path),"multipart/form-data");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

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

    /**
     * 上传图片到服务器
     *
     * @param path
     */
    /*private void upload(String path) {
        HashMap<String, Object> paramsMap = getMapParams(path);
        //TODO 1. 请求路径url
        String request_url = "";
        MultipartBody.Builder builder = new MultipartBody.Builder();
        //设置类型(表单上传)
        builder.setType(MultipartBody.FORM);
        //追加参数
        for (String key : paramsMap.keySet()) {
            Object object = paramsMap.get(key);
            if (!(object instanceof File)) {
                builder.addFormDataPart(key, object.toString());
            } else {
                File file = (File) object;
                builder.addFormDataPart(key, file.getName(), RequestBody.create(null, file));
            }
        }
        //创建RequestBody
        RequestBody body = builder.build();
        //创建Request
        final Request request = new Request.Builder().url(request_url).post(body).build();
        //单独设置参数 比如读取超时时间
        Call call = new OkHttpClient().newBuilder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.d("json", "上传失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String string = response.body().string();
                    LogUtils.d("json", "上传图片成功: " + string);
                } else {
                    LogUtils.d("json", "上传失败");
                }
            }
        });
    }*/

    //TODO 2. 添加参数
    private HashMap<String, Object> getMapParams(String path) {
        HashMap<String, Object> params = new HashMap<>();
        //todo 这里根据接口自己改变参数
        //        params.put("uid", uid);
        //        params.put("pwd", Encrypt.getMD5Str(pwd));
        //        params.put("bid", Constant.PRODUCT);
        //        params.put("cp", Constant.OS);
        //        params.put("cv", AndroidUtil.getVersionName(this));
        //        params.put("pkgname", this.getPackageName());
        //        params.put("imei", SystemUtil.getImei(this));
        //        params.put("imsi", SystemUtil.getImsi(this));
        //        params.put("netmode", SystemUtil.getNetworkName(this));
        //        params.put("ts", String.valueOf(System.currentTimeMillis() / 1000));
        File file = new File(path);
        if (file != null) {
            params.put("pic", file);
        }
        return params;
    }

    public static boolean isFileExist(String icon_path) {
        File file = new File(icon_path);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    //弹出对话框让用户选择头像来源(拍照vs相册)
    /*private void showPhotoSelectDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog, null);
        builder.setView(view);
        TextView paizhaoText = view.findViewById(R.id.zdy_text1);
        TextView fromxzheText = view.findViewById(R.id.zdy_text2);
        final AlertDialog theDialog = builder.create();
        paizhaoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                theDialog.dismiss();
                fromPaizhao();
            }
        });
        fromxzheText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                theDialog.dismiss();
                fromXianche();
            }
        });
        theDialog.show();
    }

    private String image_from_sd_paizhao_or_xianche__path;//图片sd路径
    private boolean isuploadImage;//标记用户是否上传图片
    private String path; //sd卡路径
    *//**
     * 注意：
     * 1.拍照和打开相册Android6.0系统后要动态授予应用写和读SD卡的权限，否则拍照失败/从相册获取图片失败或应用崩溃
     * 2.需重写onRequestPermissionsResult方法监听用户对权限的授予情况(拒绝vs允许)，在允许的方法里执行相应的拍照或打开相册功能
     * 3.经测试，在允许了其中一个权限后下次再点击就无需申请了
     *//*
    private static int WRITE_SD_CODE = 1;
    private static int READ_SD_CODE = 2;
    @TargetApi(Build.VERSION_CODES.M)
    private void fromPaizhao() {
        //6.0以上系统(拍照时)动态申请写sd卡权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_SD_CODE);//权限，请求码
            Log.v("TAG", "去获取拍照权限");
        }else {
            paizhao();
            Log.v("TAG", "拍照.....");
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void fromXianche() {
        //6.0以上系统(打开相册时)动态申请读sd卡权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},READ_SD_CODE);
            Log.v("TAG", "去获取相册权限");
        }else {
            xianche();
            Log.v("TAG", "相册....");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.v("TAG", "权限requestCode：" + requestCode);
        if (requestCode == WRITE_SD_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                paizhao();
                Log.v("TAG", "授予权限");
            }else {
                Log.v("TAG", "拒绝权限");
            }
        }else if(requestCode==READ_SD_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                xianche();
                Log.v("TAG", "授予权限");
            }else {
                Log.v("TAG", "拒绝权限");
            }
        }
    }

    *//**
     *  解决android7.0拍照应用挂掉问题：http://www.cnblogs.com/netcorner/p/6542373.html
     * 1.安卓7.0遇到 android.os.FileUriExposedException: file:///storage/emulated.. exposed beyond app through Intent.getData()
     * 2.注意：4..2.2的系统拍照要用Uri.fromFile(file)(oppo手机测试结果)，否则在拍照完成点击OK确认后在onActivityResult()方法里执行的是放弃拍照的if分支
     * 故要做系统版本判断
     *//*

    private void paizhao(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        path = Environment.getExternalStorageDirectory().getPath() + "/";
        //将当前的拍照时间作为图片的文件名称
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String filename = simpleDateFormat.format(new Date()) + ".jpg";
        image_from_sd_paizhao_or_xianche__path = path + filename;
        File file = new File(image_from_sd_paizhao_or_xianche__path);
        /*//******************************************************************
     Uri photoURI;
     //解决三星7.x或其他7.x系列的手机拍照失败或应用崩溃的bug.解决4.2.2(oppo)/4.x系统拍照执行不到设置显示图片的bug
     if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N){ //7.0以下的系统用 Uri.fromFile(file)
     photoURI = Uri.fromFile(file);
     }else {                                            //7.0以上的系统用下面这种方案
     photoURI = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider",file);
     }
     /*//******************************************************************
     intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);//将图片文件转化为一个uri传入
     startActivityForResult(intent, 100);
     }

     //打开相册
     private void xianche(){
     Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
     startActivityForResult(intent, 200);
     }

     @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     super.onActivityResult(requestCode, resultCode, data);
     switch (requestCode){
     case 100://拍照
     System.out.println("requestCode"+requestCode);
     System.out.println("image_from_sd_paizhao_or_xianche__path"+image_from_sd_paizhao_or_xianche__path);
     if (resultCode== RESULT_OK){
     if(image_from_sd_paizhao_or_xianche__path!=null) {
     //UIL框架加载本地sd卡图片路径为 String imageFilePath = "file://" + image_from_sd_paizhao;
     //Picssao用file来封装文件
     File file = new File(image_from_sd_paizhao_or_xianche__path);
     Picasso.with(this).load(file).into(mIvHeadPic);
     isuploadImage = true;
     Log.v("TAG", "拍照获取的图片sd卡路径:" + image_from_sd_paizhao_or_xianche__path);
     }
     }else{
     ToastUtils.ToastShort(this,"放弃拍照");
     }
     break;
     case 200://从相册
     if(resultCode== RESULT_OK) {
     //内容解析者来操作内容提供最对数据的4方法
     if (data!=null) {
     Uri uri = data.getData();
     if (uri!=null) {
     Cursor cursor = getContentResolver().query(uri, null, null, null, null);
     //选择的就只是一张图片，所以cursor只有一条记录
     if (cursor != null) {
     if (cursor.moveToFirst()) {
     image_from_sd_paizhao_or_xianche__path = cursor.getString(cursor.getColumnIndex("_data"));//获取相册路径字段
     File file = new File(image_from_sd_paizhao_or_xianche__path);
     Picasso.with(this).load(file).into(mIvHeadPic);
     isuploadImage = true;
     Log.v("TAG", "打开相册获取的图片sd卡路径:" + image_from_sd_paizhao_or_xianche__path);
     }
     }
     }
     }
     }else{
     ToastUtils.ToastShort(this,"放弃从相册选择");
     }
     break;
     }
     }*/
}
