package com.genyuanlian.consumer.job;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.genyuanlian.consumer.service.IBWSService;
import com.genyuanlian.consumer.shop.api.IAuctionApi;
import com.genyuanlian.consumer.shop.api.ICardOrderApi;
import com.genyuanlian.consumer.shop.api.ISystemApi;
import com.genyuanlian.consumer.shop.model.ShopBstkRecord;
import com.genyuanlian.consumer.shop.model.ShopCommodity;
import com.genyuanlian.consumer.shop.model.ShopOrderCalcForce;
import com.genyuanlian.consumer.shop.model.ShopOrderCalcForceTask;
import com.genyuanlian.consumer.shop.model.ShopOrderDetail;
import com.genyuanlian.consumer.shop.model.ShopPuCardType;
import com.genyuanlian.consumer.shop.model.ShopSaleVolume;
import com.genyuanlian.consumer.shop.model.ShopWallet;
import com.genyuanlian.consumer.shop.model.ShopWalletBill;
import com.genyuanlian.consumer.shop.utils.BWSProperties;
import com.genyuanlian.consumer.shop.vo.BWSWalletCreateResponseVo;
import com.genyuanlian.consumer.shop.vo.ReceiverVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.genyuanlian.consumer.vo.SaleVolumeConfigVo;
import com.hnair.consumer.dao.service.ICommonService;
import com.hnair.consumer.utils.DateUtil;
import com.hnair.consumer.utils.ProUtility;
import com.hnair.consumer.utils.QCloudSMSUtils;
import com.hnair.consumer.utils.SnoGerUtil;
import com.hnair.consumer.utils.system.ConfigPropertieUtils;

@Component
@Lazy(false)
public class OrderJob {

	private Logger logger = LoggerFactory.getLogger(OrderJob.class);

	private String jobLock = ConfigPropertieUtils.getString("job.lock");

	private String jobLockVirtualSaleColume = ConfigPropertieUtils.getString("job.lock.virtual.sale.colume");

	private String virtualSaleColumeConfig = ConfigPropertieUtils.getString("virtual.sale.volume.config");

	@Resource
	private ICommonService commonService;
	@Resource
	private ISystemApi systemApi;

	/**
	 * BWS接口服务
	 */
	@Resource
	private IBWSService bwsService;

	@Resource
	private ICardOrderApi cardOrderApi;

	@Resource
	private IAuctionApi auctionApi;

	/**
	 * 过期未支付
	 */
	@Scheduled(cron = "0 0/1 * * * ? ")
	public void pastDue() {
		if ("1".equals(jobLock)) {
			return;
		}

		logger.info("取消过期未支付订单任务-开始");

		// 超时半小时未支付即过期的订单
		// 等待时长，单位分钟
		Integer time = ConfigPropertieUtils.getLong("wait_pay_time", 30l).intValue();

		Date lastCreateTime = DateUtil.addMinute(new Date(), 0 - time);

		List<Integer> existOrderTypes = new ArrayList<>();
		existOrderTypes.add(1);
		existOrderTypes.add(2);
		List<ShopOrderDetail> orders = commonService.getList(ShopOrderDetail.class, "lastCreateTime", lastCreateTime,
				"status", 0, "existOrderTypes", existOrderTypes);

		if (orders != null && orders.size() > 0) {
			ShopMessageVo<String> result = cardOrderApi.cancelPuCardOrder(orders, 2, "过期未支付");
			if (!result.isResult()) {
				logger.error("取消过期未支付订单失败，原因：" + result.getErrorMessage());
			}
		}

		// 超时72小时未支付即过期的订单
		lastCreateTime = DateUtil.addMinute(new Date(), -1 * 72 * 60);
		orders = null;
		orders = commonService.getList(ShopOrderDetail.class, "lastCreateTime", lastCreateTime, "status", 0,
				"orderType", 3);
		if (orders != null && orders.size() > 0) {
			ShopMessageVo<String> result = auctionApi.cancelAuctionOrder(orders);
			if (!result.isResult()) {
				logger.error("取消过期未支付竞拍订单失败，原因：" + result.getErrorMessage());
			}
		}

		logger.info("取消过期未支付订单任务-结束");
	}

	/**
	 * 调用钱包接口重试
	 */
	@Scheduled(cron = "0 0/10 * * * ? ")
	public void bwsAPIReTry() {
		if ("1".equals(jobLock)) {
			return;
		}
		/*
		 * List<ShopBstkRecord> tasks =
		 * commonService.getList(ShopBstkRecord.class, "status", 2); if (tasks
		 * != null && tasks.size() > 0) { for (ShopBstkRecord record : tasks) {
		 * ShopBstkRecord upRecord = new ShopBstkRecord();
		 * upRecord.setId(record.getId()); // 执行任务 try {
		 * 
		 *//**
			 * 调用接口
			 */
		/*
		 * String response = HttpClientUtils.bwsPost(record.getCallUrl(),
		 * JSONObject.parseObject(record.getCallReq(), JSONObject.class));
		 * upRecord.setCallResp(response); BWSWalletResponse result =
		 * JSONObject.parseObject(response, BWSWalletResponse.class);
		 * 
		 *//**
			 * 创建钱包
			 */
		/*
		 * if (record.getCallType() == 1) {
		 * 
		 * BWSWalletCreateResponseVo resultVo =
		 * JSONObject.parseObject(result.getData(),
		 * BWSWalletCreateResponseVo.class);
		 * 
		 * // 调用接口更新 // 插入 wallet ShopBstkWallet upWallet = new
		 * ShopBstkWallet(); upWallet.setOwnerId(record.getOwnerId());
		 * upRecord.setOwnerType(record.getOwnerType());
		 * upWallet.setWalletAddress(resultVo.getWallet());
		 * upWallet.setPublicKeyAddr(resultVo.getMainAddr());
		 * upWallet.setCreateTime(DateUtil.getCurrentDateTime());
		 * commonService.save(upWallet);
		 * 
		 * }
		 *//**
			 * 充值 消费
			 *//*
			 * else if (record.getCallType() == 2 || record.getCallType() == 3)
			 * { BWSWalletTransferResponseVo voResult =
			 * JSONObject.parseObject(result.getData(),
			 * BWSWalletTransferResponseVo.class);
			 * 
			 * // 调用接口更新
			 * if(record.getBusinessId()!=null&&record.getBusinessId()>0) {
			 * ShopOrder upOrder = new ShopOrder();
			 * upOrder.setId(record.getBusinessId());
			 * upOrder.setTransactionNo(voResult.getTxid());
			 * commonService.update(upOrder); } }
			 * 
			 * upRecord.setStatus(1); upRecord.setRemark("自动任务调用");
			 * upRecord.setCreateTime(DateUtil.getCurrentDateTime());
			 * commonService.update(upRecord);
			 * 
			 * } catch (Exception ex) { logger.error("自动任务调用bws接口失败", ex);
			 * upRecord.setRetryCount(record.getRetryCount() + 1);
			 * upRecord.setStatus(2); upRecord.setRemark("自动任务调用");
			 * upRecord.setCreateTime(DateUtil.getCurrentDateTime());
			 * commonService.update(upRecord);
			 * 
			 * }
			 * 
			 * } }
			 */
	}

	/**
	 * 系统BWS服务告警
	 */
	@Scheduled(cron = "0 0/10 * * * ? ")
	public void bwsWarn() {
		if ("1".equals(jobLock)) {
			return;
		}

		// 钱包余额告警
		try {
			// 从配置文件读取钱包告警余额
			BigDecimal walletAccount = new BigDecimal(ConfigPropertieUtils.getString("bws.walletAccount"));
			String calcForceWalletId = ConfigPropertieUtils.getString("cal.force.serverwallet");
			String yuandianWalletId = ConfigPropertieUtils.getString("bws.serverwallet");
			String bstkWalletId = ConfigPropertieUtils.getString("bstk.serverwallet");
			String receiverPhoneNumber = ConfigPropertieUtils.getString("bws.walletWarnReceiveMobile");

			// 查询余额
			String transNo = SnoGerUtil.getUUID();
			BigDecimal calcForceWalletBalance = bwsService.walletBalance(transNo, calcForceWalletId);
			BigDecimal yuandianWalletBalance = bwsService.walletBalance(transNo, yuandianWalletId);
			BigDecimal bstkWalletBalance = bwsService.walletBalance(transNo, bstkWalletId);
			logger.info("算力包钱包余额：" + calcForceWalletBalance);
			logger.info("源点钱包余额：" + yuandianWalletBalance);
			logger.info("BSTK钱包余额：" + bstkWalletBalance);
			// 商户BSTK钱包余额
			List<ShopWallet> wallets = commonService.getList(ShopWallet.class, "ownerType", 2);
			BigDecimal totalBalance = BigDecimal.ZERO;
			for (ShopWallet w : wallets) {
				BigDecimal balan = bwsService.walletBalance(SnoGerUtil.getUUID(), w.getWalletAddress());
				totalBalance = totalBalance.add(balan);
			}

			logger.info("合计商户BSTK钱包余额：" + totalBalance);
			logger.info("算力包钱包限定额：" + walletAccount);
			if (calcForceWalletBalance.compareTo(walletAccount) < 0) {
				// 发送短信
				ArrayList<String> params = new ArrayList<>();
				params.add(calcForceWalletBalance.toString());
				QCloudSMSUtils.sendSMS("walletBalanceAlert", receiverPhoneNumber, params);
			}
		} catch (Exception ex) {
			logger.error("查询钱包余额告警失败", ex);
		}

		// 重试次数告警
		try {
			Integer retryCount = Integer.parseInt(ConfigPropertieUtils.getString("bws.retryCount"));
			List<ShopBstkRecord> tasks = commonService.getList(ShopBstkRecord.class, "status", 2, "retryCount",
					retryCount);
			logger.debug("重试任务：" + JSONObject.toJSONString(tasks));
			if (tasks != null && tasks.size() > 0) {
				String receiverPhoneNumber = ConfigPropertieUtils.getString("bws.walletRestWarnReceiveMobile");
				// 发送短信
				ArrayList<String> params = new ArrayList<>();
				QCloudSMSUtils.sendSMS("walletBwsRetryAlert", receiverPhoneNumber, params);
			}
		} catch (Exception ex) {
			logger.error("查询钱包BWS调用次数失败", ex);
		}
	}

	/**
	 * 产品虚拟销售量定时任务
	 */
	@Scheduled(cron = "0 0 1 * * ? ")
	public void virtualSaleVolume() {
		if ("1".equals(jobLockVirtualSaleColume)) {
			return;
		}

		logger.info("产品虚拟销售量定时任务-开始");

		// 产品虚拟销售量定时任务
		try {
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String saleDate = sdf.format(now);

			List<ShopSaleVolume> list = commonService.getList(ShopSaleVolume.class, "saleDate", saleDate);
			List<ShopCommodity> commodityList = commonService.getList(ShopCommodity.class, "status", 1);
			List<ShopPuCardType> cardTypeList = commonService.getList(ShopPuCardType.class, "status", 1);

			// 商品类型：1-提货卡,2-溯源卡,3-零售商品
			// 零售商品虚拟销售量
			for (ShopCommodity c : commodityList) {
				Boolean exist = false;
				for (ShopSaleVolume s : list) {
					if (c.getId() == s.getCommodityId() && s.getSaleDate().equals(saleDate)
							&& s.getCommodityType() == 3) {
						exist = true;
						break;
					}
				}

				if (exist == false) {
					// 获取配置
					SaleVolumeConfigVo conf = GetSaleVolumeConfig(c.getId(), 3, c.getCommodityType());
					if ((conf.getOrderCountMin() == 0 && conf.getOrderCountMax() == 0)
							|| (conf.getSaleCountMin() == 0 && conf.getSaleCountMax() == 0)) {
						continue;
					}
					ShopSaleVolume sv = new ShopSaleVolume();
					sv.setMerchantId(c.getMerchantId());
					sv.setCommodityId(c.getId());
					sv.setCommodityType(3);
					sv.setSaleDate(saleDate);
					int randomOrder = SnoGerUtil.getRandomNumByRange(conf.getOrderCountMin(), conf.getOrderCountMax());
					sv.setOrderCount(randomOrder);
					int randomSale = SnoGerUtil.getRandomNumByRange(conf.getSaleCountMin(), conf.getSaleCountMax());
					sv.setSaleVolume(randomSale);

					commonService.save(sv);
				}
			}

			// 提货卡虚拟销售量
			for (ShopPuCardType c : cardTypeList) {
				Boolean exist = false;
				for (ShopSaleVolume s : list) {
					if (c.getId() == s.getCommodityId() && s.getSaleDate().equals(saleDate)
							&& s.getCommodityType() == 1) {
						exist = true;
						break;
					}
				}

				if (exist == false) {
					// 获取配置
					SaleVolumeConfigVo conf = GetSaleVolumeConfig(c.getId(), 1, 0);
					if ((conf.getOrderCountMin() == 0 && conf.getOrderCountMax() == 0)
							|| (conf.getSaleCountMin() == 0 && conf.getSaleCountMax() == 0)) {
						continue;
					}
					ShopSaleVolume sv = new ShopSaleVolume();
					sv.setMerchantId(c.getMerchantId());
					sv.setCommodityId(c.getId());
					sv.setCommodityType(1);
					sv.setSaleDate(saleDate);
					int randomOrder = SnoGerUtil.getRandomNumByRange(conf.getOrderCountMin(), conf.getOrderCountMax());
					sv.setOrderCount(randomOrder);
					int randomSale = SnoGerUtil.getRandomNumByRange(conf.getSaleCountMin(), conf.getSaleCountMax());
					sv.setSaleVolume(randomSale);

					commonService.save(sv);
				}
			}

			logger.info("产品虚拟销售量定时任务-结束");
		} catch (Exception ex) {
			logger.error("产品虚拟销售量定时任务失败", ex);
		}
	}

	/**
	 * 
	 * @param commodityId:0是默认值
	 * @param commodityType:商品类型：1-提货卡,2-溯源卡,3-零售商品
	 * @param productType:产品类型：1-区块链计算机,2-通用商品,3-算力服务；0是默认值，没有类型区分
	 * @return
	 */
	private SaleVolumeConfigVo GetSaleVolumeConfig(Long commodityId, Integer commodityType, Integer productType) {
		SaleVolumeConfigVo ret = null;
		List<SaleVolumeConfigVo> config = JSONObject.parseArray(virtualSaleColumeConfig, SaleVolumeConfigVo.class);
		SaleVolumeConfigVo def = null;
		for (SaleVolumeConfigVo vo : config) {
			if (vo.getCommodityType() == commodityType && vo.getCommodityId() == 0) {
				def = vo;
			}

			if (vo.getCommodityId() == commodityId && vo.getCommodityType() == commodityType
					&& vo.getProductType() == productType) {
				ret = vo;
				break;
			}
		}

		if (ret == null) {
			return def;
		} else {
			return ret;
		}

	}

	/**
	 * 执行算力服务收益
	 */
	@Scheduled(cron = "0 0 3 * * ? ")
	public void calcForceTask() {
		if ("1".equals(jobLock)) {
			return;
		}

		logger.info("执行算力服务收益-开始");
		try {
			String systemPublish = ConfigPropertieUtils.getString("system.publish");
			int diff = Integer.parseInt(ConfigPropertieUtils.getString("calc.force.task.date.diff"));
			Date now = new Date();
			Date dateDiff = now;
			if (diff != 0) {
				dateDiff = DateUtil.addDate(now, diff);
			}

			// 下一个收益日
			Date nextDate = DateUtil.addDate(dateDiff, 1);

			String taskDate = DateUtil.formatDateByFormat(dateDiff, "yyyy-MM-dd");
			String taskNextDate = DateUtil.formatDateByFormat(nextDate, "yyyy-MM-dd");

			// 状态:0-待执行，1-执行成功，2-执行失败，3-冻结
			List<ShopOrderCalcForceTask> tasks = commonService.getList(ShopOrderCalcForceTask.class, "planDate",
					taskDate, "status", 0);
			if (tasks == null || tasks.size() == 0) {
				return;
			}

			// bstk钱包集合
			List<Long> existOwnerIds = new ArrayList<>();
			existOwnerIds = tasks.stream().map(i -> i.getMemberId()).distinct().collect(Collectors.toList());
			List<ShopWallet> bstkWallets = commonService.getList(ShopWallet.class, "ownerType", 1, "existOwnerIds",
					existOwnerIds);
			Map<Long, ShopWallet> bstkWalletMap = new HashMap<Long, ShopWallet>();
			for (ShopWallet w : bstkWallets) {
				bstkWalletMap.put(w.getOwnerId(), w);
			}

			// 钱包余额map
			HashMap<Long, BigDecimal> balancesMap = new HashMap<Long, BigDecimal>();

			// 下一个收益日任务集合
			List<ShopOrderCalcForceTask> nextTasks = commonService.getList(ShopOrderCalcForceTask.class, "planDate",
					taskNextDate);
			Map<Long, ShopOrderCalcForceTask> nextTasksMap = new HashMap<Long, ShopOrderCalcForceTask>();
			for (ShopOrderCalcForceTask t : nextTasks) {
				nextTasksMap.put(t.getOrderCalcForceId(), t);
			}

			// 批量发放收益
			List<ReceiverVo> receivers = new ArrayList<ReceiverVo>();
			String transactionNo;
			String taskIds = "";
			for (ShopOrderCalcForceTask task : tasks) {
				Long key = task.getMemberId();
				BigDecimal income = BigDecimal.valueOf(task.getBstkAmount());
				ReceiverVo vo = new ReceiverVo();
				vo.setAmount(income);
				vo.setRecvAddr(task.getPublicKeyAddr());
				receivers.add(vo);

				taskIds = taskIds + task.getId().toString() + ",";

				BigDecimal curBalance = BigDecimal.ZERO;
				if (balancesMap.containsKey(key)) {
					curBalance = balancesMap.get(key);
				} else {
					if (bstkWalletMap.containsKey(key)) {
						ShopWallet wallet = bstkWalletMap.get(key);
						curBalance = bwsService.walletBalance(SnoGerUtil.getUUID(), wallet.getWalletAddress());
					} else {
						// 创建轻钱包（对应BSTK）
						BWSWalletCreateResponseVo resp1 = bwsService.walletCreate(SnoGerUtil.getUUID(), key, 1);
						if (resp1 != null) {
							// 插入 wallet
							ShopWallet wallet = new ShopWallet();
							wallet.setOwnerId(key);
							wallet.setOwnerType(1);
							wallet.setWalletAddress(resp1.getWallet());
							wallet.setPublicKeyAddr(resp1.getMainAddr());
							wallet.setTotalIncome(0d);
							wallet.setTotalExpend(0d);
							wallet.setBalance(0d);
							wallet.setCreateTime(DateUtil.getCurrentDateTime());
							commonService.save(wallet);
							bstkWalletMap.put(key, wallet);
						}
						curBalance = BigDecimal.ZERO;
					}
				}

				BigDecimal newBalance = curBalance.add(income);
				task.setBalance(newBalance);
				balancesMap.put(key, newBalance);
			}

			// 调用钱包接口
			if (systemPublish.trim().toLowerCase().equals("dev") || systemPublish.trim().toLowerCase().equals("test")) {
				transactionNo = "模拟数据不真实发放BSTK";
			} else {
				// 上传BSTK钱包交易，合并发放收益
				transactionNo = bwsService.walletRecharge(BWSProperties.P_CALCFORCE, SnoGerUtil.getUUID(), receivers,
						"taskId集合：" + taskIds);
			}

			for (ShopOrderCalcForceTask task : tasks) {
				// 更新状态：0-待执行，1-执行成功，2-执行失败，3-冻结
				if (ProUtility.isNotNull(transactionNo)) {
					// 获取算力服务
					ShopOrderCalcForce service = commonService.get(task.getOrderCalcForceId(),
							ShopOrderCalcForce.class);

					task.setExecutionTime(new Date());
					task.setStatus(1);
					task.setTransactionNo(transactionNo);
					commonService.update(task);

					// 更新算力服务
					ShopOrderCalcForce upService = new ShopOrderCalcForce();
					upService.setId(task.getOrderCalcForceId());
					upService.setTotalIncomeBstk(BigDecimal.valueOf(service.getTotalIncomeBstk())
							.add(BigDecimal.valueOf(task.getBstkAmount())).doubleValue());

					// 判断已完成状态:0-待收益，1-收益中，2-已完成，3-冻结
					if (service.getValidityTo().equals(task.getPlanDate())) {
						// 已完成
						upService.setStatus(2);
						upService.setEstimateIncomeBstk(new Double(0));
					} else {
						// 收益中
						upService.setStatus(1);
						if (nextTasksMap.containsKey(task.getOrderCalcForceId())) {
							BigDecimal r = SnoGerUtil.getRandomBigDecimalByRange(new BigDecimal(-3), new BigDecimal(3));
							BigDecimal estimateIncomeBstk = r.add(
									BigDecimal.valueOf(nextTasksMap.get(task.getOrderCalcForceId()).getBstkAmount()));
							upService.setEstimateIncomeBstk(estimateIncomeBstk.doubleValue());
						}
					}

					// 注意对象默认值的问题
					commonService.update(upService);

					ShopWallet wallet = bstkWalletMap.get(task.getMemberId());
					if (task.getPublicKeyAddr().equals(wallet.getPublicKeyAddr())) // 收益地址是bstk钱包地址
					{
						// 保存钱包明细记录
						ShopWalletBill bill = new ShopWalletBill();
						// 收入
						bill.setAmount(task.getBstkAmount());
						bill.setBalance(task.getBalance().doubleValue());
						bill.setBusinessId(task.getId());
						bill.setBusinessType(5);
						bill.setBusinessTitle("算力包收益");
						bill.setCreateTime(DateUtil.getCurrentDateTime());
						bill.setInOutType(1); // 收支类型：1-收入；2-支出
						bill.setOwnerId(task.getMemberId());
						bill.setOwnerType(1);
						bill.setRemark(service.getCommodityName());
						bill.setTransactionNo(transactionNo);
						bill.setWalletId(wallet.getId());

						commonService.save(bill);
					}

					// 发送消息
					systemApi.sendSystemMessage(task.getMemberId(), 1, "收益通知", "算力包" + service.getPackageNo() + "-"
							+ task.getPlanDate() + ",本次收益" + task.getBstkAmount() + "BSTK已到账。");

				} else {
					task.setExecutionTime(new Date());
					task.setStatus(2);
					commonService.update(task);
				}
			}

			logger.info("执行算力服务收益-完成");
		} catch (Exception ex) {
			logger.error("执行算力服务收益失败", ex);
		}
	}
}
