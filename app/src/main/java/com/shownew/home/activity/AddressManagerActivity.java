package com.shownew.home.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.shownew.home.R;
import com.shownew.home.ShouNewApplication;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.adapter.AddressManagerAdapter;
import com.shownew.home.utils.dialog.CommonDialog;
import com.wp.baselib.widget.TitleBarView;

import java.util.ArrayList;

public class AddressManagerActivity extends BaseActivity implements View.OnClickListener {
    private XRecyclerView mXRecyclerView;
    private ShouNewApplication mShouNewApplication;
    private AddressManagerAdapter mMyCarRecyclerViewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_car);
        mShouNewApplication = ShouNewApplication.getInstance();
        initViews();
    }

    private void initViews() {
        mXRecyclerView = (XRecyclerView) findViewById(R.id.my_car_recyclerView);
        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        titleBarView.setTitle("地址管理");
        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setOnLeftOnClickListener(this);
        titleBarView.setLeftIconAndText(R.drawable.back_arrow, "返回");
        titleBarView.setTitleSize(20);
        mMyCarRecyclerViewAdapter = new AddressManagerAdapter(this, mListData);
        mXRecyclerView.setAdapter(mMyCarRecyclerViewAdapter);
        mXRecyclerView.setBackgroundColor(0xffD7DCE0);
        mXRecyclerView.setLayoutManager(new LinearLayoutManager(mShouNewApplication));
        mXRecyclerView.setRefreshProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mXRecyclerView.setArrowImageView(R.drawable.refresh_arrow);
        mXRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mXRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                getData();

            }

            @Override
            public void onLoadMore() {
                isRefresh = false;
                getData();
            }
        });

        mXRecyclerView.refresh();
    }

    private boolean isRefresh;
    private Handler hander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isRefresh) {
                mXRecyclerView.refreshComplete();
            } else {
                if (mListData.size() > 6) {
                    mXRecyclerView.setNoMore(true);
                } else
                    mXRecyclerView.loadMoreComplete();

            }
            mMyCarRecyclerViewAdapter.notifyDataSetChanged();
        }
    };
    private ArrayList<String> mListData = new ArrayList<String>();

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    if (isRefresh && mListData.size() > 0) {
                        mListData.clear();
                    }
                    for (int i = 0; i < 3; i++) {
                        mListData.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2099122691,1639935715&fm=23&gp=0.jpg");
                        mListData.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3104501119,2327871750&fm=23&gp=0.jpg");
                    }
                    hander.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                finish();
                break;
        }
    }

    public void enterAddressEdit() {
        mainApplication.redirect(AddressEditActivity.class);
    }

    public void delectAddress() {
        new CommonDialog(this, "", "确认删除当前地址吗？").setCommonListener(new CommonDialog.CommonListener() {
            @Override
            public void sure(int flag) {

            }
        }).setCancelable(true).show();
    }
}
