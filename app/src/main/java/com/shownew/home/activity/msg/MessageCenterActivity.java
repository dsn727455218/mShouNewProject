package com.shownew.home.activity.msg;

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
import com.shownew.home.adapter.MessageCenterAdapter;
import com.shownew.home.module.PublicApi;
import com.shownew.home.module.entity.MsgListEntity;
import com.wp.baselib.utils.JsonUtils;
import com.wp.baselib.widget.TitleBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;

public class MessageCenterActivity extends BaseActivity implements View.OnClickListener {
    private MessageCenterAdapter mCenterRecyclerViewAdapter;
    private XRecyclerView mRecyclerView;
    private PublicApi mPublicApi;
    private int mMsgType;
    private ArrayList<MsgListEntity> mMsgListEntities;
    private TextView mEmptyTips;
    private ProgressBar mEmptyProgressBar;
    private View mEmptyView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center);
        mPublicApi = new PublicApi(mShouNewApplication);
        initViews();
    }

    private void initViews() {
        mEmptyView = findViewById(R.id.empty_view);
        mEmptyView.setBackgroundColor(getResources().getColor(R.color.bgcolor));
        mEmptyView.setOnClickListener(this);
        mEmptyTips = (TextView) findViewById(R.id.textView);
        mEmptyProgressBar = (ProgressBar) findViewById(R.id.head_progressBar);


        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        titleBarView.setTitle("消息");
        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setOnLeftOnClickListener(this);
        titleBarView.setLeftIconAndText(R.drawable.back_arrow, "返回");
        titleBarView.setTitleSize(20);

        mRecyclerView = (XRecyclerView) findViewById(R.id.msg_xrecyclerView);
        mMsgListEntities = new ArrayList<MsgListEntity>();
        mCenterRecyclerViewAdapter = new MessageCenterAdapter(this,mMsgListEntities);
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
                getMsgList(mMsgType);
            }
        });
    }

    private void refresh() {
        isRefresh = true;
        page = 1;
        getMsgList(mMsgType);
    }

    public int getMsgType() {
        return mMsgType;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBundle != null) {
            mMsgType = mBundle.getInt("msgType");
            refresh();
        }
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
     * @param type
     */
    private void getMsgList(int type) {
        mPublicApi.getMsgList(page, type, mShouNewApplication.new ShouNewHttpCallBackLisener() {
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
                                ArrayList<MsgListEntity> msgListEntities = JsonUtils.fromJson(msg, new TypeToken<ArrayList<MsgListEntity>>() {
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
}