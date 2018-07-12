package com.genyuanlian.consumer.processor;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

//@LoginCheck
@Component("hapiweixinpayH5processor")
public class WeixinpayH5Processor extends BaseApiProcessor {

	Logger logger=LoggerFactory.getLogger(WeixinpayH5Processor.class);
	
	@Resource
	private ICardOrderApi cardOrderApi;
	@Resource
	private IWeixinpayApi weixinpayApi;

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {
		String orderNo = request.getParameter("orderNo");
		String weixinCode=request.getParameter("weixinCode");
		
		logger.info("weixinCode="+weixinCode);
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
		if (StringUtils.isNotBlank(weixinCode)) {
			//公众号支付,先获取openId
			ShopMessageVo<Map<String, Object>> openIdMsg = weixinpayApi.getWeixinOpenId(weixinCode,false);
			if (!openIdMsg.isResult()) {
				sender.fail(Integer.valueOf(openIdMsg.getErrorCode()), openIdMsg.getErrorMessage(), response);
				return;
			}
			req.setOpenId(openIdMsg.getT().get("openid").toString());
		}
		
		ShopMessageVo<Map<String,Object>> messageVo = weixinpayApi.unifiedOrder(req);
		if (!messageVo.isResult()) {
			sender.fail(Integer.valueOf(messageVo.getErrorCode()), messageVo.getErrorMessage(), response);
			return;
		}
		
		sender.put("weixinPayData", messageVo.getT());
		sender.success(response);
		
	}

}
