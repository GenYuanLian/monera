package com.genyuanlian.consumer.shop.vo;

import java.io.Serializable;

public class NicknameParamsVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1080250305657293281L;

	private Long memberId; // 会员Id

	private String nickName; // 昵称

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

}
