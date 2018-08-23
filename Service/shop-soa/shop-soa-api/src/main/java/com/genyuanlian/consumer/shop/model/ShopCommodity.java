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

	private Integer commodityType; // 商品类型：1-区块链计算机,2-通用商品,3-算力服务

	private Long productId;

	private String title;

	private String briefIntro;

	private String logo;

	private String actLogo;

	private String remark;

	private Double price;

	private Double priceTotal; // 总价(当price<price_total,需付尾款)

	private Double discount;

	private Integer inventoryQuantity;

	private Integer saleQuantity;

	private Integer status; // 状态：1-上架,2-下架

	private Integer purchaseRestrict; // 购买限制数量：0-不限制

	private String payExplain;

	private Integer isSendMail; // 是否需要发送快递(1-是;0-否)

	private String traceSource;// 溯源地址

	private Integer presentRate; // 赠送比例：买一赠几

	private Integer sort; // 排序

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

	public String getActLogo() {
		return actLogo;
	}

	public void setActLogo(String actLogo) {
		this.actLogo = actLogo;
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

	public Double getPriceTotal() {
		return priceTotal;
	}

	public void setPriceTotal(Double priceTotal) {
		this.priceTotal = priceTotal;
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

	public Integer getPurchaseRestrict() {
		return purchaseRestrict;
	}

	public void setPurchaseRestrict(Integer purchaseRestrict) {
		this.purchaseRestrict = purchaseRestrict;
	}

	public void setPayExplain(String value) {
		this.payExplain = value;
	}

	public String getPayExplain() {
		return this.payExplain;
	}

	public Integer getIsSendMail() {
		return isSendMail;
	}

	public void setIsSendMail(Integer isSendMail) {
		this.isSendMail = isSendMail;
	}

	public String getTraceSource() {
		return traceSource;
	}

	public void setTraceSource(String traceSource) {
		this.traceSource = traceSource;
	}

	public Integer getPresentRate() {
		return presentRate;
	}

	public void setPresentRate(Integer presentRate) {
		this.presentRate = presentRate;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public void setCreateTime(java.util.Date value) {
		this.createTime = value;
	}

	public java.util.Date getCreateTime() {
		return this.createTime;
	}

}
