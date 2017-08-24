package com.shownew.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lzy.okgo.request.BaseRequest;
import com.shownew.home.activity.LoginActivity;
import com.shownew.home.activity.NewCarRegisterActivity;
import com.shownew.home.module.DeviceAPI;
import com.shownew.home.module.UserAPI;
import com.shownew.home.module.entity.UserEntity;
import com.shownew.home.utils.dialog.CommonDialog;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.common.QueuedWork;
import com.wp.baselib.MainApplication;
import com.wp.baselib.common.WebActivity;
import com.wp.baselib.utils.DesUtil;
import com.wp.baselib.utils.Preference;
import com.wp.baselib.utils.TimeUtil;
import com.wp.baselib.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by home on 2017/3/31 0031.
 */

public class ShouNewApplication extends MainApplication implements Thread.UncaughtExceptionHandler {
    private static ShouNewApplication instance;
    private UserAPI mUserAPI;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mUserAPI = new UserAPI(instance);
        //        Thread.setDefaultUncaughtExceptionHandler(this);
        initJPush();
//        setDatabase();
        getSystemTime();
        updataDeviceData();
        initShare();
        getReource();

    }

    public void loadImg(String imgUrl, final ImageView imageView) {
        Glide.with(instance).load(imgUrl).asBitmap().placeholder(R.drawable.square_seize).error(R.drawable.square_seize).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                imageView.setImageBitmap(resource);
            }
        });
    }

    /**
     * 获取资源文件
     */
    private void getReource() {
        mUserAPI.getReource(new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (exception == null) {
                    if (json.has("data")) {
                        try {
                            JSONObject jsonObject = json.getJSONObject("data");
                            if (jsonObject.has("sourceList")) {
                                mUserAPI.saveRourcesData(jsonObject.getString("sourceList"));
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
     * 获取系统时间
     */
    public void getSystemTime() {
        mUserAPI.getSystemTime(new ShouNewHttpCallBackLisener() {
            @Override
            protected Object parseData(String result) {
                return null;
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (null == exception) {
                    if (json.has("data")) {
                        try {
                            JSONObject jsonObject = json.getJSONObject("data");
                            if (jsonObject.has("time")) {
                                long time = jsonObject.getLong("time");
                                //保存时间差
                                Preference.putLong(instance, "timestamp", System.currentTimeMillis() - time);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }

            }
        });
    }

    //获取单个实例
    public synchronized static ShouNewApplication getInstance() {
        if (instance == null) {
            throw new NullPointerException("DingDianApplication instance is null, please register in AndroidManifest.xml first");
        }
        return instance;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println("程序出错了==========");
        System.err.print("Exception in thread \"" + t.getName() + "\" " + e.toString());
        Thread.UncaughtExceptionHandler ueh = Thread.getDefaultUncaughtExceptionHandler();
        if (ueh != null) {
            ueh.uncaughtException(t, e);
        } else if (!(e instanceof ThreadDeath)) {
            System.err.print("Exception in thread \"" + t.getName() + "\" " + e.toString());
            e.printStackTrace(System.err);
        }

    }

    /**
     * 启动webview 展示页面
     *
     * @param title
     * @param url
     */
    public void redirectWeb(String title, String url) {
        redirectWeb(title, url, false);
    }

    /**
     * 启动webView展示页面
     *
     * @param title
     * @param url
     * @param isShowZoomControls 是否显示放大缩小
     */
    public void redirectWeb(String title, String url, boolean isShowZoomControls) {
        redirectWeb(title, url, isShowZoomControls, false, false, null);
    }

    /**
     * 启动webView展示页面
     *
     * @param title
     * @param url
     * @param isShowZoomControls   是否显示放大缩小
     * @param isGoOut              是否跳到浏览器
     * @param isShowBottomControls 是否显示底部 向前向后刷新控件
     * @param bundle               扩展参数,可以为null
     */
    public void redirectWeb(String title, String url, boolean isShowZoomControls, boolean isGoOut, boolean isShowBottomControls, Bundle bundle) {
        if (bundle == null)
            bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("url", url);
        bundle.putBoolean("isShowZoomControls", isShowZoomControls);
        bundle.putBoolean("isGoOut", isGoOut);
        bundle.putBoolean("isShowBottomControls", isShowBottomControls);
        redirectAndPrameter(WebActivity.class, bundle);
    }

    /**
     * 启动webView展示页面
     */
    public void redirectActionWeb(String title, String url, Class<?> activity, Bundle bundle) {
        if (bundle == null)
            bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("url", url);
        bundle.putBoolean("isShowZoomControls", false);
        bundle.putBoolean("isGoOut", false);
        bundle.putBoolean("isShowBottomControls", false);
        redirectAndPrameter(activity, bundle);
    }

    /**
     * 初始化极光
     */
    private void initJPush() {
        JPushInterface.setDebugMode(BuildConfig.DEBUG);// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);// 初始化 JPush
    }

    /**
     * 用于获取状态栏的高度。 使用Resource对象获取（推荐这种方式）
     *
     * @return 返回状态栏高度的像素值。
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 进入登陆界面
     */
    public void jumpLoginActivity(Context context) {
        redirect(LoginActivity.class);
//        new CommonDialog(context, "温馨提示", "你还未登录，请登录进行操作", "登录", "取消").setCommonListener(new CommonDialog.CommonListener() {
//            @Override
//            public void sure(int flag) {
//                if (1 == flag) {
//                    redirect(LoginActivity.class);
//                } else if (0 == flag) {
//                    //                    Bundle bundle = new Bundle();
//                    //                    bundle.putInt("loginOrforgetpwd_flag", 1);//注册
//                    //                    redirectAndPrameter(RegisterActivity.class, bundle);
//                }
//            }
//        }).setCancelable(true).show();
    }

    public void handleCarBind(Context context) {
        new CommonDialog(context, "你未注册车辆,请进行注册").setCommonListener(new CommonDialog.CommonListener() {
            @Override
            public void sure(int flag) {
                if (1 == flag) {
                    redirect(NewCarRegisterActivity.class);
                }
            }
        }).setCancelable(true).show();
    }

    /**
     * 让服务器获取设备的最新数据
     */
    private void updataDeviceData() {
        new DeviceAPI(instance).controlLock("4", new ShouNewHttpCallBackLisener() {
            @Override
            protected Object parseData(String result) {
                return null;
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {

            }
        });
    }

    /**
     * 所有返回数数据封装
     *
     * @desscription
     **/
    public abstract class ShouNewHttpCallBackLisener<T> extends HttpCallBack<T> {
        @Override
        public void onBefore(BaseRequest request) {

            UserEntity userEntity = mUserAPI.getUserInfo();
            if (null != userEntity) {
                //添加系统级参数

                try {
                    String json = DesUtil.MD5(userEntity.getJSessionId() + TimeUtil.getTime2String(System.currentTimeMillis() + Preference.getLong(instance, "timestamp"), "yyyyMMddHHmm")) + userEntity.getJSessionId();
                    //                    request.params("jSessionId", userEntity.getJSessionId());
                    request.params("jSessionId", json);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }


            }
            super.onBefore(request);
            onLoading();
        }

        @Override
        public void resultDataUI(T resultData, Call call, Response response) {
            try {
                String result = response.body().string();
                if (TextUtils.isEmpty(result)) {
                    resultData(resultData, null, response, new Exception("返回的数据为null"));
                    response.close();
                    return;
                }
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has(Config.STATUS_CODE)) {
                    String message = "";
                    if (jsonObject.has(Config.MESSAGE)) {
                        message = jsonObject.getString(Config.MESSAGE);
                    }
                    int statusCode = jsonObject.getInt(Config.STATUS_CODE);
                    if (200 == statusCode) {
                        if (jsonObject.has("data")) {
                            resultData = parseData(jsonObject.getString("data"));
                        }
                        resultData(resultData, jsonObject, response, null);
                        response.close();
                        return;
                    } else if (300 == statusCode) {
                        Log.e("Msg", "接口调用失败300");
                    } else if (301 == statusCode) {
                        Log.e("Msg", "参数不合法301");
                    } else if (302 == statusCode) {
                        Log.e("Msg", "用户未登录302");
                        Preference.putBoolean(instance, Preference.IS_LOGIN, false);
                        handleLogin();
                        message = "302";
                    } else if (303 == statusCode) {
                        Log.e("Msg", "服务器连接超时303");
                    } else if (305 == statusCode) {//请求被禁止，未绑定默认车辆
                        handleCarBind();
                        message = "305";
                    } else if (500 == statusCode) {
                        Log.e("Msg", "服务器连接超时500");
                        ToastUtil.showToast("服务器错误");
                    }
                    resultData(resultData, jsonObject, response, new Exception(message));
                    Log.e("Msg", message);
                    response.close();
                    return;
                }
                resultData(resultData, jsonObject, response, new Exception("没有含有statusCode"));
                response.close();
            } catch (Exception e) {
                e.printStackTrace();
                resultData(resultData, null, response, e);
                response.close();
            }
        }

        /**
         * 处理车辆未绑定
         */
        protected void handleCarBind() {

        }

        /**
         * 处理用户未登陆
         */
        protected void handleLogin() {
        }

        @Override
        public void onError(Call call, Response response, Exception e) {
            super.onError(call, response, e);
//            if (e instanceof ConnectException || e instanceof SocketTimeoutException || e instanceof UnknownHostException||e instanceof SocketException) {
//            }
            ToastUtil.showToast("网络连接失败");
            resultData(null, null, response, e);
        }

        /**
         * 数据解析
         *
         * @param result
         * @return
         */
        protected T parseData(String result) {
            return null;
        }

        /**
         * (注：使用时注意判断是否为null)
         * 服务器请求之后 返回的信息
         *
         * @param data      解析后的 实体
         * @param json      未解析的Json
         * @param response  响应
         * @param exception 有无异常   有  （请求失败，响应错误，数据解析错误等，都会回调该方法）， UI线程  exception==null  无异常可以正常处理数据
         */
        protected abstract void resultData(T data, JSONObject json, Response response, Exception exception);

        /**
         * 加载中
         */
        protected void onLoading() {
        }


    }

    //友盟分享
    private void initShare() {
        //开启debug模式，方便定位错误，具体错误检查方式可以查看http://dev.umeng.com/social/android/quick-integration的报错必看，正式发布，请关闭该模式
        com.umeng.socialize.Config.DEBUG = true;
        QueuedWork.isUseThreadPool = false;
        UMShareAPI.get(instance);
    }

    //各个平台的配置，建议放在全局Application或者程序入口
    static {
        PlatformConfig.setSinaWeibo("3946992314", "55ec1bd872fdf573992810c748b0d4ce", "http://sns.whalecloud.com");
        PlatformConfig.setWeixin("wx18e68dc827ac8cc7", "4315dffa19f1edf682b5d59eaa85530b");
        PlatformConfig.setQQZone("1106101117", "3DpGx0kvmph7ZfCr");


        PlatformConfig.setYixin("yxc0614e80c9304c11b0391514d09f13bf");
        PlatformConfig.setTwitter("3aIN7fuF685MuZ7jtXkQxalyi", "MK6FEYG63eWcpDFgRYw4w9puJhzDl0tyuqWjZ3M7XJuuG7mMbO");
        PlatformConfig.setAlipay("2015111700822536");
        PlatformConfig.setLaiwang("laiwangd497e70d4", "d497e70d4c3e4efeab1381476bac4c5e");
        PlatformConfig.setPinterest("1439206");
        PlatformConfig.setKakao("e4f60e065048eb031e235c806b31c70f");
        PlatformConfig.setDing("dingoalmlnohc0wggfedpk");
        PlatformConfig.setVKontakte("5764965", "5My6SNliAaLxEm3Lyd9J");
        PlatformConfig.setDropbox("oz8v5apet3arcdy", "h7p2pjbzkkxt02a");
    }

//    private DaoMaster.DevOpenHelper mHelper;
//    private SQLiteDatabase db;
//    private DaoMaster mDaoMaster;
//    private DaoSession mDaoSession;
//    /**
//     * 设置greenDao
//     */
//    private void setDatabase() {
//        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
//        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
//        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
//        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
//        mHelper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
//        db = mHelper.getWritableDatabase();
//        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
//        mDaoMaster = new DaoMaster(db);
//        mDaoSession = mDaoMaster.newSession();
//    }
//    public DaoSession getDaoSession() {
//        return mDaoSession;
//    }
//    public SQLiteDatabase getDb() {
//        return db;
//    }

}
