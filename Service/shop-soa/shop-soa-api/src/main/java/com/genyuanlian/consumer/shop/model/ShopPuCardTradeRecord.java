package com.genyuanlian.consumer.shop.model;

import java.io.Serializable;

/**
 * ShopPuCardTradeRecord Entity.
 */
public class ShopPuCardTradeRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4291915746575785493L;

	// 列信息
	private Long id;

	private Long memberId;

	private Long puCardId;

	private String title;

	private Double amount;

	private String transactionNo;

	private String remark;

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

	public void setPuCardId(Long value) {
		this.puCardId = value;
	}

	public Long getPuCardId() {
		return this.puCardId;
	}

	public void setTitle(String value) {
		this.title = value;
	}

	public String getTitle() {
		return this.title;
	}

	public void setAmount(Double value) {
		this.amount = value;
	}

	public Double getAmount() {
		return this.amount;
	}

	public void setTransactionNo(String value) {
		this.transactionNo = value;
	}

	public String getTransactionNo() {
		return this.transactionNo;
	}

	public void setRemark(String value) {
		this.remark = value;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setCreateTime(java.util.Date value) {
		this.createTime = value;
	}

	public java.util.Date getCreateTime() {
		return this.createTime;
	}

}
