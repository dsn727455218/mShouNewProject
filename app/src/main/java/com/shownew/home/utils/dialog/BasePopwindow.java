package com.shownew.home.utils.dialog;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.shownew.home.R;

/**
 * @author Jason
 * @version 1.0
 * @date 2017/5/4 0004
 */

public class BasePopwindow extends PopupWindow {



    public BasePopwindow(final Activity context, final String... values) {
        View   conentView = LayoutInflater.from(context).inflate(R.layout.layout_shop_pop, null);
        LinearLayout     mLinearLayout = (LinearLayout) conentView.findViewById(R.id.pop_parent);

        int size = values.length;
        for (int i = 0; i < size; i++) {
            final int tem = i;
            TextView tv = new TextView(context);
            tv.setText(values[i]);
            tv.setTextSize(16);
            tv.setPadding(20,10,10,10);
            tv.setTextColor(Color.WHITE);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mPopClickLisener) {
                       dismiss();
                        mPopClickLisener.clickPopItem(values[tem]);
                    }
                }
            });
            mLinearLayout.addView(tv);
        }


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
    public BasePopwindow showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent, parent.getLayoutParams().width / 2, -25);
        } else {
            this.dismiss();
        }
        return this;
    }

    private PopClickLisener mPopClickLisener;

    public void setPopClickLisener(PopClickLisener popClickLisener) {
        mPopClickLisener = popClickLisener;
    }

    public interface PopClickLisener {
        void clickPopItem(String mark);
    }
}
