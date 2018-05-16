package com.genyuanlian.consumer.shop.vo;

import java.io.Serializable;

public class BaseAreaParentCodeParamsVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 390274325236879728L;

	private Integer parentCode; // 父节点编码，顶级结点parentCode=0

	public Integer getParentCode() {
		return parentCode;
	}

	public void setParentCode(Integer parentCode) {
		this.parentCode = parentCode;
	}

}
