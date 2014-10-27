package com.skycober.mineral.network;

import com.skycober.mineral.bean.UserRec;

public class ResponseOauth extends BaseResponse {
	private UserRec userRec;

	public UserRec getUserRec() {
		return userRec;
	}

	public void setUserRec(UserRec userRec) {
		this.userRec = userRec;
	}

	@Override
	public String toString() {
		return "ResponseOauth [userRec=" + userRec + ", toString()="
				+ super.toString() + "]";
	}

}
