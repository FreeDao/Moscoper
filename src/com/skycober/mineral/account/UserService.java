package com.skycober.mineral.account;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.content.Context;
import android.util.Log;

import com.skycober.mineral.constant.RequestUrls;
import com.skycober.mineral.util.SettingUtil;
import com.skycober.mineral.util.StringUtil;
import com.skycober.mineral.util.Trace;
import com.skycober.mineral.util.Util;
/**
 * 个人信息的http
 * @author Yes366
 *
 */
public class UserService {
	
	private static final String TAG = "UserService";
	private static final String GetUserInfoParamUserID = "[user_id]";

	/**
	 * 获取个人信息
	 * 
	 * @param context
	 * @param userId
	 * @param callBack
	 */
	public void GetUserInfo(Context context, String userId,
			AjaxCallBack<Object> callBack) {
		final String currMethod = "GetUserInfo:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL + RequestUrls.GET_USER_INFO;
		url = url.replace(GetUserInfoParamUserID, userId);
		Trace.e(TAG, currMethod + "url->" + url);
//		boolean hasLogin = !SettingUtil.getInstance(context).getValue(SettingUtil.KEY_LOGIN_USER_ID, SettingUtil.DEFAULT_LOGIN_USER_ID).equalsIgnoreCase(SettingUtil.DEFAULT_LOGIN_USER_ID);
//		if(hasLogin){
			String cookie = SettingUtil.getInstance(context).getValue(
					SettingUtil.KEY_COOKIE, null);
			if (null != cookie && cookie.length() > 0) {
				fh.addHeader("cookie", cookie);
			}
//		}
		fh.get(url, callBack);
	}

	private static final String updateUserInfoParamEmail = "email";
	private static final String updateUserInfoParamRealName = "real_name";
	private static final String updateUserInfoParamSex = "sex";
	private static final String updateUserInfoParamBirthday = "birthday";
	private static final String updateUserInfoParamMsn = "msn";
	private static final String updateUserInfoParamQQ = "qq";
	private static final String updateUserInfoParamOfficePhone = "office_phone";
	private static final String updateUserInfoParamHomePhone = "home_phone";
	private static final String updateUserInfoParamMobilePhone = "mobile_phone";

	/**
	 * 修改个人信息
	 * 
	 * @param context
	 * @param email
	 * @param realName
	 * @param sex
	 * @param birthday
	 * @param msn
	 * @param QQ
	 * @param officePhone
	 * @param homePhone
	 * @param mobilePhone
	 * @param callBack
	 */
	public void updateUserInfo(Context context, String email, String realName,
			String sex, String birthday, String msn, String QQ,
			String officePhone, String homePhone, String mobilePhone,
			AjaxCallBack<Object> callBack) {
		final String currMethod = "updateUserInfo:";
		AjaxParams params = new AjaxParams();
		if (!StringUtil.getInstance().IsEmpty(email)) {
			params.put(updateUserInfoParamEmail, email);
		}
		if (!StringUtil.getInstance().IsEmpty(realName)) {
			params.put(updateUserInfoParamRealName, realName);
		}
		if (!StringUtil.getInstance().IsEmpty(sex)) {
			params.put(updateUserInfoParamSex, sex);
		}
		if (!StringUtil.getInstance().IsEmpty(birthday)) {
			params.put(updateUserInfoParamBirthday, birthday);
		}
		if (!StringUtil.getInstance().IsEmpty(msn)) {
			params.put(updateUserInfoParamMsn, msn);
		}
		if (!StringUtil.getInstance().IsEmpty(QQ)) {
			params.put(updateUserInfoParamQQ, QQ);
		}
		if (!StringUtil.getInstance().IsEmpty(officePhone)) {
			params.put(updateUserInfoParamOfficePhone, officePhone);
		}
		if (!StringUtil.getInstance().IsEmpty(homePhone)) {
			params.put(updateUserInfoParamHomePhone, homePhone);
		}
		if (!StringUtil.getInstance().IsEmpty(mobilePhone)) {
			params.put(updateUserInfoParamMobilePhone, mobilePhone);
		}
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL
				+ RequestUrls.UPDATE_USER_INFO;
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		Trace.e(TAG,
				currMethod + "url->" + url + ",params->" + params.toString());
		fh.post(url, params, callBack);
	}
	
	/**
	 * 上载头像
	 * 
	 * @param avatarFileName
	 * @param callBack
	 */
	private static final String UploadAvatarParamFileName = "userfile";
	public void UploadAvatar(Context context, String avatarFileName,
			File avatarFile, AjaxCallBack<Object> callBack) {
		final String currMethod = "UploadAvatar:";
		FinalHttp fh = new FinalHttp();
		AjaxParams params = new AjaxParams();
		try {
			InputStream stream = new FileInputStream(avatarFile);
			String name = avatarFile.getName();
			int start = name.lastIndexOf(".") + 1;
			String endFix = name.substring(start, name.length());
			params.put(UploadAvatarParamFileName, stream, avatarFileName, Util.getMimeTypeByFile(endFix));
		} catch (FileNotFoundException e) {
			if (null != callBack) {
				callBack.onFailure(e, "avatar file not found.FileName:"
						+ avatarFileName);
			}
			Log.e(TAG, currMethod + "file not found.", e);
		}
		String url = RequestUrls.SERVER_BASIC_URL
				+ RequestUrls.UPLOAD_USER_AVATAR_URL;
		Log.e(TAG, "url->"+url+",avatarFileName->"+",userFile"+avatarFile);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		fh.post(url, params, callBack);
	}
}
