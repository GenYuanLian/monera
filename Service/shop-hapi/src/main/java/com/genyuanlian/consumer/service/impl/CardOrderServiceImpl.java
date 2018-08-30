package com.genyuanlian.consumer.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.genyuanlian.consumer.service.IBWSService;
import com.genyuanlian.consumer.service.IBaseOrderService;
import com.genyuanlian.consumer.service.ICardOrderService;
import com.genyuanlian.consumer.shop.enums.ShopErrorCodeEnum;
import com.genyuanlian.consumer.shop.model.ShopBstkWallet;
import com.genyuanlian.consumer.shop.model.ShopBstkWalletBill;
import com.genyuanlian.consumer.shop.model.ShopOrder;
import com.genyuanlian.consumer.shop.model.ShopOrderCommoditySnapshot;
import com.genyuanlian.consumer.shop.model.ShopOrderDetail;
import com.genyuanlian.consumer.shop.model.ShopPuCard;
import com.genyuanlian.consumer.shop.model.ShopPuCardTradeRecord;
import com.genyuanlian.consumer.shop.utils.BWSProperties;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.hnair.consumer.dao.spi.ICommonDao;
import com.hnair.consumer.utils.DateUtil;
import com.hnair.consumer.utils.SnoGerUtil;

@Service("cardOrderService")
public class CardOrderServiceImpl implements ICardOrderService {

	private Logger logger = LoggerFactory.getLogger(CardOrderServiceImpl.class);

	@Resource
	private ICommonDao commonDao;

	@Resource
	private IBWSService bwsService;

	@Resource
	private IBaseOrderService baseOrderService;

	@Override
	@Transactional
	public ShopMessageVo<String> modifyCardOrderStatus(String orderNo, Integer status) {
		ShopMessageVo<String> result = new ShopMessageVo<>();

		// 查询订单明细
		List<ShopOrderDetail> orderDetails = commonDao.getList(ShopOrderDetail.class, "orderNo", orderNo);
		if (orderDetails == null || orderDetails.size() == 0) {
			result.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200002.getErrorCode().toString());
			result.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200002.getErrorMessage());
			// 手动回滚当前事物
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return result;
		}

		Long memberId = orderDetails.get(0).getMemberId();
		ShopBstkWallet bstkWallet = commonDao.get(ShopBstkWallet.class, "ownerId", memberId, "ownerType", 1);
		if (bstkWallet == null) {
			result.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200004.getErrorCode().toString());
			result.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200004.getErrorMessage());
			// 收到设置当前事物回滚
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return result;
		}

		// BSTK交易Id
		String transNo = null;
		Double amount = new Double(0);

		Date now = DateUtil.getCurrentDateTime();

		// 修改订单明细
		for (ShopOrderDetail orderDetail : orderDetails) {
			baseOrderService.setOrderStatus(orderDetail, 6);
			orderDetail.setPayTime(DateUtil.getCurrentDateTime());
			commonDao.update(orderDetail);
		}

		ShopOrderDetail orderDetail = orderDetails.get(0);
		if (orderDetail.getCommodityType() == 1) {
			// 要取消的订单中提货卡订单详情id集合
			List<Long> puCardOrderDetailIds = orderDetails.stream().filter(i -> i.getCommodityType() == 1)
					.map(i -> i.getId()).distinct().collect(Collectors.toList());
			// 根据订单详情ids查询，订单产品快照集合
			List<ShopOrderCommoditySnapshot> list = commonDao.getList(ShopOrderCommoditySnapshot.class,
					"existOrderDetailIds", puCardOrderDetailIds);

			List<Long> cardIds = list.stream().map(i -> i.getCommodityId()).distinct().collect(Collectors.toList());
			// 查询相关联提货卡
			List<ShopPuCard> cards = commonDao.get(cardIds, ShopPuCard.class);
			if (cards == null || cards.size() == 0) {
				result.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200003.getErrorCode().toString());
				result.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200003.getErrorMessage());
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return result;
			}

			Integer cardStatus = 0;
			if (status == 1) {
				// 当订单状态未已支付时，卡状态改为已售，否则都改为未售
				cardStatus = 2;
			}

			// 调用bstk接口
			amount = cards.stream().mapToDouble(ShopPuCard::getTotelValue).sum();

			transNo = SnoGerUtil.getUUID();

			// 修改相关联提货卡信息,添加提货卡交易记录
			for (ShopPuCard card : cards) {
				card.setMemberId(memberId);
				card.setSaleTime(now);
				card.setStatus(cardStatus);
				commonDao.update(card);

				// 保存提货卡交易记录
				ShopPuCardTradeRecord tradeRecord = new ShopPuCardTradeRecord();
				tradeRecord.setAmount(card.getTotelValue());
				tradeRecord.setCreateTime(now);
				tradeRecord.setMemberId(memberId);
				tradeRecord.setPuCardId(card.getId());
				tradeRecord.setTitle(card.getTitle() + " 购买");
				tradeRecord.setTransactionNo(transNo);
				commonDao.save(tradeRecord);

				// 记录钱包流水记录
				ShopBstkWalletBill record = new ShopBstkWalletBill();
				record.setWalletId(bstkWallet.getId());
				record.setOwnerId(memberId);
				record.setOwnerType(1);
				// 流水类型：1-提货卡激活，2-提货卡购买，3-提货卡支付，4-注册邀请返利，5-商品分享返利，6-代理返利，7-余额支付，8-微信支付，9-支付宝支付，10-销售收入
				record.setBillType(2);
				record.setTitle(card.getTitle() + " 购买");
				record.setAmount(card.getTotelValue());
				record.setTransactionNo(transNo);
				record.setBusinessId(card.getId());
				record.setRemark("businessId为提货卡Id");
				record.setCreateTime(now);
				commonDao.save(record);
			}

		} else {
			// 查询相关联溯源卡
		}

		ShopOrder order = commonDao.get(ShopOrder.class, "orderNo", orderNo);
		order.setTransactionNo(transNo);
		commonDao.update(order);

		// 上传BSTK钱包交易，会员账户增加
		if (amount > 0) {
			String msg = bwsService.walletRecharge(BWSProperties.P_YUANDIAN, transNo, orderDetail.getOrderId(),
					memberId, 1, bstkWallet.getPublicKeyAddr(), BigDecimal.valueOf(amount));
			if (StringUtils.isBlank(msg)) {
				result.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200004.getErrorCode().toString());
				result.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200004.getErrorMessage());
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return result;
			}
		}

		result.setResult(true);
		result.setT(orderNo);

		return result;
	}

}
