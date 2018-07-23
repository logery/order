package com.mengbao.order.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * 输入key对应config.properties文件中的key项 输出key项的值
 */
// public enum ReadProperties{
public class ReadProperties {

	public static Properties props;
	private static final Pattern PATTERN = Pattern.compile("\\$\\{([^\\}]+)\\}");
	private static InputStream is;

	static {
		try {
			props = new Properties();
			is = new FileInputStream(System.getProperty("user.dir") + "/config.properties");
			// ReadProperties.class.getResourceAsStream("/config.properties");
			props.load(is);
			is.close();
			init();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static String loop(String value) {
		// 定义matcher匹配其中的路径变量
		Matcher matcher = PATTERN.matcher(value);
		StringBuffer buffer = new StringBuffer();
		boolean flag = false;
		while (matcher.find()) {
			String matcherKey = matcher.group(1);// 依次替换匹配到的路径变量
			String matchervalue = props.getProperty(matcherKey);
			if (matchervalue != null) {
				matcher.appendReplacement(buffer, Matcher.quoteReplacement(matchervalue));// quoteReplacement方法对字符串中特殊字符进行转化
				flag = true;
			}
		}
		matcher.appendTail(buffer);
		// flag为false时说明已经匹配不到路径变量，则不需要再递归查找
		return flag ? loop(buffer.toString()) : value;
	}

	public static void init() {
		Enumeration<?> e = props.propertyNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = props.getProperty(key);
			if (value == null) {
				continue;
			}
			value = loop(value);
			props.setProperty(key, value);
		}
	}

	public synchronized static String configPropertiesGetByKey(String key) {
		return props.getProperty(key);
	}

	public static Properties loadProperties(String fileName) {
		Properties prop = new Properties();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(System.getProperty("user.dir") + "/" + fileName);
			// ReadProperties.class.getResourceAsStream("/"+fileName);
			prop.load(inputStream);
			return prop;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return prop;
	}
}