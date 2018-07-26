package com.genyuanlian.consumer.api.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.genyuanlian.consumer.service.ICardOrderService;
import com.genyuanlian.consumer.service.IPayRecordService;
import com.genyuanlian.consumer.shop.api.IPayRecordApi;
import com.genyuanlian.consumer.shop.model.ShopPayRecord;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.hnair.consumer.dao.service.ICommonService;
import com.hnair.consumer.dao.utils.Page;

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


	@Override
	public Map<String, Object> getList(String startDate, String endDate, String orderNo, String mobile,Integer pageIndex,Integer pageSize) {
		Map<String, Object>resultMap=new HashMap<>();
		
		Page<ShopPayRecord>page=new Page<>(pageIndex, pageSize);
		Map<String,Object>params=new HashMap<>();
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		params.put("orderNo", orderNo);
		params.put("mobile", mobile);
		page.setParams(params);
		page.setIsAsc(false);
		page.setOrderField("r.create_time");
		page=commonService.getPageFinderObjs(ShopPayRecord.class, "getList", page);
		
		resultMap.put("totalPage", page.getTotalPage());
		resultMap.put("totalRecord", page.getTotalRecord());
		resultMap.put("list", page.getResults());
		
		return resultMap;
	}

}
