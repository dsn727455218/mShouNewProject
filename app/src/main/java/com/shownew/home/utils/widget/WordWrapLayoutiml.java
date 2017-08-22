package com.shownew.home.utils.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shownew.home.R;
import com.wp.baselib.widget.WordWrapLayout;

import java.util.ArrayList;

/**
 * 处理图片显示
 * Created by WP on 2017/7/27.
 */

public class WordWrapLayoutiml extends WordWrapLayout {
    public WordWrapLayoutiml(Context context) {
        super(context);
    }

    public WordWrapLayoutiml(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.width=getWidth();
    }

    private int width = 0;

    public void setWidth(int width) {
        this.width = width;
    }

    public ArrayList<ImageView> getImage(final int size) {
        removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        int ImageWidth = (int) ((width - getPaddingLeft() - getPaddingRight() - (count * 20)) * 1.061 / count);
        params.width = ImageWidth;
        params.height = ImageWidth;

        params1.width = ImageWidth;
        params1.height = ImageWidth;
        ArrayList<ImageView> views = new ArrayList<ImageView>();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (int i = 0; i < size; i++) {
            View view = inflater.inflate(R.layout.layout_select_img, null);
            view.setLayoutParams(params);
            ImageView deleteImg = (ImageView) view.findViewById(R.id.delete_pic);
            ImageView pic = (ImageView) view.findViewById(R.id.pic);
            pic.setLayoutParams(params1);
            views.add(pic);
            deleteImg.setVisibility(GONE);
            if (!isShow && size >= 2 && size - 2 == i) {
                deleteImg.setVisibility(VISIBLE);
                deleteImg.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (delectImgLisener != null) {
                            delectImgLisener.delete(size - 2);
                        }
                    }
                });
            }
            addView(view);
        }
        return views;
    }

    private int count = 3;

    public void setLineShowCount(int count) {
        this.count = count;
    }

    private DelectImgLisener delectImgLisener;

    public void setDelectImgLisener(DelectImgLisener delectImgLisener) {
        this.delectImgLisener = delectImgLisener;
    }

    public interface DelectImgLisener {
        void delete(int position);
    }

    private boolean isShow;

    public void setShow(boolean isShow) {
        this.isShow = isShow;
    }

}
