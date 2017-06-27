package com.shownew.home.activity.map;

import android.os.Bundle;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;
import com.shownew.home.R;
import com.shownew.home.ShouNewApplication;
import com.shownew.home.module.DeviceAPI;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.widget.TitleBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;



/**
 * 历史轨迹
 *
 * @author Jason
 * @version 1.0
 * @date 2017/4/15 0015
 */

public class HistoryTrajectoryActivity extends BaseLocationActivity implements View.OnClickListener {
    private MapView mMapView;
    private AMap mAMap;
    private ShouNewApplication mShouNewApplication;
    private DeviceAPI mDeviceAPI;
    private int[] historyResourcesId = {R.drawable.history_1, R.drawable.history_2, R.drawable.history_3, R.drawable.history_4, R.drawable.history_5, R.drawable.history_6, R.drawable.history_7, R.drawable.history_8, R.drawable.history_9, R.drawable.history_10};
    private LatLng mLatLngs[];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_trajectory);
        mShouNewApplication = ShouNewApplication.getInstance();
        mDeviceAPI = new DeviceAPI(mShouNewApplication);
        mMapView = (MapView) findViewById(R.id.chase_map);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
        initViews();
        getGPSList();

    }


    private void initViews() {
        TitleBarView  mTitleBarView = (TitleBarView) findViewById(R.id.headbar);
        mTitleBarView.setTitle("历史轨迹");
        mTitleBarView.setTitleTextColor(R.color.color_title);
        mTitleBarView.setOnLeftOnClickListener(this);
        mTitleBarView.setLeftIconAndText(R.drawable.back_arrow, "返回");
        mTitleBarView.setTitleSize(20);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                finish();
                break;
        }
    }

    /**
     * 获取历史轨迹
     */
    private void getGPSList() {
        mDeviceAPI.getGPSList(mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (exception == null) {
                    if (null != json) {
                        if (json.has("data")) {
                            try {
                                JSONObject jsonObject = json.getJSONObject("data");
                                JSONArray gpsInfo = jsonObject.getJSONArray("gpsInfo");
                                int length = gpsInfo.length();
                                mLatLngs = new LatLng[length];
                                for (int i = 0; i < length; i++) {
                                    String gps = (String) gpsInfo.get(i);
                                    String[] gpss = gps.split(",");
                                    mLatLngs[i] = (new LatLng(Double.valueOf(gpss[0]), Double.valueOf(gpss[1])));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if (mLatLngs == null) {
                    finish();
                    ToastUtil.showToast("还没有历史轨迹");
                } else {
                    addPolylinesWithTexture(mLatLngs);
                }
            }
        });
    }


    //绘制一条纹理线
    private void addPolylinesWithTexture(LatLng... latLngs) {
        int length = latLngs.length;
        if (length > 0) {
            changeMapCenter(mAMap, latLngs[0]);
        }
        for (int i = 0; i < length; i++) {
            addMarkersToMap(mAMap, historyResourcesId[i], new LatLng(latLngs[i].latitude,latLngs[i].longitude));
        }
        //用一个数组来存放纹理
        List<BitmapDescriptor> texTuresList = new ArrayList<BitmapDescriptor>();
        texTuresList.add(BitmapDescriptorFactory.fromResource(R.drawable.wenli));
        //指定某一段用某个纹理，对应texTuresList的index即可, 四个点对应三段颜色
        List<Integer> texIndexList = new ArrayList<Integer>();
        texIndexList.add(0);//对应上面的第0个纹理

        PolylineOptions options = new PolylineOptions();
        options.width(20);//设置宽度
        //加入四个点
        options.add(latLngs);
        //加入对应的颜色,使用setCustomTextureList 即表示使用多纹理；
        options.setCustomTextureList(texTuresList);

        //设置纹理对应的Index
        options.setCustomTextureIndex(texIndexList);

        mAMap.addPolyline(options);
    }
}
