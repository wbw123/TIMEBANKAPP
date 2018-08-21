package com.chase.timebank.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chase.timebank.HomeActivity;

/**
 * Created by chase on 2017/7/27.
 */

public abstract class BaseFragment extends Fragment {

    public HomeActivity mActivity;

    // Fragment被创建
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取所在的activity对象
        mActivity = (HomeActivity) getActivity();
    }

    // 初始化Fragment布局
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = initView();
        return view;
    }

    // activity创建结束,初始化
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }


    /**
     * 初始化布局, 子类必须实现
     */
    public abstract View initView();

    /**
     * 初始化数据, 子类可以不实现
     */
    public void initData() {

    }

}
