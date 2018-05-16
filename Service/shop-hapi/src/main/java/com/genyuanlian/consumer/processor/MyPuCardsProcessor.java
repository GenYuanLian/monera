package com.genyuanlian.consumer.processor;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.genyuanlian.consumer.shop.api.IPuCardApi;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.hnair.consumer.processor.BaseApiProcessor;
import com.hnair.consumer.utils.LoginCheck;
import com.hnair.consumer.utils.ResultSender;

@LoginCheck
@Component("hapimyPuCardsprocessor")
public class MyPuCardsProcessor extends BaseApiProcessor {
	
	@Resource
	private IPuCardApi puCardApi;

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {
		
		String memberId=request.getParameter("memberId");
		
		String pageIndex=request.getParameter("pageIndex");
		
		String pageSize=request.getParameter("pageSize");
		
		String isValid=request.getParameter("isValid");
		
		ShopMessageVo<Map<String, Object>>messageVo = puCardApi.getPagePuCards(Long.valueOf(memberId), Integer.valueOf(isValid), Integer.valueOf(pageIndex), Integer.valueOf(pageSize));
		if (!messageVo.isResult()) {
			sender.fail(Integer.valueOf(messageVo.getErrorCode()), messageVo.getErrorMessage(), response);
			return;
		}
		
		sender.put("list", messageVo.getT().get("puCards"));
		sender.put("hasNext", messageVo.getT().get("hasNext"));
		sender.put("cardCount", messageVo.getT().get("cardCount"));
		sender.put("sumBalance", messageVo.getT().get("sumBalance"));
		sender.success(response);
		
	}

}
