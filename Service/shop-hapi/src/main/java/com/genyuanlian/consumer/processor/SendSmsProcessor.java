package com.genyuanlian.consumer.processor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.genyuanlian.consumer.shop.api.ISmsInfoApi;
import com.genyuanlian.consumer.shop.vo.SendSmsParamsVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.hnair.consumer.constant.ErrorCodeEnum;
import com.hnair.consumer.processor.BaseApiProcessor;
import com.hnair.consumer.utils.ResultSender;

@Component("hapisendsmsprocessor")
public class SendSmsProcessor extends BaseApiProcessor{
	@Resource
	private ISmsInfoApi smsInfoApi;

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {
		String mobile = request.getParameter("mobile");
		String smstype = request.getParameter("smstype");
        
		if (!checkParams(mobile, smstype)) {
			sender.fail(ErrorCodeEnum.ERROR_CODE_10002.getErrorCode(), ErrorCodeEnum.ERROR_CODE_10002.getErrorMessage(),
					response);
			return;
		}

		SendSmsParamsVo params = new SendSmsParamsVo();
		params.setMobile(mobile);
		params.setSmstype(smstype);
		
		ShopMessageVo<String> messageVo = smsInfoApi.sendSms(params);
		if (messageVo.isResult()) {
			sender.put("smsNumber", messageVo.getT());
			sender.success(response,messageVo.getMessage());
		}else {
			sender.fail(Integer.parseInt(messageVo.getErrorCode()), messageVo.getErrorMessage(), response);
		}
	}
}
