package com.genyuanlian.consumer.shop.vo;

import java.io.Serializable;
import java.util.ArrayList;

public class SendSmsParamsVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4621703751361060890L;
	
	private String mobile; //手机号
	
	private String smstype;//短信类型：注册-register,找回密码-findPwd, 登录-login,确认付款-confirmPayment，重置支付密码-resetPayPwd
	
	private ArrayList<String> params; //短信模板填充参数

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

	public ArrayList<String> getParams() {
		return params;
	}

	public void setParams(ArrayList<String> params) {
		this.params = params;
	}

	
}
