package com.genyuanlian.consumer.exception;


public class HapiShopException extends Exception{
	
	private static final long serialVersionUID = -552774098078522632L;

	private Integer code;
	
	private String msg;

	public HapiShopException() {
		super();
	}

	public HapiShopException(Integer code, String msg) {
		super();
		this.code = code;
		this.msg = msg;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
