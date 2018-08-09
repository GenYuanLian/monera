package com.genyuanlian.consumer.api.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import com.genyuanlian.consumer.service.IBWSService;
import com.genyuanlian.consumer.service.IBaseAreaService;
import com.genyuanlian.consumer.service.IMemberService;
import com.genyuanlian.consumer.shop.api.IMemberApi;
import com.genyuanlian.consumer.shop.enums.ShopErrorCodeEnum;
import com.genyuanlian.consumer.shop.model.ShopBaseArea;
import com.genyuanlian.consumer.shop.model.ShopBstkWallet;
import com.genyuanlian.consumer.shop.model.ShopLoginLog;
import com.genyuanlian.consumer.shop.model.ShopMember;
import com.genyuanlian.consumer.shop.model.ShopMemberAddress;
import com.genyuanlian.consumer.shop.model.ShopMemberSecurity;
import com.genyuanlian.consumer.shop.model.ShopPuCard;
import com.genyuanlian.consumer.shop.model.ShopWallet;
import com.genyuanlian.consumer.shop.model.ShopWalletBill;
import com.genyuanlian.consumer.shop.vo.BWSWalletCreateResponseVo;
import com.genyuanlian.consumer.shop.vo.IdParamsVo;
import com.genyuanlian.consumer.shop.vo.LoginNameParamsVo;
import com.genyuanlian.consumer.shop.vo.MemberAddressParamsVo;
import com.genyuanlian.consumer.shop.vo.MemberFindPwdParamsVo;
import com.genyuanlian.consumer.shop.vo.MemberIdBusinessIdParamsVo;
import com.genyuanlian.consumer.shop.vo.MemberIdPagedParamsVo;
import com.genyuanlian.consumer.shop.vo.MemberIdParamsVo;
import com.genyuanlian.consumer.shop.vo.MemberInvitationVo;
import com.genyuanlian.consumer.shop.vo.MemberLoginParamsVo;
import com.genyuanlian.consumer.shop.vo.MemberRegisterParamsVo;
import com.genyuanlian.consumer.shop.vo.NicknameParamsVo;
import com.genyuanlian.consumer.shop.vo.SendWalletBstkParamsVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.genyuanlian.consumer.shop.vo.UploadHeadPortraitParamsVo;
import com.genyuanlian.consumer.shop.vo.WalletBillVo;
import com.hnair.consumer.dao.service.ICommonService;
import com.hnair.consumer.utils.DateUtil;
import com.hnair.consumer.utils.FileResponse;
import com.hnair.consumer.utils.ProUtility;
import com.hnair.consumer.utils.SftpUtil;
import com.hnair.consumer.utils.SnoGerUtil;
import com.hnair.consumer.utils.system.ConfigPropertieUtils;

@Component("memberApi")
public class MemberApiImpl implements IMemberApi {

	private static Logger logger = LoggerFactory.getLogger(MemberApiImpl.class);

	@Resource
	private ICommonService commonService;

	@Resource
	private IMemberService memberService;

	@Resource
	private IBaseAreaService baseAreaService;

	@Resource
	private IBWSService bwsService;

	@SuppressWarnings("rawtypes")
	@Resource(name = "masterRedisTemplate")
	private RedisTemplate masterRedisTemplate;

	@SuppressWarnings("rawtypes")
	@Resource(name = "slaveRedisTemplate")
	private RedisTemplate slaveRedisTemplate;

	@Override
	@Transactional
	public ShopMessageVo<Map<String, Object>> register(MemberRegisterParamsVo params) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("新会员注册调用到这里了=================,手机号:" + params.getMobile() + ",是否第三方登录注册" + params.getIsThirdparty()
				+ ",验证码:" + params.getSmsCode() + "短信编码:" + params.getSmsNumber() + "推荐码:" + params.getReferraCode());

		if (params.getIsThirdparty()) {
			logger.info("第三方授权登录注册，不验证验证码");
		} else {
			// 判断短信验证码是否正确
			String cacheKeySmsNumber = params.getSmsNumber() + params.getMobile();
			Object smsNumber = masterRedisTemplate.opsForValue().get(cacheKeySmsNumber);
			if (smsNumber != null && ProUtility.isNotNull(smsNumber.toString())
					&& smsNumber.toString().equals(params.getSmsCode())) {
				logger.info("短信验证码验证成功=================,手机号:" + params.getMobile() + "验证码:" + params.getSmsCode()
						+ "短信编码:" + params.getSmsNumber());
			} else {
				messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100006.getErrorCode().toString());
				messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100006.getErrorMessage());
				// 手动回滚当前事物
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return messageVo;
			}
		}

		// 判断手机号是否被占用
		if (StringUtils.isNotBlank(params.getMobile())) {
			List<ShopMember> members = commonService.getList(ShopMember.class, "mobile", params.getMobile());
			if (members != null && members.size() > 0) {
				messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100001.getErrorCode().toString());
				messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100001.getErrorMessage());
				// 手动回滚当前事物
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return messageVo;
			}
		}

		// 如果有推荐码参数，验证参数是否正确
		if (ProUtility.isNotNull(params.getReferraCode())) {
			List<ShopMember> referras = commonService.getList(ShopMember.class, "invitationCode",
					params.getReferraCode());
			if (referras == null || referras.size() == 0) {
				messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100003.getErrorCode().toString());
				messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100003.getErrorMessage());
				// 手动回滚当前事物
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return messageVo;
			}

			params.setReferraId(referras.get(0).getId());
		}

		// 持久化
		ShopMember register = registerPersist(params);

		// 返回值
		resultMap.put("memberId", register.getId());
		resultMap.put("mobile", register.getMobile());
		resultMap.put("invitationCode", register.getInvitationCode());
		resultMap.put("member", register);

		messageVo.setResult(true);
		messageVo.setT(resultMap);
		messageVo.setMessage("会员注册成功");

		return messageVo;
	}

	@Override
	public ShopMember registerPersist(MemberRegisterParamsVo params) {
		ShopMember register = new ShopMember();
		register.setMobile(params.getMobile());
		register.setLoginPwd(params.getPwd());
		register.setReferraCode(params.getReferraCode());
		register.setReferraId(params.getReferraId());
		register.setChannel(params.getChannel());
		register.setChannelVersion(params.getChannelVersion());
		register.setSignIp(params.getIp());

		// 默认值
		register.setOwnerType(1);
		register.setLoginName(params.getMobile());
		register.setNickName(StringUtils.isNotBlank(params.getNickName()) ? params.getNickName() : "主人");
		register.setGender(params.getGender());
		register.setHeadPortrait(params.getHeadPortrait());
		register.setIsIdentification(0);
		register.setInvitationCode(getRandomString());
		register.setStatus(1);
		register.setCreateTime(DateUtil.getCurrentDateTime());

		// 持久化
		memberService.register(register);

		// 创建轻钱包（对应源点）
		String transNo = SnoGerUtil.getUUID();
		BWSWalletCreateResponseVo resp = bwsService.walletCreate(transNo, register.getId(), 1);
		if (resp != null) {
			// 插入 wallet
			ShopBstkWallet upWallet = new ShopBstkWallet();
			upWallet.setOwnerId(register.getId());
			upWallet.setOwnerType(1);
			upWallet.setMobile(register.getMobile());
			upWallet.setWalletAddress(resp.getWallet());
			upWallet.setPublicKeyAddr(resp.getMainAddr());
			upWallet.setCreateTime(DateUtil.getCurrentDateTime());
			commonService.save(upWallet);
		}

		// 创建轻钱包（对应BSTK）
		String transNo1 = SnoGerUtil.getUUID();
		BWSWalletCreateResponseVo resp1 = bwsService.walletCreate(transNo1, register.getId(), 1);
		if (resp1 != null) {
			// 插入 wallet
			ShopWallet wallet = new ShopWallet();
			wallet.setOwnerId(register.getId());
			wallet.setOwnerType(1);
			wallet.setWalletAddress(resp1.getWallet());
			wallet.setPublicKeyAddr(resp1.getMainAddr());
			wallet.setTotalIncome(0d);
			wallet.setTotalExpend(0d);
			wallet.setBalance(0d);
			wallet.setCreateTime(DateUtil.getCurrentDateTime());
			commonService.save(wallet);
		}

		return register;
	}

	/**
	 * 方法名称: getRandomString ； 方法描述: 校验邀请码是否重复 返回类型: String ； 创建人：Eirc.Fan ；
	 * 创建时间：2017年12月21日 下午5:36:36； @throws
	 */
	private String getRandomString() {
		String uuid = null;
		// 判断邀请码是否重复
		for (int i = 0; i < 100; i++) {
			uuid = SnoGerUtil.getRandomString(6);
			List<ShopMember> members = commonService.getList(ShopMember.class, "invitationCode", uuid);
			if (null == members || members.size() == 0) {
				break;
			}
		}
		return uuid;
	}

	@Override
	@Transactional
	public ShopMessageVo<Map<String, Object>> login(MemberLoginParamsVo params) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("会员登录调用到这里了=================,登录账号:" + params.getLoginName() + "登录方式:" + params.getLoginType()
				+ "密码或者短信编码:" + params.getLoginCode() + "短信验证码:" + params.getSmsCode());

		// 缓存登陆失败的次数
		String cacheKeyLoginCount = "login" + params.getLoginName() + "key";

		// 登陆错误的次数
		Object logCount = masterRedisTemplate.opsForValue().get(cacheKeyLoginCount);
		int maxCount = Integer.parseInt(ConfigPropertieUtils.getString("member.login.count"));
		if (logCount != null && ProUtility.isNotNull(logCount.toString())
				&& Integer.parseInt(logCount.toString()) >= maxCount) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100007.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100007.getErrorMessage());
			// 手动回滚当前事物
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return messageVo;
		}

		// 根据登陆名称，密码查询用户信息
		ShopMember member = commonService.getBySqlId(ShopMember.class, "selectByLogin", "loginCode",
				params.getLoginName());

		// 判断用户名密码是否正确
		if (member != null) {
			if (member.getStatus() != 1) {
				messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100012.getErrorCode().toString());
				messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100012.getErrorMessage());
				// 手动回滚当前事物
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return messageVo;
			}

			// 判断密码是否正确
			if (params.getLoginType().equals("password")) {
				if (params.getLoginCode().equals(member.getLoginPwd())) {
					// 登录
					resultMap = login(member, 1, params.getIp());

					messageVo.setResult(true);
					messageVo.setT(resultMap);
					messageVo.setMessage("会员登录成功");
					return messageVo;
				} else {
					// 登陆错误的次数
					int count = 1;
					if (logCount != null && ProUtility.isNotNull(logCount.toString())) {
						count = Integer.valueOf(logCount.toString()) + 1;
					}

					// Redis存储-错误次数
					masterRedisTemplate.delete(cacheKeyLoginCount);
					masterRedisTemplate.opsForValue().set(cacheKeyLoginCount, count);
					masterRedisTemplate.expire(cacheKeyLoginCount, 10, TimeUnit.MINUTES);

					messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100008.getErrorCode().toString());
					messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100008.getErrorMessage());
					// 手动回滚当前事物
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					return messageVo;
				}
			} else { // 短信验证码登录
				String cacheKeySmsNumber = params.getLoginCode() + member.getMobile();
				Object smsNumber = masterRedisTemplate.opsForValue().get(cacheKeySmsNumber);
				if (smsNumber != null && ProUtility.isNotNull(smsNumber.toString())
						&& smsNumber.toString().equals(params.getSmsCode())) {
					logger.info("短信验证登录成功=================,手机号:" + member.getMobile() + "验证码:" + params.getSmsCode()
							+ "短信编码:" + params.getLoginCode());

					// 登录
					resultMap = login(member, 2, params.getIp());

					messageVo.setResult(true);
					messageVo.setT(resultMap);
					messageVo.setMessage("会员登录成功");
					return messageVo;
				} else {
					// 登陆错误的次数
					int count = 1;
					if (logCount != null && ProUtility.isNotNull(logCount.toString())) {
						count = Integer.valueOf(logCount.toString()) + 1;
					}

					// Redis存储-错误次数
					masterRedisTemplate.delete(cacheKeyLoginCount);
					masterRedisTemplate.opsForValue().set(cacheKeyLoginCount, count);
					masterRedisTemplate.expire(cacheKeyLoginCount, 10, TimeUnit.MINUTES);

					messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100008.getErrorCode().toString());
					messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100008.getErrorMessage());
					// 手动回滚当前事物
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					return messageVo;
				}
			}
		} else {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100008.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100008.getErrorMessage());
			// 手动回滚当前事物
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return messageVo;
		}
	}

	@Override
	public ShopMessageVo<Map<String, Object>> loginOut(MemberIdParamsVo params) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("会员退出登录调用到这里了=================,会员Id:" + params.getMemberId());

		String cacheKeyLoginToken = "login" + params.getMemberId().toString() + "token";
		masterRedisTemplate.delete(cacheKeyLoginToken);

		messageVo.setResult(true);
		messageVo.setT(resultMap);
		messageVo.setMessage("退出成功");
		return messageVo;
	}

	@Override
	@Transactional
	public ShopMessageVo<Map<String, Object>> getMemberInfo(MemberIdParamsVo params) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("获取会员基本信息调用到这里了=================,会员Id:" + params.getMemberId());

		// 查询用户信息
		ShopMember member = commonService.get(params.getMemberId(), ShopMember.class);
		if (member == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorMessage());
			return messageVo;
		}

		// 用户安全
		ShopMemberSecurity security = commonService.get(ShopMemberSecurity.class, "memberId", params.getMemberId());

		resultMap.put("memberId", member.getId());
		resultMap.put("mobile", member.getMobile());
		resultMap.put("nickName", member.getNickName());
		if (ProUtility.isNotNull(member.getHeadPortrait())) {
			String imageDomain = ConfigPropertieUtils.getString("image.server.address");
			resultMap.put("headPortrait", imageDomain + member.getHeadPortrait());
		}
		resultMap.put("gender", member.getGender()); // 性别：1-女，2-男
		resultMap.put("referraCode", member.getReferraCode());
		resultMap.put("invitationCode", member.getInvitationCode());
		resultMap.put("status", member.getStatus());
		resultMap.put("userName", member.getLoginName());
		resultMap.put("hasPwd", ProUtility.isNotNull(member.getLoginPwd()) ? true : false);
		if (security != null && ProUtility.isNotNull(security.getPayPwd())) {
			resultMap.put("hasPayPwd", true);
		} else {
			resultMap.put("hasPayPwd", false);
		}

		// 提货卡统计信息
		List<Integer> existStatus = new ArrayList<>();
		existStatus.add(2);
		existStatus.add(3);
		existStatus.add(4);
		existStatus.add(5);
		existStatus.add(6);
		existStatus.add(7);
		Map<String, Object> statistics = commonService.getBySqlId(ShopPuCard.class, "getStatistics", "memberId",
				params.getMemberId(), "existStatus", existStatus);

		resultMap.put("cardCount", statistics.get("count") == null ? 0 : statistics.get("count"));
		resultMap.put("sumBalance", statistics.get("sumBalance") == null ? 0 : statistics.get("sumBalance"));

		// 用户bstk钱包
		ShopWallet wallet = commonService.get(ShopWallet.class, "ownerId", params.getMemberId(), "ownerType", 1);
		if (wallet != null) {
			BigDecimal walletBalance = bwsService.walletBalance(SnoGerUtil.getUUID(), wallet.getWalletAddress());
			resultMap.put("bstkBalance", walletBalance);
		} else {
			// 创建轻钱包（对应BSTK）
			BWSWalletCreateResponseVo resp1 = bwsService.walletCreate(SnoGerUtil.getUUID(), params.getMemberId(), 1);
			if (resp1 != null) {
				// 插入 wallet
				wallet = new ShopWallet();
				wallet.setOwnerId(params.getMemberId());
				wallet.setOwnerType(1);
				wallet.setWalletAddress(resp1.getWallet());
				wallet.setPublicKeyAddr(resp1.getMainAddr());
				wallet.setTotalIncome(0d);
				wallet.setTotalExpend(0d);
				wallet.setBalance(0d);
				wallet.setCreateTime(DateUtil.getCurrentDateTime());
				commonService.save(wallet);
			}
			resultMap.put("bstkBalance", 0);
		}

		messageVo.setResult(true);
		messageVo.setT(resultMap);
		messageVo.setMessage("数据获取成功");
		return messageVo;
	}

	@Override
	public ShopMessageVo<Map<String, Object>> getInvitationCode(MemberIdParamsVo params) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("获取会员邀请码调用到这里了=================,会员Id:" + params.getMemberId());

		// 查询用户信息
		ShopMember member = commonService.get(params.getMemberId(), ShopMember.class);
		if (member == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorMessage());
			return messageVo;
		}

		resultMap.put("invitationCode", member.getInvitationCode());

		messageVo.setResult(true);
		messageVo.setT(resultMap);
		messageVo.setMessage("数据获取成功");
		return messageVo;
	}

	@Override
	public ShopMessageVo<Map<String, Object>> getMemberInvitRegister(MemberIdPagedParamsVo params) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<Map<String, Object>>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("获取我的邀请会员注册列表分页数据调用到这里了=================");

		// 查询用户信息
		ShopMember member = commonService.get(params.getMemberId(), ShopMember.class);
		if (member == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorMessage());
			return messageVo;
		}

		if (params.getPageIndex() == null) {
			params.setPageIndex(0);
		}

		if (params.getPageSize() == null) {
			params.setPageSize(20);
		}

		List<ShopMember> list = commonService.getListBySqlId(ShopMember.class, "pageData", "referraId",
				params.getMemberId(), "pageIndex", params.getPageIndex() * params.getPageSize(), "pageSize",
				params.getPageSize() + 1);

		Integer totalCount = commonService.getBySqlId(ShopMember.class, "pageCount", "referraId", params.getMemberId());
		resultMap.put("totalCount", totalCount);

		List<MemberInvitationVo> vos = new ArrayList<MemberInvitationVo>();
		if (list == null || list.size() == 0) {
			resultMap.put("hasNext", 0);
		} else {
			int listCount = list.size();
			if (listCount > params.getPageSize()) {
				resultMap.put("hasNext", 1);
				list.remove(listCount - 1);
			} else {
				resultMap.put("hasNext", 0);
			}

			String imageDomain = ConfigPropertieUtils.getString("image.server.address");
			for (ShopMember mem : list) {
				MemberInvitationVo vo = new MemberInvitationVo();
				vo.setId(mem.getId());
				vo.setMobile(mem.getMobile());
				vo.setCreateTime(mem.getCreateTime());
				if (ProUtility.isNotNull(mem.getHeadPortrait())) {
					vo.setHeadPortrait(imageDomain + mem.getHeadPortrait());
				}

				if (ProUtility.isNotNull(mem.getNickName())) {
					vo.setName(mem.getNickName());
				} else {
					vo.setName(mem.getLoginName());
				}

				vos.add(vo);
			}
		}

		resultMap.put("list", vos);
		messageVo.setT(resultMap);
		messageVo.setMessage("数据获取成功");
		messageVo.setResult(true);

		return messageVo;
	}

	@Override
	public ShopMessageVo<Map<String, Object>> findPwd(MemberFindPwdParamsVo params) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("会员找回密码调用到这里了=================,手机号:" + params.getMobile() + "验证码:" + params.getSmsCode() + "短信编码:"
				+ params.getSmsNumber());

		// 判断短信验证码是否正确
		String cacheKeySmsNumber = params.getSmsNumber() + params.getMobile();
		Object smsNumber = masterRedisTemplate.opsForValue().get(cacheKeySmsNumber);
		if (smsNumber != null && ProUtility.isNotNull(smsNumber.toString())
				&& smsNumber.toString().equals(params.getSmsCode())) {
			logger.info("短信验证码验证成功=================,手机号:" + params.getMobile() + "验证码:" + params.getSmsCode() + "短信编码:"
					+ params.getSmsNumber());
		} else {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100006.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100006.getErrorMessage());
			return messageVo;
		}

		// 根据登陆名称，密码查询用户信息
		ShopMember member = commonService.get(ShopMember.class, "mobile", params.getMobile());
		if (member != null) {
			member.setLoginPwd(params.getPwd());
			member.setPwdUptime(new Date());
			member.setOldPwd(member.getLoginPwd());
			commonService.update(member);

			messageVo.setResult(true);
			messageVo.setT(resultMap);
			messageVo.setMessage("找回密码成功");
			return messageVo;
		} else {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorMessage());
			return messageVo;
		}
	}

	@Override
	public ShopMessageVo<Map<String, Object>> imageUpload(MultipartFile file) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("图片上传调用到这里了=================");

		if (!file.isEmpty()) {
			try {
				// 图片上传
				FileResponse ponse = SftpUtil.imgFileUpload(file.getInputStream());
				resultMap.put("name", ponse.getFileName());
				resultMap.put("size", ponse.getFileSize());
				resultMap.put("path", ponse.getFileUrl());
				resultMap.put("url", ponse.getFileUrls());

				messageVo.setResult(true);
				messageVo.setT(resultMap);
				messageVo.setMessage("图片上传成功");
				return messageVo;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("图片上传异常,e：{2}", e.getMessage());
				messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100011.getErrorCode().toString());
				messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100011.getErrorMessage());
				return messageVo;
			}
		} else {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100010.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100010.getErrorMessage());
			return messageVo;
		}
	}

	@Override
	@Transactional
	public ShopMessageVo<Map<String, Object>> uploadHeadPortrait(UploadHeadPortraitParamsVo params) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("会员上传头像调用到这里了=================");

		// 查询用户信息
		ShopMember member = commonService.get(params.getMemberId(), ShopMember.class);
		if (member != null) {
			member.setHeadPortrait(params.getImageUrl());
			commonService.update(member);

			messageVo.setResult(true);
			messageVo.setT(resultMap);
			messageVo.setMessage("头像上传成功");
			return messageVo;
		} else {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorMessage());
			// 手动回滚当前事物
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return messageVo;
		}
	}

	@Override
	@Transactional
	public ShopMessageVo<Map<String, Object>> setLoginName(LoginNameParamsVo params) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<Map<String, Object>>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("会员上传头像调用到这里了=================");

		// 查询用户信息
		ShopMember member = commonService.get(params.getMemberId(), ShopMember.class);
		if (member != null) {
			member.setLoginName(params.getLoginName());
			commonService.update(member);

			messageVo.setResult(true);
			messageVo.setT(resultMap);
			messageVo.setMessage("用户名设置成功");
			return messageVo;
		} else {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorMessage());
			// 手动回滚当前事物
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return messageVo;
		}
	}

	@Override
	@Transactional
	public ShopMessageVo<Map<String, Object>> setNickname(NicknameParamsVo params) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<Map<String, Object>>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("会员设置昵称调用到这里了=================");

		// 查询用户信息
		ShopMember member = commonService.get(params.getMemberId(), ShopMember.class);
		if (member != null) {
			member.setNickName(params.getNickName());
			commonService.update(member);

			messageVo.setResult(true);
			messageVo.setT(resultMap);
			messageVo.setMessage("昵称设置成功");
			return messageVo;
		} else {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorMessage());
			// 手动回滚当前事物
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return messageVo;
		}
	}

	@Override
	public ShopMessageVo<List<ShopMemberAddress>> getMemberAddressList(MemberIdParamsVo params) {
		ShopMessageVo<List<ShopMemberAddress>> messageVo = new ShopMessageVo<List<ShopMemberAddress>>();
		logger.info("查询会员有效的收货地址列表调用到这里了=================");

		List<ShopMemberAddress> list = commonService.getList(ShopMemberAddress.class, "memberId", params.getMemberId(),
				"status", 1);
		if (list != null) {
			for (ShopMemberAddress a : list) {
				if (ProUtility.isNotNull(a.getMobile())) {
					int len = a.getMobile().length();
					String str = a.getMobile().substring(0, 3) + "****" + a.getMobile().substring(len - 4);
					a.setMobile(str);
				}
				if (ProUtility.isNotNull(a.getTel())) {
					int len = a.getTel().length();
					String str = a.getTel().substring(0, 3) + "****" + a.getTel().substring(len - 4);
					a.setTel(str);
				}
			}

			messageVo.setResult(true);
			messageVo.setT(list);
			messageVo.setMessage("数据获取成功");
			return messageVo;
		} else {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorMessage());
			return messageVo;
		}
	}

	@Override
	public ShopMessageVo<Map<String, Object>> getMemberAddress(IdParamsVo params) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<Map<String, Object>>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("获取会员收货地址详情调用到这里了=================");

		// 查询用户收货地址信息
		ShopMemberAddress address = commonService.get(params.getId(), ShopMemberAddress.class);
		if (address != null) {
			resultMap.put("addr", address);

			// 获取收货地址的所属区域列表
			ShopBaseArea area = commonService.get(address.getAreaId(), ShopBaseArea.class);
			if (area != null) {
				List<Integer> areaCodeList = new ArrayList<Integer>();
				String position = area.getPosition();
				if (ProUtility.isNotNull(position)) {
					String[] arr = position.split("tr_");
					for (int i = 0; i < arr.length; i++) {
						if (ProUtility.isNotNull(arr[i])) {
							if (!arr[i].trim().equals("0")) {
								areaCodeList.add(Integer.parseInt(arr[i].trim()));
							}
						}
					}
				}
				areaCodeList.add(area.getAreaCode());
				List<ShopBaseArea> areas = baseAreaService.selectByAreaCodeList(areaCodeList);
				resultMap.put("areas", areas);
			}

			messageVo.setResult(true);
			messageVo.setT(resultMap);
			messageVo.setMessage("数据获取成功");
			return messageVo;
		} else {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorMessage());
			return messageVo;
		}
	}

	@Override
	@Transactional
	public ShopMessageVo<Map<String, Object>> saveMemberAddress(MemberAddressParamsVo params) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<Map<String, Object>>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("保存会员收货地址调用到这里了=================");

		ShopBaseArea area = commonService.get(ShopBaseArea.class, "areaCode", params.getAreaCode());
		if (area == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorMessage());
			// 手动回滚当前事物
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return messageVo;
		}

		// 编辑
		if (params.getId() != null && params.getId() > 0) {
			ShopMemberAddress address = commonService.get(params.getId(), ShopMemberAddress.class);
			if (address != null) {
				address.setReceiver(params.getReceiver());
				address.setGender(params.getGender());
				address.setAreaId(area.getId());
				address.setAreaName(params.getAreaName());
				address.setAddress(params.getAddress());
				address.setMobile(params.getMobile());
				address.setTel(params.getTel());
				address.setEmail(params.getEmail());
				address.setAddressAlias(params.getAddressAlias());
				address.setRemark(params.getRemark());

				// 获取收货地址的所属区域列表
				List<Integer> areaCodeList = new ArrayList<Integer>();
				String position = area.getPosition();
				if (ProUtility.isNotNull(position)) {
					String[] arr = position.split("tr_");
					for (int i = 0; i < arr.length; i++) {
						if (ProUtility.isNotNull(arr[i])) {
							if (!arr[i].trim().equals("0")) {
								areaCodeList.add(Integer.parseInt(arr[i].trim()));
							}
						}
					}
				}
				areaCodeList.add(area.getAreaCode());
				List<ShopBaseArea> areas = baseAreaService.selectByAreaCodeList(areaCodeList);
				String areaName = "";
				for (ShopBaseArea a : areas) {
					areaName = areaName + "-" + a.getAreaName();
				}

				if (areaName.startsWith("-")) {
					areaName = areaName.substring(1);
				}

				address.setAreaName(areaName);

				// 更新
				commonService.update(address);

				messageVo.setResult(true);
				messageVo.setT(resultMap);
				messageVo.setMessage("收货地址保存成功");
				return messageVo;
			} else {
				messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorCode().toString());
				messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorMessage());
				// 手动回滚当前事物
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return messageVo;
			}
		} else // 新增
		{
			ShopMemberAddress address = new ShopMemberAddress();
			address.setMemberId(params.getMemberId());
			address.setReceiver(params.getReceiver());
			address.setGender(params.getGender());
			address.setAreaId(area.getId());
			address.setAreaName(params.getAreaName());
			address.setAddress(params.getAddress());
			address.setMobile(params.getMobile());
			address.setTel(params.getTel());
			address.setEmail(params.getEmail());
			address.setAddressAlias(params.getAddressAlias());
			address.setRemark(params.getRemark());
			address.setStatus(1);
			address.setCreateTime(DateUtil.getCurrentDateTime());

			// 获取收货地址的所属区域列表
			List<Integer> areaCodeList = new ArrayList<Integer>();
			String position = area.getPosition();
			if (ProUtility.isNotNull(position)) {
				String[] arr = position.split("tr_");
				for (int i = 0; i < arr.length; i++) {
					if (ProUtility.isNotNull(arr[i])) {
						if (!arr[i].trim().equals("0")) {
							areaCodeList.add(Integer.parseInt(arr[i].trim()));
						}
					}
				}
			}
			areaCodeList.add(area.getAreaCode());
			List<ShopBaseArea> areas = baseAreaService.selectByAreaCodeList(areaCodeList);
			String areaName = "";
			for (ShopBaseArea a : areas) {
				areaName = areaName + "-" + a.getAreaName();
			}

			if (areaName.startsWith("-")) {
				areaName = areaName.substring(1);
			}

			address.setAreaName(areaName);

			// 新增
			commonService.save(address);

			messageVo.setResult(true);
			messageVo.setT(resultMap);
			messageVo.setMessage("收货地址新增成功");
			return messageVo;
		}

	}

	@Override
	@Transactional
	public ShopMessageVo<Map<String, Object>> deleteMemberAddress(IdParamsVo params) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<Map<String, Object>>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("会员删除收货地址详情调用到这里了=================");

		// 查询用户收货地址信息
		ShopMemberAddress address = commonService.get(params.getId(), ShopMemberAddress.class);
		if (address != null) {
			address.setStatus(2);
			commonService.update(address);

			messageVo.setResult(true);
			messageVo.setT(resultMap);
			messageVo.setMessage("收货地址删除成功");
			return messageVo;
		} else {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorMessage());
			// 手动回滚当前事物
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return messageVo;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public ShopMessageVo<String> resetPayPwd(MemberFindPwdParamsVo req) {
		ShopMessageVo<String> messageVo = new ShopMessageVo<>();

		// 判断短信验证码是否正确
		String cacheKeySmsNumber = req.getSmsNumber() + req.getMobile();
		Object smsNumber = masterRedisTemplate.opsForValue().get(cacheKeySmsNumber);
		if (smsNumber != null && ProUtility.isNotNull(smsNumber.toString())
				&& smsNumber.toString().equals(req.getSmsCode())) {
			masterRedisTemplate.delete(cacheKeySmsNumber);
		} else {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100006.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100006.getErrorMessage());
			// 手动回滚当前事物
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return messageVo;
		}

		// 查询会员
		ShopMember member = commonService.get(ShopMember.class, "mobile", req.getMobile());
		if (member == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorMessage());
			// 手动回滚当前事物
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return messageVo;
		}

		// 查询交易密码
		ShopMemberSecurity memberSecurity = commonService.get(ShopMemberSecurity.class, "memberId", member.getId());
		if (memberSecurity == null) {
			memberSecurity = new ShopMemberSecurity();
			memberSecurity.setMemberId(member.getId());
			memberSecurity.setPayPwd(req.getPwd());
			memberSecurity.setCreateTime(DateUtil.getCurrentDateTime());
			commonService.save(memberSecurity);
			messageVo.setT("交易密码设置成功");
		} else {
			memberSecurity.setPayPwd(req.getPwd());
			commonService.update(memberSecurity);
			messageVo.setT("重置交易密码成功");
		}
		messageVo.setResult(true);

		return messageVo;
	}

	/**
	 * 登录
	 * 
	 * @param member
	 *            登录用户对象
	 * @param loginType
	 *            登录类型：1-密码登录；2-短信验证码登录；3-第三方授权登录
	 * @param loginIp
	 *            用户ip
	 * @return
	 */
	@Transactional
	public Map<String, Object> login(ShopMember member, Integer loginType, String loginIp) {
		Map<String, Object> resultMap = new HashMap<>();
		// 获取登陆token值
		String token = SnoGerUtil.getUUID();

		// Redis存储-登陆token值
		String cacheKeyLoginToken = "login" + member.getId().toString() + "token";
		masterRedisTemplate.delete(cacheKeyLoginToken);
		masterRedisTemplate.opsForValue().set(cacheKeyLoginToken, token);
		masterRedisTemplate.expire(cacheKeyLoginToken, 8, TimeUnit.HOURS);

		// 记录登陆日志信息
		ShopLoginLog log = new ShopLoginLog();
		log.setMemberId(member.getId());
		log.setMobile(member.getMobile());
		log.setStatus(1);
		log.setMembToken(token);
		Date now = new Date();
		log.setValidTime(DateUtil.addMinute(now, 480));// token有效期8小时
		log.setLoginIp(loginIp);
		log.setLoginType(loginType);
		log.setCreateTime(now);
		commonService.save(log);

		// 返回值
		String imageDomain = ConfigPropertieUtils.getString("image.server.address");
		resultMap.put("memberId", member.getId());
		resultMap.put("mobile", member.getMobile());
		resultMap.put("nickName", member.getNickName());
		if (ProUtility.isNotNull(member.getHeadPortrait())) {
			if (member.getHeadPortrait().substring(0, 2).equals("/2")) {
				// 用户自己上传的头像
				resultMap.put("headPortrait", imageDomain + member.getHeadPortrait());
			} else {
				// 第三方头像
				resultMap.put("headPortrait", member.getHeadPortrait());
			}
		}
		resultMap.put("gender", member.getGender()); // 性别：1-女，2-男
		resultMap.put("referraCode", member.getReferraCode());
		resultMap.put("invitationCode", member.getInvitationCode());
		resultMap.put("status", member.getStatus());
		resultMap.put("token", token);

		return resultMap;
	}

	@Override
	public ShopMessageVo<Map<String, Object>> getWalletAddress(MemberIdParamsVo params) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("获取会员钱包收款地址调用到这里了=================,会员Id:" + params.getMemberId());

		// 查询用户信息
		ShopMember member = commonService.get(params.getMemberId(), ShopMember.class);
		if (member == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorMessage());
			return messageVo;
		}

		// 用户bstk钱包
		ShopWallet wallet = commonService.get(ShopWallet.class, "ownerId", params.getMemberId(), "ownerType", 1);
		if (wallet == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorMessage());
			return messageVo;
		}

		resultMap.put("walletAddress", wallet.getPublicKeyAddr());
		messageVo.setResult(true);
		messageVo.setT(resultMap);
		messageVo.setMessage("数据获取成功");
		return messageVo;
	}

	@Override
	@Transactional
	public ShopMessageVo<Map<String, Object>> sendWalletBstk(SendWalletBstkParamsVo params) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("会员发送bstk到对方钱包地址调用到这里了=================,会员Id:" + params.getMemberId());

		// 查询用户信息
		ShopMember member = commonService.get(params.getMemberId(), ShopMember.class);
		if (member == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorMessage());
			return messageVo;
		}

		// 用户bstk钱包
		ShopWallet wallet = commonService.get(ShopWallet.class, "ownerId", params.getMemberId(), "ownerType", 1);
		if (wallet == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorMessage());
			return messageVo;
		}

		// 判断钱包余额
		BigDecimal walletBalance = bwsService.walletBalance(SnoGerUtil.getUUID(), wallet.getWalletAddress());
		if (walletBalance.compareTo(params.getAmount()) < 0) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200005.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200005.getErrorMessage());
			return messageVo;
		}

		// 转账
		String transNo = bwsService.walletTransfer(SnoGerUtil.getUUID(), 0L, params.getMemberId(), 1,
				wallet.getWalletAddress(), params.getWalletAddress(), params.getAmount());
		if (ProUtility.isNull(transNo)) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_200004.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_200004.getErrorMessage());
			return messageVo;
		}

		// 记录钱包流水记录
		ShopWalletBill walletBill = new ShopWalletBill();
		walletBill.setWalletId(wallet.getId());
		// 收支类型：1-收入；2-支出
		walletBill.setInOutType(2);
		walletBill.setAmount(-params.getAmount().doubleValue());
		walletBill.setBalance(bwsService.walletBalance(SnoGerUtil.getUUID(), wallet.getWalletAddress()).doubleValue());
		walletBill.setTransactionNo(transNo);
		walletBill.setBusinessId(0L);
		walletBill.setRemark("");
		walletBill.setCreateTime(new Date());
		// 操作类型：1-充值；2-提现；3-保证金；4-消费；
		walletBill.setBusinessType(2);
		walletBill.setBusinessTitle("转出");
		walletBill.setOwnerId(params.getMemberId());
		walletBill.setOwnerType(1);
		commonService.save(walletBill);

		messageVo.setResult(true);
		messageVo.setT(resultMap);
		messageVo.setMessage("发送成功");
		return messageVo;
	}

	@Override
	public ShopMessageVo<Map<String, Object>> getWalletBill(MemberIdPagedParamsVo params) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<Map<String, Object>>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("获取会员btsk钱包流水分页数据调用到这里了=================");

		// 查询用户信息
		ShopMember member = commonService.get(params.getMemberId(), ShopMember.class);
		if (member == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorMessage());
			return messageVo;
		}

		if (params.getPageIndex() == null) {
			params.setPageIndex(0);
		}

		if (params.getPageSize() == null) {
			params.setPageSize(20);
		}

		List<ShopWalletBill> list = commonService.getListBySqlId(ShopWalletBill.class, "pageData", "ownerId",
				params.getMemberId(), "ownerType", 1, "pageIndex", params.getPageIndex() * params.getPageSize(),
				"pageSize", params.getPageSize() + 1);

		Integer totalCount = commonService.getBySqlId(ShopWalletBill.class, "pageCount", "ownerId",
				params.getMemberId(), "ownerType", 1);
		resultMap.put("totalCount", totalCount);

		List<WalletBillVo> vos = new ArrayList<WalletBillVo>();
		if (list == null || list.size() == 0) {
			resultMap.put("hasNext", 0);
		} else {
			int listCount = list.size();
			if (listCount > params.getPageSize()) {
				resultMap.put("hasNext", 1);
				list.remove(listCount - 1);
			} else {
				resultMap.put("hasNext", 0);
			}

			for (ShopWalletBill bill : list) {
				WalletBillVo vo = new WalletBillVo();
				vo.setId(bill.getId());
				vo.setBusinessTitle(bill.getBusinessTitle());
				vo.setCreateTime(bill.getCreateTime());
				vo.setBalance(bill.getBalance());
				vo.setAmount(bill.getAmount());

				vos.add(vo);
			}
		}

		resultMap.put("list", vos);
		messageVo.setT(resultMap);
		messageVo.setMessage("数据获取成功");
		messageVo.setResult(true);

		return messageVo;
	}

	@Override
	public ShopMessageVo<Map<String, Object>> getWalletBillDetail(MemberIdBusinessIdParamsVo params) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<>();
		LinkedHashMap<String, Object> resultMap = new LinkedHashMap<String, Object>();
		Map<String, Object> resultMap1 = new HashMap<String, Object>();
		logger.info("获取会员btsk钱包流水详情调用到这里了=================,会员Id:" + params.getMemberId());

		// 查询用户信息
		ShopMember member = commonService.get(params.getMemberId(), ShopMember.class);
		if (member == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorMessage());
			return messageVo;
		}

		// 钱包明细详情
		ShopWalletBill bill = commonService.get(params.getBusinessId(), ShopWalletBill.class);
		if (bill == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorMessage());
			return messageVo;
		}
		resultMap.put("流水号", bill.getTransactionNo());
		// 操作类型：1-充值；2-提现；3-保证金；4-消费；5-算力包收益
		switch (bill.getBusinessType()) {
		case 1:
			resultMap.put("类型", "转入");
			break;
		case 2:
			resultMap.put("类型", "转出");
			break;
		case 3:
			resultMap.put("类型", "支付保证金");
			break;
		case 4:
			resultMap.put("类型", "消费");
			break;
		case 5:
			resultMap.put("类型", "算力包收益");
			break;
		}

		resultMap.put("余额", bill.getBalance().toString() + " BSTK");
		resultMap.put("备注", bill.getRemark());
		resultMap.put("时间", DateUtil.getDateTime(bill.getCreateTime()));
		resultMap1.put("list", resultMap);

		// 收支类型：1-收入；2-支出
		if (bill.getInOutType() == 1) {
			resultMap1.put("title", "收入");

		} else {
			resultMap1.put("title", "支出");
		}
		resultMap1.put("amount", bill.getAmount());

		messageVo.setResult(true);
		messageVo.setT(resultMap1);
		messageVo.setMessage("数据获取成功");
		return messageVo;
	}
}
