package com.skycober.mineral.network;

import com.skycober.mineral.bean.ProductRec;

public class ResponseGetSingleProduct extends BaseResponse {
	private ProductRec productRec;

	public ProductRec getProductRec() {
		return productRec;
	}

	public void setProductRec(ProductRec productRec) {
		this.productRec = productRec;
	}

	@Override
	public String toString() {
		return "ResponseGetSingleProduct [productRec=" + productRec
				+ ", toString()=" + super.toString() + "]";
	}
	
}
