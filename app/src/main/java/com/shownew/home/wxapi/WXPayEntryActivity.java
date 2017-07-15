package com.shownew.home.wxapi;


import android.content.Intent;
import android.os.Bundle;

import com.shownew.home.Config;
import com.shownew.home.ShouNewApplication;
import com.shownew.home.activity.common.PayStateActivity;
import com.shownew.home.module.UserAPI;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.wp.baselib.AndroidActivity;
import com.wp.baselib.utils.Preference;
import com.wp.baselib.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;


public class WXPayEntryActivity extends AndroidActivity implements IWXAPIEventHandler {
    private IWXAPI api;
    private UserAPI mUserAPI;
    private ShouNewApplication mShouNewApplication;
    private int mFlag;
    private int mShopType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShouNewApplication = ShouNewApplication.getInstance();
        mUserAPI = new UserAPI(mShouNewApplication);
        api = WXAPIFactory.createWXAPI(this, Config.WXPAY);
        api.handleIntent(getIntent(), this);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        //        ToastUtil.showToast(resp.errCode + "====");
        //        0	成功	展示成功页面
        //        -1	错误	可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
        //        -2	用户取消	无需处理。发生场景：用户不支付了，点击取消，返回APP。
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {

            String orderzNo = Preference.getString(mShouNewApplication, Config.ORDER);
            mShopType = Preference.getInt(mShouNewApplication, Config.SHOP_TYPE);
            mFlag = Preference.getInt(mShouNewApplication, Config.FLAG, 1000);
            if (resp.errCode == 0) {
                //                1  超市            2  钱包 续费  活动
                checkTenpayOrderz(orderzNo);
            } else {
                if (2 == mShopType) {
                    ToastUtil.showToast("支付失败");
                    Preference.putBoolean(mShouNewApplication, Config.WX_STATE, false);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("orderzNo", orderzNo);
                    bundle.putInt("flag", mFlag);
                    bundle.putBoolean("isSucess", false);
                    mShouNewApplication.redirectAndPrameter(PayStateActivity.class, bundle);
                }
                finish();
            }
        }

    }


    /**
     * 查看微信支付订单状态接口
     */
    private void checkTenpayOrderz(final String orderNo) {
        mUserAPI.checkTenpayOrderz(orderNo, mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                Bundle bundle = new Bundle();
                if (exception == null) {
                    if (json.has("data")) {
                        try {
                            bundle.putString("orderzNo", orderNo);
                            bundle.putInt("flag", mFlag);
                            JSONObject jsonObject = json.getJSONObject("data");
                            String trade_state = jsonObject.getString("trade_state");

                            //    SUCCESS—支付成功
                            //    REFUND—转入退款
                            //    NOTPAY—未支付
                            //    CLOSED—已关闭
                            //    REVOKED—已撤销（刷卡支付）
                            //    USERPAYING--用户支付中
                            //    PAYERROR--支付失败(其他原因，如银行返回失败)
                            if ("SUCCESS".equals(trade_state)) {
                                if (2 == mShopType) {
                                    ToastUtil.showToast("支付成功");
                                    Preference.putBoolean(mShouNewApplication, Config.WX_STATE, true);
                                } else {
                                    bundle.putBoolean("isSucess", true);
                                    mShouNewApplication.redirectAndPrameter(PayStateActivity.class, bundle);
                                }
                                finish();
                                return;
                            } else if ("REFUND".equals(trade_state)) {
                                //                                ToastUtil.showToast("转入退款");
                            } else if ("NOTPAY".equals(trade_state)) {
                                //                                ToastUtil.showToast("未支付");

                            } else if ("CLOSED".equals(trade_state)) {
                                //                                ToastUtil.showToast("已关闭");

                            } else if ("REVOKED".equals(trade_state)) {
                                //                                    ToastUtil.showToast("已撤销");

                            } else if ("USERPAYING".equals(trade_state)) {
                                //                                ToastUtil.showToast("用户支付中");

                            } else if ("PAYERROR".equals(trade_state)) {
                                //                                ToastUtil.showToast("支付失败");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                isPaySucess(bundle);

            }
        });
    }

    private void isPaySucess(Bundle bundle) {
        if (2 == mShopType) {
            ToastUtil.showToast("支付失败");
            Preference.putBoolean(mShouNewApplication, Config.WX_STATE, false);
        } else {
            bundle.putBoolean("isSucess", false);
            mShouNewApplication.redirectAndPrameter(PayStateActivity.class, bundle);
        }
        finish();
    }
}