package com.genyuanlian.consumer.shop.model;

import java.io.Serializable;

/**
 * ShopCommodityOrderPay Entity.
 */
public class ShopCommodityOrderPay implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6831198862106309761L;

	// 列信息
	private Long id;

	private Long memberId;

	private Long orderId;

	private String orderNo;

	private Long puCardId;

	private Double amount;

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

	public void setOrderId(Long value) {
		this.orderId = value;
	}

	public Long getOrderId() {
		return this.orderId;
	}

	public void setOrderNo(String value) {
		this.orderNo = value;
	}

	public String getOrderNo() {
		return this.orderNo;
	}

	public void setPuCardId(Long value) {
		this.puCardId = value;
	}

	public Long getPuCardId() {
		return this.puCardId;
	}

	public void setAmount(Double value) {
		this.amount = value;
	}

	public Double getAmount() {
		return this.amount;
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
