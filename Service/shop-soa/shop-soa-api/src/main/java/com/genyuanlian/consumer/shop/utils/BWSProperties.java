package com.genyuanlian.consumer.shop.utils;

import com.hnair.consumer.utils.system.ConfigPropertieUtils;

/**
 * Created by hunter.wang on 2018/5/10.
 */
public class BWSProperties
{

    /**
     * 服务方钱包
     */
    public static String serverWallet=ConfigPropertieUtils.getString("bws.serverwallet");

    /**
     * 服务方钱包公钥
     */
    public static String serverWalletMainAddress=ConfigPropertieUtils.getString("bws.serverWalletMainAddr");

    /**
     * bws创建钱包的api地址
     */
    public static String bwsWalletCreateAPI=ConfigPropertieUtils.getString("bws.walletCreate");

    /**
     * bws钱包转账的api地址
     */
    public static String bswWalletTransferAPI=ConfigPropertieUtils.getString("bws.walletTransfer");

    /**
     * bws查询钱包余额的api地址
     */
    public static String bswWalletBalanceAPI=ConfigPropertieUtils.getString("bws.walletBalance");

}
