package com.genyuanlian.consumer.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genyuanlian.consumer.service.ISmsInfoService;
import com.genyuanlian.consumer.shop.model.ShopSmsInfo;
import com.hnair.consumer.dao.spi.ICommonDao;
import com.hnair.consumer.utils.DateUtil;

@Service("smsInfoService")
public class SmsInfoServiceImpl implements ISmsInfoService {
	private static Logger logger = LoggerFactory.getLogger(SmsInfoServiceImpl.class);
	@Resource
	private ICommonDao commonDao;

	@Override
	@Transactional
	public void save(ShopSmsInfo info) {
		try {
			commonDao.save(info);
		} catch (Exception e) {
			logger.error("保存短信发送记录失败：" + e.getMessage());
		}
	}

	/**
	 * 检验短信重复性 count 次数 time 时间
	 */
	@Override
	public boolean queryValiditytime(String mobile, int count, int time, String type) {
		// 格式化时间
		String createTime = DateUtil.getDateTime(DateUtil.addMinute(new Date(), -1));

		// 查询是否发送过短信
		List<ShopSmsInfo> list = commonDao.getListBySqlId(ShopSmsInfo.class, "selectByMobileList", "mobile", mobile, "type",
				type, "createTime", createTime);
		if (list != null && list.size() >= count) {
			return true;
		}

		return false;
	}
}
