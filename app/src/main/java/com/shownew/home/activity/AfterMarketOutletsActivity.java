package com.shownew.home.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;
import com.google.gson.reflect.TypeToken;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.shownew.home.R;
import com.shownew.home.activity.map.BaseLocationActivity;
import com.shownew.home.adapter.AfterMarketOutletsAdapter;
import com.shownew.home.module.PublicApi;
import com.shownew.home.module.entity.AfterMarkerEntity;
import com.shownew.home.module.entity.InsuranceEntity;
import com.wp.baselib.utils.JsonUtils;
import com.wp.baselib.widget.TitleBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;

/**
 * 售后网点  低价保险
 */
public class AfterMarketOutletsActivity extends BaseLocationActivity implements View.OnClickListener {

    private int mService_type;
    private XRecyclerView mXRecyclerView;
    private AfterMarketOutletsAdapter mAfterMarketOutletsAdapter;
    private PublicApi mPublicApi;
    private View mEmptyView;
    private ProgressBar mEmptyProgressBar;
    private TextView mEmptyTips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_car);
        super.onCreate(savedInstanceState);
        mPublicApi = new PublicApi(mShouNewApplication);
        initViews();
    }

    private String gps;

    private int pager;

    @Override
    protected void getLocation(AMapLocation location) {
        super.getLocation(location);
        if (null != location && TextUtils.isEmpty(gps)) {

            gps = String.format("%s,%s", location.getLatitude(), location.getLongitude());
             LatLng  mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            mAfterMarketOutletsAdapter = new AfterMarketOutletsAdapter(mShouNewApplication, mListData, mLatLng);
            mXRecyclerView.setAdapter(mAfterMarketOutletsAdapter);
            refresh();

        }
    }

    private void initViews() {
        mEmptyView = findViewById(R.id.empty_view);
        mEmptyView.setBackgroundColor(getResources().getColor(R.color.color_service_bg));
        mEmptyView.setOnClickListener(this);
        mEmptyTips = (TextView) findViewById(R.id.textView);
        mEmptyProgressBar = (ProgressBar) findViewById(R.id.head_progressBar);
        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        mXRecyclerView = (XRecyclerView) findViewById(R.id.my_car_recyclerView);
        mXRecyclerView.setBackgroundColor(0xffdadee5);
        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setOnLeftOnClickListener(this);
        titleBarView.setLeftIconAndText(R.drawable.back_arrow, "返回");
        titleBarView.setTitleSize(20);
        if (mBundle != null) {
            mService_type = mBundle.getInt("service_type");
            if (mService_type == 0) {
                titleBarView.setTitle("售后网点");
            } else if (1 == mService_type) {
                titleBarView.setTitle("低价保险");
            }
        }
        //        if (!TextUtils.isEmpty(gps)) {
        //            String[] lan = gps.split(",");

        mXRecyclerView.setLayoutManager(new LinearLayoutManager(mShouNewApplication));
        mXRecyclerView.setRefreshProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mXRecyclerView.setArrowImageView(R.drawable.refresh_arrow);
        mXRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mXRecyclerView.setEmptyView(mEmptyView);
        mXRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                refresh();
            }

            @Override
            public void onLoadMore() {
                isRefresh = false;
                if (!TextUtils.isEmpty(gps)) {
                    if (mService_type == 0) {
                        getAfterMarker();
                    } else if (1 == mService_type) {
                        getInsuranceList();
                    }
                }
            }
        });
        mEmptyView.setVisibility(View.VISIBLE);
        mXRecyclerView.setVisibility(View.GONE);

    }

    private boolean isRefresh;
    private ArrayList<Object> mListData = new ArrayList<Object>();


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


    private void refresh() {
        isRefresh = true;
        pager = 1;
        if (!TextUtils.isEmpty(gps)) {
            if (mService_type == 0) {
                getAfterMarker();
            } else if (1 == mService_type) {
                getInsuranceList();
            }
        }
    }

    /**
     * 获取售后网点列表
     */
    private void getAfterMarker() {
        mPublicApi.getAfterMarker(gps, pager, mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected Object parseData(String result) {
                return null;
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                mXRecyclerView.refreshComplete();
                mEmptyView.setEnabled(true);
                if (null == exception) {
                    if (json.has("data")) {
                        if (isRefresh && mListData.size() > 0) {
                            mListData.clear();
                        } else {
                            mXRecyclerView.loadMoreComplete();
                        }
                        try {
                            JSONObject jsonObject = json.getJSONObject("data");
                            if (jsonObject.has("branchList")) {
                                String result = jsonObject.getString("branchList");
                                ArrayList<AfterMarkerEntity> insuranceEntities = JsonUtils.fromJson(result, new TypeToken<ArrayList<AfterMarkerEntity>>() {
                                }.getType());
                                if (insuranceEntities != null && insuranceEntities.size() > 0) {
                                    mListData.addAll(insuranceEntities);
                                    pager++;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (mListData.size() == 0) {
                        mEmptyTips.setText("没有数据\n点击我刷新");
                        mEmptyProgressBar.setVisibility(View.GONE);
                    } else {
                        mXRecyclerView.setNoMore(true);
                    }
                }
                mAfterMarketOutletsAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 获取低价保险列表
     */
    private void getInsuranceList() {
        mPublicApi.getInsuranceList(gps, pager, mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected Object parseData(String result) {
                return null;
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                mEmptyView.setEnabled(true);
                mXRecyclerView.refreshComplete();
                if (null == exception) {
                    if (json.has("data")) {
                        if (isRefresh && mListData.size() > 0) {
                            mListData.clear();
                        } else if (!isRefresh) {
                            mXRecyclerView.loadMoreComplete();
                        }
                        try {
                            JSONObject jsonObject = json.getJSONObject("data");
                            if (jsonObject.has("insuranceList")) {
                                String result = jsonObject.getString("insuranceList");
                                ArrayList<InsuranceEntity> insuranceEntities = JsonUtils.fromJson(result, new TypeToken<ArrayList<InsuranceEntity>>() {
                                }.getType());
                                if (insuranceEntities != null && insuranceEntities.size() > 0) {
                                    mListData.addAll(insuranceEntities);
                                    pager++;
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (mListData.size() == 0) {
                        mEmptyTips.setText("没有数据\n点击我刷新");
                        mEmptyProgressBar.setVisibility(View.GONE);
                    } else {
                        mXRecyclerView.setNoMore(true);
                    }
                }
                mAfterMarketOutletsAdapter.notifyDataSetChanged();

            }
        });
    }
}
