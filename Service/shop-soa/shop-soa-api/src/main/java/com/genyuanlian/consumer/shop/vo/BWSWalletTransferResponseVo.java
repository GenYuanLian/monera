package com.genyuanlian.consumer.shop.vo;

import java.io.Serializable;

/**
 * Created by hunter.wang on 2018/5/10.
 */
public class BWSWalletTransferResponseVo implements Serializable
{
    private String txid;

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }
}
