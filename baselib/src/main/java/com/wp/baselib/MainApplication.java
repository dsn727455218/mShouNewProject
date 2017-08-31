package com.wp.baselib;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.BaseRequest;
import com.wp.baselib.utils.DesUtil;
import com.wp.baselib.utils.FileUtil;
import com.wp.baselib.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.logging.Level;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @author summer
 * @description 全局应用程序管理
 * @date 2014年4月2日 上午10:59:53
 */
public class MainApplication extends Application {

    public int terminalWidth; // 终端宽度
    public int terminalHeight;// 终端高度
    public String terminalOS = "Android"; // 系统
    public String terminalVersion; // 版本号
    public String terminalModel; // 机型
    public int terminalSDK; // sdk版本
    public String terminalIMEI;// 设备唯一IMEI标示
    public String terminalTEL;// 本手机号码
    public String terminalICCID;
    public String terminalIMSI;
    public String terminalMAC;//wifi mac地址
    private final static String TAG = MainApplication.class.getSimpleName();
    private static MainApplication instance;
    private static final int REQUEST_READ_PHONE_STATE = 1;

    @Override
    public void onCreate() {
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        this.terminalWidth = display.getWidth();
        this.terminalHeight = display.getHeight();
        this.terminalModel = android.os.Build.MODEL;
        this.terminalSDK = android.os.Build.VERSION.SDK_INT;
        this.terminalVersion = android.os.Build.VERSION.RELEASE;
        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            this.terminalIMEI = tm.getDeviceId(); // 取出IMEI,手机唯一标识码
            this.terminalTEL = tm.getLine1Number(); // 取出MSISDN，很可能为空(手机号码)
            this.terminalICCID = tm.getSimSerialNumber(); // 取出ICCID,集成电路卡标识，这个是唯一标识一张卡片物理号码的
            this.terminalIMSI = tm.getSubscriberId(); // 取出IMSI,国际移动用户号码标识
        } catch (Exception e) {
            e.printStackTrace();
        }
        android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) getSystemService(Context.WIFI_SERVICE);
        this.terminalMAC = wifi.getConnectionInfo().getMacAddress();
        instance = this;
        getTerminalInfo();
        if (!TextUtils.isEmpty(terminalIMEI))
            LogUtil.i("MainApplication", terminalIMEI);
        super.onCreate();
        initOkGo();
    }


    /**
     * 获取唯一标示
     *
     * @return
     */
    public void getTerminalInfo() {

        if (!TextUtils.isEmpty(terminalIMEI))
            return;

        if (!TextUtils.isEmpty(terminalTEL)) {
            terminalIMEI = terminalTEL;
            return;
        }

        if (!TextUtils.isEmpty(terminalICCID)) {
            terminalIMEI = terminalICCID;
            return;
        }

        if (!TextUtils.isEmpty(terminalIMSI)) {
            terminalIMEI = terminalIMSI;
            return;
        }

        if (!TextUtils.isEmpty(terminalMAC)) {
            terminalIMEI = terminalMAC;
            return;
        }

        String android_id = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        if (!TextUtils.isEmpty(android_id)) {
            terminalIMEI = android_id;
        } else {
            //生成uuid存储
            File file = new File(FileUtil.getCachePath(this, "/"), "wavefar_uniqueness");
            if (!file.exists()) {
                try {
                    terminalIMEI = DesUtil.MD5(UUID.randomUUID().toString());
                    FileUtil.writeFile(file.toString(), terminalIMEI.getBytes());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            } else {
                byte[] bytes = FileUtil.readFile(file.toString());
                if (bytes != null)
                    terminalIMEI = new String(bytes);
            }
        }
    }


    //获取单个实例
    public synchronized static MainApplication getInstance() {
        return instance;
    }

    /**
     * 重定向页面无参数
     *
     * @param activity
     */
    public void redirect(Class<?> activity) {
        if (activity == null) {
            LogUtil.e(TAG, "Activity cannot be null!");
            return;
        }
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(this, activity);
        startActivity(intent);
    }

    /**
     * 重定向页面并带参数
     *
     * @param activity
     * @param extras
     */
    public void redirectAndPrameter(Class<?> activity, Bundle extras) {
        if (activity == null || extras == null) {
            LogUtil.e(TAG, "Activity or Bundle cannot be null!");
            return;
        }
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(this, activity);
        intent.putExtras(extras);
        startActivity(intent);
    }

    /**
     * 重定向页面并带参数和回调
     *
     * @param context     当前Activity
     * @param activity    要访问的Activity
     * @param extras      可选 不需要传null
     * @param requestCode
     */
    public void redirectAndPrameterResult(Activity context, Class<?> activity, Bundle extras, int requestCode) {
        if (activity == null || context == null) {
            LogUtil.e(TAG, "Activity cannot or current Activity  be null!");
            return;
        }
        Intent intent = new Intent();
        intent.setClass(context, activity);
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * 是否是第一次访问app
     *
     * @return
     */
    public boolean firstStartAPP() {
        try {
            if (!FileUtil.checkFileIsExists(this, "lock")) {
                FileUtil.writeLocalFile(this, "lock", "isFrist".getBytes());
                return true;
            }
            return false;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 应用程序退出的时候调用
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    /**
     * 配置okgo
     */
    private void initOkGo() {
        OkGo.init(instance);
        try {
            OkGo.getInstance()
                    // 打开该调试开关,打印级别INFO,并不是异常,是为了显眼,不需要就不要加入该行
                    // 最后的true表示是否打印okgo的内部异常，一般打开方便调试错误
                    .debug("ShouNew", Level.INFO, true)
                    //如果使用默认的 60秒,以下三行也不需要传 .setConnectTimeout(OkGo.DEFAULT_MILLISECONDS)
                    .setConnectTimeout(5 * 1000)  //全局的连接超时时间
                    .setReadTimeOut(5 * 1000)     //全局的读取超时时间
                    .setWriteTimeOut(5 * 1000)    //全局的写入超时时间

                    //可以全局统一设置缓存模式,默认是不使用缓存,可以不传,具体其他模式看 github 介绍 https://github.com/jeasonlzy/
//                    .setCacheMode(CacheMode.NO_CACHE)
                    //可以全局统一设置缓存时间,默认永不过期,具体使用方法看 github 介绍
//                    .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)

                    //可以全局统一设置超时重连次数,默认为三次,那么最差的情况会请求4次(一次原始请求,三次重连请求),不需要可以设置为0
                    .setRetryCount(0)

                    //如果不想让框架管理cookie（或者叫session的保持）,以下不需要
                    //                .setCookieStore(new MemoryCookieStore())            //cookie使用内存缓存（app退出后，cookie消失）
//                    .setCookieStore(new PersistentCookieStore())        //cookie持久化存储，如果cookie不过期，则一直有效

                    //可以设置https的证书,以下几种方案根据需要自己设置
                    .setCertificates();                         //方法一：信任所有证书,不安全有风险
            //                    .setCertificates(new SafeTrustManager())            //方法二：自定义信任规则，校验服务端证书
            //                    .setCertificates(getAssets().open("srca.cer"))      //方法三：使用预埋证书，校验服务端证书（自签名证书）
            //                    //方法四：使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
            //                    .setCertificates(getAssets().open("xxx.bks"), "123456", getAssets().open("yyy.cer"))//

            //配置https的域名匹配规则，详细看demo的初始化介绍，不需要就不要加入，使用不当会导致https握手失败
            //                    .setHostnameVerifier(new SafeHostnameVerifier())

            //可以添加全局拦截器，不需要就不要加入，错误写法直接导致任何回调不执行
            //                .addInterceptor(new Interceptor() {
            //                    @Override
            //                    public Response intercept(Chain chain) throws IOException {
            //                        return chain.proceed(chain.request());
            //                    }
            //                })

            //这两行同上，不需要就不要加入
            //                    .addCommonHeaders(headers)  //设置全局公共头
            //                                .addCommonParams(BaseApi.);   //设置全局公共参数

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public abstract class HttpCallBack<T> extends AbsCallback<T> {

        @Override
        public void onBefore(BaseRequest request) {
            // 主要用于在所有请求之前添加公共的请求头或请求参数
            // 例如登录授权的 token
            // 使用的设备信息
            // 可以随意添加,也可以什么都不传
            // 还可以在这里对所有的参数进行加密，均在这里实现
            //                request.headers("header1", "HeaderValue1")//
            //                .params("params1", "ParamsValue1")//
            //                .params("token", "3215sdf13ad1f65asd4f3ads1f");
            super.onBefore(request);
        }


        @Override
        public T convertSuccess(Response response) throws Exception {
            return null;
        }

        @Override
        public void resultDataUI(T data, Call call, Response response) {
            super.resultDataUI(data, call, response);
        }
    }
}
