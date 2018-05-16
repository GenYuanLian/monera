package com.genyuanlian.consumer.processor;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.genyuanlian.consumer.shop.model.ShopPuCard;
import com.genyuanlian.consumer.shop.model.ShopPuCardTradeRecord;
import com.hnair.consumer.constant.ErrorCodeEnum;
import com.hnair.consumer.dao.service.ICommonService;
import com.hnair.consumer.processor.BaseApiProcessor;
import com.hnair.consumer.utils.LoginCheck;
import com.hnair.consumer.utils.ResultSender;


@LoginCheck
@Component("hapigetPuCardTradeRecordprocessor")
public class GetPuCardTradeRecordProcessor extends BaseApiProcessor {
	
	@Resource
	private ICommonService commonService;

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {
		String memberId=request.getParameter("memberId");
		String puCardId=request.getParameter("puCardId");
		
		if (!checkParams(puCardId)) {
			sender.fail(ErrorCodeEnum.ERROR_CODE_10002.getErrorCode(), ErrorCodeEnum.ERROR_CODE_10002.getErrorMessage(), response);
			return;
		}
		
		ShopPuCard card = commonService.get(Long.valueOf(puCardId), ShopPuCard.class);
		if (card==null) {
			sender.fail(ErrorCodeEnum.ERROR_CODE_10007.getErrorCode(), ErrorCodeEnum.ERROR_CODE_10007.getErrorMessage(), response);
			return;
		}
		if (Long.valueOf(memberId).compareTo(card.getMemberId())!=0) {
			sender.fail(ErrorCodeEnum.ERROR_CODE_10004.getErrorCode(), ErrorCodeEnum.ERROR_CODE_10004.getErrorMessage(), response);
			return;
		}
		
		List<ShopPuCardTradeRecord> list = commonService.getList(ShopPuCardTradeRecord.class, "puCardId",puCardId);
		
//		HtmlPage page=
		
		sender.put("puCard", card);
		sender.put("records", list);
		sender.success(response);
	}

}
