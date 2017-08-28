package com.shownew.home.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.widget.TextView;

import com.shownew.home.R;
import com.shownew.home.ShouNewApplication;
import com.shownew.home.module.PublicApi;
import com.shownew.home.receiver.AppUpdateService;
import com.shownew.home.utils.dialog.CommonDialog;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.utils.UiUtil;
import com.wp.baselib.utils.Utils;

import org.json.JSONObject;

import okhttp3.Response;


/**
 * app更新工具类
 *
 * @author summer
 */
public class AppUpdateUtil {

    private Context mContext;
    private double remoteVersionCode = 0;//远程版本号
    private String remoteAppUrl = "";
    private String remoteMsg = ""; //从服务器端获取更新信息说明
    private CommonDialog mCommonDialog;
    public static boolean isDownloading;

    /**
     * Returns version code
     *
     * @return
     */
    public static int getAppVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Returns version name
     *
     * @return
     */
    public static String getAppVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private ShouNewApplication shouNewApplication;

    /**
     * @param context
     */
    public AppUpdateUtil(Context context, ShouNewApplication shouNewApplication) {
        this.mContext = context;
        this.shouNewApplication = shouNewApplication;
    }

    private void showupdateDialog() {
        if (isDownloading) {
            ToastUtil.showToast("应用下载中");
            return;
        }
        if (mCommonDialog != null && !mCommonDialog.isShowing()) {
            mCommonDialog.setCancelable(false).show();
        }
        mCommonDialog = new CommonDialog(mContext, "版本已升级", "为了您的正常使用，请及时更新!", "立即更新", "忽略").setCommonListener(new CommonDialog.CommonListener() {
            @Override
            public void sure(int flag) {
                if (flag == 1) {
                    //去应用宝更新app
                    UiUtil.startWebIntent(mContext,"http://app.qq.com/#id=detail&appid=1106101117");
//                    int state = NetWorkUtil.checkNetworkConnectedType(mContext);
//                    if (state == 0) {
//                        new CommonDialog(mContext, "温馨提示！", "确定使用流量更新app吗?", "确定", "取消").setCommonListener(new CommonDialog.CommonListener() {
//                            @Override
//                            public void sure(int flag) {
//                                if (flag == 1) {
//                                    startDownFile();
//                                }
//                            }
//                        }).setCancelable(true).show();
//                    } else if (2 == state) {
//                        NetWorkUtil.checkNetworkConnected(mContext, true);
//                    } else {
//
//                        startDownFile();
//                    }
                }
            }
        });
        mCommonDialog.setCancelable(false).show();

    }


    private void startDownFile() {
        if (!Utils.isNotificationEnabled(mContext)) {
            new CommonDialog(mContext, "温馨提示", "为了正常下载最新应用，请打开通知!", "打开", "取消").setCommonListener(new CommonDialog.CommonListener() {
                @Override
                public void sure(int flag) {
                    if (flag == 1) {
                        Utils.toSetting(mContext);
                    }
                }
            }).setCancelable(false).show();
            return;
        }
        if (!TextUtils.isEmpty(remoteAppUrl)) {
            AppUpdateUtil.isDownloading = true;
            Intent intent = new Intent(mContext, AppUpdateService.class);
            intent.putExtra("url", remoteAppUrl);
            mContext.startService(intent);
        }
    }

    public AppUpdateUtil UpdateExecute(final boolean isinitCheck) {
        return UpdateExecute(isinitCheck, null);
    }

    /**
     * 是否是开机检查app
     *
     * @param isinitCheck
     */
    public AppUpdateUtil UpdateExecute(final boolean isinitCheck, final TextView tips) {
        new PublicApi(ShouNewApplication.getInstance()).getCheckAppUrl(shouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (exception == null) {
                    try {
                        if (json != null) {
                            if ("200".equals(json.getString("statusCode"))) {
                                if (json.has("data")) {
                                    JSONObject versionJson = json.getJSONObject("data");
                                    if (versionJson.has("version")) {
                                        JSONObject version = versionJson.getJSONObject("version");
                                        remoteVersionCode = version.getDouble("vAnversion"); //apk版本
                                        remoteAppUrl = version.getString("vAnurl");//apk 地址
                                        remoteMsg = version.getString("vAnnote");//更新描述
                                    }
                                }
                                if (isinitCheck) {
                                    checkUpdateState(tips);
                                } else {
                                    checkUpdate();
                                }
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return this;
    }


    private void checkUpdate() {
        if (remoteVersionCode > getAppVersionCode(mContext)) {
            showupdateDialog();
        } else {
            ToastUtil.showToast(R.string.download_eixts_msg);
        }
    }

    public void checkUpdateState(TextView tips) {
        if (remoteVersionCode > getAppVersionCode(mContext)) {
            tips.setCompoundDrawablesWithIntrinsicBounds(R.drawable.shape_msg_circle, 0, R.drawable.right_arrow, 0);
        } else {
            tips.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.right_arrow, 0);
        }
    }
}
