package com.skycober.mineral.bean;

/**
 * 用户对象模型
 * 
 * @author 新彬
 * 
 */
public class UserRec {
	
	public static final String ResponseUserId = "user_id";
	private String userId;
	public static final String ResponseEmail = "email";
	private String email;
	public static final String ResponseRealName = "real_name";
	private String realName;
	public static final String ResponseUserName = "user_name";
	private String userName;
	public static final String ResponseSex = "sex";
	private int sex;
	
	public static final String ResponseAttendNum = "attend_num";
	public String attendNum;
	public static final String ResponseBirthday = "birthday";
	private String birthday;
	public static final String ResponseRegTime = "reg_time";
	private long regTime;
	public static final String ResponseVisitCount = "visit_count";
	private long visitCount;
	public static final String ResponseMSN = "msn";
	private String msn;
	public static final String ResponseQQ = "qq";
	private String qq;
	public static final String ResponseAvatar = "avatar";
	private AvatarRec avatar;
	public static final String ResponseOfficePhone = "office_phone";
	private String officePhone;
	public static final String ResponseHomePhone = "home_phone";
	private String homePhone;
	public static final String ResponseMobilePhone = "mobile_phone";
	private String mobilePhone;
	public static final String ResponseIsValidated = "is_validated";
	private int isValidated;
	public static final String ResponseSignature = "signature";
	private String signature;
	public static final String ResponseUserRank = "user_rank";
	private String userRank;
	
	public static final String ResponseIsFollow = "is_follow";
	private boolean isFollowed;
	
	public static final String ResponseGOnSaleNum = "gOnSaleNum";
	/**
	 * 上架藏品数
	 */
	private long onSaleNum;
	
	public static final String ResponseGOffSaleNum = "gOffSaleNum";
	/**
	 * 下架藏品数
	 */
	private long offSaleNum;
	
	public static final String ResponseGDeleteSaleNum = "gDeleteNum";
	/**
	 * 删除藏品数
	 */
	private long deleteNum;
	
	public static final String ResponseGFavNum = "gFavNum";
	/**
	 * 收藏藏品数
	 */
	private long favNum;
	
	public long getOnSaleNum() {
		return onSaleNum;
	}

	public String getAttendNum() {
		return attendNum;
	}

	public void setAttendNum(String attendNum) {
		this.attendNum = attendNum;
	}

	public void setOnSaleNum(long onSaleNum) {
		this.onSaleNum = onSaleNum;
	}

	public long getOffSaleNum() {
		return offSaleNum;
	}

	public void setOffSaleNum(long offSaleNum) {
		this.offSaleNum = offSaleNum;
	}

	public long getDeleteNum() {
		return deleteNum;
	}

	public void setDeleteNum(long deleteNum) {
		this.deleteNum = deleteNum;
	}

	public long getFavNum() {
		return favNum;
	}

	public void setFavNum(long favNum) {
		this.favNum = favNum;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public long getRegTime() {
		return regTime;
	}

	public void setRegTime(long regTime) {
		this.regTime = regTime;
	}

	public long getVisitCount() {
		return visitCount;
	}

	public void setVisitCount(long visitCount) {
		this.visitCount = visitCount;
	}

	public String getMsn() {
		return msn;
	}

	public void setMsn(String msn) {
		this.msn = msn;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public AvatarRec getAvatar() {
		return avatar;
	}

	public void setAvatar(AvatarRec avatar) {
		this.avatar = avatar;
	}

	public String getOfficePhone() {
		return officePhone;
	}

	public void setOfficePhone(String officePhone) {
		this.officePhone = officePhone;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public int getIsValidated() {
		return isValidated;
	}

	public void setIsValidated(int isValidated) {
		this.isValidated = isValidated;
	}

	public String getUserRank() {
		return userRank;
	}

	public void setUserRank(String userRank) {
		this.userRank = userRank;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public boolean isFollowed() {
		return isFollowed;
	}

	public void setFollowed(boolean isFollowed) {
		this.isFollowed = isFollowed;
	}

	@Override
	public String toString() {
		return "UserRec [userId=" + userId + ", email=" + email + ", realName="
				+ realName + ", userName=" + userName + ", sex=" + sex
				+ ", birthday=" + birthday + ", regTime=" + regTime
				+ ", visitCount=" + visitCount + ", msn=" + msn + ", qq=" + qq
				+ ", avatar=" + avatar + ", officePhone=" + officePhone
				+ ", homePhone=" + homePhone + ", mobilePhone=" + mobilePhone
				+ ", isValidated=" + isValidated + ", signature=" + signature
				+ ", userRank=" + userRank + ", isFollowed=" + isFollowed
				+ ", onSaleNum=" + onSaleNum + ", offSaleNum=" + offSaleNum
				+ ", deleteNum=" + deleteNum + ", favNum=" + favNum + "]";
	}

}