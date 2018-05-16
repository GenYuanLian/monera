package com.genyuanlian.consumer.shop.vo;

import java.io.Serializable;

public class IdParamsVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -804924716993906437L;

	private Long Id; // 业务对象主键值

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}
}
