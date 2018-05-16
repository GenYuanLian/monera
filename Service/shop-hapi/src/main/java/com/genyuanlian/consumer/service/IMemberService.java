package com.genyuanlian.consumer.service;

import com.genyuanlian.consumer.shop.model.ShopMember;

public interface IMemberService {

	/**
	 * 会员注册service
	 * 
	 * @param member
	 */
	public void register(ShopMember member);
}
