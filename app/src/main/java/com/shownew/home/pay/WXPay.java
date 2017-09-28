package com.shownew.home.pay;

import android.content.Context;

import com.shownew.home.Config;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 微信支付
 *
 * @author Jason
 * @version 1.0
 * @date 2017/5/18 0018
 */

public class WXPay {
    private static IWXAPI msgApi;
    private static WXPay wxPay;

    private WXPay(Context context) {
        msgApi = WXAPIFactory.createWXAPI(context, null);
    }

    public static WXPay getInstans(Context context) {
        if (wxPay == null) {
            synchronized (WXPay.class) {
                if (wxPay == null) {
                    wxPay = new WXPay(context);
                }
            }
        }
        return wxPay;
    }

    public void WxPay(JSONObject json) {

        try {
            msgApi.registerApp(Config.WXPAY);
            PayReq req = new PayReq();
            req.appId = json.getString("appid");
            req.partnerId = json.getString("partnerid");
            req.prepayId = json.getString("prepayid");
            req.nonceStr = json.getString("noncestr");
            req.timeStamp = json.getString("timestamp");
            req.packageValue = json.getString("package");
            req.sign = json.getString("sign");
            req.extData = "app data"; // optional

//            ToastUtil.showToast("正常调起支付");
            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
            msgApi.sendReq(req);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
