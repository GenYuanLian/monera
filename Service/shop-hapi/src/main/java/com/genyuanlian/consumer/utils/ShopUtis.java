package com.genyuanlian.consumer.utils;

import java.util.Date;
import java.util.Random;


import com.hnair.consumer.utils.DateUtil;

public class ShopUtis {

	public static String buildOrderNo(String prefix) {
		if (prefix==null) {
			prefix="";
		}
		Random rand = new Random();
		int randLen=4;
		// 获得随机数  
	    double randNum = (1 + rand.nextDouble()) * Math.pow(10, randLen); 
        
	    return prefix+DateUtil.formatDateByFormat(new Date(), "yyyyMMddHHmmssSSS")+String.valueOf(randNum).substring(1, randLen+1);
	}
	
	public static void main(String[] args) {

	    System.out.println(buildOrderNo(null));
	}
}
