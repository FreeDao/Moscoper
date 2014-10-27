package com.skycober.socialfarm.customview.smartImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class WebImage implements SmartImage {
    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 10000;

    private static WebImageCache webImageCache;

    private String url;

    public WebImage(String url) {
        this.url = url;
    }

    public Bitmap getBitmap(Context context) {
        // Don't leak context
        if(webImageCache == null) {
            webImageCache = new WebImageCache(context);
        }

        // Try getting bitmap from cache first
        Bitmap bitmap = null;
        if(url != null) {
            bitmap = webImageCache.get(url);
            if(bitmap == null) {
                bitmap = getBitmapFromUrl(url);
                if(bitmap != null){
                    webImageCache.put(url, bitmap);
                }
            }
        }

        return bitmap;
    }

    private Bitmap getBitmapFromUrl(String url) {
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
        Log.v("nanoha", "return bitmap="+bitmap);
        return bitmap;
    }

    public static void removeFromCache(String url) {
        if(webImageCache != null) {
            webImageCache.remove(url);
        }
    }
}
