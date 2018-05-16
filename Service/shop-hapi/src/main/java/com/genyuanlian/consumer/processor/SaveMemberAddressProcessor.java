package com.genyuanlian.consumer.processor;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.genyuanlian.consumer.shop.api.IMemberApi;
import com.genyuanlian.consumer.shop.vo.MemberAddressParamsVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.hnair.consumer.constant.ErrorCodeEnum;
import com.hnair.consumer.processor.BaseApiProcessor;
import com.hnair.consumer.utils.LoginCheck;
import com.hnair.consumer.utils.ProUtility;
import com.hnair.consumer.utils.ResultSender;

@LoginCheck
@Component("hapisaveMemberAddressprocessor")
public class SaveMemberAddressProcessor extends BaseApiProcessor {
	@Resource
	private IMemberApi memberApi;

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {
		String id = request.getParameter("id");
		String memberId = request.getParameter("memberId");
		String receiver = request.getParameter("receiver");
		String gender = request.getParameter("gender");
		String areaCode = request.getParameter("areaCode");
		String areaName = request.getParameter("areaName");
		String address = request.getParameter("address");
		String mobile = request.getParameter("mobile");
		String tel = request.getParameter("tel");
		String email = request.getParameter("email");
		String addressAlias = request.getParameter("addressAlias");
		String remark = request.getParameter("remark");

		if (!checkParams(memberId, receiver, gender, areaCode, address, mobile)) {
			sender.fail(ErrorCodeEnum.ERROR_CODE_10002.getErrorCode(), ErrorCodeEnum.ERROR_CODE_10002.getErrorMessage(),
					response);
			return;
		}

		MemberAddressParamsVo params = new MemberAddressParamsVo();
		if (ProUtility.isNotNull(id)) {
			params.setId(Long.parseLong(id));
		}
		if (ProUtility.isNotNull(memberId)) {
			params.setMemberId(Long.parseLong(memberId));
		}
		params.setReceiver(receiver);
		if (ProUtility.isNotNull(gender)) {
			params.setGender(Integer.parseInt(gender));
		}
		if (ProUtility.isNotNull(areaCode)) {
			params.setAreaCode(Integer.valueOf(areaCode));
		}
		params.setAreaName(areaName);
		params.setAddress(address);
		params.setMobile(mobile);
		params.setTel(tel);
		params.setEmail(email);
		params.setAddressAlias(addressAlias);
		params.setRemark(remark);
		ShopMessageVo<Map<String, Object>> messageVo = memberApi.saveMemberAddress(params);
		if (messageVo.isResult()) {
			sender.success(response, messageVo.getMessage());
		} else {
			sender.fail(Integer.parseInt(messageVo.getErrorCode()), messageVo.getErrorMessage(), response);
		}
	}
}
