package com.genyuanlian.consumer.shop.vo;

import java.io.Serializable;

public class MemberRegisterParamsVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6550011007936956526L;

	private String mobile; // 注册手机号

	private String smsCode; // 短信验证码

	private String smsNumber;// 短信编码

	private String pwd; // 用户密码

	private String referraCode; // 推荐码

	private String channel; // 渠道(IOS/Android/H5/社区官网)

	private String channelVersion; // 渠道版本

	private String ip;
	
	/**
	 * 是否第三方授权登录注册
	 */
	private Boolean isThirdparty=false;  
	
	private String headPortrait;  //头像
	
	private Integer gender;  //性别：1-先生，2-女士
	
	private String nickName;

	// 临时属性
	private Long referraId = new Long(0); // 推荐人Id

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getSmsCode() {
		return smsCode;
	}

	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
	}

	public String getSmsNumber() {
		return smsNumber;
	}

	public void setSmsNumber(String smsNumber) {
		this.smsNumber = smsNumber;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getReferraCode() {
		return referraCode;
	}

	public void setReferraCode(String referraCode) {
		this.referraCode = referraCode;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getChannelVersion() {
		return channelVersion;
	}

	public void setChannelVersion(String channelVersion) {
		this.channelVersion = channelVersion;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Long getReferraId() {
		return referraId;
	}

	public void setReferraId(Long referraId) {
		this.referraId = referraId;
	}

	public Boolean getIsThirdparty() {
		return isThirdparty;
	}

	public void setIsThirdparty(Boolean isThirdparty) {
		this.isThirdparty = isThirdparty;
	}

	public String getHeadPortrait() {
		return headPortrait;
	}

	public void setHeadPortrait(String headPortrait) {
		this.headPortrait = headPortrait;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
}
