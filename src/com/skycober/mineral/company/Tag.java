package com.skycober.mineral.company;

public class Tag {
	private String tag_id;
	private String tag_name;
	private boolean isChecked = false;
	
	public String getTag_id() {
		return tag_id;
	}
	public void setTag_id(String tag_id) {
		this.tag_id = tag_id;
	}
	public String getTag_name() {
		return tag_name;
	}
	public void setTag_name(String tag_name) {
		this.tag_name = tag_name;
	}
	
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	@Override
	public String toString() {
		return "Tag [tag_id=" + tag_id + ", tag_name=" + tag_name
				+ ", isChecked=" + isChecked + "]";
	}
	
	
	
	
}
