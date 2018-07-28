package com.chase.timebank.fragment;

import android.view.View;
import android.widget.Button;

import com.chase.timebank.R;

/**
 * 创建者     Vincent
 * 创建时间   2016/7/8 23:49
 * 描述	     综合模块
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener{


    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_home, null);
        Button btn1 = view.findViewById(R.id.btn1);
        Button btn2 = view.findViewById(R.id.btn2);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                System.out.println("btn1 click");
//                getDataFromServer1();
                break;
            case R.id.btn2:
                System.out.println("btn2 click");
//                getDataFromServer2();
                break;
        }
    }

    /*public  void getDataFromServer1() {
        //xutil test
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("userAccount","yonghu");
        params.addBodyParameter("userPassword","123");
        httpUtils.send(HttpMethod.POST, "http://192.168.141.81:8080/loginUser", params,new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                System.out.println("请求成功");
                String result = responseInfo.result;
                System.out.println(result);
            }

            @Override
            public void onFailure(HttpException e, String msg) {
                System.out.println("请求失败");
                System.out.println("erro:"+e+"; msg:" + msg);
            }
        });
    }
    public void getDataFromServer2() {
        //xutil test
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("userAccount","yonghu");
        params.addBodyParameter("userPassword","123");
        httpUtils.send(HttpMethod.POST, "http://192.168.141.81:8080/appLoginUser", params,new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                System.out.println("请求成功");
                String result = responseInfo.result;
                System.out.println(result);
            }

            @Override
            public void onFailure(HttpException e, String msg) {
                System.out.println("请求失败");
                System.out.println("erro:"+e+"; msg:" + msg);
            }
        });
    }*/
}
