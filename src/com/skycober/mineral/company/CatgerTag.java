package com.skycober.mineral.company;

import java.util.List;

/**
 * 标签分类
 * @author CF
 *
 */
public class CatgerTag {
	private String e_cat_id;//标签分类id
	private String e_cat_name;//分类名称
	private List<Tag> tag_list;
	
	public String getE_cat_id() {
		return e_cat_id;
	}
	public void setE_cat_id(String e_cat_id) {
		this.e_cat_id = e_cat_id;
	}
	public String getE_cat_name() {
		return e_cat_name;
	}
	public void setE_cat_name(String e_cat_name) {
		this.e_cat_name = e_cat_name;
	}
	public List<Tag> getTag_list() {
		return tag_list;
	}
	public void setTag_list(List<Tag> tag_list) {
		this.tag_list = tag_list;
	}
	@Override
	public String toString() {
		return "CatgerTag [e_cat_id=" + e_cat_id + ", e_cat_name=" + e_cat_name
				+ ", tag_list=" + tag_list + "]";
	}

	
	
}
