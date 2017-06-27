package com.shownew.home.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.reflect.TypeToken;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.shownew.home.Config;
import com.shownew.home.R;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.activity.shop.ShopDetailActivity;
import com.shownew.home.activity.shouniushop.ShoppingMallActivity;
import com.shownew.home.activity.shop.SupermarketActivity;
import com.shownew.home.adapter.HeaderAdapter;
import com.shownew.home.adapter.MyAdapter;
import com.shownew.home.module.PublicApi;
import com.shownew.home.module.UserAPI;
import com.shownew.home.module.entity.HomeAdverEntity;
import com.shownew.home.module.entity.UserEntity;
import com.wp.baselib.utils.JsonUtils;
import com.wp.baselib.utils.StringUtil;
import com.wp.baselib.widget.TitleBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;


public class ShouWalletActivity extends BaseActivity implements View.OnClickListener {

    private XRecyclerView mRecyclerView = null;

    private MyAdapter mDataAdapter = null;
    private PublicApi mPublicApi;
    private UserAPI mUserAPI;
    private TextView mMy_nichegn_tv;
    private ImageView mMy_header_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center);
        mPublicApi = new PublicApi(mShouNewApplication);
        mUserAPI = new UserAPI(mShouNewApplication);
        initViews();

    }

    private void initViews() {
        findViewById(R.id.empty_view).setVisibility(View.GONE);
        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        titleBarView.setTitle("首牛钱包");
        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setOnLeftOnClickListener(this);
        titleBarView.setLeftIconAndText(R.drawable.back_arrow, "返回");
        titleBarView.setTitleSize(20);

        mRecyclerView = (XRecyclerView) findViewById(R.id.msg_xrecyclerView);
        mRecyclerView.setBackgroundColor(0XFFD7DCE0);
        mDataAdapter = new MyAdapter(this, mAdverList);
        mDataAdapter.setServiceClickLisener(new MyAdapter.ServiceClickLisener() {
            @Override
            public void clickService(HomeAdverEntity homeAdverEntity) {
                if (homeAdverEntity != null) {
                    if (Config.ACTION_WEB.equals(homeAdverEntity.getaPid())) {
                        if (!TextUtils.isEmpty(homeAdverEntity.getaUrl())) {
                            mShouNewApplication.redirectWeb("", homeAdverEntity.getaUrl());
                        }
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("shopId", homeAdverEntity.getaPid());
                        mShouNewApplication.redirectAndPrameter(ShopDetailActivity.class, bundle);
                    }
                }
            }
        });
        mRecyclerView.setAdapter(mDataAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mRecyclerView.setPullRefreshEnabled(false);
        mRecyclerView.setArrowImageView(R.drawable.refresh_arrow);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mRecyclerView.addHeaderView(getHeaderView());
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
                isRefresh = false;
                getAdverList();
            }
        });

    }

    private void getUserInfoData() {
        mUserAPI.getUserData(mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected Object parseData(String result) {
                return null;
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (null == exception) {
                    if (json.has("data")) {
                        try {
                            JSONObject result = json.getJSONObject("data");
                            String user = result.getString("user");
                            if (!TextUtils.isEmpty(user)) {
                                UserEntity.UserBean userBean = JsonUtils.fromJson(user, UserEntity.UserBean.class);
                                if (null != userBean) {
                                    Glide.with(mShouNewApplication).load(userBean.getUIcon()).asBitmap().placeholder(R.drawable.square_seize).error(R.drawable.square_seize).into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                            mMy_header_icon.setImageBitmap(resource);
                                        }
                                    });
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

    @Override
    protected void onResume() {
        super.onResume();
        getPayAboutInfo();
        refresh();
        getUserInfoData();
    }

    private void refresh() {
        page = 1;
        isRefresh = true;
        getAdverList();
    }


    private View getHeaderView() {
        View hearderView = getLayoutInflater().inflate(R.layout.layout_wallet_recyclerview_header, null);
        hearderView.findViewById(R.id.wallet_recharge_tv).setOnClickListener(this);
        mMy_nichegn_tv = (TextView) hearderView.findViewById(R.id.my_nichegn_tv);
        mMy_header_icon = (ImageView) hearderView.findViewById(R.id.my_header_icon);
        RecyclerView headerRecylerView = (RecyclerView) hearderView.findViewById(R.id.header_recyclerView);
        initRecylerViewDate(headerRecylerView);
        return hearderView;
    }

    private void initRecylerViewDate(RecyclerView headerRecylerView) {
        HeaderAdapter headerAdapter = new HeaderAdapter(this, new int[]{R.drawable.record, R.drawable.order, R.drawable.shounew_shop, R.drawable.supermarket});
        headerRecylerView.setLayoutManager(new GridLayoutManager(this, 4));
        headerAdapter.setHeaderListener(new HeaderAdapter.HeaderListener() {
            @Override
            public void clickPosition(int position) {
                switch (position) {
                    case 0:
                        mainApplication.redirect(ConsumeRecoderActivity.class);
                        break;
                    case 1:
                        mainApplication.redirect(OrderMenuActivity.class);
                        break;
                    case 2:
                        mShouNewApplication.redirect(ShoppingMallActivity.class);
                        break;
                    case 3:
                        mShouNewApplication.redirect(SupermarketActivity.class);
                        break;
                }
            }
        });
        headerRecylerView.setAdapter(headerAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                finish();
                break;
            case R.id.title_bar_more:
                break;
            case R.id.wallet_recharge_tv:
                mainApplication.redirect(AccountRechargeActivity.class);
                break;

        }
    }

    private int page = 1;
    private ArrayList<HomeAdverEntity> mAdverList = new ArrayList<HomeAdverEntity>();
    private boolean isRefresh;

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

    private void getPayAboutInfo() {
        mUserAPI.getPayAboutInfo(mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (exception == null) {
                    if (json.has("data")) {
                        try {
                            JSONObject jsonObject = json.getJSONObject("data");
                            if (jsonObject.has("remain")) {
                                mMy_nichegn_tv.setText(String.format("¥%s", StringUtil.formatMoney(jsonObject.getString("remain"))));
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
