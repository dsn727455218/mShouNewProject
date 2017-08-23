package com.shownew.home.activity.shopcommon;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shownew.home.R;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.adapter.AllEvalueteFragmentPagerAdapter;
import com.shownew.home.module.ShopAPI;
import com.shownew.home.module.entity.ShopMallDetailEntity;
import com.shownew.home.module.entity.SuperMarkeDetailEntity;
import com.shownew.home.utils.dialog.ShareDialog;
import com.wp.baselib.widget.TitleBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;

import static com.umeng.socialize.utils.DeviceConfig.context;


public class AllEvalueteActivity extends BaseActivity implements View.OnClickListener {
    private ShopAPI shopAPI;
    private TabLayout tabLayout;
    private ViewPager viewpager;
    private String id;
    private String productType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_evaluete);
        shopAPI = new ShopAPI(mShouNewApplication);
        initTitle();
    }

    private SuperMarkeDetailEntity superMarkeDetailEntity;
    private ShopMallDetailEntity shopMallDetailEntity;

    @Override
    protected void readSaveBundle(Bundle bundle) {
        super.readSaveBundle(bundle);
        if (bundle != null) {
            Object o = bundle.getParcelable("shop");
            if (null != o && o instanceof SuperMarkeDetailEntity) {
                superMarkeDetailEntity = (SuperMarkeDetailEntity) o;
            } else if (null != o && o instanceof ShopMallDetailEntity) {
                shopMallDetailEntity = (ShopMallDetailEntity) o;
            } else {
                id = bundle.getString("id");
                productType = bundle.getString("shopType");
            }


        }
    }

    private void initTitle() {
        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        titleBarView.setTitle("全部评价");
        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setOnLeftOnClickListener(this);
        setBarColor(R.color.orgin);
        titleBarView.setLeftIcon(R.drawable.back_arrow);
        titleBarView.getTitleBgTv().setBackgroundResource(R.color.orgin);
        LinearLayout rightLl = titleBarView.showRightBt();
        ImageView rightBt_img1 = (ImageView) rightLl.findViewById(R.id.rightBt_img1);
        ImageView rightBt_img2 = (ImageView) rightLl.findViewById(R.id.rightBt_img2);
        rightBt_img1.setImageResource(R.drawable.shopping_cart_b);
        rightBt_img2.setImageResource(R.drawable.details_more);
        rightBt_img2.setOnClickListener(this);
        rightBt_img1.setOnClickListener(this);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        getDiscussList();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rightBt_img2:
                new ShareDialog(context, mShouNewApplication).setCancelable(true).show();
                break;
            case R.id.rightBt_img1:
                mShouNewApplication.redirect(ShoppingCartActivity.class);
                break;
            case R.id.backBtn:
                finish();
                break;
        }
    }

    private void getDiscussList() {
        //mallproductId  productId
        if (shopMallDetailEntity != null || superMarkeDetailEntity != null) {
            id = shopMallDetailEntity == null ? superMarkeDetailEntity.getPId() : shopMallDetailEntity.getMpId();
            productType = shopMallDetailEntity == null ? "productId" : "mallproductId";
        }
        if (TextUtils.isEmpty(productType)) {
            finish();
            return;
        }
        shopAPI.getDiscussList(productType, id, "0", 1, mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (exception == null) {
                    if (json.has("data")) {
                        try {
                            JSONObject jsonObject = json.getJSONObject("data");
                            if (jsonObject.has("countArray")) {
                                JSONArray countArray = jsonObject.getJSONArray("countArray");

                                int length = countArray.length();
                                ArrayList<String> tabList = new ArrayList<String>();
                                for (int i = 0; i < length; i++) {
                                    tabList.add(countArray.getString(i));
                                }
                                for (int i = 0; i < length; i++) {
                                    tabLayout.addTab(tabLayout.newTab().setText(tabList.get(i)));
                                }
                                AllEvalueteFragmentPagerAdapter allEvalueteFragmentPagerAdapter = new AllEvalueteFragmentPagerAdapter(getSupportFragmentManager(), tabList, productType, id);
                                viewpager.setAdapter(allEvalueteFragmentPagerAdapter);
                                tabLayout.setupWithViewPager(viewpager);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }


                }
            }
        });

    }


}
