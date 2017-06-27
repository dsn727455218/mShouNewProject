package com.shownew.home.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.shownew.home.R;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.activity.shop.ShopDetailActivity;
import com.shownew.home.module.UserAPI;
import com.shownew.home.module.entity.SourcesEntity;
import com.wp.baselib.widget.TitleBarView;

import java.util.ArrayList;

public class BatteryManagerActivity extends BaseActivity implements View.OnClickListener {

    private UserAPI mUserAPI;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_manager);
        mUserAPI = new UserAPI(mShouNewApplication);
        initView();
    }

    private void initView() {
        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        titleBarView.setTitle("电池管理");
        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setOnLeftOnClickListener(this);
        titleBarView.setLeftIconAndText(R.drawable.back_arrow, "返回");
        titleBarView.setTitleSize(20);

        ImageView imageView = (ImageView) findViewById(R.id.battery_iv);
        final ArrayList<SourcesEntity> sourcesEntities = mUserAPI.getSourcesData();
        if (sourcesEntities != null && sourcesEntities.size() >= 4) {
            final String url = sourcesEntities.get(1).getSImg();
            mShouNewApplication.loadImg(url, imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("shopId", sourcesEntities.get(1).getSPid());
                    mShouNewApplication.redirectAndPrameter(ShopDetailActivity.class, bundle);
                }
            });
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                finish();
                break;

        }

    }
}
