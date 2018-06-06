package com.genyuanlian.consumer.processor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.genyuanlian.consumer.shop.api.ICardOrderApi;
import com.genyuanlian.consumer.shop.api.ICommodityOrderApi;
import com.genyuanlian.consumer.shop.model.ShopOrderDetail;
import com.genyuanlian.consumer.shop.vo.CommodityOrderPayParamsVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.hnair.consumer.constant.ErrorCodeEnum;
import com.hnair.consumer.processor.BaseApiProcessor;
import com.hnair.consumer.utils.LoginCheck;
import com.hnair.consumer.utils.ResultSender;

@LoginCheck
@Component("hapiorderPayprocessor")
public class CommodityOrderPayProcessor extends BaseApiProcessor {
	@Resource
	private ICardOrderApi cardOrderApi;

	@Resource
	private ICommodityOrderApi commodityOrderApi;

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {
		String orderNo = request.getParameter("orderNo");
		
		String payPwd=request.getParameter("payPwd");

		if (!checkParams(orderNo,payPwd)) {
			sender.fail(ErrorCodeEnum.ERROR_CODE_10002.getErrorCode(), ErrorCodeEnum.ERROR_CODE_10002.getErrorMessage(),
					response);
			return;
		}

		// 获取订单
		ShopOrderDetail order = cardOrderApi.getOrderByOrderNo(orderNo);
		if (order == null) {
			sender.fail(ErrorCodeEnum.ERROR_CODE_10006.getErrorCode(), ErrorCodeEnum.ERROR_CODE_10006.getErrorMessage(),
					response);
			return;
		}

		CommodityOrderPayParamsVo params = new CommodityOrderPayParamsVo();
		params.setMemberId(order.getMemberId());
		params.setOrderNo(order.getOrderNo());
		params.setProductDesc(order.getRemark());
		params.setSubject(order.getCommodityName());
		params.setTotalAmount(order.getAmount());
		params.setPayPwd(payPwd);

		// 调用支付接口
		ShopMessageVo<String> messageVo = commodityOrderApi.orderPay(params);
		if (messageVo.isResult()) {
			sender.success(response, messageVo.getMessage());
		} else {
			sender.fail(Integer.parseInt(messageVo.getErrorCode()), messageVo.getErrorMessage(), response);
		}
	}
}
