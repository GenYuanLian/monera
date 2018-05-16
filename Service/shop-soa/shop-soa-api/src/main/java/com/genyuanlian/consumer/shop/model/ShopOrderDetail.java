package com.genyuanlian.consumer.shop.model;

import java.io.Serializable;

/**
 * ShopOrderDetail Entity.
 */
public class ShopOrderDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5408192011767184451L;

	// 列信息
	private Long id;

	private Long memberId;

	private Long orderId;

	private String orderNo;

	private Long merchantId;

	private String merchantName;

	private Long commodityId;

	private Integer commodityType; //商品类型：1-提货卡,2-溯源卡,3-零售商品

	private String commodityName;

	private Double price;

	private Integer saleCount;

	private Integer refundCount;

	private Double amount;

	private String description;

	private Long commoditySnapshotId;

	private Integer status; //订单状态:0-未支付,1-未支付取消,2-支付过期，3-已支付，4-发货前退单，5-商家确认发货，6-买家确认收货，7-收货后退单,8-订单已完成，9-用户已删除

	private String remark;

	private java.util.Date createTime;
	
	/**
	 * 取消原因
	 */
	private String cancelReason;

	/**
	 * 剩余支付时间（秒）
	 */
	private Long surplusPayTime;
	
	private Integer payType; //支付方式:1-微信,2-支付宝,3-提货卡
	
	/**
	 * 商户类型:1-零售商,2-提货卡,3-溯源卡
	 */
	private Integer merchType;

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

	public void setMerchantId(Long value) {
		this.merchantId = value;
	}

	public Long getMerchantId() {
		return this.merchantId;
	}

	public void setMerchantName(String value) {
		this.merchantName = value;
	}

	public String getMerchantName() {
		return this.merchantName;
	}

	public void setCommodityId(Long value) {
		this.commodityId = value;
	}

	public Long getCommodityId() {
		return this.commodityId;
	}

	public void setCommodityType(Integer value) {
		this.commodityType = value;
	}

	public Integer getCommodityType() {
		return this.commodityType;
	}

	public void setCommodityName(String value) {
		this.commodityName = value;
	}

	public String getCommodityName() {
		return this.commodityName;
	}

	public void setPrice(Double value) {
		this.price = value;
	}

	public Double getPrice() {
		return this.price;
	}

	public void setSaleCount(Integer value) {
		this.saleCount = value;
	}

	public Integer getSaleCount() {
		return this.saleCount;
	}

	public void setRefundCount(Integer value) {
		this.refundCount = value;
	}

	public Integer getRefundCount() {
		return this.refundCount;
	}

	public void setAmount(Double value) {
		this.amount = value;
	}

	public Double getAmount() {
		return this.amount;
	}

	public void setDescription(String value) {
		this.description = value;
	}

	public String getDescription() {
		return this.description;
	}

	public void setCommoditySnapshotId(Long value) {
		this.commoditySnapshotId = value;
	}

	public Long getCommoditySnapshotId() {
		return this.commoditySnapshotId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	public Long getSurplusPayTime() {
		return surplusPayTime;
	}

	public void setSurplusPayTime(Long surplusPayTime) {
		this.surplusPayTime = surplusPayTime;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Integer getMerchType() {
		return merchType;
	}

	public void setMerchType(Integer merchType) {
		this.merchType = merchType;
	}

}
