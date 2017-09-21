package com.wp.baselib.utils;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Toast;

import com.wp.baselib.MainApplication;

import java.util.Timer;
import java.util.TimerTask;


/**
 * 用于显示 Toast
 * Created by wxd on 2016/7/29.
 */
public class ToastUtil {

    private static Toast toast = null;

    public static void showToast(int id) {
        showToast(MainApplication.getInstance().getResources().getString(id));
    }

    /**
     * 调用该方法 如果需要取消显示Toast 使用showToast()
     *
     * @param text text
     */
    public synchronized static void showToast(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(MainApplication.getInstance(), text, Toast.LENGTH_LONG);
        } else {
            try {
                toast.setText(text);
                toast.setDuration(Toast.LENGTH_LONG);
            } catch (Exception e) {
                toast = Toast.makeText(MainApplication.getInstance(), text, Toast.LENGTH_LONG);
                toast.setText(text);
                toast.setDuration(Toast.LENGTH_LONG);

            }
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        ViewGroup lay = (ViewGroup) toast.getView();
        if (lay != null) {
            Drawable drawable = lay.getBackground();
            if (drawable != null) {
                drawable.setAlpha(255);
            }
            if (lay.getChildCount() == 2)  //移除自定义视图
                lay.removeViewAt(0);
        }
        showToast(1000l);
    }

    public static void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }

    /**
     * 设置Toast消失的时间
     *
     * @param date
     */
    public static void showToast(long date) {
        final Timer time = new Timer();
        time.schedule(new TimerTask() {
            @Override
            public void run() {
                if (null != toast)
                    toast.show();
            }
        }, 0, 3500);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (null != time) {
                    cancelToast();
                    time.cancel();
                }
            }
        }, date);
    }

}
