package com.genyuanlian.consumer.shop.model;

import java.io.Serializable;


/**
 * ShopPayRecord Entity.
 */
public class ShopPayRecord implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 442138634714844711L;

	//列信息
	private Long id;
	
	private Long memberId;
	
	private String payNum;
	
	private String payAccount;
	
	private Integer accountType; //账户类型:1-weixin,2-alipay
	
	private String tradeNo;
	
	private String orderNo;
	
	private Double amount;
	
	private Integer status;
	
	private String remark;
	
	private String callReq;
	
	private String callResp;
	
	private java.util.Date createTime;
	
	private String mobile;
	
		
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
		
		
	public void setPayNum(String value) {
		this.payNum = value;
	}
	
	public String getPayNum() {
		return this.payNum;
	}
		
		
	public void setPayAccount(String value) {
		this.payAccount = value;
	}
	
	public String getPayAccount() {
		return this.payAccount;
	}
		
		
	public void setAccountType(Integer value) {
		this.accountType = value;
	}
	
	public Integer getAccountType() {
		return this.accountType;
	}
		
		
	public void setTradeNo(String value) {
		this.tradeNo = value;
	}
	
	public String getTradeNo() {
		return this.tradeNo;
	}
		
		
	public void setOrderNo(String value) {
		this.orderNo = value;
	}
	
	public String getOrderNo() {
		return this.orderNo;
	}
		
		
	public void setAmount(Double value) {
		this.amount = value;
	}
	
	public Double getAmount() {
		return this.amount;
	}
		
		
	public void setStatus(Integer value) {
		this.status = value;
	}
	
	public Integer getStatus() {
		return this.status;
	}
		
		
	public void setRemark(String value) {
		this.remark = value;
	}
	
	public String getRemark() {
		return this.remark;
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
		
		
	public void setCreateTime(java.util.Date value) {
		this.createTime = value;
	}
	
	public java.util.Date getCreateTime() {
		return this.createTime;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
		
}

