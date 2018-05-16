package com.genyuanlian.consumer.shop.vo;

import java.io.Serializable;

public class MemberIdParamsVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9175727759898861436L;

	private Long memberId; // 会员Id

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
}
