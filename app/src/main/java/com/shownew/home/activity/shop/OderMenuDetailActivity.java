package com.shownew.home.activity.shop;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.shownew.home.R;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.activity.shouniushop.ShopMallDetailActivity;
import com.shownew.home.adapter.LogisticsAdater;
import com.shownew.home.module.UserAPI;
import com.shownew.home.module.entity.KdEntity;
import com.shownew.home.module.entity.OderDetailEntity;
import com.wp.baselib.utils.JsonUtils;
import com.wp.baselib.utils.StringUtil;
import com.wp.baselib.widget.TitleBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;

import static com.shownew.home.R.id.logistics_state;


public class OderMenuDetailActivity extends BaseActivity implements View.OnClickListener {
    private UserAPI mUserAPI;
    private String mOderId;
    private OderDetailEntity mOderDetailEntity;
    private TextView mConsigneeTv;
    private TextView mOder_menu_phone;
    private TextView mOder_menu_address;
    private ImageView mShop_img;
    private TextView mShop_olde_prices;
    private TextView mShop_title;
    private TextView mShop_type;
    private TextView mShop_prices;
    private TextView mYunfei_tv;
    private TextView mShifu_money;
    private TextView mLogistics_state;
    private TextView mLogistics_company;
    private TextView mLogistics_account;
    private LogisticsAdater mLogisticsAdater;
    private ArrayList<KdEntity> datas = new ArrayList<KdEntity>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oder_menu_detail);
        mUserAPI = new UserAPI(mShouNewApplication);
        initViews();
        if (mBundle != null) {
            mOderId = mBundle.getString("oderId");
        }
    }

    private void initViews() {
        TitleBarView mTitleBarView = (TitleBarView) findViewById(R.id.headbar);
        mTitleBarView.setTitle("订单详情");
        mTitleBarView.setTitleTextColor(R.color.color_title);
        mTitleBarView.setOnLeftOnClickListener(this);
        mTitleBarView.setLeftIcon(R.drawable.back_arrow);
        mTitleBarView.setTitleSize(20);
        mConsigneeTv = (TextView) findViewById(R.id.consignee_tv);
        mOder_menu_phone = (TextView) findViewById(R.id.oder_menu_phone);
        mOder_menu_address = (TextView) findViewById(R.id.oder_menu_address);
        mShop_img = (ImageView) findViewById(R.id.shop_img);
        mShop_olde_prices = (TextView) findViewById(R.id.shop_olde_prices);
        mShop_title = (TextView) findViewById(R.id.shop_title);
        mShop_type = (TextView) findViewById(R.id.shop_type);
        mShop_prices = (TextView) findViewById(R.id.shop_prices);
        mYunfei_tv = (TextView) findViewById(R.id.yunfei_tv);
        mShifu_money = (TextView) findViewById(R.id.shifu_money);
        mLogistics_state = (TextView) findViewById(logistics_state);
        mLogistics_company = (TextView) findViewById(R.id.logistics_company);
        mLogistics_account = (TextView) findViewById(R.id.logistics_account);
        findViewById(R.id.home_item_parent).setOnClickListener(this);
        TextView liuyan = (TextView) findViewById(R.id.liuyan);
        ListView listView = (ListView) findViewById(R.id.listView);
        mLogisticsAdater = new LogisticsAdater(this, datas);
        listView.setAdapter(mLogisticsAdater);
        TextView tags = (TextView) findViewById(R.id.tags);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                finish();
                break;
            case R.id.home_item_parent:
                if (null != mOderDetailEntity) {
                    Bundle bundle = new Bundle();
                    if (mOderDetailEntity.getoMpid() == 0) {
                        bundle.putString("shopId", String.valueOf(mOderDetailEntity.getOPid()));
                        mShouNewApplication.redirectAndPrameter(ShopDetailActivity.class, bundle);
                    } else {
                        bundle.putString("shopId", String.valueOf(mOderDetailEntity.getoMpid()));
                        mShouNewApplication.redirectAndPrameter(ShopMallDetailActivity.class, bundle);
                    }

                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getOrderzInfo();
    }

    /**
     * 获取订单详情
     */
    private void getOrderzInfo() {
        if (TextUtils.isEmpty(mOderId))
            return;
        mUserAPI.getOrderzInfo(mOderId, mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (exception == null) {
                    if (json.has("data")) {
                        try {
                            JSONObject jsonObject = json.getJSONObject("data");
                            if (jsonObject.has("orderz")) {
                                mOderDetailEntity = JsonUtils.fromJson(jsonObject.getString("orderz"), OderDetailEntity.class);
                                setData();
                            }
                            if (jsonObject.has("kdInfo")) {
                                JSONObject kdInfo = jsonObject.getJSONObject("kdInfo");
                                if (kdInfo.has("array")) {
                                    ArrayList<KdEntity> kdEntities = JsonUtils.fromJson(kdInfo.getString("array"), new TypeToken<ArrayList<KdEntity>>() {
                                    }.getType());
                                    if (kdEntities != null && kdEntities.size() > 0) {
                                        setKdData(kdEntities);
                                    }
                                }
                                if (kdInfo.has("state")) {
                                    mLogistics_state.setText(String.format("物流状态: %s", kdInfo.getString("state")));
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }
        });
    }

    /**
     * 设置快递 数据
     *
     * @param
     */
    private void setKdData(ArrayList<KdEntity> kdData) {
        if (datas.size() > 0)
            datas.clear();
        datas.addAll(kdData);
        mLogisticsAdater.notifyDataSetChanged();
    }

    private void setData() {
        if (null != mOderDetailEntity) {
            mConsigneeTv.setText(String.format("收货人:%s", mOderDetailEntity.getOKdname()));
            mShouNewApplication.loadImg(mOderDetailEntity.getOSimg(), mShop_img);
            mShop_title.setText(mOderDetailEntity.getOTitle());
            mShop_type.setText(String.format("颜色分类：%s", mOderDetailEntity.getOColor()));

            mShop_prices.setText(String.format("¥%s", StringUtil.formatMoney(mOderDetailEntity.getOPrice() / mOderDetailEntity.getONum())));

            mShop_olde_prices.setText(String.format("X%s", mOderDetailEntity.getONum()));

            mYunfei_tv.setText(String.format("运费：¥%s", StringUtil.formatMoney(mOderDetailEntity.getOKdprice())));

            mShifu_money.setText(String.format("¥%s", StringUtil.formatMoney(mOderDetailEntity.getOTotalprice())));
            mOder_menu_phone.setText(mOderDetailEntity.getOKdphone());
            mOder_menu_address.setText(String.format("地址：%s", mOderDetailEntity.getOKdaddress()));

            int oState = mOderDetailEntity.getOState();
            mLogistics_state.setText(String.format("物流状态: %s", oState == 0 ? "未支付" : oState == 1 ? "已支付,正在揽件" : oState == 2 ? "已发货" : "订单填写有误，请在消息中查看详情"));
            mLogistics_company.setText(String.format("承运公司: %s", mOderDetailEntity.getOKdCompany()));
            mLogistics_account.setText(String.format("运单编号：%s", mOderDetailEntity.getOKdNo()));
        }
    }
}
