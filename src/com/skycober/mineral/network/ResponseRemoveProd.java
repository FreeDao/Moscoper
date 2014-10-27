package com.skycober.mineral.network;

public class ResponseRemoveProd extends BaseResponse {
	public static final String ResponseProdId = "goods_id";
	private String prodId;
	public String getProdId() {
		return prodId;
	}
	public void setProdId(String prodId) {
		this.prodId = prodId;
	}
	@Override
	public String toString() {
		return "ResponseRemoveProd [prodId=" + prodId + ", toString()="
				+ super.toString() + "]";
	}
	
}
