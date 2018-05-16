package com.genyuanlian.consumer.shop.utils;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.hnair.consumer.utils.DateUtil;
import com.hnair.consumer.utils.system.ConfigPropertieUtils;

public class AlipayProperties {
	//合作伙伴身份ID，签约账号，以2088开头由16位纯数字组成的字符串，查看地址：https://openhome.alipay.com/platform/keyManage.htm?keyType=partner
	public static String ALIPAY_PARTNER = ConfigPropertieUtils.getString("ALIPAY_PARTNER");
    //商户应用APPID，只要您的应用中包含服务窗接口且是开通状态，就可以用此应用对应的appid。开发者可登录开放平台-管理中心-对应应用中查看
	public static String ALIPAY_APP_ID=ConfigPropertieUtils.getString("ALIPAY_APP_ID");
    //开发者应用私钥。java配置PKCS8格式
	public static String ALIPAY_RSA_RRIVATE_KEY=ConfigPropertieUtils.getString("ALIPAY_RSA_RRIVATE_KEY");
	//返回页地址
	public static String ALIPAY_RETURN_URL=ConfigPropertieUtils.getString("ALIPAY_RETURN_URL");
	//异步接口请求
	public static String ALIPAY_NOTIFY_URL=ConfigPropertieUtils.getString("ALIPAY_NOTIFY_URL");
	//编码字符集。默认 utf-8
	public static String CHARSET="utf-8";
	//返回格式。默认json
	public static String FORMAT="json";
	// 签名方式
	public static String SIGN_TYPE = "RSA2";
	//支付宝公钥
	public static String ALIPAY_KEY=ConfigPropertieUtils.getString("ALIPAY_KEY");
	//开发者应用公钥，用于获取同步返回信息后进行验证，验证是否是支付宝发送的信息。
	public static String ALIPAY_PUBLIC_KEY =ConfigPropertieUtils.getString("ALIPAY_PUBLIC_KEY");
	// 接收通知的接口名
	public static String ALIPAY_SERVICE =ConfigPropertieUtils.getString("ALIPAY_SERVICE");
	
	
	public static Map<String,String> getSignMap(String outTradeNo,Double totalFee,String subject,String body){
		Map<String,String> signMap=new HashMap<String,String>();
		signMap.put("app_id", ALIPAY_APP_ID);
		signMap.put("method", "alipay.trade.app.pay");
		signMap.put("format", "JSON");
		signMap.put("notify_url", ALIPAY_NOTIFY_URL);
		signMap.put("charset", "utf-8");
		signMap.put("payment_type", "1");
		signMap.put("timestamp",DateUtil.format(new Date(), DateUtil.DATE_PATTERN));
		signMap.put("version", "1.0");
		signMap.put("sign_type","RSA");
		signMap.put("biz_content",JSONObject.toJSON(AlipayProperties.getPayMap(outTradeNo,totalFee,subject, body)).toString());
		return signMap;
	};
	/**
	 * Description: 组装支付参数
	 * @Version1.0 2016年12月14日 下午8:45:47 by 李超（li-ch3@hnair.com）创建
	 * @param outTradeNo
	 * @param totalFee
	 * @param subject
	 * @param body
	 * @return
	 */
	public static Map<String,String> getPayMap(String outTradeNo,Double totalFee,String subject,String body){
		Map<String,String> payMap=new HashMap<String,String>();
		try {
			payMap.put("out_trade_no", outTradeNo);
			payMap.put("subject",(StringUtils.isEmpty(subject)?"":URLEncoder.encode(subject,"utf-8")));
//			payMap.put("seller_id",ALIPAY_PARTNER);//此处填写pid和邮箱都可以
			payMap.put("total_amount", totalFee.toString());
			payMap.put("product_code", "QUICK_WAP_PAY");
			payMap.put("body",(StringUtils.isEmpty(body)?"":URLEncoder.encode(body,"utf-8")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return payMap;
	};


	/**
	 * 组装退款参数
	 * @param orderNo
	 * @param refundFee
	 * @param refundOrderNo
	 * @return
	 */
	public static Map<String,String> getRefundMap(String orderNo,Double refundFee,String refundOrderNo){
		Map<String,String> payMap=new HashMap<String,String>();
		try {
			payMap.put("out_trade_no",orderNo);
			payMap.put("refund_amount",refundFee.toString());
			payMap.put("out_request_no",refundOrderNo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return payMap;
	}
	
	/**
	 * 组装转账请求参数
	 * @param amount
	 * @param outBizNo
	 * @return
	 */
	public static Object getTransferMap(String amount, String outBizNo,String remark,String transferType) {
		Map<String,String> payMap=new HashMap<String,String>();
		try {
			payMap.put("out_biz_no",outBizNo);
			//如果配置文件账号修改，此处payee_type的值可能要更改，参见支付宝接口文档
			payMap.put("payee_type","ALIPAY_LOGONID");
			if("1001".equals(transferType)){
//				payMap.put("payee_account",GANGHANG_ACCOUNT);
			}else if("1002".equals(transferType)){
				payMap.put("payee_account","待定");
			}
			payMap.put("amount", amount);
			payMap.put("remark", (StringUtils.isEmpty(remark)?"":URLEncoder.encode(remark,"utf-8")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return payMap;
	}
}

