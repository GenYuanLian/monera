<template>
  <div class="gyl-paypwd-edit">
    <div v-title>设置交易密码</div>
    <Header title="设置交易密码" :border="true"></Header>
    <section class="content">
      <div class="con-row">
        <label>手机号：</label><input class="txt-mobile" type="text" :value="mobile | mobileFilter" readonly="readonly" placeholder="请输入手机号">
      </div>
      <div class="con-row">
        <label>短信验证：</label><input class="txt-code" v-model="smsCode" type="text" placeholder="请输入短信验证码">
        <GetVerifyCode :smstype="'resetPayPwd'" :mobile="mobile" v-on:sms-num="setSmsNum"></GetVerifyCode>
      </div>
      <div class="con-row">
        <label>密码：</label><input class="txt-pwd" v-model="pwd1" type="password" maxlength="6" placeholder="请输入交易密码，6位数字">
      </div>
      <div class="con-row">
        <label>确认密码：</label><input class="txt-pwd" v-model="pwd2" type="password" maxlength="6" placeholder="请再次输入交易密码">
      </div>
    </section>
    <footer class="gyl-footer m-t160">
      <input class="gyl-btn" type="button" value="确认" @click="confirmClick">
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
      smsCode: "", // 短信验证码
      pwd1: "",
      pwd2: "",
      smsNumber: "", // 验证码发送标识
      redirect:"" // 重定向地址
    };
  },
  components: {
    Header, GetVerifyCode
  },
  computed:{
    ...mapGetters(["getLoginUser"])
  },
  methods: {
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
      if(!valid.numpwd(this.pwd1)) {
        showMsg("请输入正确的交易密码");
        return;
      }
      if(this.pwd2 != this.pwd1) {
        showMsg("请输入相同的交易密码");
        return;
      }
      let param = {
        mobile: this.mobile,
        smsCode: this.smsCode,
        payPwd: md5(this.pwd1),
        smsNumber: this.smsNumber
      };
      this.$httpPost(apiUrl.resetPayPwd, param).then((res) => {
        if(res.status.code==0&&res.data) {
          showMsg(res.status.message);
          if(this.redirect != '') {
            this.$router.replace(this.redirect);
          } else {
            this.$router.push('mine');
          }
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    }
  },
  mounted () {
    this.mobile = this.getLoginUser?this.getLoginUser.mobile:"";
    this.redirect = this.$route.query.redirect ? this.$route.query.redirect : '';
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
        color: #333;
      }
      .txt-pwd{
        width:60%;
        height: 80px;
        line-height: 80px;
        font-size: 28px;
        color: #333;
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
        margin-top:-4px;
        margin-right:20px;
        vertical-align: middle;
      }
    }
    :last-child{
      border-bottom:none;
    }
  }
  .gyl-footer{
    width: 100%;
    height: 80px;
    .gyl-btn{
      width: calc(~"100% - 150px");
      background-color: #317db9;
      color: #fff;
    }
  }
}
</style>
