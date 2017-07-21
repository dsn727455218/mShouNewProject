package com.shownew.home.activity.map;

import android.animation.Animator;
import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.google.gson.reflect.TypeToken;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.shownew.home.R;
import com.shownew.home.ShouNewApplication;
import com.shownew.home.adapter.HistoryMapAdapter;
import com.shownew.home.module.DeviceAPI;
import com.shownew.home.module.entity.HistoryMapPointEntity;
import com.wp.baselib.utils.JsonUtils;
import com.wp.baselib.utils.ToastUtil;
import com.wp.baselib.widget.TitleBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
    private XRecyclerView mHistortyRecyclerView;
    private ArrayList<HistoryMapPointEntity> historyMapPointEntities = new ArrayList<HistoryMapPointEntity>();
    private HistoryMapAdapter historyMapAdapter;

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
    }

    int height, xheight;

    private void initViews() {
        TitleBarView mTitleBarView = (TitleBarView) findViewById(R.id.headbar);
        mTitleBarView.setTitle("历史轨迹");
        mTitleBarView.setTitleTextColor(R.color.color_title);
        mHistortyRecyclerView = (XRecyclerView) findViewById(R.id.histort_xrecyclerView);

        mTitleBarView.setOnLeftOnClickListener(this);
        mTitleBarView.setLeftIconAndText(R.drawable.back_arrow, "返回");
        mTitleBarView.setTitleSize(20);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mHistortyRecyclerView.getLayoutParams();
        height = ((View) mHistortyRecyclerView.getParent()).getHeight();
        xheight = (int) (height * 0.5 * 0.8);
        layoutParams.height = xheight;
        mHistortyRecyclerView.setLayoutParams(layoutParams);
        mHistortyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyMapAdapter = new HistoryMapAdapter(historyMapPointEntities, this);
        mHistortyRecyclerView.setAdapter(historyMapAdapter);
        mHistortyRecyclerView.setPullRefreshEnabled(false);//禁止刷新
        mHistortyRecyclerView.setRefreshProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mHistortyRecyclerView.setArrowImageView(R.drawable.refresh_arrow);
        mHistortyRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallClipRotateMultiple);
        mHistortyRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                refresh();
            }

            @Override
            public void onLoadMore() {
                isRefresh = false;
                getGPSList();
            }
        });

    }

    private boolean isRefresh;

    private void refresh() {
        isRefresh = true;
        pager = 1;
        getGPSList();
    }


    /**
     * 初始化AMap对象
     */
    private void init() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            setUpMap();
            setMapLogoLocation(mAMap, 3);
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
        refresh();
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

    private int pager = 1;

    /**
     * 获取历史轨迹
     */
    private void getGPSList() {
        mDeviceAPI.getGPSList(pager, mShouNewApplication.new ShouNewHttpCallBackLisener() {
            @Override
            protected void resultData(Object data, JSONObject json, Response response, Exception exception) {
                if (isRefresh && historyMapPointEntities.size() != 0) {
                    historyMapPointEntities.clear();
                    mHistortyRecyclerView.refreshComplete();
                } else {
                    mHistortyRecyclerView.loadMoreComplete();
                }
                if (exception == null) {
                    if (null != json) {
                        if (json.has("data")) {
                            try {
                                JSONObject jsonObject = json.getJSONObject("data");
                                if (jsonObject != null) {
                                    String gpsList = jsonObject.getString("gpsList");
                                    ArrayList<HistoryMapPointEntity> mapPointEntities = JsonUtils.fromJson(gpsList, new TypeToken<ArrayList<HistoryMapPointEntity>>() {
                                    }.getType());
                                    if (mapPointEntities != null && mapPointEntities.size() > 0) {
                                        historyMapPointEntities.addAll(mapPointEntities);
                                        pager++;
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    if (historyMapPointEntities.size() > 0) {
                        mHistortyRecyclerView.setNoMore(true);
                    }
                }
                if (historyMapPointEntities.size() == 0) {
                    ToastUtil.showToast("还没有历史轨迹");
                    finish();
                }

                if (historyMapPointEntities.size() > 0) {
                    clearMapMarker(mAMap);
                    HistoryMapPointEntity historyMapPointEntity = historyMapPointEntities.get(0);
                    addMarker(historyMapPointEntity);
                }
                notifyAdapter();
            }
        });
    }


//    //绘制一条纹理线
//    private void addPolylinesWithTexture(LatLng... latLngs) {
//        int length = latLngs.length;
//        if (length > 0) {
//            changeMapCenter(mAMap, latLngs[0]);
//        }
//        for (int i = 0; i < length; i++) {
//            addMarkersToMap(mAMap, historyResourcesId[i], new LatLng(latLngs[i].latitude, latLngs[i].longitude));
//        }
//        //用一个数组来存放纹理
//        List<BitmapDescriptor> texTuresList = new ArrayList<BitmapDescriptor>();
//        texTuresList.add(BitmapDescriptorFactory.fromResource(R.drawable.wenli));
//        //指定某一段用某个纹理，对应texTuresList的index即可, 四个点对应三段颜色
//        List<Integer> texIndexList = new ArrayList<Integer>();
//        texIndexList.add(0);//对应上面的第0个纹理
//
//        PolylineOptions options = new PolylineOptions();
//        options.width(20);//设置宽度
//        //加入四个点
//        options.add(latLngs);
//        //加入对应的颜色,使用setCustomTextureList 即表示使用多纹理；
//        options.setCustomTextureList(texTuresList);
//
//        //设置纹理对应的Index
//        options.setCustomTextureIndex(texIndexList);
//
//        mAMap.addPolyline(options);
////    }

    private void isShowRecylerView(boolean isShow) {
        ValueAnimator animator = new ValueAnimator();
        animator.setDuration(250);
        animator.setFloatValues(!isShow ? height : xheight, !isShow ? xheight : height);
        animator.setEvaluator(new FloatEvaluator() {
            @Override
            public Float evaluate(float fraction, Number startValue, Number endValue) {
                float values = startValue.floatValue() + (endValue.floatValue() - startValue.floatValue()) * fraction;
                return values;
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mHistortyRecyclerView.getLayoutParams();
                layoutParams.height = (int) value;
                mHistortyRecyclerView.setLayoutParams(layoutParams);
            }
        });
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (animator != null)
                    animator = null;

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private boolean isShowMore;

    /**
     * 显示更多
     */
    public void showMore() {
        if (!isShowMore) {
            isShowMore = true;
            isShowRecylerView(isShowMore);
            historyMapPointEntities.get(0).setShow(true);
            notifyAdapter();
            mHistortyRecyclerView.setPullRefreshEnabled(true);//刷新
        } else {
            isShowMore = false;
            isShowRecylerView(isShowMore);
            notifyData();
            mHistortyRecyclerView.setPullRefreshEnabled(false);//禁止刷新
        }


    }

    public void clickItem(HistoryMapPointEntity historyMapPointEntity) {
        if (isShowMore) {
            //关闭显示更多
            isShowMore = false;
            isShowRecylerView(isShowMore);
            notifyData();
        }
        mHistortyRecyclerView.setPullRefreshEnabled(false);//刷新
        addMarker(historyMapPointEntity);
    }

    private void notifyData() {
        historyMapPointEntities.get(0).setShow(false);
        notifyAdapter();
    }

    private void notifyAdapter() {
        historyMapAdapter.notifyDataSetChanged();
    }


    private void addMarker(HistoryMapPointEntity historyMapPointEntity) {
        clearMapMarker(mAMap);
        String[] la = historyMapPointEntity.getGGps().split(",");
        LatLng latLng = new LatLng(Double.valueOf(la[0]), Double.valueOf(la[1]));
        addMarkersToMap(mAMap, R.drawable.pakerlocation, latLng);
        changeMapCenter(mAMap, latLng, 20);
    }

}
