package com.skycober.mineral.updateApk;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {
	public static ApkInfo parserJson(String jsonStr){
		ApkInfo info = new ApkInfo();
		try {
			JSONObject object = new JSONObject(jsonStr).getJSONObject(ApkInfo.APKRESULT);
			info.setId(object.getInt(ApkInfo.ID));
			info.setUrl(object.getString(ApkInfo.URL));
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return info;
		
	}
}
