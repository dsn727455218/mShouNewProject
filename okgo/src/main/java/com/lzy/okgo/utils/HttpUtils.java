package com.lzy.okgo.utils;

import android.text.TextUtils;

import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/8/9
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class HttpUtils {
    /**
     * 将传递进来的参数拼接成 url
     */
    public static String createUrlFromParams( Map<String, List<String>> params,String url) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(url);
            if (url.indexOf('&') > 0 || url.indexOf('?') > 0)
                sb.append("&");
            else
                sb.append("?");
            for (Map.Entry<String, List<String>> urlParams : params.entrySet()) {
                List<String> urlValues = urlParams.getValue();
                for (String value : urlValues) {
                    //对参数进行 utf-8 编码,防止头信息传中文
                    String urlValue = URLEncoder.encode(value, "UTF-8");
                    sb.append(urlParams.getKey()).append("=").append(urlValue).append("&");
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            OkLogger.e(e);
        }
        return url;
    }

    /**
     * 通用的拼接请求头
     */
    public static Request.Builder appendHeaders(HttpHeaders headers) {
        Request.Builder requestBuilder = new Request.Builder();
        if (headers.headersMap.isEmpty())
            return requestBuilder;
        Headers.Builder headerBuilder = new Headers.Builder();
        try {
            for (Map.Entry<String, String> entry : headers.headersMap.entrySet()) {
                //对头信息进行 utf-8 编码,防止头信息传中文,这里暂时不编码,可能出现未知问题,如有需要自行编码
                //                String headerValue = URLEncoder.encode(entry.getValue(), "UTF-8");
                headerBuilder.add(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            OkLogger.e(e);
        }
        requestBuilder.headers(headerBuilder.build());
        return requestBuilder;
    }

    /**
     * 生成类似表单的请求体
     */
    public static RequestBody generateMultipartRequestBody(HttpParams params, boolean isMultipart) {
        if (params.fileParamsMap.isEmpty() && !isMultipart) {
            //表单提交，没有文件
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            for (String key : params.urlParamsMap.keySet()) {
                List<String> urlValues = params.urlParamsMap.get(key);
                for (String value : urlValues) {
                    bodyBuilder.add(key, value);
                }
            }
            return bodyBuilder.build();
        } else {
            //表单提交，有文件
            MultipartBody.Builder multipartBodybuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            //拼接键值对
            if (!params.urlParamsMap.isEmpty()) {
                for (Map.Entry<String, List<String>> entry : params.urlParamsMap.entrySet()) {
                    List<String> urlValues = entry.getValue();
                    for (String value : urlValues) {
                        multipartBodybuilder.addFormDataPart(entry.getKey(), value);
                    }
                }
            }
            //拼接文件
            for (Map.Entry<String, List<HttpParams.FileWrapper>> entry : params.fileParamsMap.entrySet()) {
                List<HttpParams.FileWrapper> fileValues = entry.getValue();
                for (HttpParams.FileWrapper fileWrapper : fileValues) {
                    RequestBody fileBody = RequestBody.create(fileWrapper.contentType, fileWrapper.file);
                    multipartBodybuilder.addFormDataPart(entry.getKey(), fileWrapper.fileName, fileBody);
                }
            }
            return multipartBodybuilder.build();
        }
    }

    /**
     * 根据响应头或者url获取文件名
     */
    public static String getNetFileName(Response response, String url) {
        String fileName = getHeaderFileName(response);
        if (TextUtils.isEmpty(fileName))
            fileName = getUrlFileName(url);
        if (TextUtils.isEmpty(fileName))
            fileName = "nofilename";
        return fileName;
    }

    /**
     * 解析文件头 Content-Disposition:attachment;filename=FileName.txt
     */
    private static String getHeaderFileName(Response response) {
        String dispositionHeader = response.header(HttpHeaders.HEAD_KEY_CONTENT_DISPOSITION);
        if (dispositionHeader != null) {
            String split = "filename=";
            int indexOf = dispositionHeader.indexOf(split);
            if (indexOf != -1) {
                String fileName = dispositionHeader.substring(indexOf + split.length(), dispositionHeader.length());
                fileName = fileName.replaceAll("\"", "");   //文件名可能包含双引号,需要去除
                return fileName;
            }
        }
        return null;
    }

    /**
     * 通过 ‘？’ 和 ‘/’ 判断文件名
     */
    private static String getUrlFileName(String url) {
        int index = url.lastIndexOf('?');
        String filename;
        if (index > 1) {
            filename = url.substring(url.lastIndexOf('/') + 1, index);
        } else {
            filename = url.substring(url.lastIndexOf('/') + 1);
        }
        return filename;
    }

    /**
     * 根据路径删除文件
     */
    public static boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path))
            return true;
        File file = new File(path);
        if (!file.exists())
            return true;
        if (file.isFile()) {
            boolean delete = file.delete();
            OkLogger.e("deleteFile:" + delete + " path:" + path);
            return delete;
        }
        return false;
    }

    /**
     * 不带任何参数构造
     * get 方式请求封装，此方法时候传统的get ?key=value& 参数
     *
     * @param url    请求的根地址不带?号--------------------------------------------------------
     * @param params 可以为null
     * @return 返回get 全路径
     */
    protected static String createUrlFromParams(String url,Map<String, String> params) {
        if (params == null)
            return url;

        StringBuilder result = new StringBuilder(url);
        result.append("?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
            result.append("&");
        }
        return result.lastIndexOf("&") > 0 ? result.substring(0, result.lastIndexOf("&")) : result.toString();
    }
    /**
     * 将实体转化Hashmap
     * @param bean 待转化的实体类
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static HashMap<String, String> convertBeanToMap(Object bean)
            throws IllegalArgumentException, IllegalAccessException {
        Class<?> object = bean.getClass();
        HashMap<String, String> data = new HashMap<String, String>();
        //父类字段
        Field[] superFields = object.getSuperclass().getDeclaredFields();
        for (Field field : superFields) {
            field.setAccessible(true);
            if (field.get(bean) != null) {
                data.put(field.getName(), String.valueOf(field.get(bean)));
            }
        }

        //子类字段
        Field[] fields = object.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.get(bean) != null) {
                data.put(field.getName(), String.valueOf(field.get(bean)));
            }
        }
        return data;
    }
}