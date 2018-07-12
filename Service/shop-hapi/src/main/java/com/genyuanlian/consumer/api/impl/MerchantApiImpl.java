package com.genyuanlian.consumer.api.impl;

import java.math.BigDecimal;
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
import com.genyuanlian.consumer.shop.model.ShopProductCalcForce;
import com.genyuanlian.consumer.shop.model.ShopProductChainComputer;
import com.genyuanlian.consumer.shop.model.ShopProductCommon;
import com.genyuanlian.consumer.shop.model.ShopSaleVolume;
import com.genyuanlian.consumer.shop.vo.CommodityIdParamsVo;
import com.genyuanlian.consumer.shop.vo.CommodityVo;
import com.genyuanlian.consumer.shop.vo.IdParamsVo;
import com.genyuanlian.consumer.shop.vo.MerchantCommodityResponseVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.genyuanlian.consumer.vo.KeyValuesVo;
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
		List<ShopMerchant> list = null;
		if (ids == null || ids.size() == 0) {
			list = commonService.getList(ShopMerchant.class, "status", 1);
		} else {
			list = commonService.getList(ShopMerchant.class, "status", 1, "existIds", ids);
		}

		if (list != null) {
			try {
				// 销量 好评
				List<ShopMerchant> salesVolumeMap = commonService.getListBySqlId(ShopOrderDetail.class,
						"getSalesVolumeByMerchant");

				List<ShopSaleVolume> virtualSalesVolumeMap = commonService.getListBySqlId(ShopSaleVolume.class,
						"getOrdersByMerchant");

				List<ShopMerchant> praiseByMerchantMap = commonService.getListBySqlId(ShopComment.class,
						"getPraiseByMerchant");
				for (ShopMerchant merch : list) {
					merch.setSalesVolume("0");
					// 真实
					if (salesVolumeMap.size() > 0) {
						for (ShopMerchant map : salesVolumeMap) {
							if (merch.getId() == map.getId()) {
								merch.setSalesVolume(map.getSalesVolume());
								break;
							}
						}
					}

					// 虚拟
					if (virtualSalesVolumeMap.size() > 0) {
						for (ShopSaleVolume map : virtualSalesVolumeMap) {
							if (merch.getId() == map.getMerchantId()) {
								int vol = Integer.valueOf(merch.getSalesVolume()) + map.getOrderCount();
								merch.setSalesVolume(String.valueOf(vol));
								break;
							}
						}
					}

					merch.setPraise("5");
					if (praiseByMerchantMap.size() > 0) {
						for (ShopMerchant map : praiseByMerchantMap) {
							if (merch.getId() == map.getId()) {
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
			// 状态：1-正常，2-冻结
			if (merchant.getStatus() != 1) {
				messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorCode().toString());
				messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorMessage());
				return messageVo;
			}

			String imageDomain = ConfigPropertieUtils.getString("image.server.address");
			merchant.setLogoPic(imageDomain + merchant.getLogoPic());
			merchant.setPraise("5");
			try {
				// 好评
				List<ShopMerchant> praiseByMerchantMap = commonService.getListBySqlId(ShopComment.class,
						"getPraiseByMerchant");
				if (praiseByMerchantMap.size() > 0) {
					for (ShopMerchant map : praiseByMerchantMap) {
						if (merchant.getId() == map.getId()) {
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
			// 状态：1-正常，2-冻结
			if (merchant.getStatus() != 1) {
				messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorCode().toString());
				messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorMessage());
				return messageVo;
			}

			String imageDomain = ConfigPropertieUtils.getString("image.server.address");
			merchant.setLogoPic(imageDomain + merchant.getLogoPic());
			merchant.setPraise("5");

			try {
				// 好评
				List<ShopMerchant> praiseByMerchantMap = commonService.getListBySqlId(ShopComment.class,
						"getPraiseByMerchant");
				if (praiseByMerchantMap.size() > 0) {
					for (ShopMerchant map : praiseByMerchantMap) {
						if (merchant.getId() == map.getId()) {
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

			// 虚拟销量
			try {
				List<ShopSaleVolume> virtualSalesVolumeMap = commonService.getListBySqlId(ShopSaleVolume.class,
						"getSaleVolumeByCommodity");
				for (ShopCommodity com : commodityList) {
					// 虚拟
					if (virtualSalesVolumeMap.size() > 0) {
						for (ShopSaleVolume map : virtualSalesVolumeMap) {
							if (com.getId() == map.getCommodityId() && map.getCommodityType() == 3) {
								int vol = com.getSaleQuantity() + map.getSaleVolume();
								com.setSaleQuantity(vol);
								break;
							}
						}
					}
				}

			} catch (Exception ex) {
				logger.error("首页获取商户商品列表销量信息异常:" + ex.getMessage());
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
		// 状态：1-正常，2-冻结
		if (merchant.getStatus() != 1) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorMessage());
			return messageVo;
		}
		merchant.setLogoPic(imageDomain + merchant.getLogoPic());
		merchant.setPraise("5");
		resultMap.put("merch", merchant);
		if (commodity != null) {
			// 状态：1-上架,2-下架
			if (commodity.getStatus() != 1) {
				messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorCode().toString());
				messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorMessage());
				return messageVo;
			}

			commodity.setLogo(imageDomain + commodity.getLogo());
			Double price = BigDecimal.valueOf(commodity.getPrice())
					.multiply(BigDecimal.valueOf(commodity.getDiscount())).doubleValue();
			commodity.setPrice(price);
			resultMap.put("commodity", commodity);
			List<KeyValuesVo> list = new ArrayList<>();
			if (commodity.getCommodityType() == 1) // 商品类型：1-区块链计算机,2-通用商品,3-算力服务
			{
				ShopProductChainComputer product = commonService.get(commodity.getProductId(),
						ShopProductChainComputer.class);
				resultMap.put("product", product);
				list.add(new KeyValuesVo("商品简介", product.getDescription()));
				list.add(new KeyValuesVo("产品特点", product.getFeature()));
				list.add(new KeyValuesVo("产品规格", product.getSpecification()));
			} else if (commodity.getCommodityType() == 2) {
				ShopProductCommon product = commonService.get(commodity.getProductId(), ShopProductCommon.class);
				resultMap.put("product", product);
				list.add(new KeyValuesVo("商品简介", product.getDescription()));
				list.add(new KeyValuesVo("产品特点", product.getFeature()));
				list.add(new KeyValuesVo("产品规格", product.getSpecification()));
				list.add(new KeyValuesVo("快递说明", product.getRemark()));
			} else if (commodity.getCommodityType() == 3) {
				ShopProductCalcForce product = commonService.get(commodity.getProductId(), ShopProductCalcForce.class);
				resultMap.put("product", product);
				list.add(new KeyValuesVo("商品简介", product.getDescription()));
				list.add(new KeyValuesVo("基本信息", product.getBaseInfo()));
				list.add(new KeyValuesVo("服务说明", product.getPackageDesc()));
			}
			list.add(new KeyValuesVo("支付说明", commodity.getPayExplain()));
			resultMap.put("list", list);

			commodity.setCommodityType(3);

			List<ShopCommodityPic> pics = commonService.getList(ShopCommodityPic.class, "commodityId",
					commodity.getId(), "status", 1);
			if (pics != null && pics.size() > 0) {
				for (ShopCommodityPic pic : pics) {
					pic.setUrl(imageDomain + pic.getUrl());
				}
			}

			resultMap.put("pics", pics);

			// 虚拟销量
			try {
				List<ShopSaleVolume> virtualSalesVolumeMap = commonService.getListBySqlId(ShopSaleVolume.class,
						"getSaleVolumeByCommodity");
				if (virtualSalesVolumeMap.size() > 0) {
					for (ShopSaleVolume map : virtualSalesVolumeMap) {
						if (commodity.getId() == map.getCommodityId() && map.getCommodityType() == 3) {
							int vol = commodity.getSaleQuantity() + map.getSaleVolume();
							commodity.setSaleQuantity(vol);
							break;
						}
					}
				}

			} catch (Exception ex) {
				logger.error("首页获取商户商品列表销量信息异常:" + ex.getMessage());
			}

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
		if (commodity == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorMessage());
			return messageVo;
		}
		// 状态：1-上架,2-下架
		if (commodity.getStatus() != 1) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorMessage());
			return messageVo;
		}
		if (commodity.getCommodityType() == 3) {
			resp.setWalletAddressRequire(1);
		}
		resp.setAddressRequire(commodity.getIsSendMail());

		ShopMerchant merchant = commonService.get(commodity.getMerchantId(), ShopMerchant.class);
		if (merchant != null) {
			resp.setMerchantId(merchant.getId());
			resp.setMerchantName(merchant.getMerchName());
			resp.setMerchantType(merchant.getMerchType());
			resp.setMerchantLogo(imageDomain + merchant.getLogoPic());
		}

		CommodityVo vo = new CommodityVo();
		vo.setCommodityId(commodity.getId());
		vo.setCommodityType(3);
		vo.setCommodityName(commodity.getTitle());
		vo.setCommodityLogo(imageDomain + commodity.getLogo());
		vo.setInventoryQuantity(commodity.getInventoryQuantity());
		Double price = BigDecimal.valueOf(commodity.getPrice()).multiply(BigDecimal.valueOf(commodity.getDiscount()))
				.doubleValue();
		vo.setCommodityPrice(price);
		vo.setTraceSource(commodity.getTraceSource());

		List<CommodityVo> list = new ArrayList<CommodityVo>();
		list.add(vo);
		resp.setCommodityList(list);

		messageVo.setResult(true);
		messageVo.setT(resp);
		messageVo.setMessage("数据获取成功");
		return messageVo;
	}

}
