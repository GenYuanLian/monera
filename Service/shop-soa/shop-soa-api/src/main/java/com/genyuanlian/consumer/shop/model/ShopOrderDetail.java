package com.genyuanlian.consumer.shop.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ShopOrderDetail Entity.
 */
public class ShopOrderDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5408192011767184451L;

	// 列信息
	private Long id;

	private Long memberId;

	private Long orderId;

	private String orderNo;

	private Long merchantId;

	private String merchantName;

	private Long commodityId;

	private Integer commodityType; // 商品类型：1-提货卡,2-溯源卡,3-零售商品

	private String commodityName;

	private Double price;

	private Integer saleCount;

	private Integer refundCount;

	private Double amount;

	private String description;

	private Integer status; // 订单状态:0-未支付,1-未支付取消,2-支付过期，3-已支付，4-发货前退单，5-商家确认发货，6-买家确认收货，7-收货后退单,8-订单已完成,9-抢购失败,已退款

	private String remark;

	private Integer presentCount; // 赠送数量

	private String walletPrivate; // 数字货币钱包私钥

	private String walletAddress; // 数字货币钱包地址

	private String walletKeyJsonName; // 钱包账号文件名

	private String walletAddressQrcode;// 数字货币钱包地址二维码图片地址

	private String source; // 订单来源：mmdj,sqgw

	private java.util.Date createTime;

	private java.util.Date payTime;

	/**
	 * 取消原因
	 */
	private String cancelReason;

	private String traceSource;// 溯源地址

	private Integer deleteFlag; // 删除标记:0-未删除,1-删除

	/**
	 * 剩余支付时间（秒）
	 */
	private Long surplusPayTime;

	private Integer payType; // 支付方式:1-微信,2-支付宝,3-提货卡

	/**
	 * 商户类型:1-零售商,2-提货卡,3-溯源卡
	 */
	private Integer merchType;

	/**
	 * 商户Logo
	 */
	private String merchLogo;

	private Integer isSendMail; // 是否需要发送快递(1-是;0-否)

	private Integer calcForceOrder = 0;// 是否是算力服务订单:0-否，1-是

	private String publicKeyAddr; // 会员钱包公钥地址

	private Integer calcForceTaskFlag = 0; // 算力服务任务创建标记:0-未创建，1-已创建

	private Double balancePayment = 0d; // 需线下付款金额,线下付款结清后，设置为0

	private String referraCode;

	private Long referraId;

	private BigDecimal totalAmount;

	private Integer orderType; // 订单类型：1-普通订单；2-抢购订单；3-竞拍订单

	private Long activityId;

	public void setId(Long value) {
		this.id = value;
	}

	public Long getId() {
		return this.id;
	}

	public void setMemberId(Long value) {
		this.memberId = value;
	}

	public Long getMemberId() {
		return this.memberId;
	}

	public void setOrderId(Long value) {
		this.orderId = value;
	}

	public Long getOrderId() {
		return this.orderId;
	}

	public void setOrderNo(String value) {
		this.orderNo = value;
	}

	public String getOrderNo() {
		return this.orderNo;
	}

	public void setMerchantId(Long value) {
		this.merchantId = value;
	}

	public Long getMerchantId() {
		return this.merchantId;
	}

	public void setMerchantName(String value) {
		this.merchantName = value;
	}

	public String getMerchantName() {
		return this.merchantName;
	}

	public void setCommodityId(Long value) {
		this.commodityId = value;
	}

	public Long getCommodityId() {
		return this.commodityId;
	}

	public void setCommodityType(Integer value) {
		this.commodityType = value;
	}

	public Integer getCommodityType() {
		return this.commodityType;
	}

	public void setCommodityName(String value) {
		this.commodityName = value;
	}

	public String getCommodityName() {
		return this.commodityName;
	}

	public void setPrice(Double value) {
		this.price = value;
	}

	public Double getPrice() {
		return this.price;
	}

	public void setSaleCount(Integer value) {
		this.saleCount = value;
	}

	public Integer getSaleCount() {
		return this.saleCount;
	}

	public void setRefundCount(Integer value) {
		this.refundCount = value;
	}

	public Integer getRefundCount() {
		return this.refundCount;
	}

	public void setAmount(Double value) {
		this.amount = value;
	}

	public Double getAmount() {
		return this.amount;
	}

	public void setDescription(String value) {
		this.description = value;
	}

	public String getDescription() {
		return this.description;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setRemark(String value) {
		this.remark = value;
	}

	public String getRemark() {
		return this.remark;
	}

	public Integer getPresentCount() {
		return presentCount;
	}

	public void setPresentCount(Integer presentCount) {
		this.presentCount = presentCount;
	}

	public String getWalletPrivate() {
		return walletPrivate;
	}

	public void setWalletPrivate(String walletPrivate) {
		this.walletPrivate = walletPrivate;
	}

	public String getWalletAddress() {
		return walletAddress;
	}

	public void setWalletAddress(String walletAddress) {
		this.walletAddress = walletAddress;
	}

	public String getWalletKeyJsonName() {
		return walletKeyJsonName;
	}

	public void setWalletKeyJsonName(String walletKeyJsonName) {
		this.walletKeyJsonName = walletKeyJsonName;
	}

	public String getWalletAddressQrcode() {
		return walletAddressQrcode;
	}

	public void setWalletAddressQrcode(String walletAddressQrcode) {
		this.walletAddressQrcode = walletAddressQrcode;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setCreateTime(java.util.Date value) {
		this.createTime = value;
	}

	public java.util.Date getCreateTime() {
		return this.createTime;
	}

	public java.util.Date getPayTime() {
		return payTime;
	}

	public void setPayTime(java.util.Date payTime) {
		this.payTime = payTime;
	}

	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	public String getTraceSource() {
		return traceSource;
	}

	public void setTraceSource(String traceSource) {
		this.traceSource = traceSource;
	}

	public Integer getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public Long getSurplusPayTime() {
		return surplusPayTime;
	}

	public void setSurplusPayTime(Long surplusPayTime) {
		this.surplusPayTime = surplusPayTime;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Integer getMerchType() {
		return merchType;
	}

	public void setMerchType(Integer merchType) {
		this.merchType = merchType;
	}

	public String getMerchLogo() {
		return merchLogo;
	}

	public void setMerchLogo(String merchLogo) {
		this.merchLogo = merchLogo;
	}

	public Integer getIsSendMail() {
		return isSendMail;
	}

	public void setIsSendMail(Integer isSendMail) {
		this.isSendMail = isSendMail;
	}

	public Integer getCalcForceOrder() {
		return calcForceOrder;
	}

	public void setCalcForceOrder(Integer calcForceOrder) {
		this.calcForceOrder = calcForceOrder;
	}

	public String getPublicKeyAddr() {
		return publicKeyAddr;
	}

	public void setPublicKeyAddr(String publicKeyAddr) {
		this.publicKeyAddr = publicKeyAddr;
	}

	public Integer getCalcForceTaskFlag() {
		return calcForceTaskFlag;
	}

	public void setCalcForceTaskFlag(Integer calcForceTaskFlag) {
		this.calcForceTaskFlag = calcForceTaskFlag;
	}

	public Double getBalancePayment() {
		return balancePayment;
	}

	public void setBalancePayment(Double balancePayment) {
		this.balancePayment = balancePayment;
	}

	public void setReferraCode(String value) {
		this.referraCode = value;
	}

	public String getReferraCode() {
		return this.referraCode;
	}

	public void setReferraId(Long referraId) {
		this.referraId = referraId;
	}

	public Long getReferraId() {
		return referraId;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

}
