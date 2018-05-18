<template>
  <div class="gyl-get-pwd">
    <div v-title>找回密码</div>
    <Header :border="true" :title="headTitle" :left="headLeft" ></Header>
    <section class="content">
      <div class="back-pwd">
        <div class="row"><i class="ico-phone"></i><input class="txt-phone" type="text" name="" id="txtPhone" @focus="phoneClear=true" @blur="phoneClear=false"  v-model="userName" placeholder="请输入手机号" /><span @click="clearClick(1)" v-show="phoneClear" class="txt-clear ico-clear"></span></div>
        <div class="row"><i class="ico-smscode"></i><input class="txt-code" type="text" name="" id="txtSms" @focus="smsClear=true" @blur="smsClear=false" v-model="smsCode" placeholder="请输入短信验证码" /><span @click="clearClick(2)" v-show="smsClear" class="txt-code-clear ico-clear"></span><input id="btnCode" :disabled="codeDisable" class="sms-code" :class="codeDisable?'disabled':''" type="button" :value="codeText" @click="getSmsCode"></div>
        <div class="row"><i class="ico-password"></i><input class="txt-pwd" type="password" name="" id="txtPwd" @focus="pwdClear=true" @blur="pwdClear=false" v-model='userPwd' placeholder="6-12位字符"/><span @click="clearClick(3)" v-show="pwdClear" class="txt-clear ico-clear"></span></div>
        <div class="row"><i class="ico-password"></i><input class="txt-pwd" type="password" name="" id="txtPwd2" @focus="pwd2Clear=true" @blur="pwd2Clear=false" v-model='userPwd2' placeholder="再次输入密码"/><span @click="clearClick(4)" v-show="pwd2Clear" class="txt-clear ico-clear"></span></div>
      </div>
      <div class="sub-btn">
        <input class="get-pwd" type="button" value="完成" @click="backPwdClick"/>
      </div>
    </section>
  </div>
</template>
<script>
import { mapActions, mapGetters } from "vuex";
import { showMsg, valid } from '@/utils/common.js';
import apiUrl from '@/config/apiUrl.js';
import Header from '@/components/common/Header';
import { md5 } from 'vux';
export default {
  data() {
    return {
      headTitle: "找回密码",
      headLeft: {
        label: "",
        className: "ico-back"
      },
      userName: '',
      userPwd: '',
      userPwd2:'',
      smsCode: '',
      invitationCode: '',
      codeDisable: false,
      codeText: '获取验证码',
      smsNumber: '',
      time: 60,
      phoneClear: false,
      smsClear: false,
      pwdClear: false,
      pwd2Clear:false
    };
  },
  components: {
    Header
  },
  methods: {
    ...mapActions({
      register: "register",
      login: "login"
    }),
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
      if(!valid.phone(this.userName)) {
        showMsg("请输入正确的手机号");
        return;
      }
      this.countDown();
      let param = {
        mobile: this.userName,
        smstype: 'findPwd'
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
    clearClick: function(flag) {
      //TODO 清空
      if(flag==1) {
        this.userName = "";
      }
      if(flag==2) {
        this.smsCode = "";
      }
      if(flag==3) {
        this.userPwd = "";
      }
      if(flag==4) {
        this.userPwd2 = "";
      }
    },
    backPwdClick: function() {
      // TODO 找回
      if(!valid.phone(this.userName)) {
        showMsg("请输入正确的手机号");
        return;
      }
      if(this.smsNumber=="") {
        showMsg("请先获取验证码");
        return;
      }
      if(!valid.smscode(this.smsCode)) {
        showMsg("请输入正确的验证码");
        return;
      }
      if(!valid.password(this.userPwd)) {
        showMsg("请输入6-20位字符密码");
        return;
      }
      if(!this.userPwd2) {
        showMsg("请再次输入密码");
        return;
      }
      if(this.userPwd !== this.userPwd2) {
        showMsg("两次密码输入不一致");
        return;
      }
      let param = {
        mobile: this.userName,
        smsCode: this.smsCode,
        pwd: md5(this.userPwd),
        smsNumber: this.smsNumber
      };
      this.$httpPost(apiUrl.findLoginPwd, param).then((res) => {
        if(res.status.code==0&&res.data) {
          let data = res.data;
          this.register(data);
          this.$router.push('login');
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
<style lang="less">
html,body{
  height: 100%;
  overflow: hidden;
}

.gyl-get-pwd{
  height: 100%;
  overflow: hidden;
  .content{
    box-sizing:border-box;
    width:100%;
    height:100%;
    padding-bottom:342px;
    overflow-x: hidden;
    overflow-y: auto;
    .back-pwd{
      margin-top:100px;
      padding:0 75px;
      text-align: left;
      .row{
        margin-top:34px;
        padding:17px 0;
        height: 56px;
        line-height: 56px;
        border-bottom: 1px solid #efefef;/*no*/
        i{
          margin-left:20px;
          font-size:0;
          margin-bottom:-4px;
          width:40px;
          height:40px;
        }
        .txt-phone{
          width:60%;
          height: 56px;
          line-height: 56px;
          margin-left:32px;
          font-size: 30px;
          color: #333;
        }
        .txt-code{
          width:45%;
          height: 56px;
          line-height: 56px;
          margin-left:32px;
          font-size: 30px;
          color: #333;
        }
        .txt-pwd{
          width:60%;
          height: 56px;
          line-height: 56px;
          margin-left:32px;
          font-size: 30px;
          color: #333;
        }
        .sms-code{
          display: inline-block;
          margin-left:10px;
          padding:0;
          width:160px;
          height: 56px;
          line-height: 56px;
          font-size: 28px;
          color: #317db9;
          background-color: #fff;
          float:right;
          &.disabled{
            color:#317db9
          }
        }
        .txt-clear{
          display: inline-block;
          width:18px;
          height:18px;
          margin-top:20px;
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
      }
    }
    .sub-btn{
      width:100%;
      height: 76px;
      margin-top:110px;
      text-align: center;
      .get-pwd{
        width:600px;
        height: 76px;
        line-height: 76px;
        background: #317db9;
        border-radius: 10px;
        font-size: 30px;
        color: #fff;
      }
    }
  }
}
</style>
