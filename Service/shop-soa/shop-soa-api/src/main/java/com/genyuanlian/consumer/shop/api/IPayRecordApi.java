package com.genyuanlian.consumer.shop.api;

import com.genyuanlian.consumer.shop.model.ShopPayRecord;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;

public interface IPayRecordApi {

	/**
	 * 支付成功，修改支付记录及订单信息
	 * @param req
	 * @return
	 */
	public ShopMessageVo<String> paySuccess(ShopPayRecord payRecord);
}
