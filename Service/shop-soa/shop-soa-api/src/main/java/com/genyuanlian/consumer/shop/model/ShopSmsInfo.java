package com.genyuanlian.consumer.shop.model;

import java.io.Serializable;


/**
 * ShopSmsInfo Entity.
 */
public class ShopSmsInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8675622022088464290L;

	//列信息
	private Long id;
	
	private Long memberId;
	
	private String mobile;
	
	private String content;
	
	private String smstype; //短信类型：注册-register,找回密码-findPwd, 登录-login
	
	private String validitytime;
	
	private String smsCode;
	
	private Integer status;
	
	private Integer supplier;
	
	private String callResp;
	
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
		
		
	public void setMobile(String value) {
		this.mobile = value;
	}
	
	public String getMobile() {
		return this.mobile;
	}
		
		
	public void setContent(String value) {
		this.content = value;
	}
	
	public String getContent() {
		return this.content;
	}
		
		
	public void setSmstype(String value) {
		this.smstype = value;
	}
	
	public String getSmstype() {
		return this.smstype;
	}
		
		
	public void setValiditytime(String value) {
		this.validitytime = value;
	}
	
	public String getValiditytime() {
		return this.validitytime;
	}
		
		
	public void setSmsCode(String value) {
		this.smsCode = value;
	}
	
	public String getSmsCode() {
		return this.smsCode;
	}
		
		
	public void setStatus(Integer value) {
		this.status = value;
	}
	
	public Integer getStatus() {
		return this.status;
	}
		
		
	public void setSupplier(Integer value) {
		this.supplier = value;
	}
	
	public Integer getSupplier() {
		return this.supplier;
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
		
}

