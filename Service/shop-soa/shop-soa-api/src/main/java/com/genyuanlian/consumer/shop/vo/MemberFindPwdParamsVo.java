package com.genyuanlian.consumer.shop.vo;

import java.io.Serializable;

public class MemberFindPwdParamsVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2509028381529954554L;

	private String mobile; //注册手机号
	
	private String smsCode; //短信验证码
	
	private String smsNumber;//短信编码
	
	private String pwd; //新用户密码

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
}
