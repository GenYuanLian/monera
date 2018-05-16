package com.genyuanlian.consumer.processor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.genyuanlian.consumer.service.IBWSService;
import com.genyuanlian.consumer.shop.model.ShopBstkWallet;
import com.genyuanlian.consumer.shop.vo.BWSWalletCreateResponseVo;
import com.hnair.consumer.dao.service.ICommonService;
import com.hnair.consumer.processor.BaseApiProcessor;
import com.hnair.consumer.utils.DateUtil;
import com.hnair.consumer.utils.ProUtility;
import com.hnair.consumer.utils.ResultSender;

@Component("hapitestCallprocessor")
public class TestCallProcessor extends BaseApiProcessor {
	@Resource
	private IBWSService bwsService;

	@Resource
	private ICommonService commonService;

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {
		Long ownerId = Long.parseLong("2");
		Integer ownerType = 2;
		ShopBstkWallet wallet = commonService.get(ShopBstkWallet.class, "ownerId", ownerId, "ownerType", ownerType);
		if (wallet == null) {
			BWSWalletCreateResponseVo resp = bwsService.walletCreate(ownerId, ownerType);
			if (resp != null && ProUtility.isNotNull(resp.getMainAddr())) {
				ShopBstkWallet upWallet = new ShopBstkWallet();
				upWallet.setOwnerId(ownerId);
				upWallet.setOwnerType(ownerType);
				upWallet.setMobile("");
				upWallet.setWalletAddress(resp.getWallet());
				upWallet.setPublicKeyAddr(resp.getMainAddr());
				upWallet.setCreateTime(DateUtil.getCurrentDateTime());
				commonService.save(upWallet);

				sender.put("result", resp);
				sender.success(response, "成功");
			} else {
				sender.fail(-1, "失败", response);
			}
		}
	}
}
