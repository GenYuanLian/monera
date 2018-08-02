<template>
  <div class="gyl-my">
    <div v-title>我的</div>
    <div class="gyl-mine">
      <div class="assets">
        <header class="head">
          <div class="head-l">
            <span class=""></span>
            <a href="javascript:;"></a>
          </div>
          <div class="head-title">我的</div>
          <div class="head-r">
            <span @click="settingClick"><i class="i-set ico-setting"></i></span>
            <span class="new" @click="newClick"><i class="i-new ico-new"></i></span>
          </div>
        </header>
      </div>
      <section class="content">
        <div class="mine-top">
          <div class="headinfo" @click="personalClick">
            <span class="head-img">
              <img v-if="headImg!=''" :src="headImg" alt="">
              <img v-else src="../../assets/images/Bg/head-default.png" alt="">
            </span>
            <span class="user-noinfo" v-if="!isLogin">未登录,点击登录</span>
            <span class="user-info" v-if="isLogin">
              <p v-text="userName"></p>
              <p v-text="userPhone"></p>
            </span>
          </div>
          <div class="mine-card">
            <div class="card-num" @click="cardClick">
              <p class="nav-title"><span>提货卡({{cardCount}})张</span><span class="card"><i class="ico-arrow-r"></i></span></p>
              <p class="acc-total">{{cardBalance}} 源点</p>
            </div>
            <div class="card-acc" @click="walletClick">
              <p class="nav-title"><span>钱包</span><span class="card-account"><i class="ico-arrow-r"></i></span></p>
              <p class="acc-total">{{bstkBalance}} BSTK</p>
            </div>
          </div>
        </div>
        <div class="mine-active">
          <div class="block-l" @click="snatchClick">
            <span class="snatch"><i class="ico-snatch"></i></span>
            <span class="sna-title">我的抢购</span>
          </div>
          <div class="block-r" @click="auctionClick">
            <span class="auction"><i class="ico-auction"></i></span>
            <span class="auc-title">我的竞拍</span>
          </div>
        </div>
        <div class="mine-block">
          <div class="block-row" @click="myInviteClick">
            <span class="power"><i class="ico-share-nav"></i></span>
            <span class="nav-title">我的分享</span>
            <span class="row-right"><i class="ico-more"></i></span>
          </div>
          <div class="block-row" @click="powerClick">
            <span class="power"><i class="ico-power"></i></span>
            <span class="nav-title">算力服务</span>
            <span class="row-right"><i class="ico-more"></i></span>
          </div>
          <div class="block-row" @click="addressClick">
            <span class="address"><i class="ico-address"></i></span>
            <span class="nav-title">收货地址</span>
            <span class="row-right"><i class="ico-more"></i></span>
          </div>
          <div class="block-row" @click="collectionClick">
            <span class="collection"><i class="ico-collection"></i></span>
            <span class="nav-title">我的收藏</span>
            <span class="row-right"><i class="ico-more"></i></span>
          </div>
        </div>
        <div class="mine-block">
          <div class="block-row" @click="serviceClick">
            <span class="service"><i class="ico-service"></i></span>
            <span class="nav-title">服务中心</span>
            <span class="row-right"><i class="ico-more"></i></span>
          </div>
          <div class="block-row" @click="scoreClick">
            <span class="score"><i class="ico-score"></i></span>
            <span class="nav-title">欢迎评分</span>
            <span class="row-right"><i class="ico-more"></i></span>
          </div>
        </div>
      </section>
    </div>
    <NavBar :isShow="true" :isLogin="isLogin"></NavBar>
  </div>
</template>
<script>
import { mapActions, mapGetters } from "vuex";
import NavBar from '@/components/common/NavBar';
import { showMsg, loading, valid } from "@/utils/common.js";
import apiUrl from "@/config/apiUrl.js";
import weixin from "@/utils/wechat.js";
export default {
  data() {
    return {
      userId: '',
      headImg: '',
      userName: "",
      userPhone: "",
      cardCount: 0,
      cardBalance: 0.00,
      bstkBalance: 0.00,
      isLogin: false
    };
  },
  components: {
    NavBar
  },
  computed:{
    ...mapGetters(["getLoginUser", "getUserInfo"])
  },
  methods: {
    ...mapActions({
      saveUserInfo: 'saveUserInfo'
    }),
    personalClick: function() {
      //TODO 个人信息
      if(this.isLogin) {
        this.$router.push("personal_infor");
      }else {
        this.$router.push({path: "login", query: { redirect: this.$route.name }});
      }
    },
    newClick: function() {
      //TODO 消息
      this.$router.push("news");
    },
    settingClick: function() {
      //TODO 设置
      this.$router.push("setting");
    },
    cardClick: function() {
      //TODO 提货卡
      if(this.isLogin) {
        this.$router.push("card");
      } else {
        showMsg("请先登录");
      }
    },
    walletClick: function() {
      //TODO 我的钱包
      if(this.isLogin) {
        this.$router.push("wallet_mine");
      } else {
        showMsg("请先登录");
      }
    },
    auctionClick: function() {
      //TODO 竞拍活动
      if(this.isLogin) {
        this.$router.push({name:"auction_home", query:{memberId:this.userId}});
      } else {
        showMsg("请先登录");
      }
    },
    snatchClick: function() {
      //TODO 抢购活动
      if(this.isLogin) {
        this.$router.push({name:"snatch_home", query:{memberId:this.userId}});
      } else {
        showMsg("请先登录");
      }
    },
    myInviteClick: function() {
      //TODO 我的分享
      if(this.isLogin) {
        this.$router.push({name:"mine_share", query:{memberId:this.userId}});
      } else {
        showMsg("请先登录");
      }
    },
    powerClick: function() {
      //TODO 算力服务
      if(this.isLogin) {
        this.$router.push("computing_server");
      } else {
        showMsg("请先登录");
      }
    },
    addressClick: function() {
      //TODO 收获地址
      if(this.isLogin) {
        this.$router.push("address");
      } else {
        showMsg("请先登录");
      }
    },
    collectionClick: function() {
      //TODO 我的收藏
      if(this.isLogin) {
        this.$router.push("collection");
      } else {
        showMsg("请先登录");
      }
    },
    serviceClick: function() {
      //TODO 服务中心
      this.$router.push("services");
    },
    scoreClick: function() {
      //TODO 欢迎评分
      this.$router.push("score");
    },
    shareClick:function() {
      //TODO 邀请有礼
      // this.$router.push({path: "/invite", query: {userId: this.userId}});
      location.href = "?#/invite?userId=" + this.userId;
    },
    initUserInfo:function() {
      //TODO 查询用户信息
      let param = {};
      this.$httpPost(apiUrl.getMemberInfo, param).then((res) => {
        if(res.status.code==0&&res.data) {
          this.headImg = res.data.info.headPortrait?res.data.info.headPortrait:"";
          this.userName = res.data.info.nickName;
          this.userPhone = res.data.info.mobile;
          this.cardCount = res.data.info.cardCount;
          this.cardBalance = res.data.info.sumBalance;
          this.bstkBalance = res.data.info.bstkBalance;
          this.saveUserInfo(res.data.info);
          this.initWxChat();
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        this.isLogin = false;
        console.log(err);
      });
    },
    initWxChat: function() {
      let url = window.localStorage.getItem("LocalUrl")||window.location.href;
      let param = {
        title: "买卖大集",
        desc: "买卖大集欢迎您体验",
        link: window.location.href.split('#')[0]+'?#'+window.location.href.split('#')[1],
        imgUrl: this.headImg,
        localUrl: url
      };
      weixin.wxChat(this, param);
    }
  },
  mounted() {
    this.userId = this.getLoginUser?this.getLoginUser.id:"";
    if(this.userId) {
      this.isLogin = true;
      this.initUserInfo();
    }
  }
};
</script>
<style lang="less">
html,body{
  height: 100%;
}
.gyl-my{
  box-sizing: border-box;
  width:100%;
  height: 100%;
  padding-bottom:97px;
  // height: calc(~"100% - 98px");
  background-color:  #f3f4f6;
  .gyl-mine{
    width:100%;
    height: 100%;
    overflow-y: scroll;
    -webkit-overflow-scrolling: touch;
    position: relative;
    top: 0;
    z-index: 2;
  }
  .head {
    position: absolute;
    display: box;
    display: -webkit-box;
    display: -moz-box;
    display: -ms-flexbox;
    display: -webkit-flex;
    display: flex;
    -webkit-flex-direction: row;
    -moz-flex-direction: row;
    -ms-flex-direction: row;
    -o-flex-direction: row;
    flex-direction: row;
    -webkit-flex-wrap: wrap;
    -moz-flex-wrap: wrap;
    -ms-flex-wrap: wrap;
    -o-flex-wrap: wrap;
    flex-wrap: wrap;
    justify-content: space-around;
    align-items: stretch;
    width: 100%;
    height: 88px;
    line-height: 88px;
    font-size: 36px;
    color: #FFFFFF ;
    box-sizing: border-box;
    margin-top:10px;
    &.bg-col{
      background-color: #317db9;
    }
    .head-l {
      width: 130px;
      padding-left: 40px;
      font-size: 32px;
      overflow: hidden;
      span {
        display: block;
        width: 66px;
        height: 88px;
        line-height: 88px;
        float: left;
      }
      a {
        display: inline-block;
        text-decoration: none;
        height: 88px;
        line-height: 88px;
        color: #000;
      }
    }
    .head-title {
      flex: 1;
      text-align: center;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      font-weight: 500;
      font-size:36px;
    }
    .head-r {
      width: 140px;
      padding-right: 30px;
      font-size: 32px;
      overflow: hidden;
      span {
        display: block;
        width: 50px;
        height: 88px;
        line-height: 88px;
        float: right;
        text-align: center;
      }
      .new {
        margin-right:30px;
      }
      .i-set {
        font-size: 0;
        margin-bottom: -6px;
        width:40px;
        height:40px;
      }
      .i-new {
        font-size: 0;
        margin-bottom: -6px;
        width:36px;
        height:40px;
      }
      a {
        display: inline-block;
        text-decoration: none;
        height: 88px;
        line-height: 88px;
        color: #000;
      }
    }
  }
  .content{
    width:100%;
    .mine-top{
      background-color: #317db9;
      background-image: url("../../assets/images/Bg/mine-bg.png");
      background-repeat: no-repeat;
      background-size: 100% auto;
      padding-top:100px;
      padding-bottom:40px;
      .headinfo{
        padding:0px 30px;
        text-align: center;
        .head-img{
          display: block;
          width:130px;
          height:130px;
          border-radius: 100%;
          border:5px solid #5a97c7;
          margin:0 auto;
          img{
            width:130px;
            height:130px;
            font-size:0;
            border-radius: 100%;
          }
        }
        .user-info{
          display: inline-block;
          height:120px;
          margin: 0 auto;
          p{
            line-height: 50px;
            color:#fff;
            font-size:30px;
          }
        }
        .user-noinfo{
          display: inline-block;
          height:120px;
          line-height: 120px;
          margin: 0 auto;
          color:#fff;
            font-size:30px;
        }
      }
    }
    .mine-card{
      display: flex;
      margin-top:40px;
      .nav-title{
        font-size:32px;
        color:#fff;
        height:50px;
        line-height: 50px;
        text-align: center;
      }
      .card-num{
        flex:1;
        color:#fff;
        p{
          text-align: center;
          font-size:32px;
        }
        .card{
          display: inline-block;
          width:32px;
          height:50px;
          margin-left:20px;
          i{
            width:32px;
            height:20px;
            margin-bottom:2px;
          }
        }
        .acc-total{
          margin-top:20px;
        }
      }
      .card-acc{
        flex:1;
        color:#fff;
        p{
          text-align: center;
          font-size:32px;
        }
        .card-account{
          display: inline-block;
          width:32px;
          height:50px;
          margin-left:20px;
          i{
            width:32px;
            height:20px;
            margin-bottom:2px;
          }
        }
        .acc-total{
          margin-top:20px;
        }
      }
    }
    .mine-active{
      display: flex;
      height:60px;
      line-height: 60px;
      padding:30px 0px;
      background-color: #fff;
      overflow: hidden;
      text-align: center;
      .block-l,.block-r{
        flex:1;
        height:60px;
        line-height: 60px;
        text-align: center;
        float:left;
        font-size:30px;
      }
      .block-l{
        border-right:1px solid #e8e8e8;/*no*/
      }
      .snatch{
        display: inline-block;
        width:40px;
        height:60px;
        line-height: 60px;
        i{
          width:40px;
          height:40px;
          margin-bottom:-8px;
        }
      }
      .auction{
        display: inline-block;
        width:40px;
        height:60px;
        line-height: 60px;
        i{
          width:40px;
          height:40px;
          margin-bottom:-8px;
        }
      }
      .sna-title{
        color:#ffa936;
        margin-left:20px;
      }
      .auc-title{
        color:#317db9;
        margin-left:20px;
      }
    }
    .mine-block{
      margin:20px 30px 0;
      border-radius:10px;
      background-color: #fff;
      .block-row{
        height:60px;
        line-height: 60px;
        padding:30px 40px 30px 0;
        margin-left:40px;
        border-bottom: 1px solid #efefef; /*no*/
        &:last-child{
          border-bottom:none;
        }
        .power,.address,.collection,.service,.score{
          display: inline-block;
          width:60px;
          height:60px;
          i{
            width:40px;
            height:40px;
            margin-bottom:-10px;
          }
        }
        .nav-title{
          font-size:30px;
          color:#333333;
        }
        .row-right{
          float:right;
          width:20px;
          height:60px;
          line-height: 60px;
          i{
            width: 14px;
            height: 24px;
          }
        }
      }
    }
  }
  .ico-auction{
    display: inline-block;
    background: url('../../assets/images/Icon/ico-auction@2x.png') center no-repeat;
    background-size:auto 100%;
  }
  .ico-snatch{
    display: inline-block;
    background: url('../../assets/images/Icon/ico-snatch@2x.png') center no-repeat;
    background-size:auto 100%;
  }
  .ico-arrow-r{
    display: inline-block;
    background: url('../../assets/images/Icon/ico-arrow-r@2x.png') center no-repeat;
    background-size:auto 100%;
  }
}
</style>
