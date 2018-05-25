package com.genyuanlian.consumer.api.impl;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.genyuanlian.consumer.service.IPayRecordService;
import com.genyuanlian.consumer.shop.api.IPayRecordApi;
import com.genyuanlian.consumer.shop.api.IWeixinpayApi;
import com.genyuanlian.consumer.shop.enums.ShopErrorCodeEnum;
import com.genyuanlian.consumer.shop.model.ShopPayRecord;
import com.genyuanlian.consumer.shop.utils.WeixinpayProperties;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.genyuanlian.consumer.shop.vo.WeixinpayParamsVo;
import com.genyuanlian.consumer.utils.WeixinpayUtils;
import com.genyuanlian.consumer.vo.WeixinOrderQuery;
import com.genyuanlian.consumer.vo.WeixinUnifiedOrderVo;
import com.hnair.consumer.dao.service.ICommonService;
import com.hnair.consumer.utils.HttpClientUtils;
import com.hnair.consumer.utils.Sha1Util;
import com.hnair.consumer.utils.XMLParser;

@Component("weixinpayApi")
public class WeixinpayApiImpl implements IWeixinpayApi {

	private static Logger logger = LoggerFactory.getLogger(WeixinpayApiImpl.class);

	@Resource
	private ICommonService commonService;

	@Resource
	private IPayRecordService payRecordService;

	@Resource
	private IPayRecordApi payRecordApi;

	@Resource(name = "slaveRedisTemplate")
	private RedisTemplate<String, String> slaveRedisTemplate;

	@Resource(name = "masterRedisTemplate")
	private RedisTemplate<String, String> masterRedisTemplate;

	@Override
	public ShopMessageVo<Map<String, Object>> unifiedOrder(WeixinpayParamsVo req) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<Map<String, Object>>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("微信支付调用到这里了=================, 订单编号为：" + req.getOrderNo() + "支付金额:" + req.getTotalAmount() + "订单编号:"
				+ req.getOrderNo() + "商品名称:" + req.getSubject());

		// 判断是否重复支付
		ShopMessageVo<ShopPayRecord> buildResult = payRecordService.buildPayRecord(req.getMemberId(),
				req.getTotalAmount(), req.getOrderNo(), req.getSubject(), 1);
		if (!buildResult.isResult()) {
			messageVo.setErrorCode(buildResult.getErrorCode());
			messageVo.setErrorMessage(buildResult.getErrorMessage());
			return messageVo;
		}
		ShopPayRecord payRecord = buildResult.getT();

		String tradeType = "JSAPI";
		String scene_info = null;
		if (req.getOpenId() == null) {
			tradeType = "MWEB";
			Map<String, Object> h5Info = new HashMap<>();
			h5Info.put("type", "Wap");
			h5Info.put("wap_url", WeixinpayProperties.WEIXIN_WAP_URL);
			h5Info.put("wap_name", WeixinpayProperties.WEIXIN_WAP_NAME);
			Map<String, Object> scene = new HashMap<>();
			scene.put("h5_info", h5Info);
			scene_info = JSON.toJSONString(scene);
		}

		Map<String, String> payResult = unifiedOrder(WeixinpayProperties.WEIXIN_NOTIFY_URL, payRecord.getPayNum(),
				req.getSubject(), req.getTotalAmount(), req.getSpbillCreateIp(), tradeType,
				WeixinpayProperties.WEIXIN_APP_ID, req.getOpenId(), WeixinpayProperties.WEIXIN_MCHID, scene_info,
				payRecord);

		if (payResult.containsKey("return_code")) {
			String returnCode = payResult.get("return_code");
			if ("FAIL".equalsIgnoreCase(returnCode)) {
				String message = payResult.get("return_msg");
				messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800009.getErrorCode().toString());
				messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800009.getErrorMessage() + "," + message);
				return messageVo;
			}
		}

		String prepay_id = null;
		if (payResult.containsKey("prepay_id") && payResult.get("prepay_id") != null) {
			prepay_id = payResult.get("prepay_id").toString();
		}
		String mweb_url = null;
		if (payResult.containsKey("mweb_url") && payResult.get("mweb_url") != null) {
			mweb_url = payResult.get("mweb_url").toString();
		}
		// 生成支付签名，要采用URLENCODER的原始值进行MD5算法！
		String appId = WeixinpayProperties.WEIXIN_APP_ID;
		String noncestr = WeixinpayUtils.getNonceStr();
		String timestamp = WeixinpayUtils.getTimeStamp();
		SortedMap<String, Object> wxPayParamMap = new TreeMap<String, Object>();
		wxPayParamMap.put("appId", appId);
		wxPayParamMap.put("timeStamp", timestamp);
		wxPayParamMap.put("nonceStr", noncestr);
		wxPayParamMap.put("package", "prepay_id=" + prepay_id);
		wxPayParamMap.put("signType", WeixinpayProperties.paySign);
		wxPayParamMap.put("key", WeixinpayProperties.WEIXIN_SELF_DEFINE);

		logger.info("微信支付，生成签名的参数：appid:" + appId + ", timeStamp:" + timestamp + ", nonceStr:" + noncestr
				+ ", package:prepay_id=" + "=" + prepay_id + ", signType" + WeixinpayProperties.paySign);

		String paySign = null;
		try {
			paySign = WeixinpayUtils.getSign(wxPayParamMap);
			logger.info("微信支付，orderNo:" + req.getOrderNo() + ",当前paySign为：" + paySign);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 支付相关参数
		resultMap.put("appId", appId);
		resultMap.put("timestamp", timestamp);
		resultMap.put("nonceStr", noncestr);
		resultMap.put("packageStr", "prepay_id=" + prepay_id);
		resultMap.put("signType", "MD5");
		resultMap.put("paySign", paySign);
		resultMap.put("mwebUrl", mweb_url);
		// // 获取jsapi的token
		// String token = "";
		// // 获取jsapi的ticket
		// String tiket = "";
		// try {
		// token = getToken(appId, WeixinpayProperties.WEIXIN_APP_SECRET);
		// logger.info("微信支付，当前获取的token为：" + token);
		// } catch (Exception e) {
		// e.printStackTrace();
		// logger.info("微信支付，当前获取的token异常");
		// }
		//
		// try {
		// tiket = getTicket(token);
		// logger.info("微信支付，当前获取的tiket为：" + tiket);
		// } catch (Exception e) {
		// e.printStackTrace();
		// logger.info("微信支付，当前获取的tiket异常");
		// }

		// 重新获取随机数，以及时间串
		// String url = WeixinpayProperties.WEIXIN_HOST_URL +
		// WeixinpayProperties.H5PayPage;

		SortedMap<String, String> singnTiketMap = new TreeMap<String, String>();

		singnTiketMap.put("noncestr", noncestr);
		// singnTiketMap.put("jsapi_ticket", tiket);
		singnTiketMap.put("timestamp", timestamp);
		// singnTiketMap.put("url", url);
		String signature = "";
		try {
			signature = Sha1Util.createSHA1Sign(singnTiketMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("微信支付， 当前获取的signature为：" + signature);

		resultMap.put("signature", signature);
		resultMap.put("orderId", payRecord.getId());

		// 根据订单ID获取相关的订单信息
		logger.info("微信支付，获取相关签名信息： noncestr=" + noncestr + ", timestamp=" + timestamp);

		messageVo.setResult(true);
		messageVo.setT(resultMap);
		return messageVo;
	}

	/**
	 * Description: 封装调用微信统一下单接口参数,并且调用接口 @Version1.0 2016年12月9日 上午10:50:52 by
	 * 李超（li-ch3@hnair.com）创建
	 * 
	 * @param hostUrl
	 * @param orderNum
	 * @param name
	 * @param price
	 * @param openId
	 * @param payRecord
	 * @return
	 */
	private Map<String, String> unifiedOrder(String notifyUrl, String orderNum, String name, Double price,
			String spbillCreateIp, String tradeType, String appId, String openId, String mchid, String scene_info,
			ShopPayRecord payRecord) {

		Map<String, String> result = new HashMap<>();
		String wxStr = "";
		Integer productPrice = 0;
		DecimalFormat df = new DecimalFormat("#######");
		productPrice = Integer.parseInt(df.format(price * 100));// 转换成分为单位

		// 如果是微信支付，则调用微信创建订单的接口
		WeixinUnifiedOrderVo reqData = new WeixinUnifiedOrderVo.UnifiedOrderReqDataBuilder(appId, mchid, name, orderNum,
				productPrice, spbillCreateIp, notifyUrl, tradeType, scene_info)
						.setKey(WeixinpayProperties.WEIXIN_SELF_DEFINE).setOpenid(openId).build();
		logger.info("微信支付问题查找，签名为：" + reqData.getSign());
		logger.info("微信支付问题查找，orderNum:" + orderNum + ", productPrice:" + productPrice + ", spbillCreateIp:"
				+ spbillCreateIp);
		try {
			Map<String, Object> map = unifiedOrder(reqData);
			// 保存支付记录
			payRecord.setCallReq(JSON.toJSONString(reqData));
			payRecord.setCallResp(JSON.toJSONString(map));
			payRecordService.save(payRecord);
			if (map.containsKey("return_code")) {
				String returnCode = (String) map.get("return_code");
				if (StringUtils.isNotBlank(returnCode) && returnCode.equalsIgnoreCase("FAIL")) {
					result.put("return_code", "FAIL");
					if (map.containsKey("return_msg")) {
						result.put("return_msg", (String) map.get("return_msg"));
					} else {
						result.put("return_msg", "获取微信支付信息失败");
					}
					return result;
				}
			}

			logger.info("微信支付问题查找, 统一下单接口:" + map.toString());
			StringBuffer strBuffer = new StringBuffer("");
			if (map != null && map.keySet() != null && map.keySet().size() > 0) {
				for (String k : map.keySet()) {
					if (strBuffer.length() == 0)
						strBuffer.append("{\"" + k + "\"").append(":").append("\"" + map.get(k) + "\"");
					else
						strBuffer.append(",").append("\"" + k + "\"").append(":").append("\"" + map.get(k) + "\"");
				}
				strBuffer.append("}");
			}
			wxStr = strBuffer.toString();
			logger.info("微信支付问题查找, 统一下单接口. 当前返回的数据转换为json格式:" + wxStr);
			wxStr = map.get("prepay_id").toString();
			if (map.containsKey("prepay_id") && map.get("prepay_id") != null) {
				result.put("prepay_id", map.get("prepay_id").toString());
			}
			if (map.containsKey("mweb_url") && map.get("mweb_url") != null) {
				String returnUrl = WeixinpayProperties.WEIXIN_RETURN_URL + payRecord.getOrderNo();
				result.put("mweb_url",
						map.get("mweb_url").toString() + "&redirect_url=" + URLEncoder.encode(returnUrl, "UTF-8"));
				logger.info("mweb_url=" + result.get("mweb_url").toString());
			}
			result.put("return_code", "SUCCESS");
			logger.info("不转为json格式的话，直接取prepay_id为：" + wxStr);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Description: 调用微信统一下单接口 @Version1.0 2016年12月9日 下午4:39:38 by
	 * 李超（li-ch3@hnair.com）创建
	 * 
	 * @param reqData
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	private static Map<String, Object> unifiedOrder(WeixinUnifiedOrderVo reqData)
			throws IOException, SAXException, ParserConfigurationException {
		if (reqData != null) {
			reqData.setKey(null);
		}
		String res = HttpClientUtils.doPost(WeixinpayProperties.UNIFIED_ORDER_API, reqData);
		logger.info("UnifiedOrder get response:" + res);
		return XMLParser.getMapFromXML(res);
	}

	@SuppressWarnings("unchecked")
	private String getToken(String appId, String appSecret) throws Exception {
		// String requestUrl = WeixinpayProperties.JSAPI_TOKEN +
		// "?grant_type=client_credential&appid=" + appId
		// + "&secret=" + appSecret;
		String requestUrl = null;

		String res = HttpClientUtils.doGet(requestUrl);
		logger.info(res);
		Map<String, Object> resultMap = JSONObject.parseObject(res, HashMap.class);
		if (resultMap.get("access_token") == null) {
			return null;
		}

		return resultMap.get("access_token").toString();
	}

	@SuppressWarnings("unchecked")
	private String getTicket(String token) throws Exception {

		// String requestUrl = WeixinpayProperties.JSAPI_TICKET + "?access_token=" +
		// token + "&&type=jsapi";
		String requestUrl = null;

		String res = HttpClientUtils.doGet(requestUrl);
		System.out.println(res);
		Map<String, Object> resultMap = JSONObject.parseObject(res, HashMap.class);
		if (resultMap.get("ticket") == null) {
			return null;
		}

		return resultMap.get("ticket").toString();
	}

	@Override
	public ShopMessageVo<String> weixinpayAysnNotify(Map<String, String> map) {
		ShopMessageVo<String> result = new ShopMessageVo<>();

		WeixinOrderQuery orderQuery = new WeixinOrderQuery();
		orderQuery.setAppid(map.get("appid"));
		orderQuery.setMch_id(map.get("mch_id"));
		orderQuery.setTransaction_id(map.get("transaction_id"));
		orderQuery.setOut_trade_no(map.get("out_trade_no"));
		orderQuery.setNonce_str(map.get("nonce_str"));

		orderQuery.setPartnerKey(WeixinpayProperties.WEIXIN_SELF_DEFINE);

		boolean isSuccess = false;
		Map<String, String> orderMap = orderQuery.reqOrderquery();
		// 此处添加支付成功后，支付金额和实际订单金额是否等价，防止钓鱼
		if (orderMap.get("return_code") != null && orderMap.get("return_code").equalsIgnoreCase("SUCCESS")) {
			if (orderMap.get("trade_state") != null && orderMap.get("trade_state").equalsIgnoreCase("SUCCESS")) {
				String total_fee = map.get("total_fee");
				String order_total_fee = orderMap.get("total_fee");
				if (Integer.parseInt(order_total_fee) >= Integer.parseInt(total_fee)) {
					isSuccess = true;
				}
			}
		}

		String noticeStr = null;
		if (!isSuccess) {
			// 支付失败
			logger.info("微信支付失败");
			noticeStr = setXML("FAILURE", "");
			result.setT(noticeStr);
		} else {
			// 支付成功，商户处理后同步返回给微信参数
			ShopMessageVo<String> payRecordCheck = payRecordCheck(orderMap);
			if (!payRecordCheck.isResult()) {
				// 支付回调逻辑失败
				logger.info("支付回调逻辑失败");
				noticeStr = setXML("FAILURE", payRecordCheck.getErrorMessage());
				result.setT(noticeStr);
			} else {
				// 支付成功
				logger.info("支付成功");
				noticeStr = setXML("SUCCESS", "");
				result.setT(noticeStr);
				result.setResult(true);
			}
		}

		return result;
	}

	/**
	 * 验证业务参数，并执行业务逻辑
	 * 
	 * @param req
	 * @return
	 */
	private ShopMessageVo<String> payRecordCheck(Map<String, String> req) {
		ShopMessageVo<String> result = new ShopMessageVo<>();

		List<ShopPayRecord> payRecords = commonService.getList(ShopPayRecord.class, "payNum", req.get("out_trade_no"),
				"accountType", 1);
		// 交易号验证
		if (payRecords == null || payRecords.size() == 0) {
			result.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800003.getErrorCode().toString());
			result.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800003.getErrorMessage());
			return result;
		}
		ShopPayRecord payRecord = payRecords.get(0);
		// 金额验证

		if (Double.compare(payRecord.getAmount() * 100, Double.valueOf(req.get("total_fee"))) != 0) {
			result.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800005.getErrorCode().toString());
			result.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800005.getErrorMessage());
			return result;
		}

		// 支付记录已修改
		if (payRecord.getStatus() != 0) {
			result.setResult(true);
			result.setT(payRecord.getPayNum());
			return result;
		}

		// 修改支付记录
		// 微信只有openid
		payRecord.setPayAccount(req.get("openid"));
		payRecord.setStatus(req.get("trade_state").equals("SUCCESS") ? 1 : 2);
		payRecord.setTradeNo(req.get("transaction_id"));

		return payRecordApi.paySuccess(payRecord);
	}

	@Override
	public String setXML(String return_code, String return_msg) {
		return "<xml><return_code><![CDATA[" + return_code + "]]></return_code><return_msg><![CDATA[" + return_msg
				+ "]]></return_msg></xml>";
	}

	@SuppressWarnings("unchecked")
	@Override
	public ShopMessageVo<Map<String, Object>> getWeixinOpenId(String code) throws Exception {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<>();
		Map<String, Object> resultMap = new HashMap<>();

		String key = "wxcode_" + code;
		//先取本地缓存
		String resp = slaveRedisTemplate.opsForValue().get(key);
		if (StringUtils.isBlank(resp)) {
			//若本地没有，调微信接口
			String requestUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
					+ WeixinpayProperties.WEIXIN_APP_ID + "&secret=" + WeixinpayProperties.WEIXIN_APP_SECRET + "&code="
					+ code + "&grant_type=authorization_code";

			logger.info("请求微信requestUrl=" + requestUrl);
			resp = HttpClientUtils.doGet(requestUrl);
			logger.info("微信返回resp="+resp);
			if (StringUtils.isBlank(resp)) {
				messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800012.getErrorCode().toString());
				messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800012.getErrorMessage());
				return messageVo;
			}
			//微信调用成功，加入缓存
			masterRedisTemplate.delete(key);
			masterRedisTemplate.opsForValue().set(key, resp);
			masterRedisTemplate.expire(key, 5, TimeUnit.MINUTES);
		}
		logger.info("resp=" + resp);

		resultMap = (Map) JSONObject.parseObject(resp, HashMap.class);

		if (resultMap.get("openid") == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800012.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800012.getErrorMessage());
			return messageVo;
		}

		messageVo.setT(resultMap);
		messageVo.setResult(true);

		return messageVo;
	}
}
