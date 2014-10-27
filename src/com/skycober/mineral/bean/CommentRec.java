package com.skycober.mineral.bean;

public class CommentRec {
	public static final String ResponseCommentId = "comment_id";
	/**
	 * 评论Id
	 */
	private String Id;
	
	public static final String ResponseCommentType = "comment_type";
	/**
	 * 评论类型
	 */
	private String commentType;
	
	public static final String ResponseProdId = "id_value";
	/**
	 * 藏品ID
	 */
	private String prodId;
	
	public static final String ResponseEmail = "email";
	/**
	 * Email
	 */
	private String email;
	
	public static final String ResponseUserName = "user_name"; 
	/**
	 * 用户名
	 */
	private String userName;
	
	public static final String ResponseContent = "content";
	/**
	 * 评论内容
	 */
	private String content;
	
	public static final String ResponseCommentRank = "comment_rank";
	/**
	 * 评分
	 */
	private int rank;
	
	public static final String ResponseAddTime = "add_time";
	/**
	 * 发送时间
	 */
	private long sendTime;
	
	public static final String ResponseAvatar = "avatar";
	/**
	 * 头像Url
	 */
	private AvatarRec avatar;
	
	public static final String ResponseStatus = "status";
	/**
	 * 是否通过审核
	 */
	private boolean isChecked;
	
	public static final String ResponseParentId = "parent_id";
	/**
	 * 父ID
	 */
	private String parentId;
	
	public static final String ResponseUserId = "user_id";
	/**
	 * 发送者ID
	 */
	private String userId;
	
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public long getSendTime() {
		return sendTime;
	}
	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}
	public String getCommentType() {
		return commentType;
	}
	public void setCommentType(String commentType) {
		this.commentType = commentType;
	}
	public String getProdId() {
		return prodId;
	}
	public void setProdId(String prodId) {
		this.prodId = prodId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public AvatarRec getAvatar() {
		return avatar;
	}
	public void setAvatar(AvatarRec avatar) {
		this.avatar = avatar;
	}
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	@Override
	public String toString() {
		return "CommentRec [Id=" + Id + ", commentType=" + commentType
				+ ", prodId=" + prodId + ", email=" + email + ", userName="
				+ userName + ", content=" + content + ", rank=" + rank
				+ ", sendTime=" + sendTime + ", avatar=" + avatar
				+ ", isChecked=" + isChecked + ", parentId=" + parentId
				+ ", userId=" + userId + "]";
	}
	
}
