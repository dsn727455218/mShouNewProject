package com.shownew.home.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.shownew.home.R;
import com.shownew.home.adapter.GuildAdapter;
import com.wp.baselib.AndroidActivity;

import java.util.ArrayList;

/**
 * Created by home on 2017/3/31 0031.
 */

public class GuideActivity extends AndroidActivity {
    private GuildAdapter mGuideAdapter;
    private int[] imgResourcesId = {R.drawable.lead1, R.drawable.lead2, R.drawable.lead3, R.drawable.lead4};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initViews();
    }

    private void initViews() {
        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
        initDate();
        mViewPager.setAdapter(mGuideAdapter);
    }

    private void initDate() {
        ArrayList<View> views = new ArrayList<View>();
        int imgLenth = imgResourcesId.length;
        for (int imgId : imgResourcesId) {
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setBackgroundResource(imgId);
            if (imgResourcesId[imgLenth - 1] == imgId) {
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        jumpLogin();
                    }
                });
            }
            views.add(imageView);

        }
        mGuideAdapter = new GuildAdapter(views);
    }

    private void jumpLogin() {
        mainApplication.redirect(MainActivity.class);
        finish();
    }


}
