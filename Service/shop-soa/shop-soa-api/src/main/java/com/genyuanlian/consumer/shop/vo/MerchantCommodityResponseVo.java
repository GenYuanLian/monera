package com.genyuanlian.consumer.shop.vo;

import java.io.Serializable;
import java.util.List;

public class MerchantCommodityResponseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6911238026500220314L;

	private Long merchantId;

	private String merchantName;

	private Integer merchantType; // 商户类型:1-零售商,2-提货卡,3-溯源卡

	private String merchantLogo;

	private List<CommodityVo> commodityList; // 产品列表

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public Integer getMerchantType() {
		return merchantType;
	}

	public void setMerchantType(Integer merchantType) {
		this.merchantType = merchantType;
	}

	public String getMerchantLogo() {
		return merchantLogo;
	}

	public void setMerchantLogo(String merchantLogo) {
		this.merchantLogo = merchantLogo;
	}

	public List<CommodityVo> getCommodityList() {
		return commodityList;
	}

	public void setCommodityList(List<CommodityVo> commodityList) {
		this.commodityList = commodityList;
	}

}
