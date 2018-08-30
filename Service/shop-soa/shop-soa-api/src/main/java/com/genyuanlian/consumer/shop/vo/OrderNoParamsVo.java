package com.genyuanlian.consumer.shop.vo;

import java.io.Serializable;

public class OrderNoParamsVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3713881672828852312L;

	private String orderNo;

	private Long memberId;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

}
