package com.genyuanlian.consumer.shop.api;

import java.util.List;
import java.util.Map;

import com.genyuanlian.consumer.shop.model.ShopPayRecord;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;

public interface IPayRecordApi {

	/**
	 * 支付成功，修改支付记录及订单信息
	 * @param req
	 * @return
	 */
	public ShopMessageVo<String> paySuccess(ShopPayRecord payRecord);
	
	/**
	 * 获取第三方支付记录(分页)
	 * @param startDate
	 * @param endDate
	 * @param orderNo
	 * @param mobile
	 * @return
	 */
	public Map<String,Object> getList(String startDate,String endDate,String orderNo,String mobile,Integer pageIndex,Integer pageSize);
}
