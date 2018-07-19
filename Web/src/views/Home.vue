<template>
  <div class="gyl-home">
      <div v-title>买卖大集</div>
      <header class="head fixed border-b">
        <div class="head-title"><a href="https://www.genyuanlian.com/"><i class="ico-logo"></i></a>买卖大集</div>
      </header>
      <homeSkeleton v-if="!init"></homeSkeleton>
      <section v-else class="content">
        <swiper class="home-swiper" :list="shopImgs" v-model="shopImgIdx" @on-index-change="shopImgOnIndexChange" :interval="5000" :loop="true" :auto="true" :show-desc-mask="false" :dots-position="'center'" :dots-class="'home-dots'"></swiper>
        <div class="activity">
          <div class="act-title"><i class="ico-title-l m-r10"></i><span>好货</span><i class="ico-title-r m-l10"></i></div>
          <div class="act-panic" @click="goSnatch"><img src="../assets/images/Bg/panic-buying.png" alt=""><span>抢购专区</span></div>
          <div class="act-auction" @click="goAuction"><img src="../assets/images/Bg/auction.png" alt=""><span>拍卖专场</span></div>
        </div>
        <div class="merchants">
          <p class="title hide"><i class="ico-supplier"></i>附近商家</p>
          <div class="mer-act">
            <div class="act-title"><i class="ico-title-l m-r10"></i><span>商家</span><i class="ico-title-r m-l10"></i></div>
          </div>
          <div class="mer-box" v-for="(mer,index) in merchantsList" :key="index" @click="jumpProDetail(mer.id,mer.merchType)">
            <img class="mer-img" :src="mer.logoPic" alt="">
            <div class="mer-detail">
              <p class="mer-name">{{mer.merchName}}</p>
              <div class="mer-sale">
                <p class="mer-evaluate">
                  <i :class="startClass(mer.praise)"></i>
                </p>
                <p class="mer-sale-num ">月售{{mer.salesVolume}}单</p>
              </div>
            </div>
          </div>
          <p class="hope">更多产品即将上线~</p>
        </div>
      </section>
      <NavBar :isShow="true" :isLogin="isLogin"></NavBar>
  </div>
</template>
<script>
import { mapGetters, mapActions, mapMutations } from "vuex";
import { Swiper } from "vux";
import PopWin from '@/components/common/PopWin';
import NavBar from '@/components/common/NavBar';
import CommingSon from '@/components/common/CommingSon';
import homeSkeleton from './Skeleton/home.skeleton.vue';
import { showMsg, loading, valid, versions } from '@/utils/common.js';
import apiUrl from '@/config/apiUrl.js';
import weixin from "@/utils/wechat.js";
var moment = require("moment");
export default {
  data() {
    return {
      imgurl:'',
      shopImgIdx: 0,
      shopImgs:[],
      merchantsList:[],
      init:false,
      isLogin:false  // 判断是否登录
    };
  },
  components: { NavBar, PopWin, CommingSon, Swiper, homeSkeleton },
  computed: {
    ...mapGetters({
      getToken: 'getToken',
      getLoginUser: 'getLoginUser'
    })
  },
  methods: {
    ...mapMutations({}),
    ...mapActions({
      saveUserInfo: 'saveUserInfo'
    }),
    login: function() {
      //TODO 登录/注册
      this.$router.push("login");
    },
    register:function() {
      //TODO 注册
      this.$router.push("register");
    },
    initWxChat: function() {
      let url = window.localStorage.getItem("LocalUrl")||window.location.href;
      let param = {
        title: "买卖大集",
        desc: "欢迎您体验买卖大集电商",
        link: window.location.href,
        imgUrl: window.location.origin+"/gylshop/static/Images/gyl-logo.png",
        localUrl: url
      };
      weixin.wxChat(this, param);
    },
    shopImgOnIndexChange:function(index) {
      // TODO 轮播滚动时当前页面的索引值
      this.shopImgIdx = index;
    },
    startClass:function(score) {
      //TODO 处理评分class
      return 'ico-star-' + score;
    },
    jumpProDetail:function(id, type) {
      // TODO 跳转至商品详情页面
      this.$router.push({name:'merchant_info', query:{id:id, type:type}});
    },
    getHomeBanner:function() {
      // TODO 首页信息获取
      this.$httpPost(apiUrl.getHomeBanner, {}).then((res) => {
        if(res.status.code==0&&res.data) {
          let data = res.data.images;
          data.forEach(element => {
            this.shopImgs.push({
              img:element,
              url:'javascript:'
            });
          });
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    getShopInfo:function() {
      // TODO 获取商户列表
      this.$httpPost(apiUrl.getMerchantList, {}).then((res) => {
        if(res.status.code==0&&res.data) {
          this.merchantsList = res.data.result;
          this.init = true;
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    goSnatch:function(id, type) {
      // TODO 跳转抢购
      this.$router.push({name:'snatch_block'});
    },
    goAuction:function(id, type) {
      // TODO 跳转拍卖
      this.$router.push({name:'auction_block'});
    }
  },
  mounted() {
    this.getHomeBanner();
    this.getShopInfo();
    if(versions.wx) {
      //IOS终端&微信
      this.initWxChat();
    };
    if(this.getLoginUser && this.getLoginUser.id) {
      this.isLogin = true;
    } else {
      this.isLogin = false;
    };
  }
};
</script>
<style lang="less">
html,body{
  height: 100%;
  overflow: hidden;
}
.gyl-home{
  height: 100%;
  overflow: hidden;
  .head{
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
    width:100%;
    height:86px;
    line-height: 86px;
    color: #317db9;
    box-sizing: border-box;
    background-color: #fff;
    &.fixed{
      position: fixed;
      z-index: 9;
    }
    &.border-b{
      border-bottom:1px solid #efefef;/*no*/
    }
    // .head-l{
    //   width:130px;
    //   padding-left:40px;
    //   font-size: 32px;
    //   overflow: hidden;
    //   span{
    //     display: block;
    //     width:50px;
    //     height: 90px;
    //     line-height: 90px;
    //     float:left;
    //   }
    //   i{
    //     font-size:0;
    //     margin-bottom:-8px;
    //     width: 36px;
    //     height: 36px;
    //   }
    //   a{
    //     display: inline-block;
    //     text-decoration: none;
    //     height: 90px;
    //     line-height: 90px;
    //     color:#000;
    //   }
    // }
    .head-title{
      flex:1;
      text-align: center;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      font-size: 27px;
      line-height: 86px;
      letter-spacing: 2px;
      a{
        display: inline-block;
        width: 54px;
        height:86px;
        line-height: 86px;
        margin-right: 15px;
      }
      i{
        display: inline-block;
        width: 54px;
        height: 54px;
        vertical-align: middle;
        background-size: 100% 100%;
        margin-top: -5px;
      }
    }
    // .head-r{
    //   width:130px;
    //   padding-right:40px;
    //   font-size: 32px;
    //   overflow: hidden;
    //   span{
    //     display: block;
    //     width:50px;
    //     height: 90px;
    //     line-height: 90px;
    //     float:right;
    //   }
    //   i{
    //     font-size:0;
    //     margin-bottom:-8px;
    //     width: 36px;
    //     height: 36px;
    //   }
    //   a{
    //     display: inline-block;
    //     text-decoration: none;
    //     height: 90px;
    //     line-height: 90px;
    //     color:#000;
    //   }
    // }
  }
  .content{
    box-sizing:border-box;
    position: relative;
    width:100%;
    height: 100%;
    padding-bottom:118px;
    background-color: #F3F4F6;
    overflow-y: auto;
    -webkit-overflow-scrolling: touch;
    .home-swiper{
      margin-top: 86px;
      width: 100%;
      height: 360px;
    }
    .home-dots{
      a{
        margin-left: 16px;
        i{
          width: 30px;
          height: 4px;
          background-color: rgba(255,255,255,.4);
        }
        .active{
          background-color: #317db9 !important;
        }
      }
    }
    .activity{
      background-color: #fff;
      margin-top:20px;
      text-align: center;
      overflow: hidden;
      padding:20px 30px 40px;
      .act-title{
        height:60px;
        margin-bottom:20px;
        span{
          display: inline-block;
          height:60px;
          line-height: 60px;
          color:#555;
          font-size:34px;
          width:80px;
          margin: 0 auto;
        }
        i{
          width:50px;
          height:10px;
          margin-bottom:4px;
        }
      }
      .act-panic{
        width:335px;
        height:260px;
        float:left;
        img{
          width:100%;
          height:100%;
          float:left;
        }
        span{
          display: block;
          position: absolute;
          color:#fff;
          font-size:30px;
          margin-top:20px;
          margin-left:20px;
        }
      }
      .act-auction{
        width:335px;
        height:260px;
        margin-left:355px;
        img{
          width:100%;
          height:100%;
          float:left;
        }
        span{
          display: block;
          position: absolute;
          color:#fff;
          font-size:30px;
          margin-top:20px;
          margin-left:20px;
        }
      }
    }
    .merchants{
      padding: 20px 30px 10px;
      background-color: #fff;
      margin-top:20px;
      .title{
        flex:1;
        overflow: hidden;
        font-size: 26px;
        line-height: 80px;
        color: #317db9;
        i{
          display: inline-block;
          width: 40px;
          height: 40px;
          vertical-align: middle;
          background-size: 100% 100%;
          margin-right: 15px;
          margin-top: -5px;
        }
      }
      .mer-act{
        background-color: #fff;
        text-align: center;
        overflow: hidden;
        .act-title{
          height:60px;
          margin-bottom:20px;
          span{
            display: inline-block;
            height:60px;
            line-height: 60px;
            color:#555;
            font-size:34px;
            width:80px;
            margin: 0 auto;
          }
          i{
            width:50px;
            height:10px;
            margin-bottom:4px;
          }
        }
        .act-auction{
          width:335px;
          height:260px;
          float:left;
          img{
            width:100%;
            height:100%;
          }
        }
        .act-panic{
          width:335px;
          height:260px;
          margin-left:355px;
          img{
            width:100%;
            height:100%;
          }
        }
      }
      .mer-box{
        padding: 30px 20px;
        overflow: hidden;
        background-color: #fff;
        margin-bottom: 20px;
        border-radius: 10px;
        border:1px solid #e8e8e8;/*no*/
        .mer-img{
          width: 120px;
          height: 120px;
          border-radius: 14px;
          margin-right: 20px;
          display: block;
          float: left;
        }
        .mer-detail{
          float: left;
          width: calc(~"100% - 140px");
          // padding: 10px 0;
          .mer-name{
            height: 70px;
            line-height: 70px;
            font-size: 30px;
            color: #333333;
          }
          .mer-sale{
            line-height: 44px;
            font-size: 22px;
            color: #999999;
            overflow: hidden;
            p{
              float: left;
            }
            .mer-sale-num{
              float: right;
            }
            .mer-evaluate{
              line-height: 44px;
              margin-right: 45px;
              i{
                display: inline-block;
                vertical-align: middle;
                margin-top: -10px;
                width: 125px;
                height: 20px;
              }
            }
          }
        }
      }
      .hope{
        height: 80px;
        line-height: 80px;
        font-size: 24px;
        color: #ccc;
        text-align: center;
        margin-top: 20px;
      }
    }
  }
  .ico-title-l{
    display: inline-block;
    background: url('../assets/images/Icon/ico-title-l@2x.png') center no-repeat;
    background-size:auto 100%;
  }
  .ico-title-r{
    display: inline-block;
    background: url('../assets/images/Icon/ico-title-r@2x.png') center no-repeat;
    background-size:auto 100%;
  }
}
</style>
