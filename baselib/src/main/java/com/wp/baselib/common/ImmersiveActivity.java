package com.wp.baselib.common;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.wp.baselib.AndroidActivity;
import com.wp.baselib.R;
import com.wp.baselib.widget.SystemBarTintManager;


/**
 * 沉侵式菜单活动基类
 */
public class ImmersiveActivity extends AndroidActivity {

    private SystemBarTintManager tintManager;
    private int barColor = R.color.nav_bg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(barColor);
    }





    /**
     * 设置状态栏颜色
     *
     * @param color
     */
    protected void setBarColor(int color) {
        this.barColor = color;
        tintManager.setStatusBarTintResource(barColor);
    }

    protected SystemBarTintManager getTintManager() {
        return tintManager;
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 隐藏软键盘
     */
    protected void hideInputKeyword() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    /**
     * 对某个edit显示软键盘
     *
     * @param editText
     */
    protected void showInputKeyword(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0,InputMethodManager.SHOW_FORCED);
        editText.setSelection(editText.getText().toString().length());
    }

}
