package com.genyuanlian.consumer.api.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.genyuanlian.consumer.service.IBWSService;
import com.genyuanlian.consumer.shop.api.ICardOrderApi;
import com.genyuanlian.consumer.shop.api.IPuCardApi;
import com.genyuanlian.consumer.shop.enums.ShopErrorCodeEnum;
import com.genyuanlian.consumer.shop.model.ShopBstkWallet;
import com.genyuanlian.consumer.shop.model.ShopMemberAddress;
import com.genyuanlian.consumer.shop.model.ShopMerchant;
import com.genyuanlian.consumer.shop.model.ShopOrder;
import com.genyuanlian.consumer.shop.model.ShopOrderCommoditySnapshot;
import com.genyuanlian.consumer.shop.model.ShopOrderDelivery;
import com.genyuanlian.consumer.shop.model.ShopOrderDetail;
import com.genyuanlian.consumer.shop.model.ShopPuCard;
import com.genyuanlian.consumer.shop.model.ShopPuCardType;
import com.genyuanlian.consumer.shop.vo.OrderNoParamsVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.genyuanlian.consumer.utils.ShopUtis;
import com.hnair.consumer.dao.service.ICommonService;
import com.hnair.consumer.utils.DateUtil;
import com.hnair.consumer.utils.ProUtility;
import com.hnair.consumer.utils.system.ConfigPropertieUtils;

@Component("cardOrderApi")
public class CardOrderApiImpl implements ICardOrderApi {

	private Logger logger = LoggerFactory.getLogger(CardOrderApiImpl.class);

	@Resource
	private ICommonService commonService;

	@Resource
	private IPuCardApi puCardApi;

	@Resource
	private IBWSService bwsService;

	// 事物隔离级别为最高，不可脏读，后期改未异步锁
	@Transactional(isolation = Isolation.SERIALIZABLE)
	@Override
	public ShopMessageVo<String> createPuCardOrder(Long puCardTypeId, Integer payCount, Integer cardType,
			BigDecimal amount, Long memberId, String remark, String addressId) {
		ShopMessageVo<String> result = new ShopMessageVo<>();

		// 查询提货卡类型
		ShopPuCardType puCardType = commonService.get(ShopPuCardType.class, "id", puCardTypeId);
		if (puCardType == null) {
			result.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100100.getErrorCode().toString());
			result.setMessage(ShopErrorCodeEnum.ERROR_CODE_100100.getErrorMessage());
			return result;
		}

		// 查询该类型未售提货卡
		List<ShopPuCard> puCards = puCardApi.getPuCardsByTypeId(puCardTypeId, 0, 2, 6);

		// 库存验证
		if (puCards == null || puCards.size() < payCount) {
			result.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200000.getErrorCode().toString());
			result.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200000.getErrorMessage());
			return result;
		}

		// 价格验证
		BigDecimal price = BigDecimal.valueOf(puCardType.getPrice());
		BigDecimal discount = BigDecimal.valueOf(puCardType.getDiscount());
		if (price.multiply(discount).multiply(new BigDecimal(payCount)).compareTo(amount) != 0) {
			result.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200001.getErrorCode().toString());
			result.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200001.getErrorMessage());
			return result;
		}

		Date now = DateUtil.getCurrentDateTime();

		// 获取提货卡商户信息
		ShopMerchant merchant = commonService.get(puCards.get(0).getMerchantId(), ShopMerchant.class);

		// 创建订单
		ShopOrder order = new ShopOrder();
		order.setAmount(amount.doubleValue());
		order.setCreateTime(now);
		order.setMemberId(memberId);
		order.setOrderNo(ShopUtis.buildOrderNo("puc"));
		order.setRemark(remark);
		Map<String, String> descMap = new HashMap<>();
		descMap.put("commodityName", puCardType.getTitle());
		descMap.put("saleCount", payCount.toString());
		descMap.put("amount", amount.toString());
		descMap.put("logo", puCardType.getPic());
		order.setDescription(JSON.toJSONString(descMap));

		commonService.save(order);

		// 创建订单明细
		ShopOrderDetail orderDetail = new ShopOrderDetail();
		orderDetail.setCreateTime(now);
		orderDetail.setMemberId(memberId);
		orderDetail.setOrderId(order.getId());
		orderDetail.setOrderNo(order.getOrderNo());
		orderDetail.setMerchantId(puCards.get(0).getMerchantId());
		if (merchant != null) {
			orderDetail.setMerchantName(merchant.getMerchName());
			orderDetail.setDescription(merchant.getLogoPic());
		}
		orderDetail.setCommodityId(puCards.get(0).getCardTypeId()); // 提货卡存提货卡类型Id
		orderDetail.setCommodityName(puCards.get(0).getTitle());
		orderDetail.setCommodityType(puCards.get(0).getCommodityType());
		orderDetail.setPrice(puCards.get(0).getPrice());
		orderDetail.setSaleCount(payCount);
		orderDetail.setAmount(amount.doubleValue());
		orderDetail.setStatus(0);
		orderDetail.setRemark(remark);
		commonService.save(orderDetail);

		// 配送信息
		if (ProUtility.isNotNull(addressId) && Long.parseLong(addressId) > 0) {
			ShopMemberAddress address = commonService.get(Long.parseLong(addressId), ShopMemberAddress.class);
			if (address != null && address.getMemberId() == memberId) // 是当前用户地址
			{
				ShopOrderDelivery delivery = new ShopOrderDelivery();
				delivery.setMemberId(memberId);
				delivery.setOrderId(order.getId());
				delivery.setOrderDetailId(orderDetail.getId());
				delivery.setReceiver(address.getReceiver());
				delivery.setGender(address.getGender());
				delivery.setAreaId(address.getAreaId());
				delivery.setAreaName(address.getAreaName());
				delivery.setAddress(address.getAddress());
				delivery.setMobile(address.getMobile());
				delivery.setTel(address.getTel());
				delivery.setRemark(address.getRemark());
				delivery.setEmail(address.getEmail());
				delivery.setCreateTime(now);

				commonService.save(delivery);
			}
		}

		for (int i = 0; i < payCount; i++) {
			// 锁定提货卡
			ShopPuCard puCard = puCards.get(i);
			puCard.setStatus(1); // 提货卡状态变为已锁定
			commonService.update(puCard);

			// 创建订单产品快照
			ShopOrderCommoditySnapshot snapshot = new ShopOrderCommoditySnapshot();
			snapshot.setCommodityId(puCard.getId());
			snapshot.setCommodityType(puCard.getCommodityType());
			snapshot.setCreateTime(now);
			snapshot.setMerchantId(merchant.getId());
			snapshot.setOrderDetailId(orderDetail.getId());
			snapshot.setCommodityJson(JSON.toJSONString(puCardType) + "#" + JSON.toJSONString(puCard));
			commonService.save(snapshot);

		}

		result.setResult(true);
		result.setT(order.getOrderNo());
		result.setMessage("下单成功，请尽快支付");
		return result;
	}

	@Override
	@Transactional
	public ShopMessageVo<String> cancelPuCardOrder(List<ShopOrderDetail> orderDetails, Integer status,
			String cancelReason) {
		ShopMessageVo<String> result = new ShopMessageVo<>();

		if (orderDetails == null || orderDetails.size() == 0) {
			result.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorCode().toString());
			result.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorMessage());
			return result;
		}

		// 修改订单详情
		for (ShopOrderDetail orderDetail : orderDetails) {
			if (orderDetail.getStatus().equals(0)) {
				orderDetail.setStatus(status);
				orderDetail.setCancelReason(cancelReason);
				commonService.update(orderDetail);
			} else {
				logger.info("订单号：" + orderDetail.getOrderNo() + "状态：" + orderDetail.getStatus() + "不可取消");
			}
		}

		// 要取消的订单中提货卡订单详情id集合
		List<Long> puCardOrderDetailIds = orderDetails.stream()
				.filter(i -> i.getCommodityType() == 1 && i.getStatus() == 0).map(i -> i.getId()).distinct()
				.collect(Collectors.toList());
		// 提货卡订单需要修改对应的提货卡信息
		if (puCardOrderDetailIds != null && puCardOrderDetailIds.size() > 0) {
			// 根据订单详情ids查询，订单产品快照集合
			List<ShopOrderCommoditySnapshot> list = commonService.getList(ShopOrderCommoditySnapshot.class,
					"existOrderDetailIds", puCardOrderDetailIds);

			List<Long> cardIds = list.stream().map(i -> i.getCommodityId()).distinct().collect(Collectors.toList());

			// 修改相关联卡信息
			List<ShopPuCard> cards = commonService.get(cardIds, ShopPuCard.class);
			if (cards == null || cards.size() == 0) {
				result.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100100.getErrorCode().toString());
				result.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100100.getErrorMessage());
				return result;
			}
			for (ShopPuCard card : cards) {
				card.setStatus(0);
				commonService.update(card);
			}
		}

		result.setT("成功");
		result.setResult(true);
		return result;
	}

	@Override
	public ShopMessageVo<String> deleteOrder(OrderNoParamsVo params) {
		ShopMessageVo<String> messageVo = new ShopMessageVo<String>();
		logger.info("用户删除订单调用到这里了=================,用户ID:" + params.getMemberId() + "订单编号:" + params.getOrderNo());

		// 查询订单明细集合
		List<ShopOrderDetail> orderDetails = commonService.getList(ShopOrderDetail.class, "orderNo",
				params.getOrderNo(), "memberId", params.getMemberId());
		if (orderDetails == null || orderDetails.size() == 0) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200002.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200002.getErrorMessage());
			return messageVo;
		}

		// 修改订单删除标记
		for (ShopOrderDetail orderDetail : orderDetails) {
			orderDetail.setDeleteFlag(1); // 删除标记:0-未删除,1-删除
			commonService.update(orderDetail);
		}

		messageVo.setResult(true);
		messageVo.setT(orderDetails.get(0).getOrderNo());
		messageVo.setMessage("订单删除成功");
		return messageVo;
	}

	@Override
	public ShopMessageVo<String> buyerConfirmOrder(OrderNoParamsVo params) {
		ShopMessageVo<String> messageVo = new ShopMessageVo<String>();
		logger.info("买家确认收货调用到这里了=================,用户ID:" + params.getMemberId() + "订单编号:" + params.getOrderNo());

		// 查询订单明细集合
		List<ShopOrderDetail> orderDetails = commonService.get(ShopOrderDetail.class, "orderNo", params.getOrderNo(),
				"memberId", params.getMemberId());
		if (orderDetails == null || orderDetails.size() == 0) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200002.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200002.getErrorMessage());
			return messageVo;
		}

		// 查询订单
		ShopOrder order = commonService.get(ShopOrder.class, "id", orderDetails.get(0).getOrderId(), "memberId",
				params.getMemberId());
		if (order == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200006.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200006.getErrorMessage());
			return messageVo;
		}

		// 检查商户,购物车后需要迭代
		ShopMerchant merchant = commonService.get(ShopMerchant.class, orderDetails.get(0).getMerchantId());
		if (merchant == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200007.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200007.getErrorMessage());
			return messageVo;
		}

		// 检查商户钱包
		ShopBstkWallet wallet = commonService.get(ShopBstkWallet.class, "ownerId", merchant.getId(), "ownerType", 2);
		if (wallet == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200004.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200004.getErrorMessage());
			return messageVo;
		}

		// 检查订单状态
		for (ShopOrderDetail detail : orderDetails) {
			if (detail.getStatus() != 3 && detail.getStatus() != 5) {
				messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800011.getErrorCode().toString());
				messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800011.getErrorMessage());
				return messageVo;
			}
		}

		// 修改订单详情
		for (ShopOrderDetail orderDetail : orderDetails) {
			orderDetail.setStatus(8); // 订单状态:0-未支付,1-未支付取消,2-支付过期，3-已支付，4-发货前退单，5-商家确认发货，6-买家确认收货，7-收货后退单,8-订单已完成
			commonService.update(orderDetail);
		}

		// 平台转账给商户，每笔交易手续费商户承担，平台少付给商户的手续费金额配置
		BigDecimal merchantFee = new BigDecimal(ConfigPropertieUtils.getString("bstk_wallet_merchant_fee"));
		BigDecimal waitPayAmount = BigDecimal.valueOf(order.getAmount()).subtract(merchantFee);

		// 上传BSTK钱包交易
		String transactionNo = bwsService.walletRecharge(order.getId(), merchant.getId(), 2, wallet.getPublicKeyAddr(),
				waitPayAmount);

		messageVo.setResult(true);
		messageVo.setT(orderDetails.get(0).getOrderNo());
		messageVo.setMessage("确认收货完成");
		return messageVo;
	}

	@Override
	public ShopOrderDetail getOrderByOrderNo(String orderNo) {
		List<ShopOrderDetail> orders = commonService.getList(ShopOrderDetail.class, "orderNo", orderNo);
		if (orders != null && orders.size() > 0) {
			return orders.get(0);
		}
		return null;
	}

	@Override
	public ShopMessageVo<Map<String, Object>> getPuCardOrders(Long memberId, Integer pageIndex, Integer pageSize) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<>();
		Map<String, Object> result = new HashMap<>();

		if (pageIndex == null) {
			pageIndex = 0;
		}

		if (pageSize == null) {
			pageSize = 20;
		}

		List<ShopOrderDetail> list = commonService.getListBySqlId(ShopOrderDetail.class, "pageData", "memberId",
				memberId, "deleteFlag", 0, "pageIndex", pageIndex, "pageSize", pageSize + 1);

		// 商户集合
		List<ShopMerchant> merchants = new ArrayList<>();
		if (list != null && list.size() > 0) {
			List<Long> merchantIds = list.stream().map(i -> i.getMerchantId()).distinct().collect(Collectors.toList());
			merchants = commonService.get(merchantIds, ShopMerchant.class);
		}

		if (list == null || list.size() == 0) {
			result.put("hasNext", 0);
		} else {
			int orderCount = list.size();
			if (orderCount > pageSize) {
				result.put("hasNext", 1);
				list.remove(orderCount - 1);
			} else {
				result.put("hasNext", 0);

			}

			String imageDomain = ConfigPropertieUtils.getString("image.server.address");
			// 支付等待时长，单位分钟
			Integer time = ConfigPropertieUtils.getLong("wait_pay_time", 30l).intValue();
			for (ShopOrderDetail order : list) {
				order.setDescription(imageDomain + order.getDescription());
				order.setSurplusPayTime(
						DateUtil.diffDateTime(DateUtil.addMinute(order.getCreateTime(), time), new Date()));
				if (merchants != null && merchants.size() > 0) {
					order.setMerchType(merchants.stream().filter(i -> i.getId() == order.getMerchantId())
							.map(i -> i.getMerchType()).findFirst().get());
				}
			}
		}

		result.put("orders", list);
		messageVo.setT(result);
		messageVo.setResult(true);

		return messageVo;
	}

}
