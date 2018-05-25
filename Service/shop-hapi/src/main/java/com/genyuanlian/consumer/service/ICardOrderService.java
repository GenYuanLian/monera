package com.genyuanlian.consumer.service;

import com.genyuanlian.consumer.shop.vo.ShopMessageVo;

public interface ICardOrderService {

	/**
	 * 修改卡订单状态
	 * @param orderNo 订单编号
	 * @param status 1-已支付，
	 * @return
	 */
	public ShopMessageVo<String> modifyCardOrderStatus(String orderNo,Integer status);
}
