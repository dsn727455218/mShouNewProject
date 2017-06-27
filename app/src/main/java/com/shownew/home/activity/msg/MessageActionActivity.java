package com.shownew.home.activity.msg;

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
import com.shownew.home.activity.shop.ShopDetailActivity;
import com.shownew.home.activity.shouniushop.ShopMallDetailActivity;
import com.shownew.home.adapter.MessageActionAdapter;
import com.shownew.home.module.PublicApi;
import com.shownew.home.module.entity.MessageActionEntity;
import com.wp.baselib.utils.JsonUtils;
import com.wp.baselib.widget.TitleBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;

public class MessageActionActivity extends BaseActivity implements View.OnClickListener {
    private MessageActionAdapter mCenterRecyclerViewAdapter;
    private XRecyclerView mRecyclerView;
    private PublicApi mPublicApi;
    private ArrayList<MessageActionEntity> mMsgListEntities;
    private TextView mEmptyTips;
    private ProgressBar mEmptyProgressBar;
    private View mEmptyView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_action);
        mPublicApi = new PublicApi(mShouNewApplication);
        initViews();
    }

    private void initViews() {
        mEmptyView = findViewById(R.id.empty_view);
        mEmptyView.setBackgroundColor(getResources().getColor(R.color.color_service_bg));
        mEmptyView.setOnClickListener(this);
        mEmptyTips = (TextView) findViewById(R.id.textView);
        mEmptyProgressBar = (ProgressBar) findViewById(R.id.head_progressBar);


        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        titleBarView.setTitle("活动消息");
        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setOnLeftOnClickListener(this);
        titleBarView.setLeftIconAndText(R.drawable.back_arrow, "返回");
        titleBarView.setTitleSize(20);

        mRecyclerView = (XRecyclerView) findViewById(R.id.msg_xrecyclerView);
        mMsgListEntities = new ArrayList<MessageActionEntity>();
        mCenterRecyclerViewAdapter = new MessageActionAdapter(this, mMsgListEntities);
        mRecyclerView.setAdapter(mCenterRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mRecyclerView.setArrowImageView(R.drawable.refresh_arrow);
        mRecyclerView.setEmptyView(mEmptyView);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                refresh();
            }

            @Override
            public void onLoadMore() {
                isRefresh = false;
                getMsgList();
            }
        });
    }

    private void refresh() {
        isRefresh = true;
        page = 1;
        getMsgList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
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
                mRecyclerView.refresh();
                break;
        }
    }

    private int page = 1;

    /**
     * 获取消息列表
     *
     * @param
     */
    private void getMsgList() {
        mPublicApi.getMsgList(page, 2, mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected Object parseData(String result) {
                return null;
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {

                mEmptyView.setEnabled(true);
                if (isRefresh) {
                    mRecyclerView.refreshComplete();
                } else {
                    mRecyclerView.loadMoreComplete();
                }

                if (null == exception) {
                    if (json.has("data")) {
                        if (isRefresh && mMsgListEntities.size() > 0) {
                            mMsgListEntities.clear();
                        }
                        try {
                            JSONObject jsonData = json.getJSONObject("data");
                            if (jsonData.has("msg")) {
                                String msg = jsonData.getString("msg");
                                ArrayList<MessageActionEntity> msgListEntities = JsonUtils.fromJson(msg, new TypeToken<ArrayList<MessageActionEntity>>() {
                                }.getType());
                                if (null != msgListEntities && msgListEntities.size() > 0) {
                                    page++;
                                    mMsgListEntities.addAll(msgListEntities);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (mMsgListEntities.size() == 0) {
                        mEmptyTips.setText("没有数据\n点击我刷新");
                        mEmptyProgressBar.setVisibility(View.GONE);
                    } else {
                        mRecyclerView.setNoMore(true);
                    }
                }
                mCenterRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }

    public void clickAction(MessageActionEntity homeAdverEntity) {
        if (homeAdverEntity != null) {
            if ("0".equals(String.valueOf(homeAdverEntity.getNPid())) && "0".equals(String.valueOf(homeAdverEntity.getnMpid()))) {
                if (!TextUtils.isEmpty(homeAdverEntity.getNUrl())) {
                    mShouNewApplication.redirectWeb("", homeAdverEntity.getNUrl());
                }
            } else {
                Bundle bundle = new Bundle();
                if ("0".equals(homeAdverEntity.getnMpid())) {
                    bundle.putString("shopId", homeAdverEntity.getNPid());
                    mShouNewApplication.redirectAndPrameter(ShopDetailActivity.class, bundle);
                } else if ("0".equals(homeAdverEntity.getNPid())) {
                    bundle.putString("shopId", homeAdverEntity.getnMpid());
                    mShouNewApplication.redirectAndPrameter(ShopMallDetailActivity.class, bundle);
                }
            }
        }
    }
}
