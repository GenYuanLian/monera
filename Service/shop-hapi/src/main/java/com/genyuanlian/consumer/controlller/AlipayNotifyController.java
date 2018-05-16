package com.genyuanlian.consumer.controlller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.genyuanlian.consumer.shop.api.IAlipayApi;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;

@Controller
@RequestMapping("/alipayNotify")
public class AlipayNotifyController {
	Logger logger = LoggerFactory.getLogger(AlipayNotifyController.class);
	
	@Resource
	private IAlipayApi alipayApi;

	@SuppressWarnings("unchecked")
	@RequestMapping("/notifyResult")
	@ResponseBody
	public String alipayNotify(HttpServletRequest request,HttpServletResponse response) {
		logger.info("==============支付宝支付结果异步回调成功==============");
		//验证支付宝通知
		Map<String,String> payResultParams=new HashMap<String,String>();
		Map<String,String> paramMap=request.getParameterMap();
		if(MapUtils.isNotEmpty(paramMap)){
			for(Map.Entry<String, String> entry:paramMap.entrySet()){    
				payResultParams.put(entry.getKey(),request.getParameter(entry.getKey()));    
			}   
		}
		logger.info("支付宝回调参数:"+JSONObject.toJSONString(payResultParams));
		
		try {
			ShopMessageVo<String> aysnNotify = alipayApi.alipayAysnNotify(payResultParams);
			if (aysnNotify.isResult()) {
				logger.info("支付宝回调处理成功");
				return "success";
			}else {
				logger.info("支付宝回调处理失败："+aysnNotify.getErrorMessage());
			}
		} catch (Exception e) {
			logger.error("支付宝回调处理出错："+e.getMessage());
		}

		
		return "failure";
	}
	
	
}
