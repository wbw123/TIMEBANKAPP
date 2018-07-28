package com.chase.timebank;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.chase.timebank.global.Constant;
import com.chase.timebank.global.Url;
import com.chase.timebank.util.JsonResolveUtils;
import com.chase.timebank.util.PCAJsonUtil;
import com.chase.timebank.util.ToastUtils;
import com.chase.timebank.view.CleanEditText;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserInfoEditActivity extends AppCompatActivity {


    /*include layout*/
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_right)
    TextView mTvSave;

    /*控件*/
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
                    if (resultModel.getCode() == 1) {
                        ToastUtils.ToastShort(activity, resultModel.getMsg());
                        //信息回传到UserInfoActivity
                        Intent intent = new Intent(activity, UserInfoActivity.class);
                        intent.putExtra("selectedGender", activity.mSpGender.getSelectedItem().toString());
                        intent.putExtra("selectedCom", activity.mSpCom.getSelectedItem().toString());
                        intent.putExtra("editAddress", activity.mUserAddr.getText().toString());
                        activity.startActivity(intent);
                        activity.finish();
                        activity.overridePendingTransition(R.anim.anim_left_to_right_in,R.anim.anim_left_to_right_out);
                    } else {
                        ToastUtils.ToastShort(activity,"服务器异常，请稍后重试！");
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
        new Thread(){
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
        mTvUserPCA.setText(users.getUserProvince()+"-"+users.getUserCity()+"-"+users.getUserDistrict());
    }

    private void initViewAndData() {
        mTvSave.setVisibility(View.VISIBLE);
        mIvBack.setVisibility(View.VISIBLE);
        mTvTitle.setText("完善信息");
        //因性别、小区等部分字段存储的为GUID，方便起见，单独获取
        mUserGender = getIntent().getStringExtra("userGender");
        mUserCom = getIntent().getStringExtra("userCom");
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
            R.id.tv_right})
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
                overridePendingTransition(R.anim.anim_left_to_right_in,R.anim.anim_left_to_right_out);
                break;
            case R.id.tv_right:
                saveUserInfo();
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
                String tx = options1Items.get(options1).getPickerViewText() +"-"+
                        options2Items.get(options1).get(options2) +"-"+
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
                Log.i(TAG,"=================="+ result);
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
}
