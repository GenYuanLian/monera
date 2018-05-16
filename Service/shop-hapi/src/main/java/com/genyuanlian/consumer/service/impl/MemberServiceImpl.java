package com.genyuanlian.consumer.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genyuanlian.consumer.service.IMemberService;
import com.genyuanlian.consumer.shop.model.ShopMember;
import com.hnair.consumer.dao.spi.ICommonDao;

@Service("memberService")
public class MemberServiceImpl implements IMemberService {

	private static Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);
	@Resource
	private ICommonDao commonDao;

	@Override
	@Transactional
	public void register(ShopMember member) {
		try {
			commonDao.save(member);
		} catch (Exception e) {
			logger.error("保存会员注册信息错误：" + e.getMessage());
		}
	}
}
