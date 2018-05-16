package com.genyuanlian.consumer.processor;

import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.genyuanlian.consumer.shop.api.ICardOrderApi;
import com.genyuanlian.consumer.shop.api.ICommodityOrderApi;
import com.genyuanlian.consumer.shop.vo.CreateCommodityOrderParamsVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.hnair.consumer.constant.ErrorCodeEnum;
import com.hnair.consumer.processor.BaseApiProcessor;
import com.hnair.consumer.utils.LoginCheck;
import com.hnair.consumer.utils.ProUtility;
import com.hnair.consumer.utils.ResultSender;

@LoginCheck
@Component("hapicreateOrderprocessor")
public class CreateOrderProcessor extends BaseApiProcessor {

	@Resource
	private ICardOrderApi cardOrderApi;

	@Resource
	private ICommodityOrderApi commodityOrderApi;

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {

		// 商品类型
		String commodityType = request.getParameter("commodityType"); // 商品类型：1-提货卡,2-溯源卡,3-零售商品

		// 商品id
		String commodityId = request.getParameter("commodityId");

		// 购买数量
		String saleCount = request.getParameter("saleCount");

		// 金额
		String amount = request.getParameter("amount");

		// 会员Id
		String memberId = request.getParameter("memberId");

		// 备注
		String remark = request.getParameter("remark");

		// 收货地址
		String addressId = request.getParameter("addressId");

		if (!checkParams(commodityType, commodityId, saleCount, amount) || Integer.valueOf(saleCount) <= 0) {
			sender.fail(ErrorCodeEnum.ERROR_CODE_10002.getErrorCode(), ErrorCodeEnum.ERROR_CODE_10002.getErrorMessage(),
					response);
			return;
		}

		ShopMessageVo<String> messageVo = null;
		// 提货卡订单
		if (commodityType.equals("1")) {
			messageVo = cardOrderApi.createPuCardOrder(Long.valueOf(commodityId), Integer.valueOf(saleCount),
					Integer.valueOf(commodityType), new BigDecimal(amount), Long.valueOf(memberId), remark);
		} else if (commodityType.equals("3")) {
			// 零售商品订单
			CreateCommodityOrderParamsVo params = new CreateCommodityOrderParamsVo();
			params.setCommodityType(Integer.valueOf(commodityType));
			params.setCommodityId(Long.parseLong(commodityId));
			params.setSaleCount(Integer.valueOf(saleCount));
			params.setAmount(new BigDecimal(amount));
			params.setMemberId(Long.parseLong(memberId));
			params.setRemark(remark);
			if (ProUtility.isNotNull(addressId)) {
				params.setAddressId(Long.parseLong(addressId));
			}
			messageVo = commodityOrderApi.createOrder(params);
		}

		if (messageVo != null && messageVo.isResult()) {
			sender.put("orderNo", messageVo.getT());
			sender.success(response, messageVo.getMessage());
		} else {
			sender.fail(Integer.parseInt(messageVo.getErrorCode()), messageVo.getErrorMessage(), response);
		}
	}

}
