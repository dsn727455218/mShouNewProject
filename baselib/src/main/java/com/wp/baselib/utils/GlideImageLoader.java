package com.wp.baselib.utils;

import android.app.Activity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.wp.baselib.R;
import com.wp.baselib.utils.imagepicker.loader.ImageLoader;

import java.io.File;

import static android.R.attr.path;


/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/5
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class GlideImageLoader implements ImageLoader {

    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
        try {
            Glide.with(activity).load(new File(path))//
                    .placeholder(R.drawable.default_image)//
                    .error(R.drawable.default_image)//
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();

        }


    }

    @Override
    public void displayImageFromUrl(Activity activity, String urlpath, ImageView imageView, int width, int height) {
        Glide.with(activity).load(urlpath)//
                .placeholder(R.drawable.default_image)//
                .error(R.drawable.default_image)//
                .diskCacheStrategy(DiskCacheStrategy.NONE)//
                .into(imageView);
    }

    @Override
    public void clearMemoryCache() {
    }
}
