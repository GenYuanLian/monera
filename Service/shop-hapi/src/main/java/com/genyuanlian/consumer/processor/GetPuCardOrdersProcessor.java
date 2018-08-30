package com.genyuanlian.consumer.processor;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.genyuanlian.consumer.shop.api.ICardOrderApi;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.hnair.consumer.constant.ErrorCodeEnum;
import com.hnair.consumer.processor.BaseApiProcessor;
import com.hnair.consumer.utils.LoginCheck;
import com.hnair.consumer.utils.ProUtility;
import com.hnair.consumer.utils.ResultSender;

@LoginCheck
@Component("hapigetPuCardOrdersprocessor")
public class GetPuCardOrdersProcessor extends BaseApiProcessor {

	@Resource
	private ICardOrderApi cardOrdeApi;

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {
		String memberId = request.getParameter("memberId");
		String pageIndex = request.getParameter("pageIndex");
		String pageSize = request.getParameter("pageSize");
		String orderSource = request.getParameter("orderSource");
		if (ProUtility.isNull(orderSource)) {
			orderSource = "mmdj"; // 订单来源：mmdj,sqgw
		}

		ShopMessageVo<Map<String, Object>> messageVo = cardOrdeApi.getPuCardOrders(Long.valueOf(memberId), orderSource,
				Integer.valueOf(pageIndex), Integer.valueOf(pageSize));

		if (messageVo.isResult()) {
			sender.put("orders", messageVo.getT().get("orders"));
			sender.put("hasNext", messageVo.getT().get("hasNext"));
			sender.success(response);
		} else {
			sender.fail(ErrorCodeEnum.ERROR_CODE_10000.getErrorCode(), ErrorCodeEnum.ERROR_CODE_10000.getErrorMessage(),
					response);
		}
	}

}
