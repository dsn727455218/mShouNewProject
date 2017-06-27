package com.shownew.home.activity.shouniushop;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.activity.shop.ShopDetailActivity;
import com.shownew.home.adapter.ShopHeaderAdapter;
import com.shownew.home.adapter.ShopHomeAdapter;
import com.shownew.home.module.ShopAPI;
import com.shownew.home.module.entity.HomeAdverEntity;
import com.shownew.home.module.entity.ShopMaillTypeEntity;
import com.shownew.home.module.entity.ShopMallListEntity;
import com.shownew.home.utils.GlideImageLoader;
import com.wp.baselib.utils.JsonUtils;
import com.wp.baselib.widget.TitleBarView;
import com.wp.baselib.widget.banner.Banner;
import com.wp.baselib.widget.banner.BannerConfig;
import com.wp.baselib.widget.banner.Transformer;
import com.wp.baselib.widget.banner.listener.OnBannerListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;



public class ShoppingMallActivity extends BaseActivity implements View.OnClickListener {
    private XRecyclerView mRecyclerView;
    private ShopHomeAdapter mDataAdapter;
    private ShopAPI mShopAPI;
    private ShopHeaderAdapter mHeaderAdapter;
    private EditText mShopSearchContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_mall);
        mShopAPI = new ShopAPI(mShouNewApplication);
        initViews();
        getTypeList();
    }

    private boolean isRefresh;

    private void initViews() {
        mRecyclerView = (XRecyclerView) findViewById(R.id.supermarket_list_recyclerView);
        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        titleBarView.setTitle("首牛商城");
        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setOnLeftOnClickListener(this);
        titleBarView.setLeftIcon(R.drawable.back_arrow);
        titleBarView.setTitleSize(20);


        mDataAdapter = new ShopHomeAdapter(mShouNewApplication, mShopMallListEntities);
        mRecyclerView.setAdapter(mDataAdapter);
        mDataAdapter.setShopHomeLisener(new ShopHomeAdapter.ShopHomeLisener() {
            @Override
            public void clickShopItem(String shopId) {
                Bundle bundle = new Bundle();
                bundle.putString("shopId", shopId);
                bundle.putInt("typeId", 0);
                mShouNewApplication.redirectAndPrameter(ShopMallDetailActivity.class, bundle);
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mRecyclerView.setArrowImageView(R.drawable.refresh_arrow);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mRecyclerView.addHeaderView(getHeaderView());
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
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
        getActionAdv();
        refresh();
    }

    private void refresh() {
        isRefresh = true;
        page = 1;
        getProductList();
    }

    private Banner mBanner;

    private View getHeaderView() {
        View hearderView = getLayoutInflater().inflate(R.layout.layout_supermarket_home_header, null);
        mBanner = (Banner) hearderView.findViewById(R.id.banner);
        hearderView.findViewById(R.id.search_shop).setOnClickListener(this);
        mShopSearchContent = (EditText) hearderView.findViewById(R.id.shop_search);
        mShopSearchContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    //do something;
                    search();
                    return true;
                }
                return false;

            }
        });

        initBanner();
        RecyclerView headerRecylerView = (RecyclerView) hearderView.findViewById(R.id.shop_recyclerView);
        initRecylerViewDate(headerRecylerView);
        return hearderView;
    }

    private void search() {
        String searchContent = mShopSearchContent.getText().toString();
        Bundle bundle = new Bundle();
        bundle.putString("keyWord", searchContent);
        bundle.putInt("type", 0);
        mainApplication.redirectAndPrameter(ShopMallSearchShopActivity.class, bundle);
    }

    private void initRecylerViewDate(RecyclerView headerRecylerView) {
        mHeaderAdapter = new ShopHeaderAdapter(this, mShopTypeEntities);
        headerRecylerView.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
        mHeaderAdapter.setShopHeaderClickLisener(new ShopHeaderAdapter.ShopHeaderClickLisener() {
            @Override
            public void click(ShopMaillTypeEntity shopMaillTypeEntity) {
                if (null != shopMaillTypeEntity) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("shop_type", shopMaillTypeEntity.getMtId());
                    bundle.putString("shop_title", shopMaillTypeEntity.getMtName());
                    mShouNewApplication.redirectAndPrameter(ShopMallVehicleSaleActivity.class, bundle);
                }
            }
        });
        headerRecylerView.setAdapter(mHeaderAdapter);
    }

    private void initBanner() {


        //设置banner样式
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
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

        }
    }

    private int page = 1;
    private ArrayList<Object> mShopMallListEntities = new ArrayList<Object>();

    private void getProductList() {
        mShopAPI.getShopMallProductList(0, page, "", mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (isRefresh) {
                    mRecyclerView.refreshComplete();
                    if (mShopMallListEntities.size() > 0) {
                        mShopMallListEntities.clear();
                    }
                } else {
                    mRecyclerView.loadMoreComplete();
                }
                if (exception == null) {
                    if (json.has("data")) {
                        try {
                            JSONObject jsonData = json.getJSONObject("data");
                            if (jsonData.has("mallproductList")) {
                                String productList = jsonData.getString("mallproductList");
                                ArrayList<ShopMallListEntity> superMarketEntities = JsonUtils.fromJson(productList, new TypeToken<ArrayList<ShopMallListEntity>>() {
                                }.getType());
                                if (superMarketEntities != null && superMarketEntities.size() > 0) {
                                    mShopMallListEntities.addAll(superMarketEntities);
                                    page++;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (mShopMallListEntities.size() > 0) {
                        mRecyclerView.setNoMore(true);
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

    private ArrayList<ShopMaillTypeEntity> mShopTypeEntities = new ArrayList<ShopMaillTypeEntity>();

    /**
     * 获取商品类别
     */
    private void getTypeList() {
        mShopAPI.getShopMallTypeList(mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (exception == null) {
                    if (json.has("data")) {
                        try {
                            JSONObject jsonObject = json.getJSONObject("data");
                            if (jsonObject.has("malltypeList")) {
                                if (mShopTypeEntities.size() > 0) {
                                    mShopTypeEntities.clear();
                                }
                                ArrayList<ShopMaillTypeEntity> shopTypeEntities = JsonUtils.fromJson(jsonObject.getString("malltypeList"), new TypeToken<ArrayList<ShopMaillTypeEntity>>() {
                                }.getType());
                                if (shopTypeEntities != null && shopTypeEntities.size() > 0) {
                                    mShopTypeEntities.addAll(shopTypeEntities);
                                    mHeaderAdapter.notifyDataSetChanged();
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
