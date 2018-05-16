package com.genyuanlian.consumer.shop.vo;

import java.io.Serializable;

public class MemberRegisterParamsVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6550011007936956526L;

	private String mobile; //注册手机号
	
	private String smsCode; //短信验证码
	
	private String smsNumber;//短信编码
	
	private String pwd; //用户密码
	
	private String referraCode; //推荐码

	private String channel; //渠道(IOS/Android/H5/PC官网)
	
	private String channelVersion; //渠道版本
	
	private String ip; 
	
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
}
