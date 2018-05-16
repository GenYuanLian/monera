package com.genyuanlian.consumer.shop.vo;

import java.io.Serializable;

public class CommodityIdParamsVo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5679422079842624909L;

	private Long commodityId;

	private Integer commodityType; // 商品类型：1-提货卡,2-溯源卡,3-零售商品

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
}
