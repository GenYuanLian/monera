package com.genyuanlian.consumer.shop.vo;

import java.io.Serializable;

/**
 * Created by hunter.wang on 2018/5/10. 查询钱包余额
 */
public class BWSWalletBalanceResponseVo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3486087953043401578L;

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
