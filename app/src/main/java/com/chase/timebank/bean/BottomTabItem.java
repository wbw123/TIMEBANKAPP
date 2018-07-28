package com.chase.timebank.bean;

import com.chase.timebank.fragment.MeFragment;
import com.chase.timebank.fragment.RequestFragment;
import com.chase.timebank.fragment.HomeFragment;
import com.chase.timebank.fragment.TransferFragment;
import com.chase.timebank.fragment.ActivityFragment;
import com.chase.timebank.R;

/**
 * Created by chase on 2018/4/16.
 */

public enum BottomTabItem {
    HOME(0, R.string.bottom_tab_home, R.drawable.selector_bottom_tab_home,
            HomeFragment.class),

    REQUEST(1, R.string.bottom_tab_request, R.drawable.selector_bottom_tab_request,
            RequestFragment.class),

    ACTIVITY(2, R.string.bottom_tab_activity, R.drawable.selector_bottom_tab_activity,
            ActivityFragment.class),

    MONEY(3, R.string.bottom_tab_money, R.drawable.selector_bottom_tab_money,
            TransferFragment.class),

    ME(4, R.string.bottom_tab_me, R.drawable.selector_bottom_tab_me,
            MeFragment.class);

    private int idx;
    private int resName;
    private int resIcon;
    private Class<?> clz;

    private BottomTabItem(int idx, int resName, int resIcon, Class<?> clz) {
        this.idx = idx;
        this.resName = resName;
        this.resIcon = resIcon;
        this.clz = clz;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getResName() {
        return resName;
    }

    public void setResName(int resName) {
        this.resName = resName;
    }

    public int getResIcon() {
        return resIcon;
    }

    public void setResIcon(int resIcon) {
        this.resIcon = resIcon;
    }

    public Class<?> getClz() {
        return clz;
    }

    public void setClz(Class<?> clz) {
        this.clz = clz;
    }
}
