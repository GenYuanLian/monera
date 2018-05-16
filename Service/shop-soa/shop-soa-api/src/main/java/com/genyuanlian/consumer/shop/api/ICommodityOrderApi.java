package com.genyuanlian.consumer.shop.api;

import com.genyuanlian.consumer.shop.vo.CommodityOrderPayParamsVo;
import com.genyuanlian.consumer.shop.vo.CreateCommodityOrderParamsVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;

public interface ICommodityOrderApi {

	/**
	 * 创建商品订单
	 * 
	 * @param CreateCommodityOrderParamsVo
	 * @return
	 */
	public ShopMessageVo<String> createOrder(CreateCommodityOrderParamsVo params);

	/**
	 * 商品类订单支付
	 * 
	 * @param CommodityOrderPayParamsVo
	 * @return
	 */
	public ShopMessageVo<String> orderPay(CommodityOrderPayParamsVo params);

}
