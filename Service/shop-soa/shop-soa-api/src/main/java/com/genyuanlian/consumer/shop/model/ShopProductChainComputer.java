package com.genyuanlian.consumer.shop.model;

import java.io.Serializable;

/**
 * ShopProductChainComputer Entity.
 */
public class ShopProductChainComputer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3316575437621717709L;

	// 列信息
	private Long id;

	private Long merchantId;

	private Long commodityId;

	private String feature;

	private String specification;

	private String model;

	private String brand;

	private String calculationForce;

	private String wallPower;

	private String powerEfficiency;

	private String inputVoltage;

	private String chipNumber;

	private String boardNumber;

	private String boxSize;

	private String weight;

	private String temperature;

	private String humidity;

	private String networkConnection;

	private String noise;

	private String powerConnection;

	private String attention;

	private String description;

	private String remark;

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

	public void setCommodityId(Long value) {
		this.commodityId = value;
	}

	public Long getCommodityId() {
		return this.commodityId;
	}

	public void setFeature(String value) {
		this.feature = value;
	}

	public String getFeature() {
		return this.feature;
	}

	public void setSpecification(String value) {
		this.specification = value;
	}

	public String getSpecification() {
		return this.specification;
	}

	public void setModel(String value) {
		this.model = value;
	}

	public String getModel() {
		return this.model;
	}

	public void setBrand(String value) {
		this.brand = value;
	}

	public String getBrand() {
		return this.brand;
	}

	public void setCalculationForce(String value) {
		this.calculationForce = value;
	}

	public String getCalculationForce() {
		return this.calculationForce;
	}

	public void setWallPower(String value) {
		this.wallPower = value;
	}

	public String getWallPower() {
		return this.wallPower;
	}

	public void setPowerEfficiency(String value) {
		this.powerEfficiency = value;
	}

	public String getPowerEfficiency() {
		return this.powerEfficiency;
	}

	public void setInputVoltage(String value) {
		this.inputVoltage = value;
	}

	public String getInputVoltage() {
		return this.inputVoltage;
	}

	public void setChipNumber(String value) {
		this.chipNumber = value;
	}

	public String getChipNumber() {
		return this.chipNumber;
	}

	public void setBoardNumber(String value) {
		this.boardNumber = value;
	}

	public String getBoardNumber() {
		return this.boardNumber;
	}

	public void setBoxSize(String value) {
		this.boxSize = value;
	}

	public String getBoxSize() {
		return this.boxSize;
	}

	public void setWeight(String value) {
		this.weight = value;
	}

	public String getWeight() {
		return this.weight;
	}

	public void setTemperature(String value) {
		this.temperature = value;
	}

	public String getTemperature() {
		return this.temperature;
	}

	public void setHumidity(String value) {
		this.humidity = value;
	}

	public String getHumidity() {
		return this.humidity;
	}

	public void setNetworkConnection(String value) {
		this.networkConnection = value;
	}

	public String getNetworkConnection() {
		return this.networkConnection;
	}

	public void setNoise(String value) {
		this.noise = value;
	}

	public String getNoise() {
		return this.noise;
	}

	public void setPowerConnection(String value) {
		this.powerConnection = value;
	}

	public String getPowerConnection() {
		return this.powerConnection;
	}

	public void setAttention(String value) {
		this.attention = value;
	}

	public String getAttention() {
		return this.attention;
	}

	public void setDescription(String value) {
		this.description = value;
	}

	public String getDescription() {
		return this.description;
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

}
