package com.wp.baselib.utils;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

/**
 * 友盟相关操作工具类
 *
 * @author summer
 * @description
 * @date 2014-7-26 下午9:50:29
 */
public class UMengUtil {

    //----------------统计相关---------------------

    /**
     * 注册
     *
     * @param mContext 是Activity的引用
     *                 已经测试
     */

    public static void uMengOnResume(Context mContext) {

        MobclickAgent.onResume(mContext);
    }

    public static void uMengOnPause(Context mContext) {
        MobclickAgent.onPause(mContext);
    }

    /**
     * 设置应用为Debug,此时会打印debug信息
     * 已测试
     */
    public static void uMengDebug() {
        MobclickAgent.setDebugMode(true);
    }


    /**
     * 事件数量统计
     *
     * @param event_id:需要在友盟网上先注册事件id号 已测试
     */
    public static void uMengOnEvent(Context mContext, String event_id) {
        MobclickAgent.onEvent(mContext, event_id);
    }


    /**
     * 杀死进程
     *
     * @param context
     */
    public static void uMengkillProcess(Context context) {
        MobclickAgent.onKillProcess(context);
    }

    //-----------更新相关-------------
    //	/**
    //	 * 在线更新
    //	 * @param context
    //	 * @param isAuto 是否自动更新
    //	 */
    //	public static void uMengUpdate(Context context,boolean isAuto)
    //	{
    //		if (isAuto) {
    //		  UmengUpdateAgent.update(context);
    //		} else {
    //		  UmengUpdateAgent.forceUpdate(context);
    //		}
    //	}
    //
    //	/**
    //	 * 静默更新版本
    //	 * @param context
    //	 */
    //	public static void uMengSilentUpdate(Context context)
    //	{
    //		UmengUpdateAgent.silentUpdate(context);
    //	}

}
