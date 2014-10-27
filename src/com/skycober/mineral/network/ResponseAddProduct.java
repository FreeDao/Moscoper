package com.skycober.mineral.network;

import com.skycober.mineral.bean.ProductRec;

public class ResponseAddProduct extends BaseResponse {
	private ProductRec productRec;

	public ProductRec getProductRec() {
		return productRec;
	}

	public void setProductRec(ProductRec productRec) {
		this.productRec = productRec;
	}
	
	
}
