package com.shownew.home.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.shownew.home.R;
import com.wp.baselib.utils.StringUtil;


/**
 * 输入支付密码
 *
 * @author lining
 */
public class PopEnterPassword extends PopupWindow {

    public PopEnterPassword(final Activity context, String money) {
        this(context, money, "tips");
    }

    public PopEnterPassword(final Activity context, String money, String tips) {

        super(context);

        Activity context1 = context;

        LayoutInflater inflater = (LayoutInflater) context1.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View menuView = inflater.inflate(R.layout.pop_enter_password, null);

        PasswordView pwdView = (PasswordView) menuView.findViewById(R.id.pwd_view);
        if (TextUtils.isEmpty(money)) {
            pwdView.getMoneyCharge().setText("");
        } else {
            pwdView.getMoneyCharge().setText(String.format("¥%s", StringUtil.formatMoney(money)));
        }
        if (TextUtils.isEmpty(tips)) {
            pwdView.getMoney_tips().setText("");
        }
        //添加密码输入完成的响应
        pwdView.setOnFinishInput(new OnPasswordInputFinish() {
            @Override
            public void inputFinish(String password) {
                dismiss();
                if (mInputLisener != null) {
                    mInputLisener.result(password);
                }
            }
        });

        // 监听X关闭按钮
        pwdView.getImgCancel().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // 监听键盘上方的返回
        pwdView.getVirtualKeyboardView().getLayoutBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // 设置SelectPicPopupWindow的View
        this.setContentView(menuView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.pop_add_ainm);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x66000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

    }

    private InputLisener mInputLisener;

    public PopEnterPassword setInputLisener(InputLisener inputLisener) {
        mInputLisener = inputLisener;
        return this;
    }

    public interface InputLisener {
        void result(String pass);
    }
}
