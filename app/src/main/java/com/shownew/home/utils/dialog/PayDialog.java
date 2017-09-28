package com.shownew.home.utils.dialog;

import android.content.Context;
import android.support.annotation.IdRes;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.shownew.home.R;

/**
 * @descirpte     支付方式选择dialog
 * @version 1.0
 * @date 2017/5/3 0003
 */

public class PayDialog extends BaseDialog {
    private RadioButton rbSn;

    /**
     * //构造方法 来实现 最基本的对话框
     *
     * @param context
     * @ param isVisiableWalt  支付方式限制
     */
    public PayDialog(final Context context, boolean isVisiableWalt) {
        super(context, Gravity.BOTTOM);
        View view = mInflater.inflate(R.layout.pay_ways_dialog, null);
        initRechargeWays(view);
        if (isVisiableWalt) {
            rbSn.setVisibility(View.GONE);
        }
        int width = display.getWidth();
        //设置dialog的宽高为屏幕的宽高
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        getDialog().setContentView(view, layoutParams);
    }

    @Override
    protected int getDialogStyleId() {
        return 0;
    }


    int flag = 0;

    private void initRechargeWays(View view) {
        RadioGroup rgPay = (RadioGroup) view.findViewById(R.id.rg_pay);
        final RadioButton rbWx = (RadioButton) view.findViewById(R.id.rg_wx);
        final RadioButton rbZf = (RadioButton) view.findViewById(R.id.rg_zf);
        rbSn = (RadioButton) view.findViewById(R.id.rg_sn);
        rbZf.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zhifubaopay, 0, 0, 0);
        rbSn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.shouniupay, 0, 0, 0);
        rbWx.setCompoundDrawablesWithIntrinsicBounds(R.drawable.weixinpay, 0, 0, 0);

        rbZf.setTextColor(0XFFFFFFFF);
        rbSn.setTextColor(0XFFFFFFFF);
        rbWx.setTextColor(0XFFFFFFFF);
        rgPay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rg_zf:
                        flag = 1;
                        break;
                    case R.id.rg_sn:
                        flag = 2;
                        break;
                    case R.id.rg_wx:
                        flag = 0;
                        break;
                }
                if (selectPayWayLisenter != null) {
                    dismiss();
                    selectPayWayLisenter.callback(flag);
                }
            }
        });
    }

    private SelectPayWayLisenter selectPayWayLisenter;

    public void setSelectPayWayLisenter(SelectPayWayLisenter selectPayWayLisenter) {
        this.selectPayWayLisenter = selectPayWayLisenter;
    }

    public interface SelectPayWayLisenter {
        void callback(int payWays);
    }

}
