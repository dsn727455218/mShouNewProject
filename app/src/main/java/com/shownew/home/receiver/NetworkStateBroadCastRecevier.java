package com.shownew.home.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.wp.baselib.utils.NetWorkUtil;

/**
 * 网络和时间状态的监听
 * Created by Administrator on 2016/11/23 0023.
 */
public class NetworkStateBroadCastRecevier extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != intent) {
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {    //如果网络状态被改变
                if (NetWorkUtil.checkNetworkConnected(context)) {
//                    ShouNewApplication.getInstance().getSystemTime();
                }
            } else if (Intent.ACTION_TIME_CHANGED.equals(action)) {   //监听系统时间被修改
//                ShouNewApplication.getInstance().getSystemTime();
            }
        }
    }
}
