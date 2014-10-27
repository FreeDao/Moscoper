package com.skycober.mineral.network;

import com.skycober.mineral.bean.PicRec;

public class ResponseUploadPic extends BaseResponse {
	private PicRec picRec;

	public PicRec getPicRec() {
		return picRec;
	}

	public void setPicRec(PicRec picRec) {
		this.picRec = picRec;
	}

	@Override
	public String toString() {
		return "ResponseUploadPic [picRec=" + picRec + ", toString()="
				+ super.toString() + "]";
	}
	
}
