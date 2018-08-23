package com.genyuanlian.consumer.shop.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.genyuanlian.consumer.shop.model.ShopOrderDetail;
import com.genyuanlian.consumer.shop.vo.CreateCommodityOrderParamsVo;
import com.genyuanlian.consumer.shop.vo.OrderNoParamsVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;

public interface ICardOrderApi {
	/**
	 * 创建提货卡订单
	 * 
	 * @param puCardTypeId
	 * @param payCount
	 * @param cardType
	 * @param amount
	 * @param memberId
	 * @param remark
	 * @return
	 */
	public ShopMessageVo<String> createPuCardOrder(CreateCommodityOrderParamsVo req);

	/**
	 * 取消订单
	 * 
	 * @param orders
	 *            要取消的订单
	 * @param status
	 *            1-取消支付；2-支付过期
	 * @param cancelReason
	 *            取消原因
	 * @return
	 */
	public ShopMessageVo<String> cancelPuCardOrder(List<ShopOrderDetail> orders, Integer status, String cancelReason);

	/**
	 * 用户删除订单
	 * 
	 * @param OrderNoParamsVo
	 * @return
	 */
	public ShopMessageVo<String> deleteOrder(OrderNoParamsVo params);

	/**
	 * 买家确认收货
	 * 
	 * @param OrderNoParamsVo
	 * @return
	 */
	public ShopMessageVo<String> buyerConfirmOrder(OrderNoParamsVo params);

	/**
	 * 根据订单编号查询订单
	 * 
	 * @param orderNo
	 * @return
	 */
	public ShopOrderDetail getOrderByOrderNo(String orderNo);

	/**
	 * 查询订单列表
	 * 
	 * @param memberId
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public ShopMessageVo<Map<String, Object>> getPuCardOrders(Long memberId, Integer pageIndex, Integer pageSize);
}
