package com.mengbao.order.utils;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeHandle{
	
	//获得当前时间戳（秒）
	public static long getCurrentTimeUnix(){
		return System.currentTimeMillis()/1000;
	}
	//获得当前时间戳（毫秒）
	public static long getCurrentTimeUnixLong(){
		return System.currentTimeMillis();
	}
	//String转换成date类型
	static public Date stringToDate(String dateString){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sdf.parse(strFormatShift(dateString));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}    
	
	//date转换成String类型
	static public String dateToString(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = sdf.format(date);
		return dateString;
	}
	
	//date转换成String类型（含时间）
	static public String dateTimeToString(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateTimeString = sdf.format(date);
		return dateTimeString;
	}
	//date取出yyyy转换成String类型
	static public String dateToYear(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String year = sdf.format(date);
		return year;
	}
	
	//date取出MM转换成String类型
	static public String dateToMonth(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("MM");
		String month = sdf.format(date);
		return month;
	}
	
	//date取出dd装换成String类型
	static public String dateToDay(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		String day = sdf.format(date);
		return day;
	}
	
	//string时间格式统一
	public static String strFormatShift(String dateStr){
		dateStr = dateStr.replace("/", "-");
		dateStr = dateStr.replace(".", "-");
		return dateStr;
	}
	
	//stamp转换成date类型（含时间）
	public static Date stampToDateTime(long stamp){
		Date date = new Date(stamp);
		return date;
	}
	
	//毫秒stamp转换成String类型
	public static String longStampToString(long longStamp){
		return dateTimeToString(stampToDateTime(longStamp));
	}
	//秒stamp转换成String类型
	public static String stampToString(long stamp){
		return dateToString(stampToDateTime(stamp*1000));
	}
	//String类型转秒stamp
	public static long stringToStamp(String dateString){
		return stringToDate(dateString).getTime()/1000;
	}
	
	public static void main(String[] args) {
		Date date = stringToDate("2017.5.18");
		
	}
//	public Date monthSet(String date){
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		Date month = null;
//		try {
//			month = sdf.parse(date);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		return month;
//	}
//	
//	public Date daySet(String date){
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		Date day = null;
//		try {
//			day = sdf.parse(date);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		return day;
//	}
	
	public Time hhSet(String date){
		Time hh = null;
		return hh;
	}
	public Time mmSet(String date){
		Time mm = null;
		return mm;
	}
	public Time ssSet(String date){
		Time ss = null;
		return ss;
	}
}