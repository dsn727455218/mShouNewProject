package com.shownew.home.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.shownew.home.Config;
import com.shownew.home.R;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.activity.shop.OderMenuDetailActivity;
import com.shownew.home.activity.shopcommon.LaunchEvaluateActivity;
import com.shownew.home.adapter.OderMenuAdapter;
import com.shownew.home.module.UserAPI;
import com.shownew.home.module.entity.OderMenuEntity;
import com.shownew.home.pay.AlipayPayUtils;
import com.shownew.home.pay.PayResult;
import com.shownew.home.pay.WXPay;
import com.shownew.home.utils.dialog.CommonDialog;
import com.shownew.home.utils.dialog.PayDialog;
import com.shownew.home.view.PopEnterPassword;
import com.wp.baselib.utils.JsonUtils;
import com.wp.baselib.utils.Preference;
import com.wp.baselib.utils.StringUtil;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.widget.TitleBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;

/**
 * 订单列表
 */
public class OrderMenuActivity extends BaseActivity implements View.OnClickListener {

    private XRecyclerView mXRecyclerView;
    private OderMenuAdapter mOderMenuAdapter;
    private UserAPI mUserAPI;
    private ArrayList<OderMenuEntity> mOderMenuEntities = new ArrayList<OderMenuEntity>();
    private View mEmptyView;
    private TextView mEmptyTips;
    private ProgressBar mEmptyProgressBar;
    private int flag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_car);
        mUserAPI = new UserAPI(mShouNewApplication);
        initViews();
    }

    private void initViews() {
        mEmptyView = findViewById(R.id.empty_view);
        mEmptyView.setBackgroundColor(getResources().getColor(R.color.color_service_bg));
        mEmptyView.setOnClickListener(this);//
        mEmptyTips = (TextView) findViewById(R.id.textView);
        mEmptyProgressBar = (ProgressBar) findViewById(R.id.head_progressBar);
        mXRecyclerView = (XRecyclerView) findViewById(R.id.my_car_recyclerView);
        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        titleBarView.setTitle("我的订单");
        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setOnLeftOnClickListener(this);
        titleBarView.setLeftIconAndText(R.drawable.back_arrow, "返回");
        titleBarView.setTitleSize(20);
        mOderMenuAdapter = new OderMenuAdapter(this, mOderMenuEntities);
        mXRecyclerView.setAdapter(mOderMenuAdapter);
        mXRecyclerView.setBackgroundColor(getResources().getColor(R.color.white));
        mXRecyclerView.setLayoutManager(new LinearLayoutManager(mShouNewApplication));
        mXRecyclerView.setRefreshProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mXRecyclerView.setArrowImageView(R.drawable.refresh_arrow);
        mXRecyclerView.setEmptyView(mEmptyView);
        mXRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mOderMenuAdapter.setOderMenuItemClickLisener(new OderMenuAdapter.OderMenuItemClickLisener() {
            @Override
            public void clickItem(OderMenuEntity menuEntity) {

                if (menuEntity != null) {
                    Bundle bundle = new Bundle();
                    if (!TextUtils.isEmpty(menuEntity.getOId())) {
                        bundle.putString("oderId", menuEntity.getOId());
                        mainApplication.redirectAndPrameter(OderMenuDetailActivity.class, bundle);
                    }
                }
            }
        });


        mXRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener()

        {
            @Override
            public void onRefresh() {
                refresh();
            }

            @Override
            public void onLoadMore() {
                isRefresh = false;
                getOderMenuList();
            }
        });

        refresh();
    }

    private void refresh() {
        isRefresh = true;
        page = 1;
        getOderMenuList();
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
                mXRecyclerView.refresh();
                break;
        }
    }

    private int page = 1;

    private void getOderMenuList() {
        mUserAPI.getOderMenuList(page, mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                mEmptyView.setEnabled(true);
                if (isRefresh) {
                    if (mOderMenuEntities.size() > 0) {
                        mOderMenuEntities.clear();
                    }
                    mXRecyclerView.refreshComplete();
                } else {
                    mXRecyclerView.loadMoreComplete();
                }
                if (null == exception) {
                    if (json.has("data")) {
                        try {
                            JSONObject jsonData = json.getJSONObject("data");
                            if (jsonData.has("orderList")) {
                                String orderList = jsonData.getString("orderList");
                                ArrayList<OderMenuEntity> oderMenuEntities = JsonUtils.fromJson(orderList, new TypeToken<ArrayList<OderMenuEntity>>() {
                                }.getType());
                                if (null != oderMenuEntities && oderMenuEntities.size() > 0) {
                                    mOderMenuEntities.addAll(oderMenuEntities);
                                    page++;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                } else {
                    if (mOderMenuEntities.size() == 0) {
                        mEmptyTips.setText("没有数据\n点击我刷新");
                        mEmptyProgressBar.setVisibility(View.GONE);
                    } else {
                        mXRecyclerView.setNoMore(true);
                    }
                }
                mOderMenuAdapter.notifyDataSetChanged();

            }
        });
    }

    /**
     * 删除订单
     *
     * @param oId
     */
    public void deleteOderMenu(final String oId) {
        new CommonDialog(this, "确认删除订单吗").setCommonListener(new CommonDialog.CommonListener() {
            @Override
            public void sure(int flag) {
                if (flag == 1) {
                    mUserAPI.deleteOrderz(oId, mShouNewApplication.new ShouNewHttpCallBackLisener() {
                        @Override
                        protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                            closeLoadingDialog();
                            if (exception == null) {
                                ToastUtil.showToast("删除成功");
                                refresh();
                            } else {
                                ToastUtil.showToast("删除失败");
                            }
                        }

                        @Override
                        protected void onLoading() {
                            super.onLoading();
                            createLoadingDialog();
                        }
                    });
                }
            }
        }).setCancelable(true).show();


    }

    public void cancelOderMenu(final String oId) {
        new CommonDialog(this, "确认取消订单吗").setCommonListener(new CommonDialog.CommonListener() {
            @Override
            public void sure(int flag) {
                if (flag == 1) {
                    mUserAPI.cancelOrderz(oId, mShouNewApplication.new ShouNewHttpCallBackLisener() {
                        @Override
                        protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                            closeLoadingDialog();
                            if (exception == null) {
                                ToastUtil.showToast("取消成功");
                                refresh();
                            } else {
                                ToastUtil.showToast("取消失败");
                            }
                        }

                        @Override
                        protected void onLoading() {
                            super.onLoading();
                            createLoadingDialog();
                        }
                    });
                }
            }
        }).setCancelable(true).show();

    }

    public void confirmReceived(final String oId) {

        new CommonDialog(this, "确认收货吗").setCommonListener(new CommonDialog.CommonListener() {
            @Override
            public void sure(int flag) {
                if (flag == 1) {
                    mUserAPI.confirmReceived(oId, mShouNewApplication.new ShouNewHttpCallBackLisener() {
                        @Override
                        protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                            closeLoadingDialog();
                            if (exception == null) {
                                ToastUtil.showToast("收货成功");
                                refresh();
                            } else {
                                ToastUtil.showToast("收货失败");
                            }
                        }

                        @Override
                        protected void onLoading() {
                            super.onLoading();
                            createLoadingDialog();
                        }
                    });
                }
            }
        }).setCancelable(true).show();

    }

    private String mOrderzNo;
    private PayDialog payDialog;

    public void againPayOderMenu(final OderMenuEntity menuEntity) {
        if (menuEntity == null)
            return;
        payDialog = new PayDialog(this, 0 != menuEntity.getOPid());
        payDialog.setSelectPayWayLisenter(new PayDialog.SelectPayWayLisenter() {
            @Override
            public void callback(int payWays) {
                payDialog = null;
                mOrderzNo = menuEntity.getoNo();
                flag = payWays;
                switch (flag) {
                    case 1:
                        alipayCharge();
                        break;
                    case 2:
                        shouniuCharge(menuEntity.getOTotalprice());
                        break;
                    case 0:
                        wxCharge();
                        break;
                }
            }
        });
        payDialog.setCancelable(true);
        payDialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        closeLoadingDialog();
        refresh();
        getPayAboutInfo();
    }

    /**
     * 获取余额、首次充值接口
     * <p>
     * "data": {
     * "isFirst": 0,
     * "remain": 990
     * },
     */
    private int tradePass;

    private void getPayAboutInfo() {
        mUserAPI.getPayAboutInfo(mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (exception == null) {
                    if (json.has("data")) {
                        try {
                            JSONObject jsonObject = json.getJSONObject("data");
                            if (jsonObject.has("tradePass")) {
                                tradePass = jsonObject.getInt("tradePass");
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
     * 支付宝支付
     */

    private void alipayCharge() {

        mUserAPI.alipayCharge(mOrderzNo, mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (exception == null) {
                    if (null != json) {
                        if (json.has("data")) {
                            try {
                                String tenpayInfo = json.getJSONObject("data").getString("alipayInfo");
                                new AlipayPayUtils(OrderMenuActivity.this, mHandler, tenpayInfo);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
            }
        });

    }

    /**
     * 首牛支付
     */
    private void shouniuCharge(final String money) {
        if (flag == 2) {
            if (tradePass == 0) {
                new CommonDialog(OrderMenuActivity.this, "温馨提示!", "您还没有设置交易密码,是否设置?", "是", "否").setCommonListener(new CommonDialog.CommonListener() {
                    @Override
                    public void sure(int flag) {
                        if (1 == flag) {
                            mShouNewApplication.redirect(TransactionActivity.class);
                        }
                    }
                }).setCancelable(true).show();
                return;
            }
        }


        PopEnterPassword popEnterPassword = new PopEnterPassword(this, StringUtil.formatMoney(money));
        popEnterPassword.
                setInputLisener(new PopEnterPassword.InputLisener() {
                    @Override
                    public void result(String pass) {
                        payWallet(mOrderzNo, pass, money);
                    }
                });

        popEnterPassword.showAtLocation(OrderMenuActivity.this.findViewById(R.id.parent), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置;
    }

    private void payWallet(final String orderzNo, String passStr, final String money) {
        mUserAPI.walletPay(orderzNo, passStr, mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void onLoading() {
                super.onLoading();
                createLoadingDialog();
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                Bundle bundle = new Bundle();
                bundle.putString("orderzNo", orderzNo);
                bundle.putInt("flag", flag);
                bundle.putString("money", StringUtil.formatMoney(money));
                closeLoadingDialog();
                if (exception == null) {
                    if (json.has("data")) {
                        try {
                            JSONObject jsonObject = json.getJSONObject("data");
                            if (jsonObject.has("trade_state")) {
                                ///3000—支付成功 3001—订单已支付 3002—支付密码错误 3003—余额不足 3004—支付失败
                                String trade_state = jsonObject.getString("trade_state");
                                if ("3000".equals(trade_state)) {
                                    ToastUtil.showToast("支付成功");
                                    refresh();
//                                    if (!TextUtils.isEmpty(mOrderzNo)) {
//                                        bundle.putBoolean("isSucess", true);
//                                        mShouNewApplication.redirectAndPrameter(PayStateActivity.class, bundle);
//                                        return;
//                                    }
                                } else if ("3001".equals(trade_state)) {
                                    ToastUtil.showToast("订单已支付");
                                    return;
                                } else if ("3002".equals(trade_state)) {
                                    ToastUtil.showToast("支付密码错误");
                                } else if ("3003".equals(trade_state)) {
                                    new CommonDialog(OrderMenuActivity.this, "余额不足,请充值").setCommonListener(new CommonDialog.CommonListener() {
                                        @Override
                                        public void sure(int flag) {
                                            if (1 == flag) {
                                                mainApplication.redirect(AccountRechargeActivity.class);
                                            }
                                        }
                                    }).setCancelable(true).show();
                                    return;
                                } else if ("3004".equals(trade_state)) {
                                    //                                    ToastUtil.showToast("支付失败");
                                }
                                bundle.putBoolean("isSucess", false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    bundle.putBoolean("isSucess", false);
                }
//                mShouNewApplication.redirectAndPrameter(PayStateActivity.class, bundle);
            }
        });
    }

    /**
     * 支付包 相关ui处理
     */
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AlipayPayUtils.SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     * 8000—正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
                     4000—订单支付失败
                     5000—重复请求
                     6001—用户中途取消
                     6002—网络连接出错
                     */

                    final Bundle bundle = new Bundle();
                    bundle.putString("orderzNo", mOrderzNo);
                    bundle.putInt("flag", flag);
                    closeLoadingDialog();
                    mUserAPI.checkAlipayOrderz(payResult.toString(), mShouNewApplication.new ShouNewHttpCallBackLisener() {
                        @Override
                        protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                            if (exception == null) {
                                if (json.has("data")) {
                                    String msgTips;
                                    try {
                                        JSONObject jsonObject;
                                        jsonObject = json.getJSONObject("data");
                                        if (jsonObject.has("trade_state")) {
                                            String state = jsonObject.getString("trade_state");
                                            if (TextUtils.equals("9000", state)) {
                                                msgTips = "支付成功";
//                                                bundle.putBoolean("isSucess", true);
//                                                mShouNewApplication.redirectAndPrameter(PayStateActivity.class, bundle);
//                                                return;
                                                refresh();
                                            } else if (TextUtils.equals("8000", state)) {
                                                msgTips = "正在处理中";
                                            } else if (TextUtils.equals("4000", state)) {
                                                msgTips = "订单支付失败";
                                            } else if (TextUtils.equals("5000", state)) {
                                                msgTips = "重复请求";
                                            } else if (TextUtils.equals("6001", state)) {
                                                //                                                msgTips = "用户中途取消";
                                                msgTips = "支付失败";
                                            } else if (TextUtils.equals("6002", state)) {
                                                msgTips = "网络连接出错";
                                            } else {
                                                msgTips = "支付失败";
                                            }
                                            ToastUtil.showToast(msgTips);
//                                            bundle.putBoolean("isSucess", false);
//                                            mShouNewApplication.redirectAndPrameter(PayStateActivity.class, bundle);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    });

                    break;


                }
            }

        }
    };


    /**
     * 微信支付
     */
    private void wxCharge() {
        mUserAPI.wxCharge(mOrderzNo, mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void onLoading() {
                super.onLoading();
                createLoadingDialog();
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (exception == null) {
                    if (null != json) {
                        if (json.has("data")) {
                            try {
                                JSONObject tenpayInfo = json.getJSONObject("data").getJSONObject("tenpayInfo");
                                Preference.putString(mShouNewApplication, Config.ORDER, mOrderzNo);
                                Preference.putInt(mShouNewApplication, Config.SHOP_TYPE, 1);
                                Preference.putInt(mShouNewApplication, Config.FLAG, flag);
                                WXPay.getInstans(OrderMenuActivity.this).WxPay(tenpayInfo);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                } else {
                    closeLoadingDialog();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null)
            mHandler.removeCallbacks(null);
    }

    public void shopTalk(OderMenuEntity menuEntity) {
        if (menuEntity.getoIsdiscuss() == 0 || menuEntity.getoIsdiscuss() == 1) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("menuEntity", menuEntity);
            mShouNewApplication.redirectAndPrameter(LaunchEvaluateActivity.class, bundle);
        }

    }
}