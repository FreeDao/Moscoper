package com.skycober.mineral.network;

public class ResponseGetMatchNum extends BaseResponse {
	public static final String ResponseMatchNum = "match_num";
	private String matchNum;
	public String getMatchNum() {
		return matchNum;
	}
	public void setMatchNum(String matchNum) {
		this.matchNum = matchNum;
	}
}
