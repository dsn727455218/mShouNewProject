package com.shownew.home.pay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;

/**
 * 支付宝
 *
 * @author Jason
 * @version 1.0
 * @date 2017/5/14 0014
 */

public class AlipayPayUtils {



    private Activity context;
    //支付宝相关数据
    //	PID:2088121943860695
    //	APPID: 2016031001199989
    //	APP SECRET:566aa58be18042919abe8c56b88f3190
    //	企业名称：首牛网络科技成都有限公司

    // 商户PID
    private static final String PARTNER = "2088121943860695";
    // 商户收款账号
    private static final String SELLER = "shouniunt@sina.com";
    // 商户私钥，pkcs8格式
    private static final String RSA_PRIVATE = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBANyjfjJriltywsQ9\n" + "9ElPchf7eE7Sb8szIAA9eJg1AEns7aDPywheh3zx/AuJXFjXsti0xWHZxPP3Posj\n" + "I2+CoalJpWvV358BgOqyi8750/4PU/kaHRjedObwGuzTUeCYxjR7jlXRTLZmlq0v\n" + "fXWbP14CNkv2nh8smAR5ByN3jnjrAgMBAAECgYBLVqA+wy3JMBSFQcpqRfBlc/6O\n" + "m5iEv/5LVrIY9vAUTgyN/qYLQ4vsBAzWO58GzxBIouFLGjsAOwpmJCdzah2ct+SE\n" + "lzveJtGnUdEty8DG7XfUvoqdDBg2KrkU/uQujf1HwdLc54YI2D9BJxu0nd4pD6oN\n" + "MFxkh2ETAhEGd7cXUQJBAPzpfFAwBXttlZZKQZPKr9+fvTAVkv7O9dtZLjE2kcS7\n" + "HrSTQNTCozM7Q2ifCJ6hKO2Ozu752lEGEBfJYNLcwNcCQQDfVSHKo80ElEHroZUf\n" + "CMtVxFya2bMk76gnLvmj2tzFfma2dGGg9uLgKmlzzlgxsJeInkfxxgdzkF8Zpor4\n" + "AQINAkBq4n5AIVtfw3tRjcZTIHjdiFPbK3L5LxwmfHUSJV7Lhs0+QXFHuY5hS218\n" + "nKF28OMsKot34a4LSgWpF9lMpDD3AkARCQZd1abCzahFl/qu9lXEQgkqSqbOj3VN\n" + "H9ks9XnXmq+tBS2ZQJvHq5SFn9y8VZMJecFVINzapM2MQFbjWfhVAkAN+j8pPnC9\n" + "eFpI3MYZIzLdmIFCWT1c7GKCto2pjfPBqT++sKjvRxFANlLXLWQkj4HiiN0Lwynt\n" + "RHh1hSzrdk1T";
    // 支付宝公钥
    public static final int SDK_PAY_FLAG = 1324959501;// 商户PID
    private Handler mHandler;
    /**
     * /**
     * 完整的符合支付宝参数规范的订单信息（服务端获取的）
     */
    private String orderInfo = "";

    public AlipayPayUtils(Activity context, Handler handler, String orderInfo) {
        this.context = context;
        mHandler = handler;
        this.orderInfo = orderInfo;
        if (context != null && mHandler != null && !TextUtils.isEmpty(orderInfo)) {
            pay();
        }
    }

    private void pay() {
        if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE) || TextUtils.isEmpty(SELLER)) {
            new AlertDialog.Builder(context).setTitle("警告").setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialoginterface, int i) {
                    //
                    context.finish();
                }
            }).show();
            return;
        }


        /**
         * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
         */


        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(context);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(orderInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }
}
