package com.shownew.home.module;

import com.shownew.home.Config;
import com.shownew.home.ShouNewApplication;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 云控设备相关接口处理
 *
 * @author Jason
 * @version 1.0
 * @date 2017/4/10 0010
 */

public class DeviceAPI extends PublicApi {
    public DeviceAPI(ShouNewApplication mainApplication) {
        super(mainApplication);
    }

    /**
     * @param value   1-一键锁定 2-一键启动 3-寻车 4-运行刷新
     * @param lisener
     */
    public void controlLock(String value, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> hashMap = getHashMap(Config.UP_CONTROL);
        hashMap.put("method", "upControl");
        hashMap.put("value", value);
        post(Config.UP_CONTROL, hashMap, lisener);
    }

    /**
     * 设置振动灵敏度接口
     *
     * @param sensitivity 1--255
     * @param lisener
     */
    public void setShockSensitivity(String sensitivity, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> hashMap = getHashMap(Config.UP_MOVE_LEVEL);
        hashMap.put("method", "upmovelevel");
        hashMap.put("value", sensitivity);
        post(Config.UP_MOVE_LEVEL, hashMap, lisener);
    }

    /**
     * 获取最新GPS数据接口
     *
     * @param lisener
     */
    public void getNewGps(ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> hashMap = getHashMap(Config.GET_NEW_GPS);
        hashMap.put("method", "getNewGPS");
        get(createUrlFromParams(Config.GET_NEW_GPS, hashMap), lisener);
    }

    /**
     * 设置振动报警接口（已测试）
     *
     * @param lisener
     * @value 0–关闭振动报警 1--开启振动报警
     */
    public void setShockWarn(String value, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> hashMap = getHashMap(Config.UP_MOVE_WARN);
        hashMap.put("method", "upMoveWarn");
        hashMap.put("value", value);
        post(Config.UP_MOVE_WARN, hashMap, lisener);
    }

    /**
     * @value 最高速度限制值1--255
     */
    public void setSpeedValues(String value, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> hashMap = getHashMap(Config.UP_LIMIT_SPEED);
        hashMap.put("method", "upLimitSpeed");
        hashMap.put("value", value);
        post(Config.UP_LIMIT_SPEED, hashMap, lisener);
    }

    /**
     * 充电器开关接口（已测试）
     *
     * @param value   0-关闭 1-打开
     * @param lisener
     */
    public void setChargerOff(String value, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> hashMap = getHashMap(Config.UP_CHARGER);
        hashMap.put("method", "upCharger");
        hashMap.put("value", value);
        post(Config.UP_CHARGER, hashMap, lisener);
    }

    /**
     * 获取电池最新电量值
     *
     * @param lisener
     */
    public void getElectricity(ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> hashMap = getHashMap(Config.GET_ELECTRICITY);
        hashMap.put("method", "getElectricity");
        get(createUrlFromParams(Config.GET_ELECTRICITY, hashMap), lisener);
    }

    /**
     * 获取硬件最新数据接口（已测试）
     *
     * @param lisener
     */
    public void getDeviceNewData(ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> hashMap = getHashMap(Config.DEVICE_NEW_DATA);
        hashMap.put("method", "getNewInfo");
        get(createUrlFromParams(Config.DEVICE_NEW_DATA, hashMap), lisener);
    }


    //车辆处理


    /**
     * 车辆注册
     */
    public void newsCarRegister(HashMap<String, String> hashMap, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        post(Config.CAR, hashMap, lisener);
    }

    /**
     * 获取车辆信息列表
     */
    public void getCarInfoList(ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> hashMap = getHashMap(Config.CAR);
        hashMap.put("method", "getCarList");
        get(createUrlFromParams(Config.CAR, hashMap), lisener);
    }

    /**
     * 绑定车辆
     *
     * @param cId
     * @param lisener
     */
    public void bindCar(String cId, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> hashMap = getHashMap(Config.CAR);
        hashMap.put("method", "bindCar");
        hashMap.put("id", cId);
        post(Config.CAR, hashMap, lisener);
    }

    /**
     * 注销车辆
     *
     * @param cId
     * @param lisener
     */
    public void delectCar(String cId, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> hashMap = getHashMap(Config.CAR);
        hashMap.put("method", "delCar");
        hashMap.put("id", cId);
        post(Config.CAR, hashMap, lisener);
    }

    /**
     * 获取车辆类型
     *
     * @param
     * @param lisener
     */
    public void getCarType(ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> hashMap = getHashMap(Config.CAR);
        hashMap.put("method", "getCarTypeList");
        get(createUrlFromParams(Config.CAR, hashMap), lisener);
    }

    /**
     * 获取历史轨迹
     *
     * @param lisener
     */
    public void getGPSList(int page,ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> hashMap = getHashMap(Config.DEVICE_NEW_DATA);
        hashMap.put("method", "getGpsList");
        hashMap.put("page",String.valueOf(page));
        get(createUrlFromParams(Config.DEVICE_NEW_DATA, hashMap), lisener);

    }

    /**
     * 上传车辆图片
     *
     * @param cId
     * @param lisener
     */
    public void uploadCarImg(String cId, File file, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> hashMap = getHashMap(Config.CAR);
        hashMap.put("method", "uploadImg");
        hashMap.put("id", cId);
        postFile(Config.CAR, hashMap, "img", file, lisener);
    }
}
