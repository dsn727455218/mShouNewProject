package com.wp.baselib.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Message;

import com.wp.baselib.Config;
import com.wp.baselib.R;

import java.io.File;
import java.util.Stack;


/**
 * 退出 注销 返回 处理类
 *
 * @author summer
 */
public class ExitUtil {

    public Stack<Activity> activityList;
    private static ExitUtil instance;
    private boolean flag;

    private ExitUtil() {
    }

    public synchronized static ExitUtil getInstance() {
        if (instance == null) {
            instance = new ExitUtil();
        }
        return instance;
    }

    public void addInstance(Activity activity) {
        if (activityList == null) {
            activityList = new Stack<Activity>();
        }
        activityList.add(activity);
    }

    /**
     * 获取当前的activity
     *
     * @return
     */
    public Activity currentActivity() {
        return activityList.lastElement();
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityList.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityList.remove(activity);
            AlertUtil.clearDialog();
            activity.finish();
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityList) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void ExitApp() {
        if (activityList == null)
            return;
        while (!activityList.empty()) {
            Activity activity = activityList.pop();
            if (activity != null) {
                activity.finish();
            }
        }
    }

    /**
     * 退出整个应用,带提示功能
     *
     * @param context
     * @param exit_msg_res
     * @return
     */
    public boolean exitShowDialog(final Context context, int exit_msg_res) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(exit_msg_res));
        builder.setPositiveButton(context.getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        flag = true;
                        ExitApp();
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                    }
                });
        builder.setNeutralButton(context.getResources().getString(R.string.cancle),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        flag = false;
                    }
                });
        builder.show();
        return flag;
    }

    /**
     * 退出整个应用Toast显示
     *
     * @param context
     * @return
     */
    public boolean exitShowToast(Context context) {

        if (!ToolsUtil.isFastDoubleClick(2000)) {
            ToastUtil.showToast("再按一次退出程序");
        } else {
            ExitApp();
        }
        return true;

    }

    /**
     * 清除图片缓存暂时不弄进度
     *
     * @param context
     */
    public void clearCache(final Context context) {

        AlertUtil.showAlert(context,
                R.string.clear_cache_title,
                R.string.clear_cache_msg, R.string.ok,
                new OnClickListener() {

                    public void onClick(DialogInterface dialog,
                                        int which) {
                        final Handler handler = new Handler() {
                            public void handleMessage(Message msg) {
                                if (msg.what == 1) {
                                    ToastUtil.showToast("缓存清除成功");
                                } else {
                                    ToastUtil.showToast("缓存清除失败");
                                }
                            }
                        };
                        ToastUtil.showToast("清除中...");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message msg = new Message();
                                try {
                                    clearAppCache(context);
                                    msg.what = 1;
                                } catch (Exception e) {
                                    msg.what = -1;
                                    e.printStackTrace();
                                }
                                handler.sendMessage(msg);
                            }
                        }).start();

                    }
                }, R.string.cancle, null);

    }


    /**
     * 清除app缓存
     */
    public void clearAppCache(Context context) {
//        FileUtil.deleteFile(context.getCacheDir());//data/data/cache目录
//		FileUtil.deleteFile(context.getFilesDir());//data/data/flies目录
        //2.2版本才有将应用缓存转移到sd卡的功能
        if (VERSION.SDK_INT >= VERSION_CODES.FROYO) {
            if (context.getExternalCacheDir() != null)
                FileUtil.deleteFile(context.getExternalCacheDir());
        }
        File temp = new File(FileUtil.getSDRootPath(), Config.CACHE_ROOT);
        File dataTemp = FileUtil.getCachePath(context, CacheUtil.CACHEDATAPATH);
        FileUtil.deleteFile(temp); // 删除sdcard里图片缓存
        if (dataTemp != null)
            FileUtil.deleteFile(dataTemp); //删除数据缓存
//		FastBitmap.create(context).clearCache();//异步任务
    }
}
