package com.genyuanlian.consumer.service;

import java.math.BigDecimal;
import java.util.Map;

import com.genyuanlian.consumer.shop.model.ShopPayRecord;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;


public interface IPayRecordService {
	
	/**
	 * 生成支付记录
	 * @param memberId
	 * @param totalAmount
	 * @param orderNo
	 * @param subject
	 * @param accountType
	 * @return
	 */
	public ShopMessageVo<ShopPayRecord> buildPayRecord(Long memberId, Double totalAmount, String orderNo, String subject,Integer accountType);
	
	/**
	 * 保存支付记录
	 * @param payRecord
	 */
	public void save(ShopPayRecord payRecord);
	

}
