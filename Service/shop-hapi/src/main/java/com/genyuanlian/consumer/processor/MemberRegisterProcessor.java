package com.genyuanlian.consumer.processor;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.genyuanlian.consumer.shop.api.IMemberApi;
import com.genyuanlian.consumer.shop.vo.MemberRegisterParamsVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.hnair.consumer.constant.ErrorCodeEnum;
import com.hnair.consumer.processor.BaseApiProcessor;
import com.hnair.consumer.utils.IpUtils;
import com.hnair.consumer.utils.ResultSender;

@Component("hapiregisterprocessor")
public class MemberRegisterProcessor extends BaseApiProcessor {
	@Resource
	private IMemberApi memberApi;

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {
		String mobile = request.getParameter("mobile");
		String smsCode = request.getParameter("smsCode");
		String smsNumber = request.getParameter("smsNumber");
		String pwd = request.getParameter("pwd");
		String channel = request.getParameter("channel");
		String channelVersion = request.getParameter("channelVersion");
		String referraCode = request.getParameter("referraCode");
		
		if (!checkParams(mobile, smsCode,smsNumber, pwd, channel, channelVersion)) {
			sender.fail(ErrorCodeEnum.ERROR_CODE_10002.getErrorCode(), ErrorCodeEnum.ERROR_CODE_10002.getErrorMessage(),
					response);
			return;
		}

		MemberRegisterParamsVo params = new MemberRegisterParamsVo();
		params.setMobile(mobile);
		params.setSmsCode(smsCode);
		params.setSmsNumber(smsNumber);
		params.setPwd(pwd);
		params.setChannel(channel);
		params.setChannelVersion(channelVersion);
		params.setReferraCode(referraCode);
		params.setIp(IpUtils.getIpAddr(request));
		
		ShopMessageVo<Map<String, Object>> messageVo = memberApi.register(params);
		if (messageVo.isResult()) {
			sender.put("result", messageVo.getT());
			sender.success(response,messageVo.getMessage());
		}else {
			sender.fail(Integer.parseInt(messageVo.getErrorCode()), messageVo.getErrorMessage(), response);
		}
	}
}
