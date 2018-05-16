package com.genyuanlian.consumer.shop.vo;

import java.io.Serializable;

public class UploadHeadPortraitParamsVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6837891810285365490L;

	private Long memberId; // 会员Id

	private String imageUrl; // 头像图片相对地址

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
