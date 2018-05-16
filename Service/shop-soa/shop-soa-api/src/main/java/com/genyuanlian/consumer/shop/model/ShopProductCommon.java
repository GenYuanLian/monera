package com.genyuanlian.consumer.shop.model;

import java.io.Serializable;

/**
 * ShopProductCommon Entity.
 */
public class ShopProductCommon implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8113261980891604022L;

	// 列信息
	private Long id;

	private Long merchantId;

	private Long commodityId;

	private String feature;

	private String specification;

	private String attention;

	private String description;

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

	public void setFeature(String value) {
		this.feature = value;
	}

	public String getFeature() {
		return this.feature;
	}

	public void setSpecification(String value) {
		this.specification = value;
	}

	public String getSpecification() {
		return this.specification;
	}

	public void setAttention(String value) {
		this.attention = value;
	}

	public String getAttention() {
		return this.attention;
	}

	public void setDescription(String value) {
		this.description = value;
	}

	public String getDescription() {
		return this.description;
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
