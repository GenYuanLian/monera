package com.genyuanlian.consumer.shop.vo;

import java.io.Serializable;

public class MemberAddressParamsVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7777802069957364699L;

	// 列信息
	private Long id; // 编辑时请赋值

	private Long memberId;

	private String receiver;

	private Integer gender; // 性别：1-先生，2-女士

	private Integer areaCode;

	private String areaName; // 北京市-北京-朝阳区-望京街道

	private String address;

	private String mobile;

	private String tel;

	private String email;

	private String addressAlias;

	private String remark;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public Integer getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(Integer areaCode) {
		this.areaCode = areaCode;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddressAlias() {
		return addressAlias;
	}

	public void setAddressAlias(String addressAlias) {
		this.addressAlias = addressAlias;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
