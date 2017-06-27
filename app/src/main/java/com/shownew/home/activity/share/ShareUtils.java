package com.shownew.home.activity.share;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.Gravity;

import com.shownew.home.R;
import com.shownew.home.ShouNewApplication;
import com.shownew.home.module.UserAPI;
import com.shownew.home.module.entity.SourcesEntity;
import com.umeng.social.tool.UMImageMark;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMEmoji;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMMin;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.media.UMusic;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.SocializeUtils;
import com.wp.baselib.utils.LogUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 友盟分享工具类
 *
 * @author Jason
 * @version 1.0
 * @date 2017/5/3 0003
 */

public class ShareUtils {
    private ArrayList<SnsPlatform> platforms = new ArrayList<SnsPlatform>();
    private UMImage imageurl, imagelocal;
    private UMVideo video;
    private UMusic music;
    private UMEmoji emoji;
    private UMWeb web;
    private File file;
    private ArrayList<String> styles = new ArrayList<String>();

    public ArrayList<SnsPlatform> getPlatforms() {
        return platforms;
    }

    private UserAPI mUserAPI;

    public ShareUtils(Context context, ShouNewApplication shouNewApplication) {
        mUserAPI = new UserAPI(shouNewApplication);
        platforms.clear();
        //添加分享平台
        platforms.add(SHARE_MEDIA.WEIXIN.toSnsPlatform());
        platforms.add(SHARE_MEDIA.WEIXIN_CIRCLE.toSnsPlatform());
        platforms.add(SHARE_MEDIA.WEIXIN_FAVORITE.toSnsPlatform());
        platforms.add(SHARE_MEDIA.SINA.toSnsPlatform());
        platforms.add(SHARE_MEDIA.QQ.toSnsPlatform());
        platforms.add(SHARE_MEDIA.QZONE.toSnsPlatform());
        initMedia(context);  //初始化内容格式
    }

    /**
     * 设置分享平台
     *
     * @param postion  选择分享平台
     * @param type     选择分享内容类型
     * @param activity
     */
    public void setSharePlatform(int postion, int type, Activity activity) {
        initStyles(platforms.get(postion).mPlatform);//设置到那个分享  (添加分享的内容格式)
        setShareType(type, activity, platforms.get(postion).mPlatform);  //设置分享的内容格式
    }


    /**
     * 初始化可以分享的内容的种类
     *
     * @param share_media
     */
    private void initStyles(SHARE_MEDIA share_media) {
        styles.clear();
        if (share_media == SHARE_MEDIA.QQ) {
            styles.add(com.shownew.home.activity.share.StyleUtil.WEB11);
            styles.add(com.shownew.home.activity.share.StyleUtil.IMAGELOCAL);
            styles.add(com.shownew.home.activity.share.StyleUtil.IMAGEURL);
            styles.add(com.shownew.home.activity.share.StyleUtil.MUSIC11);
            styles.add(com.shownew.home.activity.share.StyleUtil.VIDEO11);
        } else if (share_media == SHARE_MEDIA.QZONE) {
            styles.add(com.shownew.home.activity.share.StyleUtil.WEB11);
            styles.add(com.shownew.home.activity.share.StyleUtil.TEXT);
            styles.add(com.shownew.home.activity.share.StyleUtil.IMAGELOCAL);
            styles.add(com.shownew.home.activity.share.StyleUtil.IMAGEURL);
            styles.add(com.shownew.home.activity.share.StyleUtil.MUSIC11);
            styles.add(com.shownew.home.activity.share.StyleUtil.VIDEO11);
        } else if (share_media == SHARE_MEDIA.SINA) {
            styles.add(com.shownew.home.activity.share.StyleUtil.WEB11);
            styles.add(com.shownew.home.activity.share.StyleUtil.TEXT);
            styles.add(com.shownew.home.activity.share.StyleUtil.TEXTANDIMAGE);
            styles.add(com.shownew.home.activity.share.StyleUtil.IMAGELOCAL);
            styles.add(com.shownew.home.activity.share.StyleUtil.IMAGEURL);
            styles.add(com.shownew.home.activity.share.StyleUtil.MUSIC11);
            styles.add(com.shownew.home.activity.share.StyleUtil.VIDEO11);
        } else if (share_media == SHARE_MEDIA.WEIXIN) {
            styles.add(com.shownew.home.activity.share.StyleUtil.WEB11);
            styles.add(com.shownew.home.activity.share.StyleUtil.TEXT);
            styles.add(com.shownew.home.activity.share.StyleUtil.IMAGELOCAL);
            styles.add(com.shownew.home.activity.share.StyleUtil.IMAGEURL);
            styles.add(com.shownew.home.activity.share.StyleUtil.MUSIC11);
            styles.add(com.shownew.home.activity.share.StyleUtil.VIDEO11);
            styles.add(com.shownew.home.activity.share.StyleUtil.EMOJI);
        } else if (share_media == SHARE_MEDIA.WEIXIN_CIRCLE) {
            styles.add(com.shownew.home.activity.share.StyleUtil.WEB11);
            styles.add(com.shownew.home.activity.share.StyleUtil.TEXT);
            styles.add(com.shownew.home.activity.share.StyleUtil.IMAGELOCAL);
            styles.add(com.shownew.home.activity.share.StyleUtil.IMAGEURL);
            styles.add(com.shownew.home.activity.share.StyleUtil.MUSIC11);
            styles.add(com.shownew.home.activity.share.StyleUtil.VIDEO11);
        } else if (share_media == SHARE_MEDIA.WEIXIN_FAVORITE) {
            styles.add(com.shownew.home.activity.share.StyleUtil.WEB11);
            styles.add(com.shownew.home.activity.share.StyleUtil.TEXT);
            styles.add(com.shownew.home.activity.share.StyleUtil.IMAGELOCAL);
            styles.add(com.shownew.home.activity.share.StyleUtil.IMAGEURL);
            styles.add(com.shownew.home.activity.share.StyleUtil.MUSIC11);
            styles.add(com.shownew.home.activity.share.StyleUtil.VIDEO11);
        }
    }

    /**
     * 分享的监听
     */
    private UMShareListener shareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            LogUtil.d("ShareUtils", "onStart");
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            LogUtil.d("ShareUtils", "成功了");
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            LogUtil.d("ShareUtils", "失败" + t.getMessage());
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            LogUtil.d("ShareUtils", "取消了");

        }
    };

    /**
     * 设置分享的内容
     *
     * @param position
     * @param activity
     * @param share_media
     */
    private void setShareType(int position, Activity activity, SHARE_MEDIA share_media) {
        if (styles.get(position).equals(com.shownew.home.activity.share.StyleUtil.IMAGELOCAL)) {
            new ShareAction(activity).withMedia(imagelocal).setPlatform(share_media).setCallback(shareListener).share();
        } else if (styles.get(position).equals(com.shownew.home.activity.share.StyleUtil.IMAGEURL)) {
            new ShareAction(activity).withMedia(imageurl).setPlatform(share_media).setCallback(shareListener).share();
        } else if (styles.get(position).equals(com.shownew.home.activity.share.StyleUtil.TEXT)) {
            new ShareAction(activity).withText(Defaultcontent.text).setPlatform(share_media).setCallback(shareListener).share();
        } else if (styles.get(position).equals(com.shownew.home.activity.share.StyleUtil.TEXTANDIMAGE)) {
            new ShareAction(activity).withText(Defaultcontent.text).withMedia(imagelocal).setPlatform(share_media).setCallback(shareListener).share();
        } else if (styles.get(position).equals(com.shownew.home.activity.share.StyleUtil.WEB11) || styles.get(position).equals(com.shownew.home.activity.share.StyleUtil.WEB00) || styles.get(position).equals(com.shownew.home.activity.share.StyleUtil.WEB10) || styles.get(position).equals(com.shownew.home.activity.share.StyleUtil.WEB01)) {
            new ShareAction(activity).withText(Defaultcontent.text).withMedia(web).setPlatform(share_media).setCallback(shareListener).share();
        } else if (styles.get(position).equals(com.shownew.home.activity.share.StyleUtil.MUSIC11) || styles.get(position).equals(com.shownew.home.activity.share.StyleUtil.MUSIC00) || styles.get(position).equals(com.shownew.home.activity.share.StyleUtil.MUSIC10) || styles.get(position).equals(com.shownew.home.activity.share.StyleUtil.MUSIC01)) {
            new ShareAction(activity).withMedia(music).setPlatform(share_media).setCallback(shareListener).share();
        } else if (styles.get(position).equals(com.shownew.home.activity.share.StyleUtil.VIDEO11) || styles.get(position).equals(com.shownew.home.activity.share.StyleUtil.VIDEO00) || styles.get(position).equals(com.shownew.home.activity.share.StyleUtil.VIDEO01) || styles.get(position).equals(com.shownew.home.activity.share.StyleUtil.VIDEO10)) {
            new ShareAction(activity).withMedia(video).setPlatform(share_media).setCallback(shareListener).share();
        } else if (styles.get(position).equals(com.shownew.home.activity.share.StyleUtil.EMOJI)) {
            new ShareAction(activity).withMedia(emoji).setPlatform(share_media).setCallback(shareListener).share();
        } else if (styles.get(position).equals(com.shownew.home.activity.share.StyleUtil.FILE)) {
            new ShareAction(activity).withFile(file).withText(Defaultcontent.text).withSubject(Defaultcontent.title).setPlatform(share_media).setCallback(shareListener).share();
        } else if (styles.get(position).equals(com.shownew.home.activity.share.StyleUtil.MINAPP)) {
            UMMin umMin = new UMMin(Defaultcontent.url);
            umMin.setThumb(imagelocal);
            umMin.setTitle(Defaultcontent.title);
            umMin.setDescription(Defaultcontent.text);
            umMin.setPath("pages/page10007/page10007");
            umMin.setUserName("gh_3ac2059ac66f");
            new ShareAction(activity).withMedia(umMin).setPlatform(share_media).setCallback(shareListener).share();
        }
    }

    /**
     * 设置分享的媒体
     *
     * @param context
     */
    private void initMedia(Context context) {
        UMImageMark umImageMark = new UMImageMark();
        umImageMark.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
        umImageMark.setMarkBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
        imageurl = new UMImage(context, Defaultcontent.imageurl);
        imageurl.setThumb(new UMImage(context, R.drawable.ic_launcher));
        imagelocal = new UMImage(context, R.drawable.ic_launcher);
        imagelocal.setThumb(new UMImage(context, R.drawable.ic_launcher));
        music = new UMusic(Defaultcontent.musicurl);
        video = new UMVideo(Defaultcontent.videourl);


        final ArrayList<SourcesEntity> sourcesEntities = mUserAPI.getSourcesData();
        String url = Defaultcontent.url;
        if (sourcesEntities != null && sourcesEntities.size() >= 4) {
            url = sourcesEntities.get(4).getSImg();
        }
        web = new UMWeb(url);

        web.setTitle("首牛云控：我们坚信多方位的车辆监控报警系统，可以让人车更安全。");
        web.setThumb(new UMImage(context, R.drawable.logo));
        web.setDescription("首牛云控：我们坚信多方位的车辆监控报警系统，可以让人车更安全。");



        music.setTitle("This is music title");
        music.setThumb(new UMImage(context, R.drawable.ic_launcher));
        music.setDescription("my description");
        music.setmTargetUrl(Defaultcontent.url);
        video.setThumb(new UMImage(context, R.drawable.ic_launcher));
        video.setTitle("This is video title");
        video.setDescription("my description");

        emoji = new UMEmoji(context, "http://img5.imgtn.bdimg.com/it/u=2749190246,3857616763&fm=21&gp=0.jpg");
        emoji.setThumb(new UMImage(context, R.drawable.ic_launcher));
        file = new File(context.getFilesDir() + "test.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (SocializeUtils.File2byte(file).length <= 0) {
            String content = "U-share分享";
            byte[] contentInBytes = content.getBytes();
            try {
                FileOutputStream fop = new FileOutputStream(file);
                fop.write(contentInBytes);
                fop.flush();
                fop.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
