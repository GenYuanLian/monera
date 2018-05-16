package com.genyuanlian.consumer.processor;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.genyuanlian.consumer.shop.api.IMerchantApi;
import com.genyuanlian.consumer.shop.vo.IdParamsVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.hnair.consumer.constant.ErrorCodeEnum;
import com.hnair.consumer.processor.BaseApiProcessor;
import com.hnair.consumer.utils.ResultSender;

@Component("hapigetMerchantInfoprocessor")
public class GetMerchantInfoProcessor extends BaseApiProcessor {
	@Resource
	private IMerchantApi merchantApi;

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {
		String merchantId = request.getParameter("merchantId");

		if (!checkParams(merchantId)) {
			sender.fail(ErrorCodeEnum.ERROR_CODE_10002.getErrorCode(), ErrorCodeEnum.ERROR_CODE_10002.getErrorMessage(),
					response);
			return;
		}

		IdParamsVo params = new IdParamsVo();
		params.setId(Long.parseLong(merchantId));
		ShopMessageVo<Map<String, Object>> messageVo = merchantApi.getMerchantInfo(params);
		if (messageVo.isResult()) {
			sender.put("merch", messageVo.getT().get("merch"));
			sender.put("pics", messageVo.getT().get("pics"));
			sender.put("advertPics", messageVo.getT().get("advertPics"));
			sender.success(response, messageVo.getMessage());
		} else {
			sender.fail(Integer.parseInt(messageVo.getErrorCode()), messageVo.getErrorMessage(), response);
		}
	}
}
