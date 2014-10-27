package com.skycober.mineral.updateApk;


public class ApkInfo {
	public static final String URL = "url";
	public static final String APKRESULT = "Result";
	public static final String ID = "id";
	private String url;//新版本下载地址
	private int id;//Apk版本号
	
	public ApkInfo(){}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "ApkInfo [url=" + url + ", id=" + id + "]";
	}
	
	
	
	
}
