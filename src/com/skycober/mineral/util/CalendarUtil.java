package com.skycober.mineral.util;

import android.annotation.SuppressLint;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**
 * 时间工具
 * @author Yes366
 *
 */
@SuppressLint("SimpleDateFormat")
public class CalendarUtil {
	/**
	 * 获得当前时间的微秒值 以1970-1-1 00:00:00为起始
	 */
	public static long GetCurrentDateForTimeMills() {
		Date date = new Date();
		long mills = date.getTime();
		date = null;
		return mills;
	}

	/**
	 * 
	 * @param milliseconds
	 * @return
	 */
	public static Date GetDateByTimeMills(long milliseconds) {
		Date date = new Date();
		date.setTime(milliseconds);
	
		return date;
	}

	/**
	 * 返回日期格式：****-**-**
	 * 
	 * @param date
	 * @return
	 */
	public static String GetFormatedDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}

	/**
	 * 返回日期格式：yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public static String GetFormatedDateForQuotedPrice(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	/**
	 * 返回日期格式：yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public static String GetFormatedDateForQuotedPrice(long milliseconds) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(milliseconds);
		return GetFormatedDateForQuotedPrice(cal.getTime());
	}

	/**
	 * Token是否已过期
	 * 
	 * @param milliseconds
	 * @return
	 */
	public static boolean isBeyond24Hours(long milliseconds) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(milliseconds);
		cal.add(Calendar.HOUR_OF_DAY, 24);
		return cal.before(Calendar.getInstance());
	}

	/**
	 * 返回日期格式：****-**-**
	 * 
	 * @param cal
	 * @return
	 */
	public static String GetFormatDateStrByCalendar(Calendar cal) {
		return GetFormatedDate(cal.getTime());
	}

	/**
	 * 返回日期格式：****-**-**
	 * 
	 * @param cal
	 * @return
	 */
	public static String GetFormatDateStrByMillseconds(long milliseconds) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(milliseconds);
		return GetFormatedDate(cal.getTime());
	}

	/**
	 * 返回日期格式：****-**
	 * 
	 * @param cal
	 * @return
	 */
	public static String GetFormatDateYMStrByMillseconds(long milliseconds) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(milliseconds);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		return sdf.format(cal.getTime());
	}

	/**
	 * 返回日期格式：****-**
	 * 
	 * @param cal
	 * @return
	 */
	public static String GetFormatDateYMStrByCal(Calendar cal) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		return sdf.format(cal.getTime());
	}

	/**
	 * 返回日期格式：****年**月**日
	 * 
	 * @param context
	 * @param milliseconds
	 * @return
	 */
	public static String GetDynamicShowDate(Context context, long milliseconds) {
		return null;
	}
	/**
	 * 计算返回时间与当前时间的间隔，返回String
	 * @param milliseconds
	 * @return
	 */
	public static String GetPostingTime( long milliseconds) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(milliseconds);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		return sdf.format(cal.getTime());
	}
	
	/**
	 * 计算返回时间与当前时间的间隔，返回String
	 * @param milliseconds
	 * @return
	 */
	public static String GetPostCommentTime( long milliseconds) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(milliseconds);
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
		return sdf.format(cal.getTime());
	}
}
