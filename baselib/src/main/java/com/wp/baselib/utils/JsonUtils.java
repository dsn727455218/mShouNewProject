package com.wp.baselib.utils;

import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * json 工具类
 * @description
 * @author summer
 * @date 2014年4月23日 上午11:16:14
 *
 */
public class JsonUtils  extends Convert{



	/**
	 * 对象转json字符串
	 * @param src
	 * @return
	 */
	public static String toJson(Object src) {
		    if(src instanceof JSONObject)
		    	return src.toString();
			return gson.toJson(src);
	}

	/**
	 * json字符串转对象,解析多个实体类集合
	 * @param json 字符串
	 * @param typeOfT new TypeToken<ArrayList<T>>(){}.getType()
	 * @return T
	 */
	public static <T> T fromJson(String json, Type typeOfT) {
		try {
			return gson.fromJson(json, typeOfT);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * json字符串转对象,从json字符串解析为单个实体类
	 * @param json 字符串
	 * @param clazz 实体类
	 * @return T 实体类 
	 */
	public static <T> T fromJson(String json, Class<T> clazz) {
		try {
			return gson.fromJson(json, clazz);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
