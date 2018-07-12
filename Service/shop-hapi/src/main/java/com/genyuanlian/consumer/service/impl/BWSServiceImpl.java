package com.genyuanlian.consumer.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
import com.genyuanlian.consumer.shop.vo.ReceiverVo;
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
	public BWSWalletCreateResponseVo walletCreate(String transactionNo, Long ownerId, int ownerType) {
		try {
			BWSWalletCreateResponseVo resultVo = null;
			// 保存数据到库
			ShopBstkRecord reocrd = new ShopBstkRecord();
			reocrd.setTransactionNo(transactionNo);
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
	public BigDecimal walletBalance(String transactionNo, String walletId) {

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
			return new BigDecimal(voResult.getAvailableAmount() / 100000000l);
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
	public String walletRecharge(String transactionNo, Long businessId, Long ownerId, int ownerType,
			String walletMainAddress, BigDecimal amount) {
		try {
			String resultVo = null;
			// 保存数据到库
			ShopBstkRecord reocrd = new ShopBstkRecord();
			reocrd.setTransactionNo(transactionNo);
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
				params.put("amount", amount.multiply(new BigDecimal(100000000)));
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
	 * 同时将多个地址进行充值或者发放收益
	 * 
	 * @param receivers
	 * @param remark
	 * @return
	 */
	@Override
	public String walletRecharge(String transactionNo, List<ReceiverVo> receivers, String remark) {
		try {
			String resultVo = null;
			// 保存数据到库
			ShopBstkRecord reocrd = new ShopBstkRecord();
			reocrd.setTransactionNo(transactionNo);
			reocrd.setBusinessId(0L);
			reocrd.setOwnerId(0L);
			reocrd.setOwnerType(0);
			reocrd.setCallType(4);
			reocrd.setCreateTime(DateUtil.getCurrentDateTime());
			reocrd.setCallUrl(BWSProperties.bswWalletTransferMultiAPI);

			try {
				// 将amount 单独处理
				List<ReceiverVo> vos = new ArrayList<ReceiverVo>();
				for (ReceiverVo vo : receivers) {
					ReceiverVo newVo = new ReceiverVo();
					newVo.setRecvAddr(vo.getRecvAddr());
					newVo.setAmount(vo.getAmount().multiply(new BigDecimal(100000000)));
					vos.add(newVo);

				}
				/**
				 * 模拟提交，返回值
				 */
				JSONObject params = new JSONObject();
				params.put("senderWallet", BWSProperties.serverWallet);
				params.put("receivers", vos);
				reocrd.setCallReq(params.toJSONString());
				String response = HttpClientUtils.bwsPost(BWSProperties.bswWalletTransferMultiAPI, params);
				reocrd.setCallResp(response);
				reocrd.setStatus(1);
				BWSWalletResponse result = JSONObject.parseObject(response, BWSWalletResponse.class);
				BWSWalletTransferResponseVo voResult = JSONObject.parseObject(result.getData(),
						BWSWalletTransferResponseVo.class);
				resultVo = voResult.getTxid();

			} catch (Exception ex) {
				logger.error("调用bws接口多账户转账失败.", ex);
				reocrd.setStatus(2);

			}
			commonDao.save(reocrd);
			return resultVo;

		} catch (Exception ex) {
			logger.error("调用bws接口多账户转账失败.", ex);
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
	public String walletConsume(String transactionNo, Long businessId, Long ownerId, int ownerType, String walletId,
			BigDecimal amount) {
		try {
			String resultVo = null;
			// 保存数据到库
			ShopBstkRecord reocrd = new ShopBstkRecord();
			reocrd.setTransactionNo(transactionNo);
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
				params.put("amount", amount.multiply(new BigDecimal(100000000)));
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
//			String[] arr = "1,0.2".trim().split(",");
//			System.out.print(arr);

			// String response =
			// HttpClientUtils.bwsPost("http://service.genyuanlian.com/api/wallet/create",
			// null);
			// System.out.print(response);
			// BWSWalletResponse result = JSON.parseObject(response,
			// BWSWalletResponse.class);
			// BWSWalletCreateResponseVo voResult =
			// JSON.parseObject(result.getData(),
			// BWSWalletCreateResponseVo.class);
			// System.out.print(voResult);

//			 JSONObject params=new JSONObject();
//			 params.put("wallet","{\"coin\":\"btc\",\"network\":\"livenet\",\"xPrivKey\":\"xprv9s21ZrQH143K2DX8JhwhKEq2Qq9he3D6sU5t1sAmvyiPNxv41fLWJDUsamP21Svz7xX9oLPEB33XHez5yJ86pnrKrKbCzSB1BMpki7F6yXo\",\"xPubKey\":\"xpub6DBj9cJ1AJfph1mKnHzsLHt5dczNwZfBRgpVMufJEn6Mhz6WjuVTLy8ysJBZuWgjXQ4tD1VVnR1ciNwEgyEzSyX3dhZxRhbuyYWh3Yaqip3\",\"requestPrivKey\":\"a35c1831aee6f8c948529f6c5df8a86904598aefc67009d63b860a4ce11b5b40\",\"requestPubKey\":\"032ceeb71892eda43ed29390ef5dfc189889e98e7e5b489f09b7869ab32ede7160\",\"copayerId\":\"95e9fe994653a2d3101a94eb915544d67cef192d073e1fe14ca087a8dc5f350a\",\"publicKeyRing\":[{\"xPubKey\":\"xpub6DBj9cJ1AJfph1mKnHzsLHt5dczNwZfBRgpVMufJEn6Mhz6WjuVTLy8ysJBZuWgjXQ4tD1VVnR1ciNwEgyEzSyX3dhZxRhbuyYWh3Yaqip3\",\"requestPubKey\":\"032ceeb71892eda43ed29390ef5dfc189889e98e7e5b489f09b7869ab32ede7160\"}],\"walletId\":\"7e4e2205-53dc-4c5c-943d-3893d44e41b0\",\"walletName\":\"BSTK Wallet\",\"m\":1,\"n\":1,\"walletPrivKey\":\"a2ce06e727fc62dab1ec83e010a0476dddb3cf5b852ecb415b0aed467bb2bfcf\",\"personalEncryptingKey\":\"hcaEz1cvXHs6FF+hxAlIZQ==\",\"sharedEncryptingKey\":\"uqMJbO6XFhsz7Exxn74AuA==\",\"mnemonic\":\"哀 寄 蒋 染 奴 发 师 坝 料 平 美 师\",\"entropySource\":\"c743c246ee6146747a322d2a9b3148c50717e5cb77b5f2302558d292f5b12df9\",\"mnemonicHasPassphrase\":false,\"derivationStrategy\":\"BIP44\",\"account\":0,\"compliantDerivation\":true,\"addressType\":\"P2PKH\"}");
//			 String response =
//			 HttpClientUtils.bwsPost("http://service.genyuanlian.com/api/wallet/balance",params);
//			 BWSWalletResponse result =
//			 JSONObject.parseObject(response,BWSWalletResponse.class);
//			 System.out.println(result.getData());

//			/**
//			 * 模拟提交，返回值
//			 */
			 JSONObject params = new JSONObject();
			 params.put("senderWallet","{\"coin\":\"btc\",\"network\":\"livenet\",\"xPrivKey\":\"xprv9s21ZrQH143K2DX8JhwhKEq2Qq9he3D6sU5t1sAmvyiPNxv41fLWJDUsamP21Svz7xX9oLPEB33XHez5yJ86pnrKrKbCzSB1BMpki7F6yXo\",\"xPubKey\":\"xpub6DBj9cJ1AJfph1mKnHzsLHt5dczNwZfBRgpVMufJEn6Mhz6WjuVTLy8ysJBZuWgjXQ4tD1VVnR1ciNwEgyEzSyX3dhZxRhbuyYWh3Yaqip3\",\"requestPrivKey\":\"a35c1831aee6f8c948529f6c5df8a86904598aefc67009d63b860a4ce11b5b40\",\"requestPubKey\":\"032ceeb71892eda43ed29390ef5dfc189889e98e7e5b489f09b7869ab32ede7160\",\"copayerId\":\"95e9fe994653a2d3101a94eb915544d67cef192d073e1fe14ca087a8dc5f350a\",\"publicKeyRing\":[{\"xPubKey\":\"xpub6DBj9cJ1AJfph1mKnHzsLHt5dczNwZfBRgpVMufJEn6Mhz6WjuVTLy8ysJBZuWgjXQ4tD1VVnR1ciNwEgyEzSyX3dhZxRhbuyYWh3Yaqip3\",\"requestPubKey\":\"032ceeb71892eda43ed29390ef5dfc189889e98e7e5b489f09b7869ab32ede7160\"}],\"walletId\":\"7e4e2205-53dc-4c5c-943d-3893d44e41b0\",\"walletName\":\"BSTK Wallet\",\"m\":1,\"n\":1,\"walletPrivKey\":\"a2ce06e727fc62dab1ec83e010a0476dddb3cf5b852ecb415b0aed467bb2bfcf\",\"personalEncryptingKey\":\"hcaEz1cvXHs6FF+hxAlIZQ==\",\"sharedEncryptingKey\":\"uqMJbO6XFhsz7Exxn74AuA==\",\"mnemonic\":\"哀 寄 蒋 染 奴 发 师 坝 料 平 美 师\",\"entropySource\":\"c743c246ee6146747a322d2a9b3148c50717e5cb77b5f2302558d292f5b12df9\",\"mnemonicHasPassphrase\":false,\"derivationStrategy\":\"BIP44\",\"account\":0,\"compliantDerivation\":true,\"addressType\":\"P2PKH\"}");
			 params.put("receiverAddress","1PvUikHWcaeDTSSG95xY8trH3UqH9dBZ7t");
			 params.put("amount", 100000);
			 String response =
			 HttpClientUtils.bwsPost("http://service.genyuanlian.com/api/wallet/transfer",
			 params);
			 System.out.print(response);
			// BWSWalletResponse result = JSONObject.parseObject(response,
			// BWSWalletResponse.class);
			//
			// BWSWalletTransferResponseVo voResult =
			// JSONObject.parseObject(result.getData(),
			// BWSWalletTransferResponseVo.class);
			// System.out.print(voResult.getTxid());

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
