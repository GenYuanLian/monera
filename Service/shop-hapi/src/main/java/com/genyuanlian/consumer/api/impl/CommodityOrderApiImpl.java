package com.genyuanlian.consumer.api.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.genyuanlian.consumer.service.IBWSService;
import com.genyuanlian.consumer.shop.api.ICommodityOrderApi;
import com.genyuanlian.consumer.shop.enums.ShopErrorCodeEnum;
import com.genyuanlian.consumer.shop.model.ShopBstkWallet;
import com.genyuanlian.consumer.shop.model.ShopCommodity;
import com.genyuanlian.consumer.shop.model.ShopCommodityOrderPay;
import com.genyuanlian.consumer.shop.model.ShopMember;
import com.genyuanlian.consumer.shop.model.ShopMemberAddress;
import com.genyuanlian.consumer.shop.model.ShopMerchant;
import com.genyuanlian.consumer.shop.model.ShopOrder;
import com.genyuanlian.consumer.shop.model.ShopOrderCommoditySnapshot;
import com.genyuanlian.consumer.shop.model.ShopOrderDelivery;
import com.genyuanlian.consumer.shop.model.ShopOrderDetail;
import com.genyuanlian.consumer.shop.model.ShopProductChainComputer;
import com.genyuanlian.consumer.shop.model.ShopProductCommon;
import com.genyuanlian.consumer.shop.model.ShopPuCard;
import com.genyuanlian.consumer.shop.model.ShopPuCardTradeRecord;
import com.genyuanlian.consumer.shop.vo.CommodityOrderPayParamsVo;
import com.genyuanlian.consumer.shop.vo.CreateCommodityOrderParamsVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.genyuanlian.consumer.utils.ShopUtis;
import com.hnair.consumer.dao.service.ICommonService;
import com.hnair.consumer.utils.DateUtil;
import com.hnair.consumer.utils.system.ConfigPropertieUtils;

@Component("commodityOrderApi")
public class CommodityOrderApiImpl implements ICommodityOrderApi {
	private static Logger logger = LoggerFactory.getLogger(CommodityOrderApiImpl.class);

	@Resource
	private ICommonService commonService;

	@Resource
	private IBWSService bwsService;

	@SuppressWarnings("rawtypes")
	@Resource(name = "masterRedisTemplate")
	private RedisTemplate masterRedisTemplate;

	@SuppressWarnings("rawtypes")
	@Resource(name = "slaveRedisTemplate")
	private RedisTemplate slaveRedisTemplate;

	@Override
	public ShopMessageVo<String> createOrder(CreateCommodityOrderParamsVo params) {
		ShopMessageVo<String> messageVo = new ShopMessageVo<>();
		// 查询用户信息
		ShopMember member = commonService.get(params.getMemberId(), ShopMember.class);
		if (member == null || member.getStatus() != 1) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100014.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100014.getErrorMessage());
			return messageVo;
		}

		// 查询商品
		ShopCommodity commodity = commonService.get(params.getCommodityId(), ShopCommodity.class);

		if (commodity == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100200.getErrorCode().toString());
			messageVo.setMessage(ShopErrorCodeEnum.ERROR_CODE_100200.getErrorMessage());
			return messageVo;
		}

		if (commodity.getStatus() != 1) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100201.getErrorCode().toString());
			messageVo.setMessage(ShopErrorCodeEnum.ERROR_CODE_100201.getErrorMessage());
			return messageVo;
		}

		// 库存验证
		if (commodity.getInventoryQuantity() < params.getSaleCount()) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200000.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200000.getErrorMessage());
			return messageVo;
		}

		// 价格验证
		BigDecimal price = BigDecimal.valueOf(commodity.getPrice())
				.multiply(BigDecimal.valueOf(commodity.getDiscount()));
		if (price.multiply(new BigDecimal(params.getSaleCount())).compareTo(params.getAmount()) != 0) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200001.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200001.getErrorMessage());
			return messageVo;
		}

		Date now = DateUtil.getCurrentDateTime();

		// 获取商户信息
		ShopMerchant merchant = commonService.get(commodity.getMerchantId(), ShopMerchant.class);

		// 创建订单
		ShopOrder order = new ShopOrder();
		order.setAmount(params.getAmount().doubleValue());
		order.setCreateTime(now);
		order.setMemberId(params.getMemberId());
		order.setOrderNo(ShopUtis.buildOrderNo("com"));
		order.setRemark(params.getRemark());
		order.setPayType(3);
		Map<String, String> descMap = new HashMap<>();
		descMap.put("commodityName", commodity.getTitle());
		descMap.put("saleCount", params.getSaleCount().toString());
		descMap.put("amount", params.getAmount().toString());
		descMap.put("logo", commodity.getLogo());
		order.setDescription(JSON.toJSONString(descMap));

		// 持久化订单
		commonService.save(order);

		// 创建订单明细
		ShopOrderDetail orderDetail = new ShopOrderDetail();
		orderDetail.setCreateTime(now);
		orderDetail.setMemberId(params.getMemberId());
		orderDetail.setOrderId(order.getId());
		orderDetail.setOrderNo(order.getOrderNo());
		orderDetail.setMerchantId(merchant.getId());
		if (merchant != null) {
			orderDetail.setMerchantName(merchant.getMerchName());
			orderDetail.setDescription(merchant.getLogoPic());
		}
		orderDetail.setCommodityId(commodity.getId());
		orderDetail.setCommodityName(commodity.getTitle());
		orderDetail.setCommodityType(3);
		orderDetail.setPrice(price.doubleValue());
		orderDetail.setSaleCount(params.getSaleCount());
		orderDetail.setAmount(params.getAmount().doubleValue());
		orderDetail.setStatus(0);
		orderDetail.setRemark(params.getRemark());
		commonService.save(orderDetail);

		// 创建订单产品快照
		ShopOrderCommoditySnapshot snapshot = new ShopOrderCommoditySnapshot();
		snapshot.setCommodityId(commodity.getId());
		snapshot.setCommodityType(3);
		snapshot.setCreateTime(now);
		snapshot.setOrderDetailId(orderDetail.getId());
		snapshot.setMerchantId(merchant.getId());
		String json = JSON.toJSONString(commodity);
		if (commodity.getCommodityType() == 1) //// 商品类型：1-区块链计算机,2-通用商品
		{
			ShopProductChainComputer product = commonService.get(commodity.getProductId(),
					ShopProductChainComputer.class);
			json = json + "#" + JSON.toJSONString(product);
		} else {
			ShopProductCommon product = commonService.get(commodity.getProductId(), ShopProductCommon.class);
			json = json + "#" + JSON.toJSONString(product);
		}
		snapshot.setCommodityJson(json);
		commonService.save(snapshot);

		// 配送信息
		if (params.getAddressId() != null && params.getAddressId() > 0) {
			ShopMemberAddress address = commonService.get(params.getAddressId(), ShopMemberAddress.class);
			if (address != null && address.getMemberId() == params.getMemberId()) // 是当前用户地址
			{
				ShopOrderDelivery delivery = new ShopOrderDelivery();
				delivery.setMemberId(params.getMemberId());
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

		messageVo.setResult(true);
		messageVo.setMessage("下单成功，请尽快支付");
		messageVo.setT(order.getOrderNo());

		return messageVo;
	}

	@Override
	public ShopMessageVo<String> orderPay(CommodityOrderPayParamsVo params) {
		ShopMessageVo<String> messageVo = new ShopMessageVo<String>();
		logger.info("商品订单支付调用到这里了=================,支付金额:" + params.getTotalAmount() + "用户ID:" + params.getMemberId()
				+ "订单编号:" + params.getOrderNo() + "商品名称:" + params.getSubject());

		// 检查订单
		ShopOrder order = commonService.get(ShopOrder.class, "orderNo", params.getOrderNo());
		if (order == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800008.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800008.getErrorMessage());
			return messageVo;
		}

		ShopOrderDetail orderDetail = null;
		List<ShopOrderDetail> orderDetails = commonService.getList(ShopOrderDetail.class, "orderNo",
				params.getOrderNo());
		if (orderDetails == null || orderDetails.size() == 0) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800008.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800008.getErrorMessage());
			return messageVo;
		} else {
			orderDetail = orderDetails.get(0); // 目前只有一个订单明细
		}

		// 检查订单状态
		for (ShopOrderDetail detail : orderDetails) {
			if (detail.getStatus() != 0) {
				messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800011.getErrorCode().toString());
				messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800011.getErrorMessage());
				return messageVo;
			}
		}

		// 判断是否重复支付
		List<ShopCommodityOrderPay> payRecords = commonService.getList(ShopCommodityOrderPay.class, "orderNo",
				params.getOrderNo(), "memberId", params.getMemberId());
		if (payRecords != null && payRecords.size() > 0) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800001.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800001.getErrorMessage());
			return messageVo;
		}

		// 获取会员可用提货卡
		List<ShopPuCard> puCards = commonService.getListBySqlId(ShopPuCard.class, "selectAvailable", "memberId",
				params.getMemberId());

		// 计算提货卡可用余额
		BigDecimal totalBalance = BigDecimal.ZERO;
		for (ShopPuCard card : puCards) {
			totalBalance = totalBalance.add(BigDecimal.valueOf(card.getBalance()));
		}

		// 用户每笔交易手续费，平台承担，少收取用户支付的金额配置
		BigDecimal totalAmount = BigDecimal.valueOf(params.getTotalAmount());
		BigDecimal noPayAmount = totalAmount;
		BigDecimal memberFee = new BigDecimal(ConfigPropertieUtils.getString("bstk_wallet_member_fee"));
		BigDecimal waitPayAmount = totalAmount.subtract(memberFee);
		if (totalBalance.compareTo(totalAmount) == -1) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800010.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800010.getErrorMessage());
			return messageVo;
		}

		// 检查钱包余额
		ShopBstkWallet wallet = commonService.get(ShopBstkWallet.class, "ownerId", params.getMemberId(), "ownerType",
				1);
		if (wallet == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200004.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200004.getErrorMessage());
			return messageVo;
		}
		BigDecimal walletBalance = bwsService.walletBalance(wallet.getWalletAddress());
		if (walletBalance.compareTo(totalAmount) == -1) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200005.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200005.getErrorMessage());
			return messageVo;
		}

		// 循环处理提货卡
		Map<Long, Double> puCardConsumeMap = new HashMap<Long, Double>();
		for (ShopPuCard card : puCards) {
			if (noPayAmount.compareTo(BigDecimal.ZERO) == 0) {
				break; // 支付完成
			}

			BigDecimal balance = BigDecimal.valueOf(card.getBalance());
			if (noPayAmount.compareTo(balance) >= 0) {
				card.setStatus(5); //状态：0-未售、1-已锁定、2-售出、3-激活、4-部分使用、5-全部使用、6-已过期、7-已作废
				card.setBalance(Double.valueOf(0));
				commonService.update(card);

				// 插入支付记录
				ShopCommodityOrderPay pay = new ShopCommodityOrderPay();
				pay.setMemberId(params.getMemberId());
				pay.setAmount(card.getBalance());
				pay.setOrderId(order.getId());
				pay.setOrderNo(order.getOrderNo());
				pay.setPuCardId(card.getId());
				commonService.save(pay);

				// 插入map记录
				puCardConsumeMap.put(card.getId(), card.getBalance());

				// 计算未支付计数
				noPayAmount = noPayAmount.subtract(balance);
			} else {
				card.setStatus(4);
				card.setBalance(balance.subtract(noPayAmount).doubleValue());
				commonService.update(card);

				// 插入支付记录
				ShopCommodityOrderPay pay = new ShopCommodityOrderPay();
				pay.setMemberId(params.getMemberId());
				pay.setAmount(noPayAmount.doubleValue());
				pay.setOrderId(order.getId());
				pay.setOrderNo(order.getOrderNo());
				pay.setPuCardId(card.getId());
				commonService.save(pay);

				// 插入map记录
				puCardConsumeMap.put(card.getId(), noPayAmount.doubleValue());

				// 未支付计数为0
				noPayAmount = BigDecimal.ZERO;

				break; // 支付完成
			}
		}

		// 修改订单状态
		for (ShopOrderDetail detail : orderDetails) {
			detail.setStatus(3);
			commonService.update(detail);
		}

		// 上传BSTK钱包交易
		String transactionNo = bwsService.walletConsume(order.getId(), params.getMemberId(), 1,
				wallet.getWalletAddress(), waitPayAmount);

		// 记录提货卡交易日志表
		for (Map.Entry<Long, Double> entry : puCardConsumeMap.entrySet()) {
			ShopPuCardTradeRecord tradeRecord = new ShopPuCardTradeRecord();
			tradeRecord.setMemberId(params.getMemberId());
			tradeRecord.setPuCardId(entry.getKey());
			tradeRecord.setTitle(orderDetail.getCommodityName());
			tradeRecord.setAmount(-entry.getValue());
			tradeRecord.setTransactionNo(transactionNo);
			tradeRecord.setRemark(order.getOrderNo());
			commonService.save(tradeRecord);
		}

		// 查询商品
		ShopCommodity commodity = commonService.get(orderDetail.getCommodityId(), ShopCommodity.class);

		// 更新库存
		commodity.setInventoryQuantity(commodity.getInventoryQuantity() - orderDetail.getSaleCount());
		commodity.setSaleQuantity(commodity.getSaleQuantity() + orderDetail.getSaleCount());
		commonService.update(commodity);

		messageVo.setResult(true);
		messageVo.setT(order.getOrderNo());
		messageVo.setMessage("支付成功");
		return messageVo;
	}
}
