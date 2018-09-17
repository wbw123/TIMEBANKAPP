package com.chase.timebank.fragment;

import android.support.design.widget.TabLayout;
import android.view.View;

import com.chase.timebank.R;
import com.chase.timebank.adapter.tabAdapter.TransferAdapter;
import com.chase.timebank.view.CustomViewPager;

/**
 * 创建者     Vincent
 * 创建时间   2016/7/8 23:49
 * 描述	      问答模块
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class TransferFragment extends BaseFragment {

    private final String[] transfertTitle = {"收款列表", "汇款列表"};

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_transfer, null);
        TabLayout mTabTranTitle = view.findViewById(R.id.tab_transfer_title);
        CustomViewPager mVPTranContent = view.findViewById(R.id.vp_transfer_content);
        /*bind adapter*/
        mVPTranContent.setAdapter(new TransferAdapter(getChildFragmentManager(), transfertTitle));
        mTabTranTitle.setupWithViewPager(mVPTranContent);
        return view;
    }
}
