package com.shownew.home.utils.dialog;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.shownew.home.R;
import com.shownew.home.ShouNewApplication;
import com.shownew.home.activity.MainActivity;
import com.shownew.home.activity.msg.AllMsgActivity;
import com.shownew.home.activity.shopcommon.ShopCollectActivity;
import com.wp.baselib.utils.Preference;

import static com.shownew.home.R.id.home_pop;
import static com.shownew.home.R.id.msg;

/**商品中的菜单popwindow
 * @author Jason
 * @version 1.0
 * @date 2017/5/4 0004
 */

public class ShopPopwindow extends PopupWindow implements View.OnClickListener {

    private Activity context;
    private ShouNewApplication shouNewApplication;

    public ShopPopwindow(final Activity context, ShouNewApplication shouNewApplication) {
        this.context = context;
        this.shouNewApplication = shouNewApplication;
        View conentView = LayoutInflater.from(context).inflate(R.layout.layout_shop_pop_home, null);
        conentView.findViewById(msg).setOnClickListener(this);
        conentView.findViewById(R.id.share).setOnClickListener(this);
        conentView.findViewById(home_pop).setOnClickListener(this);
        conentView.findViewById(R.id.collect).setOnClickListener(this);
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
            this.showAsDropDown(parent, -125, -25);
            //            this.showAtLocation(parent, Gravity.BOTTOM,0,0);
        } else {
            this.dismiss();
        }
        return this;
    }

    public ShopPopwindow showPopupWindow(View parent) {
        return showPopupWindow(parent, 0);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case msg:
                if (!Preference.getBoolean(context, Preference.IS_LOGIN, false)) {
                    shouNewApplication.jumpLoginActivity(context);
                    return;
                }
                shouNewApplication.redirect(AllMsgActivity.class);
                break;
            case home_pop:
                shouNewApplication.redirect(MainActivity.class);
                context.finish();
                break;
            case R.id.share:
                new ShareDialog(context, shouNewApplication).setCancelable(true).show();
                break;
            case R.id.collect:
                if (!Preference.getBoolean(context, Preference.IS_LOGIN, false)) {
                    shouNewApplication.jumpLoginActivity(context);
                    return;
                }
                shouNewApplication.redirect(ShopCollectActivity.class);
                break;
        }


    }


}
