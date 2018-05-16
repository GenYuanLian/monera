package com.genyuanlian.consumer.shop.model;

import java.io.Serializable;

/**
 * ShopOrderCommoditySnapshot Entity.
 */
public class ShopOrderCommoditySnapshot implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4448269220132678967L;

	// 列信息
	private Long id;

	private Long merchantId;

	private Long commodityId;

	private Integer commodityType;

	private String commodityJson;

	private String remark;

	private java.util.Date createTime;

	public void setId(Long value) {
		this.id = value;
	}

	public Long getId() {
		return this.id;
	}

	public void setMerchantId(Long value) {
		this.merchantId = value;
	}

	public Long getMerchantId() {
		return this.merchantId;
	}

	public void setCommodityId(Long value) {
		this.commodityId = value;
	}

	public Long getCommodityId() {
		return this.commodityId;
	}

	public void setCommodityType(Integer value) {
		this.commodityType = value;
	}

	public Integer getCommodityType() {
		return this.commodityType;
	}

	public void setCommodityJson(String value) {
		this.commodityJson = value;
	}

	public String getCommodityJson() {
		return this.commodityJson;
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
