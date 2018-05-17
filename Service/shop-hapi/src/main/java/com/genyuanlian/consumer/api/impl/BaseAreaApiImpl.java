package com.genyuanlian.consumer.api.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.genyuanlian.consumer.service.IBaseAreaService;
import com.genyuanlian.consumer.shop.api.IBaseAreaApi;
import com.genyuanlian.consumer.shop.enums.ShopErrorCodeEnum;
import com.genyuanlian.consumer.shop.model.ShopBaseArea;
import com.genyuanlian.consumer.shop.vo.AreaVo;
import com.genyuanlian.consumer.shop.vo.BaseAreaParentCodeParamsVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.hnair.consumer.dao.service.ICommonService;

@Component("baseAreaApi")
public class BaseAreaApiImpl implements IBaseAreaApi {
	private static Logger logger = LoggerFactory.getLogger(BaseAreaApiImpl.class);

	@Resource
	private ICommonService commonService;

	@Resource
	private IBaseAreaService baseAreaService;

	@Override
	public ShopMessageVo<List<AreaVo>> getAreasByParentCode(BaseAreaParentCodeParamsVo params) {
		ShopMessageVo<List<AreaVo>> messageVo = new ShopMessageVo<List<AreaVo>>();
		logger.info("根据parentCode获取区域列表调用到这里了=================");

		List<ShopBaseArea> list = baseAreaService.getAreasByParentCode(params.getParentCode());
		if (list != null) {
			List<AreaVo> result = new ArrayList<AreaVo>();
			for (ShopBaseArea a : list) {
				AreaVo v = new AreaVo();
				v.setName(a.getAreaName());
				v.setValue(a.getAreaCode().toString());
				v.setParent(a.getParentCode().toString());
				result.add(v);
			}
			messageVo.setResult(true);
			messageVo.setT(result);
			messageVo.setMessage("数据获取成功");
			return messageVo;
		} else {
			messageVo.setErrorCode(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorCode().toString());
			messageVo.setErrorMessage(ShopErrorCodeEnum.ERROR_CODE_100013.getErrorMessage());
			return messageVo;
		}
	}

	@Override
	public ShopMessageVo<List<AreaVo>> getAreas() {
		ShopMessageVo<List<AreaVo>> messageVo = new ShopMessageVo<List<AreaVo>>();
		logger.info("获取全部区域数据调用到这里了=================");

		List<AreaVo> areas = null;
		areas = commonService.getListBySqlId(ShopBaseArea.class, "selectShortAll");
		messageVo.setResult(true);
		messageVo.setT(areas);
		messageVo.setMessage("数据获取成功");
		return messageVo;
	}

}
