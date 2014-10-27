package com.skycober.mineral.network;

public class ResponseRemoveTag extends BaseResponse {
	
	public static final String ResponseFollowId = "follow_id";
	private String followId;
	public String getFollowId() {
		return followId;
	}
	public void setFollowId(String followId) {
		this.followId = followId;
	}
	@Override
	public String toString() {
		return "ResponseRemoveTag [followId=" + followId + ", toString()="
				+ super.toString() + "]";
	}
	
}
