package com.genyuanlian.consumer.api.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.genyuanlian.consumer.service.IPayRecordService;
import com.genyuanlian.consumer.shop.api.IAlipayApi;
import com.genyuanlian.consumer.shop.api.IPayRecordApi;
import com.genyuanlian.consumer.shop.enums.ShopErrorCodeEnum;
import com.genyuanlian.consumer.shop.model.ShopPayRecord;
import com.genyuanlian.consumer.shop.utils.AlipayProperties;
import com.genyuanlian.consumer.shop.vo.AlipayParamsVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.genyuanlian.consumer.utils.AlipayClientFactory;
import com.hnair.consumer.dao.service.ICommonService;

@Component("alipayApi")
public class AlipayApiImpl implements IAlipayApi {
	private static Logger logger = LoggerFactory.getLogger(AlipayApiImpl.class);

	@Resource
	private ICommonService commonService;
	@Resource
	private IPayRecordService payRecordService;
	@Resource
	private IPayRecordApi payRecordApi;

	@Override
	public ShopMessageVo<Map<String, Object>> H5Alipay(AlipayParamsVo param) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("支付宝支付调用到这里了=================,支付金额:" + param.getTotalAmount() + "用户ID:" + param.getMemberId()
				+ "订单编号:" + param.getOrderNo() + "商品名称:" + param.getSubject());

		// 判断是否重复支付
		ShopMessageVo<ShopPayRecord> buildResult = payRecordService.buildPayRecord(param.getMemberId(),
				param.getTotalAmount(), param.getOrderNo(), param.getSubject(), 2);
		if (!buildResult.isResult()) {
			messageVo.setErrorCode(buildResult.getErrorCode());
			messageVo.setErrorMessage(buildResult.getErrorMessage());
			return messageVo;
		}
		ShopPayRecord payRecord = buildResult.getT();

		logger.info("调用支付宝支付接口");
		// 实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.wap.pay
		AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
		// 传入return_url和notify_ur;
		request.setReturnUrl(param.getReturnUrl());
		request.setNotifyUrl(AlipayProperties.ALIPAY_NOTIFY_URL);
		logger.info("return_url=" + request.getReturnUrl());
		// SDK已经封装掉了公共参数，这里只需要传入业务参数
		request.setBizContent(JSONObject.toJSON(AlipayProperties.getPayMap(payRecord.getPayNum(), payRecord.getAmount(),
				param.getSubject(), param.getProductDesc())).toString());
		AlipayTradeWapPayResponse response = null;
		try {
			response = AlipayClientFactory.getAlipayClientInstance().pageExecute(request);
			payRecord.setCallReq(JSON.toJSONString(request));
			payRecord.setCallResp(JSON.toJSONString(response));
			payRecordService.save(payRecord);
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		logger.info("调用支付宝支付接口结束:返回结果:" + response.getBody());
		// 调用成功，则处理业务逻辑
		if (response.isSuccess()) {
			String resut = response.getBody();
			resultMap.put("payResult", resut);
		} else {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800002.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800002.getErrorMessage());
			return messageVo;
		}
		messageVo.setResult(true);
		messageVo.setT(resultMap);
		return messageVo;
	}

	@Override
	public ShopMessageVo<String> alipayAysnNotify(Map<String, String> req) throws Exception {
		ShopMessageVo<String> result = new ShopMessageVo<>();
		// 调用支付宝SDK验签
		boolean signVerified = AlipaySignature.rsaCheckV1(req, AlipayProperties.ALIPAY_KEY, AlipayProperties.CHARSET,
				AlipayProperties.SIGN_TYPE);
		if (signVerified) {
			// 验签成功,验证业务参数,并执行业务逻辑
			result= payRecordCheck(req);
		} else {
			result.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800004.getErrorCode().toString());
			result.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800004.getErrorMessage());
		}
		
		return result;
	}
	
	/**
	 * 验证业务参数，并执行业务逻辑
	 * @param req
	 * @return
	 */
	private ShopMessageVo<String> payRecordCheck(Map<String, String> req){
		ShopMessageVo<String> result = new ShopMessageVo<>();
		
		List<ShopPayRecord> payRecords = commonService.getList(ShopPayRecord.class, "payNum", req.get("out_trade_no"),
				"accountType", 2);
		// 交易号验证
		if (payRecords == null || payRecords.size() == 0) {
			result.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800003.getErrorCode().toString());
			result.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800003.getErrorMessage());
			return result;
		}
		ShopPayRecord payRecord = payRecords.get(0);
		// 金额验证
		if (payRecord.getAmount().compareTo(Double.valueOf(req.get("total_amount"))) != 0) {
			result.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800005.getErrorCode().toString());
			result.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800005.getErrorMessage());
			return result;
		}
		// 收款账户验证
		if (!AlipayProperties.ALIPAY_PARTNER.equals(req.get("seller_id"))) {
			result.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800006.getErrorCode().toString());
			result.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800006.getErrorMessage());
			return result;
		}
		// 验证app_id是否为该商户本身
		if (!AlipayProperties.ALIPAY_APP_ID.equals(req.get("app_id"))) {
			result.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800007.getErrorCode().toString());
			result.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800007.getErrorMessage());
			return result;
		}
		//支付记录已修改
		if (payRecord.getStatus()!=0) {
			result.setResult(true);
			result.setT(payRecord.getPayNum());
			return result;
		}

		// 构造要修改的支付记录
		payRecord.setPayAccount(req.get("buyer_logon_id"));
		payRecord.setStatus(req.get("trade_status").equals("TRADE_SUCCESS") ? 1 : 2);
		payRecord.setTradeNo(req.get("trade_no"));

		return payRecordApi.paySuccess(payRecord);
	}
}
