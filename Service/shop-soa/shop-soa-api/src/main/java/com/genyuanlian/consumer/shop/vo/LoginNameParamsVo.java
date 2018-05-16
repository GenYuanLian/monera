package com.genyuanlian.consumer.shop.vo;

import java.io.Serializable;

public class LoginNameParamsVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6618580813004146605L;

	private Long memberId; // 会员Id

	private String loginName; // 会员登录账号

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

}
