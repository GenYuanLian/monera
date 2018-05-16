package com.genyuanlian.consumer.shop.vo;

import java.io.Serializable;

/**
 * Created by hunter.wang on 2018/5/10.
 */
public class BWSWalletCreateResponseVo implements Serializable
{
    /**
     * 钱包
     */
    private String wallet;

    /**
     * 钱包公钥
     */
    private String mainAddr;

    public String getMainAddr() {
        return mainAddr;
    }

    public String getWallet() {
        return wallet;
    }

    public void setMainAddr(String mainAddr) {
        this.mainAddr = mainAddr;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }
}
