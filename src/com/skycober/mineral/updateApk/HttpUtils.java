package com.skycober.mineral.updateApk;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpUtils {
	public static String getDate(String url){
		HttpPost post = new HttpPost(url);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = null;
		String json = null;
		
		try {
			response = client.execute(post);
			if(response.getStatusLine().getStatusCode() == 200){
				json = EntityUtils.toString(response.getEntity());
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return json;
	}
	
	public static byte[] getApk(String url){
		HttpGet get = new HttpGet(url);
		HttpClient client = new DefaultHttpClient();
		
		HttpResponse response = null;
		try {
			response = client.execute(get);
			if(response.getStatusLine().getStatusCode() == 200){
				return EntityUtils.toByteArray(response.getEntity());
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
