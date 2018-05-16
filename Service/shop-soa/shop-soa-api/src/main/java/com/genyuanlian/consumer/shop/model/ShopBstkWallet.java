package com.genyuanlian.consumer.shop.model;

import java.io.Serializable;

/**
 * ShopBstkWallet Entity.
 */
public class ShopBstkWallet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2997131326411466378L;

	// 列信息
	private Long id;

	private Long ownerId;

	private Integer ownerType; //钱包所有者类型：1-会员：2-商户

	private String mobile;

	private String walletAddress;

	private String publicKeyAddr;

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

	public void setMobile(String value) {
		this.mobile = value;
	}

	public String getMobile() {
		return this.mobile;
	}

	public void setWalletAddress(String value) {
		this.walletAddress = value;
	}

	public String getWalletAddress() {
		return this.walletAddress;
	}

	public void setPublicKeyAddr(String value) {
		this.publicKeyAddr = value;
	}

	public String getPublicKeyAddr() {
		return this.publicKeyAddr;
	}

	public void setCreateTime(java.util.Date value) {
		this.createTime = value;
	}

	public java.util.Date getCreateTime() {
		return this.createTime;
	}

}
