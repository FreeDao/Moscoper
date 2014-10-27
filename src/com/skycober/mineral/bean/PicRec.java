package com.skycober.mineral.bean;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Transient;

public class PicRec implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8105002143663506519L;
	@Transient
	public static final String ResponseImgId = "img_id";
	/**
	 * ID
	 */
	private String Id;
	
	public static final String ResponseGoodsId = "goods_id";
	/**
	 * ²ØÆ·ID
	 */
	private String goodsId;
	
	@Transient
	public static final String ResponseImgUrl = "img_url";
	/**
	 * url
	 */
	private String url;
	
	/**
	 * ±¾µØ´æ´¢Â·¾¶
	 */
	private String path;
	
	@Transient
	public static final String ResponseThumb = "thumb_url";
	/**
	 * thumb
	 */
	private String thumb;
	
	@Transient
	public static final String ResponseImgDescription = "img_desc";
	/**
	 * Í¼Æ¬ÃèÊö
	 */
	private String description;
	
	
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	public String getThumb() {
		return thumb;
	}
	public void setThumb(String thumb) {
		this.thumb = thumb;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	@Override
	public String toString() {
		return "PicRec [Id=" + Id + ", goodsId=" + goodsId + ", url=" + url
				+ ", path=" + path
				+ ", thumb=" + thumb + ", description=" + description + "]";
	}
}
