package com.mengbao.order.common;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.PropertyConfigurator;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;

public class DBsetup {

	private static final Properties properties = ReadProperties.props;
	private static DataSource dataSource;
	private static QueryRunner runner = null;

	static {
		//初始化日志
		System.setProperty ("WORKDIR", System.getProperty("user.dir"));
		PropertyConfigurator.configure(ReadProperties.loadProperties("log4j.properties"));
		try {
			dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static QueryRunner getRunner() {
		if (dataSource != null || runner == null) {
			runner = new QueryRunner(dataSource);
		}
		return runner;
	}
}
