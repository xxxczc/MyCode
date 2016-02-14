package com.czc.myrongdemo.utils;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat") public class DateUtils {
	/**
	 * 将所给时间减去一小时
	 * @param date
	 * @return
	 */
	public static String dateMinus(String date ){
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHH");
		try {
			long date1 = sf.parse(date).getTime();
			//将日期减去一个小时
			 String date2 = sf.format(new Date(date1-60*60*1000));
			 return date2;
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	
	/**
	 * 获取当前时间
	 * @return
	 */
	public static String getCurrDate(){
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHH");
		return sf.format(date);
	}
	
	/**
	 * 比较两个时间的大小
	 * @param oldDateStr 小时间
	 * @param newDateStr 大时间 
	 * @return 如果newDateStr大于oldDateStr返回true,否则返回false
	 */
	public static boolean compareDate(String oldDateStr, String newDateStr) {
		Date date1 = new Date();
		Date date2 = new Date();
		try {
			date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.parse(oldDateStr);
			date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.parse(newDateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (date1.before(date2)) {
			return true;
		} else {
			return false;
		}
	}
	
	
	public static String formateDate (String date ){
		String year = date.substring(0,4);
		String month = date.substring(4,6);
		String day = date.substring(6,8);
		String hour = date.substring(8);
		return year+"-"+month+"-"+day+"-"+hour;
	}
	
	public static String formateDate(long date){
		Date date1 = new Date(date);
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String date2 = sf.format(date1);
		return date2;
		
	}
	
	
	public static String formateDBDate(String date){
		String year = date.substring(0,4);
		String month = date.substring(5,7);
		String day = date.substring(8, 10);
		String hour = date.substring(11,13);
		
		return year+month+day+hour;
	}
	
}
