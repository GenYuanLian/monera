package com.genyuanlian.consumer.service.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genyuanlian.consumer.service.IPayRecordService;
import com.genyuanlian.consumer.shop.enums.ShopErrorCodeEnum;
import com.genyuanlian.consumer.shop.model.ShopOrder;
import com.genyuanlian.consumer.shop.model.ShopPayRecord;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.hnair.consumer.dao.spi.ICommonDao;
import com.hnair.consumer.utils.DateUtil;

@Service("payRecordService")
public class PayRecordServiceImpl implements IPayRecordService {

	private static Logger logger = LoggerFactory.getLogger(PayRecordServiceImpl.class);
	@Resource
	private ICommonDao commonDao;
	@Resource(name = "masterRedisTemplate")
	private RedisTemplate<String, String> masterRedisTemplate;

	@Override
	public ShopMessageVo<ShopPayRecord> buildPayRecord(Long memberId, Double totalAmount, String orderNo,
			String subject, Integer accountType) {
		ShopMessageVo<ShopPayRecord> result = new ShopMessageVo<>();
		// 判断是否重复提交
		String lockKey = "pay_order_num" + orderNo;
		String now=DateUtil.getCurrentDateTimeToStr2();
		// 当且仅当 key 不存在，将 key 的值设为 value ，并返回1；若给定的 key 已经存在，则 SETNX 不做任何动作，并返回0
		if (!masterRedisTemplate.getConnectionFactory().getConnection().setNX(lockKey.getBytes(), now.getBytes())) {
			byte[] bs = masterRedisTemplate.getConnectionFactory().getConnection().get(lockKey.getBytes());
			logger.info("存在时间-"+new String(bs));
			logger.info("当前时间-"+now);
			result.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800001.getErrorCode().toString());
			result.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800001.getErrorMessage());
			return result;
		}
		// 同一订单3秒内连续支付多次，则视为重复提交
		masterRedisTemplate.getConnectionFactory().getConnection().expire(lockKey.getBytes(), 3);

		List<ShopPayRecord> records = commonDao.getList(ShopPayRecord.class, "orderNo", orderNo);

		String payNum = orderNo + DateUtil.formatDateByFormat(new Date(), "HHmmss");
		ShopPayRecord payRecord = new ShopPayRecord();
		if (records != null && records.size() > 0) {
			for (ShopPayRecord r : records) {
				if (r.getStatus() == 1) {
					// 判断订单是否已支付成功
					result.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_800001.getErrorCode().toString());
					result.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_800001.getErrorMessage());
					return result;
				}
				if (r.getStatus() == 0) {
					payRecord = r;
					payRecord.setAccountType(accountType);
					payRecord.setAmount(totalAmount);
					payRecord.setPayNum(payNum);

					result.setResult(true);
					result.setT(payRecord);

					return result;
				}
			}
		}

		payRecord.setAccountType(accountType);
		payRecord.setAmount(totalAmount.doubleValue());
		payRecord.setMemberId(memberId);
		payRecord.setOrderNo(orderNo);
		payRecord.setPayNum(payNum);
		payRecord.setRemark(subject);
		payRecord.setStatus(0);

		result.setResult(true);
		result.setT(payRecord);

		return result;
	}

	@Transactional
	public void save(ShopPayRecord payRecord) {
		try {
			if (payRecord.getId() == null) {
				payRecord.setCreateTime(DateUtil.getCurrentDateTime());
				commonDao.save(payRecord);
			} else {
				commonDao.update(payRecord);
			}

			// 保存支付方式
			ShopOrder order = commonDao.get(ShopOrder.class, "orderNo", payRecord.getOrderNo());
			order.setPayType(payRecord.getAccountType());
			commonDao.update(order);

		} catch (Exception e) {
			logger.error("保持支付记录错误：" + e.getMessage());
		}
	}
}
