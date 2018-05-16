package com.genyuanlian.consumer.api.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
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
import com.genyuanlian.consumer.shop.model.ShopPuCard;
import com.genyuanlian.consumer.shop.vo.BWSWalletCreateResponseVo;
import com.genyuanlian.consumer.shop.vo.IdParamsVo;
import com.genyuanlian.consumer.shop.vo.LoginNameParamsVo;
import com.genyuanlian.consumer.shop.vo.MemberAddressParamsVo;
import com.genyuanlian.consumer.shop.vo.MemberFindPwdParamsVo;
import com.genyuanlian.consumer.shop.vo.MemberIdParamsVo;
import com.genyuanlian.consumer.shop.vo.MemberLoginParamsVo;
import com.genyuanlian.consumer.shop.vo.MemberRegisterParamsVo;
import com.genyuanlian.consumer.shop.vo.NicknameParamsVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.genyuanlian.consumer.shop.vo.UploadHeadPortraitParamsVo;
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
	public ShopMessageVo<Map<String, Object>> register(MemberRegisterParamsVo params) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("新会员注册调用到这里了=================,手机号:" + params.getMobile() + "验证码:" + params.getSmsCode() + "短信编码:"
				+ params.getSmsNumber() + "推荐码:" + params.getReferraCode());

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

		// 判断手机号是否被占用
		List<ShopMember> members = commonService.getList(ShopMember.class, "mobile", params.getMobile());
		if (members != null && members.size() > 0) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100001.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100001.getErrorMessage());
			return messageVo;
		}

		// 如果有推荐码参数，验证参数是否正确
		if (ProUtility.isNotNull(params.getReferraCode())) {
			List<ShopMember> referras = commonService.getList(ShopMember.class, "referraCode", params.getReferraCode());
			if (referras == null || referras.size() == 0) {
				messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100003.getErrorCode().toString());
				messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100003.getErrorMessage());
				return messageVo;
			}
		}
		//持久化
		ShopMember register = registerPersist(params);

		// 返回值
		resultMap.put("memberId", register.getId());
		resultMap.put("mobile", register.getMobile());
		resultMap.put("invitationCode", register.getInvitationCode());

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
		register.setChannel(params.getChannel());
		register.setChannelVersion(params.getChannelVersion());
		register.setSignIp(params.getIp());

		// 默认值
		register.setOwnerType(1);
		register.setLoginName(params.getMobile());
		register.setNickName("主人");
		register.setIsIdentification(0);
		register.setInvitationCode(getRandomString());
		register.setStatus(1);
		register.setCreateTime(DateUtil.getCurrentDateTime());

		// 持久化
		memberService.register(register);

		// 创建轻钱包
		BWSWalletCreateResponseVo resp = bwsService.walletCreate(register.getId(), 1);
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
				return messageVo;
			}

			// 判断密码是否正确
			if (params.getLoginType().equals("password")) {
				if (params.getLoginCode().equals(member.getLoginPwd())) {
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
					log.setLoginIp(params.getIp());
					log.setCreateTime(now);
					commonService.save(log);

					// 返回值
					String imageDomain = ConfigPropertieUtils.getString("image.server.address");
					resultMap.put("memberId", member.getId());
					resultMap.put("mobile", member.getMobile());
					resultMap.put("nickName", member.getNickName());
					if (ProUtility.isNotNull(member.getHeadPortrait())) {
						resultMap.put("headPortrait", imageDomain + member.getHeadPortrait());
					}
					resultMap.put("gender", member.getGender()); // 性别：1-女，2-男
					resultMap.put("referraCode", member.getReferraCode());
					resultMap.put("invitationCode", member.getInvitationCode());
					resultMap.put("status", member.getStatus());
					resultMap.put("token", token);
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
					return messageVo;
				}
			} else { // 短信验证码登录
				String cacheKeySmsNumber = params.getLoginCode() + member.getMobile();
				Object smsNumber = masterRedisTemplate.opsForValue().get(cacheKeySmsNumber);
				if (smsNumber != null && ProUtility.isNotNull(smsNumber.toString())
						&& smsNumber.toString().equals(params.getSmsCode())) {
					logger.info("短信验证登录成功=================,手机号:" + member.getMobile() + "验证码:" + params.getSmsCode()
							+ "短信编码:" + params.getLoginCode());

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
					log.setLoginIp(params.getIp());
					log.setCreateTime(now);
					commonService.save(log);

					// 返回值
					String imageDomain = ConfigPropertieUtils.getString("image.server.address");
					resultMap.put("memberId", member.getId());
					resultMap.put("mobile", member.getMobile());
					resultMap.put("nickName", member.getNickName());
					if (ProUtility.isNotNull(member.getHeadPortrait())) {
						resultMap.put("headPortrait", imageDomain + member.getHeadPortrait());
					}
					resultMap.put("gender", member.getGender()); // 性别：1-女，2-男
					resultMap.put("referraCode", member.getReferraCode());
					resultMap.put("invitationCode", member.getInvitationCode());
					resultMap.put("status", member.getStatus());
					resultMap.put("token", token);
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
					return messageVo;
				}
			}
		} else {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100008.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100008.getErrorMessage());
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
	public ShopMessageVo<Map<String, Object>> getMemberInfo(MemberIdParamsVo params) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("获取会员基本信息调用到这里了=================,会员Id:" + params.getMemberId());

		// 根据登陆名称，密码查询用户信息
		ShopMember member = commonService.get(params.getMemberId(), ShopMember.class);
		if (member == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorMessage());
			return messageVo;
		}

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

		messageVo.setResult(true);
		messageVo.setT(resultMap);
		messageVo.setMessage("数据获取成功");
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
			ShopMember upMember = new ShopMember();
			upMember.setId(member.getId());
			upMember.setLoginPwd(params.getPwd());
			upMember.setPwdUptime(new Date());
			upMember.setOldPwd(member.getLoginPwd());
			commonService.update(upMember);

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
	public ShopMessageVo<Map<String, Object>> uploadHeadPortrait(UploadHeadPortraitParamsVo params) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("会员上传头像调用到这里了=================");

		// 查询用户信息
		ShopMember member = commonService.get(params.getMemberId(), ShopMember.class);
		if (member != null) {
			ShopMember upMember = new ShopMember();
			upMember.setId(member.getId());
			upMember.setHeadPortrait(params.getImageUrl());
			commonService.update(upMember);

			messageVo.setResult(true);
			messageVo.setT(resultMap);
			messageVo.setMessage("头像上传成功");
			return messageVo;
		} else {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorMessage());
			return messageVo;
		}
	}

	@Override
	public ShopMessageVo<Map<String, Object>> setLoginName(LoginNameParamsVo params) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<Map<String, Object>>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("会员上传头像调用到这里了=================");

		// 查询用户信息
		ShopMember member = commonService.get(params.getMemberId(), ShopMember.class);
		if (member != null) {
			ShopMember upMember = new ShopMember();
			upMember.setId(member.getId());
			upMember.setLoginName(params.getLoginName());
			commonService.update(upMember);

			messageVo.setResult(true);
			messageVo.setT(resultMap);
			messageVo.setMessage("用户名设置成功");
			return messageVo;
		} else {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorMessage());
			return messageVo;
		}
	}

	@Override
	public ShopMessageVo<Map<String, Object>> setNickname(NicknameParamsVo params) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<Map<String, Object>>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("会员设置昵称调用到这里了=================");

		// 查询用户信息
		ShopMember member = commonService.get(params.getMemberId(), ShopMember.class);
		if (member != null) {
			ShopMember upMember = new ShopMember();
			upMember.setId(member.getId());
			upMember.setNickName(params.getNickName());
			commonService.update(upMember);

			messageVo.setResult(true);
			messageVo.setT(resultMap);
			messageVo.setMessage("昵称设置成功");
			return messageVo;
		} else {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorMessage());
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
	public ShopMessageVo<Map<String, Object>> saveMemberAddress(MemberAddressParamsVo params) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<Map<String, Object>>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("保存会员收货地址调用到这里了=================");

		ShopBaseArea area = commonService.get(ShopBaseArea.class, "areaCode", params.getAreaCode());
		if (area == null) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorMessage());
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
	public ShopMessageVo<Map<String, Object>> deleteMemberAddress(IdParamsVo params) {
		ShopMessageVo<Map<String, Object>> messageVo = new ShopMessageVo<Map<String, Object>>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("会员删除收货地址详情调用到这里了=================");

		// 查询用户收货地址信息
		ShopMemberAddress address = commonService.get(params.getId(), ShopMemberAddress.class);
		if (address != null) {
			ShopMemberAddress upAddress = new ShopMemberAddress();
			upAddress.setId(address.getId());
			upAddress.setStatus(2);
			commonService.update(upAddress);

			messageVo.setResult(true);
			messageVo.setT(resultMap);
			messageVo.setMessage("收货地址删除成功");
			return messageVo;
		} else {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorMessage());
			return messageVo;
		}
	}

}
