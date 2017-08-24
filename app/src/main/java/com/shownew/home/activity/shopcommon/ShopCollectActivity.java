package com.shownew.home.activity.shopcommon;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.shownew.home.R;
import com.shownew.home.ShouNewApplication;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.activity.shop.ShopDetailActivity;
import com.shownew.home.activity.shouniushop.ShopMallDetailActivity;
import com.shownew.home.adapter.CollectShopAdapter;
import com.shownew.home.module.ShopAPI;
import com.shownew.home.module.entity.CollectShopEntity;
import com.wp.baselib.utils.JsonUtils;
import com.wp.baselib.utils.Preference;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.widget.TitleBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;

public class ShopCollectActivity extends BaseActivity implements View.OnClickListener {

    private XRecyclerView mRecyclerView;
    private ShouNewApplication shouNewApplication;
    private ShopAPI shopAPI;
    private CollectShopAdapter collectShopAdapter;
    private TextView mEmptyTips;
    private ProgressBar mEmptyProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_collect);
        shouNewApplication = ShouNewApplication.getInstance();
        shopAPI = new ShopAPI(shouNewApplication);
        initTitle();

    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void initTitle() {
       View mEmptyView = findViewById(R.id.empty_view);
        mEmptyView.setBackgroundColor(getResources().getColor(R.color.white));
        mEmptyView.setOnClickListener(this);
        mEmptyTips = (TextView) findViewById(R.id.textView);
        mEmptyProgressBar = (ProgressBar) findViewById(R.id.head_progressBar);

        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        titleBarView.setTitle("收藏夹");
        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setOnLeftOnClickListener(this);
        setBarColor(R.color.orgin);
        titleBarView.setLeftIcon(R.drawable.back_arrow);
        titleBarView.getTitleBgTv().setBackgroundResource(R.color.orgin);
        mRecyclerView = (XRecyclerView) findViewById(R.id.collect_recyclerview);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mRecyclerView.setArrowImageView(R.drawable.refresh_arrow);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallClipRotateMultiple);
        collectShopAdapter = new CollectShopAdapter(collectShopEntities, this);
        mRecyclerView.setAdapter(collectShopAdapter);
        mRecyclerView.setEmptyView(mEmptyView);
        mRecyclerView.setPullRefreshEnabled(false);
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
                getCollectData();
            }
        });
    }

    private int pager = 1;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                finish();
                break;
            case R.id.empty_view:
//                mEmptyView.setEnabled(false);
//                mEmptyTips.setText("数据加载中...");
//                mEmptyProgressBar.setVisibility(View.VISIBLE);
//                refresh();
                break;
        }
    }

    private ArrayList<CollectShopEntity> collectShopEntities = new ArrayList<CollectShopEntity>();

    private void getCollectData() {
        shopAPI.getCollectData(pager, shouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
//                mEmptyView.setEnabled(true);
                if (pager == 1) {
                    mRecyclerView.refreshComplete();
                    if (collectShopEntities.size() > 0)
                        collectShopEntities.clear();
                } else {
                    mRecyclerView.loadMoreComplete();
                }
                if (exception == null) {
                    if (json.has("data")) {
                        try {
                            JSONObject dataJson = json.getJSONObject("data");
                            if (dataJson.has("collectionList")) {
                                ArrayList<CollectShopEntity> shopEntities = JsonUtils.fromJson(dataJson.getString("collectionList"), new TypeToken<ArrayList<CollectShopEntity>>() {
                                }.getType());
                                if (shopEntities != null && shopEntities.size() != 0) {
                                    collectShopEntities.addAll(shopEntities);
                                    pager++;
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    if (collectShopEntities.size() == 0) {
                        mEmptyTips.setText("你还没有收藏商品哦\n快去收藏吧!");
                        mEmptyProgressBar.setVisibility(View.GONE);
                    } else {
                        mRecyclerView.setNoMore(true);
                    }

                }

                collectShopAdapter.notifyDataSetChanged();
            }
        });
    }

    public void collection(CollectShopEntity collectShopEntity, final View view) {

        if (!Preference.getBoolean(ShopCollectActivity.this, Preference.IS_LOGIN, false)) {
            mShouNewApplication.jumpLoginActivity(ShopCollectActivity.this);
            return;
        }
        if (collectShopEntity == null)
            return;
        //  cannelCollection  取消
        //collect  收藏

        int pid = collectShopEntity.getCoMpid();
        int shopId = pid == 0 ? collectShopEntity.getCoPid() : pid;
        shopAPI.collection(pid == 0 ? 1 : 0, String.valueOf(shopId), "cannelCollection", mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (exception == null) {
                    ToastUtil.showToast("删除成功");
                    view.setVisibility(View.GONE);
                    refresh();
                } else {
                    ToastUtil.showToast("操作失败，请重试");
                }
            }
        });
    }

    private void refresh() {
        pager = 1;
        getCollectData();
    }

    public void enterShopDetail(CollectShopEntity collectShopEntity) {
        if (null != collectShopEntity) {
            Bundle bundle = new Bundle();
            if (collectShopEntity.getCoMpid() == 0) {
                bundle.putString("shopId", String.valueOf(collectShopEntity.getCoPid()));
                mShouNewApplication.redirectAndPrameter(ShopDetailActivity.class, bundle);
            } else {
                bundle.putString("shopId", String.valueOf(collectShopEntity.getCoMpid()));
                mShouNewApplication.redirectAndPrameter(ShopMallDetailActivity.class, bundle);
            }
        }
    }
}
