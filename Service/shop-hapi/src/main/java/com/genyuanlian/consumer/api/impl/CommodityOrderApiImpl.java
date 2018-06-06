package com.genyuanlian.consumer.api.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSON;
import com.genyuanlian.consumer.service.IBWSService;
import com.genyuanlian.consumer.shop.api.ICommodityOrderApi;
import com.genyuanlian.consumer.shop.api.ISystemApi;
import com.genyuanlian.consumer.shop.enums.ShopErrorCodeEnum;
import com.genyuanlian.consumer.shop.model.ShopBstkWallet;
import com.genyuanlian.consumer.shop.model.ShopCommodity;
import com.genyuanlian.consumer.shop.model.ShopCommodityOrderPay;
import com.genyuanlian.consumer.shop.model.ShopConfirmPaymentLog;
import com.genyuanlian.consumer.shop.model.ShopMember;
import com.genyuanlian.consumer.shop.model.ShopMemberAddress;
import com.genyuanlian.consumer.shop.model.ShopMemberSecurity;
import com.genyuanlian.consumer.shop.model.ShopMerchant;
import com.genyuanlian.consumer.shop.model.ShopOrder;
import com.genyuanlian.consumer.shop.model.ShopOrderCalcForce;
import com.genyuanlian.consumer.shop.model.ShopOrderCalcForceTask;
import com.genyuanlian.consumer.shop.model.ShopOrderCommoditySnapshot;
import com.genyuanlian.consumer.shop.model.ShopOrderDelivery;
import com.genyuanlian.consumer.shop.model.ShopOrderDetail;
import com.genyuanlian.consumer.shop.model.ShopProductCalcForce;
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
import com.hnair.consumer.utils.ProUtility;
import com.hnair.consumer.utils.SnoGerUtil;
import com.hnair.consumer.utils.ValidateRegexUtils;
import com.hnair.consumer.utils.system.ConfigPropertieUtils;

@Component("commodityOrderApi")
public class CommodityOrderApiImpl implements ICommodityOrderApi {
	private static Logger logger = LoggerFactory.getLogger(CommodityOrderApiImpl.class);
	private static String systemPublish = ConfigPropertieUtils.getString("system.publish");

	@Resource
	private ICommonService commonService;

	@Resource
	private IBWSService bwsService;

	@Resource
	private ISystemApi systemApi;

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

		// 如果有推荐码参数，验证参数是否正确
		if (ProUtility.isNotNull(params.getReferraCode())) {
			List<ShopMember> referras = commonService.getList(ShopMember.class, "referraCode", params.getReferraCode());
			if (referras == null || referras.size() == 0) {
				messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100003.getErrorCode().toString());
				messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100003.getErrorMessage());
				return messageVo;
			}

			params.setReferraId(referras.get(0).getId());
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

		if (commodity.getPurchaseRestrict() > 0) {
			// 限够验证（目前只验证算力服务）
			Integer Purchased = commonService.getBySqlId(ShopOrderDetail.class, "getPurchasedByMember", "memberId",
					params.getMemberId(), "calcForceOrder", 1, "commodityType", 3);
			if (commodity.getPurchaseRestrict().compareTo(Purchased + params.getSaleCount()) < 0) {
				messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200012.getErrorCode().toString());
				messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200012.getErrorMessage());
				return messageVo;
			}
		}

		if (commodity.getCommodityType() == 3) {
			// 若购买产品为算力服务，则钱包公钥地址必填
			if (StringUtils.isBlank(params.getWalletAddress())) {
				messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200011.getErrorCode().toString());
				messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200011.getErrorMessage());
				return messageVo;
			}

			// 钱包公钥地址格式验证
			if (!ValidateRegexUtils.validate(params.getWalletAddress(), ValidateRegexUtils.PUBLIC_KEY_ADDR)) {
				messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200013.getErrorCode().toString());
				messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200013.getErrorMessage());
				return messageVo;
			}
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
		order.setReferraCode(params.getReferraCode());
		order.setReferraId(params.getReferraId());

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
		orderDetail.setReferraCode(params.getReferraCode());
		orderDetail.setReferraId(params.getReferraId());
		if (commodity.getCommodityType() == 3) {
			orderDetail.setPublicKeyAddr(params.getWalletAddress());
			orderDetail.setCalcForceOrder(1);
		}
		commonService.save(orderDetail);

		// 创建订单产品快照
		ShopOrderCommoditySnapshot snapshot = new ShopOrderCommoditySnapshot();
		snapshot.setCommodityId(commodity.getId());
		snapshot.setCommodityType(3);
		snapshot.setCreateTime(now);
		snapshot.setOrderDetailId(orderDetail.getId());
		snapshot.setMerchantId(merchant.getId());
		String json = JSON.toJSONString(commodity);
		if (commodity.getCommodityType() == 1) //// 商品类型：1-区块链计算机,2-通用商品,3-算力服务
		{
			ShopProductChainComputer product = commonService.get(commodity.getProductId(),
					ShopProductChainComputer.class);
			json = json + "#" + JSON.toJSONString(product);
		} else if (commodity.getCommodityType() == 2) {
			ShopProductCommon product = commonService.get(commodity.getProductId(), ShopProductCommon.class);
			json = json + "#" + JSON.toJSONString(product);
		} else if (commodity.getCommodityType() == 3) {
			ShopProductCalcForce product = commonService.get(commodity.getProductId(), ShopProductCalcForce.class);
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

		// 检查支付密码
		ShopMemberSecurity memberSecurity = commonService.get(ShopMemberSecurity.class, "memberId",
				params.getMemberId());
		if (memberSecurity == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800014.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800014.getErrorMessage());
			return messageVo;
		}
		if (!params.getPayPwd().equals(memberSecurity.getPayPwd())) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800015.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800015.getErrorMessage());
			return messageVo;
		}

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
				card.setStatus(5); // 状态：0-未售、1-已锁定、2-售出、3-激活、4-部分使用、5-全部使用、6-已过期、7-已作废
				card.setBalance(Double.valueOf(0));
				commonService.update(card);

				// 插入支付记录
				ShopCommodityOrderPay pay = new ShopCommodityOrderPay();
				pay.setMemberId(params.getMemberId());
				pay.setAmount(balance.doubleValue());
				pay.setOrderId(order.getId());
				pay.setOrderNo(order.getOrderNo());
				pay.setPuCardId(card.getId());
				commonService.save(pay);

				// 插入map记录
				puCardConsumeMap.put(card.getId(), balance.doubleValue());

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

		// 上传BSTK钱包交易,会员账户扣除
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

		// 目标订单状态，默认为已支付
		Integer orderStatus = 3;

		if (orderDetail.getCalcForceOrder() == 1) {
			// 算力服务订单
			ShopProductCalcForce product = commonService.get(commodity.getProductId(), ShopProductCalcForce.class);
			if (commodity.getPrice().compareTo(product.getPriceTotal()) == 0) {
				// 当付全款时，直接将订单置为已收货状态，并向商户账户转账
				orderStatus = 6;

				ShopMerchant merchant = commonService.get(commodity.getMerchantId(), ShopMerchant.class);
				// 检查商户钱包
				ShopBstkWallet merWallet = commonService.get(ShopBstkWallet.class, "ownerId", merchant.getId(),
						"ownerType", 2);
				if (merWallet == null) {
					messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200004.getErrorCode().toString());
					messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200004.getErrorMessage());
					// 收到设置当前事物回滚
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					return messageVo;
				}

				// 平台转账给商户，每笔交易手续费商户承担，平台少付给商户的手续费金额配置
				BigDecimal merchantFee = new BigDecimal(ConfigPropertieUtils.getString("bstk_wallet_merchant_fee"));
				BigDecimal merwaitPayAmount = BigDecimal.valueOf(order.getAmount()).subtract(merchantFee);

				// 上传BSTK钱包交易，商户账户增加
				bwsService.walletRecharge(order.getId(), merchant.getId(), 2, merWallet.getPublicKeyAddr(),
						merwaitPayAmount);

				// 创建对应的集合任务
				CreateOrderCalcForce(orderDetails);
			} else {
				orderDetail.setBalancePayment(BigDecimal.valueOf(product.getPriceTotal())
						.subtract(BigDecimal.valueOf(commodity.getPrice())).doubleValue());
			}
		}

		// 修改订单状态
		for (ShopOrderDetail detail : orderDetails) {
			detail.setStatus(orderStatus);
			commonService.update(detail);
		}

		messageVo.setResult(true);
		messageVo.setT(order.getOrderNo());
		messageVo.setMessage("支付成功");
		return messageVo;
	}

	/**
	 * 为订单明细创建相应的算力服务计划任务
	 * 
	 * @param orderDetails
	 */
	private void CreateOrderCalcForce(List<ShopOrderDetail> orderDetails) {
		if (orderDetails == null || orderDetails.size() == 0) {
			return;
		}

		// 循环处理订单明细
		for (ShopOrderDetail detail : orderDetails) {
			// 是否是算力服务订单:0-否，1-是
			if (detail.getCalcForceOrder() == 0) {
				continue;
			}

			// 算力服务任务创建标记:0-未创建，1-已创建
			if (detail.getCalcForceTaskFlag() == 1) {
				continue;
			}

			ShopCommodity commodity = commonService.get(detail.getCommodityId(), ShopCommodity.class);
			ShopProductCalcForce product = commonService.get(commodity.getProductId(), ShopProductCalcForce.class);
			Date now = new Date();

			// 按购买商品数量循环
			for (int i = 0; i < detail.getSaleCount(); i++) {
				// 保存算力包
				ShopOrderCalcForce service = new ShopOrderCalcForce();
				String prefix = DateUtil.getCurrentSimpleDate2();
				String packageNo = prefix + SnoGerUtil.getRandomNum(6);

				// packageNo去重
				Boolean flag = true;
				while (flag) {
					List<ShopOrderCalcForce> list = commonService.getList(ShopOrderCalcForce.class, "packageNo",
							packageNo);
					if (list == null || list.size() == 0) {
						flag = false;
					} else {
						packageNo = prefix + SnoGerUtil.getRandomNum(6);
					}
				}

				service.setPackageNo(packageNo);
				service.setMemberId(detail.getMemberId());
				service.setMerchantId(detail.getMerchantId());
				service.setCommodityId(detail.getCommodityId());
				service.setCommodityName(detail.getCommodityName());
				service.setCommodityModel(product.getModel());
				service.setOrderId(detail.getOrderId());
				service.setOrderDetailId(detail.getId());
				service.setValidityFrom(DateUtil.getCurrentSimpleDate());
				service.setValidityTo(
						DateUtil.formatDateByFormat(DateUtil.addDate(now, product.getDuration() - 1), "yyyy-MM-dd"));
				service.setPublicKeyAddr(detail.getPublicKeyAddr());
				service.setTotalIncomeBstk(new Double(0));
				service.setEstimateIncomeBstk(new Double(0));
				service.setStatus(0);
				service.setCreateTime(now);
				commonService.save(service);

				// 第一天预计收益
				BigDecimal estimate = BigDecimal.ZERO;

				// 降幅因子
				int stageDeclineFactor = product.getDuration() / product.getDeclineFactorDay();
				Map<Integer, Double> mapDeclineFactor = new HashMap<Integer, Double>();
				for (int j = 0; j < stageDeclineFactor; j++) {
					switch (j) {
					case 0:
						mapDeclineFactor.put(j + 1, product.getDeclineFactorC1());
						break;
					case 1:
						mapDeclineFactor.put(j + 1, product.getDeclineFactorC2());
						break;
					case 2:
						mapDeclineFactor.put(j + 1, product.getDeclineFactorC3());
						break;
					case 3:
						mapDeclineFactor.put(j + 1, product.getDeclineFactorC4());
						break;
					case 4:
						mapDeclineFactor.put(j + 1, product.getDeclineFactorC5());
						break;
					case 5:
						mapDeclineFactor.put(j + 1, product.getDeclineFactorC6());
						break;
					}
				}
				if (systemPublish.trim().toLowerCase().equals("dev")) {
					System.out.println("###############降幅因子################");
					System.out.println("mapDeclineFactor" + mapDeclineFactor);
				}

				// 随机因子
				int stageRandomFactor = product.getDuration() / product.getRandomFactorDay();
				Map<Integer, List<BigDecimal>> mapRandomFactor = new HashMap<Integer, List<BigDecimal>>();
				for (int j = 0; j < stageRandomFactor; j++) {
					BigDecimal min = new BigDecimal(product.getRandomFactorMin());
					BigDecimal max = new BigDecimal(product.getRandomFactorMax());
					BigDecimal sum = new BigDecimal(product.getRandomFactorSum());
					int n = product.getRandomFactorDay();
					List<BigDecimal> result = SnoGerUtil.randomSet(min, max, sum, n);
					mapRandomFactor.put(j + 1, result);
				}
				if (systemPublish.trim().toLowerCase().equals("dev")) {
					System.out.println("###############随机因子################");
					System.out.println("mapRandomFactor" + mapRandomFactor);
				}

				// 随机尾数
				int stageRandomDigit = product.getDuration() / product.getRandomDigitDay();
				Map<Integer, List<BigDecimal>> mapRandomDigit = new HashMap<Integer, List<BigDecimal>>();
				for (int j = 0; j < stageRandomDigit; j++) {
					BigDecimal min = new BigDecimal(product.getRandomDigitMin());
					BigDecimal max = new BigDecimal(product.getRandomDigitMax());
					BigDecimal sum = new BigDecimal(product.getRandomDigitSum());
					int n = product.getRandomDigitDay();
					List<BigDecimal> result = SnoGerUtil.randomSet(min, max, sum, n);
					mapRandomDigit.put(j + 1, result);
				}
				if (systemPublish.trim().toLowerCase().equals("dev")) {
					System.out.println("###############随机尾数################");
					System.out.println("mapRandomDigit" + mapRandomDigit);
				}

				// 根据规则生成并保存算力包收益计划任务
				// 总日平均
				BigDecimal A = BigDecimal.valueOf(product.getTonkenTotal())
						.divide(BigDecimal.valueOf(product.getDuration()), 8, BigDecimal.ROUND_HALF_UP);
				for (int j = 0; j < product.getDuration(); j++) {
					ShopOrderCalcForceTask task = new ShopOrderCalcForceTask();
					task.setOrderCalcForceId(service.getId());
					task.setPublicKeyAddr(detail.getPublicKeyAddr());
					task.setMemberId(detail.getMemberId());
					task.setPlanDate(DateUtil.formatDateByFormat(DateUtil.addDate(now, j), "yyyy-MM-dd"));

					// 降幅因子
					int stageDeclineFactorKey = (j / product.getDeclineFactorDay()) + 1;
					BigDecimal declineFactor = BigDecimal.valueOf(mapDeclineFactor.get(stageDeclineFactorKey));
					BigDecimal x = BigDecimal.valueOf(product.getDeclineFactor());

					// 随机因子
					int stageRandomFactorKey = (j / product.getRandomFactorDay()) + 1;
					int indexRandomFactor = j % product.getRandomFactorDay();
					List<BigDecimal> randomFactorList = mapRandomFactor.get(stageRandomFactorKey);
					BigDecimal r = randomFactorList.get(indexRandomFactor);

					// 随机尾数
					int stageRandomDigitKey = (j / product.getRandomDigitDay()) + 1;
					int indexRandomDigit = j % product.getRandomDigitDay();
					List<BigDecimal> randomDigitList = mapRandomDigit.get(stageRandomDigitKey);
					BigDecimal L = randomDigitList.get(indexRandomDigit);

					// 日收益值
					BigDecimal amount = calculateForceTaskBstkAmount(A, declineFactor, x, r, L);
					if (j == 0) {
						estimate = amount;
					}
					task.setBstkAmount(amount.doubleValue());

					task.setCreateTime(now);
					commonService.save(task);
				}

				// 更新算力服务
				BigDecimal r = SnoGerUtil.getRandomBigDecimalByRange(new BigDecimal(-3), new BigDecimal(3));
				service.setEstimateIncomeBstk(estimate.add(r).doubleValue());
				commonService.update(service);
			}

			// 算力服务任务创建标记:0-未创建，1-已创建
			detail.setCalcForceTaskFlag(1);
			commonService.update(detail);
		}
	}

	/**
	 * 计算算力任务bstk日收益值
	 * 
	 * @return
	 */
	private BigDecimal calculateForceTaskBstkAmount(BigDecimal A, BigDecimal declineFactor, BigDecimal x, BigDecimal r,
			BigDecimal L) {
		// Ar=At+R+L
		BigDecimal one = new BigDecimal(1);
		BigDecimal At = A.multiply(one.add(declineFactor.multiply(x)));
		BigDecimal R = At.multiply(r);
		BigDecimal Ar = At.add(R).add(L);
		return Ar;
	}

	@Override
	@Transactional
	public ShopMessageVo<String> confirmPayment(ShopConfirmPaymentLog param) {
		ShopMessageVo<String> messageVo = new ShopMessageVo<>();

		// 用户
		List<ShopMember> members = commonService.getList(ShopMember.class, "mobile", param.getMemberMobile());
		if (members == null || members.size() == 0) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorMessage());
			return messageVo;
		}
		ShopMember member = members.get(0);
		param.setMemberId(member.getId());

		// 指定用户待付尾款的算力服务订单
		List<ShopOrderDetail> orders = commonService.getList(ShopOrderDetail.class, "memberId", member.getId(),
				"commodityType", 3, "calcForceOrder", 1, "status", 3);
		if (orders == null || orders.size() == 0) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800013.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800013.getErrorMessage());
			return messageVo;
		}

		BigDecimal balance = BigDecimal.valueOf(param.getInputAmount());
		StringBuffer orderDetailIds = new StringBuffer();

		// 按下单时间升序排列
		orders.sort((o1, o2) -> o1.getId().compareTo(o2.getId()));
		BigDecimal totalAmount = BigDecimal.ZERO; // 本次处理的订单明细所付定金之和，需要转BSTK给商户
		List<ShopOrderDetail> tasks = new ArrayList<>();
		BigDecimal debtAmount = BigDecimal.ZERO; // 未处理订单，所需总尾款
		for (ShopOrderDetail orderDetail : orders) {
			if (balance.compareTo(BigDecimal.valueOf(orderDetail.getBalancePayment())) >= 0) {
				balance = balance.subtract(BigDecimal.valueOf(orderDetail.getBalancePayment()));
				totalAmount = totalAmount.add(BigDecimal.valueOf(orderDetail.getAmount()));
				orderDetail.setStatus(6);
				commonService.update(orderDetail);
				orderDetailIds.append(orderDetail.getId());
				tasks.add(orderDetail);
			} else {
				debtAmount = debtAmount.add(BigDecimal.valueOf(orderDetail.getBalancePayment()));
			}
		}

		param.setOrderDetailIds(orderDetailIds.toString());
		param.setDeductAmount(BigDecimal.valueOf(param.getInputAmount()).subtract(balance).doubleValue());
		param.setCreateTime(DateUtil.getCurrentDateTime());
		commonService.save(param);

		if (tasks.size() > 0) {
			ShopMerchant merchant = commonService.get(orders.get(0).getMerchantId(), ShopMerchant.class);
			// 检查商户钱包
			ShopBstkWallet merWallet = commonService.get(ShopBstkWallet.class, "ownerId", merchant.getId(), "ownerType",
					2);
			if (merWallet == null) {
				messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200004.getErrorCode().toString());
				messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200004.getErrorMessage());
				// 收到设置当前事物回滚
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return messageVo;
			}

			// 平台转账给商户，每笔交易手续费商户承担，平台少付给商户的手续费金额配置
			BigDecimal merchantFee = new BigDecimal(ConfigPropertieUtils.getString("bstk_wallet_merchant_fee"));
			BigDecimal merwaitPayAmount = totalAmount.subtract(merchantFee);

			// 上传BSTK钱包交易，商户账户增加
			bwsService.walletRecharge(param.getId(), merchant.getId(), 2, merWallet.getPublicKeyAddr(),
					merwaitPayAmount);

			// 创建对应的集合任务
			CreateOrderCalcForce(tasks);

		}

		String message = "";
		if (tasks.size() == 0) {
			message = "汇款金额不足，未处理！尾款差额" + debtAmount.subtract(balance).toString() + "元";
		} else if (tasks.size() == orders.size()) {
			if (balance.compareTo(BigDecimal.ZERO) == 0) {
				message = "已确认客户线下汇款信息！";
			} else {
				message = "已确认客户线下汇款信息！客户此次汇款剩余" + balance.toString() + "元";
			}
		} else {
			message = "订单未完全处理，尾款差额" + debtAmount.subtract(balance).toString() + "元";
		}

		// 发送系统消息
		systemApi.sendSystemMessage(member.getId(), 1, "线下付款信息确认", message);
		messageVo.setT(message);
		messageVo.setResult(true);

		return messageVo;
	}
}
