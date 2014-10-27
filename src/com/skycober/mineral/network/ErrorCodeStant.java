package com.skycober.mineral.network;



public class ErrorCodeStant {
	private static ErrorCodeStant instance;
	public static ErrorCodeStant getInstance(){
		if(null == instance) instance = new ErrorCodeStant();
		return instance;
	}
	
	public static final int ErrorCodeSucceed = 1;
	public static final int ErrorCodeForServerBusy = -1;
	public static final int ErrorCodeForJsonParseError = -2;
	
	
	public boolean isSucceed(int errorCode){
		return errorCode == ErrorCodeSucceed;
	}
}
