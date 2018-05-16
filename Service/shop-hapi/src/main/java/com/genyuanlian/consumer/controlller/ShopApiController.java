package com.genyuanlian.consumer.controlller;

import com.hnair.consumer.controller.HApiController;
import com.hnair.consumer.processor.ApiProcessor;
import com.hnair.consumer.utils.spring.SpringContextHolder;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by simple on 2016/11/15.
 */
@Controller
@RequestMapping("/shop")
public class ShopApiController extends HApiController {
	
	@SuppressWarnings("rawtypes")
	@Resource(name = "slaveRedisTemplate")
	private RedisTemplate slaveRedisTemplate;

	@Override
	protected ApiProcessor getApiBean(String action) {
		try {
            ApiProcessor apiProcessor=(ApiProcessor)SpringContextHolder.getBean("hapi" + action + "processor");
            if(null != apiProcessor) {
                return apiProcessor;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (ApiProcessor) SpringContextHolder.getBean(action );
	}

	@Override
	protected boolean loginCheck(String memberId, String token) {

		if (StringUtils.isBlank(memberId) || StringUtils.isBlank(token)) {
			return false;
		}
		String cacheKeyLoginToken = "login" + memberId + "token";
		Object object = slaveRedisTemplate.opsForValue().get(cacheKeyLoginToken);
		if (object==null) {
			return false;
		}
		if (token.equals(object)) {
			return true;
		}
		return false;
	}
}
