package com.skycober.mineral.util;


import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
/**
 * 定位工具类
 * @author Yes366
 *
 */
public class LocationUtil {

	public LocationClient mLocationClient = null;
	private Context context;
	private BDLocationListener bdLocationListener;

	public LocationUtil(Context context, BDLocationListener bdLocationListener) {
		this.context = context;
		this.bdLocationListener = bdLocationListener;
		initLocation();
	}

	private void initLocation() {
		mLocationClient = new LocationClient(context.getApplicationContext()); //
		mLocationClient.registerLocationListener(bdLocationListener); //
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");//
		option.setCoorType("bd09ll");//
		option.setScanSpan(15000);//
		option.disableCache(true);//
		mLocationClient.setLocOption(option);
	}

	// 开始定位
	public void startLocation() {
		mLocationClient.start();
		if (mLocationClient != null && mLocationClient.isStarted())
			mLocationClient.requestLocation();
		else
			Log.d("LocSDK3", "locClient is null or not started");
	}

	// 结束定位
	public void stopLocation() {
		mLocationClient.stop();
	}
	
	//再次定位
	public void reStartLocation(){
		if (mLocationClient != null && mLocationClient.isStarted())
			mLocationClient.requestLocation();
		else
			Log.d("LocSDK3", "locClient is null or not started");
	}

	public String getCityInfo(BDLocation location) {
		if (null != location) {
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
			}
			String city = location.getCity();
			String addrress = location.getAddrStr();

			return city;
		} else {
			return null;
		}
	}
	/**
	 * 获取乡县
	 * @param location
	 * @return
	 */
	public String getDistrict(BDLocation location){
		return location.getDistrict();
	}
	
	/**
	 * 获取经纬度
	 * @param location
	 * @return
	 */
	
	public String getLaAndLo(BDLocation location){
		return location.getLongitude()+","+location.getLatitude();
	}
	/**
	 * 获取省
	 * @param location
	 * @return
	 */
	public String getProvince(BDLocation location){
		String str=location.toString();
		return location.getProvince();
	}
	
	/**
	 * 获取街道
	 * @param location
	 * @return
	 */
	public String getStreet(BDLocation location){
		return location.getStreet();
	}
	/**
	 * 获取地详情
	 * @param location
	 * @return
	 */
	public String getAddrStr(BDLocation location){
		return location.getAddrStr();
	}
}
