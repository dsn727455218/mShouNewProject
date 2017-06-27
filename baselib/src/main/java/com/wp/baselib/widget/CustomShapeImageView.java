package com.wp.baselib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;

import com.wp.baselib.R;


/**
 * Created by wcy .
 */
public class CustomShapeImageView extends BaseImageView {


    /**
     * <?xml version="1.0" encoding="utf-8"?>
     <RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
     xmlns:app="http://schemas.android.com/apk/res-auto"
     android:layout_width="match_parent"
     android:layout_height="match_parent"
     android:fitsSystemWindows="true">

     <com.wcy.shapeimage.shapeimageview.CustomShapeImageView
     android:layout_width="100dp"
     android:layout_height="70dp"
     android:layout_marginLeft="30dp"
     android:layout_marginTop="30dp"
     android:src="@drawable/a"
     app:borderColor="@color/colorAccent"
     app:imageBorderWidth="2dp"
     app:onlyDrawBorder="false"
     app:shape="circle" />

     <com.wcy.shapeimage.shapeimageview.CustomShapeImageView
     android:layout_width="100dp"
     android:layout_height="70dp"
     android:layout_centerInParent="true"
     android:src="@drawable/a"
     app:borderColor="@android:color/holo_red_dark"
     app:imageBorderWidth="2dp"
     app:leftBottomRadius="10dp"
     app:leftTopRadius="30dp"
     app:onlyDrawBorder="false"
     app:rightBottomRadius="15dp"
     app:shape="rectangle" />

     <com.wcy.shapeimage.shapeimageview.CustomShapeImageView
     android:layout_width="100dp"
     android:layout_height="70dp"
     android:layout_alignParentBottom="true"
     android:src="@drawable/ab"
     app:imageBorderWidth="3dp"
     app:borderColor="@color/colorAccent"
     android:layout_marginLeft="30dp"
     android:layout_marginBottom="30dp"
     app:onlyDrawBorder="false"
     app:roundRadius="5dp"
     app:shape="rectangle" />

     <com.wcy.shapeimage.shapeimageview.CustomShapeImageView
     android:layout_width="100dp"
     android:layout_height="70dp"
     android:layout_alignParentBottom="true"
     android:layout_alignParentRight="true"
     android:src="@drawable/ab"
     app:imageBorderWidth="3dp"
     app:borderColor="@color/colorAccent"
     android:layout_marginLeft="30dp"
     android:layout_marginBottom="30dp"
     app:onlyDrawBorder="false"
     app:shape="arc" />


     </RelativeLayout>
     * @param context
     */

    public CustomShapeImageView(Context context) {
        super(context);
    }

    public CustomShapeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructor(context, attrs);
    }

    public CustomShapeImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        sharedConstructor(context, attrs);
    }

    private void sharedConstructor(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomShapeImageView);
        mShape = a.getInt(R.styleable.CustomShapeImageView_shape, Shape.CIRCLE);
        borderColor = a.getColor(R.styleable.CustomShapeImageView_borderColor, Color.TRANSPARENT);
        borderWidth = a.getDimensionPixelSize(R.styleable.CustomShapeImageView_imageBorderWidth, 0);
        roundRadius = a.getDimensionPixelSize(R.styleable.CustomShapeImageView_roundRadius, 0);
        leftTopRadius = a.getDimensionPixelSize(R.styleable.CustomShapeImageView_leftTopRadius, -1);
        if (leftTopRadius == -1) {
            leftTopRadius = roundRadius;
        }
        rightTopRadius = a.getDimensionPixelSize(R.styleable.CustomShapeImageView_rightTopRadius, -1);
        if (rightTopRadius == -1) {
            rightTopRadius = roundRadius;
        }
        rightBottomRadius = a.getDimensionPixelSize(R.styleable.CustomShapeImageView_rightBottomRadius, -1);
        if (rightBottomRadius == -1) {
            rightBottomRadius = roundRadius;
        }
        leftBottomRadius = a.getDimensionPixelSize(R.styleable.CustomShapeImageView_leftBottomRadius, -1);
        if (leftBottomRadius == -1) {
            leftBottomRadius = roundRadius;
        }
        onlyDrawBorder = a.getBoolean(R.styleable.CustomShapeImageView_onlyDrawBorder, true);
        a.recycle();
    }


}

