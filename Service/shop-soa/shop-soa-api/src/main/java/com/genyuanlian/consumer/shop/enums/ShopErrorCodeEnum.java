package com.genyuanlian.consumer.shop.enums;

public enum ShopErrorCodeEnum {
	ERROR_CODE_100001(100001, "手机号已注册"), 
	ERROR_CODE_100002(100002, "会员不存在"), 
	ERROR_CODE_100003(100003, "会员推荐码无效"), 
	ERROR_CODE_100004(100004, "短信发送次数超限，一小时后再尝试"),
	ERROR_CODE_100005(100005, "短信发送重复，请稍后再试"),
	ERROR_CODE_100006(100006, "验证码错误"),
	ERROR_CODE_100007(100007, "密码输错次数已达上线，请十分钟后再试"),
	ERROR_CODE_100008(100008, "登录账号与密码输入不匹配"),
	ERROR_CODE_100009(100009, "短信发送失败"),
	ERROR_CODE_100010(100010, "图片内容不存在"),
	ERROR_CODE_100011(100011, "图片上传失败"),
	ERROR_CODE_100012(100012, "会员账号状态异常"),
	ERROR_CODE_100013(100013, "数据获取失败"),
	ERROR_CODE_100014(100014, "会员状态异常"),
	
	ERROR_CODE_100100(100100, "提货卡不存在"),
	ERROR_CODE_100101(100101, "激活码错误或者不存在"),
	ERROR_CODE_100102(100102, "提货卡已激活，请在提货卡列表查看"),
	
	ERROR_CODE_100200(100200, "商品不存在"),
	ERROR_CODE_100201(100201, "商品状态错误"),
	
	ERROR_CODE_200000(200000, "库存不足"),
	ERROR_CODE_200001(200001, "价格已发生变化"),
	ERROR_CODE_200002(200002, "订单明细不存在"),
	ERROR_CODE_200003(200003, "订单关联提货卡异常"),
	ERROR_CODE_200004(200004, "BSTK钱包异常"),
	ERROR_CODE_200005(200005, "BSTK钱包余额不足"),
	ERROR_CODE_200006(200006, "订单异常"),
	ERROR_CODE_200007(200007, "商家异常"),
	
	ERROR_CODE_800001(800001, "重复支付"), 
	ERROR_CODE_800002(800002, "支付宝支付错误"), 
	ERROR_CODE_800003(800003, "支付号不存在"), 
	ERROR_CODE_800004(800004, "支付宝回调验签失败"),
	ERROR_CODE_800005(800005, "支付宝回调通知中的金额与实际金额不符"),
	ERROR_CODE_800006(800006, "支付宝回调通知中收款账户不符"),
	ERROR_CODE_800007(800007, "支付宝回调通知中的APP_ID与应用的APP_ID不符"),
	ERROR_CODE_800008(800008, "订单不存在"),
	ERROR_CODE_800009(800009, "调用微信统一下单接口异常"),
	ERROR_CODE_800010(800010, "账户余额不足"),
	ERROR_CODE_800011(800011, "订单状态异常"),
	
	ERROR_CODE_999999(999999, "未知错误");

	private Integer errorCode;
	private String errorMessage;

	private ShopErrorCodeEnum(Integer errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public static ShopErrorCodeEnum getByErrorCode(int errorCode) {
		for (ShopErrorCodeEnum errorCodeEnum : ShopErrorCodeEnum.values()) {
			if (errorCodeEnum.errorCode.intValue() == errorCode) {
				return errorCodeEnum;
			}
		}
		return null;
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
