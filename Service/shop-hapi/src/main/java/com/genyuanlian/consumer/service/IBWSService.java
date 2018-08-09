package com.genyuanlian.consumer.service;

import java.math.BigDecimal;
import java.util.List;

import com.genyuanlian.consumer.shop.vo.BWSWalletCreateResponseVo;
import com.genyuanlian.consumer.shop.vo.ReceiverVo;

/**
 * BWS 接口的二次封装 Created by hunter.wang on 2018/5/10.
 */
public interface IBWSService {

	/**
	 * 创建钱包
	 * 
	 * @return
	 */
	public BWSWalletCreateResponseVo walletCreate(String transactionNo, Long ownerId, int ownerType);

	/**
	 * 查询钱包余额
	 * 
	 * @param walletId
	 * @return
	 */
	public BigDecimal walletBalance(String transactionNo, String walletId);

	/**
	 * 钱包充值
	 * 
	 * @param 平台钱包类型:p_yuandian,p_bstk,p_calcforce
	 * @param walletMainAddress
	 * @param amount
	 * @return
	 */
	public String walletRecharge(String p_type, String transactionNo, Long businessId, Long ownerId, int ownerType,
			String walletMainAddress, BigDecimal amount);

	/**
	 * 同时将多个地址进行充值或者发放收益
	 * 
	 * @param 平台钱包类型:p_yuandian,p_bstk,p_calcforce
	 * @param receivers
	 * @param remark
	 * @return
	 */
	public String walletRecharge(String p_type, String transactionNo, List<ReceiverVo> receivers, String remark);

	/**
	 * 钱包消费
	 * 
	 * @param 平台钱包类型:p_yuandian,p_bstk,p_calcforce
	 * @param walletId
	 * @param amount
	 * @return
	 */
	public String walletConsume(String p_type, String transactionNo, Long businessId, Long ownerId, int ownerType,
			String walletId, BigDecimal amount);

	/**
	 * 钱包消费
	 * 
	 * @param walletId
	 * @param amount
	 * @return
	 */
	public String walletTransfer(String transactionNo, Long businessId, Long ownerId, int ownerType, String walletId,
			String receiverAddress, BigDecimal amount);

}
