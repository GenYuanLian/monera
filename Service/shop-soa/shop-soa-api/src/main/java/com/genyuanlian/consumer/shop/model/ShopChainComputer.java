package com.genyuanlian.consumer.shop.model;

import java.io.Serializable;

/**
 * ShopChainComputer Entity.
 */
public class ShopChainComputer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8724847428635232327L;

	// 列信息
	private Long id;

	private Long merchantId;

	private String title;

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

	private Double price;

	private Double discount;

	private Integer inventoryQuantity;

	private Integer saleQuantity;

	private Integer status;

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

	public void setTitle(String value) {
		this.title = value;
	}

	public String getTitle() {
		return this.title;
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

	public void setCreateTime(java.util.Date value) {
		this.createTime = value;
	}

	public java.util.Date getCreateTime() {
		return this.createTime;
	}

}
