<template>
  <div class="gyl-paypwd-edit">
    <div v-title>修改交易密码</div>
    <Header title="修改交易密码" :border="true"></Header>
    <section class="content">
      <div class="con-row">
        <label>手机号：</label><input class="txt-mobile" type="text" :value="mobile | mobileFilter" readonly="readonly" placeholder="请输入手机号">
      </div>
      <div class="con-row">
        <label>短信验证：</label><input class="txt-code" @focus="smsClear=true" @blur="smsClear=false" v-model="smsCode" type="text" placeholder="请输入短信验证码"><span @click="clearClick(1)" v-show="smsClear" class="txt-code-clear ico-clear"></span>
        <GetVerifyCode :smstype="'findPayPwd'" :mobile="mobile" v-on:sms-num="setSmsNum"></GetVerifyCode>
      </div>
      <div class="con-row">
        <label>旧密码：</label><input class="txt-pwd" @focus="pwdoClear=true" @blur="pwdoClear=false" v-model="oldPwd" type="password" maxlength="6" placeholder="请输入旧的交易密码，6位数字"><span @click="clearClick(2)" v-show="pwdoClear" class="txt-clear ico-clear"></span>
      </div>
      <div class="con-row">
        <label>新密码：</label><input class="txt-pwd" @focus="pwdnClear=true" @blur="pwdnClear=false" v-model="newPwd" type="password" maxlength="6" placeholder="请输入新的交易密码，6位数字"><span @click="clearClick(3)" v-show="pwdnClear" class="txt-clear ico-clear"></span>
      </div>
    </section>
    <footer class="gyl-footer m-t160">
      <input class="eru-btn btn-l btn-primary" type="button" value="确认" @click="confirmClick">
    </footer>
  </div>
</template>
<script>
import { mapActions, mapGetters } from "vuex";
import Header from "@/components/common/Header";
import GetVerifyCode from "@/components/GetVerificationCode";
import { showMsg, loading, valid } from "@/utils/common.js";
import apiUrl from "@/config/apiUrl.js";
import { md5 } from 'vux';
export default {
  data() {
    return {
      mobile: "",
      smsCode: "",
      oldPwd: "",
      newPwd: "",
      smsNumber: "",
      smsClear: false,
      pwdoClear: false,
      pwdnClear: false
    };
  },
  components: {
    Header, GetVerifyCode
  },
  computed:{
    ...mapGetters(["getLoginUser"])
  },
  methods: {
    clearClick: function(flag) {
      //TODO 清空
      if(flag==1) {
        this.smsCode = "";
      }
      if(flag==2) {
        this.oldPwd = "";
      }
      if(flag==3) {
        this.newPwd = "";
      }
    },
    setSmsNum: function(val) {
      //TODO 验证码标识
      this.smsNumber = val;
    },
    confirmClick: function() {
      //TODO 确认提交
      if(!valid.phone(this.mobile)) {
        showMsg("手机号输入错误");
        return;
      }
      if(this.smsCode==""||this.smsNumber=="") {
        showMsg("请先获取验证码");
        return;
      }
      if(!valid.numpwd(this.oldPwd)) {
        showMsg("请输入正确的旧交易密码");
        return;
      }
      if(!valid.numpwd(this.newPwd)) {
        showMsg("请输入正确的新交易密码");
        return;
      }
      let param = {
        mobile: this.mobile,
        smsCode: this.smsCode,
        oldTradePwd: md5(this.oldPwd),
        tradePwd: md5(this.newPwd),
        smsNumber: this.smsNumber
      };
      this.$httpPost(apiUrl.modifyTradePwd, param).then((res) => {
        if(res.data&&res.data.status==="1000") {
          let data = res.data;
          showMsg(res.data.msg);
          this.$router.push('mine');
        } else {
          showMsg(res.data.msg);
        }
      }).catch((err) => {
        console.log(err);
      });
    }
  },
  mounted () {
    this.mobile = this.getLoginUser?this.getLoginUser.mobile:"";
  }
};
</script>
<style lang="less">
html,body{
  height: 100%;
  overflow: hidden;
}
.gyl-paypwd-edit{
  background-color: #F3F4F6;
  height:100%;
  background-color: #F3F4F6;
  .content{
    background-color: #fff;
    margin-top:20px;
    .con-row{
      height: 100px;
      line-height: 100px;
      margin: 0 30px;
      border-bottom:1px solid #efefef;/*no*/
      text-align: left;
      label{
        display: inline-block;
        width:180px;
      }
      .txt-mobile{
        width:60%;
        height: 80px;
        line-height: 80px;
        font-size: 28px;
        color: #393649;
      }
      .txt-code{
        width:45%;
        height: 80px;
        line-height: 80px;
        font-size: 28px;
        color: #9FA2AE;
      }
      .txt-pwd{
        width:60%;
        height: 80px;
        line-height: 80px;
        font-size: 28px;
        color: #9FA2AE;
      }
      .gyl-getCode{
        float:right;
      }
      .txt-clear{
        display: inline-block;
        width:18px;
        height:18px;
        margin-top:41px;
        margin-right:20px;
        float:right;
      }
      .txt-code-clear{
        display: inline-block;
        width:18px;
        height:18px;
        margin-top:41px;
        margin-right:20px;
      }
    }
    :last-child{
      border-bottom:none;
    }
  }
}
</style>
