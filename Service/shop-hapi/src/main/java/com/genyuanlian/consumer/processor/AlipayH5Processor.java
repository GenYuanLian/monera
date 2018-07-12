package com.genyuanlian.consumer.processor;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.genyuanlian.consumer.shop.api.IAlipayApi;
import com.genyuanlian.consumer.shop.api.ICardOrderApi;
import com.genyuanlian.consumer.shop.model.ShopOrderDetail;
import com.genyuanlian.consumer.shop.vo.AlipayParamsVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.hnair.consumer.constant.ErrorCodeEnum;
import com.hnair.consumer.dao.service.ICommonService;
import com.hnair.consumer.processor.BaseApiProcessor;
import com.hnair.consumer.utils.LoginCheck;
import com.hnair.consumer.utils.ResultSender;

@LoginCheck
@Component("hapialipayH5processor")
public class AlipayH5Processor extends BaseApiProcessor {
	
	@Resource
	private ICardOrderApi cardOrderApi;
	@Resource
	private ICommonService commonService;
	
	@Resource
	private IAlipayApi alipayApi;
	
	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {
		String orderNo=request.getParameter("orderNo");
		String returnUrl=request.getParameter("returnUrl");
		
		if (!checkParams(orderNo,returnUrl)) {
			sender.fail(ErrorCodeEnum.ERROR_CODE_10002.getErrorCode(), ErrorCodeEnum.ERROR_CODE_10002.getErrorMessage(), response);
			return;
		}
		
		//获取订单
		ShopOrderDetail order = cardOrderApi.getOrderByOrderNo(orderNo);
		if (order==null) {
			sender.fail(ErrorCodeEnum.ERROR_CODE_10006.getErrorCode(), ErrorCodeEnum.ERROR_CODE_10006.getErrorMessage(), response);
			return;
		}
		
		AlipayParamsVo params=new AlipayParamsVo();
		params.setMemberId(order.getMemberId());
		params.setOrderNo(order.getOrderNo());
		params.setProductDesc(order.getRemark());
		params.setSubject(order.getCommodityName());
		params.setTotalAmount(order.getAmount());
		params.setReturnUrl(returnUrl);
		//调用支付宝接口
		ShopMessageVo<Map<String,Object>> h5Alipay = alipayApi.H5Alipay(params);
		if (!h5Alipay.isResult()) {
			sender.fail(Integer.parseInt(h5Alipay.getErrorCode()), h5Alipay.getErrorMessage(), response);
			return;
		}
		
		String payResult=h5Alipay.getT().get("payResult").toString();
		Integer endIndex = payResult.indexOf("<script>");
		
		payResult=payResult.substring(0, endIndex);
		
		sender.put("payResult", payResult);
		sender.success(response);
//		sender.directSend(h5Alipay.getT().get("payResult").toString(), response);
	}
}
