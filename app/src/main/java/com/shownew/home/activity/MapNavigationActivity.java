package com.shownew.home.activity;

import android.os.Bundle;

import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.NaviLatLng;
import com.shownew.home.R;
import com.shownew.home.activity.map.BaseNavigationActivity;

/**
 * 地图导航
 */
public class MapNavigationActivity extends BaseNavigationActivity {

    //    protected NaviLatLng mEndLatlng = new NaviLatLng(40.084894,116.603039);
    //    protected NaviLatLng mStartLatlng = new NaviLatLng(39.825934,116.342972);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daohang);
        setBarColor(R.color.nav_bg);
        mAMapNaviView = (AMapNaviView) findViewById(R.id.naviview);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);
    }

    @Override
    protected void readSaveBundle(Bundle bundle) {
        super.readSaveBundle(bundle);
        if (bundle != null) {
            NaviLatLng startLatlNG = bundle.getParcelable("startLocaltion");
            NaviLatLng endLatlNG = bundle.getParcelable("endLocaltion");
            sList.add(startLatlNG);
            eList.add(endLatlNG);

        }
    }

    /**
     * 初始化成功
     */
    @Override
    public void onInitNaviSuccess() {
        super.onInitNaviSuccess();
        int strategy = 0;
        try {
            //再次强调，最后一个参数为true时代表多路径，否则代表单路径
            strategy = mAMapNavi.strategyConvert(true, false, false, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAMapNavi.setCarNumber("京", "DFZ588");
        mAMapNavi.calculateDriveRoute(sList, eList, null, strategy);
    }

    /**
     * 路线计算成功
     */
    @Override
    public void onCalculateRouteSuccess() {
        super.onCalculateRouteSuccess();
        mAMapNavi.startNavi(NaviType.GPS);
    }
}
