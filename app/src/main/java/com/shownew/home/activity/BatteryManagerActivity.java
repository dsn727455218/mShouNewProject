package com.shownew.home.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shownew.home.R;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.module.DeviceAPI;
import com.shownew.home.module.entity.DeviceEntity;
import com.wp.baselib.utils.JsonUtils;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.widget.TitleBarView;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;


public class BatteryManagerActivity extends BaseActivity implements View.OnClickListener {


    private DeviceAPI deviceAPI;
    private ImageView chargeBattery;
    private ImageView batteryState;
    private ImageView yellowProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_manager);
        deviceAPI = new DeviceAPI(mShouNewApplication);
        initView();
        getDeviceNewData();
    }

    private void initView() {
        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        titleBarView.setTitle("电池管理");
        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setOnLeftOnClickListener(this);
        titleBarView.setLeftIconAndText(R.drawable.back_arrow, "返回");
        titleBarView.setTitleSize(20);

        yellowProgress = (ImageView) findViewById(R.id.yellow_progress);
        TextView yellowWendu= (TextView) findViewById(R.id.yellow_wendu);

        ImageView redProgress = (ImageView) findViewById(R.id.red_progress);
        TextView redWendu= (TextView) findViewById(R.id.red_wendu);

        chargeBattery = (ImageView) findViewById(R.id.charge_battery);
        batteryState = (ImageView) findViewById(R.id.battery_state);
        chargeBattery.setOnClickListener(this);
        RelativeLayout.LayoutParams layoutParams= (RelativeLayout.LayoutParams) yellowProgress.getLayoutParams();
        RelativeLayout.LayoutParams redLayoutParams= (RelativeLayout.LayoutParams) yellowProgress.getLayoutParams();
        Bitmap yellowBitmap=BitmapFactory.decodeResource(getResources(),R.drawable.yellowup);
        int x=yellowBitmap.getWidth();
        if (yellowBitmap!=null&&!yellowBitmap.isRecycled()){
            yellowBitmap.recycle();
        }
        layoutParams.width= (int) (x*(50/150.0));
        yellowWendu.setText(String.valueOf(50));
        yellowProgress.setLayoutParams(layoutParams);
        redLayoutParams.width=(int) (x*(40/150.0));
        redWendu.setText(String.valueOf(40));


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                finish();
                break;
            case R.id.charge_battery:
                chargeBattert(1);
                break;
        }
    }

    private void chargeBattert(final int value) {

        deviceAPI.setChargerOff(value, mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (handler == null)
                    return;
                Message message = new Message();
                message.what = value;
                if (exception == null) {
                    message.arg1 = 1;
                    handler.sendMessageDelayed(message, 4000);
                } else {
                    message.arg1 = 0;
                    handler.sendMessageDelayed(message, 4000);
                }
            }

            @Override
            protected void onLoading() {
                super.onLoading();
                createLoadingDialog();
                if (value == 1) {
                    chargeBattery.setEnabled(false);
                } else if (value == 0) {
                }
            }
        });

    }

    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeLoadingDialog();

            chargeBattery.setEnabled(true);
            switch (msg.arg1) {
                case 1:
                    if (msg.what == 1) {
                        ToastUtil.showToast("开启成功");
                        chargeBattery.setImageResource(R.drawable.end_charge);
                        batteryState.setImageResource(R.drawable.battery_animation_list);
                        Drawable drawable = batteryState.getDrawable();
                        if (drawable != null && drawable instanceof AnimationDrawable) {
                            AnimationDrawable animationDrawable = (AnimationDrawable) drawable;
                            if (!animationDrawable.isRunning()) {
                                animationDrawable.start();
                            }
                        }
                    } else if (msg.what == 0) {
                        chargeBattery.setImageResource(R.drawable.charge);
                        Drawable drawable = batteryState.getDrawable();
                        if (drawable != null && drawable instanceof AnimationDrawable) {
                            AnimationDrawable animationDrawable = (AnimationDrawable) drawable;
                            if (animationDrawable.isRunning()) {
                                animationDrawable.stop();
                            }
                        }
                        batteryState.setImageResource(R.drawable.charge_battery5);
                        ToastUtil.showToast("关闭成功");
                    }
                    break;
                case 0:
                    if (msg.what == 1) {
                        ToastUtil.showToast("开启失败");
                    } else if (msg.what == 0) {
                        ToastUtil.showToast("关闭失败");
                    }
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null)
            handler.removeCallbacks(null);
    }

    /**
     * 获取硬件最新数据接口（已测试）
     */
    private void getDeviceNewData() {
        deviceAPI.getDeviceNewData(mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected DeviceEntity parseData(String result) {
                return JsonUtils.fromJson(result, DeviceEntity.class);
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                //                mSwipeRefreshLayout.setRefreshing(false);
                if (null == exception) {
                    if (json.has("data")) {
                        try {
                            JSONObject jsonObject = json.getJSONObject("data");
                            String result = jsonObject.getString("dataInfo");

                            if (!TextUtils.isEmpty(result)) {
                                DeviceEntity mDeviceEntity = JsonUtils.fromJson(result, DeviceEntity.class);
                                if (null != mDeviceEntity) {
                                    if (mDeviceEntity.getIsCharge() == 1) {
                                        batteryState.setImageResource(R.drawable.battery_animation_list);
                                        Drawable drawable = batteryState.getDrawable();
                                        if (drawable != null && drawable instanceof AnimationDrawable) {
                                            AnimationDrawable animationDrawable = (AnimationDrawable) drawable;
                                            if (!animationDrawable.isRunning()) {
                                                animationDrawable.start();
                                            }
                                        }
                                    } else {
                                        Drawable drawable = batteryState.getDrawable();
                                        if (drawable != null && drawable instanceof AnimationDrawable) {
                                            AnimationDrawable animationDrawable = (AnimationDrawable) drawable;
                                            if (animationDrawable.isRunning()) {
                                                animationDrawable.stop();
                                            }
                                        }
                                        batteryState.setImageResource(R.drawable.charge_battery5);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });
    }
}
