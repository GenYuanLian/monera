package com.genyuanlian.consumer.processor;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.genyuanlian.consumer.shop.model.ShopMerchantPic;
import com.hnair.consumer.processor.BaseApiProcessor;
import com.hnair.consumer.utils.ResultSender;
import com.hnair.consumer.utils.system.ConfigPropertieUtils;

@Component("hapigetHomeBannerprocessor")
public class GetHomeBannerProcessor extends BaseApiProcessor {
	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {

		String imageDomain = ConfigPropertieUtils.getString("image.server.address");
		List<String> banners = new ArrayList<String>();
		banners.add(imageDomain + "/Home1.png");
		banners.add(imageDomain + "/Home2.jpg");
		banners.add(imageDomain + "/Home3.jpg");

		sender.put("images", banners);
		sender.success(response, "数据获取成功");
	}
}
