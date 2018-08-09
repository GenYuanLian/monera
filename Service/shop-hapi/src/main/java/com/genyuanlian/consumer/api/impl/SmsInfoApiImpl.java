package com.genyuanlian.consumer.api.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSON;
import com.genyuanlian.consumer.service.ISmsInfoService;
import com.genyuanlian.consumer.shop.api.ISmsInfoApi;
import com.genyuanlian.consumer.shop.enums.ShopErrorCodeEnum;
import com.genyuanlian.consumer.shop.enums.SmsTypeConstants;
import com.genyuanlian.consumer.shop.model.ShopMember;
import com.genyuanlian.consumer.shop.model.ShopSmsInfo;
import com.genyuanlian.consumer.shop.vo.SendSmsParamsVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.hnair.consumer.dao.service.ICommonService;
import com.hnair.consumer.utils.DateUtil;
import com.hnair.consumer.utils.ProUtility;
import com.hnair.consumer.utils.QCloudSMSUtils;
import com.hnair.consumer.utils.SmsContent;
import com.hnair.consumer.utils.SnoGerUtil;
import com.hnair.consumer.utils.system.ConfigPropertieUtils;

@Component("smsInfoApi")
public class SmsInfoApiImpl implements ISmsInfoApi {

	private static Logger logger = LoggerFactory.getLogger(SmsInfoApiImpl.class);

	@Resource
	private ICommonService commonService;

	@Resource
	private ISmsInfoService smsInfoService;

	@Resource(name = "masterRedisTemplate")
	private RedisTemplate<String, String> masterRedisTemplate;

	@Resource(name = "slaveRedisTemplate")
	private RedisTemplate<String, String> slaveRedisTemplate;

	@Override
	@Transactional
	public ShopMessageVo<String> sendSms(SendSmsParamsVo params) {
		ShopMessageVo<String> messageVo = new ShopMessageVo<String>();
		logger.info("短信发送调用到这里了=================,手机号:" + params.getMobile() + "短信类型:" + params.getSmstype()+"参数："+params.getParams());
		// 财务确认付款
		if (SmsTypeConstants.confirmPayment.equals(params.getSmstype())) {
			// 允许发送此类验证码的手机号集合
			String mobiles = ConfigPropertieUtils.getString("confirm_payment_mobiles");
			if (StringUtils.isBlank(mobiles)) {
				messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_888888.getErrorCode().toString());
				messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_888888.getErrorMessage());
				// 手动回滚当前事物
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return messageVo;
			}

			String[] split = mobiles.split(",");
			// 验证当前手机号是否有发送此类验证码的权限
			if (!ArrayUtils.contains(split, params.getMobile())) {
				messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_888888.getErrorCode().toString());
				messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_888888.getErrorMessage());
				// 手动回滚当前事物
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return messageVo;
			}
		}

		// 缓存短信发送的次数
		String cacheKeySmsHourCount = "sendSms" + params.getMobile() + "key";
		logger.info("缓存短信发送的次数:"+cacheKeySmsHourCount);
		// 短信发送的次数
		Object logCount = masterRedisTemplate.opsForValue().get(cacheKeySmsHourCount);
		int maxCount = Integer.parseInt(ConfigPropertieUtils.getString("member.sendsms.count"));
		if (logCount != null && ProUtility.isNotNull(logCount.toString())
				&& Integer.parseInt(logCount.toString()) >= maxCount && !"confirmPayment".equals(params.getSmstype())) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100004.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100004.getErrorMessage());
			// 手动回滚当前事物
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return messageVo;
		}

		// 判断短信发送次数
		if (smsInfoService.queryValiditytime(params.getMobile(), 1, 1, params.getSmstype())) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100005.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100005.getErrorMessage());
			// 手动回滚当前事物
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return messageVo;
		}

		// 检验会员是否注册
		List<ShopMember> members = commonService.getList(ShopMember.class, "mobile", params.getMobile());
		if (SmsTypeConstants.register.equals(params.getSmstype()) && members != null && members.size() > 0) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100001.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100001.getErrorMessage());
			// 手动回滚当前事物
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return messageVo;
		}

		// 会员未注册，登录或者找回密码，返回会员未注册错误
		if (SmsTypeConstants.login.equals(params.getSmstype()) && (members == null || members.size() == 0)) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorMessage());
			// 手动回滚当前事物
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return messageVo;
		}

		if (SmsTypeConstants.findPwd.equals(params.getSmstype()) && (members == null || members.size() == 0)) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorMessage());
			// 手动回滚当前事物
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return messageVo;
		}

		if (SmsTypeConstants.resetPayPwd.equals(params.getSmstype()) && (members == null || members.size() == 0)) {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100002.getErrorMessage());
			// 手动回滚当前事物
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return messageVo;
		}

		// 调用发送短信接口
		
		ArrayList<String> pars = new ArrayList<String>();

		// 注册-register,找回密码-findPwd, 登录-login
		String verificationCode=null;
		switch (params.getSmstype()) {
		case SmsTypeConstants.register:
		case SmsTypeConstants.findPwd:
		case SmsTypeConstants.login:
		case SmsTypeConstants.confirmPayment:
		case SmsTypeConstants.resetPayPwd:
			verificationCode = SnoGerUtil.getRandomNum(6);
			pars.add(verificationCode);
			params.setParams(pars);
			break;
		}
		
		SmsSingleSenderResult resp = QCloudSMSUtils.sendSMS(params.getSmstype(), params.getMobile(), params.getParams());
		
		String content = SmsContent.buildContent(params.getParams(), params.getSmstype());// 短信内容
		// 持久化
		String smsNumber = UUID.randomUUID().toString();// 发送短信编码
		ShopSmsInfo smsInfo = new ShopSmsInfo();// 存储短信数据库
		smsInfo.setContent(content);
		if ("register".equals(params.getSmstype()) || "confirmPayment".equals(params.getSmstype())) {
			smsInfo.setMemberId(Long.valueOf("0"));
		} else {
			smsInfo.setMemberId(members.get(0).getId());
		}
		smsInfo.setMobile(params.getMobile());
		smsInfo.setSmstype(params.getSmstype());
		smsInfo.setSmsCode(smsNumber);// 短信编码
		smsInfo.setStatus(resp.result);
		smsInfo.setCallResp(JSON.toJSONString(resp));
		Date now = new Date();
		smsInfo.setValiditytime(DateUtil.getDateTime(DateUtil.addMinute(now, 5)));// 短信有效期5分钟
		smsInfo.setCreateTime(now);
		smsInfoService.save(smsInfo);

		// 结果处理
		if (resp.result == 0) {
			if (StringUtils.isNotBlank(verificationCode)) {
				// Redis存储-短信编号
				String cacheKeySmsNumber = smsNumber + params.getMobile();
				masterRedisTemplate.delete(cacheKeySmsNumber);
				masterRedisTemplate.opsForValue().set(cacheKeySmsNumber, verificationCode);
				masterRedisTemplate.expire(cacheKeySmsNumber, 5, TimeUnit.MINUTES);
			}
			

			// 短信发送控制缓存
			String cacheKeySmsSend = params.getSmstype() + params.getMobile();
			masterRedisTemplate.delete(cacheKeySmsSend);
			masterRedisTemplate.opsForValue().set(cacheKeySmsSend, params.getMobile());
			masterRedisTemplate.expire(cacheKeySmsSend, 1, TimeUnit.MINUTES);

			// 发送短信次数限制
			int count = 1;
			if (logCount != null && ProUtility.isNotNull(logCount.toString())) {
				count = Integer.valueOf(logCount.toString()) + 1;
			}

			// Redis存储-发送短信次数限制
			masterRedisTemplate.delete(cacheKeySmsHourCount);
			masterRedisTemplate.opsForValue().set(cacheKeySmsHourCount, String.valueOf(count));
			masterRedisTemplate.expire(cacheKeySmsHourCount, 1, TimeUnit.HOURS);

			// 返回发送短信成功的信息
			messageVo.setResult(true);
			messageVo.setT(smsNumber);
			messageVo.setMessage("短信发送成功");
			return messageVo;

		} else {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100009.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100009.getErrorMessage());
			return messageVo;
		}
	}

	@Override
	public ShopMessageVo<Boolean> checkVerificationCode(String smsNumber, String mobile, String verificationCode) {
		ShopMessageVo<Boolean> messageVo = new ShopMessageVo<>();

		// 判断短信验证码是否正确
		String cacheKeySmsNumber = smsNumber + mobile;
		String code = masterRedisTemplate.opsForValue().get(cacheKeySmsNumber);
		if (StringUtils.isNotBlank(code) && code.equals(verificationCode)) {
			masterRedisTemplate.delete(cacheKeySmsNumber);
			messageVo.setResult(true);
			messageVo.setT(true);
		} else {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100006.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100006.getErrorMessage());
		}

		return messageVo;
	}

}
