package com.shownew.home.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.lzy.okgo.OkGo;
import com.shownew.home.Config;
import com.shownew.home.R;
import com.shownew.home.activity.common.BaseActivity;
import com.wp.baselib.utils.NetWorkUtil;
import com.wp.baselib.utils.Preference;
import com.wp.baselib.utils.UiUtil;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by home on 2017/3/31 0031.
 */

public class WelcomeActivity extends BaseActivity {
    private TimeTask mTimeTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        setBarColor(R.color.transparent);
        mTimeTask = new TimeTask( 500, 500);
    }

    /**
     * 检查权限
     *
     * @param permiss
     */
    private void checkPermission(String... permiss) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int length = permiss.length;
            ArrayList<String> requestPermissionList = new ArrayList<String>();
            for (int i = 0; i < length; i++) {
                int permission = ContextCompat.checkSelfPermission(WelcomeActivity.this, permiss[i]);
                if (permission != PackageManager.PERMISSION_GRANTED) {//如果没有权限
                    requestPermissionList.add(permiss[i]);
                }
                if (requestPermissionList.isEmpty()) {
                    //有权限
                    permissionAllGranted();
                } else {
                    ActivityCompat.requestPermissions(WelcomeActivity.this, requestPermissionList.toArray(new String[requestPermissionList.size()]), 1);
                }
            }
        } else {
            permissionAllGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 1) {
            return;
        }

        if (grantResults.length > 0) {
            List<String> deniedPermissionList = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissionList.add(permissions[i]);
                }
            }

            if (deniedPermissionList.isEmpty()) {
                //已经全部授权
                permissionAllGranted();
            } else {

                //勾选了对话框中”Don’t ask again”的选项, 返回false
                for (String deniedPermission : deniedPermissionList) {

                    boolean flag = false;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        flag = shouldShowRequestPermissionRationale(deniedPermission);
                    }
                    if (!flag) {
                        //拒绝授权
                        permissionShouldShowRationale(deniedPermissionList);
                        return;
                    }
                }
                //拒绝授权
                permissionHasDenied(deniedPermissionList);

            }


        }

    }


    /**
     * 权限全部已经授权
     */
    private void permissionAllGranted() {
        if (mTimeTask != null) {
            mTimeTask.start();
        }
    }

    /**
     * 有权限被拒绝
     *
     * @param deniedList 被拒绝的权限
     */
    private void permissionHasDenied(List<String> deniedList) {
        ActivityCompat.requestPermissions(WelcomeActivity.this, deniedList.toArray(new String[deniedList.size()]), 1);
    }

    /**
     * 权限被拒绝并且勾选了不在询问
     *
     * @param deniedList 勾选了不在询问的权限
     */
    private void permissionShouldShowRationale(List<String> deniedList) {
        ActivityCompat.requestPermissions(WelcomeActivity.this, deniedList.toArray(new String[deniedList.size()]), 1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        JPushInterface.onResume(this);
        if (JPushInterface.isPushStopped(this)) {
            JPushInterface.resumePush(this);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimeTask != null) {
            mTimeTask.cancel();
        }
    }

    private class TimeTask extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public TimeTask(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //            mSendMsgCode.setText(String.format("%3d秒", millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            if (NetWorkUtil.checkNetworkConnected(WelcomeActivity.this, true, new UiUtil.AlertDialogLisener() {
                @Override
                public void alertDialogClick() {
                    finish();
                }
            })) {
                if (!Preference.getBoolean(mShouNewApplication, Config.ISFIRST_ENTER, false)) {
                    mainApplication.redirect(GuideActivity.class);
                    Preference.putBoolean(mShouNewApplication, Config.ISFIRST_ENTER, true);
                } else {
                    mainApplication.redirect(MainActivity.class);
                }
                finish();
            } else {
                OkGo.getInstance().cancelTag(this);
                OkGo.getInstance().cancelAll();
            }

        }
    }
}
