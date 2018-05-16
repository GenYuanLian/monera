package com.genyuanlian.consumer.shop.model;

import java.io.Serializable;

/**
 * ShopMemberAddress Entity.
 */
public class ShopMemberAddress implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3973026753614316647L;

	// 列信息
	private Long id;

	private Long memberId;

	private String receiver;

	private Integer gender;

	private Long areaId;

	private String areaName;

	private String address;

	private String mobile;

	private String tel;

	private String email;

	private String addressAlias;

	private String remark;

	private Integer status; //状态:1-正常，2-删除

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

	public void setReceiver(String value) {
		this.receiver = value;
	}

	public String getReceiver() {
		return this.receiver;
	}

	public void setGender(Integer value) {
		this.gender = value;
	}

	public Integer getGender() {
		return this.gender;
	}

	public void setAreaId(Long value) {
		this.areaId = value;
	}

	public Long getAreaId() {
		return this.areaId;
	}

	public void setAreaName(String value) {
		this.areaName = value;
	}

	public String getAreaName() {
		return this.areaName;
	}

	public void setAddress(String value) {
		this.address = value;
	}

	public String getAddress() {
		return this.address;
	}

	public void setMobile(String value) {
		this.mobile = value;
	}

	public String getMobile() {
		return this.mobile;
	}

	public void setTel(String value) {
		this.tel = value;
	}

	public String getTel() {
		return this.tel;
	}

	public void setEmail(String value) {
		this.email = value;
	}

	public String getEmail() {
		return this.email;
	}

	public void setAddressAlias(String value) {
		this.addressAlias = value;
	}

	public String getAddressAlias() {
		return this.addressAlias;
	}

	public void setRemark(String value) {
		this.remark = value;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setStatus(Integer value) {
		this.status = value;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setCreateTime(java.util.Date value) {
		this.createTime = value;
	}

	public java.util.Date getCreateTime() {
		return this.createTime;
	}

}
