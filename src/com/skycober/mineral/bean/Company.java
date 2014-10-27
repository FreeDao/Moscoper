package com.skycober.mineral.bean;

public class Company {
	private String ename;
	private String elogo;
	private String eid;
	private int pictureId;
	
	
	public String getEname() {
		return ename;
	}
	public void setEname(String ename) {
		this.ename = ename;
	}
	public String getElogo() {
		return elogo;
	}
	public void setElogo(String elogo) {
		this.elogo = elogo;
	}
	public String getEid() {
		return eid;
	}
	public void setEid(String eid) {
		this.eid = eid;
	}
	public int getPictureId() {
		return pictureId;
	}
	public void setPictureId(int pictureId) {
		this.pictureId = pictureId;
	}
	@Override
	public String toString() {
		return "Company [ename=" + ename + ", elogo=" + elogo + ", eid=" + eid
				+ ", pictureId=" + pictureId + "]";
	}
	
	
	
	
}
