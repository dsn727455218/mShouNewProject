package com.shownew.home.module;

import com.shownew.home.ShouNewApplication;
import com.wp.baselib.BaseApi;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Jason
 * @version 1.0
 * @date 2017/4/10 0010
 */

public class BaseAPI extends BaseApi {

    protected ShouNewApplication mShouNewApplication;

    public BaseAPI(ShouNewApplication mainApplication) {
        super(mainApplication);
        this.mShouNewApplication = mainApplication;
    }


    /**
     * 添加系统级别参数
     *
     * @return
     */
    private Map<String, String> getSystemTreeMap(String url) {
        Map<String, String> hashMap = new TreeMap<String, String>();
        return hashMap;
    }

    /**
     * 加密参数封装 返回已经封装了基础数据的HashMap<String, String>对象, <br>
     * 可以通过JsonUtils.toJson(src);转换为json字符串
     *
     * @param action 需要加密的api
     * @return
     */
    protected Map<String, String> getHashMap(String action) {
        return getSystemTreeMap(action);
    }
}
