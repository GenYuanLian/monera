package com.genyuanlian.consumer.processor;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import com.genyuanlian.consumer.shop.api.IMemberApi;
import com.genyuanlian.consumer.shop.vo.MemberFindPwdParamsVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.hnair.consumer.constant.ErrorCodeEnum;
import com.hnair.consumer.processor.BaseApiProcessor;
import com.hnair.consumer.utils.ResultSender;

@Component("hapifindPwdprocessor")
public class MemberFindPwdProcessor extends BaseApiProcessor {
	@Resource
	private IMemberApi memberApi;

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {
		String mobile = request.getParameter("mobile");
		String smsCode = request.getParameter("smsCode");
		String smsNumber = request.getParameter("smsNumber");
		String pwd = request.getParameter("pwd");

		if (!checkParams(mobile, smsCode, smsNumber, pwd)) {
			sender.fail(ErrorCodeEnum.ERROR_CODE_10002.getErrorCode(), ErrorCodeEnum.ERROR_CODE_10002.getErrorMessage(),
					response);
			return;
		}

		MemberFindPwdParamsVo params = new MemberFindPwdParamsVo();
		params.setMobile(mobile);
		params.setSmsCode(smsCode);
		params.setSmsNumber(smsNumber);
		params.setPwd(pwd);

		ShopMessageVo<Map<String, Object>> messageVo = memberApi.findPwd(params);
		if (messageVo.isResult()) {
			sender.put("result", messageVo.getT());
			sender.success(response,messageVo.getMessage());
		}else {
			sender.fail(Integer.parseInt(messageVo.getErrorCode()), messageVo.getErrorMessage(), response);
		}
	}
}
