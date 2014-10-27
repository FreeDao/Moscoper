package com.skycober.mineral.network;

import java.util.List;

import com.skycober.mineral.bean.CategoryRec;
import com.skycober.mineral.bean.TagRec;
import com.skycober.mineral.bean.UserRec;

public class ResponseGetAttentionTag extends BaseResponse {
	
	public static final String ResponseUser = "user";
	private List<UserRec> userRecList;
	
	public static final String ResponseCat = "cat";
	private List<CategoryRec> categoryRecList;
	
	public static final String ResponseTag = "tag";
	private List<TagRec> tagRecList;
	public List<CategoryRec> getCategoryRecList() {
		return categoryRecList;
	}
	public void setCategoryRecList(List<CategoryRec> categoryRecList) {
		this.categoryRecList = categoryRecList;
	}
	public List<TagRec> getTagRecList() {
		return tagRecList;
	}
	public void setTagRecList(List<TagRec> tagRecList) {
		this.tagRecList = tagRecList;
	}
	public List<UserRec> getUserRecList() {
		return userRecList;
	}
	public void setUserRecList(List<UserRec> userRecList) {
		this.userRecList = userRecList;
	}
	@Override
	public String toString() {
		return "ResponseGetAttentionTag [userRecList=" + userRecList
				+ ", categoryRecList=" + categoryRecList + ", tagRecList="
				+ tagRecList + ", toString()=" + super.toString() + "]";
	}
	
}
