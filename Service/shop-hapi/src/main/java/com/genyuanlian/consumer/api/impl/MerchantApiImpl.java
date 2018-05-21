package com.genyuanlian.consumer.api.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.genyuanlian.consumer.shop.api.IMerchantApi;
import com.genyuanlian.consumer.shop.enums.ShopErrorCodeEnum;
import com.genyuanlian.consumer.shop.model.ShopComment;
import com.genyuanlian.consumer.shop.model.ShopCommodity;
import com.genyuanlian.consumer.shop.model.ShopCommodityPic;
import com.genyuanlian.consumer.shop.model.ShopMerchant;
import com.genyuanlian.consumer.shop.model.ShopMerchantPic;
import com.genyuanlian.consumer.shop.model.ShopOrderDetail;
import com.genyuanlian.consumer.shop.model.ShopProductChainComputer;
import com.genyuanlian.consumer.shop.model.ShopProductCommon;
import com.genyuanlian.consumer.shop.vo.CommodityIdParamsVo;
import com.genyuanlian.consumer.shop.vo.CommodityVo;
import com.genyuanlian.consumer.shop.vo.IdParamsVo;
import com.genyuanlian.consumer.shop.vo.MerchantCommodityResponseVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.hnair.consumer.dao.service.ICommonService;
import com.hnair.consumer.utils.system.ConfigPropertieUtils;

@Component("merchantApi")
public class MerchantApiImpl implements IMerchantApi {
	private static Logger logger = LoggerFactory.getLogger(MerchantApiImpl.class);

	@Resource
	private ICommonService commonService;

	@Override
	public ShopMessageVo<List<ShopMerchant>> getMerchantList(List<Long> ids) {
		ShopMessageVo<List<ShopMerchant>> messageVo = new ShopMessageVo<List<ShopMerchant>>();
		logger.info("获取有效的商户列表调用到这里了=================");
		String imageDomain = ConfigPropertieUtils.getString("image.server.address");
		List<ShopMerchant> list = commonService.getList(ShopMerchant.class, "status", 1, "existIds", ids);
		if (list != null) {
			try {
				// 销量 好评
				List<ShopMerchant> salesVolumeMap = commonService.getListBySqlId(ShopOrderDetail.class,
						"getSalesVolumeByMerchant");
				List<ShopMerchant> praiseByMerchantMap = commonService.getListBySqlId(ShopComment.class,
						"getPraiseByMerchant");
				for (ShopMerchant merch : list) {
					merch.setSalesVolume("0");
					if (salesVolumeMap.size() > 0) {
						for (ShopMerchant map : salesVolumeMap) {
							if (merch.getId().compareTo(map.getId()) == 0) {
								merch.setSalesVolume(map.getSalesVolume());
								break;
							}
						}
					}

					merch.setPraise("0");
					if (praiseByMerchantMap.size() > 0) {
						for (ShopMerchant map : praiseByMerchantMap) {
							if (merch.getId().compareTo(map.getId()) == 0) {
								merch.setPraise(String.valueOf(Math.round(Double.valueOf(map.getPraise()))));
								break;
							}
						}
					}
				}
			} catch (Exception ex) {
				logger.error("首页获取商户点评和销量信息异常:" + ex.getMessage());
			}

			for (ShopMerchant merch : list) {
				merch.setLogoPic(imageDomain + merch.getLogoPic());
			}

			messageVo.setResult(true);
			messageVo.setT(list);
			messageVo.setMessage("数据获取成功");
			return messageVo;
		} else {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorMessage());
			return messageVo;
		}
	}

	@Override
	public ShopMessageVo<Map<String, Object>> getMerchantInfo(IdParamsVo params) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<Map<String, Object>>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("获取获取商户详细信息调用到这里了=================");
		ShopMerchant merchant = commonService.get(params.getId(), ShopMerchant.class);
		if (merchant != null) {
			String imageDomain = ConfigPropertieUtils.getString("image.server.address");
			merchant.setLogoPic(imageDomain + merchant.getLogoPic());

			try {
				// 好评
				List<ShopMerchant> praiseByMerchantMap = commonService.getListBySqlId(ShopComment.class,
						"getPraiseByMerchant");
				if (praiseByMerchantMap.size() > 0) {
					for (ShopMerchant map : praiseByMerchantMap) {
						if (merchant.getId().compareTo(map.getId()) == 0) {
							merchant.setPraise(String.valueOf(Math.round(Double.valueOf(map.getPraise()))));
							break;
						}
					}
				}
			} catch (Exception ex) {
				logger.error("获取获取商户点评信息数据异常:" + ex.getMessage());
			}

			resultMap.put("merch", merchant);

			// 商户背景图
			List<ShopMerchantPic> pics = commonService.getList(ShopMerchantPic.class, "merchId", merchant.getId(),
					"status", 1, "picType", 1);
			if (pics != null && pics.size() > 0) {
				for (ShopMerchantPic pic : pics) {
					pic.setUrl(imageDomain + pic.getUrl());
				}
			}

			resultMap.put("pics", pics);

			// 商户广告图
			List<ShopMerchantPic> advertPics = commonService.getList(ShopMerchantPic.class, "merchId", merchant.getId(),
					"status", 1, "picType", 3);
			if (advertPics != null && advertPics.size() > 0) {
				for (ShopMerchantPic pic : advertPics) {
					pic.setUrl(imageDomain + pic.getUrl());
				}
			}

			resultMap.put("advertPics", advertPics);

			messageVo.setResult(true);
			messageVo.setT(resultMap);
			messageVo.setMessage("数据获取成功");
			return messageVo;
		} else {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorMessage());
			return messageVo;
		}
	}

	@Override
	public ShopMessageVo<Map<String, Object>> getCommodityList(IdParamsVo params) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<Map<String, Object>>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("获取商户上架的商品列表调用到这里了=================");
		ShopMerchant merchant = commonService.get(params.getId(), ShopMerchant.class);
		if (merchant != null) {
			String imageDomain = ConfigPropertieUtils.getString("image.server.address");
			merchant.setLogoPic(imageDomain + merchant.getLogoPic());
			
			try {
				// 好评
				List<ShopMerchant> praiseByMerchantMap = commonService.getListBySqlId(ShopComment.class,
						"getPraiseByMerchant");
				if (praiseByMerchantMap.size() > 0) {
					for (ShopMerchant map : praiseByMerchantMap) {
						if (merchant.getId().compareTo(map.getId()) == 0) {
							merchant.setPraise(String.valueOf(Math.round(Double.valueOf(map.getPraise()))));
							break;
						}
					}
				}
			} catch (Exception ex) {
				logger.error("获取获取商户点评信息数据异常:" + ex.getMessage());
			}
			
			resultMap.put("merch", merchant);

			// 商户背景图
			List<ShopMerchantPic> pics = commonService.getList(ShopMerchantPic.class, "merchId", merchant.getId(),
					"status", 1, "picType", 2);
			if (pics != null && pics.size() > 0) {
				for (ShopMerchantPic pic : pics) {
					pic.setUrl(imageDomain + pic.getUrl());
				}
			}

			resultMap.put("pics", pics);

			// 商品列表
			List<ShopCommodity> commodityList = commonService.getList(ShopCommodity.class, "merchantId",
					merchant.getId(), "status", 1);
			if (commodityList != null && commodityList.size() > 0) {
				for (ShopCommodity com : commodityList) {
					// 折扣
					Double price = BigDecimal.valueOf(com.getPrice()).multiply(BigDecimal.valueOf(com.getDiscount()))
							.doubleValue();
					com.setPrice(price);
					com.setLogo(imageDomain + com.getLogo());
					com.setCommodityType(3);
				}
			}

			resultMap.put("commoditys", commodityList);
			messageVo.setResult(true);
			messageVo.setT(resultMap);
			messageVo.setMessage("数据获取成功");
			return messageVo;
		} else {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorMessage());
			return messageVo;
		}
	}

	@Override
	public ShopMessageVo<Map<String, Object>> getCommodityInfo(IdParamsVo params) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<Map<String, Object>>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("获取商品详细信息调用到这里了=================");
		String imageDomain = ConfigPropertieUtils.getString("image.server.address");
		ShopCommodity commodity = commonService.get(params.getId(), ShopCommodity.class);
		ShopMerchant merchant = commonService.get(commodity.getMerchantId(), ShopMerchant.class);
		merchant.setLogoPic(imageDomain + merchant.getLogoPic());
		merchant.setPraise("5");
		resultMap.put("merch", merchant);
		if (commodity != null) {
			commodity.setLogo(imageDomain + commodity.getLogo());
			Double price = BigDecimal.valueOf(commodity.getPrice())
					.multiply(BigDecimal.valueOf(commodity.getDiscount())).doubleValue();
			commodity.setPrice(price);
			resultMap.put("commodity", commodity);
			if (commodity.getCommodityType() == 1) //// 商品类型：1-区块链计算机,2-通用商品
			{
				ShopProductChainComputer product = commonService.get(commodity.getProductId(),
						ShopProductChainComputer.class);
				resultMap.put("product", product);
			} else if (commodity.getCommodityType() == 2) {
				ShopProductCommon product = commonService.get(commodity.getProductId(), ShopProductCommon.class);
				resultMap.put("product", product);
			}

			commodity.setCommodityType(3);

			List<ShopCommodityPic> pics = commonService.getList(ShopCommodityPic.class, "commodityId",
					commodity.getId(), "status", 1);
			if (pics != null && pics.size() > 0) {
				for (ShopCommodityPic pic : pics) {
					pic.setUrl(imageDomain + pic.getUrl());
				}
			}

			resultMap.put("pics", pics);

			messageVo.setResult(true);
			messageVo.setT(resultMap);
			messageVo.setMessage("数据获取成功");
			return messageVo;
		} else {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorMessage());
			return messageVo;
		}
	}

	@Override
	public ShopMessageVo<MerchantCommodityResponseVo> getOrderCommodity(CommodityIdParamsVo params) {
		ShopMessageVo<MerchantCommodityResponseVo> messageVo = new ShopMessageVo<MerchantCommodityResponseVo>();
		MerchantCommodityResponseVo resp = new MerchantCommodityResponseVo();
		logger.info("获取商户商品简要信息调用到这里了=================");
		String imageDomain = ConfigPropertieUtils.getString("image.server.address");
		ShopCommodity commodity = commonService.get(params.getCommodityId(), ShopCommodity.class);
		ShopMerchant merchant = commonService.get(commodity.getMerchantId(), ShopMerchant.class);
		if (merchant != null) {
			resp.setMerchantId(merchant.getId());
			resp.setMerchantName(merchant.getMerchName());
			resp.setMerchantType(merchant.getMerchType());
			resp.setMerchantLogo(imageDomain + merchant.getLogoPic());
		}

		if (commodity != null) {
			CommodityVo vo = new CommodityVo();
			vo.setCommodityId(commodity.getId());
			vo.setCommodityType(3);
			vo.setCommodityName(commodity.getTitle());
			vo.setCommodityLogo(imageDomain + commodity.getLogo());
			Double price = BigDecimal.valueOf(commodity.getPrice())
					.multiply(BigDecimal.valueOf(commodity.getDiscount())).doubleValue();
			vo.setCommodityPrice(price);

			List<CommodityVo> list = new ArrayList<CommodityVo>();
			list.add(vo);
			resp.setCommodityList(list);

			messageVo.setResult(true);
			messageVo.setT(resp);
			messageVo.setMessage("数据获取成功");
			return messageVo;
		} else {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorMessage());
			return messageVo;
		}
	}

}
