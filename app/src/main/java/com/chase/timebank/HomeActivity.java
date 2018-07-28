package com.chase.timebank;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.chase.timebank.bean.BottomTabItem;
import com.chase.timebank.util.ToastUtils;

import butterknife.BindView;

public class HomeActivity extends BaseActivity implements View.OnTouchListener, TabHost.OnTabChangeListener, View.OnClickListener ,NavigationView.OnNavigationItemSelectedListener{
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.fragmentTabHost)
    FragmentTabHost mFragmentTabHost;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    private static final String TAG = "HomeActivity";

    private TextView mNavUserAccount;
    private LinearLayout mNavHeader;
    // 用来计算返回键的点击间隔时间
    private long mExitTime = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        }
    };

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_home;
    }

    @Override
    public void setToolbarTitle(String title) {
        super.setToolbarTitle(title);
    }

    @Override
    protected void initViews() {
//        保证NavigationView中menu图片保持原色
        mNavView.setItemIconTintList(null);
        //初始化FragmentTabHost
        //绑定布局
        mFragmentTabHost.setup(this, getSupportFragmentManager(), R.id.bottom_content);
        //Build.VERSION.SDK_INT > 10这个是用来适配低版本的 不用管
        if (Build.VERSION.SDK_INT > 10) {
            //好像是把按钮之间的间隔线去除  你再查查
            mFragmentTabHost.getTabWidget().setShowDividers(0);
            _initFTH();
            //这两行是保证一进入应用就跳到第一个页面
            mFragmentTabHost.setCurrentTab(0);
            mFragmentTabHost.setOnTabChangedListener(this);
        }
        //获取NavigationView中的header View
        /*这里是个大坑 记得把xml中app:headerLayout删除 别用黄油刀 坑死我了*/
        View headerView = mNavView.inflateHeaderView(R.layout.nav_header_home);
        mNavUserAccount = headerView.findViewById(R.id.nav_userAccount);
        mNavHeader = headerView.findViewById(R.id.nav_header);
        mNavHeader.setOnClickListener(this);

        mNavView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void initData() {
        super.initData();
        Intent intent = getIntent();
        String userAccount = intent.getStringExtra("userAccount");
        ToastUtils.ToastShort(this, userAccount + "登录了");
        //设置用户账号
        if (userAccount == null) {
            mNavUserAccount.setText("husky");
        } else {
            mNavUserAccount.setText(userAccount);
        }
    }


    private void _initFTH() {
        //这个是枚举 你自己这道BottomTabItem.class去看看  一共包含五个枚举 应该也可以叫做对象，这个我用的少
        //每一个对象里面包含4个参数：序号、标题、图片和每个fragmenttabhost对应的fragment对象
        BottomTabItem[] bottomTabs = BottomTabItem.values();//获取所有枚举（对象）
        final int size = bottomTabs.length;//一共有几个  （不用看就是5个）
        for (int i = 0; i < size; i++) {//开始遍历
            // 找到每一个枚举（对象）
            BottomTabItem bottomTab = bottomTabs[i];

            // 1. 创建一个新的选项卡（需要传入一个参数，这里传入的是标题）
            TabHost.TabSpec tab = mFragmentTabHost.newTabSpec(String.valueOf(bottomTab.getResName()));

            //bottom_tab_indicator这个文件忘了说了，这个是每个fragmenttabhost的布局
            View indicator = LayoutInflater.from(this).inflate(R.layout.bottom_tab_indicator, null);

            TextView title = (TextView) indicator.findViewById(R.id.tab_title);//findViewById不多说了

            Drawable drawable = this.getResources().getDrawable(bottomTab.getResIcon());

            //setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom)
            //设置图片显示位置 上下左右 这里设置显示在上面
            title.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null,
                    null);
            title.setText(getString(bottomTab.getResName()));//设置标题
            tab.setIndicator(indicator);//感觉类似绑定  就是把设置好标题和图片的布局indicator和创建的选项卡绑定
            //这个应该是这个东西在哪个activity上创建的
            tab.setContent(new TabHost.TabContentFactory() {
                @Override
                public View createTabContent(String tag) {
                    return new View(HomeActivity.this);
                }
            });



            //下面这些代码还是绑定  就是每个tabhost和对应的fragment绑定
            //Bundle传递数据
            Bundle bundle = new Bundle();
            bundle.putString("key", String.valueOf(bottomTab.getResName()));


            // 2. 把新的选项卡添加到TabHost中
            mFragmentTabHost.addTab(tab, bottomTab.getClz(), bundle);

            //触摸监听事件 对应下面的onTabChanged和onTouch  默认就可以
            mFragmentTabHost.getTabWidget().getChildAt(i).setOnTouchListener(this);
        }
    }


    /**
     * 如果drawerlayout没有关闭，则关闭
     * 如果drawerlayout已经关闭，则点击两次退出
     */
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            _exit();
        }
    }

    /**
     * 点击两次退出程序
     */
    private void _exit() {
        if (System.currentTimeMillis() - mExitTime > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    public void onTabChanged(String tabId) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_header:
                Log.i(TAG, "nav 头布局被点击了");
                startActivity(new Intent(HomeActivity.this,UserInfoActivity.class));
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_1:
                Intent intent1 = new Intent(this, InsertRequestActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_2:
                Intent intent2 = new Intent(this, InsertTransferActivity.class);
                startActivity(intent2);
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
