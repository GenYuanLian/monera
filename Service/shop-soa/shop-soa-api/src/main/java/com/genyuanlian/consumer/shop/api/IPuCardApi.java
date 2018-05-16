package com.genyuanlian.consumer.shop.api;

import java.util.List;
import java.util.Map;

import com.genyuanlian.consumer.shop.model.ShopPuCard;
import com.genyuanlian.consumer.shop.vo.CommodityIdParamsVo;
import com.genyuanlian.consumer.shop.vo.MerchantCommodityResponseVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;

public interface IPuCardApi {

	/**
	 * 获取提货卡类型集合
	 * @param merchantId 商户Id
	 * @return
	 */
	public ShopMessageVo<Map<String, Object>> getPuCardTypes(Long merchantId);
	
	/**
	 * 获取指定类型id的提货卡集合
	 * @param typeId
	 * @param status
	 * @param type 类型：1-实体卡；2-电子卡
	 * @param channel 渠道：0表示线下制卡，2表示PC端分布式电商，4表示手机端分布电商，6表示H5电商电子卡
	 * @return
	 */
	public List<ShopPuCard> getPuCardsByTypeId(Long typeId,Integer status,Integer type,Integer channel);
	
	/**
	 * 激活提货卡
	 * @param memberId
	 * @param activationCode
	 * @param channel
	 * @return
	 */
	public ShopMessageVo<String> activation(Long memberId,String activationCode,String channel);
	
	/**
	 * 分页查询提货卡
	 * @param memberId 会员Id
	 * @param isValid 是否可用:1-是；0-否
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public ShopMessageVo<Map<String, Object>>getPagePuCards(Long memberId,Integer isValid,Integer pageIndex,Integer pageSize);
	
	/**
	 * 查询订单产品
	 * @param params
	 * @return
	 */
	public ShopMessageVo<MerchantCommodityResponseVo>getOrderCommodity(CommodityIdParamsVo params);
}
