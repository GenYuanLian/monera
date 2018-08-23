package com.genyuanlian.consumer.shop.api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.genyuanlian.consumer.shop.model.ShopMember;
import com.genyuanlian.consumer.shop.model.ShopMemberAddress;
import com.genyuanlian.consumer.shop.vo.IdParamsVo;
import com.genyuanlian.consumer.shop.vo.LoginNameParamsVo;
import com.genyuanlian.consumer.shop.vo.MemberAddressParamsVo;
import com.genyuanlian.consumer.shop.vo.MemberFindPwdParamsVo;
import com.genyuanlian.consumer.shop.vo.MemberIdBusinessIdParamsVo;
import com.genyuanlian.consumer.shop.vo.MemberIdPagedParamsVo;
import com.genyuanlian.consumer.shop.vo.MemberIdParamsVo;
import com.genyuanlian.consumer.shop.vo.MemberLoginParamsVo;
import com.genyuanlian.consumer.shop.vo.MemberRegisterParamsVo;
import com.genyuanlian.consumer.shop.vo.NicknameParamsVo;
import com.genyuanlian.consumer.shop.vo.SendWalletBstkParamsVo;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.genyuanlian.consumer.shop.vo.UploadHeadPortraitParamsVo;

public interface IMemberApi {
	/**
	 * 会员注册api
	 * 
	 * @param MemberRegisterParamsVo
	 */
	public ShopMessageVo<Map<String, Object>> register(MemberRegisterParamsVo params);

	/**
	 * 持久化注册信息
	 * 
	 * @param params
	 * @return
	 */
	public ShopMember registerPersist(MemberRegisterParamsVo params);

	/**
	 * 会员登录api
	 * 
	 * @param MemberLoginParamsVo
	 */
	public ShopMessageVo<Map<String, Object>> login(MemberLoginParamsVo params);

	/**
	 * 会员退出登录api
	 * 
	 * @param MemberIdParamsVo
	 */
	public ShopMessageVo<Map<String, Object>> loginOut(MemberIdParamsVo params);

	/**
	 * 获取会员基本信息api
	 * 
	 * @param MemberIdParamsVo
	 */
	public ShopMessageVo<Map<String, Object>> getMemberInfo(MemberIdParamsVo params);

	/**
	 * 获取会员邀请码api
	 * 
	 * @param MemberIdParamsVo
	 */
	public ShopMessageVo<Map<String, Object>> getInvitationCode(MemberIdParamsVo params);

	/**
	 * 获取我的邀请会员注册列表分页数据api
	 * 
	 * @param MemberIdPagedParamsVo
	 */
	public ShopMessageVo<Map<String, Object>> getMemberInvitRegister(MemberIdPagedParamsVo params);

	/**
	 * 会员找回密码api
	 * 
	 * @param MemberFindPwdParamsVo
	 */
	public ShopMessageVo<Map<String, Object>> findPwd(MemberFindPwdParamsVo params);

	/**
	 * 会员上传头像api
	 * 
	 * @param UploadHeadPortraitParamsVo
	 */
	public ShopMessageVo<Map<String, Object>> uploadHeadPortrait(UploadHeadPortraitParamsVo params);

	/**
	 * 设置登录账号api
	 * 
	 * @param LoginNameParamsVo
	 */
	public ShopMessageVo<Map<String, Object>> setLoginName(LoginNameParamsVo params);

	/**
	 * 设置昵称api
	 * 
	 * @param NicknameParamsVo
	 */
	public ShopMessageVo<Map<String, Object>> setNickname(NicknameParamsVo params);

	/**
	 * 图片上传api
	 * 
	 * @param MultipartFile
	 */
	public ShopMessageVo<Map<String, Object>> imageUpload(@RequestParam("file") MultipartFile file);

	/**
	 * 查询会员有效的收货地址列表api
	 * 
	 * @param MemberIdParamsVo
	 */
	public ShopMessageVo<List<ShopMemberAddress>> getMemberAddressList(MemberIdParamsVo params);

	/**
	 * 获取会员收货地址详情api
	 * 
	 * @param IdParamsVo
	 */
	public ShopMessageVo<Map<String, Object>> getMemberAddress(IdParamsVo params);

	/**
	 * 保存会员收货地址api
	 * 
	 * @param IdParamsVo
	 */
	public ShopMessageVo<Map<String, Object>> saveMemberAddress(MemberAddressParamsVo params);

	/**
	 * 会员删除收货地址api
	 * 
	 * @param IdParamsVo
	 */
	public ShopMessageVo<Map<String, Object>> deleteMemberAddress(IdParamsVo params);

	/**
	 * 重置交易密码
	 * 
	 * @param req
	 * @return
	 */
	public ShopMessageVo<String> resetPayPwd(MemberFindPwdParamsVo req);

	/**
	 * 登录
	 * 
	 * @param member
	 *            登录用户对象
	 * @param loginType
	 *            登录类型：1-密码登录；2-短信验证码登录；3-第三方授权登录
	 * @param loginIp
	 *            用户ip
	 * @return
	 */
	public Map<String, Object> login(ShopMember member, Integer loginType, String loginIp);

	/**
	 * 获取会员钱包收款地址api
	 * 
	 * @param MemberIdParamsVo
	 */
	public ShopMessageVo<Map<String, Object>> getWalletAddress(MemberIdParamsVo params);

	/**
	 * 会员发送bstk到对方钱包地址api
	 * 
	 * @param SendWalletBstkParamsVo
	 */
	public ShopMessageVo<Map<String, Object>> sendWalletBstk(SendWalletBstkParamsVo params);

	/**
	 * 获取会员btsk钱包流水分页数据api
	 * 
	 * @param MemberIdPagedParamsVo
	 */
	public ShopMessageVo<Map<String, Object>> getWalletBill(MemberIdPagedParamsVo params);

	/**
	 * 获取会员btsk钱包流水详情api
	 * 
	 * @param MemberIdBusinessIdParamsVo
	 */
	public ShopMessageVo<Map<String, Object>> getWalletBillDetail(MemberIdBusinessIdParamsVo params);
}
