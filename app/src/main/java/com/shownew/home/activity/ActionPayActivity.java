package com.shownew.home.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.shownew.home.Config;
import com.shownew.home.R;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.module.UserAPI;
import com.shownew.home.module.entity.AddressEntity;
import com.shownew.home.pay.AlipayPayUtils;
import com.shownew.home.pay.PayResult;
import com.shownew.home.pay.WXPay;
import com.wp.baselib.utils.JsonUtils;
import com.wp.baselib.utils.Preference;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.widget.TitleBarView;
import com.wp.baselib.widget.WordWrapLayout;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;

/**
 * 活动付款界面
 */
public class ActionPayActivity extends BaseActivity implements View.OnClickListener {
    private TextView mConsignee_tv;
    private TextView mConsignee_phone;
    private TextView mConsignee_address;

    private RadioButton mRbZf;
    private RadioButton mRbWx;
    private int mIsFirst;
    private double mSelectMoney;
    private UserAPI mUserAPI;
    private int payWay = 0;
    private View mCommit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_pay);
        mUserAPI = new UserAPI(mShouNewApplication);
        initViews();
        if (mBundle != null) {
            mIsFirst = mBundle.getInt("isFirst");
            mSelectMoney = mBundle.getDouble("money");
        }
    }

    private void initViews() {
        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        titleBarView.setTitle("参与信息");
        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setOnLeftOnClickListener(this);
        titleBarView.setLeftIconAndText(R.drawable.back_arrow, "返回");
        titleBarView.setTitleSize(20);
        mConsignee_tv = (TextView) findViewById(R.id.consignee_tv);
        mConsignee_phone = (TextView) findViewById(R.id.consignee_phone);
        mConsignee_address = (TextView) findViewById(R.id.consignee_address);
        WordWrapLayout select_car_type = (WordWrapLayout) findViewById(R.id.select_car_type);
        addViewColor(this, "电动车,汽车", select_car_type);
        colorLisener(select_car_type);
        mCommit = findViewById(R.id.commit);
        mCommit.setOnClickListener(this);
        findViewById(R.id.rl1).setOnClickListener(this);
        RadioGroup mRgPay = (RadioGroup) findViewById(R.id.rg_pay);
        mRbZf = (RadioButton) findViewById(R.id.rg_zf);
        mRbWx = (RadioButton) findViewById(R.id.rg_wx);
        mRgPay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rg_zf:
                        mRbZf.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zhifubaopay, 0, R.drawable.determine_payment_function, 0);
                        payWay = 1;
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                finish();
                break;
            case R.id.commit:
                pay();
                break;
            case R.id.rl1:
                mShouNewApplication.redirect(AddressEditActivity.class);
                break;
        }
    }

    private String locationId;

    @Override
    protected void onResume() {
        super.onResume();
        getAddress();
        closeLoadingDialog();
        if (Preference.getBoolean(mShouNewApplication, Config.WX_STATE, false)) {
            Preference.putBoolean(mShouNewApplication, Config.WX_STATE, false);
            finish();
        } else {
            mCommit.setEnabled(true);
        }
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
                                    mConsignee_address.setText(String.valueOf(mAddressEntity.getLCity() + mAddressEntity.getLAddress()));
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

    private String mSelect;

    private void colorLisener(final WordWrapLayout typeParent) {
        final int childCount = typeParent.getChildCount();
        for (int chlidPostion = 0; chlidPostion < childCount; chlidPostion++) {
            final int postion = chlidPostion;
            typeParent.getChildAt(postion).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int chlid = 0; chlid < childCount; chlid++) {
                        TextView textView = (TextView) typeParent.getChildAt(chlid);
                        if (postion == chlid) {
                            mSelect = textView.getText().toString();
                            textView.setBackgroundResource(R.drawable.shape_shop_color);
                            textView.setTextColor(Color.WHITE);
                        } else {
                            textView.setBackgroundResource(0);
                            textView.setTextColor(Color.BLACK);
                        }
                    }
                }
            });
        }
        TextView textView = (TextView) typeParent.getChildAt(0);
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundResource(R.drawable.shape_shop_color);
        mSelect = textView.getText().toString();
    }


    private void addViewColor(Context context, String values, WordWrapLayout typeParent) {
        typeParent.removeAllViews();
        if (!TextUtils.isEmpty(values) && values.contains(",")) {
            String[] colors = values.split(",");
            int length = colors.length;
            for (String color : colors) {
                TextView textView = new TextView(context);
                textView.setPadding(15, 5, 15, 5);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                textView.setText(color);
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(layoutParams);
                typeParent.addView(textView);
            }
        } else if (!TextUtils.isEmpty(values)) {
            TextView textView = new TextView(context);
            textView.setPadding(15, 5, 15, 5);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setText(values);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(layoutParams);
            typeParent.addView(textView);
        }
    }

    /**
     * 支付包 相关ui处理
     */
    private  Handler mHandler = new Handler() {
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
                    mCommit.setEnabled(true);
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    mUserAPI.checkAlipayOrderz(payResult.toString(), mShouNewApplication.new ShouNewHttpCallBackLisener() {
                        @Override
                        protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                            if (exception == null) {
                                if (json.has("data")) {
                                    String msgTips;
                                    JSONObject jsonObject;
                                    try {
                                        jsonObject = json.getJSONObject("data");
                                        if (jsonObject.has("trade_state")) {
                                            String state = jsonObject.getString("trade_state");

                                            if (TextUtils.equals("9000", state)) {
                                                Toast.makeText(ActionPayActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                                                mShouNewApplication.redirect(ConsumeRecoderActivity.class);
                                                finish();
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
    /**
     * 订单号
     */
    private String mOrderzNo;

    /**
     * 支付
     */
    private void pay() {
        if (TextUtils.isEmpty(locationId)) {
            ToastUtil.showToast("请填写地址");
            return;
        }
        if (TextUtils.isEmpty(mSelect)) {
            ToastUtil.showToast("请选择爱车类型");
            return;
        }
        mCommit.setEnabled(false);
        mUserAPI.pay(String.valueOf(mSelectMoney), 0, payWay, mIsFirst, locationId, "", mSelect, mShouNewApplication.new ShouNewHttpCallBackLisener() {
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
                                new AlipayPayUtils(ActionPayActivity.this, mHandler, jsonObject.getString("alipayInfo"));
                            }

                            if (jsonObject.has("tenpayInfo")) {
                                createLoadingDialog();
                                Preference.putString(mShouNewApplication, Config.ORDER, mOrderzNo);
                                Preference.putInt(mShouNewApplication, Config.SHOP_TYPE, 2);
                                Preference.putInt(mShouNewApplication, Config.FLAG, payWay);
                                WXPay.getInstans(ActionPayActivity.this).WxPay(jsonObject.getJSONObject("tenpayInfo"));
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    mCommit.setEnabled(true);
                }
            }

            @Override
            protected void onLoading() {
                super.onLoading();

            }
        });


    }


}
