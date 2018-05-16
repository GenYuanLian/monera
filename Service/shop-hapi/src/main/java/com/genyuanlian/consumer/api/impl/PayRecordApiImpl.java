package com.genyuanlian.consumer.api.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.genyuanlian.consumer.service.ICardOrderService;
import com.genyuanlian.consumer.service.IPayRecordService;
import com.genyuanlian.consumer.shop.api.IPayRecordApi;
import com.genyuanlian.consumer.shop.model.ShopPayRecord;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.hnair.consumer.dao.service.ICommonService;

@Component("payRecordApi")
public class PayRecordApiImpl implements IPayRecordApi {

	@Resource
	private IPayRecordService payRecordService;

	@Resource
	private ICommonService commonService;
	
	@Resource
	private ICardOrderService cardOrderService;
	
	
	@Override
	@Transactional
	public ShopMessageVo<String> paySuccess(ShopPayRecord payRecord) {
		ShopMessageVo<String> result = new ShopMessageVo<>();
		//保存支付记录
		payRecordService.save(payRecord);

		// 修改订单信息
		ShopMessageVo<String> modifyCardOrderStatus = cardOrderService.modifyCardOrderStatus(payRecord.getOrderNo(), 1);
		if (!modifyCardOrderStatus.isResult()) {
			return modifyCardOrderStatus;
		}
		
		result.setResult(true);
		result.setT(payRecord.getPayNum());

		return result;
	}

}
