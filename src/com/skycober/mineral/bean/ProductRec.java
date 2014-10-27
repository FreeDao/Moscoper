package com.skycober.mineral.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.annotation.sqlite.Transient;

/**
 * 藏品对象模型
 * 
 * @author 新彬
 * 
 */
public class ProductRec implements Serializable {

	private boolean isCheck;//是否被选中
	private boolean isVisible;//是否显示
	private String in_black;
	
	

	
	
	public String getIn_black() {
		return in_black;
	}

	public void setIn_black(String in_black) {
		this.in_black = in_black;
	}

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	private static final long serialVersionUID = 2166575876117540363L;
	public static final String ResponseInBlack = "in_black";
	@Transient
	public static final String ResponseRealName = "real_name";
	private String RealName;

	@Transient
	public static final String ResponseGoodsId = "goods_id";
	/**
	 * ID
	 */
	private String id;
	
	
	@Transient
	public static final String ResponseProId = "pro_id";
	/**
	 * 省份ID
	 */
	private String proId;
	
	@Transient
	public static final String ResponseProName = "pro_name";
	/**
	 * 省份name
	 */
	private String proName;
	
	@Transient
	public static final String ResponseCityId = "city_id";
	/**
	 * 城市ID
	 */
	private String CityId;
	
	@Transient
	public static final String ResponseCityName = "city_name";
	/**
	 * 城市name
	 */
	private String CityName;
	
	@Transient
	public static final String ResponseIsFav = "in_fav";
	/**
	 * 是否收藏
	 */
	private boolean inFav;

	@Transient
	public static final String ResponseUserId = "user_id";
	/**
	 * 用户ID
	 */
	private String userId;
	
	@Transient
	public static final String ResponseUserIsBlack= "in_black";
	/**
	 * 是否屏蔽
	 */
	private boolean isBlackUser;

	@Transient
	public static final String ResponseTagCatId = "tag_cat_id";
	/**
	 * 新分类ID
	 */
	private String tagCatId;

	@Transient
	public static final String ResponseTagCatName = "tag_cat_name";
	/**
	 * 新分类名
	 */
	private String tagCatName;
	@Transient
	public static final String ResponseTags = "tags";
	/**
	 * 新分类名
	 */
	private List<KeyWordsRec> tags;

	@Transient
	public static final String ResponseName = "goods_name";
	/**
	 * 名称
	 */
	private String name;

	@Transient
	public static final String ResponseViewNum = "view_num";
	/**
	 * 点击次数
	 */
	private long viewNum;
	@Transient
	public static final String ResponseCollectUserNum = "collect_user_num";
	/**
	 * 收藏次数
	 */
	private long collectUserNum;
	
	@Transient
	public static final String ResponseWarnNumber = "warn_number";
	/**
	 * TODO Check
	 */
	private String warnNumber;

	@Transient
	public static final String ResponseGoodsDescription = "goods_desc";
	/**
	 * 产品描述
	 */
	private String description;

	@Transient
	public static final String ResponseGoodsThumb = "goods_thumb";
	/**
	 * 产品缩略图
	 */
	private String thumb;

	@Transient
	public static final String ResponseGoodsImg = "goods_img";
	/**
	 * 产品原图(展示用)
	 */
	private String img;

	@Transient
	public static final String ResponseOriginalImg = "original_img";
	/**
	 * 产品原图（未处理）
	 */
	private String originalImg;
	
	
	@Transient
	public static final String ResponseIsView = "is_view";
	/**
	 * 产品原图（未处理）
	 */
	private String IsView;
	

	@Transient
	public static final String ResponseAddTime = "add_time";
	/**
	 * 添加时间
	 */
	private long addTime;
	
	@Transient
	public static final String ResponseGallery = "photos";
	/**
	 * 产品相册
	 */
	private List<PicRec> picList;

	public static final String START_TIME = "start_time";
	public static final String END_TIME = "end_time";
	public static final String PRAISE_NUM = "praise_num";
	public static final String BLACK_NUM = "black_num";
	private long start_time;//信息有效开始时间
	private long end_time;//信息有效结束时间
	private String praise_num;//被赞的人数
	private String black_num;//被屏蔽的人数
	
	
	
	public long getStart_time() {
		return start_time;
	}

	public void setStart_time(long start_time) {
		this.start_time = start_time;
	}

	public long getEnd_time() {
		return end_time;
	}

	public void setEnd_time(long end_time) {
		this.end_time = end_time;
	}

	public String getPraise_num() {
		return praise_num;
	}

	public void setPraise_num(String praise_num) {
		this.praise_num = praise_num;
	}

	public String getBlack_num() {
		return black_num;
	}

	public void setBlack_num(String black_num) {
		this.black_num = black_num;
	}

	public String getIsView() {
		return IsView;
	}

	public void setIsView(String isView) {
		IsView = isView;
	}

	public String getRealName() {
		return RealName;
	}

	public void setRealName(String realName) {
		RealName = realName;
	}

	public boolean isBlackUser() {
		return isBlackUser;
	}

	public void setBlackUser(boolean isBlackUser) {
		this.isBlackUser = isBlackUser;
	}
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isInFav() {
		return inFav;
	}

	public void setInFav(boolean inFav) {
		this.inFav = inFav;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTagCatId() {
		return tagCatId;
	}

	public void setTagCatId(String tagCatId) {
		this.tagCatId = tagCatId;
	}

	public String getTagCatName() {
		return tagCatName;
	}

	public void setTagCatName(String tagCatName) {
		this.tagCatName = tagCatName;
	}



	public List<KeyWordsRec> getTags() {
		return tags;
	}

	public void setTags(List<KeyWordsRec> tags) {
		this.tags = tags;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	
	public long getViewNum() {
		return viewNum;
	}

	public void setViewNum(long viewNum) {
		this.viewNum = viewNum;
	}

	public String getWarnNumber() {
		return warnNumber;
	}

	public void setWarnNumber(String warnNumber) {
		this.warnNumber = warnNumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getThumb() {
		return thumb;
	}

	public void setThumb(String thumb) {
		this.thumb = thumb;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getOriginalImg() {
		return originalImg;
	}

	public void setOriginalImg(String originalImg) {
		this.originalImg = originalImg;
	}

	public long getAddTime() {
		return addTime;
	}

	public void setAddTime(long addTime) {
		this.addTime = addTime;
	}

	public long getCollectUserNum() {
		return collectUserNum;
	}

	public void setCollectUserNum(long collectUserNum) {
		this.collectUserNum = collectUserNum;
	}

	public List<PicRec> getPicList() {
		return picList;
	}

	public void setPicList(List<PicRec> picList) {
		this.picList = picList;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getProId() {
		return proId;
	}

	public void setProId(String proId) {
		this.proId = proId;
	}

	public String getProName() {
		return proName;
	}

	public void setProName(String proName) {
		this.proName = proName;
	}

	public String getCityId() {
		return CityId;
	}

	public void setCityId(String cityId) {
		CityId = cityId;
	}

	public String getCityName() {
		return CityName;
	}

	public void setCityName(String cityName) {
		CityName = cityName;
	}

	@Override
	public String toString() {
		return "ProductRec [isCheck=" + isCheck + ", isVisible=" + isVisible
				+ ", in_black=" + in_black + ", RealName=" + RealName + ", id="
				+ id + ", proId=" + proId + ", proName=" + proName
				+ ", CityId=" + CityId + ", CityName=" + CityName + ", inFav="
				+ inFav + ", userId=" + userId + ", isBlackUser=" + isBlackUser
				+ ", tagCatId=" + tagCatId + ", tagCatName=" + tagCatName
				+ ", tags=" + tags + ", name=" + name + ", viewNum=" + viewNum
				+ ", collectUserNum=" + collectUserNum + ", warnNumber="
				+ warnNumber + ", description=" + description + ", thumb="
				+ thumb + ", img=" + img + ", originalImg=" + originalImg
				+ ", IsView=" + IsView + ", addTime=" + addTime + ", picList="
				+ picList + ", start_time=" + start_time + ", end_time="
				+ end_time + ", praise_num=" + praise_num + ", black_num="
				+ black_num + "]";
	}

	
	
	
}
