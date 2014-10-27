package com.skycober.mineral.network;

import com.skycober.mineral.bean.AvatarRec;


public class ResponseUploadAvatar extends BaseResponse {
	public static final String ResponseAvatars = "avatars";
	private AvatarRec avatarRec;

	public AvatarRec getAvatarRec() {
		return avatarRec;
	}

	public void setAvatarRec(AvatarRec avatarRec) {
		this.avatarRec = avatarRec;
	}

	@Override
	public String toString() {
		return "ResponseUploadAvatar [avatarRec=" + avatarRec + ", toString()="
				+ super.toString() + "]";
	}
	
}
