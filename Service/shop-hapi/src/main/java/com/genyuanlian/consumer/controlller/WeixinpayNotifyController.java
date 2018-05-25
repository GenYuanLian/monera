package com.genyuanlian.consumer.controlller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.genyuanlian.consumer.shop.api.IWeixinpayApi;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.hnair.consumer.utils.XMLUtil;

@Controller
@RequestMapping("/weixinpayNotify")
public class WeixinpayNotifyController {
	private Logger logger=LoggerFactory.getLogger(WeixinpayNotifyController.class);
	
	@Resource
	private IWeixinpayApi weixinpayApi;
	
	public WeixinpayNotifyController() {
		logger.info("初始化WeixinpayNotifyController");
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/notifyResult")
	@ResponseBody
	public String weixinpayNotify(HttpServletRequest request,HttpServletResponse response) throws Exception {
		logger.info("==============微信支付结果异步回调成功==============");
		InputStream inStream = request.getInputStream();
		BufferedReader bis = new BufferedReader(new InputStreamReader(
				new BufferedInputStream(inStream), "utf-8"));
		String line = null;
		String result = "";
		try {
			while ((line = bis.readLine()) != null) {
				result += line + "\r\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bis.close();
		}
		logger.info("读取byte之后的result为： " + result.trim());
		if (StringUtils.isBlank(result)) {
			String noticeStr = weixinpayApi.setXML("FAILURE", "");
			return noticeStr;
		}
		
		Map<String, String> map = null;
		try {
			map = XMLUtil.doXMLParse(result.trim());
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		logger.info("当前的订单号为：" + map.get("out_trade_no") + ", 支付流水号： "
				+ map.get("transaction_id"));
		ShopMessageVo<String> messageVo=weixinpayApi.weixinpayAysnNotify(map);
		
		return messageVo.getT();
	}
}
