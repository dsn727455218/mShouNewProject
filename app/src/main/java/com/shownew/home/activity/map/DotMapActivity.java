package com.shownew.home.activity.map;

import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
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
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.shownew.home.R;
import com.shownew.home.ShouNewApplication;
import com.wp.baselib.widget.TitleBarView;

/**
 *
 */
public class DotMapActivity extends BaseLocationActivity implements AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter, View.OnClickListener, AMap.OnMarkerClickListener, GeocodeSearch.OnGeocodeSearchListener {

    private MapView mMapView;
    private AMap mAMap;
    private int mIconType;
    private LatLng mLatLngDot;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas_station);
        mShouNewApplication = ShouNewApplication.getInstance();
        mMapView = (MapView) findViewById(R.id.gas_map);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
        initViews();
    }


    private void initViews() {


        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        titleBarView.setTitle("地图显示");
        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setOnLeftOnClickListener(this);
        titleBarView.setLeftIconAndText(R.drawable.back_arrow, "返回");
        titleBarView.setTitleSize(20);
        if (mBundle != null) {
            mIconType = mBundle.getInt("MapIcon");
            mLatLngDot = mBundle.getParcelable("location");
        }
    }


    /**
     * 初始化AMap对象
     */
    private void init() {

        if (mAMap == null) {
            mAMap = mMapView.getMap();
            setUpMap();
            setMapLogoLocation(mAMap, 0);
        }
    }

    private void setUpMap() {
        mAMap.setMapTextZIndex(2);
        customerInfoWindow(mAMap, this, this);
        mAMap.setOnMarkerClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    private boolean isFirst;

    @Override
    protected void getLocation(AMapLocation location) {
        super.getLocation(location);
        if (mMapView != null) {
            if (null != location && !isFirst) {
                isFirst = true;
                fromLantlontoAddress(new LatLonPoint(mLatLngDot.latitude, mLatLngDot.longitude), this, this);
                mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLngDot, 12));
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        return null;
    }

    @Override
    public View getInfoContents(final Marker marker) {
        View infoView = getLayoutInflater().inflate(R.layout.map_info_windows, null);
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
                finish();
                break;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //        jumpPoint(mAMap, marker);
        return false;
    }




    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {

        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null && result.getRegeocodeAddress().getFormatAddress() != null) {
                RegeocodeAddress regeocodeAddress = result.getRegeocodeAddress();
                String addressName = regeocodeAddress.getProvince() + regeocodeAddress.getCity() + regeocodeAddress.getDistrict();
                clearMapMarker(mAMap);
                if (mIconType == 0) {
                    startGrowAnimation(addMarkersToMap(mAMap, R.drawable.customerservicelocation, mLatLngDot, true, addressName, ""));
                } else if (mIconType == 1) {
                    startGrowAnimation(addMarkersToMap(mAMap, R.drawable.baolocation, mLatLngDot, true, addressName, ""));
                }
            }
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }
}
