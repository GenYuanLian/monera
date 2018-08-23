package com.genyuanlian.consumer.shop.utils;

import com.hnair.consumer.utils.system.ConfigPropertieUtils;

/**
 * Created by hunter.wang on 2018/5/10.
 */
public class BWSProperties {

	public static String P_YUANDIAN = "p_yuandian";
	public static String P_BSTK = "p_bstk";
	public static String P_CALCFORCE = "p_calcforce";

	/**
	 * 服务方源点钱包
	 */
	public static String serverWallet = ConfigPropertieUtils.getString("bws.serverwallet");

	/**
	 * 服务方源点钱包公钥
	 */
	public static String serverWalletMainAddress = ConfigPropertieUtils.getString("bws.serverWalletMainAddr");

	/**
	 * 服务方bstk钱包
	 */
	public static String bstkWallet = ConfigPropertieUtils.getString("bstk.serverwallet");

	/**
	 * 服务方bstk钱包公钥
	 */
	public static String bstkWalletMainAddress = ConfigPropertieUtils.getString("bstk.serverWalletMainAddr");

	/**
	 * 服务方算力钱包
	 */
	public static String calcForceWallet = ConfigPropertieUtils.getString("cal.force.serverwallet");

	/**
	 * 服务方算力钱包公钥
	 */
	public static String calcForceWalletMainAddress = ConfigPropertieUtils.getString("cal.force.serverWalletMainAddr");

	/**
	 * bws创建钱包的api地址
	 */
	public static String bwsWalletCreateAPI = ConfigPropertieUtils.getString("bws.walletCreate");

	/**
	 * bws钱包转账的api地址
	 */
	public static String bswWalletTransferAPI = ConfigPropertieUtils.getString("bws.walletTransfer");

	/**
	 * bws钱包多账户转账api地址
	 */
	public static String bswWalletTransferMultiAPI = ConfigPropertieUtils.getString("bws.walletTransfer_multi");

	/**
	 * bws查询钱包余额的api地址
	 */
	public static String bswWalletBalanceAPI = ConfigPropertieUtils.getString("bws.walletBalance");

}
