package com.genyuanlian.consumer.shop.api;

import java.util.List;

import com.genyuanlian.consumer.shop.model.ShopConfirmPaymentLog;
import com.genyuanlian.consumer.shop.model.ShopOrderDetail;
import com.genyuanlian.consumer.shop.vo.CommodityOrderPayParamsVo;
import com.genyuanlian.consumer.shop.vo.CreateCommodityOrderParamsVo;
import com.genyuanlian.consumer.shop.vo.OrderNoParamsVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;

public interface ICommodityOrderApi {

	/**
	 * 创建商品订单
	 * 
	 * @param CreateCommodityOrderParamsVo
	 * @return
	 */
	public ShopMessageVo<String> createOrder(CreateCommodityOrderParamsVo params);

	/**
	 * 商品类订单支付
	 * 
	 * @param CommodityOrderPayParamsVo
	 * @return
	 */
	public ShopMessageVo<String> orderPay(CommodityOrderPayParamsVo params);

	/**
	 * 确认付款
	 * 
	 * @param confirmPaymentLog
	 * @return
	 */
	public ShopMessageVo<String> confirmPayment(ShopConfirmPaymentLog confirmPaymentLog);

	/**
	 * 发送快递
	 * 
	 * @param orderDetailId
	 * @param expressNo
	 * @param expressSupplier
	 * @return
	 */
	public ShopMessageVo<String> sendDelivery(Long orderDetailId, String expressNo, String expressSupplier);

	/**
	 * 为订单明细创建相应的算力服务计划任务
	 * 
	 * @param orderDetails
	 */
	public void CreateOrderCalcForce(List<ShopOrderDetail> orderDetails);

	/**
	 * 数字货币扫码支付支付完成
	 * 
	 * @param OrderNoParamsVo
	 * @return
	 */
	public ShopMessageVo<String> completeOrderPay(OrderNoParamsVo params);
}
