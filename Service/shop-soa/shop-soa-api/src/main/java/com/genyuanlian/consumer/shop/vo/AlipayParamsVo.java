package com.genyuanlian.consumer.shop.vo;

import java.io.Serializable;

public class AlipayParamsVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7861973349310021100L;

	// 付款金额
	private Double totalAmount;

	// 商品描述
	private String subject;

	// 订单编号
	private String orderNo;

	// 用户id
	private Long memberId;

	// 商品简介
	private String productDesc;

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

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

	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

}
