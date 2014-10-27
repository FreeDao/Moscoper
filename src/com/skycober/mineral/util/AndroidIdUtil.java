package com.skycober.mineral.util;

import net.tsz.afinal.http.AjaxCallBack;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.skycober.mineral.account.GoodsService;
import com.skycober.mineral.widget.WaitLoadingDialog;

/**
 * 获取系统标识
 * 
 * @author Yes366
 * 
 */
public class AndroidIdUtil {
	private onAndroidIdGetFailure androidIdGetFailure;
	private onAndroidIdGetSuccess androidIdGetSuccess;

	public onAndroidIdGetFailure getAndroidIdGetFailure() {
		return androidIdGetFailure;
	}

	public void setAndroidIdGetFailure(onAndroidIdGetFailure androidIdGetFailure) {
		this.androidIdGetFailure = androidIdGetFailure;
	}

	public void setAndroidIdGetSuccse(onAndroidIdGetSuccess androidIdGetSuccse) {
		this.androidIdGetSuccess = androidIdGetSuccse;
	}

	public void sendAndroidId(final Context context) {
		final String androidId = Util.GetAndroidId(context);
		final WaitLoadingDialog waitDialog = new WaitLoadingDialog(context);
		waitDialog.setMessage("正在连接服务器");
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				waitDialog.show();
			}
		});
		GoodsService gs = new GoodsService();
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onFailure(Throwable t, String strMsg) {
				waitDialog.dismiss();
				// 谈提示框
				Log.e("wangxu", "onFailure");
				androidIdGetFailure.onFailure();
				super.onFailure(t, strMsg);
			}

			@Override
			public void onSuccess(Object t, HttpResponse response) {
				waitDialog.dismiss();
				if (null != response) {
					
					Header[] headers = response.getHeaders("Set-Cookie");
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < headers.length; i++) {
						String cookie = headers[i].getValue();
						sb.append(cookie);
						sb.append(";");
					}
					String myCookie = sb.toString();
					SettingUtil.getInstance(context).saveValue(
							SettingUtil.KEY_COOKIE, myCookie);
					// JPushInterface.setAliasAndTags(context, androidId,
					// null);//设置推送别名
//					androidIdGetSuccess.onSuccess();

				} else {
					Log.e("wangxu", "success but has p");

					androidIdGetFailure.onFailure();
				}
				super.onSuccess(t);
			}

		};
		gs.devIdentify(context, androidId, callBack);

	}

	public interface onAndroidIdGetFailure {
		void onFailure();
	}

	public interface onAndroidIdGetSuccess {
		void onSuccess();
	}
}
