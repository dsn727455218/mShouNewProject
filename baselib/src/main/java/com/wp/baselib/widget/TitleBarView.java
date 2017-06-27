package com.wp.baselib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wp.baselib.R;


/**
 * TitleBar组件类
 */
public class TitleBarView extends FrameLayout {

    private RelativeLayout backimg;
    private TextView layout_right_tv;
    private Button backbtn;
    private TextView titletv;
    private LinearLayout rightBt;
    private TextView more;
    private View mMsgCircle;

    public TitleBarView(Context context) {
        super(context);
        initViews(context, null);
    }

    public TitleBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context, attrs);
    }

    public TitleBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews(context, attrs);
    }

    public void showRightBt() {
        rightBt.setVisibility(VISIBLE);
        more.setVisibility(View.GONE);
        layout_right_tv.setVisibility(GONE);
    }

    public void setRightBtClick(OnClickListener listener) {
        showRightBt();
        rightBt.setOnClickListener(listener);
    }

    public void initViews(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.layout_title, this);
        if (!isInEditMode()) {
            backimg = (RelativeLayout) findViewById(R.id.layout_title_bg);
            backbtn = (Button) findViewById(R.id.backBtn);
            mMsgCircle = findViewById(R.id.msg_circle);
            rightBt = (LinearLayout) findViewById(R.id.rightBt);
            layout_right_tv = (TextView) findViewById(R.id.commitFeed);
            titletv = (TextView) findViewById(R.id.topTitle);
            more = (TextView) findViewById(R.id.title_bar_more);
            if (attrs != null) {
                TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MyTitleBar);
                if (!TextUtils.isEmpty(ta.getString(R.styleable.MyTitleBar_titleTv))) {
                    titletv.setText(ta.getString(R.styleable.MyTitleBar_titleTv));
                }
                titletv.setTextColor(ta.getColor(R.styleable.MyTitleBar_titleColor, getResources().getColor(android.R.color.white)));
                titletv.setTextSize(ta.getFloat(R.styleable.MyTitleBar_titleSize, 20));
                ta.recycle();
            }
        }
    }

    public View getMsgCircle() {
        return mMsgCircle;
    }

    public void setMoreIcon(int rid) {
        more.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(rid), null);
    }

    public void setOnMoreClickListener(OnClickListener listener) {
        layout_right_tv.setVisibility(View.GONE);
        rightBt.setVisibility(View.GONE);
        more.setVisibility(View.VISIBLE);
        more.setOnClickListener(listener);
    }

    public TextView getMoreBtn() {
        layout_right_tv.setVisibility(View.GONE);
        rightBt.setVisibility(View.GONE);
        more.setVisibility(View.VISIBLE);
        return more;
    }


    public void setRightText(String str) {
        if (!TextUtils.isEmpty(str)) {
            layout_right_tv.setVisibility(VISIBLE);
            layout_right_tv.setText(str);
        }
    }

    public void setOnRightTvClick(OnClickListener listener) {
        layout_right_tv.setOnClickListener(listener);
    }

    public void setOnLeftOnClickListener(OnClickListener cilck) {
        backimg.setVisibility(VISIBLE);
        backbtn.setOnClickListener(cilck);

    }

    public TextView getRightView() {
        return layout_right_tv;
    }

    public RelativeLayout getTitleBgTv() {
        return backimg;
    }

    public TextView getTitleView() {
        return titletv;
    }

    public void setLeftVisiable() {
        backbtn.setVisibility(View.VISIBLE);
    }

    public void setLeftUnVisiable() {
        backbtn.setVisibility(View.GONE);
    }

    public void setRightTvVisiable() {
        layout_right_tv.setVisibility(View.VISIBLE);
    }

    public void setRightTvUnVisiable() {
        layout_right_tv.setVisibility(View.GONE);
    }

    public void setTitle(int resid) {
        titletv.setText(resid);
    }

    public void setRightText(int resid) {
        layout_right_tv.setVisibility(VISIBLE);
        layout_right_tv.setText(resid);
    }

    public void setTitleSize(int fsize) {
        titletv.setTextSize(fsize);
    }

    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            titletv.setText(title);
        } else {
            titletv.setText(" ");
        }
    }

    public void setLeftIconAndText(int sources, String text) {
        backbtn.setText(text);
        backbtn.setCompoundDrawablesWithIntrinsicBounds(sources, 0, 0, 0);
    }

    public void setLeftIcon(int sources) {
        backbtn.setCompoundDrawablesWithIntrinsicBounds(sources, 0, 0, 0);
    }
    public void setRigthTextColor(int color) {
        layout_right_tv.setTextColor(getResources().getColor(color));
    }
    public void setRigthTextSize(int size) {
        layout_right_tv.setTextSize(size);
    }
    public  void setTitleTextColor(int color){
        titletv.setTextColor(getResources().getColor(color));
    }
}
