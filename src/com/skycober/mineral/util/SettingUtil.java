package com.skycober.mineral.util;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.skycober.mineral.bean.AvatarRec;
import com.skycober.mineral.bean.CategoryRec;
import com.skycober.mineral.bean.UserRec;
/**
 * 数据缓存+本地存储
 * @author Yes366
 *
 */
public class SettingUtil {

	public static final String DB_NAME = "mineral.db";
	private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 10000;

	private static SettingUtil utilInstance;
	private Context mContext;
	public static final String SETTING_PREF = "mineral_setting";

	public static final String KEY_LOGIN_USER_ID = "key_login_user_id";
	public static final String DEFAULT_LOGIN_USER_ID = "-1";

	public static final String KEY_CURRENTUSER_ID = "current_user_id";
	public static final String KEY_COOKIE = "key_cookie";
	public static final String IS_LOGIN = "is_login";
	public static final String KEY_TEMP_LOGIN_USER_NAME = "key_temp_login_user_name";
	public static final String KEY_ACCEPT_PUSH_NOTIFICATION = "key_accept_push_notification";
	public static final String KEY_PRAISE_USER_ID= "key_praise_user_id";
	public static SettingUtil getInstance(Context context) {
		if (utilInstance == null) {
			utilInstance = new SettingUtil(context);
		}
		return utilInstance;
	}

	private SettingUtil(Context context) {
		this.mContext = context;
	}

	public void saveValue(String key, int value) {
		SharedPreferences pref = mContext.getSharedPreferences(SETTING_PREF,
				Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public int getValue(String key, int defaultValue) {
		SharedPreferences pref = mContext.getSharedPreferences(SETTING_PREF,
				Context.MODE_PRIVATE);
		return pref.getInt(key, defaultValue);
	}

	public void saveValue(String key, long value) {
		SharedPreferences pref = mContext.getSharedPreferences(SETTING_PREF,
				Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public long getValue(String key, long defaultValue) {
		SharedPreferences pref = mContext.getSharedPreferences(SETTING_PREF,
				Context.MODE_PRIVATE);
		return pref.getLong(key, defaultValue);
	}

	public void saveValue(String key, String value) {
		SharedPreferences pref = mContext.getSharedPreferences(SETTING_PREF,
				Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public String getValue(String key, String defaultValue) {
		SharedPreferences pref = mContext.getSharedPreferences(SETTING_PREF,
				Context.MODE_PRIVATE);
		return pref.getString(key, defaultValue);
	}

	public void saveValue(String key, boolean value) {
		SharedPreferences pref = mContext.getSharedPreferences(SETTING_PREF,
				Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public boolean getValue(String key, boolean defaultValue) {
		SharedPreferences pref = mContext.getSharedPreferences(SETTING_PREF,
				Context.MODE_PRIVATE);
		return pref.getBoolean(key, defaultValue);
	}

	public static final String SETTING_USER_PREF = "mineral_user_setting_";

	public void saveUserInfo(String userId, UserRec mRec) {
		String rootKey = SETTING_USER_PREF + userId;
		SharedPreferences pref = mContext.getSharedPreferences(rootKey,
				Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString(UserRec.ResponseUserId, mRec.getUserId());
		editor.putString(UserRec.ResponseUserName, mRec.getUserName());
		editor.putString(AvatarRec.ResponseSmall, null == mRec.getAvatar() ? null : mRec.getAvatar().getSmall());
		editor.putString(AvatarRec.ResponseNormal, null == mRec.getAvatar() ? null : mRec.getAvatar().getNormal());
		editor.putString(AvatarRec.ResponseBig, null == mRec.getAvatar() ? null : mRec.getAvatar().getBig());
		editor.putString(UserRec.ResponseRealName, mRec.getRealName());
		editor.putInt(UserRec.ResponseSex, mRec.getSex());
		editor.putString(UserRec.ResponseQQ, mRec.getQq());
		editor.putString(UserRec.ResponseBirthday, mRec.getBirthday());
		editor.putString(UserRec.ResponseEmail, mRec.getEmail());
		editor.putString(UserRec.ResponseMSN ,mRec.getMsn());
		editor.putString(UserRec.ResponseHomePhone, mRec.getHomePhone());
		editor.putString(UserRec.ResponseOfficePhone, mRec.getOfficePhone());
		editor.putString(UserRec.ResponseMobilePhone, mRec.getMobilePhone());
		editor.putLong(UserRec.ResponseVisitCount, mRec.getVisitCount());
		editor.commit();
	}
	public static final String SETTING_CATEGORY_PREF = "mineral_Category_setting_";
	public static final String SETTING_CATEGORY_ID_PREF = "mineral_Category_Id_setting_";

	public void saveCategoryInfo(List<CategoryRec> mlist) {
		String rootKey = SETTING_CATEGORY_PREF ;
		String idRootKey = SETTING_CATEGORY_ID_PREF;
		SharedPreferences pref = mContext.getSharedPreferences(rootKey,
				Context.MODE_PRIVATE);
		SharedPreferences idPref = mContext.getSharedPreferences(idRootKey,
				Context.MODE_PRIVATE);
		
		Editor editor = pref.edit();
		Editor idEditor = idPref.edit();
		
		for (int i = 0; i <= mlist.size()-1; i++) {
			editor.putString(String.valueOf(i), mlist.get(i).getName());
		}
		editor.commit();
		for (int i = 0; i <= mlist.size()-1; i++) {
			idEditor.putString(mlist.get(i).getName(),mlist.get(i).getId());
		}
		idEditor.commit();
	}
	public List<String> getCategoryInfo(){
		String rootKey = SETTING_CATEGORY_PREF;
		SharedPreferences pref = mContext.getSharedPreferences(rootKey,
				Context.MODE_PRIVATE);
		@SuppressWarnings("unchecked")
		Map<String, String> map=(Map<String, String>) pref.getAll();
		
		List<String> mlist=new ArrayList<String>();
		for (int i = 0; i <= map.size()-1; i++) {
			mlist.add(map.get(String.valueOf(i)));
		}
		return mlist;
	}
	public Map<String,String> getCategoryMap(){
		String rootKey = SETTING_CATEGORY_ID_PREF;
		SharedPreferences pref = mContext.getSharedPreferences(rootKey,
				Context.MODE_PRIVATE);
		@SuppressWarnings("unchecked")
		Map<String, String> map=(Map<String, String>) pref.getAll();
		return map;
	}
	
	public void saveUserAvatar(String userId, AvatarRec rec){
		String rootKey = SETTING_USER_PREF + userId;
		SharedPreferences pref = mContext.getSharedPreferences(rootKey,
				Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString(AvatarRec.ResponseSmall, rec.getSmall());
		editor.putString(AvatarRec.ResponseNormal, rec.getNormal());
		editor.putString(AvatarRec.ResponseBig, rec.getBig());
		editor.commit();
	}

	public UserRec getUserInfo(String userId, UserRec defaultValue) {
		String rootKey = SETTING_USER_PREF + userId;
		SharedPreferences pref = mContext.getSharedPreferences(rootKey,
				Context.MODE_PRIVATE);
		if (null == pref || StringUtil.getInstance().IsEmpty(pref.getString(UserRec.ResponseUserId, "")))	return defaultValue;
		UserRec mRec = new UserRec();
		mRec.setUserId(userId);
		String userName = pref.getString(UserRec.ResponseUserName, null);
		mRec.setUserName(userName);
		String email = pref.getString(UserRec.ResponseEmail, null);
		mRec.setEmail(email);
		AvatarRec avatarRec = new AvatarRec();
		String small = pref.getString(AvatarRec.ResponseSmall, null);
		avatarRec.setSmall(small);
		String normal = pref.getString(AvatarRec.ResponseNormal, null);
		avatarRec.setNormal(normal);
		String big = pref.getString(AvatarRec.ResponseBig, null);
		avatarRec.setBig(big);
		mRec.setAvatar(avatarRec);
		String realName = pref.getString(UserRec.ResponseRealName, null);
		mRec.setRealName(realName);
		int sex = pref.getInt(UserRec.ResponseSex, 0);
		mRec.setSex(sex);
		String signature = pref.getString(UserRec.ResponseSignature, null);
		mRec.setSignature(signature);
		long regTime = pref.getLong(UserRec.ResponseRegTime, mRec.getRegTime());mRec.setRegTime(regTime);
		String QQ = pref.getString(UserRec.ResponseQQ, mRec.getQq());mRec.setQq(QQ);
		String birthday = pref.getString(UserRec.ResponseBirthday, mRec.getBirthday());mRec.setBirthday(birthday);
		String msn=pref.getString(UserRec.ResponseMSN, mRec.getMsn());mRec.setMsn(msn);
		String homePhone=pref.getString(UserRec.ResponseHomePhone, mRec.getHomePhone());mRec.setHomePhone(homePhone);
		String officePhone=pref.getString(UserRec.ResponseOfficePhone, mRec.getOfficePhone());mRec.setOfficePhone(officePhone);
		String mobilePhone=pref.getString(UserRec.ResponseMobilePhone, mRec.getMobilePhone());mRec.setMobilePhone(mobilePhone);
		long visitCount=pref.getLong(UserRec.ResponseVisitCount, mRec.getVisitCount());mRec.setVisitCount(visitCount);
		return mRec;
	}
	
	
	
	public static Bitmap getBitmapFromUrl(String url) {
        Bitmap bitmap = null;
        URLConnection conn = null;
        try {
            conn = new URL(url).openConnection();
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            bitmap = BitmapFactory.decodeStream((InputStream) conn.getContent());
        } catch(Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
        	BitmapFactory.Options options=new BitmapFactory.Options(); 
            options.inJustDecodeBounds = false;
            options.inSampleSize = 2;  
            Log.v("nanoha", "decode stream inSampleSize 2");
            try {
            	if(null != bitmap){
            		if(!bitmap.isRecycled()){
            			bitmap.recycle();
            	        System.gc();
            		}
            	}
            	bitmap = null;
            	conn = new URL(url).openConnection();
                conn.setConnectTimeout(CONNECT_TIMEOUT);
                conn.setReadTimeout(READ_TIMEOUT);
				bitmap = BitmapFactory.decodeStream((InputStream) conn.getContent(),null, options);
			} catch (Exception e1) {
				Log.e("nanoha", "getBitmapFromUrl decode exception:"+e1);
			} catch (OutOfMemoryError error2){
	            options.inSampleSize = 4;  
	            Log.v("nanoha", "decode stream inSampleSize 4");
	            try {
	            	if(null != bitmap){
	            		if(!bitmap.isRecycled()){
	            			bitmap.recycle();
	            	        System.gc();
	            		}
	            	}
	            	bitmap = null;
	            	conn = new URL(url).openConnection();
	                conn.setConnectTimeout(CONNECT_TIMEOUT);
	                conn.setReadTimeout(READ_TIMEOUT);
					bitmap = BitmapFactory.decodeStream((InputStream) conn.getContent(),null, options);
				} catch (Exception e3) {
					
				} catch (OutOfMemoryError error3){
					bitmap = null;
		            Log.v("nanoha", "decode stream out of memory error");
				}
			}
		}
        return bitmap;
    }
	
	public void clearLoginInfo() {
		this.saveValue(SettingUtil.KEY_LOGIN_USER_ID, SettingUtil.DEFAULT_LOGIN_USER_ID);
		this.saveValue(SettingUtil.KEY_COOKIE, null);
	}
}
