package com.shownew.home.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.shownew.home.R;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.activity.shop.OderMenuDetailActivity;
import com.shownew.home.adapter.OderMenuAdapter;
import com.shownew.home.module.UserAPI;
import com.shownew.home.module.entity.OderMenuEntity;
import com.shownew.home.utils.dialog.CommonDialog;
import com.wp.baselib.utils.JsonUtils;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.widget.TitleBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;

/**
 * 订单列表
 */
public class OrderMenuActivity extends BaseActivity implements View.OnClickListener {

    private XRecyclerView mXRecyclerView;
    private OderMenuAdapter mOderMenuAdapter;
    private UserAPI mUserAPI;
    private ArrayList<OderMenuEntity> mOderMenuEntities = new ArrayList<OderMenuEntity>();
    private View mEmptyView;
    private TextView mEmptyTips;
    private ProgressBar mEmptyProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_car);
        mUserAPI = new UserAPI(mShouNewApplication);
        initViews();
    }

    private void initViews() {
        mEmptyView = findViewById(R.id.empty_view);
        mEmptyView.setBackgroundColor(getResources().getColor(R.color.color_service_bg));
        mEmptyView.setOnClickListener(this);
        mEmptyTips = (TextView) findViewById(R.id.textView);
        mEmptyProgressBar = (ProgressBar) findViewById(R.id.head_progressBar);
        mXRecyclerView = (XRecyclerView) findViewById(R.id.my_car_recyclerView);
        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        titleBarView.setTitle("我的订单");
        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setOnLeftOnClickListener(this);
        titleBarView.setLeftIconAndText(R.drawable.back_arrow, "返回");
        titleBarView.setTitleSize(20);
        mOderMenuAdapter = new OderMenuAdapter(this, mOderMenuEntities);
        mXRecyclerView.setAdapter(mOderMenuAdapter);
        mXRecyclerView.setBackgroundColor(0xffdadee5);
        mXRecyclerView.setLayoutManager(new LinearLayoutManager(mShouNewApplication));
        mXRecyclerView.setRefreshProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mXRecyclerView.setArrowImageView(R.drawable.refresh_arrow);
        mXRecyclerView.setEmptyView(mEmptyView);
        mXRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mOderMenuAdapter.setOderMenuItemClickLisener(new OderMenuAdapter.OderMenuItemClickLisener() {
            @Override
            public void clickItem(OderMenuEntity menuEntity) {

                if (menuEntity != null) {
                    Bundle bundle = new Bundle();
                    if (!TextUtils.isEmpty(menuEntity.getOId())) {
                        bundle.putString("oderId", menuEntity.getOId());
                        mainApplication.redirectAndPrameter(OderMenuDetailActivity.class, bundle);
                    }
                }


            }
        });


        mXRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener()

        {
            @Override
            public void onRefresh() {
                refresh();
            }

            @Override
            public void onLoadMore() {
                isRefresh = false;
                getOderMenuList();
            }
        });

        mXRecyclerView.refresh();
    }

    private void refresh() {
        isRefresh = true;
        page = 1;
        getOderMenuList();
    }

    private boolean isRefresh;


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                finish();
                break;
            case R.id.empty_view:
                mEmptyView.setEnabled(false);
                mEmptyTips.setText("数据加载中...");
                mEmptyProgressBar.setVisibility(View.VISIBLE);
                mXRecyclerView.refresh();
                break;
        }
    }

    private int page = 1;

    private void getOderMenuList() {
        mUserAPI.getOderMenuList(page, mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                mEmptyView.setEnabled(true);
                if (isRefresh) {
                    mXRecyclerView.refreshComplete();
                } else {
                    mXRecyclerView.loadMoreComplete();
                }
                if (null == exception) {
                    if (json.has("data")) {
                        try {
                            if (isRefresh && mOderMenuEntities.size() > 0) {
                                mOderMenuEntities.clear();
                            }
                            JSONObject jsonData = json.getJSONObject("data");
                            if (jsonData.has("orderList")) {
                                String orderList = jsonData.getString("orderList");
                                ArrayList<OderMenuEntity> oderMenuEntities = JsonUtils.fromJson(orderList, new TypeToken<ArrayList<OderMenuEntity>>() {
                                }.getType());
                                if (null != oderMenuEntities && oderMenuEntities.size() > 0) {
                                    mOderMenuEntities.addAll(oderMenuEntities);
                                    page++;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                } else {
                    if (mOderMenuEntities.size() == 0) {
                        mEmptyTips.setText("没有数据\n点击我刷新");
                        mEmptyProgressBar.setVisibility(View.GONE);
                    } else {
                        mXRecyclerView.setNoMore(true);
                    }
                }
                mOderMenuAdapter.notifyDataSetChanged();

            }
        });
    }

    /**
     * 删除订单
     *
     * @param oId
     */
    public void deleteOderMenu(final String oId) {
        new CommonDialog(this, "确认删除订单吗").setCommonListener(new CommonDialog.CommonListener() {
            @Override
            public void sure(int flag) {
                if (flag == 1) {
                    mUserAPI.deleteOrderz(oId, mShouNewApplication.new ShouNewHttpCallBackLisener() {
                        @Override
                        protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                            closeLoadingDialog();
                            if (exception == null) {
                                ToastUtil.showToast("删除成功");
                                refresh();
                            } else {
                                ToastUtil.showToast("删除失败");
                            }
                        }

                        @Override
                        protected void onLoading() {
                            super.onLoading();
                            createLoadingDialog();
                        }
                    });
                }
            }
        }).setCancelable(true).show();


    }
}