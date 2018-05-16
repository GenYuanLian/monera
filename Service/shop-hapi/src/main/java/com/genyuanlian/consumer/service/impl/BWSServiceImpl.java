package com.genyuanlian.consumer.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.genyuanlian.consumer.service.IBWSService;
import com.genyuanlian.consumer.shop.model.ShopBstkRecord;
import com.genyuanlian.consumer.shop.utils.BWSProperties;
import com.genyuanlian.consumer.shop.vo.BWSWalletBalanceResponseVo;
import com.genyuanlian.consumer.shop.vo.BWSWalletCreateResponseVo;
import com.genyuanlian.consumer.shop.vo.BWSWalletResponse;
import com.genyuanlian.consumer.shop.vo.BWSWalletTransferResponseVo;
import com.hnair.consumer.dao.spi.ICommonDao;
import com.hnair.consumer.utils.DateUtil;
import com.hnair.consumer.utils.HttpClientUtils;

/**
 * BWS 调用Service Created by hunter.wang on 2018/5/10.
 */
@Service("bwsService")
public class BWSServiceImpl implements IBWSService {
	private static Logger logger = LoggerFactory.getLogger(BWSServiceImpl.class);

	@Resource
	private ICommonDao commonDao;

	/**
	 * 创建钱包 businessId 预留字段
	 * 
	 * @return
	 */
	@Override
	public BWSWalletCreateResponseVo walletCreate(Long ownerId, int ownerType) {
		try {
			BWSWalletCreateResponseVo resultVo = null;
			// 保存数据到库
			ShopBstkRecord reocrd = new ShopBstkRecord();
			reocrd.setOwnerId(ownerId);
			reocrd.setOwnerType(ownerType);
			reocrd.setCallType(1);
			reocrd.setCreateTime(DateUtil.getCurrentDateTime());
			reocrd.setCallUrl(BWSProperties.bwsWalletCreateAPI);

			try {
				/**
				 * 模拟提交，返回值
				 */
				String response = HttpClientUtils.bwsPost(BWSProperties.bwsWalletCreateAPI, null);
				reocrd.setCallResp(response);
				reocrd.setStatus(1);
				BWSWalletResponse result = JSONObject.parseObject(response, BWSWalletResponse.class);
				resultVo = JSONObject.parseObject(result.getData(), BWSWalletCreateResponseVo.class);

			} catch (Exception ex) {
				logger.error("调用bws接口创建钱包失败.", ex);
				reocrd.setStatus(2);
			}
			commonDao.save(reocrd);
			return resultVo;

		} catch (Exception ex) {
			logger.error("保存创建钱包任务失败.", ex);
		}
		return null;

	}

	/**
	 * 查询钱包余额
	 * 
	 * @param walletId
	 * @return
	 */
	@Override
	public Double walletBalance(String walletId) {

		try {

			/**
			 * 模拟提交，返回值
			 */
			JSONObject params = new JSONObject();
			params.put("wallet", walletId);
			String response = HttpClientUtils.bwsPost(BWSProperties.bswWalletBalanceAPI, params);
			BWSWalletResponse result = JSONObject.parseObject(response, BWSWalletResponse.class);
			BWSWalletBalanceResponseVo voResult = JSONObject.parseObject(result.getData(),
					BWSWalletBalanceResponseVo.class);
			return voResult.getAvailableAmount() / 100000000;
		} catch (Exception ex) {
			logger.error("查询钱包余额失败.", ex);
		}
		return null;
	}

	/**
	 * 钱包充值
	 * 
	 * @param walletMainAddress
	 * @param amount
	 * @return
	 */
	@Override
	public String walletRecharge(Long businessId, Long ownerId, int ownerType, String walletMainAddress,
			Double amount) {
		try {
			String resultVo = null;
			// 保存数据到库
			ShopBstkRecord reocrd = new ShopBstkRecord();
			reocrd.setBusinessId(businessId);
			reocrd.setOwnerId(ownerId);
			reocrd.setOwnerType(ownerType);
			reocrd.setCallType(2);
			reocrd.setCreateTime(DateUtil.getCurrentDateTime());
			reocrd.setCallUrl(BWSProperties.bswWalletTransferAPI);

			try {
				/**
				 * 模拟提交，返回值
				 */
				JSONObject params = new JSONObject();
				params.put("senderWallet", BWSProperties.serverWallet);
				params.put("receiverAddress", walletMainAddress);
				params.put("amount", amount * 100000000l);
				reocrd.setCallReq(params.toJSONString());
				String response = HttpClientUtils.bwsPost(BWSProperties.bswWalletTransferAPI, params);
				reocrd.setCallResp(response);
				reocrd.setStatus(1);
				BWSWalletResponse result = JSONObject.parseObject(response, BWSWalletResponse.class);
				BWSWalletTransferResponseVo voResult = JSONObject.parseObject(result.getData(),
						BWSWalletTransferResponseVo.class);
				resultVo = voResult.getTxid();

			} catch (Exception ex) {
				logger.error("调用bws接口充值失败.", ex);
				reocrd.setStatus(2);

			}
			commonDao.save(reocrd);
			return resultVo;

		} catch (Exception ex) {
			logger.error("保存充值任务失败.", ex);
		}
		return null;

	}

	/**
	 * 钱包消费
	 * 
	 * @param walletId
	 * @param amount
	 * @return
	 */
	@Override
	public String walletConsume(Long businessId, Long ownerId, int ownerType, String walletId, Double amount) {
		try {
			String resultVo = null;
			// 保存数据到库
			ShopBstkRecord reocrd = new ShopBstkRecord();
			reocrd.setBusinessId(businessId);
			reocrd.setOwnerId(ownerId);
			reocrd.setOwnerType(ownerType);
			reocrd.setCallType(3);
			reocrd.setCreateTime(DateUtil.getCurrentDateTime());
			reocrd.setCallUrl(BWSProperties.bswWalletTransferAPI);

			try {
				/**
				 * 模拟提交，返回值
				 */
				JSONObject params = new JSONObject();
				params.put("senderWallet", walletId);
				params.put("receiverAddress", BWSProperties.serverWalletMainAddress);
				params.put("amount", amount * 100000000l);
				reocrd.setCallReq(params.toJSONString());
				String response = HttpClientUtils.bwsPost(BWSProperties.bswWalletTransferAPI, params);
				reocrd.setCallResp(response);
				reocrd.setStatus(1);
				BWSWalletResponse result = JSONObject.parseObject(response, BWSWalletResponse.class);
				BWSWalletTransferResponseVo voResult = JSONObject.parseObject(result.getData(),
						BWSWalletTransferResponseVo.class);
				resultVo = voResult.getTxid();

			} catch (Exception ex) {
				logger.error("调用bws接口充值失败.", ex);
				reocrd.setStatus(2);

			}
			commonDao.save(reocrd);
			return resultVo;

		} catch (Exception ex) {
			logger.error("保存充值任务失败.", ex);
		}
		return null;

	}

	public static void main(String[] args) {
		try {
			// String response =
			// HttpClientUtils.bwsPost("http://service.genyuanlian.com/api/wallet/create",null);
			// BWSWalletResponse result =
			// JSON.parseObject(response,BWSWalletResponse.class);
			//
			// BWSWalletCreateResponseVo voResult =
			// JSON.parseObject(result.getData(),BWSWalletCreateResponseVo.class);
			// System.out.print( voResult);

			 JSONObject params=new JSONObject();
			 params.put("wallet","{\"coin\":\"btc\",\"network\":\"livenet\",\"xPrivKey\":\"xprv9s21ZrQH143K2XzEhAapHG9SBRM4gpjwNANqJ3bRTVq3L6i9PB4J4oNvnBFAQ5DndjiDrXhfvccSwJqqTRN2QBrsWxBw3pXqRXCoi96vjJA\",\"xPubKey\":\"xpub6CcjgCQ8L5jYkcx4y1e3f1whRxvx1wdPHN45kTfR9tJeFfAkordoqyBKyFF9soKuuX6fLuzJkRajw7yP7VUdg9Vr3VVacxgPSkyqVY5ipKQ\",\"requestPrivKey\":\"7dbd8f878a18028475a841cd5fea25b27ba153a476b4e6da395148e426b2ffa5\",\"requestPubKey\":\"03324a8ccff4cdb4eb208e584fbb331a722e56e82036509d95e7fcbfaa14ae9a48\",\"copayerId\":\"c9b852df7c533039814fb4df93f0a15091abce06f4dc797bd58112483fd3ac75\",\"publicKeyRing\":[{\"xPubKey\":\"xpub6CcjgCQ8L5jYkcx4y1e3f1whRxvx1wdPHN45kTfR9tJeFfAkordoqyBKyFF9soKuuX6fLuzJkRajw7yP7VUdg9Vr3VVacxgPSkyqVY5ipKQ\",\"requestPubKey\":\"03324a8ccff4cdb4eb208e584fbb331a722e56e82036509d95e7fcbfaa14ae9a48\"}],\"walletId\":\"903089b5-76ad-4c97-9096-b3078b199a8a\",\"walletName\":\"BSTK Wallet\",\"m\":1,\"n\":1,\"walletPrivKey\":\"282030920c602ea368e53b0140ae27ef888d1eb3960e58f5ed5cae7d149bbe83\",\"personalEncryptingKey\":\"yqZVqMVjXO1oy45GaptzjA==\",\"sharedEncryptingKey\":\"EU06xNl9u8kJL46Zsappwg==\",\"mnemonic\":\"�� �� �� �� �� �� �� �� �� �� �� ʶ\",\"entropySource\":\"b18872e8399bc8fad831585a3efeef158a10b2df4b49a9cab0afda5bbd47b8d4\",\"mnemonicHasPassphrase\":false,\"derivationStrategy\":\"BIP44\",\"account\":0,\"compliantDerivation\":true,\"addressType\":\"P2PKH\"}");
			 String response =
			 HttpClientUtils.bwsPost("http://service.genyuanlian.com/api/wallet/balance",params);
			 BWSWalletResponse result =
			 JSONObject.parseObject(response,BWSWalletResponse.class);
			 System.out.println(result.getData());

			/**
			 * 模拟提交，返回值
			 */
//			JSONObject params = new JSONObject();
//			params.put("senderWallet",
//					"{\"coin\":\"btc\",\"network\":\"livenet\",\"xPrivKey\":\"xprv9s21ZrQH143K2XzEhAapHG9SBRM4gpjwNANqJ3bRTVq3L6i9PB4J4oNvnBFAQ5DndjiDrXhfvccSwJqqTRN2QBrsWxBw3pXqRXCoi96vjJA\",\"xPubKey\":\"xpub6CcjgCQ8L5jYkcx4y1e3f1whRxvx1wdPHN45kTfR9tJeFfAkordoqyBKyFF9soKuuX6fLuzJkRajw7yP7VUdg9Vr3VVacxgPSkyqVY5ipKQ\",\"requestPrivKey\":\"7dbd8f878a18028475a841cd5fea25b27ba153a476b4e6da395148e426b2ffa5\",\"requestPubKey\":\"03324a8ccff4cdb4eb208e584fbb331a722e56e82036509d95e7fcbfaa14ae9a48\",\"copayerId\":\"c9b852df7c533039814fb4df93f0a15091abce06f4dc797bd58112483fd3ac75\",\"publicKeyRing\":[{\"xPubKey\":\"xpub6CcjgCQ8L5jYkcx4y1e3f1whRxvx1wdPHN45kTfR9tJeFfAkordoqyBKyFF9soKuuX6fLuzJkRajw7yP7VUdg9Vr3VVacxgPSkyqVY5ipKQ\",\"requestPubKey\":\"03324a8ccff4cdb4eb208e584fbb331a722e56e82036509d95e7fcbfaa14ae9a48\"}],\"walletId\":\"903089b5-76ad-4c97-9096-b3078b199a8a\",\"walletName\":\"BSTK Wallet\",\"m\":1,\"n\":1,\"walletPrivKey\":\"282030920c602ea368e53b0140ae27ef888d1eb3960e58f5ed5cae7d149bbe83\",\"personalEncryptingKey\":\"yqZVqMVjXO1oy45GaptzjA==\",\"sharedEncryptingKey\":\"EU06xNl9u8kJL46Zsappwg==\",\"mnemonic\":\"�� �� �� �� �� �� �� �� �� �� �� ʶ\",\"entropySource\":\"b18872e8399bc8fad831585a3efeef158a10b2df4b49a9cab0afda5bbd47b8d4\",\"mnemonicHasPassphrase\":false,\"derivationStrategy\":\"BIP44\",\"account\":0,\"compliantDerivation\":true,\"addressType\":\"P2PKH\"}");
//			params.put("receiverAddress", "15LzQP7ofCCFSf2neBAFPvz8Mu4dpCnnUb");
//			params.put("amount", 1000011000l);
//			String response = HttpClientUtils.bwsPost("http://service.genyuanlian.com/api/wallet/transfer", params);
//			System.out.print(response);
//			BWSWalletResponse result = JSONObject.parseObject(response, BWSWalletResponse.class);
//
//			BWSWalletTransferResponseVo voResult = JSONObject.parseObject(result.getData(),
//					BWSWalletTransferResponseVo.class);
//			System.out.print(voResult.getTxid());

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
