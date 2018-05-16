package com.genyuanlian.consumer.shop.vo;

import java.io.Serializable;

public class SendSmsParamsVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4621703751361060890L;
	
	private String mobile; //手机号
	
	private String smstype;//短信类型：注册-register,找回密码-findPwd, 登录-login

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getSmstype() {
		return smstype;
	}

	public void setSmstype(String smstype) {
		this.smstype = smstype;
	}
}
