package com.skycober.mineral.bean;

public class AvatarRec {
	public static final String ResponseBig = "big";
	private String big;

	public static final String ResponseNormal = "normal";
	private String normal;

	public static final String ResponseSmall = "small";
	private String small;

	public String getBig() {
		return big;
	}

	public void setBig(String big) {
		this.big = big;
	}

	public String getNormal() {
		return normal;
	}

	public void setNormal(String normal) {
		this.normal = normal;
	}

	public String getSmall() {
		return small;
	}

	public void setSmall(String small) {
		this.small = small;
	}

	@Override
	public String toString() {
		return "AvatarRec [big=" + big + ", normal=" + normal + ", small="
				+ small + "]";
	}
}
