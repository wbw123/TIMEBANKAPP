package com.chase.timebank.register;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.chase.timebank.R;
import com.chase.timebank.bean.ResultModel;
import com.chase.timebank.global.Url;
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

public class RegisterActivity extends AppCompatActivity {
    @BindView(R.id.cet_nickname)
    CleanEditText mCetNickName;
    @BindView(R.id.cet_password)
    CleanEditText mCetPassWord;
    @BindView(R.id.cet_repassword)
    CleanEditText mCetRePassWord;
    @BindView(R.id.btn_create_account)
    Button mBtnCreatAccount;
    private static final String TAG = "RegisterActivity";

    private String mNickName;
    private String mPass;
    private String mRePass;

    //TODO：用弱引用，防止内存溢出，程序崩溃 有时间的话之前的都得改
    private static class RegisterHandler extends Handler {
        private final WeakReference<RegisterActivity> mActivity;

        RegisterHandler(RegisterActivity mActivity) {
            super();
            this.mActivity = new WeakReference<RegisterActivity>(mActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            RegisterActivity activity = mActivity.get();
            if (activity == null)
                return;
            ResultModel resultModel = (ResultModel) msg.obj;
            switch (resultModel.getCode()) {
                case 0:
                case 2:
                case 3:
                case 4:
                    ToastUtils.ToastLong(activity, "注册失败");
                    break;
                case 1:
                    ToastUtils.ToastShort(activity, resultModel.getMsg());
                    activity.finish();
                    break;
                case 11://账号已存在
                    ToastUtils.ToastLong(activity, resultModel.getMsg());
                    break;
            }
        }
    }

    RegisterHandler registerHandler = new RegisterHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_create_account)
    public void clickCase(View view) {
        switch (view.getId()) {
            case R.id.btn_create_account:
                _register();
                break;
        }
    }

    private void _register() {
        mNickName = mCetNickName.getText().toString();
        mPass = mCetPassWord.getText().toString();
        mRePass = mCetRePassWord.getText().toString();
        if (!TextUtils.isEmpty(mNickName)
                && !TextUtils.isEmpty(mPass)
                && !TextUtils.isEmpty(mRePass)) {

            Log.i(TAG, "mNickName:" + mNickName);
            Log.i(TAG, "mPass:" + mPass);
            Log.i(TAG, "mRePass:" + mRePass);

            if (mPass.equals(mRePass)) {
                Log.i(TAG, "两次密码是否相等:" + mPass.equals(mRePass));
                //上传用户名、密码到数据库
                _uploadToServer();
            } else {
                ToastUtils.ToastShort(this, "两次输入的密码不一致,请重新输入!");
            }

        } else {
            ToastUtils.ToastShort(this, "用户名和密码不能为空!");
        }
    }

    private void _uploadToServer() {
        RequestParams params = new RequestParams(Url.REGISTER_URL);
        params.addBodyParameter("userAccount", mNickName);
        params.addBodyParameter("userPassword", mPass);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //解析json数据
                ResultModel resultModel = JsonResolveUtils.parseJsonToBean(result, ResultModel.class);
                Message msg = new Message();
                msg.obj = resultModel;
                registerHandler.sendMessage(msg);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "onError: " + ex + "  ," + isOnCallback);
                ToastUtils.ToastShort(getApplicationContext(), "onError: " + ex + "  ," + isOnCallback);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i(TAG, "onCancelled: " + cex);
                ToastUtils.ToastShort(getApplicationContext(), "onCancelled: " + cex);
            }

            @Override
            public void onFinished() {
//                Log.i(TAG, "onFinished");
            }
        });
    }
}
