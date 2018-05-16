package com.genyuanlian.consumer.job;


import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.genyuanlian.consumer.shop.model.ShopBstkRecord;
import com.genyuanlian.consumer.shop.model.ShopBstkWallet;
import com.genyuanlian.consumer.shop.model.ShopOrder;
import com.genyuanlian.consumer.shop.model.ShopOrderDetail;
import com.genyuanlian.consumer.shop.vo.BWSWalletCreateResponseVo;
import com.genyuanlian.consumer.shop.vo.BWSWalletResponse;
import com.genyuanlian.consumer.shop.vo.BWSWalletTransferResponseVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.hnair.consumer.utils.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.genyuanlian.consumer.shop.api.ICardOrderApi;
import com.hnair.consumer.dao.service.ICommonService;
import com.hnair.consumer.utils.DateUtil;
import com.hnair.consumer.utils.system.ConfigPropertieUtils;

@Component
@EnableScheduling
@Lazy(false)
public class OrderJob {
	
	private Logger logger=LoggerFactory.getLogger(OrderJob.class);
	
	private String jobLock=ConfigPropertieUtils.getString("job.lock");
	
	@Resource
	private ICommonService commonService;
	
	@Resource
	private ICardOrderApi cardOrderApi;
	
	/**
	 * 过期未支付
	 */
	@Scheduled(cron="0 0/1 * * * ? ")
	public void pastDue() {
		if ("1".equals(jobLock)) {
			return;
		}
		logger.info("取消过期未支付订单任务-开始");
		//等待时长，单位分钟
		Integer time=ConfigPropertieUtils.getLong("wait_pay_time",30l).intValue();
		
		Date lastCreateTime = DateUtil.addMinute(new Date(), 0-time);
		
		//提货卡订单
		List<ShopOrderDetail> orders = commonService.getList(ShopOrderDetail.class, "lastCreateTime",lastCreateTime,"status",0);
		if (orders!=null && orders.size()>0) {
			ShopMessageVo<String> result = cardOrderApi.cancelPuCardOrder(orders, 2,"过期未支付");
			if (!result.isResult()) {
				logger.error("取消过期微支付订单失败，原因："+result.getErrorMessage());
			}
		}
		logger.info("取消过期未支付订单任务-结束");
	}
	/**
	 * 调用钱包接口重试
	 */
	@Scheduled(cron="0 0/1 * * * ? ")
	public void bwsAPIReTry()
	{
		List<ShopBstkRecord> tasks = commonService.getList(ShopBstkRecord.class,"status",2);
		if(tasks != null && tasks.size()>0)
		{
			for(ShopBstkRecord record:tasks)
			{
				ShopBstkRecord upRecord = new ShopBstkRecord();
				upRecord.setId(record.getId());
				//执行任务
				try
				{

					/**
					 * 调用接口
					 */
					String response =HttpClientUtils.bwsPost(record.getCallUrl(), JSONObject.parseObject(record.getCallReq(),JSONObject.class));
					upRecord.setCallResp(response);
					BWSWalletResponse result = JSONObject.parseObject(response,BWSWalletResponse.class);

					/**
					 * 创建钱包
					 */
					if(record.getCallType() == 1)
					{

						BWSWalletCreateResponseVo resultVo = JSONObject.parseObject(result.getData(),BWSWalletCreateResponseVo.class);

						//调用接口更新 todo
						//插入 wallet
						ShopBstkWallet upWallet = new ShopBstkWallet();
						upWallet.setOwnerId(record.getOwnerId());
						upRecord.setOwnerType(record.getOwnerType());
						upWallet.setWalletAddress(resultVo.getWallet());
						upWallet.setPublicKeyAddr(resultVo.getMainAddr());
						upWallet.setCreateTime(DateUtil.getCurrentDateTime());
						commonService.save(upWallet);

					}
					/**
					 * 充值
					 */
					else if(record.getCallType()==2)
					{
						BWSWalletTransferResponseVo voResult = JSONObject.parseObject(result.getData(),BWSWalletTransferResponseVo.class);


						//调用接口更新 todo card order
						ShopOrder upOrder = new ShopOrder();
						upOrder.setId(record.getBusinessId());
						upOrder.setTransactionNo(voResult.getTxid());
						commonService.update(upOrder);


					}
					/**
					 * 消费
					 */
					else if(record.getCallType()==3)
					{
						BWSWalletTransferResponseVo voResult = JSONObject.parseObject(result.getData(),BWSWalletTransferResponseVo.class);

						//调用接口更新 todo order
						ShopOrder upOrder = new ShopOrder();
						upOrder.setId(record.getBusinessId());
						upOrder.setTransactionNo(voResult.getTxid());
						commonService.update(upOrder);

					}
					upRecord.setStatus(1);
					upRecord.setRemark("自动任务调用");
					upRecord.setCreateTime(DateUtil.getCurrentDateTime());
					commonService.update(upRecord);



				}
				catch (Exception ex)
				{
					logger.error("自动任务调用bws接口失败",ex);
					upRecord.setRetryCount(record.getRetryCount()+1);
					upRecord.setStatus(2);
					upRecord.setRemark("自动任务调用");
					upRecord.setCreateTime(DateUtil.getCurrentDateTime());
					commonService.update(upRecord);

				}

			}
		}
	}

}
