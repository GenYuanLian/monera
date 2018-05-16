package com.genyuanlian.consumer.shop.api;

import java.util.Map;

import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.genyuanlian.consumer.shop.vo.WeixinpayParamsVo;

public interface IWeixinpayApi {
	/**
	 * 微信支付，同意下单接口
	 * @param req
	 * @return
	 */
	public ShopMessageVo<Map<String,Object>> unifiedOrder(WeixinpayParamsVo req);
	
	/**
	 * 微信支付回调接口处理
	 * @param map
	 * @return
	 */
	public ShopMessageVo<String> weixinpayAysnNotify(Map<String, String> map);
}
