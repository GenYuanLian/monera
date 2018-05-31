package com.genyuanlian.consumer.processor;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.genyuanlian.consumer.shop.model.ShopCommodity;
import com.genyuanlian.consumer.shop.model.ShopMerchant;
import com.genyuanlian.consumer.shop.model.ShopOrder;
import com.genyuanlian.consumer.shop.model.ShopOrderDelivery;
import com.genyuanlian.consumer.shop.model.ShopOrderDetail;
import com.genyuanlian.consumer.shop.model.ShopProductCalcForce;
import com.hnair.consumer.constant.ErrorCodeEnum;
import com.hnair.consumer.dao.service.ICommonService;
import com.hnair.consumer.processor.BaseApiProcessor;
import com.hnair.consumer.utils.DateUtil;
import com.hnair.consumer.utils.LoginCheck;
import com.hnair.consumer.utils.ProUtility;
import com.hnair.consumer.utils.ResultSender;
import com.hnair.consumer.utils.system.ConfigPropertieUtils;

@LoginCheck
@Component("hapigetPuCardOrderDetailprocessor")
public class GetPuCardOrderDetailProcessor extends BaseApiProcessor {

	@Resource
	private ICommonService commonService;

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {

		String orderNo = request.getParameter("orderNo");
		String memberId = request.getParameter("memberId");

		if (!checkParams(orderNo)) {
			sender.fail(ErrorCodeEnum.ERROR_CODE_10002.getErrorCode(), ErrorCodeEnum.ERROR_CODE_10002.getErrorMessage(),
					response);
			return;
		}

		ShopOrder order = commonService.get(ShopOrder.class, "orderNo", orderNo);
		List<ShopOrderDetail> orderDetailList = commonService.getList(ShopOrderDetail.class, "orderNo", orderNo);
		ShopOrderDetail orderDetail = orderDetailList.get(0);
		if (orderDetail == null) {
			sender.fail(ErrorCodeEnum.ERROR_CODE_10006.getErrorCode(), ErrorCodeEnum.ERROR_CODE_10006.getErrorMessage(),
					response);
			return;
		}

		ShopOrderDelivery delivery = commonService.get(ShopOrderDelivery.class, "orderId", orderDetail.getOrderId(),
				"orderDetailId", orderDetail.getId());
		if (delivery != null) {
			if (ProUtility.isNotNull(delivery.getMobile())) {
				int len = delivery.getMobile().length();
				String str = delivery.getMobile().substring(0, 3) + "****" + delivery.getMobile().substring(len - 4);
				delivery.setMobile(str);
			}
			if (ProUtility.isNotNull(delivery.getTel())) {
				int len = delivery.getTel().length();
				String str = delivery.getTel().substring(0, 3) + "****" + delivery.getTel().substring(len - 4);
				delivery.setTel(str);
			}
		}

		if (Long.valueOf(memberId).compareTo(orderDetail.getMemberId()) != 0) {
			sender.fail(ErrorCodeEnum.ERROR_CODE_10004.getErrorCode(), ErrorCodeEnum.ERROR_CODE_10004.getErrorMessage(),
					response);
			return;
		}

		String imageDomain = ConfigPropertieUtils.getString("image.server.address");
		// 商户
		ShopMerchant merchan = commonService.get(ShopMerchant.class, "id", orderDetail.getMerchantId());
		// 支付等待时长，单位分钟
		Integer time = ConfigPropertieUtils.getLong("wait_pay_time", 30l).intValue();
		orderDetail.setMerchType(merchan.getMerchType());
		orderDetail.setMerchLogo(imageDomain + merchan.getLogoPic());
		orderDetail.setPayType(order.getPayType());
		orderDetail.setDescription(imageDomain + orderDetail.getDescription());
		orderDetail.setSurplusPayTime(
				DateUtil.diffDateTime(DateUtil.addMinute(orderDetail.getCreateTime(), time), new Date()));

		sender.put("cardOrder", orderDetail);
		sender.put("delivery", delivery);
		
		if (orderDetail.getCalcForceOrder()==1) {
			ShopCommodity commodity = commonService.get(orderDetail.getCommodityId(), ShopCommodity.class);
			ShopProductCalcForce product=commonService.get(commodity.getProductId(),ShopProductCalcForce.class);
			sender.put("payExplain", product.getPayExplain());
		}
		
		sender.success(response);
	}

}
