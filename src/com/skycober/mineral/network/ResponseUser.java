package com.skycober.mineral.network;

import com.skycober.mineral.bean.UserRec;

public class ResponseUser extends BaseResponse{
	private UserRec userRec;

	public UserRec getUserRec() {
		return userRec;
	}

	public void setUserRec(UserRec userRec) {
		this.userRec = userRec;
	}

	@Override
	public String toString() {
		return "ResponseRegistUser [userRec=" + userRec + ", toString()="
				+ super.toString() + "]";
	}

}
