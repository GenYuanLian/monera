package com.genyuanlian.consumer.shop.vo;

import java.io.Serializable;

public class WeixinpayParamsVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 商品描述
	private String subject;
	// 订单编号
	private String orderNo;
	// 付款金额
	private Double totalAmount;
	// 用户id
	private Long memberId;
	//用户IP
	private String spbillCreateIp;
	//用户openId
	private String openId;

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

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getSpbillCreateIp() {
		return spbillCreateIp;
	}

	public void setSpbillCreateIp(String spbillCreateIp) {
		this.spbillCreateIp = spbillCreateIp;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

}
