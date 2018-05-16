<template>
  <div class="gyl-invite-share scroller">
    <div v-title>欧罗巴聚财</div>
    <Header title="欧罗巴聚财" :left="left" :bgCss="'bg-col'" :right="right"></Header>
    <section class="content">
      <div class="container">
        <div class="inv-top">
          <img src="../../assets/images/Center/invite-share-top.png" alt="">
          <p class="inv-head">
            <img v-if="headImg" class="head-img" :src="headImg" alt="">
            <img v-else class="head-img" src="" alt="">
          </p>
          <p class="inv-name"><span>您的好友{{userNick}}</span></p>
          <p class="inv-gift"><span>正在用欧罗巴聚财 送你专享万元大礼包</span></p>
        </div>
        <div class="reward">
          <img src="../../assets/images/Center/invite-friend-get.png" alt="">
        </div>
        <div class="inv-reg-title"><span class="ico-inv-reg"></span></div>
        <div class="inv-reg-form">
          <div class="reg-row"><input class="txt-phone" type="text" id="txtPhone" v-model="userName" placeholder="请输入手机号"></div>
          <div class="reg-row"><input class="txt-code" type="text" name="" id="txtSms" v-model="smsCode" placeholder="请输入短信验证码"><input id="btnCode" :disabled="codeDisable" class="sms-code" :class="codeDisable?'disabled':''" type="button" :value="codeText" @click="getSmsCode"></div>
          <div class="reg-row"><input class="txt-pwd" type="text" name="" id="txtPwd" v-model='userPwd' placeholder="请输入登录密码"></div>
        </div>
        <div class="inv-reg-btn">
          <input class="reg-go" type="button" value="立即注册 去体验">
        </div>
        <div class="split">
          <img src="../../assets/images/Center/invite-split.svg" alt="">
        </div>
        <div class="activity">
          <img src="../../assets/images/Center/invite-activity.svg" alt="">
        </div>
        <div class="inv-info">
          <p>1. 完成注册即可得万元体验金、1%加息卡、30元现金券。</p>
          <p>2. 完成注册后好友可得30元现金券。</p>
        </div>
        <div class="inv-bot"><img src="../../assets/images/Center/invite-bot-decorate.svg" alt=""></div>
      </div>
      <div class="copyright">最终解释权归欧罗巴聚财所有</div>
    </section>
  </div>
</template>
<script>
import { mapActions, mapGetters } from "vuex";
import { showMsg, valid } from '@/utils/common.js';
import apiUrl from '@/config/apiUrl.js';
import Header from "@/components/common/Header";
import weixin from "@/utils/wechat.js";
export default {
  data() {
    return {
      left: {
        label: "",
        className: ""
      },
      right: {
        label: "活动规则",
        className: ""
      },
      headImg: "",
      userId:'',
      userNick: "",
      userName: '',
      userPwd: '',
      smsCode: '',
      invitationCode: '',
      codeDisable: false,
      codeText: '获取验证码',
      smsNumber: '',
      time: 60
    };
  },
  components: {
    Header
  },
  methods: {
    ...mapActions({
      register: "register"
    }),
    initUserInfo:function() {
      //TODO 查询用户信息
      if(!this.userId) {
        return;
      }
      let param = {
        memberId: +this.userId
      };
      this.$httpPost(apiUrl.selectByMemberId, param).then((res) => {
        if(res.data&&res.data.status==="1000") {
          this.headImg = res.data.headPortrait?res.data.headPortrait:"";
          this.userNick = res.data.nickName?res.data.nickName:"";
          this.invitationCode = res.data.invitationCode?res.data.invitationCode:"";
          this.initWxChat();
        } else {
          showMsg(res.data.msg);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    countDown: function() {
      // TODO 倒计时
      let _this = this;
      let count = 60;
      this.codeDisable = true;
      let t = setInterval(() => {
        if(_this.time>0) {
          _this.time--;
          _this.codeText = _this.time + "s后获取";
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
        showMsg("请输入11位手机号码");
        return;
      }
      this.countDown();
      let param = {
        mobile: this.userName,
        smstype: 'registr'
      };
      this.$httpPost(apiUrl.sendSmsCode, param).then((res) => {
        if(res.data&&res.data.status=='1000') {
          let data = res.data;
          this.smsNumber = res.data.smsNumber;
          showMsg(res.data.msg);
        }else{
          showMsg(res.data.msg);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    registerClick: function() {
      // TODO 注册
      if(!valid.phone(this.userName)) {
        showMsg("请输入11位手机号码");
        return;
      }
      if(this.smsNumber=="") {
        showMsg("请输入短信验证码");
        return;
      }
      if(!valid.password(this.userPwd)) {
        showMsg("请输入正确的密码规则");
        return;
      }
      let param = {
        mobile: this.userName,
        smsCode: this.smsCode,
        password: this.userPwd,
        inviteCode: this.invitationCode,
        smsNumber: this.smsNumber
      };
      this.$httpPost(apiUrl.userRegister, param).then((res) => {
        if(res.data&&res.data.status==="1000") {
          let data = res.data;
          this.register(data);
          this.$router.replace('/');
        } else {
          showMsg(res.data.msg);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    initWxChat: function() {
      let param = {
        title: this.userNick + "分享有礼",
        desc: this.userNick + "正在邀请您体验欧罗巴聚财",
        link: window.location.href,
        imgUrl: this.headImg,
        localUrl: window.location.href
      };
      weixin.wxChat(this, param);
    }
  },
  mounted () {
    this.userId = window.location.href.split('userId=') ? window.location.href.split('userId=')[1].split('&')[0] || '' : '';
    this.initUserInfo();
  }
};
</script>
<style lang="less">
.gyl-invite-share{
  width:100%;
  height:100%;
  overflow-y:auto;
  .gyl-header.bg-col{
    background-color: #3C3E50;
    .head {
      color:#fff;
    }
  }
  .head .head-r a{
    color:#fff;
    font-size: 28px;
  }
  .content{
    height:auto;
    background-color: #3C3E50;
    padding:0 30px;
    padding-bottom:44px;
    .container{
      background-color: #fff;
      border-radius: 30px;
      padding-bottom:80px;
    }
    .inv-top{
      position: relative;
      width:100%;
      height:310px;
      img{
        width:100%;
        height:100%;
      }
      .inv-head{
        position: absolute;
        display: inline-block;
        width:100%;
        height:100px;
        line-height: 100px;
        top:50px;
        text-align: center;
        .head-img{
          width:100px;
          height:100px;
          border-radius: 100px;
          margin:0 auto;
        }
      }
      .inv-name{
        position: absolute;
        display: inline-block;
        width:100%;
        top:180px;
        font-size: 28px;
        color: #997345;
        text-align: center;
        span{
          width:486px;
          margin:0 auto;
          line-height: 30px;
        }
      }
      .inv-gift{
        position: absolute;
        display: inline-block;
        width:100%;
        top:220px;
        font-size: 28px;
        color: #997345;
        text-align: center;
        span{
          width:486px;
          margin:0 auto;
          line-height: 30px;
        }
      }
    }
    .reward{
      margin-top:30px;
      padding:0 30px;
      height:250px;
      img{
        width:100%;
        height:100%;
      }
    }
    .inv-reg-title{
      margin-top:51px;
      height:41px;
      text-align: center;
      .ico-inv-reg{
        display: block;
        margin:0 auto;
        width:170px;
        height:41px;
        background-image: url('../../assets/images/Center/invite-share-regist.svg');
        background-size: 100%;
      }
    }
    .inv-reg-form{
      margin-top:40px;
      padding:0 30px;
      .reg-row{
        width:100%;
        height:90px;
        margin-bottom:20px;
        input::-webkit-input-placeholder {
          color: #997345;
        }
      }
      .txt-phone{
        width:100%;
        height:68px;
        line-height: 68px;
        border: 1px solid #D8AA54;/*no*/
        border-radius: 100px;
        padding:10px 0px;
        text-indent: 30px;
      }
      .txt-code{
        width:60%;
        height:68px;
        line-height: 68px;
        border: 1px solid #D8AA54;/*no*/
        border-radius: 100px;
        padding:10px 0px;
        text-indent: 30px;
      }
      .txt-pwd{
        width:100%;
        height:68px;
        line-height: 68px;
        border: 1px solid #D8AA54;/*no*/
        border-radius: 100px;
        padding:10px 0px;
        text-indent: 30px;
      }
      .sms-code{
        width:220px;
        height:88px;
        line-height: 88px;
        font-size: 28px;
        color: #4A3519;
        background-color:#FFD275;
        border-radius: 100px;
        float:right;
        &.disabled{
          color:#9fa2ae
        }
      }
    }
    .inv-reg-btn{
      margin:0px 30px;
      padding-top:10px;
      .reg-go{
        width:100%;
        height:88px;
        line-height: 88px;
        color: #4A3519;
        background-color:#FFD275;
        border-radius:100px;
      }
    }
    .split{
      margin-top:50px;
    }
    .activity{
      height:41px;
      margin-top:50px;
      text-align: center;
      img{
        width:170px;
        height:41px;
      }
    }
    .inv-info{
      padding:0 30px;
      margin-top:30px;
      p{
        line-height: 40px;
        font-size: 28px;
        color: #D8AA54;
      }
    }
    .inv-bot{
      margin-top:82px;
      img{
        display: block;
        width:389px;
        height:30px;
        margin:0 auto;
      }
    }
    .copyright{
      margin-top:40px;
      font-size: 24px;
      color: #A7A7B0;
      text-align: center;
    }
  }
}
</style>