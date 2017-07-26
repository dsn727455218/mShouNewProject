package com.shownew.home.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.shownew.home.R;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.utils.AppUpdateUtil;
import com.shownew.home.utils.DataCleanManager;
import com.shownew.home.utils.dialog.CommonDialog;
import com.wp.baselib.utils.Preference;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.widget.TitleBarView;

public class SetActivity extends BaseActivity implements View.OnClickListener {
    private TextView mClearCacher;
    private TextView mVersion_news_tips;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        iniView();
    }

    private void iniView() {

        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        titleBarView.setTitle("系统设置");
        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setOnLeftOnClickListener(this);
        titleBarView.setLeftIconAndText(R.drawable.back_arrow, "返回");
        titleBarView.setTitleSize(20);
        mClearCacher = (TextView) findViewById(R.id.clear_cacher);
        findViewById(R.id.transaction_pwd).setOnClickListener(this);
        mClearCacher.setOnClickListener(this);
        mVersion_news_tips = (TextView) findViewById(R.id.version_news_tips);
        findViewById(R.id.version_news).setOnClickListener(this);
        mVersion_news_tips.setOnClickListener(this);
        TextView version_news_tv = (TextView) findViewById(R.id.version_news_tv);
        version_news_tv.setOnClickListener(this);
        findViewById(R.id.clear_cacher_tv).setOnClickListener(this);
        try {
            mClearCacher.setText(DataCleanManager.getTotalCacheSize(mShouNewApplication));
            version_news_tv.setText(String.format("当前版本：%s", AppUpdateUtil.getAppVersionName(mShouNewApplication)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new AppUpdateUtil(this, mShouNewApplication).UpdateExecute(true, mVersion_news_tips);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.version_news:
            case R.id.version_news_tv:
            case R.id.version_news_tips:
                checkUpdate();
                break;
            case R.id.clear_cacher_tv:
            case R.id.clear_cacher:
                new CommonDialog(this, "确认清除缓存吗?").setCommonListener(new CommonDialog.CommonListener() {
                    @Override
                    public void sure(int flag) {
                        if (1 == flag) {
                            try {
                                if ("0.00KB".equals(DataCleanManager.getTotalCacheSize(mShouNewApplication))) {
                                    ToastUtil.showToast("没有缓存数据了");
                                    return;
                                }
                                ToastUtil.showToast("清除中...");
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DataCleanManager.clearAllCache(mShouNewApplication);
                                        hander.sendEmptyMessage(1);
                                    }
                                }).start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).setCancelable(true).show();

                break;
            case R.id.backBtn:
                finish();
                break;
            case R.id.transaction_pwd:
                if (!Preference.getBoolean(mShouNewApplication, Preference.IS_LOGIN, false)) {
                    mShouNewApplication.jumpLoginActivity(this);
                    return;
                }
                mShouNewApplication.redirect(TransactionActivity.class);
                break;
        }
    }

    private void checkUpdate() {
        new AppUpdateUtil(this, mShouNewApplication).UpdateExecute(false);
    }


    private Handler hander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (1 == msg.what) {
                try {
                    ToastUtil.showToast("清除完成...");
                    mClearCacher.setText(DataCleanManager.getTotalCacheSize(mShouNewApplication));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (hander != null) {
            hander.removeCallbacks(null);
        }
    }
}
