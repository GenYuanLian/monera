package com.genyuanlian.consumer.shop.model;

import java.io.Serializable;

/**
 * ShopCommodity Entity.
 */
public class ShopCommodity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6478484519777325397L;

	// 列信息
	private Long id;

	private Long merchantId;

	private Integer commodityType; //商品类型：1-区块链计算机,2-通用商品

	private Long productId;

	private String title;

	private String briefIntro;

	private String logo;

	private String remark;

	private Double price;

	private Double discount;

	private Integer inventoryQuantity;

	private Integer saleQuantity;

	private Integer status; //状态：1-上架,2-下架

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

	public void setCommodityType(Integer value) {
		this.commodityType = value;
	}

	public Integer getCommodityType() {
		return this.commodityType;
	}

	public void setProductId(Long value) {
		this.productId = value;
	}

	public Long getProductId() {
		return this.productId;
	}

	public void setTitle(String value) {
		this.title = value;
	}

	public String getTitle() {
		return this.title;
	}

	public void setBriefIntro(String value) {
		this.briefIntro = value;
	}

	public String getBriefIntro() {
		return this.briefIntro;
	}

	public void setLogo(String value) {
		this.logo = value;
	}

	public String getLogo() {
		return this.logo;
	}

	public void setRemark(String value) {
		this.remark = value;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setPrice(Double value) {
		this.price = value;
	}

	public Double getPrice() {
		return this.price;
	}

	public void setDiscount(Double value) {
		this.discount = value;
	}

	public Double getDiscount() {
		return this.discount;
	}

	public void setInventoryQuantity(Integer value) {
		this.inventoryQuantity = value;
	}

	public Integer getInventoryQuantity() {
		return this.inventoryQuantity;
	}

	public void setSaleQuantity(Integer value) {
		this.saleQuantity = value;
	}

	public Integer getSaleQuantity() {
		return this.saleQuantity;
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
