package com.genyuanlian.consumer.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.hnair.consumer.utils.MD5Utils;
import com.hnair.consumer.utils.Sha1Util;
import com.hnair.consumer.utils.XMLParser;

public class WeixinpayUtils {

	private static Logger logger = LoggerFactory.getLogger(WeixinpayUtils.class);
	
	/**
     * 获取一定长度的随机字符串
     * @param length 指定字符串长度
     * @return 一定长度的字符串
     */
    public static String getRandomStringByLength(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
    
    /**
     * 签名算法
     *
     * @param o 要参与签名的数据对象
     * @return 签名
     * @throws IllegalAccessException
     */
    public static String getSign(Map<String, Object> map) {
        ArrayList<String> list = new ArrayList<String>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() != "" && entry.getKey() != "key") {
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        int size = list.size();
        String[] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        logger.info("微信支付，当前微信支付的key为："+map.get("key"));
        result += "key=" + map.get("key");
        logger.info("Sign Before MD5:" + result);
        result = MD5Utils.MD5Encode(result).toUpperCase();
        logger.info("Sign after MD5:" + result);
        return result;
    }
    
    /**
     * 
     * @param map
     * @return
     */
    public static String getsha1Sign(Map<String, String> map) {
    	ArrayList<String> list = new ArrayList<String>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (StringUtils.isNotBlank(entry.getValue())) {
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        int size = list.size();
        String[] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        result=result.substring(0, result.length()-1);
        logger.info("待签名字符串："+result);
        result=SHA1.getDigestOfString(result.toString().getBytes());
//        result = Sha1Util.getSha1(result);
        logger.info("签名结果："+result);
        return result;
    }

    /**
     * 从API返回的XML数据里面重新计算一次签名
     *
     * @param responseString API返回的XML数据
     * @return 新鲜出炉的签名
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static String getSignFromResponseString(String responseString) throws IOException, SAXException, ParserConfigurationException {
        Map<String, Object> map = XMLParser.getMapFromXML(responseString);
        //清掉返回数据对象里面的Sign数据（不能把这个数据也加进去进行签名），然后用签名算法进行签名
        map.put("sign", "");
        //将API返回的数据根据用签名算法进行计算新的签名，用来跟API返回的签名进行比较
        return getSign(map);
    }

    /**
     * 检验API返回的数据里面的签名是否合法，避免数据在传输的过程中被第三方篡改
     *
     * @param responseString API返回的XML数据字符串
     * @return API签名是否合法
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static boolean checkIsSignValidFromResponseString(String responseString) {

        try {
            Map<String, Object> map = XMLParser.getMapFromXML(responseString);
            logger.debug(map.toString());

            String signFromAPIResponse = map.get("sign").toString();
            if (signFromAPIResponse == "" || signFromAPIResponse == null) {
                logger.debug("API返回的数据签名数据不存在，有可能被第三方篡改!!!");
                return false;
            }
            logger.debug("服务器回包里面的签名是:" + signFromAPIResponse);
            //清掉返回数据对象里面的Sign数据（不能把这个数据也加进去进行签名），然后用签名算法进行签名
            map.put("sign", "");
            //将API返回的数据根据用签名算法进行计算新的签名，用来跟API返回的签名进行比较
            String signForAPIResponse = getSign(map);

            if (!signForAPIResponse.equals(signFromAPIResponse)) {
                //签名验不过，表示这个API返回的数据有可能已经被篡改了
                logger.debug("API返回的数据签名验证不通过，有可能被第三方篡改!!!");
                return false;
            }
            logger.debug("恭喜，API返回的数据签名验证通过!!!");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public static String getNonceStr() {
		Random random = new Random();
		return MD5Utils.MD5Encode(String.valueOf(random.nextInt(10000)), "UTF-8");

	}

	public static String getTimeStamp() {
		return String.valueOf(System.currentTimeMillis() / 1000);
	}

	public static void main(String[] args) {
		Map<String, String>result=new HashMap<>();
		result.put("noncestr", "az50a3qaru2bj4bk");
		result.put("jsapi_ticket", "kgt8ON7yVITDhtdwci0qefZU8RG_OqsjvDt2ocZMOHAZ3VZW9-wbRR6E3ZPUKtHTX2AGLLvJh3rtEZ_ftxbFCw");
		result.put("timestamp", "1528452197");
		result.put("url", "http%3A%2F%2Fservice.genyuanlian.com%2Fgylshop%2F%23%2Fproduct_detail%3FproId%3D2%26proType%3D3%26code%3DK82ep1");
		String signature=WeixinpayUtils.getsha1Sign(result);
		System.out.println(signature);
	}
}
