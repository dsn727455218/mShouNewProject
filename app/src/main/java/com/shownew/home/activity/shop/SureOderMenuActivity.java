package com.shownew.home.activity.shop;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.shownew.home.Config;
import com.shownew.home.R;
import com.shownew.home.activity.AccountRechargeActivity;
import com.shownew.home.activity.AddressEditActivity;
import com.shownew.home.activity.TransactionActivity;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.activity.common.PayStateActivity;
import com.shownew.home.module.UserAPI;
import com.shownew.home.module.entity.AddressEntity;
import com.shownew.home.module.entity.SuperMarkeDetailEntity;
import com.shownew.home.pay.AlipayPayUtils;
import com.shownew.home.pay.PayResult;
import com.shownew.home.pay.WXPay;
import com.shownew.home.utils.dialog.CommonDialog;
import com.shownew.home.view.PopEnterPassword;
import com.wp.baselib.utils.JsonUtils;
import com.wp.baselib.utils.Preference;
import com.wp.baselib.utils.StringUtil;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.widget.TitleBarView;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;


/**
 * 确认订单
 */
public class SureOderMenuActivity extends BaseActivity implements View.OnClickListener {
    private SuperMarkeDetailEntity mSuperMarkeDetailEntity;
    private String mColor;
    private int mNumber;
    private ImageView mShop_img;
    private TextView mShop_title;
    private TextView mShop_color;
    private TextView mShop_prices;
    private EditText mShop_number;
    private TextView mKuaidi_fee;
    private TextView mCount_number;
    private TextView mTotal_prices;
    private UserAPI mUserAPI;
    private TextView mConsignee_tv;
    private TextView mConsignee_phone;
    private TextView mConsignee_address;
    private String locationId;
    private RadioGroup rgPay;
    private RadioButton rbWx;
    private RadioButton rbZf;
    private RadioButton rbSn;
    private double prices;
    private double money;
    private String mOrderzNo;
    private TextView mTotal_tv;
    private EditText mKuaidi_fee_ed;
    private View mComitOrder;
    private TextView mTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sure_oder_menu);
        mUserAPI = new UserAPI(mShouNewApplication);
        initViews();
        if (mBundle != null) {
            mSuperMarkeDetailEntity = mBundle.getParcelable("mSuperMarkeDetailEntity");
            mColor = mBundle.getString("color");
            prices = mBundle.getDouble("prices");
            mShop_color.setText(String.format("分类：%s", mColor));
            mNumber = mBundle.getInt("number");
            mShop_number.setText(String.valueOf(mNumber));

        }
        setData();
    }

    private void setData() {
        if (mSuperMarkeDetailEntity != null) {
            mKuaidi_fee.setText(String.format("快递：%s元", StringUtil.formatMoney(mSuperMarkeDetailEntity.getPKdprice())));
            mShop_prices.setText(String.format("¥%s", StringUtil.formatMoney(prices)));

            mShop_title.setText(mSuperMarkeDetailEntity.getPTitle());
            Glide.with(SureOderMenuActivity.this).load(mSuperMarkeDetailEntity.getPSimg()).asBitmap().placeholder(R.drawable.square_seize).error(R.drawable.square_seize).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                    mShop_img.setImageBitmap(bitmap);
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    super.onLoadFailed(e, errorDrawable);
                    mShop_img.setImageDrawable(errorDrawable);
                }
            });
            mCount_number.setText(String.format("共%s件商品  小计:", mNumber));
            mTotal_prices.setText(String.format("¥%s", StringUtil.formatMoney(prices * mNumber)));
            money = prices * mNumber + mSuperMarkeDetailEntity.getPKdprice();
            mTotal_tv.setText(String.format("合计:¥%s", StringUtil.formatMoney(money)));
            if ("首牛".equals(mSuperMarkeDetailEntity.getpOwn())) {
                mTags.setVisibility(View.VISIBLE);
            } else {
                rbSn.setVisibility(View.GONE);
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
        mTotal_tv = (TextView) findViewById(R.id.total_tv);
        mComitOrder = findViewById(R.id.commit_order);
        mComitOrder.setOnClickListener(this);
        mShop_img = (ImageView) findViewById(R.id.shop_img);
        mShop_title = (TextView) findViewById(R.id.shop_title);
        mKuaidi_fee_ed = (EditText) findViewById(R.id.kuaidi_fee_ed);
        mShop_color = (TextView) findViewById(R.id.shop_color);
        mShop_prices = (TextView) findViewById(R.id.shop_prices);
        mShop_number = (EditText) findViewById(R.id.shop_number);
        mKuaidi_fee = (TextView) findViewById(R.id.kuaidi_fee);
        mCount_number = (TextView) findViewById(R.id.count_number);
        mTotal_prices = (TextView) findViewById(R.id.total_prices);
        findViewById(R.id.reduce_number).setOnClickListener(this);
        findViewById(R.id.add_number).setOnClickListener(this);

        mConsignee_tv = (TextView) findViewById(R.id.consignee_tv);
        mConsignee_phone = (TextView) findViewById(R.id.consignee_phone);
        mConsignee_address = (TextView) findViewById(R.id.consignee_address);
        mTags = (TextView) findViewById(R.id.tags);
        initRechargeWays();
    }

    /**
     * 用来判断    选择的哪个支付方式
     */
    private int flag = 0;

    private void initRechargeWays() {
        rgPay = (RadioGroup) findViewById(R.id.rg_pay);
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
        new CommonDialog(SureOderMenuActivity.this, "温馨提示", "优惠不常在,下手要赶快!", "狠心离开", "我再想想").setCommonListener(new CommonDialog.CommonListener() {
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
            case R.id.add_number:
                if (mNumber < 9999) {
                    mNumber++;
                    mShop_number.setText(String.valueOf(mNumber));
                }
                setMoneyData();
                break;
            case R.id.reduce_number:
                mNumber = Integer.parseInt(mShop_number.getText().toString().trim());
                if (mNumber > 1) {
                    mNumber--;
                    mShop_number.setText(String.valueOf(mNumber));
                }
                setMoneyData();

                break;
            case R.id.rl1:
                mainApplication.redirect(AddressEditActivity.class);
                break;
            case R.id.commit_order:
                submitOrderz();
                break;
        }
    }

    private void setMoneyData() {
        money = prices * mNumber + mSuperMarkeDetailEntity.getPKdprice();
        mCount_number.setText(String.format("共%s件商品  小计:", StringUtil.formatMoney(money)));
        mTotal_prices.setText(String.format("¥%s", StringUtil.formatMoney(prices * mNumber)));
        mTotal_tv.setText(String.format("合计:¥%s", StringUtil.formatMoney(money)));
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

        if (mSuperMarkeDetailEntity != null) {
            if (TextUtils.isEmpty(locationId)) {
                ToastUtil.showToast("请填写地址");
                return;
            }
            if (flag == 2) {
                if (tradePass == 0) {
                    new CommonDialog(SureOderMenuActivity.this, "温馨提示!", "您还没有设置交易密码,是否设置?", "是", "否").setCommonListener(new CommonDialog.CommonListener() {
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

            String note = mKuaidi_fee_ed.getText().toString();
            mUserAPI.submitOrderz(locationId, StringUtil.formatMoney(money - mSuperMarkeDetailEntity.getPKdprice()), note, mSuperMarkeDetailEntity.getPId(), mColor, mNumber, flag, mShouNewApplication.new ShouNewHttpCallBackLisener() {
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

                                WXPay.getInstans(SureOderMenuActivity.this).WxPay(tenpayInfo);
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
                                                bundle.putBoolean("isSucess", true);
                                                mShouNewApplication.redirectAndPrameter(PayStateActivity.class, bundle);
                                                return;
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
                                            mShouNewApplication.redirectAndPrameter(PayStateActivity.class, bundle);
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
                                new AlipayPayUtils(SureOderMenuActivity.this, mHandler, tenpayInfo);
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
        popEnterPassword.showAtLocation(SureOderMenuActivity.this.findViewById(R.id.parent), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置;
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
                                    if (!TextUtils.isEmpty(mOrderzNo)) {
                                        bundle.putBoolean("isSucess", true);
                                        mComitOrder.setEnabled(true);
                                        mShouNewApplication.redirectAndPrameter(PayStateActivity.class, bundle);
                                        return;
                                    }
                                } else if ("3001".equals(trade_state)) {
                                    ToastUtil.showToast("订单已支付");
                                    return;
                                } else if ("3002".equals(trade_state)) {
                                    ToastUtil.showToast("支付密码错误");
                                } else if ("3003".equals(trade_state)) {
                                    new CommonDialog(SureOderMenuActivity.this, "余额不足,请充值").setCommonListener(new CommonDialog.CommonListener() {
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
                mShouNewApplication.redirectAndPrameter(PayStateActivity.class, bundle);
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
}
