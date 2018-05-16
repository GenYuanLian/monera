package com.genyuanlian.consumer.processor;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.genyuanlian.consumer.shop.api.ICardOrderApi;
import com.genyuanlian.consumer.shop.api.IWeixinpayApi;
import com.genyuanlian.consumer.shop.model.ShopOrderDetail;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.genyuanlian.consumer.shop.vo.WeixinpayParamsVo;
import com.hnair.consumer.constant.ErrorCodeEnum;
import com.hnair.consumer.processor.BaseApiProcessor;
import com.hnair.consumer.utils.IpUtils;
import com.hnair.consumer.utils.LoginCheck;
import com.hnair.consumer.utils.ResultSender;

@LoginCheck
@Component("hapiweixinpayH5processor")
public class WeixinpayH5Processor extends BaseApiProcessor {

	@Resource
	private ICardOrderApi cardOrderApi;
	@Resource
	private IWeixinpayApi weixinpayApi;

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {
		String orderNo = request.getParameter("orderNo");
		String spbillCreateIp = IpUtils.getIpAddr(request);

		if (!checkParams(orderNo)) {
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

		WeixinpayParamsVo req=new WeixinpayParamsVo();
		req.setMemberId(order.getMemberId());
		req.setOrderNo(orderNo);
		req.setSpbillCreateIp(spbillCreateIp);
		req.setSubject(order.getCommodityName());
		req.setTotalAmount(order.getAmount());
		
		ShopMessageVo<Map<String,Object>> unifiedOrder = weixinpayApi.unifiedOrder(req);
	}

}
