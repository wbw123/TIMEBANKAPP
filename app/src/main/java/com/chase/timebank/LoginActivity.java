package com.chase.timebank;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chase.timebank.bean.ResultModel;
import com.chase.timebank.global.Url;
import com.chase.timebank.register.RegisterActivity;
import com.chase.timebank.util.JsonResolveUtils;
import com.chase.timebank.util.ToastUtils;
import com.chase.timebank.view.CleanEditText;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.btn_skip)
    Button mBtnSkip;
    @BindView(R.id.iv_logo)
    ImageView mIvLogo;
    @BindView(R.id.et_username)
    CleanEditText mEtUsername;
    @BindView(R.id.et_password)
    CleanEditText mEtPassword;
    @BindView(R.id.btn_login)
    Button mBtnLogin;
    @BindView(R.id.tv_create_account)
    TextView mTvCreateAccount;
    @BindView(R.id.tv_forget_password)
    TextView mTvForgetPassword;
    private static final String TAG = "LoginActivity";
    private static final int RETURN_LOGIN_MSG = 01;//请求成功码
    //TODO：用弱引用，防止内存溢出，程序崩溃 有时间的话之前的都得改
    private static class LoginHandler extends Handler {
        private final WeakReference<LoginActivity> mActivity;

        LoginHandler(LoginActivity mActivity) {
            super();
            this.mActivity = new WeakReference<>(mActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LoginActivity activity = mActivity.get();
            if (activity == null)
                return;
            //主线程展示数据
            switch (msg.what) {
                case RETURN_LOGIN_MSG:
                    String result = (String) msg.obj;
                    //解析json数据
                    ResultModel resultModel = JsonResolveUtils.parseJsonToBean(result, ResultModel.class);
                    Log.i(TAG, "json解析："+ resultModel.toString());
                    switch (resultModel.getCode()) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                            ToastUtils.ToastShort(activity, resultModel.getMsg());
                            break;
                        case 4:
                            ToastUtils.ToastShort(activity, "登录成功");
                            Intent intent = new Intent(activity, HomeActivity.class);
                            intent.putExtra("userAccount", activity.mUserName);
                            intent.putExtra("userRole", resultModel.getMsg());
                            activity.startActivity(intent);
                            activity.finish();
                            break;
                    }
                    break;
            }
        }
    }
    LoginHandler loginHandler = new LoginHandler(this);

    private String mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mEtUsername.setText("用户1");
        mEtPassword.setText("123");
    }

    @OnClick({R.id.btn_skip, R.id.btn_login, R.id.tv_create_account, R.id.tv_forget_password})
    public void clickCase(View view) {
        switch (view.getId()) {
            case R.id.btn_skip:

                break;
            case R.id.btn_login:
                _loginUser();
                break;
            case R.id.tv_create_account:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.tv_forget_password:

                break;
        }
    }

    private void _loginUser() {
        mUserName = mEtUsername.getText().toString();
        String userPassword = mEtPassword.getText().toString();
        if (TextUtils.isEmpty(mUserName) || TextUtils.isEmpty(userPassword)) {
            ToastUtils.ToastLong(this, "用户名或密码不能为空！");
            return;
        }
        RequestParams params = new RequestParams(Url.LOGIN_URL);
        params.addBodyParameter("userAccount", mUserName);
        params.addBodyParameter("userPassword", userPassword);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Message msg = new Message();
                msg.what = RETURN_LOGIN_MSG;
                msg.obj = result;
                loginHandler.sendMessage(msg);
                ToastUtils.ToastShort(getApplicationContext(),"onSuccess: result=" + result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "onError: " + ex + "  ," + isOnCallback);
                ToastUtils.ToastShort(getApplicationContext(),"onError: " + ex + "  ," + isOnCallback);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i(TAG, "onCancelled: " + cex);
                ToastUtils.ToastShort(getApplicationContext(),"onCancelled: " + cex);
            }

            @Override
            public void onFinished() {
                Log.i(TAG, "onFinished");
            }
        });
    }


}

