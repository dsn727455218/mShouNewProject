package com.wp.baselib;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okgo.utils.HttpUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by home on 2017/3/31 0031.
 */

public class BaseApi extends HttpUtils {
//    private Object appId;
    protected MainApplication mainApplication;

    protected BaseApi(MainApplication mainApplication) {
        this.mainApplication = mainApplication;
        /**
         * 获取appId
         */
//        appId = StringUtil.getMetaValue(mainApplication, "DINGDIAN_APPID");
    }

    protected void get(String url, MainApplication.HttpCallBack lisener) {
        OkGo.get(url).execute(lisener);
    }

    protected void get(String url, AbsCallback lisener) {
        OkGo.get(url).execute(lisener);
    }

    protected void post(String url, Map<String, String> params, MainApplication.HttpCallBack lisener) {
        post(url, params).execute(lisener);
    }

    private PostRequest post(String url, Map<String, String> params) {
        if (null == params) {
            return OkGo.post(url);
        }
        return OkGo.post(url).params(params);
    }


    /**
     * 参数加多个文件上传
     * @param url
     * @param params
     * @param fileKey
     * @param files
     * @param lisener
     */
    protected void postFiles(String url, Map<String, String> params, String fileKey, ArrayList<File> files, MainApplication.HttpCallBack lisener) {
        post(url, params).addFileParams(fileKey, files).execute(lisener);
    }
    /**
     * 参数加一个文件上传
     *
     * @param url
     * @param params
     * @param fileKey
     * @param files
     * @param lisener
     */
    protected void postFile(String url, Map<String, String> params, String fileKey, File files, MainApplication.HttpCallBack lisener) {
        post(url, params).params(fileKey, files).execute(lisener);
    }

    /**
     * 无参数加多个文件上传
     *
     * @param url
     * @param fileKey
     * @param files
     * @param lisener
     */
    protected void postFiles(String url, String fileKey, ArrayList<File> files, MainApplication.HttpCallBack lisener) {
        post(url, null).addFileParams(fileKey, files).execute(lisener);
    }

    /**
     * 无参数加一个文件上传
     *
     * @param url
     * @param fileKey
     * @param files
     * @param lisener
     */
    protected void postFile(String url, String fileKey, File files, MainApplication.HttpCallBack lisener) {
        post(url, null).params(fileKey, files).execute(lisener);
    }
}
