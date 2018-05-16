package com.genyuanlian.consumer.shop.model;

import java.io.Serializable;

/**
 * ShopChainComputerPic Entity.
 */
public class ShopChainComputerPic implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8498213220602129907L;

	// 列信息
	private Long id;

	private Long computerId;

	private Integer picType;

	private String title;

	private String url;

	private Integer sort;

	private Integer status;

	private String remark;

	private java.util.Date createTime;

	public void setId(Long value) {
		this.id = value;
	}

	public Long getId() {
		return this.id;
	}

	public void setComputerId(Long value) {
		this.computerId = value;
	}

	public Long getComputerId() {
		return this.computerId;
	}

	public void setPicType(Integer value) {
		this.picType = value;
	}

	public Integer getPicType() {
		return this.picType;
	}

	public void setTitle(String value) {
		this.title = value;
	}

	public String getTitle() {
		return this.title;
	}

	public void setUrl(String value) {
		this.url = value;
	}

	public String getUrl() {
		return this.url;
	}

	public void setSort(Integer value) {
		this.sort = value;
	}

	public Integer getSort() {
		return this.sort;
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

}
