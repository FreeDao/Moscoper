package com.skycober.mineral.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
/**
 * 网络相关工具类
 * @author Yes366
 *
 */
public class NetworkUtil {
	private static NetworkUtil instance;
	public static NetworkUtil getInstance(){
		if(null == instance) instance = new NetworkUtil();
		return instance;
	}
	
	/**
	 * decide current network whether to be enable.
	 * @param context
	 * @return
	 */
	public boolean existNetwork(Context context){
		boolean netStatus = false;
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(null != mConnectivityManager.getActiveNetworkInfo()){
			netStatus = mConnectivityManager.getActiveNetworkInfo().isAvailable();
		}
		return netStatus;
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public boolean isWifiForNetwork(Context context){
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		State state = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		return state == State.CONNECTED;
	}
}
