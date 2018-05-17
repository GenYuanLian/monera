package com.genyuanlian.consumer.api.impl;

import static org.hamcrest.CoreMatchers.nullValue;

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
import org.springframework.transaction.annotation.Transactional;

import com.genyuanlian.consumer.service.IBWSService;
import com.genyuanlian.consumer.shop.api.IPuCardApi;
import com.genyuanlian.consumer.shop.enums.ShopErrorCodeEnum;
import com.genyuanlian.consumer.shop.model.ShopBstkWallet;
import com.genyuanlian.consumer.shop.model.ShopCommodity;
import com.genyuanlian.consumer.shop.model.ShopMerchant;
import com.genyuanlian.consumer.shop.model.ShopMerchantPic;
import com.genyuanlian.consumer.shop.model.ShopOrderDetail;
import com.genyuanlian.consumer.shop.model.ShopPuCard;
import com.genyuanlian.consumer.shop.model.ShopPuCardTradeRecord;
import com.genyuanlian.consumer.shop.model.ShopPuCardType;
import com.genyuanlian.consumer.shop.vo.CommodityIdParamsVo;
import com.genyuanlian.consumer.shop.vo.CommodityVo;
import com.genyuanlian.consumer.shop.vo.MerchantCommodityResponseVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.hnair.consumer.dao.service.ICommonService;
import com.hnair.consumer.utils.DateUtil;
import com.hnair.consumer.utils.system.ConfigPropertieUtils;

@Component("puCardApi")
public class PuCardApiImpl implements IPuCardApi {

	private Logger logger = LoggerFactory.getLogger(PuCardApiImpl.class);

	@Resource
	private ICommonService commonService;

	@Resource
	private IBWSService bwsService;

	@Override
	public ShopMessageVo<Map<String, Object>> getPuCardTypes(Long merchantId) {
		ShopMessageVo<Map<String, Object>> result = new ShopMessageVo<>();
		Map<String, Object> resultMap = new HashMap<>();

		// 商户信息
		ShopMerchant merchant = commonService.get(merchantId, ShopMerchant.class);
		if (merchant == null) {
			result.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorCode().toString());
			result.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorMessage());
			return result;
		}

		String imageDomain = ConfigPropertieUtils.getString("image.server.address");
		merchant.setLogoPic(imageDomain + merchant.getLogoPic());

		resultMap.put("merchant", merchant);

		// 商户背景图
		List<ShopMerchantPic> pics = commonService.getList(ShopMerchantPic.class, "merchId", merchant.getId(), "status",
				1, "picType", 2);
		if (pics != null && pics.size() > 0) {
			for (ShopMerchantPic pic : pics) {
				pic.setUrl(imageDomain + pic.getUrl());
			}
		}

		resultMap.put("pics", pics);

		// 提货卡类型集合
		List<ShopPuCardType> types = commonService.getList(ShopPuCardType.class, "status", 1, "merchantId", merchantId);
		if (types == null || types.size() == 0) {
			result.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100100.getErrorCode().toString());
			result.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100100.getErrorMessage());
			return result;
		}

		List<Long> typeIds = types.stream().map(i -> i.getId()).distinct().collect(Collectors.toList());

		// 获取库存及销量
		List<ShopPuCardType> puCardTypes = commonService.getListBySqlId(ShopPuCard.class, "groupByCardType", "list",
				typeIds, "type", 2, "channel", 6);

		if (puCardTypes == null || puCardTypes.size() == 0) {
			result.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100100.getErrorCode().toString());
			result.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100100.getErrorMessage());
			return result;
		}

		List<ShopPuCardType> list = new ArrayList<>();
		for (ShopPuCardType vo : puCardTypes) {
			if (vo.getInventory() > 0) {
				for (ShopPuCardType type : types) {
					if (vo.getId().equals(type.getId())) {
						vo.setPic(imageDomain + type.getPic());
						vo.setPrice(type.getPrice());
						vo.setRemark(type.getRemark());
						vo.setTitle(type.getTitle());
						break;
					}
				}
				list.add(vo);
			}

		}

		resultMap.put("puCardTypes", list);

		if (list.size() == 0) {
			result.setMessage("提货卡全部售完");
		}

		result.setT(resultMap);
		result.setResult(true);

		return result;
	}

	@Override
	public List<ShopPuCard> getPuCardsByTypeId(Long typeId, Integer status, Integer type, Integer channel) {
		return commonService.getList(ShopPuCard.class, "cardTypeId", typeId, "status", status, "type", type, "channel",
				channel);
	}

	@Override
	@Transactional
	public ShopMessageVo<String> activation(Long memberId, String activationCode, String channel) {
		ShopMessageVo<String> messageVo = new ShopMessageVo<>();

		List<ShopPuCard> list = commonService.getList(ShopPuCard.class, "activationCode", activationCode, "type", 1,
				"channel", 0);

		if (list == null || list.size() == 0) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100101.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100101.getErrorMessage());
			return messageVo;
		}

		ShopPuCard card = list.get(0);
		if (card.getStatus() != 0) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100102.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100102.getErrorMessage());
			return messageVo;
		}

		Date now = DateUtil.getCurrentDateTime();
		card.setActivationTime(now);
		card.setSaleTime(now);
		card.setStatus(3);
		card.setMemberId(memberId);
		card.setRemark(channel);
		commonService.update(card);

		messageVo.setT(card.getCode());
		messageVo.setResult(true);

		ShopBstkWallet bstkWallet = commonService.get(ShopBstkWallet.class, "ownerId", memberId, "ownerType", 1);
		String transactionNo = bwsService.walletRecharge(0l, memberId, 1, bstkWallet.getPublicKeyAddr(),
				card.getTotelValue());
		logger.info("调用bstk接口返回值：" + transactionNo);

		// 保存提货卡交易记录
		ShopPuCardTradeRecord tradeRecord = new ShopPuCardTradeRecord();
		tradeRecord.setAmount(card.getTotelValue());
		tradeRecord.setCreateTime(now);
		tradeRecord.setMemberId(memberId);
		tradeRecord.setPuCardId(card.getId());
		tradeRecord.setTitle(card.getTitle());
		tradeRecord.setTransactionNo(transactionNo);
		commonService.save(tradeRecord);

		return messageVo;
	}

	@Override
	public ShopMessageVo<Map<String, Object>> getPagePuCards(Long memberId, Integer isValid, Integer pageIndex,
			Integer pageSize) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<>();
		Map<String, Object> result = new HashMap<>();

		if (pageIndex == null) {
			pageIndex = 0;
		}

		if (pageSize == null) {
			pageSize = 20;
		}

		List<Integer> existStatus = null;
		if (isValid != null) {
			existStatus = new ArrayList<>();
			if (isValid == 1) {
				existStatus.add(2);
				existStatus.add(3);
				existStatus.add(4);
			} else {
				existStatus.add(5);
				existStatus.add(6);
				existStatus.add(7);
			}
		}

		Map<String, Object> statistics = commonService.getBySqlId(ShopPuCard.class, "getStatistics", "memberId",
				memberId, "existStatus", existStatus);

		result.put("cardCount", statistics.get("count"));
		result.put("sumBalance", statistics.get("sumBalance"));

		List<ShopPuCard> list = commonService.getListBySqlId(ShopPuCard.class, "pageData", "memberId", memberId,
				"existStatus", existStatus, "pageIndex", pageIndex, "pageSize", pageSize + 1);

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

			// 提货卡类型集合
			List<Long> cardTypeIds = list.stream().map(i -> i.getCardTypeId()).distinct().collect(Collectors.toList());
			List<ShopPuCardType> types = commonService.get(cardTypeIds, ShopPuCardType.class);

			String imageDomain = ConfigPropertieUtils.getString("image.server.address");
			// 支付等待时长，单位分钟
			Integer time = ConfigPropertieUtils.getLong("wait_pay_time", 30l).intValue();
			for (ShopPuCard card : list) {
				for (ShopPuCardType type : types) {
					if (card.getCardTypeId().equals(type.getId())) {
						card.setPicUrl(imageDomain + type.getPic());
						break;
					}
				}
			}
		}

		result.put("puCards", list);

		messageVo.setT(result);
		messageVo.setResult(true);

		return messageVo;
	}

	@Override
	public ShopMessageVo<MerchantCommodityResponseVo> getOrderCommodity(CommodityIdParamsVo params) {
		ShopMessageVo<MerchantCommodityResponseVo> messageVo = new ShopMessageVo<MerchantCommodityResponseVo>();

		String imageDomain = ConfigPropertieUtils.getString("image.server.address");
		ShopPuCardType puCardType = commonService.get(params.getCommodityId(), ShopPuCardType.class);
		if (puCardType == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorMessage());
			return messageVo;
		}

		ShopMerchant merchant = commonService.get(puCardType.getMerchantId(), ShopMerchant.class);
		if (merchant == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorMessage());
			return messageVo;
		}

		MerchantCommodityResponseVo resp = new MerchantCommodityResponseVo();
		resp.setMerchantId(merchant.getId());
		resp.setMerchantName(merchant.getMerchName());
		resp.setMerchantType(merchant.getMerchType());
		resp.setMerchantLogo(imageDomain + merchant.getLogoPic());
		CommodityVo vo = new CommodityVo();
		vo.setCommodityId(puCardType.getId());
		vo.setCommodityType(1);
		vo.setCommodityName(puCardType.getTitle());
		vo.setCommodityLogo(imageDomain + puCardType.getPic());
		Double price = BigDecimal.valueOf(puCardType.getPrice()).multiply(BigDecimal.valueOf(puCardType.getDiscount()))
				.doubleValue();
		vo.setCommodityPrice(price);

		List<CommodityVo> list = new ArrayList<CommodityVo>();
		list.add(vo);
		resp.setCommodityList(list);

		messageVo.setResult(true);
		messageVo.setT(resp);
		messageVo.setMessage("数据获取成功");
		return messageVo;

	}
}