package com.skycober.mineral.network;

import java.util.List;

import com.skycober.mineral.bean.CategoryRec;
import com.skycober.mineral.bean.UserRec;

public class ResponseCategory extends BaseResponse {
	private List<CategoryRec> categoryRecList;

	public List<CategoryRec> getCategoryRec() {
		return categoryRecList;
	}

	public void setCategoryRec(List<CategoryRec> categoryRecList) {
		this.categoryRecList = categoryRecList;
	}

}
