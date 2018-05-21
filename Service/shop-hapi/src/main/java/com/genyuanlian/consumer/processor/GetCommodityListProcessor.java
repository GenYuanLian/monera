package com.genyuanlian.consumer.processor;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.genyuanlian.consumer.shop.api.IFavoriteApi;
import com.genyuanlian.consumer.shop.api.IMerchantApi;
import com.genyuanlian.consumer.shop.vo.IdParamsVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.hnair.consumer.constant.ErrorCodeEnum;
import com.hnair.consumer.processor.BaseApiProcessor;
import com.hnair.consumer.utils.ResultSender;

@Component("hapigetCommodityListprocessor")
public class GetCommodityListProcessor extends BaseApiProcessor {
	@Resource
	private IMerchantApi merchantApi;
	@Resource
	private IFavoriteApi favoriteApi;

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {
		String merchantId = request.getParameter("merchantId");
		String memberId=request.getParameter("memberId");

		if (!checkParams(merchantId)) {
			sender.fail(ErrorCodeEnum.ERROR_CODE_10002.getErrorCode(), ErrorCodeEnum.ERROR_CODE_10002.getErrorMessage(),
					response);
			return;
		}
		if (StringUtils.isNotBlank(memberId)) {
			Boolean isCollection = favoriteApi.isCollection(Long.valueOf(memberId), Long.valueOf(merchantId), 1);
			sender.put("isCollection", isCollection);
		}

		IdParamsVo params = new IdParamsVo();
		params.setId(Long.parseLong(merchantId));
		ShopMessageVo<Map<String, Object>> messageVo = merchantApi.getCommodityList(params);
		if (messageVo.isResult()) {
			sender.put("merch", messageVo.getT().get("merch"));
			sender.put("pics", messageVo.getT().get("pics"));
			sender.put("commoditys", messageVo.getT().get("commoditys"));
			sender.success(response, messageVo.getMessage());
		} else {
			sender.fail(Integer.parseInt(messageVo.getErrorCode()), messageVo.getErrorMessage(), response);
		}
	}
}
