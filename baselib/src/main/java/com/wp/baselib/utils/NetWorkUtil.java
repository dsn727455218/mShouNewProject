package com.wp.baselib.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;

import static java.net.Proxy.Type.HTTP;

/**
 * 网络连接及网络数据传输处理工具类
 * 
 * @author summer
 */
public class NetWorkUtil {
	 Proxy mProxy = null;
	 Context context;

	public NetWorkUtil(Context context){
		this.context = context;
		
	}
	/**
	 * 检查代理，是否cnwap接入
	 */
	private  void detectProxy() {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null && ni.isAvailable()
				&& ni.getType() == ConnectivityManager.TYPE_MOBILE) {
			String proxyHost = android.net.Proxy.getDefaultHost();
			int port = android.net.Proxy.getDefaultPort();
			if (proxyHost != null) {
				final InetSocketAddress sa = new InetSocketAddress(proxyHost,
						port);
				mProxy = new Proxy(HTTP, sa);
			}
		}
	}





	/**
	 * 判断GPS是否可用
	 * @param context
	 * @return
	 */
	public static boolean isGpsEnabled(Context context) {
		LocationManager locationManager = ((LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE));
		List<String> accessibleProviders = locationManager.getProviders(true);
		return accessibleProviders != null && accessibleProviders.size() > 0;
	}

	/**
	 * 判断网络是否联接
	 * @param context
	 * @return
	 */
	public static boolean checkNetworkConnected(Context context) {
		return checkNetworkConnected(context,false);
	}
	
	/**
	 * 判断网络是否联接
	 * @param context
	 * @param isShowDialog 是否显示网络设置框
	 * @return
	 */
	public static boolean checkNetworkConnected(Context context, boolean isShowDialog) {
		boolean isConnect = false;
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = cm.getActiveNetworkInfo();
		if (network != null && network.isConnected()) {
			isConnect = true;
		} else {
			if(isShowDialog)
				UiUtil.showNoNetworkDialog(context);
			isConnect = false;
		}
		return isConnect;
	}

	/**
	 * 
	 * 获取wifi bssid 列表
	 * 
	 * @param context
	 * @return String
	 */
	public static String getNetWorkSSID(Context context) {
		String str = "";
		WifiManager wifiMan = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (wifiMan.isWifiEnabled()) {
			WifiInfo info = wifiMan.getConnectionInfo();
			str = info.getBSSID();
		}
		return str;
	}
}