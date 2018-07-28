package com.chase.timebank;

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
            if (resultModel.getCode() == 1) {
                Log.i(TAG, resultModel.getMsg());
                finish();
            }else {
                Log.i(TAG, "汇款提交失败！");
                ToastUtils.ToastLong(getApplicationContext(),"数据库异常，请稍后重试！");
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
                _insertTransfer();
                break;
            case R.id.btn_tran_back:
                finish();
                break;
        }
    }

    private void _insertTransfer() {
        RequestParams params = new RequestParams(Url.INSERT_TRANSFER_URL);
        params.addBodyParameter("transToUserGuid", mTranAccount.getText().toString());
        params.addBodyParameter("transCurrency", mTranNum.getText().toString());
        params.addBodyParameter("transDesp", mTranDesp.getText().toString());
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
