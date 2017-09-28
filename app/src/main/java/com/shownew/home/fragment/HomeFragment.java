package com.shownew.home.fragment;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.google.gson.reflect.TypeToken;
import com.shownew.home.Config;
import com.shownew.home.R;
import com.shownew.home.ShouNewApplication;
import com.shownew.home.activity.BatteryManagerActivity;
import com.shownew.home.activity.MainActivity;
import com.shownew.home.activity.MyCarActivity;
import com.shownew.home.activity.common.WebActionActivity;
import com.shownew.home.activity.map.ChaseBrownActivity;
import com.shownew.home.activity.map.HistoryTrajectoryActivity;
import com.shownew.home.activity.msg.AllMsgActivity;
import com.shownew.home.activity.shop.ShopDetailActivity;
import com.shownew.home.activity.shop.SupermarketActivity;
import com.shownew.home.activity.shouniushop.ShopMallDetailActivity;
import com.shownew.home.activity.shouniushop.ShoppingMallActivity;
import com.shownew.home.module.DeviceAPI;
import com.shownew.home.module.entity.DeviceEntity;
import com.shownew.home.module.entity.HomeAdverEntity;
import com.shownew.home.utils.LocalUtils;
import com.shownew.home.utils.dialog.ShareDialog;
import com.umeng.socialize.UMShareAPI;
import com.wp.baselib.utils.JsonUtils;
import com.wp.baselib.utils.Preference;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.widget.TitleBarView;
import com.wp.baselib.widget.banner.Banner;
import com.wp.baselib.widget.banner.listener.OnBannerListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

import static com.shownew.home.Config.DRIVEROUTE;
import static com.shownew.home.Config.RIDEROUTE;


/**主页
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private View mConverView;
    private Banner mBanner;
    private DeviceAPI mDeviceAPI;
    /**
     * 电动车控制
     */
    private LinearLayout mCar_mute;


    private ImageView mLock_iv;
    private DeviceEntity mDeviceEntity;
    private TextView mCentigradeTv;
    private TextView mWeatherTv;
    private final static String HTML = "%s<br/>%s";
    private TextView mBattery;

    private ObjectAnimator mAnim;
    /**
     * 获取到的广告图片实体
     */
    private ArrayList<HomeAdverEntity> mHomeAdverEntities;


    private String mType;
    private ImageView mCarLockIv;
    private TitleBarView mTitleBarView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mConverView) {
            mConverView = inflater.inflate(R.layout.fragment_home, container, false);
            initViews();
            LocalUtils.getInstances().initLocation(context, locationListener);
        }
        return mConverView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDeviceAPI = new DeviceAPI(mShouNewApplication);
    }

    private void setTextColorContent(TextView tv, String content) {
        tv.setText(content);
    }

    private void initViews() {
        mBanner = (Banner) mConverView.findViewById(R.id.banner);
        findViewIdCenterCircle();

        findViewIdByCar();
        mConverView.findViewById(R.id.nestedScrollView).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mCentigradeTv = (TextView) mConverView.findViewById(R.id.centigrade_tv);
        mWeatherTv = (TextView) mConverView.findViewById(R.id.weather_tv);
        mBattery = (TextView) mConverView.findViewById(R.id.battery_values_tv);
        View mChepeiShoppingIv = mConverView.findViewById(R.id.chepei_shopping_iv);
        mChepeiShoppingIv.setOnClickListener(this);
        View mSelectShouniuIv = mConverView.findViewById(R.id.select_shouniu_iv);
        mSelectShouniuIv.setOnClickListener(this);

        mTitleBarView = (TitleBarView) mConverView.findViewById(R.id.headbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mTitleBarView.setPadding(0, ShouNewApplication.getStatusBarHeight(getActivity()), 0, 0);
        }
        mTitleBarView.setTitle("首牛云控");
        mTitleBarView.setTitleTextColor(R.color.color_title);
        mTitleBarView.setOnMoreClickListener(this);
        mTitleBarView.setOnLeftOnClickListener(this);
        mTitleBarView.setMoreIcon(R.drawable.share);
        mTitleBarView.setLeftIcon(R.drawable.news);
        mTitleBarView.setTitleSize(20);
        initBanner(mBanner);
    }

    /**
     * 汽车相关的控间
     */
    private void findViewIdByCar() {
        LinearLayout mCarLocation = (LinearLayout) mConverView.findViewById(R.id.track_location_car);
        ((ImageView) mCarLocation.getChildAt(0)).setImageResource(R.drawable.select_location);
        setTextColorContent(((TextView) mCarLocation.getChildAt(1)), "追踪定位");
        mCarLocation.getChildAt(0).setTag("mCarLocation");
        mCarLocation.getChildAt(0).setOnClickListener(this);
        LinearLayout
                mCarHistory = (LinearLayout) mConverView.findViewById(R.id.car_mute_car);
        ((ImageView) mCarHistory.getChildAt(0)).setImageResource(R.drawable.select_history_car);
        setTextColorContent(((TextView) mCarHistory.getChildAt(1)), "历史轨迹");
        mCarHistory.getChildAt(0).setTag("mCarHistory");
        mCarHistory.getChildAt(0).setOnClickListener(this);

        LinearLayout mCarMYCAR = (LinearLayout) mConverView.findViewById(R.id.battery_manage_car);
        ((ImageView) mCarMYCAR.getChildAt(0)).setImageResource(R.drawable.select_my_car);
        setTextColorContent(((TextView) mCarMYCAR.getChildAt(1)), "我的车辆");
        mCarMYCAR.getChildAt(0).setTag("mCarMYCAR");
        mCarMYCAR.getChildAt(0).setOnClickListener(this);


        mCarLockIv = (ImageView) mConverView.findViewById(R.id.lock_iv_car);
        mCarLockIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Preference.getBoolean(context, Preference.IS_LOGIN, false)) {
                    mShouNewApplication.jumpLoginActivity(context);
                    return;
                }
                if (mAnim.isRunning()) {
                    return;
                }
                if (null != mDeviceEntity) {
                    String lock = mDeviceEntity.getIsLock();
                    if ("0".equals(lock)) {
                        mAnim.start();
                        controlLock(2);
                    } else {
                        mAnim.start();
                        controlLock(1);
                    }
                } else {
                    mShouNewApplication.handleCarBind(context);
                }
            }
        });
    }

    /**
     * 电动车
     */
    private void findViewIdCenterCircle() {

        LinearLayout mLocationView = (LinearLayout) mConverView.findViewById(R.id.track_location);
        ((ImageView) mLocationView.getChildAt(0)).setImageResource(R.drawable.select_location);
        setTextColorContent(((TextView) mLocationView.getChildAt(1)), "追踪定位");
        mLocationView.getChildAt(0).setTag("mLocationView");
        mLocationView.getChildAt(0).setOnClickListener(this);


        LinearLayout mBattery_manage = (LinearLayout) mConverView.findViewById(R.id.battery_manage);
        ((ImageView) mBattery_manage.getChildAt(0)).setImageResource(R.drawable.select_battery_manage);
        setTextColorContent(((TextView) mBattery_manage.getChildAt(1)), "电池管理");
        mBattery_manage.getChildAt(0).setTag("battery_manage");
        mBattery_manage.getChildAt(0).setOnClickListener(this);

        LinearLayout mSercher = (LinearLayout) mConverView.findViewById(R.id.search);
        ((ImageView) mSercher.getChildAt(0)).setImageResource(R.drawable.select_sercher);
        setTextColorContent(((TextView) mSercher.getChildAt(1)), "一键寻车");
        mSercher.getChildAt(0).setTag("search");
        mSercher.getChildAt(0).setOnClickListener(this);

        mCar_mute = (LinearLayout) mConverView.findViewById(R.id.car_mute);
        ((ImageView) mCar_mute.getChildAt(0)).setImageResource(R.drawable.select_car_mute);

        mCar_mute.getChildAt(0).setTag("car_mute");
        mCar_mute.getChildAt(0).setOnClickListener(this);
        setTextColorContent(((TextView) mCar_mute.getChildAt(1)), "关闭静音");

        mLock_iv = (ImageView) mConverView.findViewById(R.id.lock_iv);

        //
        //
        //
        mAnim = ObjectAnimator//
                .ofFloat(mLock_iv, "alpha", 0.3F, 1.0F)//
                .setDuration(1000);
        mAnim.setRepeatMode(ValueAnimator.RESTART);
        mAnim.setRepeatCount(3);


        mLock_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Preference.getBoolean(context, Preference.IS_LOGIN, false)) {
                    mShouNewApplication.jumpLoginActivity(context);
                    return;
                }
                if (mAnim.isRunning()) {
                    return;
                }
                if (null != mDeviceEntity) {

                    String lock = mDeviceEntity.getIsLock();
                    if ("0".equals(lock)) {
                        mAnim.start();
                        controlLock(2);
                    } else {
                        mAnim.start();
                        controlLock(1);
                    }
                } else {
                    mShouNewApplication.handleCarBind(context);
                }
            }
        });


        mAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if ("汽车".equals(mType)) {
                    mCarLockIv.setImageResource(R.drawable.off_down);
                } else if ("电动车".equals(mType)) {
                    mLock_iv.setImageResource(R.drawable.off_down);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        mAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                try {
                    float cVal = (Float) animation.getAnimatedValue();
                    if ("汽车".equals(mType)) {
                        mCarLockIv.setAlpha(cVal);
                    } else if ("电动车".equals(mType)) {
                        mLock_iv.setAlpha(cVal);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        /**
         * 汽车 控制
         */
        LinearLayout mMy_vehicle = (LinearLayout) mConverView.findViewById(R.id.my_vehicle);
        ((ImageView) mMy_vehicle.getChildAt(0)).setImageResource(R.drawable.select_my_vehicle);
        setTextColorContent(((TextView) mMy_vehicle.getChildAt(1)), "我的车辆");
        mMy_vehicle.getChildAt(0).setTag("my_vehicle");
        mMy_vehicle.getChildAt(0).setOnClickListener(this);
    }

    private boolean isPause = false;

    @Override
    public void onStop() {
        super.onStop();
        isPause = true;
    }

    @Override
    public void onPause() {
        isPause = true;
        super.onPause();
        if (null != mBanner) {
            mBanner.stopAutoPlay();
        }
    }


    @Override
    public void onRefresh() {
        getDeviceNewData();
    }


    @Override
    public void onClick(View v) {
        Bundle mBundle = new Bundle();
        switch (v.getId()) {
            case R.id.backBtn://消息
                if (!Preference.getBoolean(context, Preference.IS_LOGIN, false)) {
                    mShouNewApplication.jumpLoginActivity(context);
                    return;
                }
                mShouNewApplication.redirect(AllMsgActivity.class);
                if (context != null && context instanceof MainActivity) {
                    MainActivity activity = (MainActivity) context;
                    activity.overridePendingTransition(R.anim.left_in, R.anim.right_out);
                }

                return;
            case R.id.title_bar_more:
                new ShareDialog(context, mShouNewApplication).setCancelable(true).show();
                return;
            case R.id.chepei_shopping_iv://车配超市
                mShouNewApplication.redirect(SupermarketActivity.class);
                return;
            case R.id.select_shouniu_iv://首牛商城
                mShouNewApplication.redirect(ShoppingMallActivity.class);
                return;
        }

        if (!Preference.getBoolean(context, Preference.IS_LOGIN, false)) {
            mShouNewApplication.jumpLoginActivity(context);
            return;
        }
        String tag = (String) v.getTag();

        if ("mLocationView".equals(tag) || "mCarLocation".equals(tag)) {//追踪定位
            if (null == mDeviceEntity) {
                mShouNewApplication.handleCarBind(context);
                return;
            }
            if ("mLocationView".equals(tag)) {
                mBundle.putInt("type", 1);
            } else if ("mCarLocation".equals(tag)) {
                mBundle.putInt("type", 2);
            }
            mShouNewApplication.redirectAndPrameter(ChaseBrownActivity.class, mBundle);
        } else if ("my_vehicle".equals(tag) || "mCarMYCAR".equals(tag)) { // 我的车辆
            mShouNewApplication.redirect(MyCarActivity.class);
        } else if ("car_mute".equals(tag)) {//车辆静音
            if (null != mDeviceEntity) {
                setShockWarn("0".equals(mDeviceEntity.getIsMute()) ? "1" : "0");
            } else {
                mShouNewApplication.handleCarBind(context);
            }
        } else if ("search".equals(v.getTag())) {//一键寻车
            if (null == mDeviceEntity) {
                mShouNewApplication.handleCarBind(context);
                return;
            }
            controlLock(3);
        } else if ("battery_manage".equals(v.getTag())) {
//            if (null == mDeviceEntity) {
//                mShouNewApplication.handleCarBind(context);
//                return;
//            }
            mShouNewApplication.redirect(BatteryManagerActivity.class);
            //车辆管理
        } else if ("mCarHistory".equals(tag)) {//汽车的历史轨迹
            if (null == mDeviceEntity) {
                mShouNewApplication.handleCarBind(context);
                return;
            }
            mShouNewApplication.redirect(HistoryTrajectoryActivity.class);
        }
    }

    /**
     * 1-一键锁定 2-一键启动 3-寻车 4-运行刷新
     */
    private void controlLock(final int values) {
        mDeviceAPI.controlLock(String.valueOf(values), mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            public void onError(Call call, Response response, Exception e) {
                mHandler.sendEmptyMessageDelayed(1, 5000);
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (null == exception) {
                    Message message = new Message();
                    message.what = 3;//车子开关锁 成功
                    message.arg1 = values;
                    mHandler.sendMessageDelayed(message, 5000);
                    //                    ToastUtil.showToast("操作成功");
                } else if ("302".equals(exception.getMessage()) || "305".equals(exception.getMessage())) {

                } else {
                    //                    ToastUtil.showToast("操作失败");
                    mHandler.sendEmptyMessageDelayed(1, 5000);
                }

            }

            @Override
            protected void onLoading() {
                if (context instanceof MainActivity) {
                    MainActivity activity = (MainActivity) context;
                    activity.createLoadingDialog();
                }
            }

            @Override
            protected void handleCarBind() {
                super.handleCarBind();
                closeLoadingDialog();
                mShouNewApplication.handleCarBind(context);
            }

            @Override
            protected void handleLogin() {
                super.handleLogin();
                closeLoadingDialog();
                mShouNewApplication.jumpLoginActivity(context);
            }
        });
    }

    /**
     * 设置振动报警接口
     *
     * @param values
     */
    private void setShockWarn(final String values) {
        mDeviceAPI.setShockWarn(values, mShouNewApplication.new ShouNewHttpCallBackLisener() {

            @Override
            public void onError(Call call, Response response, Exception e) {
                mHandler.sendEmptyMessageDelayed(1, 5000);
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (null == exception) {
                    Message message = new Message(); //车辆静音
                    message.what = 2;
                    message.obj = values;
                    mHandler.sendMessageDelayed(message, 5000);
                } else if ("302".equals(exception.getMessage()) || "305".equals(exception.getMessage())) {
                } else {
                    mHandler.sendEmptyMessageDelayed(1, 5000);
                }
            }

            @Override
            protected void onLoading() {
                if (null != context && context instanceof MainActivity) {
                    MainActivity activity = (MainActivity) context;
                    activity.createLoadingDialog();
                }
            }

            @Override
            protected void handleCarBind() {
                super.handleCarBind();
                closeLoadingDialog();
                mShouNewApplication.handleCarBind(context);

            }

            @Override
            protected void handleLogin() {
                super.handleLogin();
                closeLoadingDialog();
                mShouNewApplication.jumpLoginActivity(context);
            }
        });
    }

    private void closeLoadingDialog() {
        if (context != null && context instanceof MainActivity) {
            MainActivity activity = (MainActivity) context;
            activity.closeLoadingDialog();
        }
    }


    /**
     * 获取硬件最新数据接口（已测试）
     */
    private void getDeviceNewData() {
        mDeviceAPI.getDeviceNewData(mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected DeviceEntity parseData(String result) {
                return JsonUtils.fromJson(result, DeviceEntity.class);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {

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
                                mDeviceEntity = JsonUtils.fromJson(result, DeviceEntity.class);
                                if (null != mDeviceEntity) {
                                    mType = jsonObject.getString("type");
                                    if ("汽车".equals(mType)) {
                                        Preference.putInt(mShouNewApplication
                                        ,Config.DEVICETYPE,DRIVEROUTE);
                                        if ("1".equals(mDeviceEntity.getIsLock())) {
                                            mCarLockIv.setImageResource(R.drawable.select_unlock_start);
                                        } else {
                                            mCarLockIv.setImageResource(R.drawable.select_lock_start);
                                        }
                                        mConverView.findViewById(R.id.electric_vehicle).setVisibility(View.INVISIBLE);
                                        mConverView.findViewById(R.id.car).setVisibility(View.VISIBLE);
                                    } else if ("电动车".equals(mType)) {
                                        Preference.putInt(mShouNewApplication
                                                ,Config.DEVICETYPE,RIDEROUTE);
                                        if ("1".equals(mDeviceEntity.getIsLock())) {
                                            mLock_iv.setImageResource(R.drawable.select_unlock_start);
                                        } else {
                                            mLock_iv.setImageResource(R.drawable.select_lock_start);
                                        }
                                        mConverView.findViewById(R.id.electric_vehicle).setVisibility(View.VISIBLE);
                                        mConverView.findViewById(R.id.car).setVisibility(View.INVISIBLE);


                                        if ("0".equals(mDeviceEntity.getIsMute())) {
                                            ((ImageView) mCar_mute.getChildAt(0)).setImageResource(R.drawable.select_car_mute);
                                        } else {
                                            ((ImageView) mCar_mute.getChildAt(0)).setImageResource(R.drawable.select_volume);
                                        }


                                    }

                                    String elecStr = mDeviceEntity.getElectricity();

                                    if (TextUtils.isEmpty(elecStr))
                                        return;
                                    int elect = Integer.parseInt(mDeviceEntity.getElectricity());
                                    if (elect <= 0) {
                                        mBattery.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.battery1, 0);
                                    } else if (elect <= 20) {
                                        mBattery.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.battery2, 0);
                                    } else if (elect <= 40) {
                                        mBattery.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.battery3, 0);
                                    } else if (elect <= 60) {
                                        mBattery.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.battery4, 0);
                                    } else if (elect <= 80) {
                                        mBattery.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.battery5, 0);
                                    } else {
                                        mBattery.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.battery6, 0);
                                    }
                                    mBattery.setText(String.valueOf(elect > 100 ? 100 : (elect < 0 ? 0 : elect)));
                                    String isMute = mDeviceEntity.getIsMute();
                                    // isMute：0=静音     1=非静音
                                    if ("0".equals(isMute)) {
                                        ((ImageView) mCar_mute.getChildAt(0)).setImageResource(R.drawable.select_car_mute);
                                        setTextColorContent(((TextView) mCar_mute.getChildAt(1)), "关闭静音");
                                    } else if ("1".equals(isMute)) {
                                        ((ImageView) mCar_mute.getChildAt(0)).setImageResource(R.drawable.select_volume);
                                        setTextColorContent(((TextView) mCar_mute.getChildAt(1)), "开启静音");
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    mDeviceEntity = null;
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        isPause = false;
        if (null != mBanner) {
            mBanner.startAutoPlay();
        }
        getData();
    }

    private void getData() {
        getDeviceNewData();
        getActionAdv();
    }

    @Override
    public void isHaveMsg(int unRead) {
        if (0 == unRead) {
            mTitleBarView.getMsgCircle().setVisibility(View.GONE);
        } else if (1 == unRead) {
            mTitleBarView.getMsgCircle().setVisibility(View.VISIBLE);
        }
    }

    /**
     * 天气刷新
     *
     * @param aMapLocation
     */
    private void refreshWeather(AMapLocation aMapLocation) {
        searchliveweather(aMapLocation.getCity(), 1, mWeatherSearchListener);
    }

    /**
     * 天气查询
     *
     * @param cityname              查询天气的城市
     * @param weartherTypeLive      查询天气的类型   实时天气为1（WeatherSearchQuery.WEATHER_TYPE_LIVE）、  天气预报为2（WeatherSearchQuery.WEATHER_TYPE_FORECAST）
     * @param weatherSearchListener onWeatherLiveSearched       实时天气查询回调
     *                              onWeatherForecastSearched  天气预报查询结果回调
     */
    protected void searchliveweather(String cityname, int weartherTypeLive, WeatherSearch.OnWeatherSearchListener weatherSearchListener) {
        if (!TextUtils.isEmpty(cityname)) {
            WeatherSearchQuery mquery = new WeatherSearchQuery(cityname, weartherTypeLive);//检索参数为城市和天气类型，实时天气为1、天气预报为2
            WeatherSearch mweathersearch = new WeatherSearch(context.getApplicationContext());
            mweathersearch.setOnWeatherSearchListener(weatherSearchListener);
            mweathersearch.setQuery(mquery);
            mweathersearch.searchWeatherAsyn(); //异步搜索
        }
    }

    private AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (null != aMapLocation) {
                refreshWeather(aMapLocation);
            } else {
                System.err.println("===========定位失败");
            }

        }
    };


    /**
     * 查询天气的监听器
     */
    private WeatherSearch.OnWeatherSearchListener mWeatherSearchListener = new WeatherSearch.OnWeatherSearchListener() {
        //onWeatherLiveSearched       实时天气查询回调
        @Override
        public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int i) {
            if (null != localWeatherLiveResult) {
                LocalWeatherLive localWeatherLive = localWeatherLiveResult.getLiveResult();
                if (null != localWeatherLive) {
                    mCentigradeTv.setText(String.format("%s°", localWeatherLive.getTemperature()));
                    mWeatherTv.setText(Html.fromHtml(String.format(HTML, localWeatherLive.getCity(), localWeatherLive.getWeather())));
                    if (mDeviceEntity == null && !isPause) {
                        getData();
                    }
                }

            }

        }

        @Override
        public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(context).onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(context).release();
        if (mHandler != null) {
            mHandler.removeCallbacks(null);
        }
    }

    /**
     * 获取广告图片
     */
    private void getActionAdv() {
        mDeviceAPI.getLastAdList(0, mShouNewApplication.new ShouNewHttpCallBackLisener() {

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (null == exception) {
                    if (json.has("data")) {
                        try {
                            JSONObject jsonData = json.getJSONObject("data");
                            if (jsonData.has("ad")) {
                                String ad = jsonData.getString("ad");
                                mHomeAdverEntities = JsonUtils.fromJson(ad, new TypeToken<ArrayList<HomeAdverEntity>>() {
                                }.getType());
                                if (null != mHomeAdverEntities && mHomeAdverEntities.size() > 0) {
                                    ArrayList<String> images = new ArrayList<>();
                                    ArrayList<String> title = new ArrayList<>();
                                    for (HomeAdverEntity homeAdverEntity : mHomeAdverEntities) {
                                        images.add(String.format("%s1", homeAdverEntity.getAImg()));
                                        title.add(homeAdverEntity.getAImg());
                                    }
                                    mBanner.setImages(images);
                                    //设置banner动画效果
                                    //设置标题集合（当banner样式有显示title时）
                                    mBanner.setBannerTitles(title);
                                    mBanner.setOnBannerListener(new OnBannerListener() {
                                        @Override
                                        public void OnBannerClick(int position) {
                                            HomeAdverEntity homeAdverEntity = mHomeAdverEntities.get(position);
                                            if (mHomeAdverEntities != null) {
                                                if (Config.ACTION_WEB.equals(homeAdverEntity.getaPid()) && Config.ACTION_WEB.equals(homeAdverEntity.getaMpid())) {
                                                    if (Config.ACTION_ADV.equals(homeAdverEntity.getAId())) {
                                                        if (!TextUtils.isEmpty(homeAdverEntity.getaUrl())) {
                                                            mShouNewApplication.redirectActionWeb("参与活动", homeAdverEntity.getaUrl(), WebActionActivity.class, null);
                                                        }
                                                        return;
                                                    }
                                                    if (!TextUtils.isEmpty(homeAdverEntity.getaUrl())) {
                                                        mShouNewApplication.redirectWeb("", homeAdverEntity.getaUrl());
                                                    }
                                                } else {
                                                    Bundle bundle = new Bundle();
                                                    if ("0".equals(homeAdverEntity.getaMpid())) {
                                                        bundle.putString("shopId", homeAdverEntity.getaPid());
                                                        mShouNewApplication.redirectAndPrameter(ShopDetailActivity.class, bundle);
                                                    } else if ("0".equals(homeAdverEntity.getaPid())) {
                                                        bundle.putString("shopId", homeAdverEntity.getaMpid());
                                                        mShouNewApplication.redirectAndPrameter(ShopMallDetailActivity.class, bundle);
                                                    }
                                                }
                                            }
                                        }
                                    });
                                    mBanner.start();
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


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (context != null) {
                MainActivity activity = (MainActivity) context;
                activity.closeLoadingDialog();

                if (msg.what == 0) {
                    ToastUtil.showToast("发送成功");
                } else if (msg.what == 1) {
                    ToastUtil.showToast("发送失败");
                } else if (msg.what == 3) {
                    ToastUtil.showToast("发送成功");
                    switch (msg.arg1) {
                        case 1:
                            if ("汽车".equals(mType)) {
                                mCarLockIv.setImageResource(R.drawable.select_lock_start);
                            } else if ("电动车".equals(mType)) {
                                mLock_iv.setImageResource(R.drawable.select_lock_start);
                            }
                            mDeviceEntity.setIsLock("0");
                            break;
                        case 2:
                            mDeviceEntity.setIsLock("1");
                            if ("汽车".equals(mType)) {
                                mCarLockIv.setImageResource(R.drawable.select_unlock_start);
                            } else if ("电动车".equals(mType)) {
                                mLock_iv.setImageResource(R.drawable.select_unlock_start);
                            }
                            break;
                    }
                } else if (msg.what == 2) {
                    ToastUtil.showToast("发送成功");
                    String values = (String) msg.obj;
                    if ("0".equals(values)) {
                        ((ImageView) mCar_mute.getChildAt(0)).setImageResource(R.drawable.select_car_mute);
                        setTextColorContent(((TextView) mCar_mute.getChildAt(1)), "关闭静音");
                    } else if ("1".equals(values)) {
                        ((ImageView) mCar_mute.getChildAt(0)).setImageResource(R.drawable.select_volume);
                        setTextColorContent(((TextView) mCar_mute.getChildAt(1)), "开启静音");
                    }
                    mDeviceEntity.setIsMute(values);
                }
            }

        }
    };

}
