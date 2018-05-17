package com.genyuanlian.consumer.processor;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.genyuanlian.consumer.shop.enums.ShopErrorCodeEnum;
import com.genyuanlian.consumer.shop.model.ShopPuCard;
import com.hnair.consumer.constant.ErrorCodeEnum;
import com.hnair.consumer.dao.service.ICommonService;
import com.hnair.consumer.processor.BaseApiProcessor;
import com.hnair.consumer.utils.ResultSender;
import com.hnair.consumer.utils.system.ConfigPropertieUtils;

@Component("hapiactivationPuCardprocessor")
public class ActivationRedirectProcessor extends BaseApiProcessor {

	private static String redirectUrl=ConfigPropertieUtils.getString("activationRedirectUrl");
	
	@Resource
	private ICommonService commonService;
	
	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {
		
		String activationCode=request.getParameter("activationCode");
		String channel=request.getParameter("channel");
		
		if (!checkParams(activationCode)) {
			sender.fail(ErrorCodeEnum.ERROR_CODE_10002.getErrorCode(), ErrorCodeEnum.ERROR_CODE_10002.getErrorMessage(), response);
			return;
		}
		
		List<ShopPuCard> list = commonService.getList(ShopPuCard.class, "activationCode", activationCode,"type",1,"channel",0);

		if (list == null || list.size() == 0) {
			sender.fail(ShopErrorCodeEnum.ERROR_CODE_100101.getErrorCode(), ShopErrorCodeEnum.ERROR_CODE_100101.getErrorMessage(), response);
			return;
		}

		ShopPuCard card = list.get(0);
//		if (card.getStatus() != 0) {
//			sender.fail(ShopErrorCodeEnum.ERROR_CODE_100102.getErrorCode(), ShopErrorCodeEnum.ERROR_CODE_100102.getErrorMessage(), response);
//			return;
//		}
		
		
//		sender.directSend(redirectUrl+"?cardCode="+card.getCode()+"&cardValue="+card.getPrice()+"&cardChannel="+channel+"&activeCode="+activationCode, response);
		
		response.sendRedirect(redirectUrl+"?cardCode="+card.getCode()+"&cardValue="+card.getPrice()+"&cardChannel="+channel+"&activeCode="+activationCode);
	}

}
