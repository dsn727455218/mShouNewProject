package com.wp.baselib;

/**
 * @Description 全局库的配置文件，子项目可以继承该类做扩展
 * @Author summer
 * @Date 2014年4月2日 上午10:53:02
 */
public class Config {
	/**
	 * 网络请求成功回调 200
	 */
	public static final String RESPONSE_CODE="200";
	public static final String STATUS_CODE="statusCode";
	public static final String MESSAGE="message";
	public static final String DATA_DESKEY = "lba952b3"; // 数据传输加密key
	public static final String DATA_MD5_JAMMER = "b2471dd81f48fe34";//md5干扰码
	public static final int CONNECTION_TIMEOUT = 1000 * 10; // 连接超时
	public static final int SO_TIMEOUT = 1000 * 30; // 数据传输超时设置
	public static final String Cookies = "cookies";// 存储cookies
	public static final String CACHE_ROOT = "/temp/";// 缓存父级目录
	public static final String CACHE_FILEDIR_PIC = CACHE_ROOT + "pic/";// 图片存储到的缓存目录
	/**
	 * 默认裁剪图片宽度高度及比例
	 */
	public static int aspectX = 1; 	// 比例
	public static int aspectY = 1;
	public static final int w = 180; //尺寸
	public static final int h = 180;

	/**
	 * 照片临时路径
	 */
	public static final String TEMPIMGFILE = "TEMPIMGFILE";

	/**
	 * 4.4以下(也就是kitkat以下)的版本
	 */
	public static final int KITKAT_LESS = 0;

	/**
	 * 4.4以上(也就是kitkat以上)的版本,当然也包括最新出的5.0棒棒糖
	 */
	public static final int KITKAT_ABOVE = 1;

}
