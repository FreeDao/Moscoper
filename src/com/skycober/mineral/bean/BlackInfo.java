package com.skycober.mineral.bean;

public class BlackInfo {
	private String id;
	private String mid;
	private String iseid;
	private String eid;
	private String name;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getIseid() {
		return iseid;
	}
	public void setIseid(String iseid) {
		this.iseid = iseid;
	}
	public String getEid() {
		return eid;
	}
	public void setEid(String eid) {
		this.eid = eid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "BlackInfo [id=" + id + ", mid=" + mid + ", iseid=" + iseid
				+ ", eid=" + eid + ", name=" + name + "]";
	}
	
	
}
