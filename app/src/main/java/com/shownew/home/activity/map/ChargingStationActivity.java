package com.shownew.home.activity.map;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.shownew.home.Config;
import com.shownew.home.R;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.widget.TitleBarView;

import java.util.ArrayList;

/**
 * 充电站
 */
public class ChargingStationActivity extends BaseLocationActivity implements AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter, View.OnClickListener, AMap.OnMarkerClickListener, PoiSearch.OnPoiSearchListener {

    private MapView mMapView;
    private AMap mAMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas_station);
        mMapView = (MapView) findViewById(R.id.gas_map);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
        initViews();
    }


    private void initViews() {


        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.headbar);
        titleBarView.setTitle("充电站");
        titleBarView.setTitleTextColor(R.color.color_title);
        titleBarView.setOnLeftOnClickListener(this);
        titleBarView.setLeftIconAndText(R.drawable.back_arrow, "返回");
        titleBarView.setTitleSize(20);
    }


    /**
     * 初始化AMap对象
     */
    private void init() {

        if (mAMap == null) {
            mAMap = mMapView.getMap();
            setUpMap();
            setupLocationStyle();
            setMapLogoLocation(mAMap, 0);
            uiSetting(mAMap, true, true, true);
        }
    }

    private void setUpMap() {
        //        mAMap.moveCamera(CameraUpdateFactory.zoomTo(1));
        mAMap.setMapTextZIndex(2);
        customerInfoWindow(mAMap, this, this);
        mAMap.setOnMarkerClickListener(this);
    }


    /**
     * 设置自定义定位蓝点
     */
    private void setupLocationStyle() {
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.gps_point));
        myLocationStyle.strokeColor(Color.argb(180, 3, 145, 255));
        myLocationStyle.strokeWidth(5f);
        myLocationStyle.interval(30 * 1000);
        myLocationStyle.radiusFillColor(Color.argb(10, 0, 0, 180));
        mAMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        mAMap.getUiSettings().setMyLocationButtonEnabled(false);//设置默认定位按钮是否显示，非必需设置。
        mAMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
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
                addMarkersToMap(mAMap, R.drawable.gps_point, new LatLng(location.getLatitude(), location.getLongitude()), true, location.getAddress(), "");
                doSearchQuery("充电站", new LatLonPoint(location.getLatitude(), location.getLongitude()), 5000, this);
                mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12));
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
    public void onPoiSearched(PoiResult result, int rcode) {
        if (rcode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                ArrayList<PoiItem> poiItems = result.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                if (poiItems != null && poiItems.size() > 0) {
                    clearMapMarker(mAMap);
                    addMarkersToMap(mAMap, R.drawable.gps_point, new LatLng(location.getLatitude(), location.getLongitude()), true, location.getAddress(), "");
                    changeMapCenter(mAMap, new LatLng(location.getLatitude(), location.getLongitude()), 12);
                    for (PoiItem poiItem : poiItems) {
                        startGrowAnimation(addMarkersToMap(mAMap, R.drawable.chargelocation, new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude()), true, poiItem.getTitle(), ""));
                    }
                    mAMap.addCircle(new CircleOptions().center(new LatLng(location.getLatitude(), location.getLongitude())).radius(5000).strokeColor(Color.BLUE).fillColor(Color.argb(15, 1, 1, 1)).strokeWidth(2));
                } else {
                    ToastUtil.showToast("没有检索到相关数据");
                }

            }
        }

    }


    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
}
