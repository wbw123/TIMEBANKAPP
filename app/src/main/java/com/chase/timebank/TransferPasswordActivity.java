package com.chase.timebank;

import android.content.Intent;
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

/**
 * Created by chase on 2018/8/30.
 */
public class TransferPasswordActivity extends AppCompatActivity {
    private static final String[] KEY = new String[]{
            "1", "2", "3",
            "4", "5", "6",
            "7", "8", "9",
            "<<", "0", "完成"
    };
    private static final String TAG = "TransferPWActivity";
    private PayEditText payEditText;
    private Keyboard keyboard;
    private TextView mTvTitle;
    private ImageView mIvBack;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = (String) msg.obj;
            //解析json数据
            ResultModel resultModel = JsonResolveUtils.parseJsonToBean(result, ResultModel.class);
            Log.i(TAG, "json解析：" + resultModel.toString());

            switch (resultModel.getCode()) {
                case 1://汇款提交成功，等待审核
                    ToastUtils.ToastLong(getApplicationContext(), resultModel.getMsg());
                    finish();
                    break;
                case 11://所持时间币不足
                    ToastUtils.ToastLong(getApplicationContext(), resultModel.getMsg());
                    finish();
                    break;
                case 12://密码错误，请重新输入
                    payEditText.remove();
                    ToastUtils.ToastLong(getApplicationContext(), resultModel.getMsg());
//                default:
//                    ToastUtils.ToastLong(getApplicationContext(), "服务器异常，请稍后重试！");
//                    finish();
                    break;
            }


        }
    };
    private String mTransToUserAccount;
    private String mTransCurrency;
    private String mTransDesp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_password);
        initView();
        setSubView();
        initEvent();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        mTransToUserAccount = intent.getStringExtra("transToUserAccount");
        mTransCurrency = intent.getStringExtra("transCurrency");
        mTransDesp = intent.getStringExtra("transDesp");
    }

    private void initView() {
        payEditText = (PayEditText) findViewById(R.id.PayEditText_pay);
        keyboard = (Keyboard) findViewById(R.id.KeyboardView_pay);

        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvBack.setVisibility(View.VISIBLE);
        mTvTitle.setText("支付密码");
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
                if (position < 11 && position != 9) {
                    payEditText.add(value);
                } else if (position == 9) {
                    payEditText.remove();
                } else if (position == 11) {
                    //当点击完成的时候，也可以通过payEditText.getText()获取密码，此时不应该注册OnInputFinishedListener接口
                    String transPassword = payEditText.getText();
                    _insertTransfer(transPassword);
                }
            }
        });

        /**
         * 当密码输入完成时的回调
         */
        payEditText.setOnInputFinishedListener(new PayEditText.OnInputFinishedListener() {
            @Override
            public void onInputFinished(String password) {
                Toast.makeText(getApplication(), "您的密码是：" + password, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void _insertTransfer(String transPassword) {
        RequestParams params = new RequestParams(Url.INSERT_TRANSFER_URL);
        params.addBodyParameter("transToUserAccount", mTransToUserAccount);
        params.addBodyParameter("transCurrency", mTransCurrency);
        params.addBodyParameter("transDesp", mTransDesp);
        params.addBodyParameter("userTransPassword", transPassword);
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
                ToastUtils.ToastShort(getApplication(),"服务器忙，请稍后重试！");
                finish();
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
