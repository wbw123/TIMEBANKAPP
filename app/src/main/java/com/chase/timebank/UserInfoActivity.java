package com.chase.timebank;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chase.timebank.bean.Users;
import com.chase.timebank.global.Url;
import com.chase.timebank.util.GlobalVariables;
import com.chase.timebank.util.JsonResolveUtils;
import com.chase.timebank.util.SpUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends AppCompatActivity {

    @BindView(R.id.iv_head_picture)
    CircleImageView mUserAvatar;
    @BindView(R.id.tv_userAccount)
    TextView mUserAccount;
    @BindView(R.id.tv_role)
    TextView mRole;
    @BindView(R.id.tv_gender)
    TextView mGender;
    @BindView(R.id.tv_userSate)
    TextView mUserState;
    @BindView(R.id.tv_time)
    TextView mTime;
    @BindView(R.id.tv_userAddress)
    TextView mAddress;
    @BindView(R.id.tv_userCom)
    TextView mUserCom;
    @BindView(R.id.btn_editInfo)
    Button mBtnEditInfo;
    @BindView(R.id.btn_exit)
    Button mBtnExit;
    /*include layout*/
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    private static final String TAG = "UserInfoActivity";
    private static Users mUsers;

    //TODO：用弱引用，防止内存溢出，程序崩溃 有时间的话之前的都得改
    private static class UserInfoHandler extends Handler {
        private final WeakReference<UserInfoActivity> mActivity;

        UserInfoHandler(UserInfoActivity mActivity) {
            super();
            this.mActivity = new WeakReference<UserInfoActivity>(mActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            UserInfoActivity activity = mActivity.get();
            if (activity == null)
                return;
            //主线程展示数据
            mUsers = (Users) msg.obj;
            activity._setData(mUsers);
        }
    }

    UserInfoHandler userInfoHandler = new UserInfoHandler(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        //返回按钮可见
        mIvBack.setVisibility(View.VISIBLE);
        //页标题
        mTvTitle.setText("个人资料");
        //本地读取用户头像
        showIcon();
        //联网获取userinfo
        getUserInfoFromServer();
    }

    /*从完善信息页面返回走此方法*/
    @Override
    protected void onRestart() {
        super.onRestart();
        //本地读取用户头像
        showIcon();
    }

    //从本地获取
    private void showIcon() {
        //显示用户头像
        String avatar_path = SpUtil.getString(this, GlobalVariables.USER_AVATAR_FILE_PATH);
        if (!"".equals(avatar_path) && isFileExist(avatar_path)) {
            Bitmap bitmap = BitmapFactory.decodeFile(avatar_path);
            mUserAvatar.setImageBitmap(bitmap);
        }
    }
    public static boolean isFileExist(String icon_path) {
        File file = new File(icon_path);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        setEditData();
    }

    private void _setData(Users users) {
        mUserAccount.setText(users.getUserAccount());
        mRole.setText(users.getUserRole());
        mGender.setText(users.getUserTypeGuidGender());
        mUserState.setText(users.getUserTypeAccountStatus());
        mTime.setText(String.valueOf(users.getUserOwnCurrency()));
        mAddress.setText(users.getUserAddress());
        mUserCom.setText(users.getUserCommGuid());
    }

    private void setEditData() {
        String selectedGender = getIntent().getStringExtra("selectedGender");
        String selectedCom = getIntent().getStringExtra("selectedCom");
        String editAddress = getIntent().getStringExtra("editAddress");
        Log.i(TAG, "onNewIntent " + selectedGender + " " + selectedCom + " " + editAddress);
        if (selectedGender != null) {
            mGender.setText(selectedGender);
        }
        if (selectedCom != null) {
            mUserCom.setText(selectedCom);
        }
        mAddress.setText(editAddress);
    }

    @OnClick({R.id.btn_editInfo, R.id.btn_exit,R.id.iv_back})
    public void clickCase(View view) {
        switch (view.getId()) {
            case R.id.btn_editInfo:
                Intent intent = new Intent(getApplicationContext(), UserInfoEditActivity.class);
//                intent.putExtra("userAccount", mUserAccount.getText());
//                intent.putExtra("userAddress", mAddress.getText());
                intent.putExtra("userGender", mGender.getText());
                intent.putExtra("userCom", mUserCom.getText());
                intent.putExtra("Users", mUsers);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_right_to_left_in,R.anim.anim_right_to_left_out);
                break;
            case R.id.btn_exit:

                break;
            case R.id.iv_back:
                finish();
                overridePendingTransition(R.anim.anim_left_to_right_in,R.anim.anim_left_to_right_out);
                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    public void getUserInfoFromServer() {
        RequestParams params = new RequestParams(Url.USER_INFO_URL);
        Log.i(TAG, "-------------------------");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, result);
                //子线程解析json数据
                Users users = JsonResolveUtils.parseJsonToBean(result, Users.class);
                Message msg = new Message();
                msg.obj = users;
                userInfoHandler.sendMessage(msg);
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
