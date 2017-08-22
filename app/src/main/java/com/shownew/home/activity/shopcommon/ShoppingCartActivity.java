package com.shownew.home.activity.shopcommon;

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
import com.shownew.home.adapter.ShoppingCartAdapter;
import com.shownew.home.db.DatabaseUtils;
import com.shownew.home.module.ShopAPI;
import com.shownew.home.module.dao.ShopCarEntity;
import com.shownew.home.module.dao.ShopCarEntityIml;
import com.wp.baselib.utils.JsonUtils;
import com.wp.baselib.utils.Preference;
import com.wp.baselib.utils.StringUtil;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.widget.TitleBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;

public class ShoppingCartActivity extends BaseActivity implements View.OnClickListener {

    private ArrayList<ShopCarEntity> shopCarEntities = new ArrayList<ShopCarEntity>();
    private ShoppingCartAdapter shoppingCartAdapter;
    private TitleBarView titleBarView;
    private TextView select;
    private ShopAPI shopAPI;
    private XRecyclerView mRecyclerView;
    private TextView show_shop_prices;
    private TextView buyShop;
    private TextView mEmptyTips;
    private ProgressBar mEmptyProgressBar;
    private View mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        shopAPI = new ShopAPI(mShouNewApplication);
        initTitle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        callBackData(0,0);
        select.setText("全选");
        select.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.not_selected_cart, 0, 0);
        if (!Preference.getBoolean(mShouNewApplication, Preference.IS_LOGIN, false)) {
            getLoadData();
        } else {
            getShopcarList();
        }
    }

    private void initTitle() {
        titleBarView = (TitleBarView) findViewById(R.id.headbar);
        titleBarView.setTitle("购物车(4)");
        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setOnLeftOnClickListener(this);
        setBarColor(R.color.orgin);
        titleBarView.setRightText("编辑");
        titleBarView.setRightTextClick(this);
        titleBarView.setLeftIcon(R.drawable.back_arrow);
        titleBarView.getTitleBgTv().setBackgroundResource(R.color.orgin);
        select = (TextView) findViewById(R.id.select);
        select.setOnClickListener(this);
        show_shop_prices = (TextView) findViewById(R.id.show_shop_prices);

        mEmptyView = findViewById(R.id.empty_view);
        mEmptyView.setBackgroundColor(getResources().getColor(R.color.white));
        mEmptyView.setOnClickListener(this);
        mEmptyTips = (TextView) findViewById(R.id.textView);
        mEmptyProgressBar = (ProgressBar) findViewById(R.id.head_progressBar);

        buyShop = (TextView) findViewById(R.id.buy_shop);
        buyShop.setOnClickListener(this);
        mRecyclerView = (XRecyclerView) findViewById(R.id.xrecyclerView);

        shoppingCartAdapter = new ShoppingCartAdapter(this, shopCarEntities);
        mRecyclerView.setAdapter(shoppingCartAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mRecyclerView.setArrowImageView(R.drawable.refresh_arrow);
        mRecyclerView.setEmptyView(mEmptyView);
        mRecyclerView.setPullRefreshEnabled(false);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mRecyclerView.setPullRefreshEnabled(false);
        mRecyclerView.setLoadingMoreEnabled(false);
    }

    @Override
    public void onClick(View view) {

        int size = shopCarEntities.size();
        switch (view.getId()) {
            case R.id.buy_shop:
                if (!Preference.getBoolean(this, Preference.IS_LOGIN, false)) {
                    mShouNewApplication.jumpLoginActivity(this);
                    return;
                }
                ArrayList<ShopCarEntityIml> shopCarEntityImls = new ArrayList<ShopCarEntityIml>();
                for (ShopCarEntity shopCarEntity : shopCarEntities) {
                    if (shopCarEntity.isSelect()) {
                        if (shopCarEntity instanceof ShopCarEntityIml) {
                            shopCarEntityImls.add((ShopCarEntityIml) shopCarEntity);
                        }
                    }
                }
                if (shopCarEntityImls != null && shopCarEntityImls.size() > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("shopCarEntityImls", shopCarEntityImls);
                    mShouNewApplication.redirectAndPrameter(SureOderMenuShopCarActivity.class, bundle);
                } else {
                    ToastUtil.showToast("请选择结算的商品!");
                }
                break;
            case R.id.select:
                if (size > 0) {
                    String rightText = select.getText().toString();
                    if ("全选".equals(rightText)) {
                        select.setText("已选");
                        select.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.selected_cart, 0, 0);
                        for (int i = 0; i < size; i++) {
                            shopCarEntities.get(i).setSelect(true);
                        }
                    } else if ("已选".equals(rightText)) {
                        select.setText("全选");
                        select.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.not_selected_cart, 0, 0);
                        for (int i = 0; i < size; i++) {
                            shopCarEntities.get(i).setSelect(false);
                        }
                    }
                    callBackData();
                    shoppingCartAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.backBtn:
                finish();
                break;
            case R.id.commitFeed:
                if (size > 0) {
                    String rightText = titleBarView.getRightView().getText().toString();
                    if ("编辑".equals(rightText)) {
                        titleBarView.setRightText("完成");
                        for (int i = 0; i < size; i++) {
                            shopCarEntities.get(i).setEdit(true);
                        }

                    } else if ("完成".equals(rightText)) {
                        if (!Preference.getBoolean(mShouNewApplication, Preference.IS_LOGIN, false)) {
                            DatabaseUtils.updateSelectData(this, shopCarEntities);
                        } else {
                            updateShopCart(shopCarEntities);
                        }
                        titleBarView.setRightText("编辑");
                        for (int i = 0; i < size; i++) {
                            shopCarEntities.get(i).setEdit(false);
                        }
                    }
                    shoppingCartAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    private void updateShopCart(ArrayList<ShopCarEntity> shopCarEntities) {
        shopAPI.updateShopCart(JsonUtils.toJson(shopCarEntities), mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {

            }
        });

    }


    private void getLoadData() {
        int size = shopCarEntities.size();
        if (size > 0) {
            shopCarEntities.clear();
        }
        titleBarView.setTitle(String.format("购物车(%s)", size));
        shopCarEntities.addAll(DatabaseUtils.queryAllData(this));
        size = shopCarEntities.size();
        titleBarView.setTitle(String.format("购物车(%s)", size));
        if (size == 0) {
            mEmptyTips.setText("你购物车还没有商品哦\n快去加入购物车吧!");
            mEmptyProgressBar.setVisibility(View.GONE);
        }
        shoppingCartAdapter.notifyDataSetChanged();
    }

    public void isSelect(boolean b) {
        if (b) {
            select.setText("已选");
            select.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.selected_cart, 0, 0);
        } else {
            select.setText("全选");
            select.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.not_selected_cart, 0, 0);
        }
    }

    public void saveShopCar(ShopCarEntity shopCarEntity) {
        if (shopCarEntity != null) {
            if (!Preference.getBoolean(mShouNewApplication, Preference.IS_LOGIN, false)) {
                DatabaseUtils.updateData(this, shopCarEntity);
            } else {
                ArrayList<ShopCarEntity> shopCarEntities = new ArrayList<ShopCarEntity>();
                shopCarEntities.add(shopCarEntity);
                updateShopCart(shopCarEntities);
            }
        }

    }

    public void deleteShopCar(ArrayList<ShopCarEntity> shopCarEntity, int position) {
        if (shopCarEntity != null && shopCarEntity.size() > position) {
            ShopCarEntity carEntity = shopCarEntity.get(position);
            if (!Preference.getBoolean(mShouNewApplication, Preference.IS_LOGIN, false)) {
                DatabaseUtils.delete(this, carEntity);
                shopCarEntity.remove(position);
                if (shopCarEntity.size() == 0) {
                    mEmptyTips.setText("你购物车还没有商品哦\n快去加入购物车吧!");
                    mEmptyProgressBar.setVisibility(View.GONE);
                }
                shoppingCartAdapter.notifyDataSetChanged();
                shoppingCartAdapter.isAllSelect();
            } else {
                deleteShopCarF(carEntity, shopCarEntity, position);
            }

        }
    }


    private void deleteShopCarF(ShopCarEntity carEntity, final ArrayList<ShopCarEntity> shopCarEntity, final int position) {
        if (carEntity instanceof ShopCarEntityIml) {
            ShopCarEntityIml shopCarEntityIml = (ShopCarEntityIml) carEntity;
            shopAPI.deleteShopCar(shopCarEntityIml.getShId(), mShouNewApplication.new ShouNewHttpCallBackLisener() {
                @Override
                protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                    if (exception == null) {
                        shopCarEntity.remove(position);
                        if (shopCarEntity.size() == 0) {
                            mEmptyTips.setText("你购物车还没有商品哦\n快去加入购物车吧!");
                            mEmptyProgressBar.setVisibility(View.GONE);
                        }
                        shoppingCartAdapter.notifyDataSetChanged();
                        shoppingCartAdapter.isAllSelect();
                    }
                }
            });
        }


    }


    private void getShopcarList() {
        shopAPI.getShopcarList(mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (shopCarEntities.size() > 0) {
                    shopCarEntities.clear();
                }
                mRecyclerView.refreshComplete();
                if (exception == null) {
                    try {
                        String shopcarList = json.getJSONObject("data").getString("shopcarList");
                        ArrayList<ShopCarEntityIml> shopCarEntityImls =
                                JsonUtils.fromJson(shopcarList, new TypeToken<ArrayList<ShopCarEntityIml>>() {
                                }.getType());
                        if (shopCarEntityImls != null && shopCarEntityImls.size() > 0) {
                            shopCarEntities.addAll(shopCarEntityImls);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (shopCarEntities.size() == 0) {
                        mEmptyTips.setText("你购物车还没有商品哦\n快去加入购物车吧!");
                        mEmptyProgressBar.setVisibility(View.GONE);
                    }
                }
                titleBarView.setTitle(String.format("购物车(%s)", shopCarEntities.size()));
                shoppingCartAdapter.notifyDataSetChanged();
            }
        });
    }


    public void callBackData(int shopCount, double shopPrices) {
        show_shop_prices.setText(String.format("合计：¥%s", StringUtil.formatMoney(shopPrices)));
        buyShop.setText(String.format("结算(%s)", shopCount));
    }

    private void callBackData() {
        if (shopCarEntities != null)

        {
            int shopCount = 0;
            double shopPrices = 0;
            for (ShopCarEntity shopCarEntityIml : shopCarEntities) {
                if (shopCarEntityIml.isSelect()) {
                    shopCount += shopCarEntityIml.getShNum();
                    shopPrices += shopCarEntityIml.getShKdprice();
                    shopPrices += shopCarEntityIml.getShPrice();
                }
            }
            callBackData(shopCount, shopPrices);
        }


    }
}
