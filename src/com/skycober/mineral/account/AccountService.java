package com.skycober.mineral.account;

import com.skycober.mineral.constant.RequestUrls;

import com.skycober.mineral.util.Trace;

import com.skycober.mineral.util.SettingUtil;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.content.Context;
import android.util.Log;

/**
 * ÕË»§ÀàµÄhttpÇëÇó
 * 
 * @author Yes366
 * 
 */

public class AccountService {
	private static final String TAG = "AccountService";
	// ×¢²á
	private static final String RegistUserParamUserName = "username";
	private static final String RegistUserParamPassword = "password";
	// private static final String RegistUserParamEmail = "email";
	private static final String RegistUserAndroidId = "dev_identify";

	/**
	 * ×¢²á
	 * 
	 * @param context
	 * @param userName
	 * @param password
	 * @param email
	 * @param callBack
	 */
	public void RegistUser(Context context, String userName, String password,
			String androidId, AjaxCallBack<Object> callBack) {
		final String currMethod = "RegistUser:";
		AjaxParams params = new AjaxParams();
		params.put(RegistUserParamUserName, userName);
		// params.put(RegistUserParamEmail, email);
		params.put(RegistUserParamPassword, password);
		params.put(RegistUserAndroidId, androidId);

		FinalHttp fh = new FinalHttp();
		// String cookie = SettingUtil.getInstance(context).getValue(
		// SettingUtil.KEY_COOKIE, null);
		// if (null != cookie && cookie.length() > 0) {
		// fh.addHeader("cookie", cookie);
		// }
		// Log.e("wangxu", cookie);
		String url = RequestUrls.SERVER_BASIC_URL + RequestUrls.REGIST_USER_URL;
		System.out.println("==RegistUser==="+url);
		Trace.e(TAG,
				currMethod + "url->" + url + ",params->" + params.toString());
		fh.post(url, params, callBack);
	}

	private static final String OauthParamUserName = "username";
	private static final String OauthParamPassWord = "password";
	private static final String OauthAndroidId = "dev_identify";

	/**
	 * µÇÂ¼
	 * 
	 * @param context
	 * @param userName
	 * @param password
	 * @param callBack
	 */
	public void Oauth(Context context, String userName, String password,
			String androidId, AjaxCallBack<Object> callBack) {
		final String currMethod = "Oauth:";
		AjaxParams params = new AjaxParams();
		params.put(OauthParamUserName, userName);
		params.put(OauthParamPassWord, password);
		params.put(OauthAndroidId, androidId);

		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL + RequestUrls.OAUTH_URL;
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		Trace.e(TAG,
				currMethod + "url->" + url + ",params->" + params.toString());
		fh.post(url, params, callBack);
	}

	private static final String UpdatePwdParamOldPass = "old_password";
	private static final String UpdatePwdParamNewPass = "new_password";

	/**
	 * ÐÞ¸ÄÃÜÂë
	 * 
	 * @param context
	 * @param oldPass
	 * @param newPass
	 * @param callBack
	 */
	public void UpdatePwd(Context context, String oldPass, String newPass,
			AjaxCallBack<Object> callBack) {
		final String currMethod = "UpdatePwd:";
		AjaxParams params = new AjaxParams();
		params.put(UpdatePwdParamOldPass, oldPass);
		params.put(UpdatePwdParamNewPass, newPass);
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL + RequestUrls.UPDATE_PWD_URL;
		Trace.e(TAG,
				currMethod + "url->" + url + ",params->" + params.toString());
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		fh.post(url, params, callBack);
	}

}
