package com.genyuanlian.consumer.shop.model;

import java.io.Serializable;

/**
 * ShopMerchant Entity.
 */
public class ShopMerchant implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1631197169747312037L;

	// 列信息
	private Long id;

	private String merchName;

	private String merchCode;

	private Integer merchType; //商户类型:1-零售商,2-提货卡,3-溯源卡

	private String logoPic;

	private String briefIntro;

	private String category;

	private Long areaId;

	private String areaName;

	private String address;

	private String contact;

	private String tel;

	private String notice;

	private Integer status;

	private String remark;

	private java.util.Date createTime;

	// 非数据库字段
	private String salesVolume; // 销量

	private String praise; // 好评

	public void setId(Long value) {
		this.id = value;
	}

	public Long getId() {
		return this.id;
	}

	public void setMerchName(String value) {
		this.merchName = value;
	}

	public String getMerchName() {
		return this.merchName;
	}

	public void setMerchCode(String value) {
		this.merchCode = value;
	}

	public String getMerchCode() {
		return this.merchCode;
	}

	public void setMerchType(Integer value) {
		this.merchType = value;
	}

	public Integer getMerchType() {
		return this.merchType;
	}

	public void setLogoPic(String value) {
		this.logoPic = value;
	}

	public String getLogoPic() {
		return this.logoPic;
	}

	public void setBriefIntro(String value) {
		this.briefIntro = value;
	}

	public String getBriefIntro() {
		return this.briefIntro;
	}

	public void setCategory(String value) {
		this.category = value;
	}

	public String getCategory() {
		return this.category;
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

	public void setContact(String value) {
		this.contact = value;
	}

	public String getContact() {
		return this.contact;
	}

	public void setTel(String value) {
		this.tel = value;
	}

	public String getTel() {
		return this.tel;
	}

	public void setNotice(String value) {
		this.notice = value;
	}

	public String getNotice() {
		return this.notice;
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

	public void setCreateTime(java.util.Date value) {
		this.createTime = value;
	}

	public java.util.Date getCreateTime() {
		return this.createTime;
	}

	public String getSalesVolume() {
		return salesVolume;
	}

	public void setSalesVolume(String salesVolume) {
		this.salesVolume = salesVolume;
	}

	public String getPraise() {
		return praise;
	}

	public void setPraise(String praise) {
		this.praise = praise;
	}

}
