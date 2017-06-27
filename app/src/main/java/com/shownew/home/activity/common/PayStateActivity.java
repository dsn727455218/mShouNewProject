package com.shownew.home.activity.common;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.shownew.home.Config;
import com.shownew.home.R;
import com.shownew.home.ShouNewApplication;
import com.shownew.home.activity.AccountRechargeActivity;
import com.shownew.home.activity.OrderMenuActivity;
import com.shownew.home.module.UserAPI;
import com.shownew.home.pay.AlipayPayUtils;
import com.shownew.home.pay.PayResult;
import com.shownew.home.pay.WXPay;
import com.shownew.home.utils.dialog.CommonDialog;
import com.shownew.home.view.PopEnterPassword;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.wp.baselib.AndroidActivity;
import com.wp.baselib.utils.Preference;
import com.wp.baselib.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;


public class PayStateActivity extends AndroidActivity implements View.OnClickListener {
    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
    private IWXAPI api;
    private TextView mPay_state;
    private Button mLookOrderMenu;
    private Button mGotoShop;
    private UserAPI mUserAPI;
    private ShouNewApplication mShouNewApplication;
    private String mOrderzNo;
    private int flag;
    private boolean isSucess;
    private String mMoney;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        mShouNewApplication = ShouNewApplication.getInstance();
        mUserAPI = new UserAPI(mShouNewApplication);
        initViews();
        if (mBundle != null) {
            mOrderzNo = mBundle.getString("orderzNo");
            flag = mBundle.getInt("flag");
            isSucess = mBundle.getBoolean("isSucess");
            mMoney = mBundle.getString("money");
            isSucess(isSucess);
        }
    }

    private void initViews() {
        mPay_state = (TextView) findViewById(R.id.pay_state);
        mGotoShop = (Button) findViewById(R.id.goto_shop);
        mGotoShop.setOnClickListener(this);
        mLookOrderMenu = (Button) findViewById(R.id.look_odermenu);
        mLookOrderMenu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goto_shop:
                String gotoShop = mGotoShop.getText().toString().trim();
                if ("回到超市".equals(gotoShop)) {
                    //                    mainApplication.redirect(SupermarketActivity.class);
                    finish();
                } else if ("重新支付".equals(gotoShop)) {
                    pay();
                }
                break;
            case R.id.look_odermenu:
                String order = mLookOrderMenu.getText().toString().trim();
                if ("查看订单".equals(order)) {
                    mainApplication.redirect(OrderMenuActivity.class);
                }
                //                else if ("回到商品".equals(order)) {
                //                }
                finish();
                break;
        }

    }

    private void pay() {
        mGotoShop.setEnabled(false);
        switch (flag) {
            case 1:
                createLoadingDialog();
                alipayCharge(mOrderzNo);
                break;
            case 2:

                shouniuCharge(mOrderzNo);
                break;
            case 3:
                createLoadingDialog();
                wxCharge(mOrderzNo);
                break;
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
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (exception == null) {
                    if (null != json) {
                        if (json.has("data")) {
                            try {
                                JSONObject tenpayInfo = json.getJSONObject("data").getJSONObject("tenpayInfo");
                                Preference.putString(mShouNewApplication, Config.ORDER, orderzNo);
                                Preference.putInt(mShouNewApplication, Config.SHOP_TYPE, 2);
                                Preference.putInt(mShouNewApplication, Config.FLAG, flag);
                                WXPay.getInstans(PayStateActivity.this).WxPay(tenpayInfo);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                isSucess(false);
                            }

                        }
                    }
                } else {
                    closeLoadingDialog();
                    mGotoShop.setEnabled(true);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mBundle != null) {
            if (mBundle.getBoolean("isSucess", false)) {
                isSucess(true);
            } else {
                isSucess(false);
            }
            mGotoShop.setEnabled(true);
            closeLoadingDialog();
        }

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
                     */
                    mGotoShop.setEnabled(true);
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
                                                isSucess(true);
                                                return;
                                            } else if (TextUtils.equals("8000", state)) {
                                                msgTips = "正在处理中";

                                            } else if (TextUtils.equals("4000", state)) {

                                                msgTips = "订单支付失败";
                                            } else if (TextUtils.equals("5000", state)) {

                                                msgTips = "重复请求";
                                            } else if (TextUtils.equals("6001", state)) {
                                                msgTips = "用户中途取消";

                                            } else if (TextUtils.equals("6002", state)) {
                                                msgTips = "网络连接出错";
                                            } else {
                                                msgTips = "支付失败";
                                            }
                                            isSucess(false);
                                            ToastUtil.showToast(msgTips);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        isSucess(false);
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
                                new AlipayPayUtils(PayStateActivity.this, mHandler, tenpayInfo);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                isSucess(false);
                            }

                        }
                    }
                } else {
                    isSucess(false);
                    closeLoadingDialog();
                    mGotoShop.setEnabled(true);
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
        PopEnterPassword popEnterPassword = new PopEnterPassword(this, mMoney).
                setInputLisener(new PopEnterPassword.InputLisener() {
                    @Override
                    public void result(String pass) {
                        payWallet(orderzNo, pass);
                    }
                });
        popEnterPassword.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mGotoShop.setEnabled(true);
            }
        });
        popEnterPassword.showAtLocation(PayStateActivity.this.findViewById(R.id.parent), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置;
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
                                    isSucess(true);
                                    return;
                                } else if ("3001".equals(trade_state)) {
                                    isSucess(false);
                                    ToastUtil.showToast("订单已支付");
                                } else if ("3002".equals(trade_state)) {
                                    ToastUtil.showToast("支付密码错误");
                                    isSucess(false);
                                } else if ("3003".equals(trade_state)) {
                                    isSucess(false);
                                    new CommonDialog(PayStateActivity.this, "余额不足,请充值").setCommonListener(new CommonDialog.CommonListener() {
                                        @Override
                                        public void sure(int flag) {
                                            if (1 == flag) {
                                                mainApplication.redirect(AccountRechargeActivity.class);
                                            }
                                        }
                                    }).setCancelable(true).show();
                                } else if ("3004".equals(trade_state)) {
                                    isSucess(false);
                                    ToastUtil.showToast("支付失败");
                                }
                                isSucess(false);
                                mGotoShop.setEnabled(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            isSucess(false);
                            ToastUtil.showToast("支付失败");
                            mGotoShop.setEnabled(true);
                        }
                    }
                } else {
                    ToastUtil.showToast("支付失败");
                    isSucess(false);
                    mGotoShop.setEnabled(true);
                }
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

    private void isSucess(boolean issucess) {
        if (issucess) {
            mPay_state.setText("付款成功");
            mPay_state.setCompoundDrawablesWithIntrinsicBounds(R.drawable.determine_payment_function, 0, 0, 0);
            mGotoShop.setText("回到超市");
            mLookOrderMenu.setText("查看订单");
        } else {
            mPay_state.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lose, 0, 0, 0);
            mGotoShop.setText("重新支付");
            mLookOrderMenu.setText("回到商品");
            mPay_state.setText("付款失败");
        }
    }
}