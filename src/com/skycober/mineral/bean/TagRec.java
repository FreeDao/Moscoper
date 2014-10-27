package com.skycober.mineral.bean;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Transient;

public class TagRec implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6802049338963245581L;
	@Transient
	public static final String ResponseId = "id";
	private String id;
	@Transient
	public static final String ResponseName = "name";
	private String name;
	@Transient
	public static final String ResponseWeight = "weight";
	private long weight;
	
	private String address;
	public static final String ResponseAddress="manu";
	
	
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getWeight() {
		return weight;
	}
	public void setWeight(long weight) {
		this.weight = weight;
	}
	@Override
	public String toString() {
		return "TagRec [id=" + id + ", name=" + name + ", weight=" + weight
				+ ", address=" + address + "]";
	}
	
	
}
