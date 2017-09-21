package com.shownew.home.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.shownew.home.R;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.module.UserAPI;
import com.shownew.home.module.entity.SourcesEntity;
import com.wp.baselib.widget.TitleBarView;

import java.util.ArrayList;


/**
 * 微客户
 */
public class MicroCustomerActivity extends BaseActivity implements View.OnClickListener {


    private static final String HTML = "客服电话<br/><font color=#9ba4b2><big><big><big>%s</big></big></big></font>";
    private UserAPI mUserAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_micro_customer);
        mUserAPI = new UserAPI(mShouNewApplication);
        initViews();
    }

    private void initViews() {
        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        titleBarView.setTitle("微客服");
        titleBarView.setTitleSize(20);
        titleBarView.setOnLeftOnClickListener(this);
        titleBarView.setLeftIcon(R.drawable.back_arrow);
        titleBarView.setTitleTextColor(R.color.color_title);
        TextView mCustomerPhone = (TextView) findViewById(R.id.customer_phone);
        mCustomerPhone.setText(Html.fromHtml(String.format(HTML, "400-997-9880")));
        final ImageView  mQr_code = (ImageView) findViewById(R.id.qr_code);
        ArrayList<SourcesEntity> sourcesEntities = mUserAPI.getSourcesData();
        if (sourcesEntities != null && sourcesEntities.size() >= 4) {
            String tag = (String) mQr_code.getTag();
            final String url = sourcesEntities.get(0).getSImg();
            if (!TextUtils.equals(url, tag)) {
                mQr_code.setImageResource(R.drawable.square_seize);
            }
            Glide.with(this).load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.square_seize).error(R.drawable.square_seize).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    mQr_code.setTag(url);
                    mQr_code.setImageBitmap(resource);
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
