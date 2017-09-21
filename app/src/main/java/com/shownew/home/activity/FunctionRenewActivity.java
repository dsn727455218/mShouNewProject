package com.shownew.home.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.shownew.home.Config;
import com.shownew.home.R;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.adapter.FunctionRenewAdater;
import com.shownew.home.module.UserAPI;
import com.shownew.home.module.entity.SelectEntity;
import com.shownew.home.module.entity.SourcesEntity;
import com.shownew.home.pay.AlipayPayUtils;
import com.shownew.home.pay.PayResult;
import com.shownew.home.pay.WXPay;
import com.shownew.home.utils.dialog.CommonDialog;
import com.shownew.home.view.PopEnterPassword;
import com.wp.baselib.utils.Preference;
import com.wp.baselib.utils.StringUtil;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.widget.NoScrollGridView;
import com.wp.baselib.widget.TitleBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;


public class FunctionRenewActivity extends BaseActivity implements View.OnClickListener {
    private RadioButton mRbZf;
    private RadioButton mRbSn;
    private RadioButton mRbWx;
    private FunctionRenewAdater mFunctionRenewAdater;
    private ArrayList<SelectEntity> mSelectEntities = new ArrayList<SelectEntity>();
    private UserAPI mUserAPI;
    private ImageView mBattery_day_tv;
    private int payWay = 0;
    private View mPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_renew);
        mUserAPI = new UserAPI(mShouNewApplication);
        initViews();
        ArrayList<SourcesEntity> sourcesEntities = mUserAPI.getSourcesData();
        if (sourcesEntities != null && sourcesEntities.size() >= 4) {
            final String url = sourcesEntities.get(3).getSImg();
            Glide.with(this).load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.renew_seize).error(R.drawable.renew_seize).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    mBattery_day_tv.setImageBitmap(resource);
                }
            });
        }
    }

    private void initViews() {
        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        titleBarView.setTitle("功能续费");
        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setOnLeftOnClickListener(this);
        titleBarView.setLeftIconAndText(R.drawable.back_arrow, "返回");
        titleBarView.setTitleSize(20);
        NoScrollGridView mNoScrollGridView = (NoScrollGridView) findViewById(R.id.func_gridView);
        mBattery_day_tv = (ImageView) findViewById(R.id.battery_day_tv);
        mFunctionRenewAdater = new FunctionRenewAdater(this, mSelectEntities);
        mNoScrollGridView.setAdapter(mFunctionRenewAdater);
        mPay = findViewById(R.id.pay);
        mPay.setOnClickListener(this);
        RadioGroup mRgPay = (RadioGroup) findViewById(R.id.rg_pay);
        mRbZf = (RadioButton) findViewById(R.id.rg_zf);
        mRbSn = (RadioButton) findViewById(R.id.rg_sn);
        mRbWx = (RadioButton) findViewById(R.id.rg_wx);
        mRgPay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rg_zf:
                        payWay = 1;
                        mRbZf.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zhifubaopay, 0, R.drawable.determine_payment_function, 0);
                        mRbSn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.shouniupay, 0, R.drawable.uncertain_payment_function, 0);
                        mRbWx.setCompoundDrawablesWithIntrinsicBounds(R.drawable.weixinpay, 0, R.drawable.uncertain_payment_function, 0);
                        break;
                    case R.id.rg_sn:
                        payWay = 2;
                        mRbZf.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zhifubaopay, 0, R.drawable.uncertain_payment_function, 0);
                        mRbSn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.shouniupay, 0, R.drawable.determine_payment_function, 0);
                        mRbWx.setCompoundDrawablesWithIntrinsicBounds(R.drawable.weixinpay, 0, R.drawable.uncertain_payment_function, 0);
                        break;
                    case R.id.rg_wx:
                        payWay = 0;
                        mRbZf.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zhifubaopay, 0, R.drawable.uncertain_payment_function, 0);
                        mRbSn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.shouniupay, 0, R.drawable.uncertain_payment_function, 0);
                        mRbWx.setCompoundDrawablesWithIntrinsicBounds(R.drawable.weixinpay, 0, R.drawable.determine_payment_function, 0);
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                finish();
                break;
            case R.id.pay:
                pay();
                break;
        }
    }

    private String selectMoney;

    /**
     * 获取选择的续费价格
     *
     * @param object
     */
    public void setSelect(SelectEntity object) {
        selectMoney = object.getIds();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRenewList();
        getPayAboutInfo();
        closeLoadingDialog();
        if (Preference.getBoolean(mShouNewApplication, Config.WX_STATE, false)) {
            Preference.putBoolean(mShouNewApplication, Config.WX_STATE, false);
        }
        mPay.setEnabled(true);
    }

    /**
     * 获取续费价格
     */
    private void getRenewList() {
        mUserAPI.getRenewList(mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (exception == null) {
                    mSelectEntities.clear();
                    if (json.has("data")) {
                        try {
                            JSONObject jsonData = json.getJSONObject("data");
                            if (jsonData.has("renewList")) {
                                JSONArray jsonArray = jsonData.getJSONArray("renewList");
                                int length = jsonArray.length();
                                ArrayList<SelectEntity> selectEntities = new ArrayList<SelectEntity>();
                                for (int i = 0; i < length; i++) {
                                    if (i == 0) {
                                        selectMoney = jsonArray.getString(i);
                                        selectEntities.add(new SelectEntity(true, jsonArray.getString(i), String.format("¥%s/月", StringUtil.formatMoney(jsonArray.getString(i)))));
                                    } else if (i == 1) {
                                        selectEntities.add(new SelectEntity(false, jsonArray.getString(i), String.format("¥%s/半年", StringUtil.formatMoney(jsonArray.getString(i)))));
                                    } else if (i == 2) {
                                        selectEntities.add(new SelectEntity(false, jsonArray.getString(i), String.format("¥%s/年", StringUtil.formatMoney(jsonArray.getString(i)))));
                                    }
                                }
                                mSelectEntities.addAll(selectEntities);
                                mFunctionRenewAdater.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }

            }
        });
    }

    private int isFirst;
    private double remain;
    private int tradePass;


    private void getPayAboutInfo() {
        mUserAPI.getPayAboutInfo(mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (exception == null) {
                    if (json.has("data")) {
                        try {
                            JSONObject jsonObject = json.getJSONObject("data");
                            if (jsonObject.has("isFirst")) {
                                isFirst = jsonObject.getInt("isFirst");
                            }
                            if (jsonObject.has("remain")) {
                                remain = jsonObject.getInt("remain");
                            }
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

    private String passStr;

    /**
     * 支付
     */
    private void pay() {

        double money = 0;
        if (!TextUtils.isEmpty(selectMoney)) {
            money = Double.parseDouble(selectMoney);
        }
        if (money <= 0) {
            ToastUtil.showToast("请选择充值金额");
            return;
        }
        if (isFirst == 0 && money >= 298) {
            new CommonDialog(this, "您是首次续费,金额高于298会赠送礼品,是否需要获取礼品").setCommonListener(new CommonDialog.CommonListener() {
                @Override
                public void sure(int flag) {
                    if (1 == flag) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("isFirst", isFirst);
                        bundle.putDouble("money", Double.parseDouble(selectMoney));
                        mShouNewApplication.redirectAndPrameter(ActionPayActivity.class, bundle);
                    }
                }
            }).setCancelable(true).show();
            return;
        }
        isFirst = 1;
        if (payWay == 2) {
            if (tradePass == 0) {
                new CommonDialog(FunctionRenewActivity.this, "温馨提示!", "您还没有设置交易密码,是否设置?", "是", "否").setCommonListener(new CommonDialog.CommonListener() {
                    @Override
                    public void sure(int flag) {
                        if (1 == flag) {
                            mShouNewApplication.redirect(TransactionActivity.class);
                        }
                    }
                }).setCancelable(true).show();
                return;
            }

            PopEnterPassword popEnterPassword = new PopEnterPassword(this, StringUtil.formatMoney(selectMoney));
            popEnterPassword.setInputLisener(new PopEnterPassword.InputLisener() {
                @Override
                public void result(String pass) {
                    passStr = pass;
                    createLoadingDialog();
                    payWallet();
                }

            });
            popEnterPassword.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    mPay.setEnabled(true);
                }
            });
            popEnterPassword.showAtLocation(FunctionRenewActivity.this.findViewById(R.id.parent_fu), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置;
        } else {
            payWallet();
        }


    }

    private void payWallet() {
        mPay.setEnabled(false);
        mUserAPI.pay(selectMoney, 1, payWay, isFirst, "", passStr, "", mShouNewApplication.new ShouNewHttpCallBackLisener() {


            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                mPay.setEnabled(true);
                if (exception == null) {
                    try {
                        if (json.has("data")) {
                            JSONObject jsonObject = json.getJSONObject("data");
                            if (jsonObject.has("orderzNo")) {
                                mOrderzNo = jsonObject.getString("orderzNo");
                            }
                            if (jsonObject.has("alipayInfo")) {
                                new AlipayPayUtils(FunctionRenewActivity.this, mHandler, jsonObject.getString("alipayInfo"));
                            }
                            if (jsonObject.has("tenpayInfo")) {
                                createLoadingDialog();
                                Preference.putString(mShouNewApplication, Config.ORDER, mOrderzNo);
                                Preference.putInt(mShouNewApplication, Config.SHOP_TYPE, 2);
                                Preference.putInt(mShouNewApplication, Config.FLAG, payWay);
                                WXPay.getInstans(FunctionRenewActivity.this).WxPay(jsonObject.getJSONObject("tenpayInfo"));
                            }

                            if (jsonObject.has("trade_state")) {
                                closeLoadingDialog();
                                ///3000—支付成功 3001—订单已支付 3002—支付密码错误 3003—余额不足 3004—支付失败
                                String trade_state = jsonObject.getString("trade_state");
                                if ("3000".equals(trade_state)) {
                                    ToastUtil.showToast("支付成功");
                                    mShouNewApplication.redirect(ConsumeRecoderActivity.class);
                                } else if ("3001".equals(trade_state)) {
                                    ToastUtil.showToast("订单已支付");
                                } else if ("3002".equals(trade_state)) {
                                    ToastUtil.showToast("支付密码错误");

                                } else if ("3003".equals(trade_state)) {

                                    new CommonDialog(FunctionRenewActivity.this, "余额不足,请充值").setCommonListener(new CommonDialog.CommonListener() {
                                        @Override
                                        public void sure(int flag) {
                                            if (1 == flag) {
                                                mainApplication.redirect(AccountRechargeActivity.class);
                                            }
                                        }
                                    }).setCancelable(true).show();
                                } else if ("3004".equals(trade_state)) {
                                    ToastUtil.showToast("支付失败");
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }

                } else {
                    closeLoadingDialog();
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
                     */
                    mPay.setEnabled(true);
                    mUserAPI.checkAlipayOrderz(payResult.toString(), mShouNewApplication.new ShouNewHttpCallBackLisener() {
                        @Override
                        protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                            if (exception == null) {
                                if (json.has("data")) {
                                    try {
                                        String msgTips = "";
                                        JSONObject jsonObject = json.getJSONObject("data");
                                        if (jsonObject.has("trade_state")) {
                                            String state = jsonObject.getString("trade_state");
                                            if (TextUtils.equals("9000", state)) {
                                                ToastUtil.showToast("支付成功");
                                                mShouNewApplication.redirect(ConsumeRecoderActivity.class);
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
                                            ToastUtil.showToast(msgTips);
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
    private String mOrderzNo;

}
