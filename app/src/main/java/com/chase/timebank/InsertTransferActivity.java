package com.chase.timebank;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.chase.timebank.bean.ResultModel;
import com.chase.timebank.global.Url;
import com.chase.timebank.util.JsonResolveUtils;
import com.chase.timebank.util.ToastUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InsertTransferActivity extends AppCompatActivity {
    @BindView(R.id.et_tran_account)
    EditText mTranAccount;
    @BindView(R.id.et_tran_num)
    EditText mTranNum;
    @BindView(R.id.et_tran_desp)
    EditText mTranDesp;
    @BindView(R.id.btn_tran_ok)
    Button mTranOk;
    @BindView(R.id.btn_tran_back)
    Button mTranBack;

    private static final String TAG = "InsertTransferActivity";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = (String) msg.obj;
            //解析json数据
            ResultModel resultModel = JsonResolveUtils.parseJsonToBean(result, ResultModel.class);
            Log.i(TAG, "json解析：" + resultModel.toString());

            switch (resultModel.getCode()) {
                case 21://请先设置支付密码
                    ToastUtils.ToastShort(getApplicationContext(), resultModel.getMsg());
                    startActivity(new Intent(InsertTransferActivity.this, TransferPasswordEditActivity.class));
                    break;
                case 22://跳转到输入支付密码页面
                    Intent intent = new Intent(InsertTransferActivity.this, TransferPasswordActivity.class);
                    intent.putExtra("transToUserAccount", mTranAccount.getText().toString());
                    intent.putExtra("transCurrency", mTranNum.getText().toString());
                    intent.putExtra("transDesp", mTranDesp.getText().toString());
                    startActivity(intent);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_transfer);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_tran_ok, R.id.btn_tran_back})
    public void clickCase(View view) {
        switch (view.getId()) {
            case R.id.btn_tran_ok:
                if (mTranAccount.getText().toString().equals("") || mTranNum.getText().toString().equals("")) {
                    ToastUtils.ToastLong(getApplication(), "对方账号名或待转时间币不能为空");
                } else {
                    _checkIfSetTransPW();
                }
                break;
            case R.id.btn_tran_back:
                finish();
                break;
        }
    }

    private void _checkIfSetTransPW() {
        RequestParams params = new RequestParams(Url.QUERT_TRANSFER_PW_URL);
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
