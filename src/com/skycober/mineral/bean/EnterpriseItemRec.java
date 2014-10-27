package com.skycober.mineral.bean;

public class EnterpriseItemRec {
	public static final String EID = "eid";

	public static final String ENAME = "ename";

	public static final String INDUSTRY = "industry";

	public static final String ELOGO = "elogo";
	private String eid;
	private String ename;
	private String industry;
	private String elogo;

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

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getElogo() {
		return elogo;
	}

	public void setElogo(String elogo) {
		this.elogo = elogo;
	}

}
