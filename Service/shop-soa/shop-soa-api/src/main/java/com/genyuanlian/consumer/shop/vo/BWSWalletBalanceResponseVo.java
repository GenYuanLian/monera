package com.genyuanlian.consumer.shop.vo;

import java.io.Serializable;

/**
 * Created by hunter.wang on 2018/5/10.
 * 查询钱包余额
 */
public class BWSWalletBalanceResponseVo implements Serializable
{
//    "totalAmount": 899995124,
//            "lockedAmount": 0,
//            "totalConfirmedAmount": 899995124,
//            "lockedConfirmedAmount": 0,
//            "availableAmount": 899995124,
//            "availableConfirmedAmount": 899995124,
//            "byAddress": [
//    {
//        "address": "1ky3nTZq7txYwdRWp93DHHDYrhUpgJnV1",
//            "path": "m/1/0",
//            "amount": 899995124
//    }
//    ]

    private Double totalAmount;
    private Double lockedAmount;

    private Double totalConfirmedAmount;
    private Double lockedConfirmedAmount;

    private Double availableAmount;

    private Double availableConfirmedAmount;

    public Double getAvailableAmount() {
        return availableAmount;
    }

    public Double getAvailableConfirmedAmount() {
        return availableConfirmedAmount;
    }

    public Double getLockedAmount() {
        return lockedAmount;
    }

    public Double getLockedConfirmedAmount() {
        return lockedConfirmedAmount;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public Double getTotalConfirmedAmount() {
        return totalConfirmedAmount;
    }

    public void setAvailableAmount(Double availableAmount) {
        this.availableAmount = availableAmount;
    }

    public void setAvailableConfirmedAmount(Double availableConfirmedAmount) {
        this.availableConfirmedAmount = availableConfirmedAmount;
    }

    public void setLockedAmount(Double lockedAmount) {
        this.lockedAmount = lockedAmount;
    }

    public void setLockedConfirmedAmount(Double lockedConfirmedAmount) {
        this.lockedConfirmedAmount = lockedConfirmedAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setTotalConfirmedAmount(Double totalConfirmedAmount) {
        this.totalConfirmedAmount = totalConfirmedAmount;
    }
}
