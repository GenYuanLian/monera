package com.genyuanlian.consumer.shop.vo;

import java.io.Serializable;

/**
 * BWS 返回对象
 * Created by hunter.wang on 2018/5/10.
 */
public class BWSWalletResponse implements Serializable
{
    /**
     * 是否ok
     */
    private boolean isOK;

    /**
     * 返回数据类型
     */
    private String data;

    public boolean isOK() {
        return isOK;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setOK(boolean isOK) {
        isOK = isOK;
    }
}
