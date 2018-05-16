package com.genyuanlian.consumer.utils;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.genyuanlian.consumer.shop.utils.AlipayProperties;

public class AlipayClientFactory {
	// SDK public request class. It includes the common request parameters, signature signing and verification. The developer would not need to worry about the digital signature portion.
    private static final AlipayClient client = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",AlipayProperties.ALIPAY_APP_ID,AlipayProperties.ALIPAY_RSA_RRIVATE_KEY,"json",AlipayProperties.CHARSET,AlipayProperties.ALIPAY_PUBLIC_KEY,AlipayProperties.SIGN_TYPE);

    public static AlipayClient getAlipayClientInstance() {
        return client;
    }
}
