package com.genyuanlian.consumer.shop.model;

import java.io.Serializable;

/**
 * ShopBstkRecord Entity.
 */
public class ShopBstkRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2470732169121233634L;

	// 列信息
	private Long id;

	private Long ownerId;

	private Integer ownerType; // 钱包所有者类型：1-会员：2-商户

	private Integer callType; // 业务类型:1-创建钱包,2-充值,3-消费,4-批量充值,5-转账

	private Long businessId;

	private String callUrl;

	private String callReq;

	private String callResp;

	private Integer status;

	private Integer retryCount;

	private String remark;

	private String transactionNo; // 交易编号,本地生成

	private java.util.Date createTime;

	public void setId(Long value) {
		this.id = value;
	}

	public Long getId() {
		return this.id;
	}

	public void setOwnerId(Long value) {
		this.ownerId = value;
	}

	public Long getOwnerId() {
		return this.ownerId;
	}

	public void setOwnerType(Integer value) {
		this.ownerType = value;
	}

	public Integer getOwnerType() {
		return this.ownerType;
	}

	public void setCallType(Integer value) {
		this.callType = value;
	}

	public Integer getCallType() {
		return this.callType;
	}

	public void setBusinessId(Long value) {
		this.businessId = value;
	}

	public Long getBusinessId() {
		return this.businessId;
	}

	public void setCallUrl(String value) {
		this.callUrl = value;
	}

	public String getCallUrl() {
		return this.callUrl;
	}

	public void setCallReq(String value) {
		this.callReq = value;
	}

	public String getCallReq() {
		return this.callReq;
	}

	public void setCallResp(String value) {
		this.callResp = value;
	}

	public String getCallResp() {
		return this.callResp;
	}

	public void setStatus(Integer value) {
		this.status = value;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setRetryCount(Integer value) {
		this.retryCount = value;
	}

	public Integer getRetryCount() {
		return this.retryCount;
	}

	public void setRemark(String value) {
		this.remark = value;
	}

	public String getRemark() {
		return this.remark;
	}

	public String getTransactionNo() {
		return transactionNo;
	}

	public void setTransactionNo(String transactionNo) {
		this.transactionNo = transactionNo;
	}

	public void setCreateTime(java.util.Date value) {
		this.createTime = value;
	}

	public java.util.Date getCreateTime() {
		return this.createTime;
	}

}
