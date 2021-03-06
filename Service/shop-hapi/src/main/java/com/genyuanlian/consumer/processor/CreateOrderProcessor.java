package com.genyuanlian.consumer.processor;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.genyuanlian.consumer.shop.vo.CreateCommodityOrderParamsVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.hnair.consumer.constant.ErrorCodeEnum;
import com.hnair.consumer.utils.LoginCheck;
import com.hnair.consumer.utils.ProUtility;
import com.hnair.consumer.utils.ResultSender;

@LoginCheck
@Component("hapicreateOrderprocessor")
public class CreateOrderProcessor extends BaseCreateOrderProcessor {

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

		// 钱包地址(算力服务收益发放BSTK钱包)
		String walletAddress = request.getParameter("walletAddress");

		// 推荐码
		String referraCode = request.getParameter("referraCode");

		// 订单来源：mmdj,sqgw
		String source = request.getParameter("source");
		if (ProUtility.isNull(source)) {
			source = "mmdj";
		}

		// 支付方式:1-微信,2-支付宝,3-提货卡,4-BSTK,5-ETH
		String payType = request.getParameter("payType");

		if (!checkParams(commodityType, commodityId, saleCount, amount) || Integer.valueOf(saleCount) <= 0) {
			sender.fail(ErrorCodeEnum.ERROR_CODE_10002.getErrorCode(), ErrorCodeEnum.ERROR_CODE_10002.getErrorMessage(),
					response);
			return;
		}

		// 参数
		CreateCommodityOrderParamsVo params = new CreateCommodityOrderParamsVo();
		params.setCommodityType(Integer.valueOf(commodityType));
		params.setCommodityId(Long.parseLong(commodityId));
		params.setSaleCount(Integer.valueOf(saleCount));
		params.setAmount(new BigDecimal(amount));
		params.setMemberId(Long.parseLong(memberId));
		params.setOrderType(1);
		params.setRemark(remark);
		if (ProUtility.isNotNull(addressId)) {
			params.setAddressId(Long.parseLong(addressId));
		}
		if (StringUtils.isNotBlank(walletAddress)) {
			params.setWalletAddress(walletAddress);
		}
		if (StringUtils.isNotBlank(referraCode)) {
			params.setReferraCode(referraCode);
		}

		if (ProUtility.isNotNull(payType)) {
			params.setPayType(Integer.parseInt(payType));
		}
		if (StringUtils.isNotBlank(source)) {
			params.setSource(source);
		}

		ShopMessageVo<String> messageVo = super.createOrderMain(params);

		if (messageVo != null && messageVo.isResult()) {
			sender.put("orderNo", messageVo.getT());
			sender.success(response, messageVo.getMessage());
		} else {
			sender.fail(Integer.parseInt(messageVo.getErrorCode()), messageVo.getErrorMessage(), response);
		}
	}

}
