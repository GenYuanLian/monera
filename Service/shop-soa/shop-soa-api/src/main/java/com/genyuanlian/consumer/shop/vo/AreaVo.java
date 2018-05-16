package com.genyuanlian.consumer.shop.vo;

import java.io.Serializable;

public class AreaVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5693495090544160230L;

	private String value; // code

	private String name; // name

	private String parent; // parent

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

}
