package com.shownew.home.activity.shouniushop;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.shownew.home.R;
import com.shownew.home.activity.MainActivity;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.activity.msg.AllMsgActivity;
import com.shownew.home.adapter.ShopHomeAdapter;
import com.shownew.home.module.ShopAPI;
import com.shownew.home.module.entity.ShopMallListEntity;
import com.shownew.home.utils.dialog.ShareDialog;
import com.shownew.home.utils.dialog.ShopPopwindow;
import com.wp.baselib.utils.JsonUtils;
import com.wp.baselib.utils.Preference;
import com.wp.baselib.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;

public class ShopMallSearchShopActivity extends BaseActivity implements View.OnClickListener {
    private XRecyclerView mRecyclerView;
    private ShopHomeAdapter mDataAdapter;
    private ShopAPI mShopAPI;
    private String mKeyWord;

    private ArrayList<Object> mSuperMarketEntities = new ArrayList<Object>();
    private EditText mSearchContent;
    private TextView mQueque_all;
    private TextView mQueue_prices;
    private int mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_shop);
        setBarColor(R.color.color_search);
        mShopAPI = new ShopAPI(mShouNewApplication);
        initViews();
        if (mBundle != null) {
            mKeyWord = mBundle.getString("keyWord");
            mType = mBundle.getInt("type");
            if (!TextUtils.isEmpty(mKeyWord)) {
                mSearchContent.setText(mKeyWord);
                refresh();
            }
        }
        mSearchContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    mKeyWord = mSearchContent.getText().toString();
                    refresh();
                    return true;
                }
                return false;

            }
        });
    }

    private boolean isRefresh;

    private void initViews() {

        findViewById(R.id.search_iv).setOnClickListener(this);
        mSearchContent = (EditText) findViewById(R.id.search_content);
        findViewById(R.id.backBtn).setOnClickListener(this);
        findViewById(R.id.more).setOnClickListener(this);
        mRecyclerView = (XRecyclerView) findViewById(R.id.search_xrecyclerView);
        mDataAdapter = new ShopHomeAdapter(mShouNewApplication, mSuperMarketEntities);
        mRecyclerView.setAdapter(mDataAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mRecyclerView.setArrowImageView(R.drawable.refresh_arrow);
        mRecyclerView.setPullRefreshEnabled(false);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
                isRefresh = false;
                if (oder != 5) {
                    getProductList(oder);
                } else {
                    searchShop(mKeyWord);
                }
            }
        });
        mQueque_all = (TextView) findViewById(R.id.queque_all);
        mQueque_all.setOnClickListener(this);
        mQueue_prices = (TextView) findViewById(R.id.queue_prices);
        mQueue_prices.setOnClickListener(this);
        mDataAdapter.setShopHomeLisener(new ShopHomeAdapter.ShopHomeLisener() {
            @Override
            public void clickShopItem(String shopId) {
                Bundle bundle = new Bundle();
                bundle.putString("shopId", shopId);
                bundle.putInt("typeId", 0);
                mShouNewApplication.redirectAndPrameter(ShopMallDetailActivity.class, bundle);
            }
        });
    }


    private void refresh() {

        isRefresh = true;
        page = 1;
        searchShop(mKeyWord);
    }


    private int page = 1;

    /**
     * 商品搜索
     *
     * @param keyWord
     */
    private void searchShop(String keyWord) {
        if (TextUtils.isEmpty(keyWord)) {
            ToastUtil.showToast("请输入搜索关键字");
            return;
        }
        mShopAPI.searchShopMall(keyWord, page, mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void onLoading() {
                super.onLoading();
                createLoadingDialog();
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                closeLoadingDialog();
                if (isRefresh) {
                    if (mSuperMarketEntities.size() > 0) {
                        mSuperMarketEntities.clear();
                    }
                    mRecyclerView.refreshComplete();
                } else {
                    mRecyclerView.loadMoreComplete();
                }
                if (exception == null) {
                    if (json.has("data")) {
                        try {

                            JSONObject jsonData = json.getJSONObject("data");
                            if (jsonData.has("mallproductList")) {
                                String productList = jsonData.getString("mallproductList");
                                ArrayList<ShopMallListEntity> superMarketEntities = JsonUtils.fromJson(productList, new TypeToken<ArrayList<ShopMallListEntity>>() {
                                }.getType());
                                if (superMarketEntities != null && superMarketEntities.size() > 0) {
                                    mSuperMarketEntities.addAll(superMarketEntities);
                                    page++;
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (mSuperMarketEntities.size() > 0) {
                        mRecyclerView.setNoMore(true);
                    }
                }
                mDataAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_iv:
                mKeyWord = mSearchContent.getText().toString();
                refresh();
                break;
            case R.id.backBtn:
                finish();
                break;
            case R.id.queque_all:
                mQueque_all.setTextColor(0xffe77817);
                mQueue_prices.setTextColor(0xff595e66);
                page = 1;
                oder = 0;
                isRefresh=true;
                mKeyWord = mSearchContent.getText().toString();
                getProductList(oder);
                break;
            case R.id.queue_prices:
                String queueTypq = mQueue_prices.getText().toString();
                mQueue_prices.setTextColor(0xffe77817);
                mQueque_all.setTextColor(0xff595e66);
                page = 1;
                isRefresh=true;
                mKeyWord = mSearchContent.getText().toString();
                if ("价格升序".equals(queueTypq)) {
                    oder = 1;
                    getProductList(oder);
                }
                if ("价格降序".equals(queueTypq)) {
                    oder = 2;
                    getProductList(oder);
                }
                break;
            case R.id.more:
                new ShopPopwindow(this).showPopupWindow(v, (int) (v.getWidth() / 2 * 0.1)).setPopClickLisener(new ShopPopwindow.PopClickLisener() {
                    @Override
                    public void clickPopItem(int position) {
                        switch (position) {
                            case 0:
                                if (!Preference.getBoolean(ShopMallSearchShopActivity.this, Preference.IS_LOGIN, false)) {
                                    mShouNewApplication.jumpLoginActivity(ShopMallSearchShopActivity.this);
                                    return;
                                }
                                mShouNewApplication.redirect(AllMsgActivity.class);
                                break;
                            case 1:
                                mainApplication.redirect(MainActivity.class);
                                finish();
                                break;
                            case 2:
                                new ShareDialog(ShopMallSearchShopActivity.this, mShouNewApplication).setCancelable(true).show();
                                break;
                        }
                    }
                });
                break;

        }
    }

    private int oder = 5;

    /*8
           order	int	否	不传或传入0-综合排序 1-价格升序 2-价格降序
     */
    private void getProductList(final int oder) {
        if (TextUtils.isEmpty(mKeyWord)) {
            ToastUtil.showToast("请输入搜索内容");
            return;
        }
        mShopAPI.getShopMallProductList(mType, page, "", mKeyWord, String.valueOf(oder), mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                closeLoadingDialog();
                if (isRefresh) {
                    mRecyclerView.refreshComplete();
                    if (mSuperMarketEntities.size() > 0) {
                        mSuperMarketEntities.clear();
                    }
                } else {
                    mRecyclerView.loadMoreComplete();
                }
                if (exception == null) {
                    if (json.has("data")) {
                        try {
                            if (1 == oder) {
                                mQueue_prices.setText("价格降序");
                            } else if (2 == oder) {
                                mQueue_prices.setText("价格升序");
                            }
                            JSONObject jsonData = json.getJSONObject("data");
                            if (jsonData.has("mallproductList")) {
                                String productList = jsonData.getString("mallproductList");
                                ArrayList<ShopMallListEntity> superMarketEntities = JsonUtils.fromJson(productList, new TypeToken<ArrayList<ShopMallListEntity>>() {
                                }.getType());
                                if (superMarketEntities != null && superMarketEntities.size() > 0) {
                                    mSuperMarketEntities.addAll(superMarketEntities);
                                    page++;
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (mSuperMarketEntities.size() > 0) {
                        mRecyclerView.setNoMore(true);
                    }
                }
                mDataAdapter.notifyDataSetChanged();
            }

            @Override
            protected void onLoading() {
                super.onLoading();
                createLoadingDialog();
            }
        });
    }

}
