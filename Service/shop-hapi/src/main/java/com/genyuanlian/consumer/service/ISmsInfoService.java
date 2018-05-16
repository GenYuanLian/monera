package com.genyuanlian.consumer.service;

import com.genyuanlian.consumer.shop.model.ShopSmsInfo;

public interface ISmsInfoService {
	/**
	 * 短信发送记录service
	 * 
	 * @param info
	 */
	public void save(ShopSmsInfo info);

	/**
	 * 
	 * 方法名称: queryValiditytime ； 
	 * 方法描述: count:次数 time：分钟 
	 * 返回类型: boolean ；
	 * 创建人：Eirc.Fan ； 创建时间：2017年12月21日 上午9:10:17； @throws
	 */
	public boolean queryValiditytime(String mobile, int count, int time, String type);
}
