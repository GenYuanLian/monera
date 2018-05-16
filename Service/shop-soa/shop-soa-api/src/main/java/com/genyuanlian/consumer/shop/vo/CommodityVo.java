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

	private Double commodityPrice;

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
}
