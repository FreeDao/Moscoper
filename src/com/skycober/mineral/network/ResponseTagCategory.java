package com.skycober.mineral.network;

import java.util.List;

import com.skycober.mineral.bean.KeyWordsRec;
import com.skycober.mineral.bean.TagCategoryRec;

public class ResponseTagCategory extends BaseResponse {
	private List<TagCategoryRec> list;
	private String ishavekey;
	
	
	
	public String getIshavekey() {
		return ishavekey;
	}
	public void setIshavekey(String ishavekey) {
		this.ishavekey = ishavekey;
	}
	public List<TagCategoryRec> getList() {
		return list;
	}
	public void setList(List<TagCategoryRec> list) {
		this.list = list;
	}
	@Override
	public String toString() {
		return "ResponseTagCategory [list=" + list + ", ishavekey=" + ishavekey
				+ "]";
	}
	
	
	
	
	
}
