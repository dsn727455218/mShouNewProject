package com.shownew.home.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.shownew.home.Config;
import com.shownew.home.R;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.adapter.AccountRechargeAdapter;
import com.shownew.home.module.UserAPI;
import com.shownew.home.module.entity.SelectAccountMoneyEntity;
import com.shownew.home.pay.AlipayPayUtils;
import com.shownew.home.pay.PayResult;
import com.shownew.home.pay.WXPay;
import com.shownew.home.utils.dialog.CommonDialog;
import com.wp.baselib.utils.Preference;
import com.wp.baselib.utils.StringUtil;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.widget.TitleBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;


/**
 * 账户充值
 */
public class AccountRechargeActivity extends BaseActivity implements View.OnClickListener {

    private AccountRechargeAdapter mAccountRechargeAdapter;
    private UserAPI mUserAPI;
    private RadioButton mRbZf;
    private RadioButton mRbWx;
    private ArrayList<SelectAccountMoneyEntity> mSelectAccountMoneyEntities;

    private EditText mZidingyi_money;
    private View mPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_recharge);
        mUserAPI = new UserAPI(mShouNewApplication);
        initViews();
    }

    private int payWay = 0;

    private void initViews() {
        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        titleBarView.setTitle("账户充值");
        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setOnLeftOnClickListener(this);
        titleBarView.setLeftIconAndText(R.drawable.back_arrow, "返回");
        titleBarView.setTitleSize(20);
        GridView mMGridView;
        mMGridView = (GridView) findViewById(R.id.recharge_money_noscorllgrivView);
        mSelectAccountMoneyEntities = new ArrayList<SelectAccountMoneyEntity>();
        mAccountRechargeAdapter = new AccountRechargeAdapter(this, mSelectAccountMoneyEntities);
        mMGridView.setAdapter(mAccountRechargeAdapter);
        mPay = findViewById(R.id.pay);
        mPay.setOnClickListener(this);
        mZidingyi_money = (EditText) findViewById(R.id.zidingyi_money);

        mZidingyi_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString().trim();
                int length = s.length();
                if (text.contains(".")) {
                    if (length - 1 - text.indexOf(".") > 2) {
                        s = text.subSequence(0, text.indexOf(".") + 3);
                        mZidingyi_money.setText(s);
                        mZidingyi_money.setSelection(length - 1);
                    }
                }
                if (text.equals(".")) {
                    s = "0" + s;
                    mZidingyi_money.setText(s);
                    mZidingyi_money.setSelection(2);
                }
                if (text.startsWith("0") && length > 1) {
                    if (!text.substring(1, 2).equals(".")) {
                        mZidingyi_money.setText(s.subSequence(0, 1));
                        mZidingyi_money.setSelection(1);
                    }
                }
                String trim = mZidingyi_money.getText().toString().trim();
                if (!TextUtils.isEmpty(trim)) {
                    float i = Float.parseFloat(trim);
                    if (i > 100000) {
                        mZidingyi_money.setText(StringUtil.formatMoney(100000));
                        mZidingyi_money.setSelection(mZidingyi_money.getText().length());
                    }
                }
                if (!TextUtils.isEmpty(text)) {
                    for (SelectAccountMoneyEntity accountMoneyEntity : mSelectAccountMoneyEntities) {
                        accountMoneyEntity.setSelect(false);
                    }
                    selectMoney = "0";
                    mAccountRechargeAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        RadioGroup mRgPay = (RadioGroup) findViewById(R.id.rg_pay);
        mRbZf = (RadioButton) findViewById(R.id.rg_zf);
        mRbWx = (RadioButton) findViewById(R.id.rg_wx);
        mRgPay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rg_zf:
                        payWay = 1;
                        mRbZf.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zhifubaopay, 0, R.drawable.determine_payment_function, 0);
                        mRbWx.setCompoundDrawablesWithIntrinsicBounds(R.drawable.weixinpay, 0, R.drawable.uncertain_payment_function, 0);
                        break;
                    case R.id.rg_wx:
                        payWay = 0;
                        mRbZf.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zhifubaopay, 0, R.drawable.uncertain_payment_function, 0);
                        mRbWx.setCompoundDrawablesWithIntrinsicBounds(R.drawable.weixinpay, 0, R.drawable.determine_payment_function, 0);
                        break;
                }
            }
        });
        getPayList();
    }

    private String selectMoney;

    @Override
    protected void onResume() {
        super.onResume();
        getPayAboutInfo();
        if (Preference.getBoolean(mShouNewApplication, Config.WX_STATE, false)) {
            Preference.putBoolean(mShouNewApplication, Config.WX_STATE, false);
        }
        mPay.setEnabled(true);
        closeLoadingDialog();
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

    /**
     * 获取充值的额度
     */
    private void getPayList() {
        mUserAPI.getPayList(mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (null == exception) {
                    if (json.has("data")) {
                        mSelectAccountMoneyEntities.clear();
                        try {
                            JSONObject jsonData = json.getJSONObject("data");

                            if (jsonData.has("payList")) {
                                JSONArray jsonArray = jsonData.getJSONArray("payList");
                                ArrayList<SelectAccountMoneyEntity> selectAccountMoneyEntities = new ArrayList<SelectAccountMoneyEntity>();
                                int length = jsonArray.length();
                                for (int i = 0; i < length; i++) {
                                    if (i == 0) {
                                        selectMoney = jsonArray.getString(i);
                                        selectAccountMoneyEntities.add(new SelectAccountMoneyEntity(jsonArray.getString(i), true));
                                    } else {
                                        selectAccountMoneyEntities.add(new SelectAccountMoneyEntity(jsonArray.getString(i), false));
                                    }
                                }
                                mSelectAccountMoneyEntities.addAll(selectAccountMoneyEntities);
                                mAccountRechargeAdapter.notifyDataSetChanged();
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
     * 获取余额、首次充值接口
     * <p>
     * "data": {
     * "isFirst": 0,
     * "remain": 990
     * },
     */

    private int isFirst;


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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacks(null);
        }
    }

    /**
     * 支付包 相关ui处理
     */
    private Handler mHandler = new Handler() {
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
                }
                break;
            }

        }
    };

    private String mOrderzNo;
    private double money = 0;

    /**
     * 支付
     */
    private void pay() {

        if (!TextUtils.isEmpty(selectMoney)) {
            money = Double.parseDouble(selectMoney);
        }
        String zidingyimoney = mZidingyi_money.getText().toString();
        if (!TextUtils.isEmpty(zidingyimoney) && 0 != Double.parseDouble(zidingyimoney)) {
            money = Double.parseDouble(zidingyimoney);
        }
        if (money <= 0) {
            ToastUtil.showToast("请选择充值金额");
            return;
        }
        if (isFirst == 0 && money >= 298) {
            new CommonDialog(this, "您是首次充值,金额高于298会赠送礼品,是否需要获取礼品?").setCommonListener(new CommonDialog.CommonListener() {
                @Override
                public void sure(int flag) {
                    if (1 == flag) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("isFirst", isFirst);
                        bundle.putDouble("money", money);
                        mShouNewApplication.redirectAndPrameter(ActionPayActivity.class, bundle);
                    }
                }
            }).setCancelable(true).show();
            return;
        } else {
            isFirst = 1;
        }
        mPay.setEnabled(false);
        mUserAPI.pay(StringUtil.formatMoney(money), 0, payWay, isFirst, "", "", "", mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (exception == null) {
                    try {
                        if (json.has("data")) {
                            JSONObject jsonObject = json.getJSONObject("data");

                            if (jsonObject.has("orderzNo")) {
                                mOrderzNo = jsonObject.getString("orderzNo");
                            }
                            if (jsonObject.has("alipayInfo")) {
                                new AlipayPayUtils(AccountRechargeActivity.this, mHandler, jsonObject.getString("alipayInfo"));
                            }

                            if (jsonObject.has("tenpayInfo")) {
                                createLoadingDialog();
                                Preference.putString(mShouNewApplication, Config.ORDER, mOrderzNo);
                                Preference.putInt(mShouNewApplication, Config.SHOP_TYPE, 2);
                                Preference.putInt(mShouNewApplication, Config.FLAG, payWay);
                                WXPay.getInstans(AccountRechargeActivity.this).WxPay(jsonObject.getJSONObject("tenpayInfo"));
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    mPay.setEnabled(true);
                }
            }


        });


    }


    public void itemClick(String money) {
        selectMoney = money;
    }
}
