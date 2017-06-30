package com.shownew.home.module;

import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.callback.AbsCallback;
import com.shownew.home.Config;
import com.shownew.home.ShouNewApplication;
import com.shownew.home.module.entity.SourcesEntity;
import com.shownew.home.module.entity.UserEntity;
import com.wp.baselib.utils.JsonUtils;
import com.wp.baselib.utils.Preference;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Jason
 * @version 1.0
 * @date 2017/3/31 0031
 */

public class PublicApi extends BaseAPI {
    public PublicApi(ShouNewApplication mainApplication) {
        super(mainApplication);
    }

    /**
     * 保存用户数据
     *
     * @param userInfo
     */
    public void saveUserInfo(UserEntity userInfo) {
        Preference.putString(Config.FILE, mainApplication, Config.USERINFO, JsonUtils.toJson(userInfo));
    }

    /**
     * 获取用户数据
     *
     * @return
     */
    public UserEntity getUserInfo() {
        return JsonUtils.fromJson(Preference.getString(Config.FILE, mainApplication, Config.USERINFO), UserEntity.class);
    }

    /**
     * 保存资源数据
     *
     * @param data
     */
    public void saveRourcesData(String data) {
        Preference.putString(Config.FILE, mainApplication, Config.ROURCES_DATA, data);
    }

    /**
     * 获取资源数据
     *
     * @return
     */
    public ArrayList<SourcesEntity> getSourcesData() {
        return JsonUtils.fromJson(Preference.getString(Config.FILE, mainApplication, Config.ROURCES_DATA), new TypeToken<ArrayList<SourcesEntity>>() {
        }.getType());
    }




    /**
     * 获取消息列表
     *
     * @param pager
     * @param msgType
     * @param lisener
     */
    public void getMsgList(int pager, int msgType, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> hashMap = getHashMap(Config.GET_MSG_LIST);
        hashMap.put("method", "getMessageList");
        hashMap.put("page", String.valueOf(pager));
        hashMap.put("type", String.valueOf(msgType));
        get(createUrlFromParams(Config.GET_MSG_LIST, hashMap), lisener);
    }

    /**
     * 获取活动广告图
     */
    public void getAdverPic(int page, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> hashMap = getHashMap(Config.GET_MSG_LIST);
        hashMap.put("method", "getAdList");
        hashMap.put("page", String.valueOf(page));
        get(createUrlFromParams(Config.GET_MSG_LIST, hashMap), lisener);
    }

    /**
     * /**
     * 获取轮播活动广告图
     * 0-首页广告 1-服务上方广告
     */
    public void getLastAdList(int level, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> hashMap = getHashMap(Config.GET_MSG_LIST);
        hashMap.put("method", "getLastAdList");
        hashMap.put("level", String.valueOf(level));
        get(createUrlFromParams(Config.GET_MSG_LIST, hashMap), lisener);
    }

    /**
     * 设置活动点击量加一接口
     */
    public void setAdverClick(int id, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> hashMap = getHashMap(Config.GET_MSG_LIST);
        hashMap.put("method", "upClickNum");
        hashMap.put("id", String.valueOf(id));
        get(createUrlFromParams(Config.GET_MSG_LIST, hashMap), lisener);
    }

    /**
     * 获取消息3个消息最新数据
     *
     * @param lisener
     */
    public void getMsgAllNew(ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> hashMap = getHashMap(Config.GET_MSG_LIST);
        hashMap.put("method", "getOneMessageList");
        get(createUrlFromParams(Config.GET_MSG_LIST, hashMap), lisener);
    }

    /**
     * 获取系统服务器时间
     *
     * @param lisener
     */
    public void getSystemTime(ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> hashMap = getHashMap(Config.USER);
        hashMap.put("method", "getSysTime");
        get(createUrlFromParams(Config.USER, hashMap), lisener);
    }

    /**
     * 用户获取售后网点列表
     *
     * @param lisener
     */
    public void getAfterMarker(String gps, int pager, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> hashMap = getHashMap(Config.SERICE);
        hashMap.put("method", "getBranchList");
        hashMap.put("gps", gps);
        hashMap.put("page", String.valueOf(pager));
        get(createUrlFromParams(Config.SERICE, hashMap), lisener);
    }

    /**
     * 用户获取售后网点列表
     *
     * @param lisener
     */
    public void getInsuranceList(String gps, int pager, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> hashMap = getHashMap(Config.SERICE);
        hashMap.put("method", "getInsuranceList");
        hashMap.put("gps", gps);
        hashMap.put("page", String.valueOf(pager));
        get(createUrlFromParams(Config.SERICE, hashMap), lisener);
    }

    /**
     * 下载文件
     *
     * @param url
     * @param lisener
     */
    public void downFile(String url, AbsCallback lisener) {
        get(url, lisener);
    }

    /**
     * 获取app版本接口
     *
     * @param lisener
     */
    private void getAppVersion(ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> map = getHashMap(Config.USER);
        map.put("method", "getLastVersion");
        map.put("platform", "1");
        get(createUrlFromParams(Config.USER, map), lisener);
    }

    /**
     * 检查app版本
     *
     * @param lisener
     */
    public void getCheckAppUrl(ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        getAppVersion(lisener);
    }

    /**
     *  获取是否存在未读消息接口
     * @param lisener
     */
    public void exsitUnReadMsg(ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> map = getHashMap(Config.GET_MSG_LIST);
        map.put("method", "exsitUnRead");
        get(createUrlFromParams(Config.GET_MSG_LIST, map), lisener);
    }
}
