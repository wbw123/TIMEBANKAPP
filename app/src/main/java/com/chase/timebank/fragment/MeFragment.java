package com.chase.timebank.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chase.timebank.MeRequestActivity;
import com.chase.timebank.R;
import com.chase.timebank.UserInfoActivity;
import com.chase.timebank.global.Url;
import com.chase.timebank.util.GlobalVariables;
import com.chase.timebank.util.SpUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 创建者     Vincent
 * 创建时间   2016/7/8 23:49
 * 描述	     我模块
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class MeFragment extends BaseFragment implements View.OnClickListener {
    private static final int QUERY_USER_CURRENCY = 101;
    private LinearLayout ll_request;
    private LinearLayout ll_service;
    private LinearLayout ll_transfer;
    private LinearLayout ll_clean;
    private LinearLayout ll_feedback;
    private LinearLayout ll_setting;
    private CircleImageView civ_me_pic;
    private TextView tv_userAccount;

    private static final String TAG = "MeFragment";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case QUERY_USER_CURRENCY:
                    String obj = (String) msg.obj;
                    Log.i(TAG, obj);
                    break;
            }
        }
    };
    private String mUserAccount;

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_me, null);

        ll_request = view.findViewById(R.id.ll_request);
        ll_service = view.findViewById(R.id.ll_service);
        ll_transfer = view.findViewById(R.id.ll_transfer);
        ll_clean = view.findViewById(R.id.ll_clean);
        ll_feedback = view.findViewById(R.id.ll_feedback);
        ll_setting = view.findViewById(R.id.ll_setting);
        civ_me_pic = view.findViewById(R.id.civ_me_pic);
        tv_userAccount = view.findViewById(R.id.tv_me_userAccount);

        mUserAccount = mActivity.getUserAccount();
        tv_userAccount.setText(mUserAccount);

        ll_request.setOnClickListener(this);
        ll_service.setOnClickListener(this);
        ll_transfer.setOnClickListener(this);
        ll_clean.setOnClickListener(this);
        ll_feedback.setOnClickListener(this);
        ll_setting.setOnClickListener(this);
        civ_me_pic.setOnClickListener(this);
        return view;
    }
    @Override
    public void initData() {
        _getUserCurrency();
    }

    private void _getUserCurrency() {
        RequestParams params = new RequestParams(Url.QUERY_USER_CURRENCY);
//        params.addBodyParameter("userAccount", mUserAccount);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Message msg = new Message();
                msg.what = QUERY_USER_CURRENCY;
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

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.civ_me_pic:
                Intent intent1 = new Intent(mActivity, UserInfoActivity.class);
                mActivity.startActivity(intent1);
                break;
            case R.id.ll_request:
                Intent intent2 = new Intent(mActivity, MeRequestActivity.class);
                intent2.putExtra("me_userAccount", mUserAccount);
                mActivity.startActivity(intent2);
                break;
            case R.id.ll_service:

                break;
            case R.id.ll_transfer:
                break;
            case R.id.ll_clean:
                cleanCache();
                break;
            case R.id.ll_feedback:
                break;
            case R.id.ll_setting:
                break;

        }
    }

    private void cleanCache() {
        String pkName = getContext().getPackageName();
        Intent intent = new Intent(
                "android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.parse("package:" + pkName));
        startActivity(intent);
    }



    @Override
    public void onResume() {
        System.out.println("MeFragment onResume");
        super.onResume();
    }
    //从本地获取用户头像
    private void showIcon() {
        //显示用户头像
        String avatar_path = SpUtil.getString(mActivity, GlobalVariables.USER_AVATAR_FILE_PATH);
        if (!"".equals(avatar_path) && isFileExist(avatar_path)) {
            Bitmap bitmap = BitmapFactory.decodeFile(avatar_path);
            civ_me_pic.setImageBitmap(bitmap);
        }
    }
    public static boolean isFileExist(String icon_path) {
        File file = new File(icon_path);
        if (file.exists()) {
            return true;
        }
        return false;
    }
}
