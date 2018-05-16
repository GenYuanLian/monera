package com.genyuanlian.consumer.shop.api;

import java.util.List;

import com.genyuanlian.consumer.shop.model.ShopBaseArea;
import com.genyuanlian.consumer.shop.vo.AreaVo;
import com.genyuanlian.consumer.shop.vo.BaseAreaParentCodeParamsVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;

public interface IBaseAreaApi {
	/**
	 * 根据parentCode获取区域列表api
	 * 
	 * @param parentCode
	 */
	public ShopMessageVo<List<ShopBaseArea>> getAreasByParentCode(BaseAreaParentCodeParamsVo params);

	/**
	 * 获取全部区域数据
	 * 
	 * @param
	 */
	public ShopMessageVo<List<AreaVo>> getAreas();
}
