package com.genyuanlian.consumer.shop.model;

import java.io.Serializable;

/**
 * ShopBaseArea Entity.
 */
public class ShopBaseArea implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6829713617886097225L;

	// 列信息
	private Long id;

	private Integer areaCode;

	private String areaName;

	private Integer parentCode;

	private String shortName;

	private Integer zoneCode;

	private Integer zipCode;

	private String pinyin;

	private String lng;

	private String lat;

	private Integer level;

	private String position;

	private Integer sort;

	private java.util.Date createTime;

	public void setId(Long value) {
		this.id = value;
	}

	public Long getId() {
		return this.id;
	}

	public void setAreaCode(Integer value) {
		this.areaCode = value;
	}

	public Integer getAreaCode() {
		return this.areaCode;
	}

	public void setAreaName(String value) {
		this.areaName = value;
	}

	public String getAreaName() {
		return this.areaName;
	}

	public void setParentCode(Integer value) {
		this.parentCode = value;
	}

	public Integer getParentCode() {
		return this.parentCode;
	}

	public void setShortName(String value) {
		this.shortName = value;
	}

	public String getShortName() {
		return this.shortName;
	}

	public void setZoneCode(Integer value) {
		this.zoneCode = value;
	}

	public Integer getZoneCode() {
		return this.zoneCode;
	}

	public void setZipCode(Integer value) {
		this.zipCode = value;
	}

	public Integer getZipCode() {
		return this.zipCode;
	}

	public void setPinyin(String value) {
		this.pinyin = value;
	}

	public String getPinyin() {
		return this.pinyin;
	}

	public void setLng(String value) {
		this.lng = value;
	}

	public String getLng() {
		return this.lng;
	}

	public void setLat(String value) {
		this.lat = value;
	}

	public String getLat() {
		return this.lat;
	}

	public void setLevel(Integer value) {
		this.level = value;
	}

	public Integer getLevel() {
		return this.level;
	}

	public void setPosition(String value) {
		this.position = value;
	}

	public String getPosition() {
		return this.position;
	}

	public void setSort(Integer value) {
		this.sort = value;
	}

	public Integer getSort() {
		return this.sort;
	}

	public void setCreateTime(java.util.Date value) {
		this.createTime = value;
	}

	public java.util.Date getCreateTime() {
		return this.createTime;
	}

}
