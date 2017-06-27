package com.wp.baselib.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import com.wp.baselib.MainApplication;
import com.wp.baselib.R;

import java.io.File;

import java.util.List;

/**
 * 常用视图操作工具栏
 */
public class UiUtil {

    private static final String TAG = UiUtil.class.getSimpleName();

    /**
     * 启动打电话Intent
     *
     * @param context
     * @param phoneNumber
     */
    public static void startDialer(Context context, String phoneNumber) {
        try {
            Intent dial = new Intent();
            dial.setAction(Intent.ACTION_DIAL);
            dial.setData(Uri.parse("tel:" + phoneNumber));
            context.startActivity(dial);
        } catch (Exception ex) {
            LogUtil.e(TAG, "Error starting phone dialer intent.", ex);
            ToastUtil.showToast("对不起,我们找不到任何应用程序来打电话!");
        }
    }

    /**
     * 启动第三方市场操作
     *
     * @param context
     */
    public static void startMarket(Context context) {
        try {
            Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            ToastUtil.showToast("对不起,没找到可用的应用市场");
        }
    }


    /**
     * 启动发短信Intent
     *
     * @param context
     * @param phoneNumber
     */
    public static void startSmsIntent(Context context, String phoneNumber) {
        try {
            Uri uri = Uri.parse("sms:" + phoneNumber);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.putExtra("address", phoneNumber);
            intent.setType("vnd.android-dir/mms-sms");
            context.startActivity(intent);
        } catch (Exception ex) {
            LogUtil.e(TAG, "Error starting sms intent.", ex);
            ToastUtil.showToast("对不起,我们找不到任何应用程序发送短信!");
        }
    }

    /**
     * 启动发送邮箱intent
     *
     * @param context
     * @param emailAddress
     */
    public static void startEmailIntent(Context context, String emailAddress) {
        try {
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setType("plain/text");
            intent.putExtra(android.content.Intent.EXTRA_EMAIL,
                    new String[]{emailAddress});
            context.startActivity(intent);
        } catch (Exception ex) {
            LogUtil.e(TAG, "Error starting email intent.", ex);
            ToastUtil.showToast("对不起,我们找不到任何应用程序来发送电子邮件!");
        }
    }

    /**
     * 启动浏览器intent
     *
     * @param context
     * @param url
     */
    public static void startWebIntent(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        } catch (Exception ex) {
            LogUtil.e(TAG, "Error starting url intent.", ex);
            ToastUtil.showToast("对不起,我们找不到任何应用程序用于查看该url !");
        }
    }

    /*
     * 地图检查操作
     */
    public static boolean checkGoogleMap(Context context) {
        Boolean isInstallGMap = false;
        List<PackageInfo> packs = context.getPackageManager()
                .getInstalledPackages(0);
        int size = packs.size();
        for (int i = 0; i < size; i++) {
            PackageInfo p = packs.get(i);
            if (p.versionName == null) { // system packages
                continue;
            }
            if ("com.google.android.apps.maps".equals(p.packageName)) {
                isInstallGMap = true;
                break;
            }
        }
        return isInstallGMap;
    }

    /**
     * 启动地图并定点
     */
    public static void PostionMapPoint(Context context, String Lat, String Lon) {
        Intent it = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://ditu.google.cn/maps?hl=zh&mrt=loc&q=" + Lat
                        + "," + Lon + ""));
        context.startActivity(it);
    }

    public static void NavigateMapPath(Context context, String slat,
                                       String slon, String dlat, String dlon) {
        StringBuilder params = new StringBuilder().append("&dirflg=");
        // final CharSequence[] items = { "乘车", "步行", "驾车" };
        // params.append("r");
        // params.append("w");
        // params.append("d");
        params.append("d");
        Intent i = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://ditu.google.cn/maps?f=d&source=s_d&saddr="
                        + slat + "," + slon + "&daddr=" + dlat + "," + dlon
                        + "&hl=zh&t=m&" + params));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        i.setClassName("com.google.android.apps.maps",
                "com.google.android.maps.MapsActivity");
        context.startActivity(i);
    }

    public static void postionMap(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(uri);
        intent.setClassName("com.google.android.apps.maps",
                "com.google.android.maps.MapsActivity");
        context.startActivity(intent);
    }

    /**
     * 根据屏幕设置字体大小
     *
     * @param screenWidth
     * @param screenHeight
     * @return
     */
    public static int adjustFontSize(WindowManager windowmanager) {

        int screenWidth = windowmanager.getDefaultDisplay().getWidth();

        if (screenWidth <= 240) { // 240X320 屏幕
            return 10;
        } else if (screenWidth <= 320) { // 320X480 屏幕
            return 14;
        } else if (screenWidth <= 480) { // 480X800 或 480X854 屏幕
            return 24;
        } else if (screenWidth <= 540) { // 540X960 屏幕
            return 26;
        } else if (screenWidth <= 800) { // 800X1280 屏幕
            return 26;
        } else { // 大于 800X1280
            return 26;
        }
    }

    /**
     * 开启分享Intent
     *
     * @param content 分享的内容
     */
    public static void startShareIntent(Activity context, String content) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain"); // 分享的数据类型
            intent.putExtra(Intent.EXTRA_SUBJECT, context.getTitle()); // 主题
            intent.putExtra(Intent.EXTRA_TEXT, content); // 内容
            intent = Intent.createChooser(intent, "朋友分享");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception ex) {
            LogUtil.e(TAG, "Error starting ShareIntent", ex);
            ToastUtil.showToast("对不起,我们找不到分享程序!");
        }
    }

    /**
     * 本地照片分享
     *
     * @param photoUri 全路径名
     * @param activity
     */
    public static void SharePhoto(String photoUri, final Activity activity, String content) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        File file = new File(photoUri);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        shareIntent.putExtra(Intent.EXTRA_TEXT, content); // 内容
        activity.startActivity(Intent.createChooser(shareIntent, activity.getTitle()));
    }

    /**
     * 通过加载网络图片照片分享处理
     *
     * @param activity
     * @param cacheDir 存放缓存目录名【可以自定义,但最好和列表缓存目录一致减少网络访问流量】
     * @param httpUrl  网络地址
     * @param content  分享的内容
     */
    public static void SharePhoto(final Activity activity, final String cacheDir, final String httpUrl, final String content) {
//
//        final Handler handler = new Handler(new Handler.Callback() {
//            @Override
//            public boolean handleMessage(Message msg) {
//                Uri uri = null;
//                if (msg.what == 1 && msg.obj != null) {
//                    uri = (Uri) msg.obj;
//                } else if (msg.what == 0) {
//                    ToastUtil.showToast("获取图片失败");
//                }
//                Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                shareIntent.setType("image/*");
//                if (uri != null) {
//                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
//                }
//                shareIntent.putExtra(Intent.EXTRA_TEXT, content); // 内容
//                activity.startActivity(Intent.createChooser(shareIntent, activity.getTitle()));
//                AlertUtil.closeProgressDialog();
//                return false;
//            }
//        });
//        AlertUtil.showProgressDialog(activity, "", "请等待...");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Uri uri = null;
//                File file = FileUtil.getLocalFilePath(activity, cacheDir, httpUrl);
//
//                if (file.exists()) {
//                    uri = Uri.fromFile(file);
//                    handler.sendMessage(handler.obtainMessage(1, uri));
//                    return;
//                }
//                byte[] bytes = null;
//                FileOutputStream fileout = null;
//                try {
//                    bytes = new NetWorkUtil(activity).getPacketByByte(httpUrl);
//                    if (bytes != null) {
//                        fileout = new FileOutputStream(file);
//                        fileout.write(bytes, 0, bytes.length);
//                        fileout.flush();
//                        uri = Uri.fromFile(file); //通过网络地址获取本地图片
//                        handler.sendMessage(handler.obtainMessage(1, uri));
//                    }
//                } catch (Exception e1) {
//                    e1.printStackTrace();
//                    handler.sendMessage(handler.obtainMessage(0, uri));
//                } finally {
//                    if (fileout != null) {
//                        try {
//                            fileout.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        }).start();

    }

    /**
     * 没有网络时提醒设置网络对话框
     *
     * @param context
     */
    public static void showNoNetworkDialog(final Context context) {
        AlertUtil.showAlert(context, R.string.not_network,
                R.string.open_gprs_msg, R.string.ok, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                        if (android.os.Build.VERSION.SDK_INT > 10) {
                            // 3.0以上打开设置界面
                            context.startActivity(new Intent(
                                    android.provider.Settings.ACTION_SETTINGS));
                        } else {
                            // context.startActivity(new Intent(
                            // android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                            Intent intent = new Intent("/");
                            intent.setComponent(new ComponentName(
                                    "com.android.settings",
                                    "com.android.settings.WirelessSettings"));
                            intent.setAction("android.intent.action.VIEW");
                            context.startActivity(intent);
                        }
                    }
                }, R.string.cancle, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
    }

    /**
     * 显示popup弹窗1
     *
     * @param context
     * @param layout_resid 弹窗布局文件
     * @param fromview     在哪儿添加弹窗 如：Button等视图
     * @param x            坐标
     * @param y            坐标
     * @return PopupWindow
     */
    public static PopupWindow showPopupWindow(final Context context,
                                              int layout_resid, View fromview, int x, int y) {
        View view = LayoutInflater.from(context).inflate(layout_resid, null);
        return showPopupWindow(context, view, fromview, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, x, y);
    }

    /**
     * 显示popup弹窗,指定相应的位置
     *
     * @param context
     * @param layout_resid 弹窗布局文件
     * @param fromview     在哪儿添加弹窗 如：Button等视图
     * @param gravity      布局位置
     * @param x偏移坐标
     * @param y偏移坐标
     * @return PopupWindow
     */
    public static PopupWindow showLocationPopupWindow(final Context context,
                                                      int layout_resid, View fromview, int gravity, int x, int y) {

        View view = LayoutInflater.from(context).inflate(layout_resid, null);

        PopupWindow mPopupWindow = new PopupWindow(view,
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(
                android.R.color.transparent));
        mPopupWindow.setOutsideTouchable(true);

        mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        mPopupWindow.update();
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);

        if (!mPopupWindow.isShowing()) {
            mPopupWindow.showAtLocation(fromview, gravity, x, y);
            // 在点击视图中间
        }

        return mPopupWindow;
    }

    /**
     * 显示popup弹窗2
     *
     * @param context
     * @param toView   弹窗布局文件
     * @param fromview 在哪儿添加弹窗 如：Button等视图
     * @param width    宽度
     * @param height   高度
     * @param x        坐标
     * @param y        坐标
     * @return
     */
    public static PopupWindow showPopupWindow(final Context context, View toview,
                                              View fromview, int width, int height, int x, int y) {

        PopupWindow mPopupWindow = new PopupWindow(toview,
                width, height);
        mPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(
                android.R.color.transparent));
        mPopupWindow.setOutsideTouchable(true);

        mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        mPopupWindow.update();
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);

        if (!mPopupWindow.isShowing()) {
            mPopupWindow.showAsDropDown(fromview, x, y);
        }
        return mPopupWindow;
    }

    /**
     * 汽包窗口
     *
     * @param context
     * @param toview
     * @param fromview
     * @param x
     * @param y
     * @return
     */
    public static PopupWindow showPopupWindow(final Context context, View toview,
                                              View fromview, int x, int y) {
        return showPopupWindow(context, toview, fromview, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, x, y);
    }

    /**
     * 判断Intent是否被激活
     *
     * @param context
     * @param action
     * @return
     */
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * 设置listview高度，注意listview子项必须为LinearLayout才能调用该方法
     *
     * @param listview listview
     */
    public static void setListViewHeight(ListView listview) {
        int totalHeight = 0;
        ListAdapter adapter = listview.getAdapter();
        if (null != adapter) {
            int count = adapter.getCount();
            for (int i = 0; i < count; i++) {
                View listItem = adapter.getView(i, null, listview);
                if (null != listItem) {
                    listItem.measure(0, 0);// 注意listview子项必须为LinearLayout才能调用该方法
                    totalHeight += listItem.getMeasuredHeight();
                }
            }

            ViewGroup.LayoutParams params = listview.getLayoutParams();
            params.height = totalHeight
                    + (listview.getDividerHeight() * (listview.getCount() - 1));
            listview.setLayoutParams(params);

        }
    }

    private static final String ShortCut = "ShortCut";
    private static final String isShortCut = "isshrotcut";

    /**
     * 创建桌面快捷方式
     *
     * @param act
     * @param iconResId
     * @param appnameResId
     */
    private static void createShortCut(Activity act, Class<?> classz) {
        // com.android.launcher.permission.INSTALL_SHORTCUT 权限声明
        Intent shortcut = new Intent(
                "com.android.launcher.action.INSTALL_SHORTCUT");

        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
                act.getString(R.string.app_name));
        shortcut.putExtra("duplicate", false); // 不允许重复创建
        ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(
                act, R.drawable.ic_launcher);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes); // 快捷方式资源图标

        String action = "com.anyjoys";
        Intent respondIntent = new Intent(act, classz);
        respondIntent.setAction(action);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, respondIntent);
        act.sendBroadcast(shortcut);
        SharedPreferences spf = act.getSharedPreferences(ShortCut,
                Context.MODE_PRIVATE);
        Editor edit = spf.edit();
        edit.putBoolean(isShortCut, true);
        edit.commit();

    }

    /**
     * 删除无效的快捷键
     */
    private void deleteShortcut(Activity act, Class<?> classz) {

        Intent shortcut = new Intent(
                "com.android.launcher.action.UNINSTALL_SHORTCUT");
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
                act.getString(R.string.app_name)); // 必须和创建时名字一致

        String action = "com.anyjoys"; // 必须和创建时名字一致
        String appClass = classz.getName();
        ComponentName comp = new ComponentName(act.getPackageName(), appClass);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT,
                new Intent(action).setComponent(comp));
        act.sendBroadcast(shortcut);
    }

    /**
     * 判断快捷是否存在 不存在則提示創建 否則不創建
     *
     * @param uri
     * @param activity
     * @param res_app_name
     * @return
     */
    public static void hasShortcut(final Activity activity, Class<?> classz) {
        SharedPreferences spf = activity.getSharedPreferences(ShortCut,
                Context.MODE_PRIVATE);
        boolean isShowIcon = spf.getBoolean(isShortCut, false);
        if (!isShowIcon) {
            showShortCut(activity, classz);
        }
    }

    private static void showShortCut(final Activity activity, final Class<?> classz) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("是否创建快捷方式").setCancelable(false)
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        createShortCut(activity, classz);
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).create().show();
    }

    /**
     * 是否立即登录选择框
     *
     * @param context                     当前上下文
     * @param callBackActivity登录成功后回调的页面类
     * @param loginActivityClassz         登陆的Activity
     */
    public static void startLoginDialog(final Activity context,
                                        final Class<?> callBackActivity, final Class<?> loginActivityClassz) {

        AlertUtil.showAlert(context, 0, "您还没登录是否立即登录？", R.string.ok,
                new OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();

                        if (callBackActivity != null) {
                            intent.putExtra("preclassName",
                                    callBackActivity.getName());
                        }
                        intent.setClass(context, loginActivityClassz);
                        context.startActivity(intent);
                    }
                }, R.string.cancle, null);
    }

    /**
     * 是否立即登录选择框
     *
     * @param context             当前上下文
     * @param extras              扩展参数
     * @param callBackActivity    登录成功后回调的页面类
     * @param loginActivityClassz 登陆的Activity
     */
    public static void startLoginDialog(final Activity context, final Bundle extras,
                                        final Class<?> callBackActivity, final Class<?> loginActivityClassz) {

        AlertUtil.showAlert(context, 0, "您还没登录是否立即登录？", R.string.ok,
                new OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.putExtras(extras);
                        intent.putExtra("preclassName",
                                callBackActivity.getName());
                        intent.setClass(context, loginActivityClassz);
                        context.startActivity(intent);
                    }
                }, R.string.cancle, null);
    }

    /**
     * 返回前一个页面的类实例
     *
     * @param intent
     * @return
     * @throws ClassNotFoundException
     */
    public static Class<?> getPreActivity(Intent intent) throws ClassNotFoundException {
        String className = intent.getStringExtra("preclassName");
        return className != null ? Class.forName(className) : null;
    }

    /**
     * 发送App异常崩溃报告
     *
     * @param context
     * @param crashReport 异常信息
     */
    public static void sendAppCrashReport(final Context context, final String crashReport) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("错误信息");
        builder.setMessage("应用出小差了,需要重新启动");
        builder.setPositiveButton("提交", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //发送异常报告
                Intent i = new Intent(Intent.ACTION_SEND);
                //i.setType("text/plain"); //模拟器
                i.setType("message/rfc822"); //真机
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"summer@anyjoys.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.app_name) + "- 错误报告");
                i.putExtra(Intent.EXTRA_TEXT, crashReport);
                context.startActivity(Intent.createChooser(i, "发送错误报告"));
                ExitUtil.getInstance().ExitApp();
            }
        });
        builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ExitUtil.getInstance().ExitApp();
            }
        });
        builder.show();
    }

    /**
     * 添加网页的点击图片展示支持
     *
     * @param cxt 全局应用
     * @param wv  webview 控件
     */
    @SuppressLint("JavascriptInterface")
    public static void addWebImageShow(final MainApplication cxt, WebView wv) {
        wv.getSettings().setJavaScriptEnabled(true);
        wv.addJavascriptInterface(new OnWebViewImageListener() {

            @Override
            public void onImageClick(String bigImageUrl) {
                if (bigImageUrl != null) {
                    Bundle bundle = new Bundle();
                    bundle.putStringArray("images", new String[]{bigImageUrl});
//                    cxt.redirectAndPrameter(BigImageActivity.class, bundle);
                }
            }
        }, "mWebViewImageListener");
    }

    /**
     * 增加web图片点击处理
     *
     * @author summer
     * @description
     * @date 2014-5-25 下午10:26:27
     */
    public interface OnWebViewImageListener {

        /**
         * 点击webview上的图片，传入该缩略图的大图Url
         *
         * @param bigImageUrl
         */
        void onImageClick(String bigImageUrl);

    }
}
