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
	
	//收货地址是否必填：1-是；0-否
	private Integer AddressRequire=0;
	
	//钱包地址是否必填：1-是；0-否
	private Integer walletAddressRequire=0;

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

	
	
	public Integer getAddressRequire() {
		return AddressRequire;
	}

	public void setAddressRequire(Integer addressRequire) {
		AddressRequire = addressRequire;
	}

	public Integer getWalletAddressRequire() {
		return walletAddressRequire;
	}

	public void setWalletAddressRequire(Integer walletAddressRequire) {
		this.walletAddressRequire = walletAddressRequire;
	}

	public void setCommodityList(List<CommodityVo> commodityList) {
		this.commodityList = commodityList;
	}

}
