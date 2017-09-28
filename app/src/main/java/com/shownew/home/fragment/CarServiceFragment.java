package com.shownew.home.fragment;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.shownew.home.Config;
import com.shownew.home.R;
import com.shownew.home.ShouNewApplication;
import com.shownew.home.activity.AfterMarketOutletsActivity;
import com.shownew.home.activity.MainActivity;
import com.shownew.home.activity.map.ChargingStationActivity;
import com.shownew.home.activity.map.GasStationActivity;
import com.shownew.home.activity.map.StopCarActivity;
import com.shownew.home.activity.msg.AllMsgActivity;
import com.shownew.home.activity.shop.ShopDetailActivity;
import com.shownew.home.activity.shouniushop.ShopMallDetailActivity;
import com.shownew.home.adapter.HeaderAdapter;
import com.shownew.home.adapter.ServiceAdapter;
import com.shownew.home.module.entity.HomeAdverEntity;
import com.shownew.home.utils.dialog.ShareDialog;
import com.wp.baselib.utils.JsonUtils;
import com.wp.baselib.utils.Preference;
import com.wp.baselib.widget.TitleBarView;
import com.wp.baselib.widget.banner.Banner;
import com.wp.baselib.widget.banner.listener.OnBannerListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;


/**服务
 * A simple {@link Fragment} subclass.
 */
public class CarServiceFragment extends BaseFragment implements View.OnClickListener {


    private XRecyclerView mRecyclerView = null;

    private ServiceAdapter mDataAdapter = null;

    protected View mConverView;
    private LayoutInflater inflater;
    private boolean isRefresh;

    /**
     * 获取到的广告图片实体
     */
    private ArrayList<HomeAdverEntity> mHomeAdverEntities;
    private TitleBarView mTitleBarView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mConverView) {
            this.inflater = inflater;
            mConverView = inflater.inflate(R.layout.fragment_carservice, container, false);
            initViews();
        }
        return mConverView;
    }

    @Override
    public void isHaveMsg(int unRead) {
        if (0 == unRead) {
            mTitleBarView.getMsgCircle().setVisibility(View.GONE);
        } else if (1 == unRead) {
            mTitleBarView.getMsgCircle().setVisibility(View.VISIBLE);
        }
    }

    private void initViews() {
        mTitleBarView = (TitleBarView) mConverView.findViewById(R.id.headbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mTitleBarView.setPadding(0, ShouNewApplication.getStatusBarHeight(getActivity()), 0, 0);
        }
        mTitleBarView.setTitle("服务");
        mTitleBarView.setTitleTextColor(R.color.color_title);
        //        titleBarView.setMoreIcon(1);
        mTitleBarView.setOnMoreClickListener(this);
        mTitleBarView.setOnLeftOnClickListener(this);
        mTitleBarView.setMoreIcon(R.drawable.share);
        mTitleBarView.setLeftIcon(R.drawable.news);
        mTitleBarView.setTitleSize(20);

        mRecyclerView = (XRecyclerView) mConverView.findViewById(R.id.list);

        mDataAdapter = new ServiceAdapter(context, mAdverList);
        mRecyclerView.setAdapter(mDataAdapter);
        mDataAdapter.setServiceClickLisener(new ServiceAdapter.ServiceClickLisener() {
            @Override
            public void clickService(HomeAdverEntity homeAdverEntity) {
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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mRecyclerView.setArrowImageView(R.drawable.refresh_arrow);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallClipRotateMultiple);
        //        mRecyclerView.addItemDecoration(new MyDecoration(context,MyDecoration.HORIZONTAL_LIST));
        mRecyclerView.addHeaderView(getHeaderView());
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                refresh();
            }

            @Override
            public void onLoadMore() {
                isRefresh = false;
                getAdverList();
            }
        });
        refresh();

    }

    private void refresh() {
        isRefresh = true;
        page = 1;
        getAdverList();
        getActionAdv();
    }

    private Banner mBanner;

    private View getHeaderView() {
        View hearderView = inflater.inflate(R.layout.layout_recyclerview_header, null);
        mBanner = (Banner) hearderView.findViewById(R.id.header_banner);
        initBanner(mBanner);
        RecyclerView headerRecylerView = (RecyclerView) hearderView.findViewById(R.id.header_recyclerView);
        initRecylerViewDate(headerRecylerView);
        return hearderView;
    }

    private void initRecylerViewDate(RecyclerView headerRecylerView) {


        HeaderAdapter headerAdapter = new HeaderAdapter(getContext(), new int[]{R.drawable.customer_service, R.drawable.service_insurance, R.drawable.gas_station, R.drawable.service_charge, R.drawable.service_parking});
        headerRecylerView.setLayoutManager(new GridLayoutManager(context, 5));
        headerAdapter.setHeaderListener(new HeaderAdapter.HeaderListener() {
            @Override
            public void clickPosition(int position) {
                switch (position) {
                    case 0:
                    case 1:
                        Bundle bundle = new Bundle();
                        bundle.putInt("service_type", position);
                        mShouNewApplication.redirectAndPrameter(AfterMarketOutletsActivity.class, bundle);
                        break;
                    case 2:
                        mShouNewApplication.redirect(GasStationActivity.class);
                        break;
                    case 3:
                        mShouNewApplication.redirect(ChargingStationActivity.class);
                        break;
                    case 4:
                        mShouNewApplication.redirect(StopCarActivity.class);
                        break;
                }
            }
        });
        headerRecylerView.setAdapter(headerAdapter);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn://消息
                if (!Preference.getBoolean(context, Preference.IS_LOGIN, false)) {
                    mShouNewApplication.jumpLoginActivity(context);
                    return;
                }
                mShouNewApplication.redirect(AllMsgActivity.class);
                if (context != null && context instanceof MainActivity) {
                    MainActivity activity = (MainActivity) context;
                    activity.overridePendingTransition(R.anim.left_in, R.anim.right_out);
                }
                break;
            case R.id.title_bar_more:
                new ShareDialog(context, mShouNewApplication).setCancelable(true).show();
        }
    }

    /**
     * 获取广告图片
     */
    private void getActionAdv() {
        mPublicApi.getLastAdList(1, mShouNewApplication.new ShouNewHttpCallBackLisener() {
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
                                mHomeAdverEntities = JsonUtils.fromJson(ad, new TypeToken<ArrayList<HomeAdverEntity>>() {
                                }.getType());
                                if (null != mHomeAdverEntities && mHomeAdverEntities.size() > 0) {
                                    ArrayList<String> images = new ArrayList<>();
                                    ArrayList<String> title = new ArrayList<>();
                                    for (HomeAdverEntity homeAdverEntity : mHomeAdverEntities) {
                                        images.add(homeAdverEntity.getAImg());
                                        title.add("");
                                    }
                                    mBanner.setImages(images);
                                    //设置banner动画效果
                                    //设置标题集合（当banner样式有显示title时）
                                    mBanner.setBannerTitles(title);
                                    mBanner.setOnBannerListener(new OnBannerListener() {
                                        @Override
                                        public void OnBannerClick(int position) {
                                            HomeAdverEntity homeAdverEntity = mHomeAdverEntities.get(position);
                                            if (mHomeAdverEntities != null) {
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

    private int page = 1;
    private ArrayList<HomeAdverEntity> mAdverList = new ArrayList<HomeAdverEntity>();

    private void getAdverList() {
        mPublicApi.getAdverPic(page, mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (isRefresh) {
                    mRecyclerView.refreshComplete();
                } else {
                    mRecyclerView.loadMoreComplete();
                }
                if (null == exception) {
                    if (json.has("data")) {
                        try {
                            if (isRefresh && mAdverList.size() > 0) {
                                mAdverList.clear();
                            }
                            JSONObject jsonData = json.getJSONObject("data");
                            if (jsonData.has("msg")) {
                                String ad = jsonData.getString("msg");
                                ArrayList<HomeAdverEntity> adverEntities = JsonUtils.fromJson(ad, new TypeToken<ArrayList<HomeAdverEntity>>() {
                                }.getType());
                                if (null != adverEntities && adverEntities.size() > 0) {
                                    page++;
                                    mAdverList.addAll(adverEntities);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (mAdverList.size() > 0) {
                        mRecyclerView.setNoMore(true);
                    }
                }
                mDataAdapter.notifyDataSetChanged();
            }
        });
    }


}
