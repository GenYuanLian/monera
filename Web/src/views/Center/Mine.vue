<template>
  <div class="gyl-my">
    <div v-title>我的</div>
    <div class="gyl-mine">
      <div class="assets">
        <header class="head bg-col">
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
            <span class="user-noinfo" v-if="!isLogin">未登录</span>
            <span class="user-info" v-if="isLogin">
              <p v-text="userName"></p>
              <p v-text="userPhone"></p>
            </span>
          </div>
        </div>
        <div class="mine-card">
          <div class="card-row" @click="cardClick">
            <div class="card-num"><span class="card"><i class="ico-card"></i></span><span class="nav-title">提货卡</span><span class="num">{{cardCount}}张</span></div>
            <div class="card-acc"><span class="card-account"><i class="ico-balances"></i></span><span class="nav-title">余额合计</span><span class="total">{{cardBalance}}BSTK</span></div>
          </div>
        </div>
        <div class="mine-block">
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
    <NavBar :isShow="true"></NavBar>
  </div>
</template>
<script>
import { mapActions, mapGetters } from "vuex";
import NavBar from '@/components/common/NavBar';
import { showMsg, loading, valid } from "@/utils/common.js";
import apiUrl from "@/config/apiUrl.js";
export default {
  data() {
    return {
      userId: '',
      headImg: '',
      userName: "",
      userPhone: "",
      cardCount: 0,
      cardBalance: 0.00,
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
      this.$router.push("personal_infor");
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
      }
    },
    addressClick: function() {
      //TODO 收获地址
      if(this.isLogin) {
        this.$router.push("address");
      }
    },
    collectionClick: function() {
      //TODO 我的收藏
      if(this.isLogin) {
        this.$router.push("collection");
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
          this.saveUserInfo(res.data.info);
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
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
  padding-bottom:100px;
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
    &.fixed {
      position: fixed;
    }
    &.border-b {
      border-bottom: 1px solid #efefef;/*no*/
    }
    &.bg-col{
      background-color: #317db9;
    }
    &.bg{
      background-color:rgba(0,0,0,0)
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
      img {
        font-size: 0;
        margin-bottom: -22px;
        width:66px;
        height:66px;
        border-radius: 100px;
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
  .assets{
    padding-top:10px;
    background-color: #317db9;
  }
  .content{
    width:100%;
    .mine-top{
      background-color: #317db9;
      height:340px;
      .headinfo{
        padding:40px 30px 0;
        .head-img{
          display: block;
          width:130px;
          height:130px;
          border-radius: 100%;
          border:5px solid #5a97c7;
          float:left;
          img{
            width:130px;
            height:130px;
            font-size:0;
            border-radius: 100%;
          }
        }
        .user-info{
          display: inline-block;
          height:130px;
          margin-left:20px;
          margin-top:20px;
          p{
            line-height: 50px;
            color:#fff;
            font-size:30px;
          }
        }
        .user-noinfo{
          display: inline-block;
          height:130px;
          line-height: 130px;
          margin-left:20px;
          color:#fff;
            font-size:30px;
        }
      }
    }
    .mine-card{
      margin:-60px 30px 0;
      padding:30px 0;
      border-radius:10px;
      background-color: #fff;
      .card-row{
        padding:0 40px;
        .nav-title{
          margin-left:20px;
          font-size:30px;
          color:#333333;
        }
        .card-num{
          height:60px;
          line-height: 60px;
          overflow: hidden;
          .card{
            display: inline-block;
            width:60px;
            height:60px;
            i{
              width:57px;
              height:43px;
              margin-bottom:-10px;
            }
          }
          .num{
            color:#ffa936;
            font-size:30px;
            float:right;
          }
        }
        .card-acc{
          height:60px;
          line-height:60px;
          margin-top:20px;
          overflow: hidden;
          .card-account{
            display: inline-block;
            height:60px;
            line-height: 60px;
            font-size:30px;
            i{
              width:52px;
              height:40px;
              margin-bottom:-8px;
            }
          }
          .total{
            color:#ffa936;
            float:right;
            font-size:30px;
          }
          strong{
            color:#ffa936;
            margin-left:10px;
          }
        }
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
        .address,.collection,.service,.score{
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
}
</style>
