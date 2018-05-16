package com.genyuanlian.consumer.processor;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.genyuanlian.consumer.shop.api.IMemberApi;
import com.genyuanlian.consumer.shop.api.IPuCardApi;
import com.genyuanlian.consumer.shop.model.ShopMember;
import com.genyuanlian.consumer.shop.vo.MemberRegisterParamsVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.hnair.consumer.constant.ErrorCodeEnum;
import com.hnair.consumer.dao.service.ICommonService;
import com.hnair.consumer.processor.BaseApiProcessor;
import com.hnair.consumer.utils.IpUtils;
import com.hnair.consumer.utils.ResultSender;

@Component("hapiactivationPuCardImplprocessor")
public class ActivationPuCardProcessor extends BaseApiProcessor {
	
	@Resource
	private IPuCardApi puCardApi;
	@Resource
	private ICommonService commonService;
	@Resource
	private IMemberApi memberApi;

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {
		
		String mobile=request.getParameter("mobile");
		String cardCode=request.getParameter("cardCode");
		String channel=request.getParameter("channel");
		String activationCode=request.getParameter("activationCode");
		
		if (!checkParams(cardCode,mobile,activationCode)) {
			sender.fail(ErrorCodeEnum.ERROR_CODE_10002.getErrorCode(), ErrorCodeEnum.ERROR_CODE_10002.getErrorMessage(), response);
			return;
		}
		Long memberId=0l;
		List<ShopMember>members = commonService.getList(ShopMember.class, "mobile",mobile,"status",1);
		if (members==null || members.size()==0) {
			MemberRegisterParamsVo params =new MemberRegisterParamsVo();
			params.setChannel("H5-activationCode");
			params.setChannelVersion("1.0.0");
			params.setIp(IpUtils.getIpAddr(request));
			params.setMobile(mobile);
			
			ShopMember member = memberApi.registerPersist(params);
			memberId=member.getId();
		}else {
			memberId=members.get(0).getId();
		}
		
		ShopMessageVo<String> messageVo = puCardApi.activation(memberId, activationCode,channel);
		
		if (messageVo.isResult()) {
			sender.put("cardCode", messageVo.getT());
			sender.success(response,"激活成功");
		}else {
			sender.fail(Integer.valueOf(messageVo.getErrorCode()), messageVo.getErrorMessage(), response);
		}
	}

}
