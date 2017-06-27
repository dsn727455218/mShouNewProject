package com.shownew.home.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;

import com.shownew.home.R;
import com.shownew.home.ShouNewApplication;
import com.shownew.home.module.PublicApi;
import com.shownew.home.receiver.AppUpdateService;
import com.shownew.home.utils.dialog.CommonDialog;
import com.wp.baselib.utils.ToastUtil;

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
    public static double getAppVersionName(Context context) {
        try {
            return Double.parseDouble(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
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
        final Intent intent = new Intent(mContext, AppUpdateService.class);
        new CommonDialog(mContext, "系统更新提示", remoteMsg, "更新", "取消").setCommonListener(new CommonDialog.CommonListener() {
            @Override
            public void sure(int flag) {
                if (flag == 1) {
                    intent.putExtra("url", remoteAppUrl);
                    mContext.startService(intent);
                }
            }
        }).setCancelable(false).show();
    }

    /**
     * 是否是开机检查app
     *
     * @param isinitCheck
     */
    public void UpdateExecute(final boolean isinitCheck) {
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
                                    initCheckUpdate();
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
    }

    private void initCheckUpdate() {
        if (remoteVersionCode > getAppVersionName(mContext)) {
            showupdateDialog();
        }
    }

    private void checkUpdate() {
        if (remoteVersionCode > getAppVersionName(mContext)) {
            showupdateDialog();
        } else {
            ToastUtil.showToast(R.string.download_eixts_msg);
        }
    }

}
