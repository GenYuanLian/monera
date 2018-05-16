<template>
  <div class="gyl-login">
    <div v-title>{{headTitle}}</div>
    <Header :title="headTitle" :left="headLift" :right="headRight" :rightFn="rightFn"></Header>
    <section class="content">
      <div class="login-tab">
        <span class="tab" :class="tabType==0?'active':''" @click="tabClick(0)">账号密码登录</span><span class="tab" :class="tabType==1?'active':''" @click="tabClick(1)">手机号快捷登录</span>
      </div>
      <div class="login-name" v-show="tabType==0">
        <div class="row"><i class="ico-username"></i><input class="phone" type="text" name="userName" id="userName" autocomplete="off" @focus="nameClear=true" @blur="nameClear=false" v-model="userName" placeholder="请输入用户名或手机号"><span v-show="nameClear" @click="clearClick(1)" class="txt-clear ico-clear"></span></div>
        <div class="row"><i class="ico-password"></i><input class="password" type="password" name="userPwd" id="userPwd" autocomplete="off" @focus="pwdClear=true" @blur="pwdClear=false" v-model="userPwd" placeholder="请输入密码"><span v-show="pwdClear" @click="clearClick(2)" class="txt-clear ico-clear"></span></div>
        <div class="forget"><router-link :to="{path:'forget_pwd'}">忘记密码?</router-link></div>
      </div>
      <div class="login-phone" v-show="tabType==1">
        <div class="row"><i class="ico-phone"></i><input class="phone" type="text" name="userPhone" id="userPhone" autocomplete="off" @focus="phoneClear=true" @blur="phoneClear=false" v-model="userPhone" placeholder="请输入手机号"><span v-show="phoneClear" @click="clearClick(3)" class="txt-clear ico-clear"></span></div>
        <div class="row"><i class="ico-smscode"></i><input class="code" type="text" name="userSmsCode" id="userSmsCode" autocomplete="off" @focus="smsClear=true" @blur="smsClear=false" v-model="smsCode" placeholder="请输入短信码"><span v-show="smsClear" @click="clearClick(4)" class="txt-code-clear ico-clear"></span><input id="btnCode" :disabled="codeDisable" class="sms-code" :class="codeDisable?'disabled':''" type="button" :value="codeText" @click="getSmsCode"></div>
      </div>
      <div class="btn-login"><input class="logining" type="button" value="登录" @click="loginClick"></div>
    </section>
  </div>
</template>
<script>
import { mapActions, mapGetters } from "vuex";
import { showMsg, loading, valid } from '@/utils/common.js';
import apiUrl from '@/config/apiUrl.js';
import { md5 } from 'vux';
import Header from '@/components/common/Header';
export default {
  data() {
    return {
      headTitle: "登录",
      headLift: {
        label: "",
        className: "ico-back"
      },
      headRight: {
        label: "注册",
        className: ""
      },
      tabType:0,
      userName: "",
      userPwd: "",
      userPhone:"",
      smsCode: '',
      invitationCode: '',
      codeDisable: false,
      codeText: '获取验证码',
      smsNumber: '',
      time: 60,
      nameClear: false,
      phoneClear: false,
      pwdClear: false,
      smsClear: false
    };
  },
  components: {Header},
  computed: {
    ...mapGetters(["loginToken", "loginInfo", "userInfo"])
  },
  methods: {
    ...mapActions({
      login: "login"
    }),
    backTo: function() {
      // 返回
      this.$router.back();
    },
    rightFn:function() {
      // 注册
      this.$router.push('register');
    },
    tabClick: function(val) {
      //TODO 登录方式切换
      this.tabType = val;
    },
    clearClick: function(flag) {
      //TODO 情况输入框
      if(flag==1) {
        this.userName = "";
      }
      if(flag==2) {
        this.userPwd = "";
      }
      if(flag==3) {
        this.userPhone = "";
      }
      if(flag==4) {
        this.smsCode = "";
      }
    },
    countDown: function() {
      // TODO 倒计时
      let _this = this;
      let count = 60;
      this.codeDisable = true;
      let t = setInterval(() => {
        if(_this.time>0) {
          _this.time--;
          _this.codeText = _this.time + "秒";
        } else {
          clearInterval(t);
          _this.time = 60;
          _this.codeDisable = false;
          _this.codeText = "获取验证码";
        }
      }, 1000);
    },
    getSmsCode: function() {
      // TODO 获取短信验证码
      if(!valid.phone(this.userPhone)) {
        showMsg("请输入正确的手机号");
        return;
      }
      this.countDown();
      let param = {
        mobile: this.userPhone,
        smstype: 'login'
      };
      this.$httpPost(apiUrl.sendSmsCode, param).then((res) => {
        if(res.status.code==0&&res.data) {
          let data = res.data;
          this.smsNumber = res.data.smsNumber;
          showMsg(res.status.message);
        }else{
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    loginClick() {
      //TODO 登录
      if(this.tabType==0) {
        if(this.userName=="") {
          showMsg("请输入用户名");
          return;
        }
        if(!valid.password(this.userPwd)) {
          showMsg("请输入正确的密码");
          return;
        }
      }
      if(this.tabType==1) {
        if(!valid.phone(this.userPhone)) {
          showMsg("请输入11位手机号码");
          return;
        }
        if(this.smsNumber=="") {
          showMsg("请先获取短信码");
          return;
        }
        if(!valid.smscode(this.smsCode)) {
          showMsg("请输入正确的短信码");
          return;
        }
      }
      let param = {
        loginName: this.tabType==0 ?this.userName:this.userPhone,
        loginType: this.tabType==0 ?'password':'smscode',
        loginCode: this.tabType==0 ?md5(this.userPwd):this.smsNumber,
        smsCode:this.smsCode
      };
      this.$httpPost(apiUrl.userLogin, param).then((res) => {
        if(res.status.code==0&&res.data) {
          this.login(res.data.userInfo);
          let redirect = this.$route.query.redirect;
          if(redirect) {
            this.$router.replace(decodeURIComponent(redirect));
          } else {
            this.$router.replace("/");
          }
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    }
  },
  mounted() {}
};
</script>
<style lang="less" scoped>
html,body{
  height: 100%;
  overflow: hidden;
}
.gyl-login{
  width:100%;
  height:100%;
  .content{
    height: auto;
    padding:0 30px;
    .login-tab{
      height: 96px;
      line-height: 96px;
      color: #999999;
      text-align: center;
      .tab{
        display: inline-block;
        width:50%;
        line-height: 96px;
        font-size:30px;
      }
      .tab.active{
        color:#317db9;
        border-bottom:4px solid #5a97c7;
      }
    }
    .login-name{
      margin-top:115px;
      padding: 0 45px;
      .row{
        height: 70px;
        line-height: 70px;
        margin-top:34px;
        padding:15px 0;
        border-bottom:1px solid #E2E2E2;/*no*/
        text-align: left;
        i{
          margin-left:20px;
          margin-bottom:-4px;
          width:40px;
          height:40px;
        }
        input{
          margin-left:40px;
          width:70%;
          height: 70px;
          line-height: 70px;
          font-size:28px;
        }
        .txt-clear{
          display: inline-block;
          width:18px;
          height:18px;
          margin-top:26px;
          margin-right:20px;
          float:right;
        }
      }
      .forget{
        height: 30px;
        line-height: 30px;
        margin-top:16px;
        font-size:24px;
        color: #9FA2AE;
        text-align: right;
        a{
          color: #9FA2AE;
          text-decoration:none;
        }
      }
    }
    .login-phone{
      margin-top:115px;
      padding: 0 45px;
      margin-bottom:110px;
      .row{
        height: 70px;
        line-height: 70px;
        margin-top:34px;
        padding:15px 0;
        border-bottom:1px solid #E2E2E2;/*no*/
        text-align: left;
        i{
          margin-left:20px;
          margin-bottom:-4px;
          width:40px;
          height:40px;
        }
        input{
          margin-left:40px;
          width:70%;
          height: 70px;
          line-height: 70px;
          font-size:28px;
        }
        .code{
          width:40%;
        }
        .txt-clear{
          display: inline-block;
          width:18px;
          height:18px;
          margin-top:26px;
          margin-right:20px;
          float:right;
        }
        .txt-code-clear{
          display: inline-block;
          width:18px;
          height:18px;
          margin-top:10px;
          margin-right:20px;
        }
        .sms-code{
          display: inline-block;
          margin-left:10px;
          padding:0;
          width:160px;
          height: 70px;
          line-height: 70px;
          font-size: 28px;
          color: #317db9;
          border-left: 1px solid #e3e3e3;/*no*/
          background-color: #fff;
          float:right;
          &.disabled{
            color:#317db9
          }
        }
      }
    }
    .btn-login{
      height: 76px;
      line-height: 76px;
      margin-top:70px;
      text-align: center;
      padding:0 75px;
      .logining{
        width:100%;
        height: 76px;
        line-height: 76px;
        background: #317db9;
        border-radius: 10px;
        font-size: 30px;
        color: #FFF;
      }
    }
  }
}
</style>
