package com.chase.timebank.fragment;

import android.support.design.widget.TabLayout;
import android.view.View;

import com.chase.timebank.R;
import com.chase.timebank.adapter.tabAdapter.RequestAdapter;
import com.chase.timebank.view.CustomViewPager;

/**
 * 创建者     Vincent
 * 创建时间   2016/7/8 23:49
 * 描述	      动弹模块
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class RequestFragment extends BaseFragment {
    private final String[] requestTitle = {"最新需求", "附近需求", "我的需求", "我的服务"};

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_request, null);
        TabLayout mTabReqTitle = view.findViewById(R.id.tab_request_title);
        CustomViewPager mVPReqContent = view.findViewById(R.id.vp_request_content);
        /*bind adapter*/
        mVPReqContent.setAdapter(new RequestAdapter(getChildFragmentManager(), requestTitle));
        mTabReqTitle.setupWithViewPager(mVPReqContent);
        mVPReqContent.setOffscreenPageLimit(3);
        return view;
    }
}
