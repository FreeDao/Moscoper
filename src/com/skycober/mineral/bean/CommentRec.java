package com.skycober.mineral.bean;

public class CommentRec {
	public static final String ResponseCommentId = "comment_id";
	/**
	 * ����Id
	 */
	private String Id;
	
	public static final String ResponseCommentType = "comment_type";
	/**
	 * ��������
	 */
	private String commentType;
	
	public static final String ResponseProdId = "id_value";
	/**
	 * ��ƷID
	 */
	private String prodId;
	
	public static final String ResponseEmail = "email";
	/**
	 * Email
	 */
	private String email;
	
	public static final String ResponseUserName = "user_name"; 
	/**
	 * �û���
	 */
	private String userName;
	
	public static final String ResponseContent = "content";
	/**
	 * ��������
	 */
	private String content;
	
	public static final String ResponseCommentRank = "comment_rank";
	/**
	 * ����
	 */
	private int rank;
	
	public static final String ResponseAddTime = "add_time";
	/**
	 * ����ʱ��
	 */
	private long sendTime;
	
	public static final String ResponseAvatar = "avatar";
	/**
	 * ͷ��Url
	 */
	private AvatarRec avatar;
	
	public static final String ResponseStatus = "status";
	/**
	 * �Ƿ�ͨ�����
	 */
	private boolean isChecked;
	
	public static final String ResponseParentId = "parent_id";
	/**
	 * ��ID
	 */
	private String parentId;
	
	public static final String ResponseUserId = "user_id";
	/**
	 * ������ID
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
