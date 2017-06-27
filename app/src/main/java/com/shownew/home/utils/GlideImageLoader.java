package com.shownew.home.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.shownew.home.R;
import com.wp.baselib.widget.banner.loader.ImageLoader;

/**
 * @author Jason
 * @version 1.0
 * @date 2017/4/8 0008
 */

public class GlideImageLoader extends ImageLoader {
    private int sourceId = R.drawable.sevice_runseize;

    public GlideImageLoader(int sourceId) {
        this.sourceId = sourceId;
    }

    public GlideImageLoader() {

    }

    @Override
    public void displayImage(Context context, Object path, final ImageView imageView) {

//        Glide.with(context).load(path).placeholder(sourceId).error(sourceId).into(imageView);
                Glide.with(context).load(path).asBitmap().
                                placeholder(sourceId)
                                .error(sourceId)
                                .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                                imageView.setImageBitmap(bitmap);
                            }
                        });
    }

}
