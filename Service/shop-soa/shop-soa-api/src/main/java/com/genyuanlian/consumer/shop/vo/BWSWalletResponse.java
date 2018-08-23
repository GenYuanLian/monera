package com.genyuanlian.consumer.shop.vo;

import java.io.Serializable;

/**
 * BWS 返回对象 Created by hunter.wang on 2018/5/10.
 */
public class BWSWalletResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -472969402040395057L;

	/**
	 * 是否ok
	 */
	private boolean isOK;

	/**
	 * 返回数据类型
	 */
	private String data;

	public boolean isOK() {
		return isOK;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public void setOK(boolean isOK) {
		this.isOK = isOK;
	}
}
