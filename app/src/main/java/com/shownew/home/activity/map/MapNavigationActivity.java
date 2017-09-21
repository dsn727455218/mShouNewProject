package com.shownew.home.activity.map;

import android.os.Bundle;

import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.NaviLatLng;
import com.shownew.home.Config;
import com.shownew.home.R;
import com.wp.baselib.utils.Preference;

import static com.shownew.home.Config.DRIVEROUTE;
import static com.shownew.home.Config.RIDEROUTE;

/**
 * 地图导航
 */
public class MapNavigationActivity extends BaseNavigationActivity {

    private NaviLatLng startLatlNG;
    private NaviLatLng endLatlNG;
    private int navigationType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daohang);
        setBarColor(R.color.colornavigation);
        mAMapNaviView = (AMapNaviView) findViewById(R.id.naviview);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);
    }

    @Override
    protected void readSaveBundle(Bundle bundle) {
        super.readSaveBundle(bundle);
        if (bundle != null) {
            startLatlNG = bundle.getParcelable("startLocaltion");
            endLatlNG = bundle.getParcelable("endLocaltion");
            navigationType = bundle.getInt("navigationType");//1  代表骑行  2 代表步行


        }
    }

    /**
     * 初始化成功
     */
    @Override
    public void onInitNaviSuccess() {
        super.onInitNaviSuccess();
        if (startLatlNG == null && endLatlNG == null)
            return;
        if (Config.WALKROUTE == navigationType) {
            mAMapNavi.calculateWalkRoute(startLatlNG, endLatlNG);//步行
            return;
        }
        navigationType = Preference.getInt(mShouNewApplication
                , Config.DEVICETYPE, RIDEROUTE);
        if (RIDEROUTE == navigationType) {
            mAMapNavi.calculateRideRoute(startLatlNG, endLatlNG);//骑行
        } else if (DRIVEROUTE == navigationType) {//自驾车
            sList.add(startLatlNG);
            eList.add(endLatlNG);
            int strategy = 0;
            try {
                //再次强调，最后一个参数为true时代表多路径，否则代表单路径
                strategy = mAMapNavi.strategyConvert(true, false, false, false, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            mAMapNavi.setCarNumber("京", "DFZ588");
            mAMapNavi.calculateDriveRoute(sList, eList, null, strategy);
        }

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
