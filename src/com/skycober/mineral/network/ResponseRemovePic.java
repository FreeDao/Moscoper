package com.skycober.mineral.network;

public class ResponseRemovePic extends BaseResponse {
	public static final String ResponsePicId = "img_id";
	private String picId;

	public String getPicId() {
		return picId;
	}

	public void setPicId(String picId) {
		this.picId = picId;
	}

	@Override
	public String toString() {
		return "ResponseRemovePic [picId=" + picId + ", toString()="
				+ super.toString() + "]";
	}

}
