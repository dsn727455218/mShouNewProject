package com.wp.baselib.utils.imagepicker.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.wp.baselib.utils.imagepicker.ImagePicker;
import com.wp.baselib.utils.imagepicker.Utils;
import com.wp.baselib.utils.imagepicker.bean.ImageItem;
import com.wp.baselib.utils.imagepicker.loader.ImageLoader;
import com.wp.baselib.utils.imagepicker.photoview.PhotoView;
import com.wp.baselib.utils.imagepicker.photoview.PhotoViewAttacher;

import java.util.ArrayList;


/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ImagePageAdapter extends PagerAdapter {

    private int screenWidth;
    private int screenHeight;
    private ImagePicker imagePicker;
    private ArrayList<ImageItem> images;
    private Activity mActivity;
    public PhotoViewClickListener listener;
    private String[] mImageUrls;

    public ImagePageAdapter(Activity activity, ArrayList<ImageItem> images, String[] mImageUrls) {
        this.mActivity = activity;
        this.images = images;
        this.mImageUrls = mImageUrls;
        DisplayMetrics dm = Utils.getScreenPix(activity);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        imagePicker = ImagePicker.getInstance();
        ImageLoader imageLoader = imagePicker.getImageLoader();
        if (imageLoader == null) {
            imagePicker.setImageLoader(new com.wp.baselib.utils.GlideImageLoader());
        }
    }

    public void setData(ArrayList<ImageItem> images) {
        this.images = images;
    }

    public void setPhotoViewClickListener(PhotoViewClickListener listener) {
        this.listener = listener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(mActivity);
        if (null != images) {
            ImageItem imageItem = images.get(position);
            imagePicker.getImageLoader().displayImage(mActivity, imageItem.path, photoView, screenWidth, screenHeight);
        } else if (null != mImageUrls) {
            //在浏览评价大图的时候显示原图
            imagePicker.getImageLoader().displayImageFromUrl(mActivity, mImageUrls[position]+"_", photoView, screenWidth, screenHeight);
        }
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                if (listener != null) listener.OnPhotoTapListener(view, x, y);
            }
        });
        container.addView(photoView);
        return photoView;
    }

    @Override
    public int getCount() {
        if (null != mImageUrls) {
            return mImageUrls.length;
        } else if (null != images) {
            return images.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public interface PhotoViewClickListener {
        void OnPhotoTapListener(View view, float v, float v1);
    }
}
