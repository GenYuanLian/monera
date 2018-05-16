package com.genyuanlian.consumer.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import com.genyuanlian.consumer.service.IBaseAreaService;
import com.genyuanlian.consumer.shop.model.ShopBaseArea;
import com.hnair.consumer.dao.spi.ICommonDao;

/**
 * Using IntelliJ IDEA.
 *
 * @author HNAyd.xian
 * @date 2018/2/1 16:51
 */
@Service("baseAreaService")
public class BaseAreaServiceImpl implements IBaseAreaService {

	@Resource
	private ICommonDao shopDao;

	@Override
	public List<ShopBaseArea> getAreasByParentCode(Integer parentCode) {
		return shopDao.getList(ShopBaseArea.class, "parentCode", parentCode);
	}

	@Override
	public List<ShopBaseArea> selectByAreaCodeList(@Param("areaCodeList") List<Integer> areaCodeList) {
		List<ShopBaseArea> areas = shopDao.getListBySqlId(ShopBaseArea.class, "selectByAreaCodeList", "areaCodeList",
				areaCodeList);
		return areas;
	}

}
