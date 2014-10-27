package com.skycober.mineral.network;

import com.skycober.mineral.bean.TagRec;

public class ResponseAddTag extends BaseResponse {
	private TagRec tagRec;

	public TagRec getTagRec() {
		return tagRec;
	}

	public void setTagRec(TagRec tagRec) {
		this.tagRec = tagRec;
	}

	@Override
	public String toString() {
		return "ResponseAddTag [tagRec=" + tagRec + ", toString()="
				+ super.toString() + "]";
	}
	
}
