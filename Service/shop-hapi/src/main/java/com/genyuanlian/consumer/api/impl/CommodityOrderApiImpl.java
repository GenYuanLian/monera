package com.genyuanlian.consumer.api.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSON;
import com.genyuanlian.consumer.service.IBWSService;
import com.genyuanlian.consumer.service.IBaseOrderService;
import com.genyuanlian.consumer.service.IETHService;
import com.genyuanlian.consumer.service.IMemberSecurityService;
import com.genyuanlian.consumer.shop.api.ICommodityOrderApi;
import com.genyuanlian.consumer.shop.api.ISystemApi;
import com.genyuanlian.consumer.shop.enums.ShopErrorCodeEnum;
import com.genyuanlian.consumer.shop.model.ShopBstkWallet;
import com.genyuanlian.consumer.shop.model.ShopBstkWalletBill;
import com.genyuanlian.consumer.shop.model.ShopCoinPrice;
import com.genyuanlian.consumer.shop.model.ShopCommodity;
import com.genyuanlian.consumer.shop.model.ShopCommodityOrderPay;
import com.genyuanlian.consumer.shop.model.ShopConfirmPaymentLog;
import com.genyuanlian.consumer.shop.model.ShopMember;
import com.genyuanlian.consumer.shop.model.ShopMemberAddress;
import com.genyuanlian.consumer.shop.model.ShopMemberShare;
import com.genyuanlian.consumer.shop.model.ShopMerchant;
import com.genyuanlian.consumer.shop.model.ShopOrder;
import com.genyuanlian.consumer.shop.model.ShopOrderCalcForce;
import com.genyuanlian.consumer.shop.model.ShopOrderCalcForceTask;
import com.genyuanlian.consumer.shop.model.ShopOrderCommoditySnapshot;
import com.genyuanlian.consumer.shop.model.ShopOrderDelivery;
import com.genyuanlian.consumer.shop.model.ShopOrderDetail;
import com.genyuanlian.consumer.shop.model.ShopOrderSplitBill;
import com.genyuanlian.consumer.shop.model.ShopProductCalcForce;
import com.genyuanlian.consumer.shop.model.ShopProductChainComputer;
import com.genyuanlian.consumer.shop.model.ShopProductCommon;
import com.genyuanlian.consumer.shop.model.ShopPuCard;
import com.genyuanlian.consumer.shop.model.ShopPuCardTradeRecord;
import com.genyuanlian.consumer.shop.utils.BWSProperties;
import com.genyuanlian.consumer.shop.vo.CommodityOrderPayParamsVo;
import com.genyuanlian.consumer.shop.vo.CreateCommodityOrderParamsVo;
import com.genyuanlian.consumer.shop.vo.OrderNoParamsVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.genyuanlian.consumer.utils.ShopUtis;
import com.genyuanlian.consumer.vo.EthWalletResp;
import com.hnair.consumer.dao.service.ICommonService;
import com.hnair.consumer.utils.DateUtil;
import com.hnair.consumer.utils.FileResponse;
import com.hnair.consumer.utils.ProUtility;
import com.hnair.consumer.utils.QrCodeCreateUtil;
import com.hnair.consumer.utils.SftpUtil;
import com.hnair.consumer.utils.SnoGerUtil;
import com.hnair.consumer.utils.ValidateRegexUtils;
import com.hnair.consumer.utils.system.ConfigPropertieUtils;

@Component("commodityOrderApi")
public class CommodityOrderApiImpl extends BaseOrderApiImpl implements ICommodityOrderApi {
	private static Logger logger = LoggerFactory.getLogger(CommodityOrderApiImpl.class);
	private static String systemPublish = ConfigPropertieUtils.getString("system.publish");

	@Resource
	private ICommonService commonService;

	@Resource
	private IBWSService bwsService;

	@Resource
	private IMemberSecurityService memberSecurityService;

	@Resource
	private ISystemApi systemApi;

	@Resource
	private IETHService ethService;

	@SuppressWarnings("rawtypes")
	@Resource(name = "masterRedisTemplate")
	private RedisTemplate masterRedisTemplate;

	@SuppressWarnings("rawtypes")
	@Resource(name = "slaveRedisTemplate")
	private RedisTemplate slaveRedisTemplate;

	@Resource
	private IBaseOrderService baseOrderService;

	@Override
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public ShopMessageVo<String> createOrder(CreateCommodityOrderParamsVo params) {
		ShopMessageVo<String> messageVo = new ShopMessageVo<>();
		// 查询用户信息
		ShopMember member = commonService.get(params.getMemberId(), ShopMember.class);
		if (member == null || member.getStatus() != 1) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100014.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100014.getErrorMessage());
			// 手动回滚当前事物
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return messageVo;
		}

		// 查询商品
		ShopCommodity commodity = commonService.get(params.getCommodityId(), ShopCommodity.class);

		if (commodity == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100200.getErrorCode().toString());
			messageVo.setMessage(ShopErrorCodeEnum.ERROR_CODE_100200.getErrorMessage());
			// 手动回滚当前事物
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return messageVo;
		}

		BigDecimal price = BigDecimal.ZERO;

		ShopOrder order = new ShopOrder();
		// 订单类型：1-普通订单；2-抢购订单；3-竞拍订单
		if (params.getOrderType() == 1) {
			if (params.getPayType() != null) { // 后续的业务代码指定了支付方式
				order.setPayType(params.getPayType());
			} else {
				order.setPayType(3);// 支付方式:1-微信,2-支付宝,3-提货卡,4-BSTK,5-ETH
			}

			if (commodity.getStatus() != 1) {
				messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100201.getErrorCode().toString());
				messageVo.setMessage(ShopErrorCodeEnum.ERROR_CODE_100201.getErrorMessage());
				// 手动回滚当前事物
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return messageVo;
			}

			// 库存验证
			if (commodity.getInventoryQuantity() < params.getSaleCount()) {
				messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200000.getErrorCode().toString());
				messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200000.getErrorMessage());
				// 手动回滚当前事物
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return messageVo;
			}

			// 获取商品实时价格
			if (order.getPayType() == 3) // 支付方式:1-微信,2-支付宝,3-提货卡,4-BSTK,5-ETH
			{
				price = BigDecimal.valueOf(commodity.getPrice()).multiply(BigDecimal.valueOf(commodity.getDiscount()));
			} else if (order.getPayType() == 5) // 支付方式:1-微信,2-支付宝,3-提货卡,4-BSTK,5-ETH
			{
				BigDecimal rmbPrice = BigDecimal.valueOf(commodity.getPrice())
						.multiply(BigDecimal.valueOf(commodity.getDiscount()));
				// ETH行情
				List<ShopCoinPrice> coinPriceList = commonService.getListBySqlId(ShopCoinPrice.class, "pageData",
						"coinType", "ETH", "pageIndex", 0, "pageSize", 1);
				if (coinPriceList != null && coinPriceList.size() > 0) {
					ShopCoinPrice coinPrice = coinPriceList.get(0);
					BigDecimal priceEth = rmbPrice.divide(BigDecimal.valueOf(coinPrice.getPrice()), 8,
							BigDecimal.ROUND_HALF_UP);
					price = priceEth;
				}
			}

			// 价格验证
			if (price.multiply(new BigDecimal(params.getSaleCount())).compareTo(params.getAmount()) != 0) {
				messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200001.getErrorCode().toString());
				messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200001.getErrorMessage());
				// 手动回滚当前事物
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return messageVo;
			}

			if (commodity.getPurchaseRestrict() > 0) {
				// 限够验证
				Integer Purchased = commonService.getBySqlId(ShopOrderDetail.class, "getPurchasedByMember", "memberId",
						params.getMemberId(), "commodityType", 3, "commodityId", commodity.getId());
				if (commodity.getPurchaseRestrict().compareTo(Purchased + params.getSaleCount()) < 0) {
					messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200012.getErrorCode().toString());
					messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200012.getErrorMessage());
					// 手动回滚当前事物
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					return messageVo;
				}
			}
		} else {
			price = params.getTotalAmount().divide(BigDecimal.valueOf(params.getSaleCount().longValue()));
			order.setPayType(4); // 支付方式:1-微信,2-支付宝,3-提货卡,4-BSTK,5-ETH
		}

		if (commodity.getCommodityType() == 3) {
			// 若购买产品为算力服务，则钱包公钥地址必填
			if (StringUtils.isBlank(params.getWalletAddress())) {
				messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200011.getErrorCode().toString());
				messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200011.getErrorMessage());
				// 手动回滚当前事物
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return messageVo;
			}

			// 钱包公钥地址格式验证
			if (!ValidateRegexUtils.validate(params.getWalletAddress(), ValidateRegexUtils.PUBLIC_KEY_ADDR)) {
				messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200013.getErrorCode().toString());
				messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200013.getErrorMessage());
				// 手动回滚当前事物
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return messageVo;
			}
		}

		Date now = DateUtil.getCurrentDateTime();

		// 如果有推荐码参数，验证参数是否正确
		if (ProUtility.isNotNull(params.getReferraCode())) {
			List<ShopMember> referras = commonService.getList(ShopMember.class, "invitationCode",
					params.getReferraCode());
			if (referras == null || referras.size() == 0) {
				messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100003.getErrorCode().toString());
				messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100003.getErrorMessage());
				// 手动回滚当前事物
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return messageVo;
			}

			params.setReferraId(referras.get(0).getId());

			// 当前产品当前推荐码分享记录
			List<ShopMemberShare> shares = commonService.getList(ShopMemberShare.class, "memberId",
					params.getReferraId(), "shareType", 1, "commodityId", params.getCommodityId(), "commodityType",
					params.getCommodityType());
			if (shares == null || shares.size() == 0) {
				// 若分享记录不存在，则插入一条
				ShopMemberShare share = new ShopMemberShare();
				share.setChannelType(1); // TODO:默认微信，此字段数据不准确
				share.setCommodityId(params.getCommodityId());
				share.setCommodityType(params.getCommodityType());
				share.setCreateTime(now);
				share.setMemberId(params.getReferraId());
				share.setShareType(1);
				share.setStatus(1);

				commonService.save(share);
			}
		}

		// 获取商户信息
		ShopMerchant merchant = commonService.get(commodity.getMerchantId(), ShopMerchant.class);

		// 创建订单
		order.setAmount(params.getAmount().doubleValue());
		order.setTotalAmount(params.getTotalAmount() != null ? params.getTotalAmount() : params.getAmount());
		order.setCreateTime(now);
		order.setMemberId(params.getMemberId());
		order.setOrderNo(ShopUtis.buildOrderNo("com"));
		order.setRemark(params.getRemark());
		order.setSource(params.getSource());
		Map<String, String> descMap = new HashMap<>();
		descMap.put("commodityName", commodity.getTitle());
		descMap.put("saleCount", params.getSaleCount().toString());
		descMap.put("amount", params.getAmount().toString());
		descMap.put("totalAmount",
				(params.getTotalAmount() != null ? params.getTotalAmount() : params.getAmount()).toString());
		descMap.put("logo", commodity.getLogo());
		order.setDescription(JSON.toJSONString(descMap));
		// 不能自己推荐自己
		if (params.getReferraId() > 0 && params.getReferraId() != params.getMemberId()) {
			order.setReferraCode(params.getReferraCode());
			order.setReferraId(params.getReferraId());
		}

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
		orderDetail.setCommodityType(3);
		orderDetail.setPrice(price.doubleValue());
		orderDetail.setSaleCount(params.getSaleCount());
		orderDetail.setAmount(params.getAmount().doubleValue());
		orderDetail.setRemark(params.getRemark());
		orderDetail.setPresentCount(commodity.getPresentRate() * params.getSaleCount()); // 计算赠送数量
		// 不能自己推荐自己
		if (params.getReferraId() > 0 && params.getReferraId() != params.getMemberId()) {
			orderDetail.setReferraCode(params.getReferraCode());
			orderDetail.setReferraId(params.getReferraId());
		}
		// 是否需要发送快递(1-是;0-否)
		orderDetail.setIsSendMail(commodity.getIsSendMail());
		// 是否是算力服务订单
		if (commodity.getCommodityType() == 3) {
			orderDetail.setPublicKeyAddr(params.getWalletAddress());
			orderDetail.setCalcForceOrder(1);
		}
		orderDetail.setTraceSource(commodity.getTraceSource());
		if (params.getOrderType() != 1) { // 订单类型：1-普通订单；2-抢购订单；3-竞拍订单
			orderDetail.setActivityId(params.getActivityId());
			orderDetail.setOrderType(params.getOrderType());
			orderDetail.setTotalAmount(params.getTotalAmount());
			orderDetail.setCommodityName(params.getActivityTitel());
		} else {
			orderDetail.setTotalAmount(params.getAmount());
			orderDetail.setOrderType(1);
			orderDetail.setCommodityName(commodity.getTitle());
		}

		orderDetail.setSource(params.getSource());

		// 支付方式:1-微信,2-支付宝,3-提货卡,4-BSTK,5-ETH
		if (order.getPayType() == 5) {
			// 创建ETH账号
			EthWalletResp ethWallet = ethService.newAccount();
			if (ethWallet == null) {
				messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200015.getErrorCode().toString());
				messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200015.getErrorMessage());
				// 手动回滚当前事物
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return messageVo;
			}

			// 订单记录数字货币账号信息
			orderDetail.setWalletAddress(ethWallet.getAddress());
			orderDetail.setWalletPrivate(ethWallet.getPrivateKey());
			orderDetail.setWalletKeyJsonName(ethWallet.getKeyJsonName());
		}

		// 生成的二维码输出流
		ByteArrayOutputStream outputStreamQrCode = new ByteArrayOutputStream();
		try {
			QrCodeCreateUtil.writeToStream(orderDetail.getWalletAddress(), 200, "png", null, outputStreamQrCode);
			Map<String, Object> sftpParams = new HashMap<>();
			sftpParams.put("host", ConfigPropertieUtils.getString("sftp.host"));
			sftpParams.put("port", ConfigPropertieUtils.getString("sftp.port"));
			sftpParams.put("username", ConfigPropertieUtils.getString("sftp.username"));
			sftpParams.put("password", ConfigPropertieUtils.getString("sftp.password"));
			sftpParams.put("imgSize", ConfigPropertieUtils.getString("sftp.imgSize"));
			sftpParams.put("imageSuffix", ConfigPropertieUtils.getString("sftp.imageSuffix"));
			sftpParams.put("imageDir", ConfigPropertieUtils.getString("sftp.imageDir"));
			sftpParams.put("basePath", ConfigPropertieUtils.getString("sftp.basePath"));
			FileResponse ponse = SftpUtil.imgFileUpload(new ByteArrayInputStream(outputStreamQrCode.toByteArray()),
					sftpParams);
			orderDetail.setWalletAddressQrcode(ponse.getFileUrl());
		} catch (Exception e) {
			logger.error("生成收款地址二维码错误：" + e.getMessage());
		}

		// 保存订单明细
		commonService.save(orderDetail);
		baseOrderService.setOrderStatus(orderDetail, 0);

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
	@Transactional
	public ShopMessageVo<String> orderPay(CommodityOrderPayParamsVo params) {
		ShopMessageVo<String> messageVo = new ShopMessageVo<String>();
		logger.info("商品订单支付调用到这里了=================,支付金额:" + params.getTotalAmount() + "用户ID:" + params.getMemberId()
				+ "订单编号:" + params.getOrderNo() + "商品名称:" + params.getSubject());

		// 检查支付密码
		ShopMessageVo<String> verifyMsg = memberSecurityService.payPwdVerify(params.getMemberId(), params.getPayPwd());
		if (!verifyMsg.isResult()) {
			messageVo = verifyMsg;
			return messageVo;
		}

		// 检查订单
		ShopOrder order = commonService.get(ShopOrder.class, "orderNo", params.getOrderNo(), "memberId",
				params.getMemberId());
		if (order == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800008.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800008.getErrorMessage());
			// 手动回滚当前事物
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return messageVo;
		}

		ShopOrderDetail orderDetail = null;
		List<ShopOrderDetail> orderDetails = commonService.getList(ShopOrderDetail.class, "orderNo",
				params.getOrderNo());
		if (orderDetails == null || orderDetails.size() == 0) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800008.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800008.getErrorMessage());
			// 手动回滚当前事物
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return messageVo;
		} else {
			orderDetail = orderDetails.get(0); // 目前只有一个订单明细
		}

		// 检查订单状态
		for (ShopOrderDetail detail : orderDetails) {
			if (detail.getStatus() != 0) {
				messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800011.getErrorCode().toString());
				messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800011.getErrorMessage());
				// 手动回滚当前事物
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return messageVo;
			}
		}

		// 判断是否重复支付
		List<ShopCommodityOrderPay> payRecords = commonService.getList(ShopCommodityOrderPay.class, "orderNo",
				params.getOrderNo(), "memberId", params.getMemberId());
		if (payRecords != null && payRecords.size() > 0) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800001.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800001.getErrorMessage());
			// 手动回滚当前事物
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
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
			// 手动回滚当前事物
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return messageVo;
		}

		// 检查钱包余额
		ShopBstkWallet wallet = commonService.get(ShopBstkWallet.class, "ownerId", params.getMemberId(), "ownerType",
				1);
		if (wallet == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200004.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200004.getErrorMessage());
			// 手动回滚当前事物
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return messageVo;
		}
		String transNo = SnoGerUtil.getUUID();
		BigDecimal walletBalance = bwsService.walletBalance(transNo, wallet.getWalletAddress());
		if (walletBalance.compareTo(totalAmount) == -1) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200005.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200005.getErrorMessage());
			// 手动回滚当前事物
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
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
		String transNo1 = SnoGerUtil.getUUID();

		// 记录提货卡交易日志表，记录钱包流水记录
		for (Map.Entry<Long, Double> entry : puCardConsumeMap.entrySet()) {
			// 记录提货卡交易日志表
			ShopPuCardTradeRecord tradeRecord = new ShopPuCardTradeRecord();
			tradeRecord.setMemberId(params.getMemberId());
			tradeRecord.setPuCardId(entry.getKey());
			tradeRecord.setTitle(orderDetail.getCommodityName());
			tradeRecord.setAmount(-entry.getValue());
			tradeRecord.setTransactionNo(transNo1);
			tradeRecord.setRemark(order.getOrderNo());
			commonService.save(tradeRecord);

			// 记录钱包流水记录
			ShopBstkWalletBill record = new ShopBstkWalletBill();
			record.setWalletId(wallet.getId());
			record.setOwnerId(params.getMemberId());
			record.setOwnerType(1);
			// 流水类型：1-提货卡激活，2-提货卡购买，3-提货卡支付，4-注册邀请返利，5-商品分享返利，6-代理返利，7-余额支付，8-微信支付，9-支付宝支付，10-销售收入
			record.setBillType(3);
			record.setTitle(orderDetail.getCommodityName());
			record.setAmount(-entry.getValue());
			record.setTransactionNo(transNo1);
			record.setBusinessId(orderDetail.getId());
			record.setRemark("businessId为订单明细Id");
			commonService.save(record);
		}

		// 查询商品
		ShopCommodity commodity = commonService.get(orderDetail.getCommodityId(), ShopCommodity.class);
		// 更新库存
		commodity.setInventoryQuantity(commodity.getInventoryQuantity() - orderDetail.getSaleCount());
		commodity.setSaleQuantity(commodity.getSaleQuantity() + orderDetail.getSaleCount());
		commonService.update(commodity);

		// 目标订单状态，默认为已支付
		Integer orderStatus = 3;

		// 创建订单返利记录
		ShopMember member = commonService.get(params.getMemberId(), ShopMember.class);
		List<ShopOrderSplitBill> bills = super.CreateOrderSplitBill(orderDetails, member);

		if (commodity.getPrice().compareTo(commodity.getPriceTotal()) == 0) {
			// 当付全款时，且商品不需要邮寄时，直接将订单置为已收货状态，并向商户账户转账
			if (commodity.getIsSendMail() == 0) {
				orderStatus = 6;
			}

			ShopMerchant merchant = commonService.get(commodity.getMerchantId(), ShopMerchant.class);
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

			// 结清，修改订单状态
			orderDetail.setBalancePayment(new Double(0)); // 所需尾款为0
			baseOrderService.setOrderStatus(orderDetail, orderStatus);
			orderDetail.setPayTime(DateUtil.getCurrentDateTime());
			commonService.update(orderDetail);

			// 平台转账给商户，每笔交易手续费商户承担，平台少付给商户的手续费金额配置
			BigDecimal merchantFee = new BigDecimal(ConfigPropertieUtils.getString("bstk_wallet_merchant_fee"));
			BigDecimal merwaitPayAmount = BigDecimal.valueOf(order.getAmount()).subtract(merchantFee);

			// 创建对应的集合任务
			// 商品类型：1-区块链计算机,2-通用商品,3-算力服务
			if (commodity.getCommodityType() == 3) {
				CreateOrderCalcForce(orderDetails);
			}

			// 买家已收货，分账，商户打款
			if (orderStatus == 6) {
				// 平台返利,上传BSTK钱包交易，平台返利发放,会员合并发放
				super.platformRebate(bills);

				// 上传BSTK钱包交易，商户账户增加
				String transNo2 = SnoGerUtil.getUUID();
				ShopBstkWalletBill record = new ShopBstkWalletBill();
				record.setWalletId(merWallet.getId());
				record.setOwnerId(merchant.getId());
				record.setOwnerType(2);
				record.setBillType(10);// 流水类型：1-提货卡激活，2-提货卡购买，3-提货卡支付，4-注册邀请返利，5-商品分享返利，6-代理返利，7-余额支付，8-微信支付，9-支付宝支付，10-销售收入
				record.setTitle("销售收入");
				record.setAmount(merwaitPayAmount.doubleValue());
				record.setTransactionNo(transNo2);
				record.setBusinessId(orderDetail.getId());
				record.setCreateTime(new Date());
				record.setRemark("businessId为订单明细Id");
				commonService.save(record);

				// 更新商户钱包余额
				BigDecimal amount = BigDecimal.valueOf(merWallet.getTotelAmount()).add(merwaitPayAmount);
				merWallet.setTotelAmount(amount.doubleValue());
				BigDecimal merBalance = BigDecimal.valueOf(merWallet.getBalance()).add(merwaitPayAmount);
				merWallet.setBalance(merBalance.doubleValue());
				commonService.update(merWallet);

				// 上传BSTK钱包交易，商户账户增加
				bwsService.walletRecharge(BWSProperties.P_YUANDIAN, transNo2, orderDetail.getId(), merchant.getId(), 2,
						merWallet.getPublicKeyAddr(), merwaitPayAmount);
			}
		} else {
			// 未结清 需要线下付款，修改订单状态
			orderDetail.setBalancePayment(
					(BigDecimal.valueOf(commodity.getPriceTotal()).subtract(BigDecimal.valueOf(commodity.getPrice()))
							.multiply(BigDecimal.valueOf(orderDetail.getSaleCount()))).doubleValue());
			baseOrderService.setOrderStatus(orderDetail, orderStatus);
			orderDetail.setPayTime(DateUtil.getCurrentDateTime());
			commonService.update(orderDetail);
		}

		// 上传BSTK钱包交易,会员账户扣除
		if (waitPayAmount.compareTo(BigDecimal.ZERO) > 0) {
			bwsService.walletConsume(BWSProperties.P_YUANDIAN, transNo1, orderDetail.getId(), params.getMemberId(), 1,
					wallet.getWalletAddress(), waitPayAmount);
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
	@Override
	public void CreateOrderCalcForce(List<ShopOrderDetail> orderDetails) {
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

			// 按购买商品数量+赠送数量循环
			for (int i = 0; i < detail.getSaleCount() + detail.getPresentCount(); i++) {
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
					task.setCommodityId(service.getCommodityId());
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
	public ShopMessageVo<String> confirmPayment(ShopConfirmPaymentLog confirmPaymentLog) {
		ShopMessageVo<String> messageVo = new ShopMessageVo<>();

		// 用户
		List<ShopMember> members = commonService.getList(ShopMember.class, "mobile",
				confirmPaymentLog.getMemberMobile());
		if (members == null || members.size() == 0) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorMessage());
			// 手动回滚当前事物
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return messageVo;
		}
		ShopMember member = members.get(0);
		confirmPaymentLog.setMemberId(member.getId());

		// 指定用户待付尾款的算力服务订单
		List<ShopOrderDetail> orders = commonService.getList(ShopOrderDetail.class, "memberId", member.getId(),
				"balancePaymentGreaterThan0", 0);
		if (orders == null || orders.size() == 0) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800013.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800013.getErrorMessage());
			// 手动回滚当前事物
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return messageVo;
		}

		// 临时变量
		BigDecimal balance = BigDecimal.valueOf(confirmPaymentLog.getInputAmount());
		BigDecimal debtAmount = BigDecimal.ZERO; // 未处理订单，所需总尾款

		// 按下单时间升序排列
		orders.sort((o1, o2) -> o1.getId().compareTo(o2.getId()));

		// 付清的订单明细列表
		List<ShopOrderDetail> orderDetails = new ArrayList<ShopOrderDetail>(); // 临时保存，付清的订单明细列表
		List<Long> orderDetailidList = new ArrayList<Long>();// 临时保存，付清的订单明细Id集合

		// 需要分账与商户打款的订单明细列表
		BigDecimal finanTransferAmount = BigDecimal.ZERO; // 本次处理的需要分账与商户打款的订单明细所付定金之和，需要转BSTK给商户
		List<Long> finanTransferOrderDetailIdList = new ArrayList<Long>(); // 临时保存，需要分账与商户打款的订单明细列表

		// 付清的-算力服务的-订单明细列表
		List<ShopOrderDetail> calcForceOrderDetails = new ArrayList<ShopOrderDetail>(); // 临时保存，付清的-算力服务的-订单明细列表

		// 接受转账商户
		Long merchantId = 0l;
		for (ShopOrderDetail orderDetail : orders) {
			if (balance.compareTo(BigDecimal.valueOf(orderDetail.getBalancePayment())) >= 0) {
				balance = balance.subtract(BigDecimal.valueOf(orderDetail.getBalancePayment()));

				// 是否需要发送快递(1-是;0-否)
				if (orderDetail.getIsSendMail() == 0) {
					baseOrderService.setOrderStatus(orderDetail, 6);
					finanTransferOrderDetailIdList.add(orderDetail.getId());
					finanTransferAmount = finanTransferAmount.add(BigDecimal.valueOf(orderDetail.getAmount()));
					merchantId = orderDetail.getMerchantId();
				}
				orderDetail.setBalancePayment(0d); // 结清
				commonService.update(orderDetail);

				// 临时值
				orderDetailidList.add(orderDetail.getId());
				orderDetails.add(orderDetail);
				// 是否是算力服务订单:0-否，1-是
				if (orderDetail.getCalcForceOrder() == 1) {
					calcForceOrderDetails.add(orderDetail);
				}
			} else {
				debtAmount = debtAmount.add(BigDecimal.valueOf(orderDetail.getBalancePayment()));
			}
		}

		// 保存线下付款记录
		confirmPaymentLog.setOrderDetailIds(StringUtils.join(orderDetailidList, ","));
		confirmPaymentLog.setDeductAmount(
				BigDecimal.valueOf(confirmPaymentLog.getInputAmount()).subtract(balance).doubleValue());
		confirmPaymentLog.setCreateTime(DateUtil.getCurrentDateTime());
		commonService.save(confirmPaymentLog);

		// 创建对应的集合任务
		if (calcForceOrderDetails != null && calcForceOrderDetails.size() > 0) {
			CreateOrderCalcForce(calcForceOrderDetails);
		}

		// 平台转账给商户，每笔交易手续费商户承担，平台少付给商户的手续费金额配置
		BigDecimal merchantFee = new BigDecimal(ConfigPropertieUtils.getString("bstk_wallet_merchant_fee"));
		BigDecimal merwaitPayAmount = finanTransferAmount.subtract(merchantFee);

		String message = "";
		if (orderDetails.size() == 0) {
			message = "汇款金额不足，未处理！尾款差额" + debtAmount.subtract(balance).toString() + "元";
		} else if (orderDetails.size() == orders.size()) {
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

		// 上传BSTK钱包交易，商户账户增加
		if (finanTransferOrderDetailIdList != null && finanTransferOrderDetailIdList.size() > 0
				&& merwaitPayAmount.compareTo(BigDecimal.ZERO) > 0 && merchantId != 0l) {

			// 平台返利,上传BSTK钱包交易，平台返利发放,会员合并发放
			List<ShopOrderSplitBill> bills = commonService.getListBySqlId(ShopOrderSplitBill.class,
					"selectByOrderDetailIds", "list", finanTransferOrderDetailIdList);
			super.platformRebate(bills);

			// 获取商户信息
			ShopMerchant merchant = commonService.get(merchantId, ShopMerchant.class);

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

			// 记录商户钱包交易记录
			String transNo = SnoGerUtil.getUUID();
			ShopBstkWalletBill record = new ShopBstkWalletBill();
			record.setWalletId(merWallet.getId());
			record.setOwnerId(merchant.getId());
			record.setOwnerType(2);
			record.setBillType(10);// 流水类型：1-提货卡激活，2-提货卡购买，3-提货卡支付，4-注册邀请返利，5-商品分享返利，6-代理返利，7-余额支付，8-微信支付，9-支付宝支付，10-销售收入
			record.setTitle("销售收入");
			record.setAmount(merwaitPayAmount.doubleValue());
			record.setTransactionNo(transNo);
			record.setBusinessId(confirmPaymentLog.getId());
			record.setCreateTime(new Date());
			record.setRemark("businessId为线下收款记录Id");
			commonService.save(record);

			// 更新商户钱包余额
			BigDecimal amount = BigDecimal.valueOf(merWallet.getTotelAmount()).add(merwaitPayAmount);
			merWallet.setTotelAmount(amount.doubleValue());
			BigDecimal merBalance = BigDecimal.valueOf(merWallet.getBalance()).add(merwaitPayAmount);
			merWallet.setBalance(merBalance.doubleValue());
			commonService.update(merWallet);

			// 上传BSTK商户钱包交易
			bwsService.walletRecharge(BWSProperties.P_YUANDIAN, transNo, confirmPaymentLog.getId(), merchant.getId(), 2,
					merWallet.getPublicKeyAddr(), merwaitPayAmount);
		}

		messageVo.setT(message);
		messageVo.setResult(true);

		return messageVo;
	}

	@Override
	@Transactional
	public ShopMessageVo<String> sendDelivery(Long orderDetailId, String expressNo, String expressSupplier) {
		ShopMessageVo<String> messageVo = new ShopMessageVo<>();

		// 订单明细
		ShopOrderDetail orderDetail = commonService.get(orderDetailId, ShopOrderDetail.class);
		if (orderDetail == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800008.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800008.getErrorMessage());
			return messageVo;
		}
		if (orderDetail.getIsSendMail() == 0) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800016.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800016.getErrorMessage());
			return messageVo;
		}
		if (orderDetail.getStatus() != 3) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800011.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800011.getErrorMessage());
			return messageVo;
		}

		// 订单物流信息
		ShopOrderDelivery delivery = commonService.get(ShopOrderDelivery.class, "orderDetailId", orderDetailId);
		if (delivery == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorMessage());
			return messageVo;
		}

		// 修改物流休息，保存物流单号和物流公司
		delivery.setExpressNo(expressNo);
		delivery.setExpressSupplier(expressSupplier);
		commonService.update(delivery);

		// 修改订单状态为已发货
		baseOrderService.setOrderStatus(orderDetail, 5);
		commonService.update(orderDetail);

		messageVo.setResult(true);
		messageVo.setT("物流信息更新成功");

		return messageVo;
	}

	@Override
	public ShopMessageVo<String> completeOrderPay(OrderNoParamsVo params) {
		ShopMessageVo<String> messageVo = new ShopMessageVo<String>();
		logger.info("数字货币扫码支付支付完成调用到这里了=================,用户ID:" + params.getMemberId() + "订单编号:" + params.getOrderNo());

		// 查询订单明细集合
		List<ShopOrderDetail> orderDetails = commonService.getList(ShopOrderDetail.class, "orderNo",
				params.getOrderNo(), "memberId", params.getMemberId());
		if (orderDetails == null || orderDetails.size() == 0) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200002.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200002.getErrorMessage());

			return messageVo;
		}

		Boolean payFlag = false;

		// 循环处理订单，目前只有一个订单明细
		for (ShopOrderDetail order : orderDetails) {
			if (order.getStatus() != 0) {
				continue;
			}

			String walletAddress = order.getWalletAddress();
			// 查询地址是否收到转账
			if (ethService.getBalance(walletAddress).compareTo(BigDecimal.valueOf(order.getAmount())) >= 0) {
				payFlag = true;
			}

			if (payFlag == true) {
				// 为竞拍到的算力服务创建任务
				List<ShopOrderDetail> list = new ArrayList<ShopOrderDetail>();
				list.add(order);
				this.CreateOrderCalcForce(list);

				// 修改订单状态信息
				order.setPayTime(new Date());
				baseOrderService.setOrderStatus(order, 8); // 订单状态:0-未支付,1-未支付取消,2-支付过期，3-已支付，4-发货前退单，5-商家确认发货，6-买家确认收货，7-收货后退单,8-订单已完成,9-抢购失败,已退款
				commonService.update(order);
			}
		}

		messageVo.setResult(true);
		messageVo.setT(orderDetails.get(0).getOrderNo());
		if (payFlag == true) {
			messageVo.setMessage("支付成功");
		} else {
			messageVo.setMessage("支付结果后台正在确认中，如果你已经完成支付，可返回进行其他操作");
		}

		return messageVo;
	}
}
