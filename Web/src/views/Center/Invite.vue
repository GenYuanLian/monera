<template>
  <div class="gyl-invite">
    <div v-title>邀请有礼</div>
    <div v-desc>{{nickName}}正在邀请您体验欧罗巴聚财</div>
    <div class="share-img"><img :src="headPortrait" alt=""></div>
    <div class="scroller">
      <Header title="邀请有礼" :bgCss="'headBg'" :left="left" :right="right"></Header>
      <section class="content">
        <div class="con-top">
          <div class="inv-header"><img src="../../assets/images/Center/invite-top.png" ></div>
          <div class="inv-get"><img class="fl" src="../../assets/images/Center/invite-friend1.png" ><img class="fr" src="../../assets/images/Center/invite-friend2.png"></div>
          <div class="inv-btn"><input type="button" @click="inviteShare"></div>
          <div class="inv-line"></div>
          <div class="inv-intro">
            <h1></h1>
            <p>1. 分享页面给好友，好友在你的邀请页领取奖励。<br>2. 被邀好友完成首次注册即可获得万元欧罗巴聚财专用大礼包。</p>
          </div>
        </div>
        <div class="inv-foot"></div>
        <div class="con-bot">最终解释权归欧罗巴聚财所有</div>
      </section>
    </div>
    <WxShareTip ref="wxShareTip" :isShow="false"></WxShareTip>
  </div>
</template>
<script>
import Header from "@/components/common/Header";
import WxShareTip from "@/components/common/WxShareTip";
import { mapGetters } from 'vuex';
import { showMsg, valid, versions } from '@/utils/common.js';
import apiUrl from "@/config/apiUrl.js";
import weixin from "@/utils/wechat.js";
export default {
  data() {
    return {
      left: {
        label: "",
        className: "ico-back-white"
      },
      right: {
        label: "活动规则",
        className: ""
      },
      userId: "",   // 用户id
      headPortrait: "",  // 头像
      nickName: "",  // 昵称
      invitationCode: "", // 邀请码
      url:"" // 链接
    };
  },
  components: {
    Header, WxShareTip
  },
  computed:{
    ...mapGetters({
      getUserInfo:'getUserInfo'
    })
  },
  methods: {
    inviteShare:function() {
      // 邀请分享
      // this.$router.push({path:'invite_share', query:{headPortrait:this.headPortrait, nickName:this.nickName, invitationCode:this.invitationCode}});
      if(versions.wx) {
        this.$refs.wxShareTip.openWin();
      } else if(versions.gyl_android||versions.gyl_ios) {
        this.schemeUrl("weixin://", function() {
          showMsg("请在微信打开，邀请好友");
        });
      } else {
        showMsg("请在微信打开，邀请好友");
      }
    },
    schemeUrl: function(url, callbak) {
      var ifr = document.createElement("iframe");
      ifr.src = url; /***打开app的协议，如zhe800://goto_home***/
      ifr.style.display = "none";
      document.body.appendChild(ifr);
      window.setTimeout(function() {
        document.body.removeChild(ifr);
        if(typeof callbak == 'function') {
          callbak();
        }
      }, 1000);
    },
    initUserInfo:function() {
      //TODO 查询用户信息
      let param = {
        id: this.userId
      };
      this.$httpPost(apiUrl.selectByPrimaryKey, param).then((res) => {
        if(res.data&&res.data.status==="1000") {
          this.headPortrait = (res.data.baseModel&&res.data.baseModel.headPortrait)?res.data.baseModel.headPortrait:"";
          this.nickName = (res.data.baseModel&&res.data.baseModel.nickName)?res.data.baseModel.nickName:"";
          this.invitationCode = (res.data.baseModel&&res.data.baseModel.invitationCode)?res.data.baseModel.invitationCode:"";
          this.initWxChat();
        } else {
          showMsg(res.data.msg);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    initWxChat: function() {
      // 初始化微信设置
      //link: window.location.href.split('#')[0]+"#"+"/invite_share?headPortrait="+this.headPortrait+"&nickName="+this.nickName+"&invitationCode="+this.invitationCode,
      let url = window.localStorage.getItem("LocalUrl")||window.location.href;
      let param = {
        title: this.nickName + "分享有礼",
        desc: this.nickName + "正在邀请您体验欧罗巴聚财",
        link: window.location.href.split('#')[0] + "#/invite_share?userId=" + this.userId,
        imgUrl: this.headPortrait,
        localUrl: url
      };
      weixin.wxChat(this, param);
    }
  },
  mounted () {
    this.userId = this.$route.query.userId;
    this.initUserInfo();
  },
  watch: {
    '$route' (to, from) {
      location.reload();
    }
  }
};
</script>
<style lang="less">
.gyl-invite{
  width:100%;
  height:100%;
  .share-img{
    display: none;
    width:80px;
    height:80px;
  }
  .scroller{
    height: 100%;
  }
  .headBg{
    background-color: #3C3E50;
    color: #fff;
  }
  .head-title{
    color: #fff;
  }
  .head-r{
    a{
      color: #fff;
      font-size: 28px;
    }
  }
  .content{
    width:100%;
    // height:100%;
    background-color: #3C3E50;
    padding:20px;
    // padding-bottom: 108px;
    box-sizing: border-box;
    .con-top{
      width: 690px;
      height: auto;
      margin: 0 10px;
      background-color: #fff;
      border-top-left-radius: 30px;
      border-top-right-radius: 30px;
      div{
        img{
          display: block;
          border: 0;
          width: 100%;
          height: auto;
        }
        &.inv-get{
          padding: 0 30px;
          overflow: hidden;
          img{
            width: 300px;
            margin: 40px 0 50px 0;
          }
        }
        &.inv-btn{
          width: 511px;
          height: 113px;
          margin: auto;
          background: url('../../assets/images/Center/invite-btn.svg') no-repeat center;
          background-size: 100% 100%;
          input{
            display: block;
            width: 100%;
            height: 100%;
            background-color: transparent;
          }
        }
        &.inv-line{
          width: 630px;
          height: 20px;
          background: url('../../assets/images/Center/invite-split.svg') no-repeat center;
          background-size: 100% 100%;
          margin: 30px auto;
        }
        &.inv-intro{
          padding: 0 30px;
          overflow: hidden;
          h1{
            width: 170px;
            height: 42px;
            background: url('../../assets/images/Center/invite-activity.svg') no-repeat center;
            background-size: 100% 100%;
            margin:20px auto;
          }
          p{
            font-size: 28px;
            color: #D8AA54;
            line-height: 40px;
            margin-bottom: 20px;
          }
        }
      }
    }
    .inv-foot{
      width: 710px;
      height: 414px;
      background: url('../../assets/images/Center/invite-bottom.svg') no-repeat center;
      background-size: 100% 100%;
      margin-top: -20px;
    }
    .con-bot{
      font-size: 24px;
      color: #A7A7B0;
      text-align: center;
      line-height: 72px;
      margin-top: 16px;
    }
  }
}
</style>