package com.shownew.home.utils.dialog;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.shownew.home.R;

/**
 * @author Jason
 * @version 1.0
 * @date 2017/5/4 0004
 */

public class ShopPopwindow extends PopupWindow implements View.OnClickListener {

    private View conentView;
    private final TextView msg;
    private final TextView share;
    private final TextView home_pop;


    public ShopPopwindow(final Activity context) {
        conentView = LayoutInflater.from(context).inflate(R.layout.layout_shop_pop_home, null);
        msg = (TextView) conentView.findViewById(R.id.msg);
        share = (TextView) conentView.findViewById(R.id.share);
        home_pop = (TextView) conentView.findViewById(R.id.home_pop);
        home_pop.setOnClickListener(this);
        share.setOnClickListener(this);
        msg.setOnClickListener(this);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setContentView(conentView);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);

    }

    /**
     * 显示popWindow
     *
     * @param parent
     */
    public ShopPopwindow showPopupWindow(View parent, int x) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow  x == 0 ? parent.getLayoutParams().width / 2
            this.showAsDropDown(parent, -125 , -25);
            //            this.showAtLocation(parent, Gravity.BOTTOM,0,0);
        } else {
            this.dismiss();
        }
        return this;
    }

    public ShopPopwindow showPopupWindow(View parent) {
        return showPopupWindow(parent, 0);
    }


    private PopClickLisener mPopClickLisener;

    public void setPopClickLisener(PopClickLisener popClickLisener) {
        mPopClickLisener = popClickLisener;
    }

    @Override
    public void onClick(View v) {
        int position = 0;
        switch (v.getId()) {
            case R.id.msg:
                position = 0;
                break;
            case R.id.home_pop:
                position = 1;
                break;
            case R.id.share:
                position = 2;
                break;
        }
        if (null != mPopClickLisener) {
            dismiss();
            mPopClickLisener.clickPopItem(position);
        }
    }

    public interface PopClickLisener {
        void clickPopItem(int position);
    }
}
