package com.skycober.mineral.bean;

import java.io.Serializable;

public class TagCategoryRec implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4067084742661450326L;
	public static final String ResponseTagCatId = "tag_cat_id";
	private String tagCatID;
	public static final String ResponseTagCatName = "tag_cat_name";
	private String tagCatName;
	public static final String ResponseUseNum = "use_num";
	private String useNum;
	private boolean isChecked;
	private String sortLetters; // 显示数据拼音的首字母

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public String getTagCatID() {
		return tagCatID;
	}

	public void setTagCatID(String tagCatID) {
		this.tagCatID = tagCatID;
	}

	public String getTagCatName() {
		return tagCatName;
	}

	public void setTagCatName(String tagCatName) {
		this.tagCatName = tagCatName;
	}

	public String getUseNum() {
		return useNum;
	}

	public void setUseNum(String useNum) {
		this.useNum = useNum;
	}

	@Override
	public String toString() {
		return "TagCategoryRec [tagCatID=" + tagCatID + ", tagCatName="
				+ tagCatName + ", useNum=" + useNum + ", isChecked="
				+ isChecked + "]";
	}

}
