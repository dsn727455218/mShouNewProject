package com.shownew.home.receiver;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.lzy.okgo.callback.FileCallback;
import com.shownew.home.R;
import com.shownew.home.ShouNewApplication;
import com.shownew.home.module.PublicApi;
import com.shownew.home.utils.AppUpdateUtil;
import com.wp.baselib.utils.ToolsUtil;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;


public class AppUpdateService extends IntentService {


    private RemoteViews remoteViews;
    private Notification notification;
    private NotificationManager manager;
    private static final int NOTICE_ID_TYPE_0 = R.string.app_name;
    private PublicApi mPublicApi;
    public AppUpdateService() {
        super("AppUpdateService'");
        ShouNewApplication mShouNewApplication = ShouNewApplication.getInstance();
        mPublicApi = new PublicApi(mShouNewApplication);
    }

    public void sendResidentNoticeType2(Context context, String title, int progress, String apkUrl) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setOngoing(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.notifiticon_update_file);
        remoteViews.setTextViewText(R.id.title_tv, title);
        remoteViews.setTextViewText(R.id.progress, String.format("%s", progress));
        remoteViews.setProgressBar(R.id.progressBar, 100, progress, false);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        notification = builder.build();
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            notification = builder.build();
            notification.bigContentView = remoteViews;
        }
        notification.contentView = remoteViews;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTICE_ID_TYPE_0, notification);
        downFile(apkUrl);
    }

    /**
     * 下载文件
     */
    private void  downFile(String apkUrl) {
        if (TextUtils.isEmpty(apkUrl))
            return;
        mPublicApi.downFile(apkUrl, new FileCallback("shouniu.apk") {
            @Override
            public void resultDataUI(File data, Call call, Response response) {
                super.resultDataUI(data, call, response);
                installApk(data);
                AppUpdateUtil.isDownloading=false;
                if (manager != null) {
                    manager.cancel(NOTICE_ID_TYPE_0);
                }
            }

            //  * @param currentSize  当前下载的字节数
            //     * @param totalSize    总共需要下载的字节数
            //     * @param progress     当前下载的进度
            //     * @param networkSpeed 当前下载的速度 字节/秒
            @Override
            public void downloadProgress(long currentSize, long totalSize, final float progress, long networkSpeed) {
                super.downloadProgress(currentSize, totalSize, progress, networkSpeed);
                int progressCur = (int) (progress * 100);
                if (remoteViews != null) {
                    String value = progressCur + "%";
                    remoteViews.setTextViewText(R.id.progress, value);
                    remoteViews.setProgressBar(R.id.progressBar, 100, progressCur, false);
                    notification.contentView = remoteViews;
                    manager.notify(NOTICE_ID_TYPE_0, notification);
                    if (progressCur == 100) {
                        remoteViews.setTextViewText(R.id.title_tv, "首牛云控更新包下载完成");
                        notification.defaults = Notification.DEFAULT_SOUND;
                        manager.cancel(NOTICE_ID_TYPE_0);
                    }
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                AppUpdateUtil.isDownloading=false;
                if (manager != null) {
                    manager.cancel(NOTICE_ID_TYPE_0);
                }
            }
        });
    }


    private void installApk(File data) {
        if (null != data)
            ToolsUtil.installAPK(data, AppUpdateService.this);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String apkUrl = intent.getStringExtra("url");
            sendResidentNoticeType2(this, "首牛云控更新包下载中", 0, apkUrl);
        }
    }


    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        //，我们可以选择START_NOT_STICKY，这也是IntentService的默认选项，当我们认为操作十分重要时，则应该选择START_REDELIVER_INTENT 型服务
        setIntentRedelivery(true);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (manager != null) {
            manager.cancel(NOTICE_ID_TYPE_0);
            stopSelf();
        }

    }
}

