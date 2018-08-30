package com.genyuanlian.consumer.processor;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.genyuanlian.consumer.shop.api.ICardOrderApi;
import com.genyuanlian.consumer.shop.model.ShopOrderDetail;
import com.genyuanlian.consumer.shop.vo.OrderNoParamsVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.hnair.consumer.constant.ErrorCodeEnum;
import com.hnair.consumer.dao.service.ICommonService;
import com.hnair.consumer.processor.BaseApiProcessor;
import com.hnair.consumer.utils.LoginCheck;
import com.hnair.consumer.utils.ResultSender;

@LoginCheck
@Component("hapideleteOrderprocessor")
public class DeleteOrderProcessor extends BaseApiProcessor {
	@Resource
	private ICardOrderApi cardOrderApi;

	@Resource
	private ICommonService commonService;

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {
		String memberId = request.getParameter("memberId");
		String orderNo = request.getParameter("orderNo");

		if (!checkParams(orderNo)) {
			sender.fail(ErrorCodeEnum.ERROR_CODE_10002.getErrorCode(), ErrorCodeEnum.ERROR_CODE_10002.getErrorMessage(),
					response);
			return;
		}

		List<ShopOrderDetail> orderDetails = commonService.getList(ShopOrderDetail.class, "orderNo", orderNo);
		if (orderDetails == null) {
			sender.fail(ErrorCodeEnum.ERROR_CODE_10007.getErrorCode(), ErrorCodeEnum.ERROR_CODE_10007.getErrorMessage(),
					response);
			return;
		}

		for (ShopOrderDetail orderDetail : orderDetails) {
			if (orderDetail.getMemberId().compareTo(Long.valueOf(memberId)) != 0) {
				sender.fail(ErrorCodeEnum.ERROR_CODE_10004.getErrorCode(),
						ErrorCodeEnum.ERROR_CODE_10004.getErrorMessage(), response);
				return;
			}
		}

		OrderNoParamsVo params = new OrderNoParamsVo();
		params.setMemberId(Long.valueOf(memberId));
		params.setOrderNo(orderNo);
		ShopMessageVo<String> messageVo = cardOrderApi.deleteOrder(params);
		if (messageVo.isResult()) {
			sender.success(response, messageVo.getMessage());
		} else {
			sender.fail(Integer.parseInt(messageVo.getErrorCode()), messageVo.getErrorMessage(), response);
		}
	}
}
