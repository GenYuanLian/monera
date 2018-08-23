package com.genyuanlian.consumer.processor;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.genyuanlian.consumer.shop.api.IMemberApi;
import com.genyuanlian.consumer.shop.vo.MemberLoginParamsVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.hnair.consumer.constant.ErrorCodeEnum;
import com.hnair.consumer.processor.BaseApiProcessor;
import com.hnair.consumer.utils.IpUtils;
import com.hnair.consumer.utils.ProUtility;
import com.hnair.consumer.utils.ResultSender;

@Component("hapiloginprocessor")
public class MemberLoginProcessor extends BaseApiProcessor {
	@Resource
	private IMemberApi memberApi;

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {
		String loginName = request.getParameter("loginName");
		String loginType = request.getParameter("loginType");
		String loginCode = request.getParameter("loginCode");
		String smsCode = request.getParameter("smsCode");

		if (!checkParams(loginName, loginType, loginCode)) {
			sender.fail(ErrorCodeEnum.ERROR_CODE_10002.getErrorCode(), ErrorCodeEnum.ERROR_CODE_10002.getErrorMessage(),
					response);
			return;
		}

		if (loginType.equals("smscode")) {
			if (!checkParams(smsCode)) {
				sender.fail(ErrorCodeEnum.ERROR_CODE_10002.getErrorCode(),
						ErrorCodeEnum.ERROR_CODE_10002.getErrorMessage(), response);
				return;
			}
		}

		MemberLoginParamsVo params = new MemberLoginParamsVo();
		params.setLoginName(loginName);
		params.setLoginType(loginType);
		params.setLoginCode(loginCode);
		params.setSmsCode(smsCode);
		params.setIp(IpUtils.getIpAddr(request));

		ShopMessageVo<Map<String, Object>> messageVo = memberApi.login(params);
		if (messageVo.isResult()) {
			sender.put("userInfo", messageVo.getT());
			sender.success(response, messageVo.getMessage());
		} else {
			sender.fail(Integer.parseInt(messageVo.getErrorCode()), messageVo.getErrorMessage(), response);
		}
	}
}
