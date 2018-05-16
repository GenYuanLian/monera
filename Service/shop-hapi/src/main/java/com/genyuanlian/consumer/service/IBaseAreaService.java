package com.genyuanlian.consumer.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.genyuanlian.consumer.shop.model.ShopBaseArea;

/**
 * Using IntelliJ IDEA.
 *
 * @author HNAyd.xian
 * @date 2018/2/1 16:51
 */
public interface IBaseAreaService {

	/**
	 * 根据parentCode获取区域列表service
	 * 
	 * @param parentCode
	 */
	public List<ShopBaseArea> getAreasByParentCode(Integer parentCode);
	
	//根据areaCode集合查询区域列表
	public List<ShopBaseArea> selectByAreaCodeList(@Param("areaCodeList")List<Integer> areaCodeList);
}
