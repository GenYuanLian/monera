package com.genyuanlian.consumer.shop.api;

import com.genyuanlian.consumer.shop.vo.SendSmsParamsVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;

public interface ISmsInfoApi {
	/**
	 * 短信发送记录api
	 * 
	 * @param params
	 */
	public ShopMessageVo<String> sendSms(SendSmsParamsVo params);
}
