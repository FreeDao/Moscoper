package com.skycober.mineral.company;

import java.util.List;

public class TagResult {
	private List<CatgerTag> done_tag_list ;
	private List<CatgerTag> donot_tag_list;
	public List<CatgerTag> getDone_tag_list() {
		return done_tag_list;
	}
	public void setDone_tag_list(List<CatgerTag> done_tag_list) {
		this.done_tag_list = done_tag_list;
	}
	public List<CatgerTag> getDonot_tag_list() {
		return donot_tag_list;
	}
	public void setDonot_tag_list(List<CatgerTag> donot_tag_list) {
		this.donot_tag_list = donot_tag_list;
	}
	@Override
	public String toString() {
		return "TagResult [done_tag_list=" + done_tag_list
				+ ", donot_tag_list=" + donot_tag_list + "]";
	}
	
	
	
}
