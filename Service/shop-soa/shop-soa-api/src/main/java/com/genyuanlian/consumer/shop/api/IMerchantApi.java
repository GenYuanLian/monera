package com.genyuanlian.consumer.shop.api;

import java.util.List;
import java.util.Map;

import com.genyuanlian.consumer.shop.model.ShopMerchant;
import com.genyuanlian.consumer.shop.vo.CommodityIdParamsVo;
import com.genyuanlian.consumer.shop.vo.IdParamsVo;
import com.genyuanlian.consumer.shop.vo.MerchantCommodityResponseVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;

public interface IMerchantApi {
	
	/**
	 * 获取有效的商户列表api
	 * @param ids 商户Id集合，为null则查询全部正常商户
	 * @return
	 */
	public ShopMessageVo<List<ShopMerchant>> getMerchantList(List<Long>ids);

	/**
	 * 获取商户详细信息api
	 * 
	 */
	public ShopMessageVo<Map<String, Object>> getMerchantInfo(IdParamsVo params);

	/**
	 * 获取商户上架的商品列表api
	 * 
	 */
	public ShopMessageVo<Map<String, Object>> getCommodityList(IdParamsVo params);

	/**
	 * 获取商品详细信息api
	 * 
	 */
	public ShopMessageVo<Map<String, Object>> getCommodityInfo(IdParamsVo params);

	/**
	 * 获取商户商品简要信息api
	 * 
	 */
	public ShopMessageVo<MerchantCommodityResponseVo> getOrderCommodity(CommodityIdParamsVo params);
	
	/**
	 * 获取算力服务商品列表
	 * 
	 */
	public ShopMessageVo<Map<String, Object>> getCommodityCalcForceList();

}
