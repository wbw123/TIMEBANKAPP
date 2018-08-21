package com.chase.timebank.news;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chase.timebank.R;
import com.chase.timebank.global.Url;
import com.chase.timebank.home.EMCAdapter;
import com.chase.timebank.home.EMCData;
import com.chase.timebank.home.TopEMCAdapter;
import com.chase.timebank.util.CacheUtils;
import com.chase.timebank.util.ToastUtils;
import com.chase.timebank.view.HorizontalScrollViewPager;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.viewpagerindicator.CirclePageIndicator;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "NewsActivity";
    private ListView mListView;
    private RefreshLayout mRefreshLayout;
    private RelativeLayout mRlCase;
    private RelativeLayout mRlNews;
    private RelativeLayout mRlTech;
    private static int count = 10;//用来记录第一次加载的条目数,以及在加载更多后加载的条目数
    private String mCache;//条目缓存数据 json字符串
    //    private int mFlContainerHeight;//轮播图控件高度
//    private int mLlContainerHeight;//mid控件高度
    /*handler*/
    private Handler mIndicatorHandler = null;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            String result = (String) msg.obj;
//                    mRlError.setVisibility(View.GONE);
//                    mListView.setVisibility(View.VISIBLE);
            //解析json数据
            processResult(result);
            //写缓存 将成功读取的json字符串写入XML中保存
            CacheUtils.setCache(Url.EMCJSON_URL, result, NewsActivity.this);


//                    Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();


        }
    };


    private ArrayList<EMCData.DataBean.EMCDataBean> mEMCListData;
    private EMCData mEMCData;
    private EMCAdapter mEMCAdapter;
    private ArrayList<EMCData.DataBean.TopEMCBean> mEMCTopData;
    private TopEMCAdapter mTopEMCAdapter;
    private HorizontalScrollViewPager mViewPager;
    private CirclePageIndicator mIndicator;
    private TextView mTopEMCTitle;
    private FrameLayout mFlContainer;
    private LinearLayout mLlContainer;
    private TextView mTvTop;
    private LinearLayout mLlRecom;
    private TextView mTvRecom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        initView();
        initData();
    }

    public void initView() {
//        View view = View.inflate(this, R.layout.fragment_home_emc, null);
        View topMiddleView = View.inflate(this, R.layout.fragment_home_emc_topview, null);
        View recommend = View.inflate(this, R.layout.fragment_recommend, null);

        mRefreshLayout = (RefreshLayout) findViewById(R.id.refresh_layout);
        mListView = (ListView) findViewById(R.id.lv_list);
        mTvTop = (TextView) findViewById(R.id.tv_top);
        mFlContainer = topMiddleView.findViewById(R.id.fl_viewpager);
        mLlContainer = topMiddleView.findViewById(R.id.ll_mid_container);
        mViewPager = topMiddleView.findViewById(R.id.emc_viewpager);
        mIndicator = topMiddleView.findViewById(R.id.indicator);
        mTopEMCTitle = topMiddleView.findViewById(R.id.emc_vp_title);
        mLlRecom = recommend.findViewById(R.id.ll_recommend);
        mTvRecom = recommend.findViewById(R.id.tv_recom);
        mRlCase = topMiddleView.findViewById(R.id.rl_case);
        mRlNews = topMiddleView.findViewById(R.id.rl_news);
        mRlTech = topMiddleView.findViewById(R.id.rl_tech);

        mRlCase.setOnClickListener(this);
        mRlNews.setOnClickListener(this);
        mRlTech.setOnClickListener(this);

        mListView.addHeaderView(topMiddleView);
        mListView.addHeaderView(recommend);

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 1) {
                    mTvTop.setVisibility(View.VISIBLE);
                    mTvRecom.setVisibility(View.INVISIBLE);
                } else if (firstVisibleItem == 0) {
                    mTvTop.setVisibility(View.GONE);
                    mTvRecom.setVisibility(View.VISIBLE);
                }
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*listview将'推荐图文'当做头布局加入,所以不可点击,否则角标越界异常*/
                if (position == 1) {
                    return;
                }

                /*点击条目标记已读状态*//*
                EMCDataBean emcDataBean = mEMCListData.get(position - 2);

                *//*条目跳转*//*
                Intent intent = new Intent(mActivity, WebViewActivity.class);
                intent.putExtra("url", Constants.HOME_URL+emcDataBean.url);//webView链接
                *//*收藏*//*
                intent.putExtra("title",emcDataBean.title);
                intent.putExtra("author",emcDataBean.author);
                intent.putExtra("date",emcDataBean.date);
                intent.putExtra("imgUrl1",emcDataBean.imgUrl1);
                intent.putExtra("imgUrl2",emcDataBean.imgUrl2);
                intent.putExtra("imgUrl3",emcDataBean.imgUrl3);
                intent.putExtra("from",emcDataBean.from);
                intent.putExtra("type",emcDataBean.type);
                intent.putExtra("id",emcDataBean.id);
                mActivity.startActivity(intent);

                //当前点击的item的标题颜色置灰
                TextView tvTitle = view.findViewById(R.id.tv_emc_title);
                TextView tvDate = view.findViewById(R.id.tv_emc_date);
                TextView tvAuthor = view.findViewById(R.id.tv_emc_author);
                TextView tvFrom = view.findViewById(R.id.tv_emc_from);
                tvTitle.setTextColor(Color.argb(255, 155, 155, 155));
                tvFrom.setTextColor(Color.argb(255, 155, 155, 155));
                tvAuthor.setTextColor(Color.argb(255, 155, 155, 155));
                tvDate.setTextColor(Color.argb(255, 155, 155, 155));
                //将已读状态持久化到本地
                //key:read_ids; value:id
                String readIds = PrefUtils.getString("read_ids", "", mActivity);
                if (!readIds.contains(emcDataBean.id)) {
                    readIds = readIds + emcDataBean.id + ",";
                    PrefUtils.putString("read_ids", readIds, mActivity);
                }*/
            }
        });

        System.out.println("home detail1 加载布局");
    }


    public void initData() {

        //获取缓存
        mCache = CacheUtils.getCache(Url.EMCJSON_URL, this);
        System.out.println("mCache:" + mCache);
        if (!TextUtils.isEmpty(mCache)) {
            System.out.println("有缓存  不自动刷新");
            processResult(mCache);
        } else {
            //触发自动刷新
            mRefreshLayout.autoRefresh();
            System.out.println("无缓存  自动刷新");
        }
        System.out.println("home emc 加载数据");


        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                System.out.println("home emc 下拉刷新 加载数据");
//                refreshlayout.finishRefresh(2000);
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        count = 10;
                        if (!TextUtils.isEmpty(mCache)) {
                            // 有缓存 解析json缓存
                            System.out.println("发现缓存....");
                            processResult(mCache);
                        }
                        //通过网络获取数据
                        getDataFromServer();
                        refreshlayout.finishRefresh();//完成刷新
                        refreshlayout.setLoadmoreFinished(false);//可以出发加载更多事件
                    }
                }, 1500);
            }
        });
        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(final RefreshLayout refreshlayout) {
                System.out.println("home detail1 下拉 加载数据");
//                refreshlayout.finishLoadmore(2000);
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        count += 10;
                        if (!TextUtils.isEmpty(mCache)) {
                            // 有缓存 解析json缓存
                            System.out.println("发现缓存....");
                            processResult(mCache);
                        }
                        // 即使发现有缓存,仍继续调用网络, 获取最新数据
                        //通过网络获取数据
                        getDataFromServer();
                        refreshlayout.finishLoadmore();//完成加载更多
                        if (count > mEMCListData.size()) {
                            Toast.makeText(NewsActivity.this, "数据全部加载完毕", Toast.LENGTH_SHORT).show();
                            refreshlayout.setLoadmoreFinished(true);//将不会再次触发加载更多事件
                        }
                    }
                }, 1500);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_case:
//                RadioButton rCaseBtn = getActivity().findViewById(R.id.rb_bottom_case);
//                rCaseBtn.performClick();
                break;
            case R.id.rl_news:
//                RadioButton rNewsBtn = getActivity().findViewById(R.id.rb_bottom_news);
//                rNewsBtn.performClick();
                break;
            case R.id.rl_tech:
//                Intent intent = new Intent(mActivity, TechActivity.class);
//                startActivity(intent);
                break;
        }
    }

    /**
     * 通过网络获取数据
     */
    public void getDataFromServer() {
        RequestParams params = new RequestParams(Url.EMCJSON_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Message msg = new Message();
                msg.obj = result;
                mHandler.sendMessage(msg);
                ToastUtils.ToastShort(NewsActivity.this, "onSuccess: result=" + result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "onError: " + ex + "  ," + isOnCallback);
                Toast.makeText(NewsActivity.this, "请检查是否连接网络!", Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(mCache)) {
                    // 有缓存 读取缓存数据 显示布局
                    System.out.println("发现缓存....");
                    processResult(mCache);
//                        mRlError.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                } else {
                    //没有缓存 添加加载失败布局
//                        mRlError.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i(TAG, "onCancelled: " + cex);
                ToastUtils.ToastShort(NewsActivity.this, "onCancelled: " + cex);
            }

            @Override
            public void onFinished() {
                Log.i(TAG, "onFinished");
            }
        });

    }

    /**
     * 解析json数据
     */
    private void processResult(String result) {
        Gson gson = new Gson();
        mEMCData = gson.fromJson(result, EMCData.class);
        System.out.println("mEMCData解析结果:" + mEMCData.toString());

        /*轮播图*/
        mEMCTopData = mEMCData.data.topEMC;
//        if (mTopEMCAdapter == null) {
        mTopEMCAdapter = new TopEMCAdapter(mEMCTopData, this);
        mViewPager.setAdapter(mTopEMCAdapter);
//        } else {
//            mTopEMCAdapter.notifyDataSetChanged();
//            System.out.println("刷新adapter notifyDataSetChanged");
//        }

        /*listview*/
        mEMCListData = mEMCData.data.EMCData;
//        if (mEMCAdapter == null) {
        mEMCAdapter = new EMCAdapter(this, mEMCListData);
        mListView.setAdapter(mEMCAdapter);
//        } else {
//            mEMCAdapter.notifyDataSetChanged();
//        }

        //indicator绑定ViewPager
        indicatorBindViewPager();

        //TopNews两秒切换一次
        autoChangeAfter2s();
    }

    /**
     * indicator绑定ViewPager
     */
    private void indicatorBindViewPager() {
        mIndicator.setViewPager(mViewPager);//将轮播图和指示器绑定
        mIndicator.setSnap(true);// 快照模式 取消则为粘性模式
//        mIndicator.onPageSelected(0);// 将小圆点位置归零, 解决它会在页面销毁时仍记录上次位置的bug
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //标题
                EMCData.DataBean.TopEMCBean topEMCBean = mEMCTopData.get(position);
                mTopEMCTitle.setText(topEMCBean.title);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //当前viewpager是哪一页就设置哪一页的标题
        int currentItem = mViewPager.getCurrentItem();
        mTopEMCTitle.setText(mEMCTopData.get(currentItem).title);
    }

    /**
     * TopNews两秒切换一次
     */
    private void autoChangeAfter2s() {
        if (mIndicatorHandler == null) {
            mIndicatorHandler = new Handler(Looper.getMainLooper()) {
                public void handleMessage(Message msg) {
                    int currentItem = mViewPager.getCurrentItem();

                    if (currentItem < mEMCTopData.size() - 1) {
                        currentItem++;
                    } else {
                        currentItem = 0;
                    }

                    mViewPager.setCurrentItem(currentItem);

                    mIndicatorHandler.sendEmptyMessageDelayed(0, 2000);
                }

                ;
            };

            //  延时2秒切换广告条
            mIndicatorHandler.sendEmptyMessageDelayed(0, 2000);

            mViewPager.setOnTouchListener(new View.OnTouchListener() {
                private float mDownX;
                private float mDownY;
                private long mDownTime;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            System.out.println("ACTION_DOWN");
                            mDownX = event.getX();
                            mDownY = event.getY();
                            mDownTime = System.currentTimeMillis();
                            // 删除所有消息
                            mIndicatorHandler.removeCallbacksAndMessages(null);
                            break;
                        case MotionEvent.ACTION_CANCEL:// 事件取消(当按下后,然后移动下拉刷新,导致抬起后无法响应ACTION_UP,
                            // 但此时会响应ACTION_CANCEL,也需要继续播放轮播条)
                        case MotionEvent.ACTION_UP:

                            float upX = event.getX();
                            float upY = event.getY();
                            long upTime = System.currentTimeMillis();
                            // 设置轮播图点击事件
                            if (mDownX == upX && mDownY == upY) {
                                if (upTime - mDownTime < 500) {
//                                onClickListener.onClick(mViewPager.getCurrentItem() % mTopnews.size());
//                                    int positon = mViewPager.getCurrentItem();
//                                    System.out.println("当前viewpager：" + positon);
//                                    Intent topIntent = new Intent(mActivity, WebViewActivity.class);
//                                    topIntent.putExtra("url", Constants.HOME_URL + mEMCTopData.get(positon).url);//webView链接
//                                    /*收藏*/
//                                    topIntent.putExtra("title", mEMCTopData.get(positon).title);
//                                    topIntent.putExtra("author", mEMCTopData.get(positon).author);
//                                    topIntent.putExtra("date", mEMCTopData.get(positon).date);
//                                    topIntent.putExtra("imgUrl", mEMCTopData.get(positon).imgUrl);
//                                    topIntent.putExtra("from", mEMCTopData.get(positon).from);
//                                    topIntent.putExtra("type", mEMCTopData.get(positon).type);
//                                    topIntent.putExtra("id", mEMCTopData.get(positon).id);
//                                    mActivity.startActivity(topIntent);
                                }
                            }
                            // 延时2秒切换广告条
                            mIndicatorHandler.sendEmptyMessageDelayed(0, 2000);
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });
        }
    }


    public static int getCount() {
        return count;
    }

}
