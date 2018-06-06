package com.genyuanlian.consumer.processor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.genyuanlian.consumer.shop.api.IMerchantApi;
import com.genyuanlian.consumer.shop.api.IPuCardApi;
import com.genyuanlian.consumer.shop.vo.CommodityIdParamsVo;
import com.genyuanlian.consumer.shop.vo.MerchantCommodityResponseVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.hnair.consumer.constant.ErrorCodeEnum;
import com.hnair.consumer.processor.BaseApiProcessor;
import com.hnair.consumer.utils.ResultSender;

@Component("hapigetOrderCommodityprocessor")
public class GetOrderCommodityProcessor extends BaseApiProcessor {
	@Resource
	private IMerchantApi merchantApi;
	@Resource
	private IPuCardApi puCardApi;

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {

		// 商品类型
		String commodityType = request.getParameter("commodityType"); // 商品类型：1-提货卡,2-溯源卡,3-零售商品

		// 商品id
		String commodityId = request.getParameter("commodityId");

		if (!checkParams(commodityType, commodityId)) {
			sender.fail(ErrorCodeEnum.ERROR_CODE_10002.getErrorCode(), ErrorCodeEnum.ERROR_CODE_10002.getErrorMessage(),
					response);
			return;
		}

		CommodityIdParamsVo params = new CommodityIdParamsVo();
		params.setCommodityId(Long.parseLong(commodityId));
		params.setCommodityType(Integer.parseInt(commodityType));
		ShopMessageVo<MerchantCommodityResponseVo> messageVo = null;
		// 提货卡
		if (commodityType.equals("1")) {
			messageVo = puCardApi.getOrderCommodity(params);
		} else if (commodityType.equals("3")) {
			// 零售商品
			messageVo = merchantApi.getOrderCommodity(params);
		}

		if (messageVo != null && messageVo.isResult()) {
			sender.put("result", messageVo.getT());
			sender.success(response, messageVo.getMessage());
		} else {
			sender.fail(Integer.parseInt(messageVo.getErrorCode()), messageVo.getErrorMessage(), response);
		}
	}
}
