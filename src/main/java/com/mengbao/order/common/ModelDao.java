package com.mengbao.order.common;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelDao {

	private static Logger logger = LoggerFactory.getLogger(ModelDao.class);
	private static QueryRunner qr = DBsetup.getRunner();

	public static class MysqlStatement {
		public static StringBuilder fieldsToString(String table, List<Field> fieldList) {
			StringBuilder fieldsStr = new StringBuilder();
			for (int i = 0; i < fieldList.size(); i++) {
				fieldsStr.append(fieldList.get(i).getName());
				if (i != fieldList.size() - 1) {
					fieldsStr.append(",");
				}
			}
			return fieldsStr;
		}

		public static <T> String questionMarkCount(List<T> list){
			StringBuilder sb = new StringBuilder();
			for(int i=0;i<list.size();i++){
				sb.append("?,");
			}
			sb.deleteCharAt(sb.length() - 1);
			return sb.toString();
		}

		public static StringBuilder save(String table, List<Field> fieldList) {
			StringBuilder fieldsStr = new StringBuilder();
			StringBuilder placeholder = new StringBuilder();
			for (int i = 0; i < fieldList.size(); i++) {
				fieldsStr.append(fieldList.get(i).getName());
				placeholder.append("?");
				if (i != fieldList.size() - 1) {
					fieldsStr.append(",");
					placeholder.append(",");
				}
			}
			StringBuilder sql = new StringBuilder();
			sql.append(" insert into ").append(table).append(" ( ").append(fieldsStr.toString()).append(" ) ")
					.append(" values ").append(" ( ").append(placeholder.toString()).append(" ) ");
			return sql;
		}

		public static StringBuilder selectAll(String table, List<Field> fieldList) {
			StringBuilder fieldsStr = fieldsToString(table, fieldList);
			StringBuilder sql = new StringBuilder();
			sql.append(" select ").append(fieldsStr.toString()).append(" from ").append(table);
			return sql;
		}

		// String sql = "select [field] from [db] where [primaryKey] = ?";
		public static StringBuilder selectByPrimary(String table, List<Field> fieldList, String primaryKey) {
			StringBuilder sql = selectAll(table, fieldList);
			sql.append(" where ").append(primaryKey).append("=?");
			return sql;
		}
		
		public static StringBuilder selectSeveralByPrimaryKeys(List<String> keysList, String table, List<Field> fieldList, String primaryKey){
			StringBuilder sql = selectAll(table, fieldList);
			sql.append(" where ").append(primaryKey).append(" in(").append(questionMarkCount(keysList)).append(")");
			return sql;
		}
	}

	public static class MysqlBuilder {
		public static <T> Map<String, Object> insert_Statement(T t, String table, List<Field> allField) {
			StringBuilder sql = MysqlStatement.save(table, allField);
			List<Object> params = JavaReflect.getFieldValues(allField, t);
			if (null == params) {
				return null;
			}
			Map<String, Object> map = new HashMap<>();
			map.put("sql", sql.toString());
			map.put("params", params.toArray());
			return map;
		}

		public static <T> Map<String, Object> batch_Statement(List<T> tList, String table, List<Field> allField) {
			StringBuilder sql = MysqlStatement.save(table, allField);
			int listSize = tList.size();
			Object[][] params = new Object[listSize][];
			for (int i = 0; i < listSize; i++) {
				T t = tList.get(i);
				List<Object> objList = JavaReflect.getFieldValues(allField, t);
				if (null == objList) {
					return null;
				}
				params[i] = objList.toArray();
			}
			Map<String, Object> map = new HashMap<>();
			map.put("sql", sql.toString());
			map.put("params", params);
			return map;
		}
	}
	
	// 通用插入单条
	public static <T> boolean insert(T t, String table, List<Field> allField) {
		return insert(null, t, table, allField);
	}
	
	// 通用插入单条(自定义连接)
	public static <T> boolean insert(Connection conn, T t, String table, List<Field> allField){
		Map<String, Object> map = MysqlBuilder.insert_Statement(t, table, allField);
		if (null == map) {
			return false;
		}
		// "insert into basic_user
		// (UUID,Name,Type,Status,Sex,Birthday,PortraitMD5,PhoneNumber,Email,Account,Password)
		// values (?,?,?,?,?,?,?,?,?,?,?)";
		try {
			int n = 0;
			if (null==conn) {
				n = qr.update((String) map.get("sql"), (Object[]) map.get("params"));
			} else {
				n = qr.update(conn, (String) map.get("sql"), (Object[]) map.get("params"));
			}
			if (n > 0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("", e);
		}
		return false;
	}
	
	// //通用插入单条
	// public static <T> boolean insert(Map<String, Object>... maps) {
	// if(null==maps){
	// return false;
	// }
	// Connection conn = null;
	// try {
	// conn = qr.getDataSource().getConnection();
	// conn.setAutoCommit(false);
	// for(Map<String, Object> map:maps){
	// int n = qr.update(conn, (String) map.get("sql"),
	// (Object[])map.get("params"));
	// if(n<=0){
	// conn.rollback();
	// return false;
	// }
	// }
	// conn.commit();
	// return true;
	// } catch (SQLException e) {
	// if(conn!=null){
	// //出现异常之后就回滚事务
	// try {
	// conn.rollback();
	// } catch (SQLException e1) {
	// }
	// }
	// e.printStackTrace();
	// logger.error(e.getMessage());
	// } finally {
	// try {
	// if(conn!=null){
	// conn.close();
	// }
	// } catch (SQLException e) {
	// }
	// }
	// return false;
	// }
	
	// 通用插入批量
	public static <T> int batch(List<T> tList, String table, List<Field> allField) {
		return batch(null, tList, table, allField);
	}

	// 通用插入批量(自定义连接)
	public static <T> int batch(Connection conn, List<T> tList, String table, List<Field> allField) {
		Map<String, Object> map = MysqlBuilder.batch_Statement(tList, table, allField);
		if (null == map) {
			return -1;
		}
		try {
			int[] n;
			if (null == conn) {
				n = qr.batch((String) map.get("sql"), (Object[][]) map.get("params"));
			} else {
				n = qr.batch(conn, (String) map.get("sql"), (Object[][]) map.get("params"));
			}
			return n.length;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("", e);
		}
		return -1;
	}

	// 通用删除单条
	public static boolean deleteByPrimaryKey(String UUID, String table, String primaryKey) {
		return deleteByPrimaryKey(null, UUID, table, primaryKey);
	}

	// 通用删除单条(自定义连接)
	public static boolean deleteByPrimaryKey(Connection conn, String UUID, String table, String primaryKey) {
		StringBuilder sql = new StringBuilder();
		sql.append("delete from ").append(table).append(" where ").append(primaryKey).append("=?");
		// "delete from basic_user where UUID = ?";
		try {
			int n = 0;
			if (null == conn) {
				n = qr.update(sql.toString(), UUID);
			} else {
				n = qr.update(conn, sql.toString(), UUID);
			}
			if (n > 0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// 通用删除批量
	public static boolean batchDeleteByPrimaryKey(List<String> UUIDList, String table, String primaryKey) {
		return batchDeleteByPrimaraKey(null, UUIDList, table, primaryKey);
	}

	// 通用删除批量(自定义连接)
	public static boolean batchDeleteByPrimaraKey(Connection conn, List<String> UUIDList, String table,
			String primaryKey) {
		if (UUIDList.isEmpty()) {
			return false;
		}
		List<String> params = new ArrayList<>();
		StringBuilder sql = new StringBuilder();
		sql.append("delete from ").append(table).append(" where ").append(primaryKey).append(" in ( ");
		for (String UUID : UUIDList) {
			sql.append("?,");
			params.add(UUID);
		}
		sql.deleteCharAt(sql.length() - 1).append(" )");
		// "delete from basic_user where UUID in (?,?)";
		try {
			int n = 0;
			if (null == conn) {
				n = qr.update(sql.toString(), params.toArray());
			} else {
				n = qr.update(conn, sql.toString(), params.toArray());
			}
			if (n > 0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("", e);
		}
		return false;
	}

	// 通用更新单条
	public static <T> boolean updateByPrimaryKey(T t, String table, List<Field> allField, String primaryKey) {
		return updateByPrimaryKey(null, t, table, allField, primaryKey);
	}

	// 通用更新单条(自定义连接)
	public static <T> boolean updateByPrimaryKey(Connection conn, T t, String table, List<Field> allField,
			String primaryKey) {
		List<Object> params = new ArrayList<>();
		StringBuilder sql = new StringBuilder();
		sql.append("update ").append(table).append(" set ");
		List<Field> all = new ArrayList<>();
		all.addAll(allField);
		all.remove(0);
		List<Object> objList = JavaReflect.getFieldValues(allField, t);
		boolean otherFieldsHasNull = JavaReflect.fieldsAllNull(all, t);
		if (null == objList.get(0) || otherFieldsHasNull) {
			return false;
		}
		try {
			for (int i = 1; i < objList.size(); i++) {
				if (null != objList.get(i)) {
					sql.append(allField.get(i).getName() + "=?,");
					params.add(objList.get(i));
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			logger.error("", e);
			return false;
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(" where ").append(primaryKey).append("=?");
		params.add(objList.get(0));
		// "update basic_user set
		// Name=?,Type=?,Status=?,Sex=?,Birthday=?,PortraitMD5=?,PhoneNumber=?,Email=?,Account=?,Password=?
		// where UUID = ?";
		try {
			int n = 0;
			if (null == conn) {
				n = qr.update(sql.toString(), params.toArray());
			} else {
				n = qr.update(conn, sql.toString(), params.toArray());
			}
			if (n > 0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("", e);
		}
		return false;
	}

	// 通用查询单条
	public static <T> T selectSingleByPrimaryKey(Object obj, String table, List<Field> allField, String primaryKey,
			Class<T> clazz) {
		StringBuilder sql = MysqlStatement.selectByPrimary(table, allField, primaryKey);
		// "select
		// UUID,Name,Type,Status,Sex,Birthday,PortraitMD5,PhoneNumber,Email,Account,Password
		// from basic_user where UUID = ?";
		try {
			T t = qr.query(sql.toString(), new BeanHandler<T>(clazz), obj);
			return t;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("", e);
			return null;
		}
	}
	
	// 通用查询若干条（主键）
	public static <T> List<T> selectSeveralByPrimaryKeys(List<String> UUIDList, String table, List<Field> allField, String primaryKey, Class<T> clazz){
		StringBuilder sql = MysqlStatement.selectSeveralByPrimaryKeys(UUIDList, table, allField, primaryKey);
		try {
			List<T> list = qr.query(sql.toString(), new BeanListHandler<T>(clazz), UUIDList.toArray());
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("", e);
			return null;
		}
	}

	// 通用查询所有
	public static <T> List<T> selectAll(String table, List<Field> allField, Class<T> clazz, String otherExample,
			String order, String otherCondition) {
		StringBuilder sql = new StringBuilder();
		StringBuilder fieldsStr = MysqlStatement.fieldsToString(table, allField);
		sql.append(" select ").append(fieldsStr).append(" from ").append(table);
		sql.append(" where 1=1 ");
		if (null != otherExample) {
			sql.append(otherExample);
		}
		if (null != order) {
			sql.append(" order by ").append(order);
		}
		if (null != otherCondition) {
			sql.append(otherCondition);
		}
		try {
			List<T> list = qr.query(sql.toString(), new BeanListHandler<T>(clazz));
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("", e);
			return null;
		}
	}

	// 通用查询多条，以order排序
	public static <T> List<T> selectList(T t, String table, List<Field> allField, Class<T> clazz, String otherExample,
			String order, String otherCondition) {
		List<Object> objList = JavaReflect.getFieldValues(allField, t);
		List<Object> params = new ArrayList<>();
		StringBuilder sql = new StringBuilder();
		StringBuilder fieldsStr = MysqlStatement.fieldsToString(table, allField);
		sql.append(" select ").append(fieldsStr).append(" from ").append(table);
		sql.append(" where 1=1 and ");
		for (int i = 0; i < objList.size(); i++) {
			if (null != objList.get(i)) {
				sql.append(allField.get(i).getName() + "=? and ");
				params.add(objList.get(i));
			}
		}
		sql.delete(sql.length() - 4, sql.length() - 1);
		if (null != otherExample) {
			sql.append(otherExample);
		}
		if (null != order) {
			sql.append(" order by ").append(order);
		}
		if (null != otherCondition) {
			sql.append(otherCondition);
		}
		// "select
		// UUID,Name,Type,Status,Sex,Birthday,PortraitMD5,PhoneNumber,Email,Account,Password
		// from basic_user where
		// UUID =
		// ?,Name=?,Type=?,Status=?,Sex=?,Birthday=?,PortraitMD5=?,PhoneNumber=?,Email=?,Account=?,Password=?";
		try {
			List<T> list = qr.query(sql.toString(), new BeanListHandler<T>(clazz), params.toArray());
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("", e);
			return null;
		}
	}

	public static <T> List<T> selectList(T t, String table, List<Field> allField, Class<T> clazz, String order,
			String otherCondition) {
		return selectList(t, table, allField, clazz, null, order, otherCondition);
	}

	public static <T> List<T> selectList(T t, String table, List<Field> allField, Class<T> clazz,
			String otherCondition) {
		return selectList(t, table, allField, clazz, null, null, otherCondition);
	}

	public static <T> List<T> selectList(T t, String table, List<Field> allField, Class<T> clazz) {
		return selectList(t, table, allField, clazz, null, null, null);
	}

	// 通用按条件删除
	public static <T> boolean deleteByCondition(T t, String table, List<Field> allField, Class<T> clazz) {
		return deleteByCondition(null, t, table, allField, clazz);
	}

	// 通用按条件删除(自定义连接)
	public static <T> boolean deleteByCondition(Connection conn, T t, String table, List<Field> allField,
			Class<T> clazz) {
		List<Object> objList = JavaReflect.getFieldValues(allField, t);
		List<Object> params = new ArrayList<>();
		StringBuilder sql = new StringBuilder();
		sql.append(" delete from ").append(table).append(" where 1=1 and ");
		for (int i = 0; i < objList.size(); i++) {
			if (null != objList.get(i)) {
				sql.append(allField.get(i).getName() + "=? and ");
				params.add(objList.get(i));
			}
		}
		sql.delete(sql.length() - 4, sql.length() - 1);
		// "delete from basic_user where
		// UUID =
		// ?,Name=?,Type=?,Status=?,Sex=?,Birthday=?,PortraitMD5=?,PhoneNumber=?,Email=?,Account=?,Password=?";
		try {
			int n = 0;
			if (null == conn) {
				n = qr.update(sql.toString(), params.toArray());
			} else {
				n = qr.update(conn, sql.toString(), params.toArray());
			}
			if (n > 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("", e);
			return false;
		}
		return false;
	}

	// 通用查询数量
	public static <T> int count(T t, String table, List<Field> allField, String primaryKey) {
		List<Object> objList = JavaReflect.getFieldValues(allField, t);
		List<Object> params = new ArrayList<>();
		StringBuilder sql = new StringBuilder();
		sql.append(" select ").append(" count( ").append(primaryKey).append(" ) from ").append(table);
		sql.append(" where 1=1 and ");
		for (int i = 0; i < objList.size(); i++) {
			if (null != objList.get(i)) {
				sql.append(allField.get(i).getName() + "=? and ");
				params.add(objList.get(i));
			}
		}
		sql.delete(sql.length() - 4, sql.length() - 1);
		// "select count(UUID) from basic_user where
		// UUID =
		// ?,Name=?,Type=?,Status=?,Sex=?,Birthday=?,PortraitMD5=?,PhoneNumber=?,Email=?,Account=?,Password=?";
		try {
			int count = ((Long) qr.query(sql.toString(), new ScalarHandler<>(), params.toArray())).intValue();
			System.out.println(count);
			return count;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("", e);
		}
		return -1;
	}
}
