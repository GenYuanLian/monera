package com.genyuanlian.consumer.processor;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.genyuanlian.consumer.shop.api.IBaseAreaApi;
import com.genyuanlian.consumer.shop.vo.AreaVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.hnair.consumer.processor.BaseApiProcessor;
import com.hnair.consumer.utils.ResultSender;

@Component("hapigetAreasprocessor")
public class GetAreasProcessor extends BaseApiProcessor {
	@Resource
	private IBaseAreaApi baseAreaApi;

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {

		ShopMessageVo<List<AreaVo>> messageVo = baseAreaApi.getAreas();
		if (messageVo.isResult()) {
			sender.put("list", messageVo.getT());
			sender.success(response, messageVo.getMessage());
		} else {
			sender.fail(Integer.parseInt(messageVo.getErrorCode()), messageVo.getErrorMessage(), response);
		}
	}
}