package com.wp.baselib.widget.banner;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wp.baselib.R;
import com.wp.baselib.widget.banner.loader.ImageLoader;

/**
 * @author Jason
 * @version 1.0
 * @date 2017/4/8 0008
 */

public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, final ImageView imageView) {
        Glide.with(context).load(path).placeholder(R.drawable.ic_launcher).error(R.drawable.ic_launcher).into(imageView);
    }

}
