package com.genyuanlian.consumer.shop.vo;

import java.io.Serializable;

public class ShopMessageVo<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2345555603923912776L;
	/**
	 * 返回结果 true 成功 false 失败
	 */
	private boolean result = false;
	/**
	 * 返回消息
	 */
	private String message;
	/**
	 * 服务端的错误码 注：只有返回结果为false时才有值
	 */
	private String errorCode;
	/**
	 * 服务端错误码对应的错误信息 注：只有返回结果为false时才有值
	 */
	private String errorMessage;
	/**
	 * 返回相应的VO实体
	 */
	private T t;

	private  int count;


	private int isHasNext;

	/**
	 * 备注信息
	 */
	private String desc;

	public ShopMessageVo() {
	}

	public ShopMessageVo(boolean result, String message, T t) {
		this.result = result;
		this.message = message;
		this.t = t;
	}

	public ShopMessageVo(boolean result, String message, String errorCode, String errorMessage, T t) {
		this.result = result;
		this.message = message;
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.t = t;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return the result
	 */
	public boolean isResult() {
		return result;
	}

	/**
	 * @param result
	 *            the result to set
	 */
	public void setResult(boolean result) {
		this.result = result;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode
	 *            the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage
	 *            the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the t
	 */
	public T getT() {
		return t;
	}

	/**
	 * @param t
	 *            the t to set
	 */
	public void setT(T t) {
		this.t = t;
	}

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * @param desc
	 *            the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getIsHasNext() {
		return isHasNext;
	}

	public void setIsHasNext(int isHasNext) {
		this.isHasNext = isHasNext;
	}

	@Override
	public String toString() {
		return "UserMessageVo [result=" + result + ", message=" + message + ", errorCode=" + errorCode
				+ ", errorMessage=" + errorMessage + ", t=" + t + ", desc=" + desc + "]";
	}

}
