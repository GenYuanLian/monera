package com.genyuanlian.consumer.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
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
	 * @param 平台钱包类型:p_yuandian,p_bstk,p_calcforce
	 * @param walletMainAddress
	 * @param amount
	 * @return
	 */
	@Override
	public String walletRecharge(String p_type, String transactionNo, Long businessId, Long ownerId, int ownerType,
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
				if (p_type.equals(BWSProperties.P_YUANDIAN)) {
					params.put("senderWallet", BWSProperties.serverWallet);
				} else if (p_type.equals(BWSProperties.P_BSTK)) {
					params.put("senderWallet", BWSProperties.bstkWallet);
				} else if (p_type.equals(BWSProperties.P_CALCFORCE)) {
					params.put("senderWallet", BWSProperties.calcForceWallet);
				}

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
	 * @param 平台钱包类型:p_yuandian,p_bstk,p_calcforce
	 * @param receivers
	 * @param remark
	 * @return
	 */
	@Override
	public String walletRecharge(String p_type, String transactionNo, List<ReceiverVo> receivers, String remark) {
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
			reocrd.setRemark(remark);

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
				if (p_type.equals(BWSProperties.P_YUANDIAN)) {
					params.put("senderWallet", BWSProperties.serverWallet);
				} else if (p_type.equals(BWSProperties.P_BSTK)) {
					params.put("senderWallet", BWSProperties.bstkWallet);
				} else if (p_type.equals(BWSProperties.P_CALCFORCE)) {
					params.put("senderWallet", BWSProperties.calcForceWallet);
				}

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
	 * @param 平台钱包类型:p_yuandian,p_bstk,p_calcforce
	 * @param walletId
	 * @param amount
	 * @return
	 */
	@Override
	public String walletConsume(String p_type, String transactionNo, Long businessId, Long ownerId, int ownerType,
			String walletId, BigDecimal amount) {
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
				if (p_type.equals(BWSProperties.P_YUANDIAN)) {
					params.put("receiverAddress", BWSProperties.serverWalletMainAddress);
				} else if (p_type.equals(BWSProperties.P_BSTK)) {
					params.put("receiverAddress", BWSProperties.bstkWalletMainAddress);
				} else if (p_type.equals(BWSProperties.P_CALCFORCE)) {
					params.put("receiverAddress", BWSProperties.calcForceWalletMainAddress);
				}

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
				logger.error("调用bws接口消费失败.", ex);
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
	 * 钱包转账
	 * 
	 * @param walletId
	 * @param amount
	 * @return
	 */
	@Override
	public String walletTransfer(String transactionNo, Long businessId, Long ownerId, int ownerType, String walletId,
			String receiverAddress, BigDecimal amount) {
		try {
			String resultVo = null;
			// 保存数据到库
			ShopBstkRecord reocrd = new ShopBstkRecord();
			reocrd.setTransactionNo(transactionNo);
			reocrd.setBusinessId(businessId);
			reocrd.setOwnerId(ownerId);
			reocrd.setOwnerType(ownerType);
			reocrd.setCallType(5);
			reocrd.setCreateTime(DateUtil.getCurrentDateTime());
			reocrd.setCallUrl(BWSProperties.bswWalletTransferAPI);

			try {
				/**
				 * 模拟提交，返回值
				 */
				JSONObject params = new JSONObject();
				params.put("senderWallet", walletId);
				params.put("receiverAddress", receiverAddress);
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
				logger.error("调用bws接口转账失败.", ex);
				reocrd.setStatus(2);

			}
			commonDao.save(reocrd);
			return resultVo;

		} catch (Exception ex) {
			logger.error("保存转账任务失败.", ex);
		}
		return null;
	}

	public static void main(String[] args) {
		try {
//			 String response =HttpClientUtils.bwsPost("http://service.genyuanlian.com/api/wallet/create",null);
//			 System.out.print(response);
//			 BWSWalletResponse result = JSON.parseObject(response,BWSWalletResponse.class);
//			 BWSWalletCreateResponseVo voResult =JSON.parseObject(result.getData(),
//			 BWSWalletCreateResponseVo.class);
//			 System.out.print(voResult);

			 JSONObject params=new JSONObject();
			 params.put("wallet","{\"coin\":\"btc\",\"network\":\"livenet\",\"xPrivKey\":\"xprv9s21ZrQH143K4DiN4zjhQHU9YekqUnWmpCbc5LSinZp1XysEHPdpLZDMAetYv4KPfCP7EiotG8JYtzHjobQaeiyQpiyfoHvwQr75nuXdNUL\",\"xPubKey\":\"xpub6D6Kbh271o67CKsj1cwRXyVNJrAtKexLqMdNyE35gfTcZXC7ihyzb8QVgFdXaEeZxUcDxEEHcdcXiPQD4XtAnvrZd7CprjMsYEp84g5Gqep\",\"requestPrivKey\":\"e82604fed40abeaa18a0c342f1dfe4e6f7736629bf0bf0d958f0da6a43215e87\",\"requestPubKey\":\"02e28f73bc1ce85a304b9143556689c9553ca18f2a0c16b98bdf91cd3d6c898e74\",\"copayerId\":\"7bf796cbb544228c8f9c0fdc490486de54dff18226eb80d75b78e2027a026e61\",\"publicKeyRing\":[{\"xPubKey\":\"xpub6D6Kbh271o67CKsj1cwRXyVNJrAtKexLqMdNyE35gfTcZXC7ihyzb8QVgFdXaEeZxUcDxEEHcdcXiPQD4XtAnvrZd7CprjMsYEp84g5Gqep\",\"requestPubKey\":\"02e28f73bc1ce85a304b9143556689c9553ca18f2a0c16b98bdf91cd3d6c898e74\"}],\"walletId\":\"a57e7428-5b11-4197-b19c-abe935edc762\",\"walletName\":\"BSTK Wallet\",\"m\":1,\"n\":1,\"walletPrivKey\":\"aec81bfee02cef6f8d5e47e252f0c3ea813a9de7a3eb07ca74847f92463ed9bc\",\"personalEncryptingKey\":\"KBCru9zW5sNHRPotxW4XDQ==\",\"sharedEncryptingKey\":\"DGvmbZqWQmHosJa5VtxwoA==\",\"mnemonic\":\"硝 补 饲 宴 斥 胞 条 柄 访 失 库 磁\",\"entropySource\":\"49fcbf6436feb6208013293098bc5f4637e0e038d63fbaa3b79fbcdcb5389c47\",\"mnemonicHasPassphrase\":false,\"derivationStrategy\":\"BIP44\",\"account\":0,\"compliantDerivation\":true,\"addressType\":\"P2PKH\"}");
			 String response =HttpClientUtils.bwsPost("https://service.genyuanlian.com/api/wallet/balance",params);
			 BWSWalletResponse result =
			 JSONObject.parseObject(response,BWSWalletResponse.class);
			 System.out.println(result.getData());
			
//			 JSONObject params = new JSONObject();
//			 params.put("senderWallet","");
//			 params.put("receiverAddress","");
//			 params.put("amount", new BigDecimal(99.999).multiply(new BigDecimal(100000000)));
//			 String response = HttpClientUtils.bwsPost("https://service.genyuanlian.com/api/wallet/transfer",params);
//			 System.out.print(response);
//			 BWSWalletResponse result = JSONObject.parseObject(response,BWSWalletResponse.class);
//			 BWSWalletTransferResponseVo voResult =JSONObject.parseObject(result.getData(),BWSWalletTransferResponseVo.class);
//			 System.out.print(voResult.getTxid());

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
