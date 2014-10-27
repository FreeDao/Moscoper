package com.skycober.mineral.network;

import java.util.List;

import com.skycober.mineral.bean.KeyWordsRec;

public class ResponseKeyWords extends BaseResponse {
	private List<KeyWordsRec> list;
	private String ishavekey;

	public String getIshavekey() {
		return ishavekey;
	}

	public void setIshavekey(String ishavekey) {
		this.ishavekey = ishavekey;
	}

	public List<KeyWordsRec> getList() {
		return list;
	}

	public void setList(List<KeyWordsRec> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "ResponseKeyWords [list=" + list + ", ishavekey=" + ishavekey
				+ "]";
	}

}
