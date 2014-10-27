package com.skycober.mineral.network;


import com.skycober.mineral.bean.ProductRec;

public class ResponseGetProductDetail extends BaseResponse{
	private ProductRec productRec;
	public ProductRec getProductRec() {
		return productRec;
	}

	public void setProductRec(ProductRec productRec) {
		this.productRec = productRec;
	}

	@Override
	public String toString() {
		return "ResponseGetProductDetail [productRec=" + productRec
				+ ", toString()=" + super.toString() + "]";
	}

}
