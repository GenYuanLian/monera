package com.genyuanlian.consumer.shop.api;

import java.util.Map;

import com.genyuanlian.consumer.shop.vo.AlipayParamsVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;

public interface IAlipayApi {

	/**
	 * H5支付宝支付
	 * @param param
	 * @return
	 */
	public ShopMessageVo<Map<String, Object>> H5Alipay(AlipayParamsVo param);
	
	/**
	 * 支付宝回调
	 * @param req
	 * @return
	 */
	public ShopMessageVo<String> alipayAysnNotify(Map<String, String> req) throws Exception;
}
