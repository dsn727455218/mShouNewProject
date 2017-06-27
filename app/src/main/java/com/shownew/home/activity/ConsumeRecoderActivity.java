package com.shownew.home.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.shownew.home.R;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.adapter.ConsumeRecoderAdapter;
import com.shownew.home.module.UserAPI;
import com.shownew.home.module.entity.RecoderEntity;
import com.wp.baselib.utils.JsonUtils;
import com.wp.baselib.widget.TitleBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;

public class ConsumeRecoderActivity extends BaseActivity implements View.OnClickListener {

    private XRecyclerView mXRecyclerView;
    private UserAPI mUserAPI;
    private ConsumeRecoderAdapter mRecoderAdapter;
    private View mEmptyView;
    private TextView mEmptyTips;
    private ProgressBar mEmptyProgressBar;
    private ArrayList<RecoderEntity> mRecoderEntities;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_car);
        mUserAPI = new UserAPI(mShouNewApplication);
        initViews();
    }

    private void initViews() {
        mEmptyView = findViewById(R.id.empty_view);

        mEmptyView.setOnClickListener(this);
        mEmptyTips = (TextView) findViewById(R.id.textView);
        mEmptyProgressBar = (ProgressBar) findViewById(R.id.head_progressBar);


        mXRecyclerView = (XRecyclerView) findViewById(R.id.my_car_recyclerView);
        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        titleBarView.setTitle("消费记录");
        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setOnLeftOnClickListener(this);
        titleBarView.setLeftIconAndText(R.drawable.back_arrow, "返回");
        titleBarView.setTitleSize(20);
        mRecoderEntities = new ArrayList<RecoderEntity>();
        mRecoderAdapter = new ConsumeRecoderAdapter(mRecoderEntities);
        mXRecyclerView.setEmptyView(mEmptyView);
        mXRecyclerView.setAdapter(mRecoderAdapter);
        mXRecyclerView.setBackgroundColor(0xffD7DCE0);
        mXRecyclerView.setLayoutManager(new LinearLayoutManager(mShouNewApplication));
        mXRecyclerView.setRefreshProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mXRecyclerView.setArrowImageView(R.drawable.refresh_arrow);
        mXRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mXRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                refresh();
            }

            @Override
            public void onLoadMore() {
                isRefresh = false;
                getRecordList();
            }
        });

        mXRecyclerView.refresh();
    }

    private void refresh() {
        isRefresh = true;
        page = 1;
        getRecordList();
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

    /**
     * 获取消费记录
     */
    private void getRecordList() {
        mUserAPI.getRecordList(page, mShouNewApplication.new ShouNewHttpCallBackLisener() {

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
                            if (isRefresh && mRecoderEntities.size() > 0) {
                                mRecoderEntities.clear();
                            }
                            JSONObject jsonData = json.getJSONObject("data");
                            if (jsonData.has("payList")) {
                                ArrayList<RecoderEntity> recoderEntities = JsonUtils.fromJson(jsonData.getString("payList"), new TypeToken<ArrayList<RecoderEntity>>() {
                                }.getType());
                                if (recoderEntities != null && recoderEntities.size() > 0) {
                                    page++;
                                    mRecoderEntities.addAll(recoderEntities);
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (mRecoderEntities.size() == 0) {
                        mEmptyTips.setText("没有数据\n点击我刷新");
                        mEmptyProgressBar.setVisibility(View.GONE);
                    } else {
                        mXRecyclerView.setNoMore(true);
                    }
                }
                mRecoderAdapter.notifyDataSetChanged();
            }
        });
    }
}