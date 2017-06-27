package com.shownew.home.utils.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shownew.home.R;
import com.wp.baselib.widget.WheelView;
import com.wp.zxing.view.ViewfinderView;

import java.util.ArrayList;

/**
 * @author Jason
 * @version 1.0
 * @date 2017/4/18 0018
 */

public class SelectBatteryDialog extends BaseDialog implements View.OnClickListener {
    private String selectItem;
    private ArrayList<String> data;

    /**
     * //构造方法 来实现 最基本的对话框
     *
     * @param context
     */
    public SelectBatteryDialog(Context context, String title, ArrayList<String> data) {
        super(context, Gravity.BOTTOM);

        View view = LayoutInflater.from(context).inflate(R.layout.layout_select_battery_dialog, null);
        WheelView wheelView = (WheelView) view.findViewById(R.id.wheel_select_battery);
        wheelView.setItems(data, 1);
        wheelView.setTextColor(context.getResources().getColor(R.color.color_common_press));
        TextView titleTv = (TextView) view.findViewById(R.id.title);
        titleTv.setText(title);
        WheelView.LineConfig config = new WheelView.LineConfig();
        config.setColor(context.getResources().getColor(R.color.bgcolor));//线颜色
        view.findViewById(R.id.sure).setOnClickListener(this);
        view.findViewById(R.id.cancel).setOnClickListener(this);
        config.setAlpha(50);//线透明度
        config.setRatio((float) (1.0 / 10.0));//线比率
        config.setThick(ViewfinderView.dip2px(context, 1));//线粗
        wheelView.setLineConfig(config);
        wheelView.setOnWheelListener(new WheelView.OnWheelListener() {
            @Override
            public void onSelected(boolean isUserScroll, int index, String item) {
                selectItem = item;
            }
        });
        int width = display.getWidth();
        //设置dialog的宽高为屏幕的宽高
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        getDialog().setContentView(view, layoutParams);
    }

    @Override
    protected int getDialogStyleId() {
        return R.style.dialog_ios_style;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                break;
            case R.id.sure:
                if (!TextUtils.isEmpty(selectItem) && mSelectBatteryLisener != null) {
                    mSelectBatteryLisener.sure(selectItem);
                }
                break;
        }
        dismiss();
    }


    private SelectBatteryLisener mSelectBatteryLisener;

    public SelectBatteryDialog setSelectBatteryLisener(SelectBatteryLisener selectBatteryLisener) {
        mSelectBatteryLisener = selectBatteryLisener;
        return this;
    }

    public interface SelectBatteryLisener {
        void sure(String values);
    }
}
