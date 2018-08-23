package com.genyuanlian.consumer.shop.api;

import java.util.Map;

import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.genyuanlian.consumer.shop.vo.WeixinpayParamsVo;

public interface IWeixinpayApi {
	/**
	 * 微信支付，同意下单接口
	 * @param req
	 * @return
	 */
	public ShopMessageVo<Map<String,Object>> unifiedOrder(WeixinpayParamsVo req);
	
	/**
	 * 微信支付回调接口处理
	 * @param map
	 * @return
	 */
	public ShopMessageVo<String> weixinpayAysnNotify(Map<String, String> map);
	
	/**
	 * 获取微信openId
	 * @param code
	 * @param isMinProgram 是否小程序
	 * @return
	 */
	public ShopMessageVo<Map<String, Object>>getWeixinOpenId(String code,Boolean isMinProgram) throws Exception ;
	
	/**
	 * 微信回调结果XML构造
	 * @param return_code
	 * @param return_msg
	 * @return
	 */
	public String setXML(String return_code, String return_msg);
	
	/**
	 * 获取微信分享配置参数
	 * @return
	 */
	public ShopMessageVo<Map<String, String>> getWeixinShareConfig(String url);
}
