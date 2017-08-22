package com.shownew.home.utils;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.shownew.home.ShouNewApplication;
import com.wp.baselib.utils.imagepicker.ImagePicker;
import com.wp.baselib.utils.imagepicker.ui.ImagePreviewActivity;

/**
 * Created by WP on 2017/8/15.
 */

public class PreviewImgUtils {

    public static void previewImg(ImageView imageView, final String[] dims, final int curPosition, final ShouNewApplication shouNewApplication) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putStringArray(ImagePicker.EXTRA_URL_ITEMS, dims);
                bundle.putInt(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, curPosition);
                shouNewApplication.redirectAndPrameter(ImagePreviewActivity.class, bundle);
            }
        });
    }
}
