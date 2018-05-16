<template>
  <div class="gyl-paypwd">
    <div v-title>设置交易密码</div>
    <Header title="设置交易密码" :border="true"></Header>
    <section class="content" v-if="!showError">
      <div v-if="showPay">
        <pay-pwd ref="paypwd1" :payShow="true" v-on:save-pwd="getPwd1"></pay-pwd>
        <div class="paypwd-next">
          <input :class="nextDisable1?'btn-next':''" type="button" value="下一步" @click="setPwdNext" />
        </div>
      </div>
      <div v-if="!showPay">
        <pay-pwd ref="paypwd2" :payShow="true" title="请再次输入6位数字的交易密码" v-on:save-pwd="getPwd2"></pay-pwd>
        <div class="paypwd-next">
          <input :class="nextDisable2?'btn-next':''" type="button" value="确认" @click="setPwdConfirm"/>
        </div>
      </div>
    </section>
    <section class="result" v-if="showError">
      <div class="tip">
        <img src="../../assets/images/Ident/mistake.svg" alt="">
        <p class="tip-no">您两次输入的交易密码不一致，请重试</p>
        <p class="tip-re">请重新输入</p>
      </div>
      <div class="reinput">
        <input type="button" value="重新输入" @click="reinputClick"/>
      </div>
    </section>
  </div>
</template>
<script>
import Header from "@/components/common/Header";
import PayPwd from "@/components/common/PayPwd";
import { showMsg, loading, valid } from "@/utils/common.js";
import apiUrl from "@/config/apiUrl.js";
import { md5 } from 'vux';
export default {
  data() {
    return {
      payPwd: "",
      againPayPwd: "",
      step: 1,
      showPay:true,
      nextDisable1: true,
      nextDisable2: true,
      showError: false
    };
  },
  components: {
    Header,
    PayPwd
  },
  methods: {
    getPwd1:function(val) {
      // TODO 获取设置密码
      this.payPwd = val;
    },
    getPwd2:function(val) {
      // TODO 获取再次设置密码
      if(this.payPwd!=val) {
        this.showError = true;
        this.$refs.paypwd2.clearData();
      }else {
        this.againPayPwd = val;
      }
    },
    setPwdNext: function() {
      //TODO 设置密码下一步
      if(this.payPwd!="") {
        this.showPay = false;
        this.$refs.paypwd1.clearData();
        this.$refs.paypwd1.showKeyNum();
      }
    },
    setPwdConfirm: function() {
      //TODO 再次设置密码确认
      let param = {
        tradePwd: md5(this.againPayPwd)
      };
      this.$httpPost(apiUrl.saveTradePwd, param).then((res) => {
        if(res.data&&res.data.status==="1000") {
          let data = res.data;
          showMsg(res.data.msg);
          this.$router.push("pay_succ");
        } else {
          showMsg(res.data.msg);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    reinputClick: function() {
      //TODO 重新设置密码
      this.payPwd = "";
      this.againPayPwd = "";
      this.showError = false;
      this.showPay = true;
    }
  },
  mounted() {},
  watch: {
    payPwd: function(val, oldVal) {
      if(val!="" && val.length==6) {
        this.nextDisable1 = false;
      }else{
        this.nextDisable1 = true;
      }
    },
    againPayPwd: function(val, oldVal) {
      if(val!="" && val.length==6) {
        this.nextDisable2 = false;
      }else{
        this.nextDisable2 = true;
      }
    }
  }
};
</script>
<style lang="less">
.gyl-paypwd{
  .content{
    .paypwd-next{
      width:100%;
      height:90px;
      margin-top:146px;
      text-align: center;
      input[type='button']{
        width:550px;
        height: 90px;
        line-height:90px;
        background: #EBC97F;
        border-radius: 90px;
        font-size: 36px;
        color: #624621;
        margin:0 auto;
        &.btn-next{
          background: #E2E2E2;
          font-size: 36px;
          color: #6F7281;
        }
      }
    }
  }
  .result{
    .tip{
      margin-top:120px;
      text-align: center;
      img{
        width:120px;
        height: 120px;
        font-size:0;
      }
      .tip-no{
        margin-top:40px;
        font-size: 40px;
        color: #F9615C;
      }
      .tip-re{
        margin-top:36px;
        font-size: 32px;
        color: #333646;
      }
    }
    .reinput{
      width:100%;
      height:90px;
      margin-top:324px;
      text-align: center;
      input[type='button']{
        width:550px;
        height: 90px;
        line-height:90px;
        background: #EBC97F;
        border-radius: 90px;
        font-size: 36px;
        color: #624621;
        margin:0 auto;
        &.btn-next{
          background: #E2E2E2;
          font-size: 36px;
          color: #6F7281;
        }
      }
    }
  }
}
</style>
