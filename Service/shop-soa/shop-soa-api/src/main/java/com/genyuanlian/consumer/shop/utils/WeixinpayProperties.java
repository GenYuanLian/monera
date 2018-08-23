package com.genyuanlian.consumer.shop.utils;

import java.util.Random;

import com.hnair.consumer.utils.MD5Utils;
import com.hnair.consumer.utils.system.ConfigPropertieUtils;

public class WeixinpayProperties {
	public static String WEIXIN_APP_ID = ConfigPropertieUtils.getString("WEIXIN_APP_ID");
	public static String WEIXIN_APP_SECRET = ConfigPropertieUtils.getString("WEIXIN_APP_SECRET");
	public static String WEIXIN_APP_ID_MIN_PROGRAM=ConfigPropertieUtils.getString("WEIXIN_APP_ID_MIN_PROGRAM");
	public static String WEIXIN_APP_SECRET_MIN_PROGRAM = ConfigPropertieUtils.getString("WEIXIN_APP_SECRET_MIN_PROGRAM");
	public static String WEIXIN_HOST_URL = ConfigPropertieUtils.getString("WEIXIN_HOST_URL");
	public static String UNIFIED_ORDER_API = ConfigPropertieUtils.getString("UNIFIED_ORDER_API");
	public static String WEIXIN_SELF_DEFINE = ConfigPropertieUtils.getString("WEIXIN_SELF_DEFINE");
	public static String WEIXIN_MCHID = ConfigPropertieUtils.getString("WEIXIN_MCHID");
	public static String JSAPI_TOKEN = ConfigPropertieUtils.getString("JSAPI_TOKEN");
	public static String JSAPI_TICKET = ConfigPropertieUtils.getString("JSAPI_TICKET");
//	public static String PAY_QUERY_API = ConfigPropertieUtils.getString("PAY_QUERY_API");
	public static String paySign = "MD5";
	public static String H5PayPage = ConfigPropertieUtils.getString("H5_PAY_URL");
//	public static String WEIXIN_APP_CERT = ConfigPropertieUtils.getString("WEIXIN_APP_CERT");
	//WAP网站URL地址
	public static String WEIXIN_WAP_URL=ConfigPropertieUtils.getString("WEIXIN_WAP_URL");
	 //WAP 网站名
	public static String WEIXIN_WAP_NAME=ConfigPropertieUtils.getString("WEIXIN_WAP_NAME");
	//回调地址
	public static String WEIXIN_NOTIFY_URL=ConfigPropertieUtils.getString("WEIXIN_NOTIFY_URL");
	//返回页
	public static String WEIXIN_RETURN_URL=ConfigPropertieUtils.getString("WEIXIN_RETURN_URL");
	//获取微信code接口
	public static String USER_AUTHORIZE=ConfigPropertieUtils.getString("USER_AUTHORIZE");
	//获取code时的跳转链接
	public static String WEIXIN_CODE_REDIRECT=ConfigPropertieUtils.getString("WEIXIN_CODE_REDIRECT");

	public static String getNonceStr() {
		Random random = new Random();
		return MD5Utils.MD5Encode(String.valueOf(random.nextInt(10000)), "UTF-8");
	}
}
