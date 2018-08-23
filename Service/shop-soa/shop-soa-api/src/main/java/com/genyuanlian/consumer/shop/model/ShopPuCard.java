package com.genyuanlian.consumer.shop.model;

import java.io.Serializable;

/**
 * ShopPuCard Entity.
 */
public class ShopPuCard implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1180254835823820605L;

	// 列信息
	private Long id;

	private Long merchantId;

	private Long cardTypeId;

	private String title;

	private String code;

	private Double totelValue;

	private Double balance;

	private Double bstkRate;

	private Double bstkValue;

	private Double price;

	private Double discount;

	private Long memberId;

	private java.util.Date saleTime;

	private java.util.Date validDate;

	private String activationCode;

	private java.util.Date activationTime;

	private Integer status; //状态：0-未售、1-已锁定、2-售出、3-激活、4-部分使用、5-全部使用、6-已过期、7-已作废

	private String remark;

	private java.util.Date createTime;
	
	/**
	 * 类型：1-实体卡；2-电子卡
	 */
	private Integer type;
	
	/**
	 * 渠道：0表示线下制卡，2表示PC端分布式电商，4表示手机端分布电商，6表示H5电商电子卡
	 */
	private Integer channel;

	// 很重要，小胖子 你别删掉（商品列表或下单页面 后台接口参数，用来区分是哪种商品）
	private Integer commodityType = 1;// 商品类型：1-提货卡,2-溯源卡,3-零售商品
	
	private String picUrl;

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

	public void setCardTypeId(Long value) {
		this.cardTypeId = value;
	}

	public Long getCardTypeId() {
		return this.cardTypeId;
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

	public void setBalance(Double value) {
		this.balance = value;
	}

	public Double getBalance() {
		return this.balance;
	}

	public void setBstkRate(Double value) {
		this.bstkRate = value;
	}

	public Double getBstkRate() {
		return this.bstkRate;
	}

	public void setBstkValue(Double value) {
		this.bstkValue = value;
	}

	public Double getBstkValue() {
		return this.bstkValue;
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

	public void setMemberId(Long value) {
		this.memberId = value;
	}

	public Long getMemberId() {
		return this.memberId;
	}

	public void setSaleTime(java.util.Date value) {
		this.saleTime = value;
	}

	public java.util.Date getSaleTime() {
		return this.saleTime;
	}

	public void setValidDate(java.util.Date value) {
		this.validDate = value;
	}

	public java.util.Date getValidDate() {
		return this.validDate;
	}

	public void setActivationCode(String value) {
		this.activationCode = value;
	}

	public String getActivationCode() {
		return this.activationCode;
	}

	public void setActivationTime(java.util.Date value) {
		this.activationTime = value;
	}

	public java.util.Date getActivationTime() {
		return this.activationTime;
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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getChannel() {
		return channel;
	}

	public void setChannel(Integer channel) {
		this.channel = channel;
	}

	public Integer getCommodityType() {
		return commodityType;
	}

	public void setCommodityType(Integer commodityType) {
		this.commodityType = commodityType;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

}
