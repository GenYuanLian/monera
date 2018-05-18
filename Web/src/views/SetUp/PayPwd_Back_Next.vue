<template>
  <div class="gyl-paypwd-back">
    <div v-title>找回交易密码</div>
    <Header title="找回交易密码" :border="true"></Header>
    <section class="content">
      <div class="con-row">
        <label>姓名：</label><input class="txt-mobile" :value="realName" type="text" readonly="readonly" placeholder="请输入姓名">
      </div>
      <div class="con-row">
        <label>身份证号：</label><input class="txt-mobile" @focus="idcardClear=true" @blur="idcardClear=false" v-model="idCard" type="text" placeholder="请输入身份证号码"><span @click="clearClick(1)" v-show="idcardClear" class="txt-clear ico-clear"></span>
      </div>
      <div class="con-row">
        <label>卡号：</label><input class="txt-pwd" @focus="banknoClear=true" @blur="banknoClear=false" v-model="bankNo" type="text" :placeholder="cardPalaceHolder"><span @click="clearClick(2)" v-show="banknoClear" class="txt-clear ico-clear"></span>
      </div>
      <div class="con-row">
        <label>手机号：</label><input class="txt-pwd" @focus="phoneClear=true" @blur="phoneClear=false" v-model="mobile" type="text" placeholder="银行卡预留手机号"><span @click="clearClick(3)" v-show="phoneClear" class="txt-clear ico-clear"></span>
      </div>
    </section>
    <footer class="gyl-footer m-t160">
      <input class="eru-btn btn-l btn-primary" type="button" @click="confirmClick" value="确认">
    </footer>
    <VerifyCodeModel ref="popVerifyCode" :mobile="mobile" :smstype="'backPayPwd'" v-on:sms-next="smsNext"></VerifyCodeModel>
  </div>
</template>
<script>
import Header from "@/components/common/Header";
import VerifyCodeModel from "@/components/VerifyCodeModel";
import { showMsg, loading, valid, bankTypeStr } from "@/utils/common.js";
import apiUrl from "@/config/apiUrl.js";
export default {
  data() {
    return {
      realName: "",
      idCard: "",
      mobile: "",
      bankNo: "",
      smsCode: "",
      smsNumber: "",
      cardPalaceHolder: "",
      idcardClear: false,
      banknoClear: false,
      phoneClear: false
    };
  },
  components: {
    Header, VerifyCodeModel
  },
  methods: {
    getLastBankNo: function(bankNo) {
      //TODO 获取卡号后四位
      return bankNo?bankNo.substr(-4):"";
    },
    clearClick: function(flag) {
      //TODO 清空
      if(flag==1) {
        this.idCard = "";
      }
      if(flag==2) {
        this.bankNo = "";
      }
      if(flag==3) {
        this.mobile = "";
      }
    },
    setSmsNum: function(val) {
      //TODO 验证码标识
      this.smsNumber = val;
    },
    initAuthInfo:function() {
      //TODO 查询用户身份认证信息
      let param = {};
      this.$httpPost(apiUrl.queryAuthentication, param).then((res) => {
        if(res.data&&res.data.status==="1000") {
          this.realName = res.data.bean.idName;
          this.idCard = res.data.bean.idCardNo;
        } else {
          showMsg(res.data.msg);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    confirmClick: function() {
      //TODO 确认
      if(this.realName=="") {
        showMsg("请输入姓名");
        return;
      }
      if(!valid.idCodeValid(this.idCard)) {
        showMsg("请输入正确的身份证号");
        return;
      }
      if(!valid.bankNo(this.bankNo)) {
        showMsg("请输入银行卡号");
        return;
      }
      if(!valid.phone(this.mobile)) {
        showMsg("手机号输入错误");
        return;
      }
      this.$refs.popVerifyCode.openModel();
    },
    smsNext: function(smsNumber, smsCode) {
      //TODO 输入验证码-提交
      let param = {
        mobile: this.mobile,
        smsCode: smsCode,
        smsNumber: smsNumber,
        idNumber: this.idCard,
        bankCard: this.bankNo
      };
      this.$httpPost(apiUrl.backPayPwd, param).then((res) => {
        if(res.data&&res.data.status==="1000") {
          this.$router.push("pay_pwd");
        } else {
          showMsg(res.data.msg);
        }
      }).catch((err) => {
        console.log(err);
      });
    }
  },
  mounted () {
    this.cardPalaceHolder = this.$route.query.bankName +bankTypeStr(+this.$route.query.cardType) +"尾号"+ this.getLastBankNo(this.$route.query.cardNo);
    this.mobile = this.$route.query.bindMobile;
    this.initAuthInfo();
  }
};
</script>
<style lang="less">
html,body{
  height: 100%;
  overflow: hidden;
}
.gyl-paypwd-back{
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
        width:60%;
        height: 80px;
        line-height: 80px;
        font-size: 28px;
        color: #393649;
      }
      .txt-pwd{
        width:60%;
        height: 80px;
        line-height: 80px;
        font-size: 28px;
        color: #393649;
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
