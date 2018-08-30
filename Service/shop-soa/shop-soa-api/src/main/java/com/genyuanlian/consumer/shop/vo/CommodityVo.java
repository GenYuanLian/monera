package com.genyuanlian.consumer.shop.vo;

import java.io.Serializable;

public class CommodityVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4131188386699231301L;

	private Long commodityId;

	private Integer commodityType; // 商品类型：1-提货卡,2-溯源卡,3-零售商品

	private String commodityName;

	private String commodityLogo;

	private Double commodityPrice; // RMB价格

	private Double commodityPriceEth; // ETH价格

	private Integer inventoryQuantity;

	private Integer purchaseRestrict; // 购买限制数量：0-不限制

	private String traceSource; // 商品溯源信息

	public Long getCommodityId() {
		return commodityId;
	}

	public void setCommodityId(Long commodityId) {
		this.commodityId = commodityId;
	}

	public Integer getCommodityType() {
		return commodityType;
	}

	public void setCommodityType(Integer commodityType) {
		this.commodityType = commodityType;
	}

	public String getCommodityName() {
		return commodityName;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}

	public String getCommodityLogo() {
		return commodityLogo;
	}

	public void setCommodityLogo(String commodityLogo) {
		this.commodityLogo = commodityLogo;
	}

	public Double getCommodityPrice() {
		return commodityPrice;
	}

	public void setCommodityPrice(Double commodityPrice) {
		this.commodityPrice = commodityPrice;
	}

	public Double getCommodityPriceEth() {
		return commodityPriceEth;
	}

	public void setCommodityPriceEth(Double commodityPriceEth) {
		this.commodityPriceEth = commodityPriceEth;
	}

	public Integer getInventoryQuantity() {
		return inventoryQuantity;
	}

	public void setInventoryQuantity(Integer inventoryQuantity) {
		this.inventoryQuantity = inventoryQuantity;
	}

	public Integer getPurchaseRestrict() {
		return purchaseRestrict;
	}

	public void setPurchaseRestrict(Integer purchaseRestrict) {
		this.purchaseRestrict = purchaseRestrict;
	}

	public String getTraceSource() {
		return traceSource;
	}

	public void setTraceSource(String traceSource) {
		this.traceSource = traceSource;
	}

}
