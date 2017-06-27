package com.shownew.home.activity.shop;

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
import com.shownew.home.activity.shouniushop.ShopMallDetailActivity;
import com.shownew.home.adapter.ShopHomeAdapter;
import com.shownew.home.adapter.SuperMarkHeaderAdapter;
import com.shownew.home.module.ShopAPI;
import com.shownew.home.module.entity.HomeAdverEntity;
import com.shownew.home.module.entity.ShopTypeEntity;
import com.shownew.home.module.entity.SuperMarketEntity;
import com.shownew.home.utils.GlideImageLoader;
import com.shownew.home.utils.dialog.BasePopwindow;
import com.wp.baselib.utils.JsonUtils;
import com.wp.baselib.widget.TitleBarView;
import com.wp.baselib.widget.banner.Banner;
import com.wp.baselib.widget.banner.BannerConfig;
import com.wp.baselib.widget.banner.Transformer;
import com.wp.baselib.widget.banner.listener.OnBannerListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;

import static com.lzy.okgo.OkGo.getContext;

/**
 * 车配超市
 */
public class SupermarketActivity extends BaseActivity implements View.OnClickListener {
    private XRecyclerView mRecyclerView;
    private ShopHomeAdapter mDataAdapter;
    private ShopAPI mShopAPI;
    private ArrayList<Object> mSuperMarketEntities = new ArrayList<Object>();
    private TitleBarView mTitleBarView;
    private EditText mShop_search;
    private SuperMarkHeaderAdapter mHeaderAdapter;
    private ArrayList<ShopTypeEntity> mShopTypeEntities = new ArrayList<ShopTypeEntity>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        mShopAPI = new ShopAPI(mShouNewApplication);
        initViews();
    }

    private boolean isRefresh;

    private void initViews() {
        mRecyclerView = (XRecyclerView) findViewById(R.id.shaop_list_recyclerView);

        mTitleBarView = (TitleBarView) findViewById(R.id.headbar);
        mTitleBarView.setTitle("车配超市");
        mTitleBarView.setTitleTextColor(R.color.color_title);
        mTitleBarView.getMoreBtn().setText("品牌");
        mTitleBarView.setOnMoreClickListener(this);
        mTitleBarView.setOnLeftOnClickListener(this);
        mTitleBarView.setLeftIcon(R.drawable.back_arrow);
        mTitleBarView.setTitleSize(20);


        mDataAdapter = new ShopHomeAdapter(mShouNewApplication, mSuperMarketEntities);
        mRecyclerView.setAdapter(mDataAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mRecyclerView.setArrowImageView(R.drawable.refresh_arrow);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mRecyclerView.addHeaderView(getHeaderView());
        mDataAdapter.setShopHomeLisener(new ShopHomeAdapter.ShopHomeLisener() {
            @Override
            public void clickShopItem(String shopId) {
                Bundle bundle = new Bundle();
                bundle.putString("shopId", shopId);
                bundle.putInt("typeId", 0);
                mShouNewApplication.redirectAndPrameter(ShopDetailActivity.class, bundle);
            }
        });
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
    }


    private Banner mBanner;

    private View getHeaderView() {
        View hearderView = getLayoutInflater().inflate(R.layout.layout_shop_home_header, null);
        mBanner = (Banner) hearderView.findViewById(R.id.banner);
        hearderView.findViewById(R.id.search_shop).setOnClickListener(this);
        mShop_search = (EditText) hearderView.findViewById(R.id.shop_search);
        mShop_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

    private void initRecylerViewDate(RecyclerView headerRecylerView) {


        mHeaderAdapter = new SuperMarkHeaderAdapter(getContext(), mShopTypeEntities);
        headerRecylerView.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
        mHeaderAdapter.setShopHeaderClickLisener(new SuperMarkHeaderAdapter.ShopHeaderClickLisener() {
            @Override
            public void click(ShopTypeEntity shopTypeEntity) {
                if (null != shopTypeEntity) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("shop_type", shopTypeEntity.getTId());
                    bundle.putString("shop_title", shopTypeEntity.getTName());
                    mShouNewApplication.redirectAndPrameter(VehicleSaleActivity.class, bundle);
                }
            }
        });
        headerRecylerView.setAdapter(mHeaderAdapter);
        getTypeList();
    }

    private void refresh() {
        page = 1;
        isRefresh = true;
        getProductList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
        getMyCarTypeList();
        getActionAdv();
    }

    private void initBanner() {

        //        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        //设置banner样式
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
                if (type != null && type.length > 0) {
                    new BasePopwindow(this, type).showPopupWindow(v).setPopClickLisener(new BasePopwindow.PopClickLisener() {
                        @Override
                        public void clickPopItem(String mark) {
                            upCarType4Mall(mark);
                        }
                    });
                }
                break;
        }
    }

    private void search() {
        String searchContent = mShop_search.getText().toString();
        Bundle bundle = new Bundle();
        bundle.putString("keyWord", searchContent);
        bundle.putInt("type", 0);
        mainApplication.redirectAndPrameter(SearchShopActivity.class, bundle);
    }

    private int page = 1;

    private void getProductList() {
        mShopAPI.getProductList(0, page, "", mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                //                mEmptyView.setEnabled(true);
                if (isRefresh) {
                    mRecyclerView.refreshComplete();
                    if (mSuperMarketEntities.size() > 0) {
                        mSuperMarketEntities.clear();
                    }
                } else {
                    mRecyclerView.loadMoreComplete();
                }
                if (exception == null) {
                    if (json.has("data")) {
                        try {

                            JSONObject jsonData = json.getJSONObject("data");
                            if (jsonData.has("productList")) {
                                String productList = jsonData.getString("productList");
                                ArrayList<SuperMarketEntity> superMarketEntities = JsonUtils.fromJson(productList, new TypeToken<ArrayList<SuperMarketEntity>>() {
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
        mShopAPI.getLastAdList(2, mShouNewApplication.new ShouNewHttpCallBackLisener() {
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

    private String[] type;

    private void getMyCarTypeList() {
        mShopAPI.getMyCarTypeList(mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (exception == null) {
                    if (json.has("data")) {
                        try {
                            JSONObject jsonObject = json.getJSONObject("data");
                            JSONArray jsonArray = jsonObject.getJSONArray("carTypeList");
                            int length = jsonArray.length();
                            type = new String[length];
                            for (int i = 0; i < length; i++) {
                                type[i] = (String) jsonArray.get(i);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }
                if (type == null || type.length == 0) {
                    mTitleBarView.getMoreBtn().setVisibility(View.GONE);
                }

            }
        });
    }

    private void upCarType4Mall(String mark) {
        mShopAPI.upCarType4Mall(mark, mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (exception == null) {
                    refresh();
                }
            }
        });
    }

    /**
     * 获取商品类别
     */
    private void getTypeList() {
        mShopAPI.getTypeList(mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (exception == null) {
                    if (json.has("data")) {
                        try {
                            JSONObject jsonObject = json.getJSONObject("data");
                            if (jsonObject.has("typeList")) {
                                if (mShopTypeEntities.size() > 0) {
                                    mShopTypeEntities.clear();
                                }
                                ArrayList<ShopTypeEntity> shopTypeEntities = JsonUtils.fromJson(jsonObject.getString("typeList"), new TypeToken<ArrayList<ShopTypeEntity>>() {
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
