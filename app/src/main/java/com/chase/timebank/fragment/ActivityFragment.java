package com.chase.timebank.fragment;

import android.support.design.widget.TabLayout;
import android.view.View;

import com.chase.timebank.R;
import com.chase.timebank.adapter.tabAdapter.ActivityAdapter;
import com.chase.timebank.view.CustomViewPager;

/**
 * 创建者     Vincent
 * 创建时间   2016/7/8 23:49
 * 描述	      快速模块
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class ActivityFragment extends BaseFragment {
    TabLayout mTabActTitle;
    CustomViewPager mVPActContent;
    private final String[] activityTitle = {"活动列表", "我的活动"};
    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_activity, null);
        TabLayout mTabActTitle = view.findViewById(R.id.tab_activity_title);
        CustomViewPager mVPActContent = view.findViewById(R.id.vp_activity_content);
        /*bind adapter*/
        mVPActContent.setAdapter(new ActivityAdapter(getChildFragmentManager(),activityTitle));
        mTabActTitle.setupWithViewPager(mVPActContent);
        return view;
    }
}
