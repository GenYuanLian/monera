package com.genyuanlian.consumer.service;

import com.genyuanlian.consumer.shop.vo.BWSWalletCreateResponseVo;

/**
 * BWS 接口的二次封装
 * Created by hunter.wang on 2018/5/10.
 */
public interface IBWSService
{

    /**
     * 创建钱包
     * @return
     */
    public BWSWalletCreateResponseVo walletCreate(Long ownerId,int ownerType);

    /**
     * 查询钱包余额
     * @param walletId
     * @return
     */
    public Double walletBalance(String walletId);

    /**
     * 钱包充值
     * @param walletMainAddress
     * @param amount
     * @return
     */
    public String walletRecharge(Long businessId,Long ownerId,int ownerType,String walletMainAddress,Double amount);

    /**
     * 钱包消费
     * @param walletId
     * @param amount
     * @return
     */
    public String walletConsume(Long businessId,Long ownerId,int ownerType,String walletId,Double amount);



}
