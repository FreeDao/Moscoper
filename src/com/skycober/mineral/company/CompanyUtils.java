package com.skycober.mineral.company;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

import com.skycober.mineral.util.SettingUtil;

public class CompanyUtils {
	/**
	 * 取消关注
	 * 
	 * @param eid
	 */
	public static void cancleAttentionCompany(final Context context,String url) {
		FinalHttp fh = new FinalHttp();
		String cookie = SettingUtil.getInstance(context).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		fh.get(url, new AjaxCallBack<Object>() {

			@Override
			public void onSuccess(Object t, HttpResponse response) {
				// TODO Auto-generated method stub
				super.onSuccess(t, response);
				try {
					JSONObject jsonObject = new JSONObject(t.toString());
					if ("1".equals(CompanyJsonUtils.parserResut(jsonObject))) {
//						getCommpanyInfos();
						Toast.makeText(context, "取消成功", 1).show();
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Throwable t, String strMsg) {
				// TODO Auto-generated method stub
				super.onFailure(t, strMsg);
			}

		});
	}
}
