package com.shownew.home.utils.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.shownew.home.R;
import com.shownew.home.module.entity.CityEntity;
import com.wp.baselib.widget.WheelView;
import com.wp.zxing.view.ViewfinderView;

import java.util.ArrayList;
import java.util.List;

/**
 * 城市选择dialog
 * @author Jason
 * @version 1.0
 * @date 2017/4/20 0020
 */

public class CitySelectDialog extends BaseDialog implements View.OnClickListener {

    private WheelView privinces;
    private List<String> privincesData = new ArrayList<String>();
    private List<String> cityData = new ArrayList<String>();
    private List<String> districtData = new ArrayList<String>();
    private WheelView city;
    private WheelView district;
    private String selectPrivinves;
    private String selectCity;
    private String selectDistric;

    /**
     * //构造方法 来实现 最基本的对话框
     *
     * @param context
     */
    public CitySelectDialog(Context context, final List<CityEntity> cityEntities) {
        super(context, Gravity.BOTTOM);
        View view = mInflater.inflate(R.layout.layout_select_city_dialog, null);

        privinces = (WheelView) view.findViewById(R.id.privince);
        city = (WheelView) view.findViewById(R.id.city);
        district = (WheelView) view.findViewById(R.id.district);
        view.findViewById(R.id.city_cancel).setOnClickListener(this);
        view.findViewById(R.id.city_sure).setOnClickListener(this);
        getProvinces(cityEntities);
        getCityData(cityEntities);
        getDistrictData(cityEntities);


        privinces.setItems(privincesData);
        privinces.setTextColor(context.getResources().getColor(R.color.color_common_press));
        privinces.setTextSize(13);
        WheelView.LineConfig config = new WheelView.LineConfig();
        config.setColor(context.getResources().getColor(R.color.bgcolor));//线颜色
        config.setAlpha(50);//线透明度
        config.setRatio((float) (1.0 / 10.0));//线比率
        config.setThick(ViewfinderView.dip2px(context, 1));//线粗
        privinces.setLineConfig(config);
        privinces.setOnWheelListener(new WheelView.OnWheelListener() {
            @Override
            public void onSelected(boolean isUserScroll, int index, String item) {
                selectPrivinves = item;
                getCityData(cityEntities);
                city.setItems(cityData);
                getDistrictData(cityEntities);
                district.setItems(districtData);
            }
        });


        city.setItems(cityData);
        city.setTextColor(context.getResources().getColor(R.color.color_common_press));
        city.setTextSize(13);
        city.setLineConfig(config);
        city.setOnWheelListener(new WheelView.OnWheelListener() {
            @Override
            public void onSelected(boolean isUserScroll, int index, String item) {
                selectCity = item;
                getDistrictData(cityEntities);
                district.setItems(districtData);
            }
        });


        district.setItems(districtData);
        district.setTextSize(13);
        district.setTextColor(context.getResources().getColor(R.color.color_common_press));
        district.setLineConfig(config);
        district.setOnWheelListener(new WheelView.OnWheelListener() {
            @Override
            public void onSelected(boolean isUserScroll, int index, String item) {
                selectDistric = item;
            }
        });

        selectDistric = district.getSelectedItem();
        selectPrivinves = privinces.getSelectedItem();
        selectCity = city.getSelectedItem();
        int width = display.getWidth();
        //设置dialog的宽高为屏幕的宽高
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        getDialog().setContentView(view, layoutParams);
    }

    private void getProvinces(List<CityEntity> cityEntities) {
        if (cityEntities != null && cityEntities.size() > 0) {
            for (CityEntity cityEntity : cityEntities) {
                privincesData.add(cityEntity.getPrivince());
            }
        }
    }

    private void getDistrictData(List<CityEntity> cityEntities) {
        if (cityEntities != null) {
            districtData.clear();
            if (cityEntities.size() > privinces.getCurrentPosition()) {
                List<CityEntity.CityBean> cityEntity = cityEntities.get(privinces.getCurrentPosition()).getCity();
                if (cityEntity != null && cityEntity.size() > city.getCurrentPosition()) {
                    List<CityEntity.CityBean.DistrictBean> districtBeen = cityEntity.get(city.getCurrentPosition()).getDistrict();
                    for (CityEntity.CityBean.DistrictBean districtBean : districtBeen) {
                        districtData.add(districtBean.getDistrict());
                    }
                }
            }
        }
    }

    private void getCityData(List<CityEntity> cityEntities) {
        cityData.clear();
        if (null != cityEntities) {
            if (cityEntities.size() > privinces.getCurrentPosition()) {
                List<CityEntity.CityBean> cityEntity = cityEntities.get(privinces.getCurrentPosition()).getCity();
                for (CityEntity.CityBean cityBean : cityEntity) {
                    cityData.add(cityBean.getCity());
                }
            }
        }
    }

    @Override
    protected int getDialogStyleId() {
        return 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.city_cancel:
                dismiss();
                break;
            case R.id.city_sure:
                if (null != mCitySelectLisener) {
                    dismiss();
                    mCitySelectLisener.sure(selectPrivinves + selectCity + selectDistric);

                }
                break;
        }
    }

    private CitySelectLisener mCitySelectLisener;

    public CitySelectDialog setCitySelectLisener(CitySelectLisener citySelectLisener) {
        mCitySelectLisener = citySelectLisener;
        return this;
    }

    public interface CitySelectLisener {
        void sure(String address);
    }

}
