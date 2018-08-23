package com.genyuanlian.consumer.shop.model;

import java.io.Serializable;

/**
 * ShopMember Entity.
 */
public class ShopMember implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5459881174828795160L;

	// 列信息
	private Long id;

	private String mobile;

	private Integer ownerType;

	private String loginName;

	private String loginPwd;

	private String oldPwd;

	private java.util.Date pwdUptime;

	private String nickName;

	private String email;

	private Integer gender;

	private String headPortrait;

	private Integer isIdentification;

	private String idName;

	private String idNumber;

	private String address;

	private String referraCode;

	private Long referraId;

	private String invitationCode;
	
	private String invitationCodePic;

	private String channel;

	private String channelVersion;

	private String signIp;

	private Integer status; // 状态：1-正常，2-冻结

	private String remark;

	private java.util.Date createTime;

	public void setId(Long value) {
		this.id = value;
	}

	public Long getId() {
		return this.id;
	}

	public void setMobile(String value) {
		this.mobile = value;
	}

	public String getMobile() {
		return this.mobile;
	}

	public void setOwnerType(Integer value) {
		this.ownerType = value;
	}

	public Integer getOwnerType() {
		return this.ownerType;
	}

	public void setLoginName(String value) {
		this.loginName = value;
	}

	public String getLoginName() {
		return this.loginName;
	}

	public void setLoginPwd(String value) {
		this.loginPwd = value;
	}

	public String getLoginPwd() {
		return this.loginPwd;
	}

	public void setOldPwd(String value) {
		this.oldPwd = value;
	}

	public String getOldPwd() {
		return this.oldPwd;
	}

	public void setPwdUptime(java.util.Date value) {
		this.pwdUptime = value;
	}

	public java.util.Date getPwdUptime() {
		return this.pwdUptime;
	}

	public void setNickName(String value) {
		this.nickName = value;
	}

	public String getNickName() {
		return this.nickName;
	}

	public void setEmail(String value) {
		this.email = value;
	}

	public String getEmail() {
		return this.email;
	}

	public void setGender(Integer value) {
		this.gender = value;
	}

	public Integer getGender() {
		return this.gender;
	}

	public void setHeadPortrait(String value) {
		this.headPortrait = value;
	}

	public String getHeadPortrait() {
		return this.headPortrait;
	}

	public void setIsIdentification(Integer value) {
		this.isIdentification = value;
	}

	public Integer getIsIdentification() {
		return this.isIdentification;
	}

	public void setIdName(String value) {
		this.idName = value;
	}

	public String getIdName() {
		return this.idName;
	}

	public void setIdNumber(String value) {
		this.idNumber = value;
	}

	public String getIdNumber() {
		return this.idNumber;
	}

	public void setAddress(String value) {
		this.address = value;
	}

	public String getAddress() {
		return this.address;
	}

	public void setReferraCode(String value) {
		this.referraCode = value;
	}

	public String getReferraCode() {
		return this.referraCode;
	}

	public void setReferraId(Long referraId) {
		this.referraId = referraId;
	}

	public Long getReferraId() {
		return referraId;
	}

	public void setInvitationCode(String value) {
		this.invitationCode = value;
	}

	public String getInvitationCode() {
		return this.invitationCode;
	}

	public String getInvitationCodePic() {
		return invitationCodePic;
	}

	public void setInvitationCodePic(String invitationCodePic) {
		this.invitationCodePic = invitationCodePic;
	}

	public void setChannel(String value) {
		this.channel = value;
	}

	public String getChannel() {
		return this.channel;
	}

	public void setChannelVersion(String value) {
		this.channelVersion = value;
	}

	public String getChannelVersion() {
		return this.channelVersion;
	}

	public void setSignIp(String value) {
		this.signIp = value;
	}

	public String getSignIp() {
		return this.signIp;
	}

	public void setStatus(Integer value) {
		this.status = value;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setRemark(String value) {
		this.remark = value;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setCreateTime(java.util.Date value) {
		this.createTime = value;
	}

	public java.util.Date getCreateTime() {
		return this.createTime;
	}

}