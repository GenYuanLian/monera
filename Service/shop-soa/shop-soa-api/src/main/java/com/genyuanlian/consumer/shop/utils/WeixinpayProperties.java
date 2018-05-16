package com.genyuanlian.consumer.shop.utils;

import java.util.Random;

import com.hnair.consumer.utils.MD5Utils;
import com.hnair.consumer.utils.system.ConfigPropertieUtils;

public class WeixinpayProperties {
	public static String WEIXIN_APP_ID = ConfigPropertieUtils.getString("WEIXIN_APP_ID");
	public static String WEIXIN_APP_SECRET = ConfigPropertieUtils.getString("WEIXIN_APP_SECRET");
	public static String WEIXIN_HOST_URL = ConfigPropertieUtils.getString("WEIXIN_HOST_URL");
	public static String UNIFIED_ORDER_API = ConfigPropertieUtils.getString("UNIFIED_ORDER_API");
	public static String WEIXIN_SELF_DEFINE = ConfigPropertieUtils.getString("WEIXIN_SELF_DEFINE");
	public static String WEIXIN_MCHID = ConfigPropertieUtils.getString("WEIXIN_MCHID");
	public static String JSAPI_TOKEN = ConfigPropertieUtils.getString("JSAPI_TOKEN");
	public static String JSAPI_TICKET = ConfigPropertieUtils.getString("JSAPI_TICKET");
	public static String PAY_QUERY_API = ConfigPropertieUtils.getString("PAY_QUERY_API");
	public static String paySign = "MD5";
	public static String H5PayPage = ConfigPropertieUtils.getString("H5_PAY_URL");
	public static String WEIXIN_APP_CERT = ConfigPropertieUtils.getString("WEIXIN_APP_CERT");
	//WAP网站URL地址
	public static String WEIXIN_WAP_URL=ConfigPropertieUtils.getString("WEIXIN_WAP_URL");
	 //WAP 网站名
	public static String WEIXIN_WAP_NAME=ConfigPropertieUtils.getString("WEIXIN_WAP_NAME");

	public static String getNonceStr() {
		Random random = new Random();
		return MD5Utils.MD5Encode(String.valueOf(random.nextInt(10000)), "UTF-8");
	}
}
