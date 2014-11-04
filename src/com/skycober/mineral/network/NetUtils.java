package com.skycober.mineral.network;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient.ConnectCallback;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.RequestAddCookies;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.skycober.mineral.constant.RequestUrls;

import android.content.Context;
import android.widget.Toast;

public class NetUtils {
	
	public static void getUserToken(final String userId, final String userName,
			final String portraitUri, final String appKey,
			final String appSecret, final Callback callback) {
		System.out.println("======NetUtils=======");
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost post = new HttpPost(RequestUrls.URI);
				post.addHeader("appKey", appKey);
				post.addHeader("appSecret", appSecret);
				List<NameValuePair> parmas = new ArrayList<NameValuePair>();
				parmas.add(new BasicNameValuePair("userId", userId));
				parmas.add(new BasicNameValuePair("name", userName));
				try {
					UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
							parmas);
					post.setEntity(entity);
					HttpResponse response = httpClient.execute(post);
					System.out.println("=====response==="
							+ response.getStatusLine().getStatusCode());
					if (response.getStatusLine().getStatusCode() == 200) {
						String reString = EntityUtils.toString(response
								.getEntity());
						System.out.println("===reString====" + reString);
						JSONObject object = new JSONObject(reString);
						String token = object.getString("token");
						callback.onCallBack(token);
						// RongIM.connect(token, new ConnectCallback() {
						//
						// @Override
						// public void onSuccess(String arg0) {
						// // TODO Auto-generated method stub
						// Toast.makeText(context, "³É¹¦", 0).show();
						//
						// }
						//
						// @Override
						// public void onError(ErrorCode arg0) {
						// // TODO Auto-generated method stub
						//
						// }
						// });
					}
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();

	}

	public interface Callback {
		public void onCallBack(String token);
	}
}
