package com.skycober.mineral.network;

public class ResponseMakeSale extends BaseResponse {
public static final String ResponseId = "goods_id";
	
	private String Id;

	public String getId() {
		return Id;
	}

	public void setId(String Id) {
		this.Id = Id;
	}

	@Override
	public String toString() {
		return "ResponseFavPost [Id=" + Id + ", toString()="
				+ super.toString() + "]";
	}
}
