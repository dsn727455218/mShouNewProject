package com.shownew.home.activity.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.Projection;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.NavigateArrowOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.shownew.home.activity.common.BaseActivity;
import com.shownew.home.utils.LocalUtils;
import com.wp.baselib.utils.TimeUtil;

import java.util.List;


/**
 * 地图使用的相关基类
 *
 * @author Jason
 * @version 1.0
 * @date 2017/4/5 0005
 */

public class BaseLocationActivity extends BaseActivity {
    protected AMapLocation location;
    protected String cityCode = "010";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permission != PackageManager.PERMISSION_GRANTED) {//如果没有权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        LocalUtils.getInstances().initLocation(getApplicationContext(), locationListener);
    }

    /**
     * 定位成功的回调
     */
    private AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (null != aMapLocation) {
                location = aMapLocation;
                getLocation(aMapLocation);
                System.out.println("===========" + getLocationStr(aMapLocation));
            } else {
                System.err.println("===========定位失败");
            }

        }
    };

    /**
     * 根据定位结果返回定位信息的字符串
     *
     * @param location
     * @return
     */
    private synchronized static String getLocationStr(AMapLocation location) {
        if (null == location) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
        if (location.getErrorCode() == 0) {
            sb.append("定位成功" + "\n");
            sb.append("定位类型: " + location.getLocationType() + "\n");
            sb.append("经    度    : " + location.getLongitude() + "\n");
            sb.append("纬    度    : " + location.getLatitude() + "\n");
            sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
            sb.append("提供者    : " + location.getProvider() + "\n");

            sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
            sb.append("角    度    : " + location.getBearing() + "\n");
            // 获取当前提供定位服务的卫星个数
            sb.append("星    数    : " + location.getSatellites() + "\n");
            sb.append("国    家    : " + location.getCountry() + "\n");
            sb.append("省            : " + location.getProvince() + "\n");
            sb.append("市            : " + location.getCity() + "\n");
            sb.append("城市编码 : " + location.getCityCode() + "\n");
            sb.append("区            : " + location.getDistrict() + "\n");
            sb.append("区域 码   : " + location.getAdCode() + "\n");
            sb.append("地    址    : " + location.getAddress() + "\n");
            sb.append("兴趣点    : " + location.getPoiName() + "\n");
            //定位完成的时间
            sb.append("定位时间: " + TimeUtil.getTime2String(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");
        } else {
            //定位失败
            sb.append("定位失败" + "\n");
            sb.append("错误码:" + location.getErrorCode() + "\n");
            sb.append("错误信息:" + location.getErrorInfo() + "\n");
            sb.append("错误描述:" + location.getLocationDetail() + "\n");
        }
        //定位之后的回调时间
        sb.append("回调时间: " + TimeUtil.getTime2String(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");
        return sb.toString();
    }

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(6 * 1000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    /**
     * 获取定位后的数据
     */
    protected void getLocation(AMapLocation location) {

    }

    /**
     * 改变地图中心点
     */
    protected void changeMapCenter(AMap aMap, LatLng latLng) {
        changeMapCenter(aMap,latLng,15);
    }



    /**
     * 改变地图中心点
     */
    protected void changeMapCenter(AMap aMap, LatLng latLng,float zoom) {
        if (null != aMap) {
            //            aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 18, 30, 30)));
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }
    }


    /**
     * 设置Marker 在地图上的位置
     *
     * @param marker
     * @param latLng
     */
    protected void setMarkPostion(Marker marker, LatLng latLng) {
        marker.setPosition(latLng);
    }

    /**
     * 设置地图logo位置
     *
     * @param aMap
     * @param location 左下  0   ，底部居中  1  ` ，   右下    2         右中心3
     */

    protected void setMapLogoLocation(AMap aMap, int location) {
        if (null != aMap) {
            UiSettings mUiSettings = aMap.getUiSettings();
            switch (location) {
                case 0:
                    mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);// 设置地图logo显示在左下方
                    break;
                case 1:
                    mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_CENTER);// 设置地图logo显示在底部居中
                    break;
                case 2:
                    mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);// 设置地图logo显示在右下方
                    break;
                case 3:
                    mUiSettings.setLogoPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);// 设置地图logo显示在右中心
                    break;
            }
        }

    }

    /**
     * 给地图上添加marker
     *
     * @param aMap
     * @param latlng 经纬度
     */
    protected Marker addMarkersToMap(AMap aMap, int resource, LatLng latlng, boolean isShowInfoWindowns, String title, String message) {
        MarkerOptions markerOption;
        if (resource == 0) {
            markerOption = new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).position(latlng).draggable(false);
        } else {

            markerOption = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(resource)).position(latlng).draggable(false); //draggable  设置 marker是否可以拖拽
        }
        if (isShowInfoWindowns) {
            //draggable  设置 marker是否可以拖拽
            markerOption.anchor(0.5f, 0.5f).position(latlng).title(title).snippet(message).draggable(false).period(10);
        }
        return aMap.addMarker(markerOption);

    }

    /**
     * 给地图上添加marker
     *
     * @param aMap
     * @param latlng 经纬度
     */
    protected Marker addMarkersToMap(AMap aMap, int resource, LatLng latlng) {
        return addMarkersToMap(aMap, resource, latlng, false, "", "");
    }

    /**
     * 清空地图上所有已经标注的marker
     */
    protected void clearMapMarker(AMap aMap) {
        if (null != aMap) {
            aMap.clear();
        }
    }

    /**
     * marker点击时跳动一下
     */
    public void jumpPoint(AMap aMap, final Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = aMap.getProjection();
        final LatLng markerLatlng = marker.getPosition();
        Point markerPoint = proj.toScreenLocation(markerLatlng);
        markerPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(markerPoint);
        final long duration = 1500;
        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * markerLatlng.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * markerLatlng.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    /**
     * 在map中绘制多彩线
     *
     * @param aMap
     * @param colorList          渐变颜色
     * @param lineWidth          线宽
     * @param isAddNavigateArrow 是否添加带有导航箭头的线
     * @param latLngs            绘制线的点
     */
    protected void addPolylinesWithGradientColors(AMap aMap, List<Integer> colorList, int lineWidth, boolean isAddNavigateArrow, LatLng... latLngs) {
        //用一个数组来存放颜色，渐变色，四个点需要设置四个颜色
        if (latLngs == null)
            return;
        PolylineOptions options = new PolylineOptions();
        options.width(lineWidth);//设置宽度
        if (isAddNavigateArrow) {
            aMap.addNavigateArrow(new NavigateArrowOptions().add(latLngs).width(lineWidth + 5));
        }
        //加入对应的颜色,使用colorValues 即表示使用多颜色，使用color表示使用单色线
        options.colorValues(colorList);//如果最后一个颜色不添加，那么最后一段将显示上一段的颜色
        //加上这个属性，表示使用渐变线
        options.useGradient(true);

        aMap.addPolyline(options);
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
        WeatherSearchQuery mquery = new WeatherSearchQuery(cityname, weartherTypeLive);//检索参数为城市和天气类型，实时天气为1、天气预报为2
        WeatherSearch mweathersearch = new WeatherSearch(this);
        mweathersearch.setOnWeatherSearchListener(weatherSearchListener);
        mweathersearch.setQuery(mquery);
        mweathersearch.searchWeatherAsyn(); //异步搜索
    }

    /**
     * 开始进行poi搜索
     *
     * @param sercherConent       搜索的关键字
     * @param latLonPoint         搜索的中心点
     * @param boundRegion         搜索的范围
     * @param onPoiSearchListener 搜索成功后的回掉
     */
    protected void doSearchQuery(String sercherConent, LatLonPoint latLonPoint, int boundRegion, PoiSearch.OnPoiSearchListener onPoiSearchListener) {

        PoiSearch.Query query = new PoiSearch.Query(sercherConent, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(0);// 设置查第一页
        if (latLonPoint != null) {
            PoiSearch poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(onPoiSearchListener);
            poiSearch.setBound(new PoiSearch.SearchBound(latLonPoint, boundRegion, true));
            // 设置搜索区域为以lp点为圆心，其周围5000米范围
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }

    /**
     * @param aMap
     * @param isShowlocationLayer  是否显示默认的定位按钮
     * @param isShowCompassenabled 是否可触发定位并显示    设置地图默认的指南针是否显示
     * @param isShowScaleSize      设置地图默认的比例尺是否显示
     */
    protected void uiSetting(AMap aMap, boolean isShowlocationLayer, boolean isShowCompassenabled, boolean isShowScaleSize) {
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(isShowlocationLayer); // 是否显示默认的定位按钮
        aMap.setMyLocationEnabled(isShowlocationLayer);// 是否可触发定位并显示
        uiSettings.setCompassEnabled(isShowCompassenabled);//设置地图默认的指南针是否显示
        uiSettings.setScaleControlsEnabled(isShowScaleSize);//设置地图默认的比例尺是否显示
    }

    /**
     * 自定义infoWindows
     *
     * @param aMap
     * @param onInfoWindowClickListener 设置点击infoWindow事件监听器
     * @param infoWindowAdapter         设置自定义InfoWindow样式
     */
    protected void customerInfoWindow(AMap aMap, AMap.OnInfoWindowClickListener onInfoWindowClickListener, AMap.InfoWindowAdapter infoWindowAdapter) {
        if (null != onInfoWindowClickListener) {
            aMap.setOnInfoWindowClickListener(onInfoWindowClickListener);// 设置点击infoWindow事件监听器
        }
        if (null != infoWindowAdapter) {
            aMap.setInfoWindowAdapter(infoWindowAdapter);// 设置自定义InfoWindow样式
        }
    }

    /**
     * 把经纬度转成详细地址
     *
     * @param latLonPoint
     * @param
     */
    protected void fromLantlontoAddress(LatLonPoint latLonPoint, Context context, GeocodeSearch.OnGeocodeSearchListener onGeocodeSearchListener) {
        GeocodeSearch geocodeSearch = new GeocodeSearch(context);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocodeSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
        geocodeSearch.setOnGeocodeSearchListener(onGeocodeSearchListener);
    }

    /**
     * 把详细地址转成经纬度
     *
     * @param
     * @param
     */
    protected void fromAddresstoLatLon(String address, Context context, GeocodeSearch.OnGeocodeSearchListener onGeocodeSearchListener) {
        GeocodeSearch geocodeSearch = new GeocodeSearch(context);
        GeocodeQuery query = new GeocodeQuery(address, cityCode);// // 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        geocodeSearch.getFromLocationNameAsyn(query);// 设置异步逆地理编码请求
        geocodeSearch.setOnGeocodeSearchListener(onGeocodeSearchListener);
    }


    /**
     * 地上生长的Marker
     */
    protected void startGrowAnimation(Marker growMarker) {
        if (growMarker != null) {
            com.amap.api.maps.model.animation.Animation animation = new com.amap.api.maps.model.animation.ScaleAnimation(0, 1, 0, 1);
            animation.setInterpolator(new LinearInterpolator());
            //整个移动所需要的时间
            animation.setDuration(1000);
            //设置动画
            growMarker.setAnimation(animation);
            //开始动画
            growMarker.startAnimation();
        }
    }
}
