package com.genyuanlian.consumer.shop.vo;

import java.io.Serializable;

public class MemberLoginParamsVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6898453904863723164L;

	private String loginName; //登录账号
	
	private String loginType; //登录方式 password,smscode
	
	private String loginCode; //密码或者短信编码
	
	private String smsCode; //短信验证码
	
	private String ip; 

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getLoginType() {
		return loginType;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}

	public String getLoginCode() {
		return loginCode;
	}

	public void setLoginCode(String loginCode) {
		this.loginCode = loginCode;
	}

	public String getSmsCode() {
		return smsCode;
	}

	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
}
