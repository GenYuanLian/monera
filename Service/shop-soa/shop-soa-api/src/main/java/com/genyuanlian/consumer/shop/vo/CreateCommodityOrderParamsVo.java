package com.genyuanlian.consumer.shop.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class CreateCommodityOrderParamsVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 221246473624777061L;

	private Integer commodityType; // 商品类型：1-提货卡,2-溯源卡,3-零售商品

	private Long commodityId; // 商品id

	private Integer saleCount; // 购买数量

	private BigDecimal amount; // 金额

	private Long memberId; // 会员Id

	private String remark; // 备注信息

	private Long addressId; // 收货地址Id

	public Integer getCommodityType() {
		return commodityType;
	}

	public void setCommodityType(Integer commodityType) {
		this.commodityType = commodityType;
	}

	public Long getCommodityId() {
		return commodityId;
	}

	public void setCommodityId(Long commodityId) {
		this.commodityId = commodityId;
	}

	public Integer getSaleCount() {
		return saleCount;
	}

	public void setSaleCount(Integer saleCount) {
		this.saleCount = saleCount;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getAddressId() {
		return addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}
}
