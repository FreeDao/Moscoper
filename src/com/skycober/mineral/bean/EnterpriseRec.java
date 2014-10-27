package com.skycober.mineral.bean;

public class EnterpriseRec {
	public final static String EID = "eid";

	public final static String ENAME = "ename";

	public final static String TAG_CAT_ID = "tag_cat_id";
	private String eid;
	private String ename;
	private String tagCatId;

	public String getEid() {
		return eid;
	}

	public void setEid(String eid) {
		this.eid = eid;
	}

	public String getEname() {
		return ename;
	}

	public void setEname(String ename) {
		this.ename = ename;
	}

	public String getTagCatId() {
		return tagCatId;
	}

	public void setTagCatId(String tagCatId) {
		this.tagCatId = tagCatId;
	}
}
