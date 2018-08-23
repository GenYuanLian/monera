package com.genyuanlian.consumer.shop.model;

import java.io.Serializable;

/**
 * ShopLoginLog Entity.
 */
public class ShopLoginLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4802767806245103194L;

	// 列信息
	private Long id;

	private Long memberId;

	private String mobile;

	private Integer status;

	private String membToken;

	private java.util.Date validTime;

	private String loginIp;
	
	/**
	 * 登录类型：1-密码登录；2-短信验证码登录；3-第三方授权登录
	 */
	private Integer loginType;

	private String latitude;

	private String longitude;

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

	public void setStatus(Integer value) {
		this.status = value;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setMembToken(String value) {
		this.membToken = value;
	}

	public String getMembToken() {
		return this.membToken;
	}

	public void setValidTime(java.util.Date value) {
		this.validTime = value;
	}

	public java.util.Date getValidTime() {
		return this.validTime;
	}

	public void setLoginIp(String value) {
		this.loginIp = value;
	}

	public String getLoginIp() {
		return this.loginIp;
	}

	public Integer getLoginType() {
		return loginType;
	}

	public void setLoginType(Integer loginType) {
		this.loginType = loginType;
	}

	public void setLatitude(String value) {
		this.latitude = value;
	}

	public String getLatitude() {
		return this.latitude;
	}

	public void setLongitude(String value) {
		this.longitude = value;
	}

	public String getLongitude() {
		return this.longitude;
	}

	public void setCreateTime(java.util.Date value) {
		this.createTime = value;
	}

	public java.util.Date getCreateTime() {
		return this.createTime;
	}

}
