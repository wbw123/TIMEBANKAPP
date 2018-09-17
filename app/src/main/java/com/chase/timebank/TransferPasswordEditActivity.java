package com.chase.timebank;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chase.timebank.bean.ResultModel;
import com.chase.timebank.global.Url;
import com.chase.timebank.util.JsonResolveUtils;
import com.chase.timebank.util.ToastUtils;
import com.chase.timebank.view.Keyboard;
import com.chase.timebank.view.PayEditText;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;


public class TransferPasswordEditActivity extends AppCompatActivity {
    private static final String[] KEY = new String[]{
            "1", "2", "3",
            "4", "5", "6",
            "7", "8", "9",
            "<<", "0", "完成"
    };
    private static final String TAG = "TransferPWEditActivity";
    private PayEditText payEditText1;
    private PayEditText payEditText2;
    private Keyboard keyboard;

    private boolean isFirstKeyborad = true;
    private TextView mTvTitle;
    private ImageView mIvBack;
    private String password1;
    private String password2;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = (String) msg.obj;
            //解析json数据
            ResultModel resultModel = JsonResolveUtils.parseJsonToBean(result, ResultModel.class);
            Log.i(TAG, "json解析：" + resultModel.toString());
            switch (resultModel.getCode()) {
                case 1://支付密码设置成功
                    ToastUtils.ToastShort(getApplicationContext(), resultModel.getMsg());
                    finish();
                    break;
                default:
                    ToastUtils.ToastLong(getApplicationContext(), "服务器异常，请稍后重试！");
                    break;
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_password_edit);
        initView();
        setSubView();
        initEvent();
    }

    private void initView() {
//        setContentView(R.layout.activity_main);
        payEditText1 = (PayEditText) findViewById(R.id.PayEditText_pay1);
        payEditText2 = (PayEditText) findViewById(R.id.PayEditText_pay2);
        keyboard = (Keyboard) findViewById(R.id.KeyboardView_pay);

        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvBack.setVisibility(View.VISIBLE);
        mTvTitle.setText("设置支付密码");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setSubView() {
        //设置键盘
        keyboard.setKeyboardKeys(KEY);
    }

    private void initEvent() {
        keyboard.setOnClickKeyboardListener(new Keyboard.OnClickKeyboardListener() {
            @Override
            public void onKeyClick(int position, String value) {
                if (isFirstKeyborad) {//第一个keyborad输入密码
                    if (position < 11 && position != 9) {
                        payEditText1.add(value);
                    } else if (position == 9) {
                        payEditText1.remove();
                    } else if (position == 11) {
                        //当点击完成的时候，也可以通过payEditText.getText()获取密码，此时不应该注册OnInputFinishedListener接口
                        if (payEditText1.getText().length() == 6) {
                            Toast.makeText(getApplication(), "您的密码是：" + payEditText1.getText(), Toast.LENGTH_SHORT).show();
                            isFirstKeyborad = false;
                        } else {
                            ToastUtils.ToastShort(getApplication(), "密码必须为6位");
                        }
                    }
                } else {
                    if (position < 11 && position != 9) {
                        payEditText2.add(value);
                    } else if (position == 9) {
                        payEditText2.remove();
                    } else if (position == 11) {
                        //当点击完成的时候，也可以通过payEditText.getText()获取密码，此时不应该注册OnInputFinishedListener接口
                        if (payEditText1.getText().length() == 6) {

                            if (password1.equals(password2)) {
                                _insertTransPWtoServer();
                            } else {
                                ToastUtils.ToastLong(getApplication(),"两次输入密码不一致，请重新输入！");
                                payEditText2.remove();
                                payEditText2.remove();
                                payEditText2.remove();
                                payEditText2.remove();
                                payEditText2.remove();
                                payEditText2.remove();
                                payEditText1.remove();
                                payEditText1.remove();
                                payEditText1.remove();
                                payEditText1.remove();
                                payEditText1.remove();
                                payEditText1.remove();
                                isFirstKeyborad = true;
                            }
                        } else {
                            ToastUtils.ToastShort(getApplication(), "密码必须为6位");
                        }
                    }
                }

            }
        });

        /**
         * 当密码输入完成时的回调
         */
        payEditText1.setOnInputFinishedListener(new PayEditText.OnInputFinishedListener() {
            @Override
            public void onInputFinished(String password) {
                password1 = password;
                isFirstKeyborad = false;
            }
        });
        payEditText2.setOnInputFinishedListener(new PayEditText.OnInputFinishedListener() {
            @Override
            public void onInputFinished(String password) {
                password2 = password;
            }
        });
    }

    private void _insertTransPWtoServer() {
        RequestParams params = new RequestParams(Url.INSERT_TRANSFER_PW_URL);
        params.addBodyParameter("userTransPassword", password2);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, result);
                Message msg = new Message();
                msg.obj = result;
                mHandler.sendMessage(msg);
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
