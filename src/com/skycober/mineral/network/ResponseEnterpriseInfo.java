package com.skycober.mineral.network;
import com.skycober.mineral.bean.EnterpriseItemRec;

public class ResponseEnterpriseInfo extends BaseResponse {
	private EnterpriseItemRec enterpriseInfo;

	public EnterpriseItemRec getEnterpriseInfo() {
		return enterpriseInfo;
	}

	public void setEnterpriseInfo(EnterpriseItemRec enterpriseInfo) {
		this.enterpriseInfo = enterpriseInfo;
	}
	
}
