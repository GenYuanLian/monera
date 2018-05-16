package com.genyuanlian.consumer.processor;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.genyuanlian.consumer.shop.api.IMemberApi;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.genyuanlian.consumer.shop.vo.UploadHeadPortraitParamsVo;
import com.hnair.consumer.constant.ErrorCodeEnum;
import com.hnair.consumer.processor.BaseApiProcessor;
import com.hnair.consumer.utils.LoginCheck;
import com.hnair.consumer.utils.ResultSender;

@LoginCheck
@Component("hapiuploadHeadPortraitprocessor")
public class UploadHeadPortraitProcessor extends BaseApiProcessor {
	@Resource
	private IMemberApi memberApi;

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {
		String memberId = request.getParameter("memberId");
		String imageUrl = request.getParameter("imageUrl");

		if (!checkParams(memberId, imageUrl)) {
			sender.fail(ErrorCodeEnum.ERROR_CODE_10002.getErrorCode(), ErrorCodeEnum.ERROR_CODE_10002.getErrorMessage(),
					response);
			return;
		}

		UploadHeadPortraitParamsVo params = new UploadHeadPortraitParamsVo();
		params.setMemberId(Long.parseLong(memberId));
		params.setImageUrl(imageUrl);

		ShopMessageVo<Map<String, Object>> messageVo = memberApi.uploadHeadPortrait(params);
		if (messageVo.isResult()) {
			sender.put("result", messageVo.getT());
			sender.success(response, messageVo.getMessage());
		} else {
			sender.fail(Integer.parseInt(messageVo.getErrorCode()), messageVo.getErrorMessage(), response);
		}
	}
}
