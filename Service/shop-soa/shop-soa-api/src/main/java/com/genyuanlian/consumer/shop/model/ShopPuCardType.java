package com.genyuanlian.consumer.shop.model;

import java.io.Serializable;

/**
 * ShopPuCardType Entity.
 */
public class ShopPuCardType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -694909745401111837L;

	// 列信息
	private Long id;

	private Long merchantId;

	private String title;

	private String code;

	private Double totelValue;

	private Double price;

	private Double discount;

	private String pic;

	private Integer status;

	private String remark;

	private java.util.Date createTime;

	private Integer inventory;

	private Integer salesVolume;

	//很重要，小胖子 你别删掉（商品列表或下单页面 后台接口参数，用来区分是哪种商品）
	private Integer commodityType = 1;// 商品类型：1-提货卡,2-溯源卡,3-零售商品

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

	public void setTitle(String value) {
		this.title = value;
	}

	public String getTitle() {
		return this.title;
	}

	public void setCode(String value) {
		this.code = value;
	}

	public String getCode() {
		return this.code;
	}

	public void setTotelValue(Double value) {
		this.totelValue = value;
	}

	public Double getTotelValue() {
		return this.totelValue;
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

	public void setPic(String value) {
		this.pic = value;
	}

	public String getPic() {
		return this.pic;
	}

	public void setStatus(Integer value) {
		this.status = value;
	}

	public Integer getStatus() {
		return this.status;
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

	public Integer getInventory() {
		return inventory;
	}

	public void setInventory(Integer inventory) {
		this.inventory = inventory;
	}

	public Integer getSalesVolume() {
		return salesVolume;
	}

	public void setSalesVolume(Integer salesVolume) {
		this.salesVolume = salesVolume;
	}

	public Integer getCommodityType() {
		return commodityType;
	}

	public void setCommodityType(Integer commodityType) {
		this.commodityType = commodityType;
	}

}
