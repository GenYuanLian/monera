package com.genyuanlian.consumer.shop.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ShopOrder Entity.
 */
public class ShopOrder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1915879358172888865L;

	// 列信息
	private Long id;

	private Long memberId;

	private String orderNo;

	private Integer payType; // 支付方式:1-微信,2-支付宝,3-提货卡

	private Double amount;
	
	private BigDecimal totalAmount;

	private String description;

	private String remark;

	private String transactionNo;

	private String referraCode;

	private Long referraId;

	private java.util.Date createTime;

	public void setId(Long value) {
		this.id = value;
	}

	public Long getId() {
		return this.id;
	}

	public void setMemberId(Long value) {
		this.memberId = value;
	}

	public Long getMemberId() {
		return this.memberId;
	}

	public void setOrderNo(String value) {
		this.orderNo = value;
	}

	public String getOrderNo() {
		return this.orderNo;
	}

	public void setPayType(Integer value) {
		this.payType = value;
	}

	public Integer getPayType() {
		return this.payType;
	}

	public void setAmount(Double value) {
		this.amount = value;
	}

	public Double getAmount() {
		return this.amount;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public void setDescription(String value) {
		this.description = value;
	}

	public String getDescription() {
		return this.description;
	}

	public void setRemark(String value) {
		this.remark = value;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setTransactionNo(String value) {
		this.transactionNo = value;
	}

	public String getTransactionNo() {
		return this.transactionNo;
	}

	public void setReferraCode(String value) {
		this.referraCode = value;
	}

	public String getReferraCode() {
		return this.referraCode;
	}

	public void setReferraId(Long referraId) {
		this.referraId = referraId;
	}

	public Long getReferraId() {
		return referraId;
	}

	public void setCreateTime(java.util.Date value) {
		this.createTime = value;
	}

	public java.util.Date getCreateTime() {
		return this.createTime;
	}

}
