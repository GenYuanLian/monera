package com.genyuanlian.consumer.shop.api;

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
import com.genyuanlian.consumer.shop.vo.MemberIdParamsVo;
import com.genyuanlian.consumer.shop.vo.MemberLoginParamsVo;
import com.genyuanlian.consumer.shop.vo.MemberRegisterParamsVo;
import com.genyuanlian.consumer.shop.vo.NicknameParamsVo;
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

}
