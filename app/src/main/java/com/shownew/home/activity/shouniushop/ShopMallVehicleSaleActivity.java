package com.shownew.home.activity.shouniushop;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.shownew.home.Config;
import com.shownew.home.R;
import com.shownew.home.activity.msg.AllMsgActivity;
import com.shownew.home.activity.MainActivity;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.activity.shop.ShopDetailActivity;
import com.shownew.home.adapter.ShopHomeAdapter;
import com.shownew.home.module.ShopAPI;
import com.shownew.home.module.entity.HomeAdverEntity;
import com.shownew.home.module.entity.ShopMallListEntity;
import com.shownew.home.utils.GlideImageLoader;
import com.shownew.home.utils.dialog.ShareDialog;
import com.shownew.home.utils.dialog.ShopPopwindow;
import com.wp.baselib.utils.JsonUtils;
import com.wp.baselib.utils.Preference;
import com.wp.baselib.widget.TitleBarView;
import com.wp.baselib.widget.banner.Banner;
import com.wp.baselib.widget.banner.BannerConfig;
import com.wp.baselib.widget.banner.Transformer;
import com.wp.baselib.widget.banner.listener.OnBannerListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;

/**
 * 整车特卖
 */
public class ShopMallVehicleSaleActivity extends BaseActivity implements View.OnClickListener {

    private XRecyclerView mXRecyclerView;
    private ShopHomeAdapter mDataAdapter;
    private ShopAPI mShopAPI;
    private ArrayList<Object> mSuperMarketEntities = new ArrayList<Object>();
    private EditText mShop_search;
    private TitleBarView mTitleBarView;
    private String mShop_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_sale);
        mShopAPI = new ShopAPI(mShouNewApplication);
        if (mBundle != null) {
            type = mBundle.getInt("shop_type", 0);
            mShop_title = mBundle.getString("shop_title");
        }
        initViews();
    }

    private boolean isRefresh;

    private void initViews() {

        mTitleBarView = (TitleBarView) findViewById(R.id.headbar);
        mTitleBarView.setTitle(mShop_title);
        mTitleBarView.setTitleTextColor(R.color.color_title);
        mTitleBarView.setMoreIcon(R.drawable.gengduo);
        mTitleBarView.setOnMoreClickListener(this);
        mTitleBarView.setOnLeftOnClickListener(this);
        mTitleBarView.setLeftIcon(R.drawable.back_arrow);
        mTitleBarView.setTitleSize(20);
        mXRecyclerView = (XRecyclerView) findViewById(R.id.vehicle_sale_recyclerView);
        mDataAdapter = new ShopHomeAdapter(mShouNewApplication, mSuperMarketEntities);
        mXRecyclerView.setAdapter(mDataAdapter);
        mXRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mXRecyclerView.setRefreshProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mXRecyclerView.setArrowImageView(R.drawable.refresh_arrow);
        mXRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mDataAdapter.setShopHomeLisener(new ShopHomeAdapter.ShopHomeLisener() {
            @Override
            public void clickShopItem(String shopId) {
                Bundle bundle = new Bundle();
                bundle.putString("shopId", shopId);
                bundle.putInt("typeId", type);
                mShouNewApplication.redirectAndPrameter(ShopMallDetailActivity.class, bundle);
            }
        });
        mXRecyclerView.addHeaderView(getHeaderView());
        mXRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                refresh();
            }

            @Override
            public void onLoadMore() {
                isRefresh = false;
                getProductList();
            }
        });
    }

    private Banner mBanner;

    private View getHeaderView() {
        View hearderView = getLayoutInflater().inflate(R.layout.layout_supermarket_home_header, null);
        mBanner = (Banner) hearderView.findViewById(R.id.banner);
        mShop_search = (EditText) hearderView.findViewById(R.id.shop_search);
        mShop_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    search();
                    return true;
                }
                return false;
            }
        });
        hearderView.findViewById(R.id.search_shop).setOnClickListener(this);
        hearderView.findViewById(R.id.shop_recyclerView).setVisibility(View.GONE);
        initBanner();
        return hearderView;
    }

    @Override
    protected void onResume() {
        super.onResume();
        createLoadingDialog();
        refresh();
        getActionAdv();
    }

    private void refresh() {
        page = 1;
        isRefresh = true;
        getProductList();
    }

    private void initBanner() {
        //设置banner样式
        //        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //        mBanner.  setIndicatorGravity(BannerConfig.CENTER);
        //设置图片加载器
        mBanner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        //        mBanner.setImages(images);
        //设置banner动画效果
        mBanner.setBannerAnimation(Transformer.Default);
        //设置标题集合（当banner样式有显示title时）
        //        mBanner.setBannerTitles(data);
        //设置自动轮播，默认为true
        mBanner.isAutoPlay(true);
        //设置轮播时间
        mBanner.setDelayTime(3000);

        //设置指示器位置（当banner模式中有指示器时）
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        //banner设置方法全部调用完毕时最后调用
        //        mBanner.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.backBtn:
                finish();
                break;
            case R.id.search_shop:
                search();
                break;
            case R.id.title_bar_more:
                new ShopPopwindow(this).showPopupWindow(mTitleBarView.getMoreBtn()).setPopClickLisener(new ShopPopwindow.PopClickLisener() {
                    @Override
                    public void clickPopItem(int position) {
                        switch (position) {
                            case 0:
                                if (!Preference.getBoolean(ShopMallVehicleSaleActivity.this, Preference.IS_LOGIN, false)) {
                                    mShouNewApplication.jumpLoginActivity(ShopMallVehicleSaleActivity.this);
                                    return;
                                }
                                mShouNewApplication.redirect(AllMsgActivity.class);
                                break;
                            case 1:
                                mainApplication.redirect(MainActivity.class);
                                finish();
                                break;
                            case 2:
                                new ShareDialog(ShopMallVehicleSaleActivity.this, mShouNewApplication).setCancelable(true).show();
                                break;
                        }
                    }
                });
                break;
        }
    }

    private void search() {
        String searchContent = mShop_search.getText().toString();
        Bundle bundle = new Bundle();
        bundle.putString("keyWord", searchContent);
        bundle.putInt("type", type);
        mainApplication.redirectAndPrameter(ShopMallSearchShopActivity.class, bundle);
    }

    private int type;
    private int page = 1;

    private void getProductList() {
        mShopAPI.getShopMallProductList(type, page, "", mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void onLoading() {
                super.onLoading();
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                closeLoadingDialog();
                if (isRefresh) {
                    mXRecyclerView.refreshComplete();
                } else {
                    mXRecyclerView.loadMoreComplete();
                }
                if (exception == null) {

                    if (json.has("data")) {
                        try {
                            if (isRefresh && mSuperMarketEntities.size() > 0) {
                                mSuperMarketEntities.clear();
                            }
                            JSONObject jsonData = json.getJSONObject("data");
                            if (jsonData.has("mallproductList")) {
                                String productList = jsonData.getString("mallproductList");
                                ArrayList<ShopMallListEntity> superMarketEntities = JsonUtils.fromJson(productList, new TypeToken<ArrayList<ShopMallListEntity>>() {
                                }.getType());
                                if (superMarketEntities != null && superMarketEntities.size() > 0) {
                                    mSuperMarketEntities.addAll(superMarketEntities);
                                    page++;
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {

                    if (mSuperMarketEntities.size() > 0) {
                        mXRecyclerView.setNoMore(true);
                    }
                }
                mDataAdapter.notifyDataSetChanged();
            }
        });
    }


    /**
     * 获取广告图片
     */
    private void getActionAdv() {
        mShopAPI.getLastAdList(3, mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected Object parseData(String result) {
                return null;
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (null == exception) {
                    if (json.has("data")) {
                        try {
                            JSONObject jsonData = json.getJSONObject("data");
                            if (jsonData.has("ad")) {
                                String ad = jsonData.getString("ad");
                                final ArrayList<HomeAdverEntity> mHomeAdverEntities = JsonUtils.fromJson(ad, new TypeToken<ArrayList<HomeAdverEntity>>() {
                                }.getType());
                                if (null != mHomeAdverEntities && mHomeAdverEntities.size() > 0) {
                                    ArrayList<String> images = new ArrayList<>();
                                    ArrayList<String> title = new ArrayList<>();
                                    for (HomeAdverEntity homeAdverEntity : mHomeAdverEntities) {
                                        images.add(homeAdverEntity.getAImg());
                                        title.add(homeAdverEntity.getAImg());
                                    }
                                    mBanner.setImages(images);
                                    //设置banner动画效果
                                    //设置标题集合（当banner样式有显示title时）
                                    mBanner.setBannerTitles(title);
                                    mBanner.setOnBannerListener(new OnBannerListener() {
                                        @Override
                                        public void OnBannerClick(int position) {
                                            HomeAdverEntity homeAdverEntity = mHomeAdverEntities.get(position);
                                            if (homeAdverEntity != null) {
                                                if (Config.ACTION_WEB.equals(homeAdverEntity.getaPid()) && Config.ACTION_WEB.equals(homeAdverEntity.getaMpid())) {
                                                    if (!TextUtils.isEmpty(homeAdverEntity.getaUrl())) {
                                                        mShouNewApplication.redirectWeb("", homeAdverEntity.getaUrl());
                                                    }
                                                } else {
                                                    Bundle bundle = new Bundle();
                                                    if ("0".equals(homeAdverEntity.getaMpid())) {
                                                        bundle.putString("shopId", homeAdverEntity.getaPid());
                                                        mShouNewApplication.redirectAndPrameter(ShopDetailActivity.class, bundle);
                                                    } else if ("0".equals(homeAdverEntity.getaPid())) {
                                                        bundle.putString("shopId", homeAdverEntity.getaMpid());
                                                        mShouNewApplication.redirectAndPrameter(ShopMallDetailActivity.class, bundle);
                                                    }
                                                }
                                            }
                                        }
                                    });
                                    mBanner.start();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }
            }
        });
    }
}
