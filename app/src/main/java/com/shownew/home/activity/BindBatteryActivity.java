package com.shownew.home.activity;

import android.os.Bundle;

import com.shownew.home.R;
import com.shownew.home.activity.common.BaseActivity;
import com.wp.baselib.widget.WheelView;
import com.wp.zxing.view.ViewfinderView;

public class BindBatteryActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_battery);
        initViews();
    }

    private void initViews() {
        WheelView wheelView = (WheelView) findViewById(R.id.wheelview_single);
        wheelView.setItems(new String[]{"65v", "55v", "35v", "45v", "65v", "55v", "35v", "65v", "55v", "35v"}, 2);
        wheelView.setTextColor(getResources().getColor(R.color.color_common_press));
        WheelView.LineConfig config = new WheelView.LineConfig();
        config.setColor(getResources().getColor(R.color.bgcolor));//线颜色
        config.setAlpha(50);//线透明度
        config.setRatio((float) (1.0 / 10.0));//线比率
        config.setThick(ViewfinderView.dip2px(this, 1));//线粗
        wheelView.setLineConfig(config);
        wheelView.setOnWheelListener(new WheelView.OnWheelListener() {
            @Override
            public void onSelected(boolean isUserScroll, int index, String item) {

            }
        });
    }
}
