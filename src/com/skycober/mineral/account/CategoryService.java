package com.skycober.mineral.account;

import com.skycober.mineral.constant.RequestUrls;
import com.skycober.mineral.util.SettingUtil;
import com.skycober.mineral.util.Trace;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.content.Context;
import android.util.Log;
/**
 * 获取分类的http
 * @author Yes366
 *
 */
public class CategoryService {
	private static final String TAG = "CategoryService";
	private static final String GetCategoryParamCatId = "[cat_id]";

	public void GetCategory(Context context, String catId,
			AjaxCallBack<Object> callBack) {
		final String currMethod = "GetCategoryByCatId:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL + RequestUrls.GET_CATEGORY;
		url = url.replace(GetCategoryParamCatId, catId);
		Trace.e(TAG, currMethod + "url->" + url);
		fh.get(url, callBack);
	}

	public void GetCategory(Context context, AjaxCallBack<Object> callBack) {
		final String currMethod = "GetCategory:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL
				+ RequestUrls.GET_CATEGORY_NOID;
		Trace.e(TAG, currMethod + "url->" + url);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		fh.get(url, callBack);
	}

	public void GetCategoryForHasLogin(Context context, AjaxCallBack<Object> callBack) {
		final String currMethod = "GetCategoryForHasLogin:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL
				+ RequestUrls.GET_CATEGORY_NOID;
		Trace.e(TAG, currMethod + "url->" + url);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		fh.get(url, callBack);
	}
	private static final String GetTagCategoryOffset = "[offset]";
	private static final String GetTagCategoryCount = "[count]";
	private static final String GetTagCategoryType = "[type]";

	public void GetTagCategory(Context context,String serach,String type,int offset,int count, AjaxCallBack<Object> callBack) {
		final String currMethod = "GetCategory:";
		FinalHttp fh = new FinalHttp();
		String url = RequestUrls.SERVER_BASIC_URL
				+ RequestUrls.GET_TAG_CATEGORY;
		url=url.replace(GetTagCategoryOffset, String.valueOf(offset));
		url=url.replace(GetTagCategoryCount, String.valueOf(count));
		url=url.replace(GetTagCategoryType, type);

		
		if(null!=serach&&!serach.equalsIgnoreCase(""))
		url=url+"/search/"+serach;
		Log.e("wangxu", currMethod + "url->" + url);
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		fh.get(url, callBack);
	}
}
