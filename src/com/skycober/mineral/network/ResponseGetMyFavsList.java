package com.skycober.mineral.network;

import java.util.List;

import com.skycober.mineral.bean.ProductRec;

public class ResponseGetMyFavsList extends BaseResponse{
	private List<ProductRec> productRecs;
	public List<ProductRec> getProductRecs() {
		return productRecs;
	}

	public void setProductRecs(List<ProductRec> productRecs) {
		this.productRecs = productRecs;
	}

	@Override
	public String toString() {
		return "ResponseGetMyFavsList [productRecs=" + productRecs
				+ ", toString()=" + super.toString() + "]";
	}

}
