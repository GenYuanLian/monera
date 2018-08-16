package com.genyuanlian.consumer.processor;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.genyuanlian.consumer.shop.model.ShopSystemPic;
import com.genyuanlian.consumer.shop.vo.LinkedPictureVo;
import com.hnair.consumer.dao.service.ICommonService;
import com.hnair.consumer.processor.BaseApiProcessor;
import com.hnair.consumer.utils.ResultSender;
import com.hnair.consumer.utils.system.ConfigPropertieUtils;

@Component("hapigetHomeBannerprocessor")
public class GetHomeBannerProcessor extends BaseApiProcessor {
	@Resource
	private ICommonService commonService;

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {

		String imageDomain = ConfigPropertieUtils.getString("image.server.address");

		List<ShopSystemPic> list = commonService.getList(ShopSystemPic.class, "picType", 1, "status", 1);
		List<LinkedPictureVo> banners = new ArrayList<LinkedPictureVo>();
		for (ShopSystemPic pic : list) {
			String url = imageDomain + pic.getUrl();
			LinkedPictureVo b = new LinkedPictureVo();
			b.setUrl(url);
			b.setId(pic.getOwnerId());
			b.setType(pic.getOwnerType());
			banners.add(b);
		}

		sender.put("images", banners);
		sender.success(response, "数据获取成功");
	}
}
