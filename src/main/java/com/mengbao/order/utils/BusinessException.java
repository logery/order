package com.mengbao.order.utils;

public class BusinessException extends Exception {
	
	/**
	 * 错误编码
	 */
	private int errorCode;
	
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BusinessException(String message) {
        super(message);
    }
	public BusinessException(int errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}
}
