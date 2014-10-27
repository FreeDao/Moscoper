package com.skycober.mineral.network;

import java.util.List;

import com.skycober.mineral.bean.CategoryRec;
import com.skycober.mineral.bean.UserRec;

public class ResponsePraiseAndBlack extends BaseResponse {
	public static final String KEY_USER_ID="user_id";
	public static final String KEY_B_ID="bid";
	private String userId;
	private String bId;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getbId() {
		return bId;
	}
	public void setbId(String bId) {
		this.bId = bId;
	}




}
