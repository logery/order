package com.mengbao.order.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by weng on 2017/3/31.
 */
public class JavaReflect {
    private static Logger logger= LoggerFactory.getLogger(JavaReflect.class);
    public static Object getProperty(Class t,String fieldName) {
        Object object=null;
        try {
            Field field=t.getDeclaredField(fieldName);
            field.setAccessible(true);
            object=field.get(fieldName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            logger.error("reflect error ",e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            logger.error("reflect error ",e);
        }
        return object;
    }
    
    //获得类的所有域
    public static List<Field> getFields(String className){
		Class tempClass = null;
		try {
			tempClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if(tempClass == null){
			return null;
		}
		List<Field> fieldList = new ArrayList<>();
		while (!tempClass.getName().toLowerCase().equals("java.lang.object")) {//当父类为null的时候说明到达了最上层的父类(Object类).
		      fieldList.addAll(0, Arrays.asList(tempClass.getDeclaredFields()));
		      tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
		}
		return fieldList;
    }
    
    //获得类的所有的域的值
    public static List<Object> getFieldValues(List<Field> list,Object obj){
    	List<Object> objList = new ArrayList<>();
		try {
        	for(Field field:list){
        		field.setAccessible(true);
        		Object param = field.get(obj);
        		objList.add(param);
			} 
        } catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		return objList;
    }
    
    //判断类的所有域的值是否全为null
    public static boolean fieldsAllNull(List<Field> list,Object obj){
    	try{
    		boolean flag = true;
    		boolean result = true;
	    	for(Field field:list){
	    		field.setAccessible(true);
	    		Object param = field.get(obj);
	    		if(null==param){
	    			flag = true;
	    		}else{
	    			flag = false;
	    		}
	    		result = result&&flag;
	    	}
	    	return result;
    	} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
    }
    
    //判断类的某个域的值是否为null
    public static boolean fieldNull(Field field,Object obj){
    	try{
    		field.setAccessible(true);
    		Object param = field.get(obj);
    		if(null==param){
    			return true;
    		}else{
    			return false;
    		}
    	} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
    }
}
