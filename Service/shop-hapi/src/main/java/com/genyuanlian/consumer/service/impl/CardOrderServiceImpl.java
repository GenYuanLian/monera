package com.genyuanlian.consumer.service.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genyuanlian.consumer.service.IBWSService;
import com.genyuanlian.consumer.service.ICardOrderService;
import com.genyuanlian.consumer.shop.enums.ShopErrorCodeEnum;
import com.genyuanlian.consumer.shop.model.ShopBstkWallet;
import com.genyuanlian.consumer.shop.model.ShopOrder;
import com.genyuanlian.consumer.shop.model.ShopOrderDetail;
import com.genyuanlian.consumer.shop.model.ShopPuCard;
import com.genyuanlian.consumer.shop.model.ShopPuCardTradeRecord;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.hnair.consumer.dao.spi.ICommonDao;
import com.hnair.consumer.utils.DateUtil;

@Service("cardOrderService")
public class CardOrderServiceImpl implements ICardOrderService {

	private Logger logger=LoggerFactory.getLogger(CardOrderServiceImpl.class);
	
	@Resource
	private ICommonDao commonDao;
	
	@Resource
	private IBWSService bwsService;

	@Override
	@Transactional
	public ShopMessageVo<String> modifyCardOrderStatus(String orderNo, Integer status) {
		ShopMessageVo<String> result = new ShopMessageVo<>();
		
		//查询订单明细
		List<ShopOrderDetail> orderDetails = commonDao.getList(ShopOrderDetail.class, "orderNo",orderNo);
		if (orderDetails==null || orderDetails.size()==0) {
			result.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200002.getErrorCode().toString());
			result.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200002.getErrorMessage());
			return result;
		}
		List<Long> cardIds = new ArrayList<>();
		
		for (ShopOrderDetail orderDetail : orderDetails) {
			cardIds.add(orderDetail.getCommodityId());
			orderDetail.setStatus(3);
			commonDao.update(orderDetail);
		}
		Long memberId=orderDetails.get(0).getMemberId();
		
		//BSTK交易Id
		String transactionNo=null;
		
		Date now=DateUtil.getCurrentDateTime();
		ShopOrderDetail orderDetail=orderDetails.get(0);
		if (orderDetail.getCommodityType()==1) {
			//查询相关联提货卡
			List<ShopPuCard> cards = commonDao.get(cardIds, ShopPuCard.class);
			if (cards==null || cards.size()==0) {
				result.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200003.getErrorCode().toString());
				result.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200003.getErrorMessage());
				return result;
			}
			
			Integer cardStatus=0;
			if (status==1) {
				//当订单状态未已支付时，卡状态改为已售，否则都改为未售
				cardStatus=2;
			}
			
			//调用bstk接口
			Double amount=cards.stream().mapToDouble(ShopPuCard:: getTotelValue).sum();
			ShopBstkWallet bstkWallet=commonDao.get(ShopBstkWallet.class, "ownerId",memberId,"ownerType",1);
			transactionNo = bwsService.walletRecharge(orderDetail.getOrderId(), memberId, 1, bstkWallet.getPublicKeyAddr(), amount);
			logger.info("调用bstk接口返回值："+transactionNo);
			
			//修改相关联提货卡信息,添加提货卡交易记录
			for (ShopPuCard card : cards) {
				card.setMemberId(memberId);
				card.setSaleTime(now);
				card.setStatus(cardStatus);
				commonDao.update(card);
				
				ShopPuCardTradeRecord tradeRecord=new ShopPuCardTradeRecord();
				tradeRecord.setAmount(card.getTotelValue());
				tradeRecord.setCreateTime(now);
				tradeRecord.setMemberId(memberId);
				tradeRecord.setPuCardId(card.getId());
				tradeRecord.setTitle(card.getTitle());
				tradeRecord.setTransactionNo(transactionNo);
				commonDao.save(tradeRecord);
			}
			
		}else {
			//查询相关联溯源卡
		}

		ShopOrder order=commonDao.get(ShopOrder.class, "orderNo",orderNo);
		order.setTransactionNo(transactionNo);
		commonDao.update(order);
		
		result.setResult(true);
		result.setT(orderNo);

		return result;
	}
	
}
