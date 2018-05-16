package com.genyuanlian.consumer.processor;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.genyuanlian.consumer.shop.api.IPuCardApi;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.hnair.consumer.processor.BaseApiProcessor;
import com.hnair.consumer.utils.ResultSender;

/**
 * 获取提货卡类型集合
 * @author taojiajun
 *
 */
@Component("hapigetPuCardTypesprocessor")
public class GetPuCardTypesProcessor extends BaseApiProcessor {
	
	@Resource
	private IPuCardApi puCardApi;

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {
		String merchantId=request.getParameter("merchantId");
		
		
		ShopMessageVo<Map<String, Object>> messageVo = puCardApi.getPuCardTypes(Long.valueOf(merchantId));
		
		if (messageVo.isResult()) {
			sender.put("puCardTypes", messageVo.getT().get("puCardTypes"));
			sender.put("merchant", messageVo.getT().get("merchant"));
			sender.success(response,messageVo.getMessage());
		}else {
			sender.fail(Integer.parseInt(messageVo.getErrorCode()), messageVo.getErrorMessage(), response);
		}
	}
}
