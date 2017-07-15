package com.shownew.home.activity.map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.shownew.home.R;
import com.shownew.home.ShouNewApplication;
import com.shownew.home.module.DeviceAPI;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.widget.TitleBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Response;


/**
 * @author Jason
 * @version 1.0
 * @date 2017/4/15 0015
 */

public class ChaseBrownActivity extends BaseLocationActivity implements AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter, View.OnClickListener, AMap.OnMarkerClickListener {
    private MapView mMapView;
    private AMap mAMap;
    private Marker mPersonMarker;
    private ShouNewApplication mShouNewApplication;
    private DeviceAPI mDeviceAPI;
    /**
     * 经   度
     */
    private double mDeviceLongitude;
    /**
     * 纬    度
     */
    private double mDeviceLatitude;
    private View mHistoryPoint;
    private ImageView mCarLocation;
    private Marker carMarker;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chasse_brown);
        mShouNewApplication = ShouNewApplication.getInstance();
        mDeviceAPI = new DeviceAPI(mShouNewApplication);
        mMapView = (MapView) findViewById(R.id.chase_map);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
        initViews();
        if (mBundle != null) {
            int mType = mBundle.getInt("type");
            if (1 == mType) {//电动车
                mCarLocation.setImageResource(R.drawable.cheweizhi);
            } else if (2 == mType) {//汽车
                mHistoryPoint.setVisibility(View.GONE);
                mCarLocation.setImageResource(R.drawable.qicheweizhi);
            }
        }
        createLoadingDialog();
        getGPS();
    }

    private void getGPS() {
        mDeviceAPI.getNewGps(mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected Object parseData(String result) {
                return null;
            }

            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {

                if (null == exception) {
                    if (json.has("data")) {
                        try {
                            JSONObject result = json.getJSONObject("data");
                            if (result.has("gps")) {
                                String gps = result.getString("gps");
                                if (!TextUtils.isEmpty(gps)) {
                                    //                                    经    度    : 104.008349
                                    //                                    04-20 18:14:56.060 19957-19957/? I/System.out: 纬    度     getLongitude   : 30.465408
                                    //                                    sb.append("经    度    : " + location.getLongitude() + "\n");
                                    //                                    sb.append("纬    度    : " + location.getLatitude() + "\n");
                                    String[] local = gps.split(",");
                                    mDeviceLatitude = Double.parseDouble(local[0]);
                                    mDeviceLongitude = Double.parseDouble(local[1]);
                                    //LatLonPoint(39.90865, 116.39751);
                                    fromLantlontoAddress(new LatLonPoint(mDeviceLatitude, mDeviceLongitude), ChaseBrownActivity.this, mOnGeocodeSearchListener);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (getNewsCount <= 4 && getNewsCount > 0) {
                            if (getNewsCount == 4) {
                                mHandler.sendEmptyMessageDelayed(0, 5000);
                            }
                            getNewsCount--;
                            mHandler.sendEmptyMessageDelayed(2, 3000);
                        }
                    }
                } else {

                    mHandler.sendEmptyMessageDelayed(1, 3000);
                }
            }

            @Override
            protected void onLoading() {

            }
        });
    }

    private int getNewsCount = 4;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    ToastUtil.showToast("获取成功");
                    closeLoadingDialog();
                    break;
                case 1:
                    ToastUtil.showToast("获取失败,请重新获取");
                    closeLoadingDialog();
                    break;
                case 2:
                    if (isFinish)
                        break;
                    getGPS();
                    break;
            }
        }
    };

    private GeocodeSearch.OnGeocodeSearchListener mOnGeocodeSearchListener = new GeocodeSearch.OnGeocodeSearchListener() {
        //        逆地理编码回调
        @Override
        public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int rCode) {
            if (rCode == AMapException.CODE_AMAP_SUCCESS) {
                if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null && regeocodeResult.getRegeocodeAddress().getFormatAddress() != null) {

                    removeMarker(carMarker);
                    carMarker = addMarkersToMap(mAMap, R.drawable.vehiclelocation, new LatLng(mDeviceLatitude, mDeviceLongitude), true, regeocodeResult.getRegeocodeAddress().getFormatAddress(), "");
                    carMarker.setObject("carMarker");
                    mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mDeviceLatitude, mDeviceLongitude), 16));
                    setMarkPostion(carMarker, new LatLng(mDeviceLatitude, mDeviceLongitude));
                    carMarker.setZIndex(3);
                    mPersonMarker.setZIndex(4);
                }
            }
        }

        // 地理编码查询回调
        @Override
        public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

        }
    };

    private void removeMarker(Marker mark){
        List<Marker> mapScreenMarkers = mAMap.getMapScreenMarkers();
        int length = mapScreenMarkers.size();
        for (int i = 0; i < length; i++) {
            Marker marker = mapScreenMarkers.get(i);
            if (marker.equals(mark)) {
                marker.remove();//移除当前Marker
            }
        }
    }
    private void initViews() {
        findViewById(R.id.my_localtion_btn).setOnClickListener(this);
        mCarLocation = (ImageView) findViewById(R.id.car_localtion_btn);
        mCarLocation.setOnClickListener(this);
        findViewById(R.id.person_daohang_btn).setOnClickListener(this);
        mHistoryPoint = findViewById(R.id.history_stop_car_point_btn);
        mHistoryPoint.setOnClickListener(this);

        TitleBarView mTitleBarView = (TitleBarView) findViewById(R.id.headbar);
        mTitleBarView.setTitle("定位追踪");
        mTitleBarView.setTitleTextColor(R.color.color_title);
        mTitleBarView.setOnLeftOnClickListener(this);
        mTitleBarView.setLeftIconAndText(R.drawable.back_arrow, "返回");
        mTitleBarView.setTitleSize(20);
    }

    private boolean isFirst;

    @Override
    protected void getLocation(AMapLocation location) {
        super.getLocation(location);

        if (location != null && !isFirst) {
            changeMapCenter(mAMap, new LatLng(location.getLatitude(), location.getLongitude()), 16);
            isFirst = true;
            removeMarker(mPersonMarker);
            mPersonMarker = addMarkersToMap(mAMap, R.drawable.peoplelocation, new LatLng(location.getLatitude(), location.getLongitude()), true, location.getAddress(), location.getCity());
            mPersonMarker.setZIndex(4);
        }
    }


    /**
     * 初始化AMap对象
     */
    private void init() {

        if (mAMap == null) {
            mAMap = mMapView.getMap();
            setUpMap();
            //            setupLocationStyle();
            setMapLogoLocation(mAMap, 0);
            uiSetting(mAMap, false, true, true);
        }
    }

    private void setUpMap() {
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(1));
        mAMap.setMapTextZIndex(2);
        customerInfoWindow(mAMap, this, this);
        mAMap.setOnMarkerClickListener(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isFinish = false;
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public View getInfoWindow(final Marker marker) {
        //        View infoView = getLayoutInflater().inflate(R.layout.map_info_windows, null);
        //        ImageView infowindowsImg = (ImageView) infoView.findViewById(R.id.infowindows_img);
        //        TextView infowindowsTv = (TextView) infoView.findViewById(R.id.infowindows_tv);
        //        infoView.findViewById(R.id.infowindows_btn).setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                ToastUtil.showToast("title:" + marker.getTitle() + "11snippet:" + marker.getSnippet());
        //            }
        //        });
        //        infowindowsTv.setText(marker.getTitle());
        return null;
    }

    @Override
    public View getInfoContents(final Marker marker) {
        View infoView = getLayoutInflater().inflate(R.layout.map_info_windows, null);
        if (mPersonMarker.equals(marker)) {
            infoView.findViewById(R.id.infowindows_btn).setVisibility(View.GONE);
        }
        TextView infowindowsTv = (TextView) infoView.findViewById(R.id.infowindows_tv);
        infoView.findViewById(R.id.infowindows_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (null != location && marker != null) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("startLocaltion", new NaviLatLng(location.getLatitude(), location.getLongitude()));
                    bundle.putParcelable("endLocaltion", new NaviLatLng(marker.getPosition().latitude, marker.getPosition().longitude));
                    mainApplication.redirectAndPrameter(MapNavigationActivity.class, bundle);
                }
            }
        });
        infowindowsTv.setText(marker.getTitle());
        return infoView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                isFinish = true;
                finish();
                break;
            case R.id.my_localtion_btn:
                if (null != location) {
                    //                    clearMapMarker(mAMap);
                    isFirst = false;
                    changeMapCenter(mAMap, new LatLng(location.getLatitude(), location.getLongitude()));
                }
                break;
            case R.id.car_localtion_btn:
                createLoadingDialog();
                getNewsCount = 4;
                getGPS();
                if (null != location && 0 != mDeviceLatitude && 0 != mDeviceLongitude) {
                    //                    clearMapMarker(mAMap);mDeviceLatitude
                    changeMapCenter(mAMap, new LatLng(mDeviceLatitude, mDeviceLongitude), 16);
                }
                break;
            case R.id.person_daohang_btn:
                if (0 == mDeviceLatitude && 0 == mDeviceLongitude) {
                    ToastUtil.showToast("没有获取到车位置");
                    createLoadingDialog();
                    getGPS();
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putParcelable("startLocaltion", new NaviLatLng(location.getLatitude(), location.getLongitude()));
                bundle.putParcelable("endLocaltion", new NaviLatLng(mDeviceLatitude, mDeviceLongitude));
                mainApplication.redirectAndPrameter(MapNavigationActivity.class, bundle);
                break;
            case R.id.history_stop_car_point_btn:
                mainApplication.redirect(HistoryTrajectoryActivity.class);
                break;
        }

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //        jumpPoint(mAMap, marker);
        return false;
    }

    private boolean isFinish;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isFinish = true;
        finish();
    }
}
