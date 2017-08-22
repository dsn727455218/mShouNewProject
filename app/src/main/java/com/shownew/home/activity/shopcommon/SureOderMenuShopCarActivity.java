package com.shownew.home.activity.shopcommon;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.shownew.home.Config;
import com.shownew.home.R;
import com.shownew.home.activity.AccountRechargeActivity;
import com.shownew.home.activity.AddressEditActivity;
import com.shownew.home.activity.OrderMenuActivity;
import com.shownew.home.activity.TransactionActivity;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.adapter.OrderMenuListAdapter;
import com.shownew.home.module.UserAPI;
import com.shownew.home.module.dao.ShopCarEntityIml;
import com.shownew.home.module.entity.AddressEntity;
import com.shownew.home.module.entity.OrderMenuShopCarsEntity;
import com.shownew.home.pay.AlipayPayUtils;
import com.shownew.home.pay.PayResult;
import com.shownew.home.pay.WXPay;
import com.shownew.home.utils.dialog.CommonDialog;
import com.shownew.home.view.PopEnterPassword;
import com.wp.baselib.utils.JsonUtils;
import com.wp.baselib.utils.Preference;
import com.wp.baselib.utils.StringUtil;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.widget.NoScrollListView;
import com.wp.baselib.widget.TitleBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;


/**
 * 确认订单
 */
public class SureOderMenuShopCarActivity extends BaseActivity implements View.OnClickListener {
    private UserAPI mUserAPI;
    private TextView mConsignee_tv;
    private TextView mConsignee_phone;
    private TextView mConsignee_address;
    private String locationId;
    private RadioButton rbWx;

    private TextView mCount_number;
    private TextView mTotal_prices;
    private TextView mTotal_tv;
    private RadioButton rbZf;
    private RadioButton rbSn;
    private OrderMenuListAdapter orderMenuListAdapter;
    private double money;
    private String mOrderzNo;
    private View mComitOrder;
    private ArrayList<ShopCarEntityIml> shopCarEntityImls;

    private NoScrollListView order_detail_ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sure_oder_menu_shop_cars);
        mUserAPI = new UserAPI(mShouNewApplication);
        initViews();
        if (mBundle != null) {
            shopCarEntityImls = mBundle.getParcelableArrayList("shopCarEntityImls");
            if (shopCarEntityImls == null) {
                finish();
                return;
            }
            orderMenuListAdapter = new OrderMenuListAdapter(this, shopCarEntityImls);
            order_detail_ll.setAdapter(orderMenuListAdapter);
            int size = shopCarEntityImls.size();
            int shopCount = 0;
            double shopPrices = 0;
            for (int i = 0; i < size; i++) {
                ShopCarEntityIml shopCarEntityIml = shopCarEntityImls.get(i);
                shopCount += shopCarEntityIml.getShNum();
                shopPrices += shopCarEntityIml.getShKdprice();
                shopPrices += shopCarEntityIml.getShPrice();
                callBackData(shopCount, shopPrices);
                if (shopCarEntityIml.getShMpid() == 0) {
                    rbSn.setVisibility(View.GONE);
                    continue;
                }
            }

        }
    }


    private void initViews() {
        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        titleBarView.setTitle("确认订单");
        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setOnLeftOnClickListener(this);
        titleBarView.setLeftIcon(R.drawable.back_arrow);
        titleBarView.setTitleSize(20);
        findViewById(R.id.rl1).setOnClickListener(this);
        mComitOrder = findViewById(R.id.commit_order);
        mComitOrder.setOnClickListener(this);
        order_detail_ll = (NoScrollListView) findViewById(R.id.order_detail_listview);
        mConsignee_tv = (TextView) findViewById(R.id.consignee_tv);
        mConsignee_phone = (TextView) findViewById(R.id.consignee_phone);
        mConsignee_address = (TextView) findViewById(R.id.consignee_address);
        mCount_number = (TextView) findViewById(R.id.count_number);
        mTotal_prices = (TextView) findViewById(R.id.total_prices);
        mTotal_tv = (TextView) findViewById(R.id.total_tv);
        initRechargeWays();

    }

    /**
     * 用来判断    选择的哪个支付方式
     */
    private int flag = 0;

    private void initRechargeWays() {
        RadioGroup rgPay = (RadioGroup) findViewById(R.id.rg_pay);
        rbWx = (RadioButton) findViewById(R.id.rg_wx);
        rbZf = (RadioButton) findViewById(R.id.rg_zf);
        rbSn = (RadioButton) findViewById(R.id.rg_sn);

        rgPay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rg_zf:
                        flag = 1;
                        rbZf.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zhifubaopay, 0, R.drawable.determine_payment_function, 0);
                        rbSn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.shouniupay, 0, R.drawable.uncertain_payment_function, 0);
                        rbWx.setCompoundDrawablesWithIntrinsicBounds(R.drawable.weixinpay, 0, R.drawable.uncertain_payment_function, 0);
                        break;
                    case R.id.rg_sn:
                        flag = 2;
                        rbZf.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zhifubaopay, 0, R.drawable.uncertain_payment_function, 0);
                        rbSn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.shouniupay, 0, R.drawable.determine_payment_function, 0);
                        rbWx.setCompoundDrawablesWithIntrinsicBounds(R.drawable.weixinpay, 0, R.drawable.uncertain_payment_function, 0);
                        break;
                    case R.id.rg_wx:
                        flag = 0;
                        rbZf.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zhifubaopay, 0, R.drawable.uncertain_payment_function, 0);
                        rbSn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.shouniupay, 0, R.drawable.uncertain_payment_function, 0);
                        rbWx.setCompoundDrawablesWithIntrinsicBounds(R.drawable.weixinpay, 0, R.drawable.determine_payment_function, 0);
                        break;
                }
            }
        });
    }

    private void exitShow() {
        new CommonDialog(SureOderMenuShopCarActivity.this, "温馨提示", "优惠不常在,下手要赶快!", "狠心离开", "我再想想").setCommonListener(new CommonDialog.CommonListener() {
            @Override
            public void sure(int flag) {
                if (1 == flag) {
                    finish();
                }
            }
        }).setCancelable(true).show();
    }

    @Override
    public void onBackPressed() {
        exitShow();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                exitShow();
                break;

            case R.id.rl1:
                mainApplication.redirect(AddressEditActivity.class);
                break;
            case R.id.commit_order:
                submitOrderz();
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        closeLoadingDialog();
        getPayAboutInfo();
        getAddress();
        mComitOrder.setEnabled(true);
    }

    private void getAddress() {
        mUserAPI.getDefaultAddress(mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected Object parseData(String result) {
                return null;
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (null == exception) {
                    if (json.has("data")) {
                        try {
                            JSONObject jsonData = json.getJSONObject("data");
                            if (jsonData.has("location")) {
                                AddressEntity mAddressEntity = JsonUtils.fromJson(jsonData.getString("location"), AddressEntity.class);
                                if (null != mAddressEntity) {
                                    mConsignee_tv.setText(mAddressEntity.getLName());
                                    mConsignee_phone.setText(mAddressEntity.getLPhone());
                                    mConsignee_address.setText(mAddressEntity.getLCity() + mAddressEntity.getLAddress());
                                    locationId = mAddressEntity.getLId();
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

    private void submitOrderz() {
        money = 0;
        ArrayList<OrderMenuShopCarsEntity> orderMenuShopCarsEntities = new ArrayList<OrderMenuShopCarsEntity>();
        for (ShopCarEntityIml shopCarEntityIml : shopCarEntityImls) {
            OrderMenuShopCarsEntity orderMenuShopCarsEntity = new OrderMenuShopCarsEntity();

            orderMenuShopCarsEntity.setOColor(shopCarEntityIml.getShColor());

            orderMenuShopCarsEntity.setOKdprice(shopCarEntityIml.getShKdprice());

            orderMenuShopCarsEntity.setOMpid(String.valueOf(shopCarEntityIml.getShMpid()));

            orderMenuShopCarsEntity.setOPrice(shopCarEntityIml.getShPrice());

            orderMenuShopCarsEntity.setONote(shopCarEntityIml.getoNote());

            orderMenuShopCarsEntity.setOPid(String.valueOf(shopCarEntityIml.getShPid()));

            orderMenuShopCarsEntity.setONum(shopCarEntityIml.getShNum());
            orderMenuShopCarsEntity.setoShid(shopCarEntityIml.getShId());
            orderMenuShopCarsEntities.add(orderMenuShopCarsEntity);
            money += orderMenuShopCarsEntity.getOPrice();
            money += orderMenuShopCarsEntity.getOKdprice();
        }
        if (orderMenuShopCarsEntities != null && orderMenuShopCarsEntities.size() > 0) {
            if (TextUtils.isEmpty(locationId)) {
                ToastUtil.showToast("请填写地址");
                return;
            }
            if (flag == 2) {
                if (tradePass == 0) {
                    new CommonDialog(SureOderMenuShopCarActivity.this, "温馨提示!", "您还没有设置交易密码,是否设置?", "是", "否").setCommonListener(new CommonDialog.CommonListener() {
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


            String json = JsonUtils.toJson(orderMenuShopCarsEntities);
            mUserAPI.submitOrderzShopCarts(locationId, flag, json, mShouNewApplication.new ShouNewHttpCallBackLisener() {
                @Override
                protected void onLoading() {
                    super.onLoading();
                    mComitOrder.setEnabled(false);
                }

                @Override
                protected void resultData(Object data, JSONObject json, Response response, Exception exception) {

                    if (exception == null) {
                        if (null != json) {
                            if (json.has("data")) {
                                try {
                                    JSONObject jsonObject = json.getJSONObject("data");
                                    mOrderzNo = jsonObject.getString("orderzNo");
                                    switch (flag) {
                                        case 1:
                                            alipayCharge(mOrderzNo);
                                            break;
                                        case 2:
                                            shouniuCharge(mOrderzNo);
                                            break;
                                        case 0:
                                            wxCharge(mOrderzNo);
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    } else {
                        mComitOrder.setEnabled(true);
                    }

                }
            });

        }


    }

    /**
     * 微信支付
     *
     * @param orderzNo
     */
    private void wxCharge(final String orderzNo) {
        mUserAPI.wxCharge(orderzNo, mShouNewApplication.new ShouNewHttpCallBackLisener() {
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
                                Preference.putString(mShouNewApplication, Config.ORDER, orderzNo);
                                Preference.putInt(mShouNewApplication, Config.SHOP_TYPE, 1);
                                Preference.putInt(mShouNewApplication, Config.FLAG, flag);
                                Preference.putInt(mShouNewApplication, Config.Carts, 100);

                                WXPay.getInstans(SureOderMenuShopCarActivity.this).WxPay(tenpayInfo);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                } else {
                    closeLoadingDialog();
                    mComitOrder.setEnabled(false);
                }
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
                    mComitOrder.setEnabled(true);
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
                                                mainApplication.redirect(OrderMenuActivity.class);
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
                                            bundle.putBoolean("isSucess", false);
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
     * 支付宝支付
     *
     * @param orderzNo
     */

    private void alipayCharge(final String orderzNo) {

        mUserAPI.alipayCharge(orderzNo, mShouNewApplication.new ShouNewHttpCallBackLisener() {


            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (exception == null) {
                    if (null != json) {
                        if (json.has("data")) {
                            try {
                                String tenpayInfo = json.getJSONObject("data").getString("alipayInfo");
                                new AlipayPayUtils(SureOderMenuShopCarActivity.this, mHandler, tenpayInfo);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                } else {
                    mComitOrder.setEnabled(true);
                }
            }
        });

    }

    /**
     * 首牛支付
     *
     * @param orderzNo
     */
    private void shouniuCharge(final String orderzNo) {
        PopEnterPassword popEnterPassword = new PopEnterPassword(this, StringUtil.formatMoney(money));
        popEnterPassword.
                setInputLisener(new PopEnterPassword.InputLisener() {
                    @Override
                    public void result(String pass) {
                        payWallet(orderzNo, pass);
                    }


                });
        popEnterPassword.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mComitOrder.setEnabled(true);
            }
        });
        popEnterPassword.showAtLocation(SureOderMenuShopCarActivity.this.findViewById(R.id.parent), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置;
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

    private void payWallet(final String orderzNo, String passStr) {
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
//                                    if (!TextUtils.isEmpty(mOrderzNo)) {
//                                        bundle.putBoolean("isSucess", true);
//                                        mComitOrder.setEnabled(true);
//                                        mShouNewApplication.redirectAndPrameter(PayStateActivity.class, bundle);
//                                        return;
//                                    }
                                    mainApplication.redirect(OrderMenuActivity.class);
                                } else if ("3001".equals(trade_state)) {
                                    ToastUtil.showToast("订单已支付");
                                    return;
                                } else if ("3002".equals(trade_state)) {
                                    ToastUtil.showToast("支付密码错误");
                                } else if ("3003".equals(trade_state)) {
                                    new CommonDialog(SureOderMenuShopCarActivity.this, "余额不足,请充值").setCommonListener(new CommonDialog.CommonListener() {
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
                                mComitOrder.setEnabled(true);
                                bundle.putBoolean("isSucess", false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    mComitOrder.setEnabled(true);
                    bundle.putBoolean("isSucess", false);
                }


//                mShouNewApplication.redirectAndPrameter(PayStateActivity.class, bundle);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mHandler) {
            mHandler.removeCallbacks(null);
        }
    }


    public void callBackData(int shopCount, double shopPrices) {
        mTotal_prices.setText(String.format("¥%s", StringUtil.formatMoney(shopPrices)));
        mCount_number.setText(String.format("共%s件商品  小计:", shopCount));
        mTotal_tv.setText(String.format("合计:¥%s", StringUtil.formatMoney(shopPrices)));
    }
}
