package com.chase.timebank;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private ActionBar mActionBar;
    private String title = "TIME_BANK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(attachLayoutRes());
        ButterKnife.bind(this);
        initActionBar();
        setToolbarTitle(title);
        initViews();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * 绑定布局文件
     *
     * @return 布局文件ID
     */
    @LayoutRes
    protected abstract int attachLayoutRes();

    /**
     * 初始化Actionbar
     */
    private void initActionBar() {
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        /*toolbar左上角图标是否可以显示、是否可点击*/
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);

        /*Drawer和Toolbar的联动*/
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }

    /**
     * 设置toolbar标题
     */
    public void setToolbarTitle(String title){
        mActionBar.setTitle(title);
    }
    /**
     * 初始化视图控件
     */
    protected abstract void initViews();
    /**
     * 初始化数据, 子类可以不实现
     */
    public void initData() {}

}
