package com.skycober.mineral.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
/**
 * 杂乱的工具类
 * @author Yes366
 *
 */
public class Util {
	
	/**
	 * 从文件中读取信息.
	 * 
	 * @param fileName
	 * @return 文件内容(String类型)
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static String readStringFromFile(String fileName, Context context)
			throws UnsupportedEncodingException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				context.getAssets().open(fileName), "UTF-8"));
		StringBuilder builder = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}
		return builder.toString();
	}
	
	public static String GetAndroidId(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String android_id = telephonyManager.getDeviceId();
//				+ telephonyManager.getSimSerialNumber()+telephonyManager.getSubscriberId();
		// Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		return android_id;
	}

	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	static final String SAVE_TMP_PIC_FILEPATH = Environment
			.getExternalStorageDirectory().getPath() + "/MS_TMP.png";

	public static void showToast(Context context, int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}

	public static void showToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static int dateToInt(Date date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd",
					Locale.CHINA);
			return Integer.parseInt(sdf.format(date));
		} catch (Exception e) {
			Log.e("nanoha", "dateToInt error:" + e);
		}
		return 0;
	}

	public static Date stringToDate(String dateStr) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
					Locale.CHINA);
			return sdf.parse(dateStr);
		} catch (Exception e) {
			Log.e("nanoha", "stringToDate error:" + e);
		}
		return new Date();
	}

	public static String dateToString(Date date) {

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
					Locale.CHINA);
			return sdf.format(date);
		} catch (Exception e) {
			Log.e("nanoha", "stringToDate error:" + e);
		}
		return "";
	}

	public static String dateToStringForURL(Date date) {

		try {
			SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy-MM-dd%20HH:mm:ss", Locale.CHINA);
			return sdf.format(date);
		} catch (Exception e) {
			Log.e("nanoha", "dateToStringForURL error:" + e);
		}
		return "";
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cwjManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cwjManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			return true;
		} else {
			return false;
		}
	}

	public static String saveBitmap2File(Bitmap bmp) {
		try {
			FileOutputStream out = new FileOutputStream(SAVE_TMP_PIC_FILEPATH);
			bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
			return SAVE_TMP_PIC_FILEPATH;
		} catch (Exception e) {
			Log.e("nanoha", "saveBitmap2File error:" + e);
		}
		return null;
	}

	public static Bitmap decodeBitmapFromFilePath(String filePath,
			Context context) throws IOException {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		Bitmap bitmap = decodeBitmapFromFilePath(filePath, dm.widthPixels,
				dm.heightPixels);
		return bitmap;
	}

	public static Bitmap decodeBitmapFromFilePath(String filePath,
			int reqWidth, int reqHeight) throws IOException {

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
		return bitmap;
	}

	public static Bitmap decodeBitmapFromStream(Context context,
			ContentResolver cr, Uri uri) throws IOException {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		Bitmap bitmap = Util.decodeBitmapFromStream(cr, uri, dm.widthPixels,
				dm.widthPixels);
		return bitmap;
	}

	public static Bitmap decodeBitmapFromStream(ContentResolver cr, Uri uri,
			int reqWidth, int reqHeight) throws IOException {

		InputStream is = cr.openInputStream(uri);
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(is, null, options);

		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		is.close();
		is = null;
		is = cr.openInputStream(uri);
		Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
		is.close();
		is = null;
		return bitmap;
	}

	public static byte[] getBytes(InputStream is) throws IOException {
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024]; // 鐢ㄦ暟鎹
		int len = -1;
		while ((len = is.read(buffer)) != -1) {
			outstream.write(buffer, 0, len);
		}
		outstream.close();
		// 鍏抽棴娴佷竴瀹氳璁板緱銆?
		return outstream.toByteArray();
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

	/**
	 * 得到文件的mimeType类型
	 * 
	 * @param endFix
	 * @return mimeType
	 */
	public static String getMimeTypeByFile(String endFix) {
		MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
		if (!StringUtil.getInstance().IsEmpty(endFix)
				&& mimeTypeMap.hasExtension(endFix)) {
			return mimeTypeMap.getMimeTypeFromExtension(endFix);
		} else {
			return "*/*";
		}
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static String getPhotoPath(Activity activity, Intent data) {
		String path = null;
		Bitmap photo = null;
		int maxWidth = 800;
		int maxHeight = 480;
		try {
			Uri uri = data.getData();
			if (uri != null) {
				photo = Util.decodeBitmapFromFilePath(uri.getPath(), activity);
			}
			if (photo == null) {
				ContentResolver cr = activity.getContentResolver();
				try {
					photo = Util.decodeBitmapFromStream(cr, uri, maxWidth,
							maxHeight);
					path = Util.saveBitmap2File(photo);
				} catch (Exception e) {
					if (photo != null) {
						photo.recycle();
					}
					Bundle extras = data.getExtras();
					photo = (Bitmap) extras.get("data");
					if (null != photo) {
						path = Util.saveBitmap2File(photo);
					}
				}
			} else {
				path = Util.saveBitmap2File(photo);
			}
		} catch (Exception e) {

		}
		if (photo != null) {
			photo.recycle();
			photo = null;
		}
		return path;
	}

}
