package com.skycober.mineral.bean;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Transient;

public class CategoryRec implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7091261614976756640L;
	@Transient
	public static final String ResponseCatId = "cat_id";
	private String id;
	@Transient
	public static final String ResponseCatName = "cat_name";
	private String name;
	@Transient
	public static final String ResponseParentId = "parent_id";
	private String parentId;
	
	@Transient
	public static final String ResponseIsFollow = "is_follow";
	private boolean isFollowed;
	
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

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	public boolean isFollowed() {
		return isFollowed;
	}

	public void setFollowed(boolean isFollowed) {
		this.isFollowed = isFollowed;
	}

	@Override
	public String toString() {
		return "CategoryRec [id=" + id + ", name=" + name + ", parentId="
				+ parentId + ", isFollowed=" + isFollowed + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((parentId == null) ? 0 : parentId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CategoryRec other = (CategoryRec) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parentId == null) {
			if (other.parentId != null)
				return false;
		} else if (!parentId.equals(other.parentId))
			return false;
		return true;
	}
	
}
