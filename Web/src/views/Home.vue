<template>
  <div class="gyl-home">
      <div v-title>人民电商</div>
      <header class="head fixed border-b">
        <!-- <div class="head-l" @click="showService">
          <span><i class="ico-service"></i></span>
          <a href="javascript:;"></a>
        </div> -->
        <div class="head-title"><i class="ico-logo"></i>人民电商</div>
        <!-- <div class="head-r" @click="newsClick">
          <a href="javascript:;"></a>
          <span><i class="ico-news"></i></span>
        </div> -->
      </header>
      <section class="content">
        <swiper class="home-swiper" :list="shopImgs" v-model="shopImgIdx" @on-index-change="shopImgOnIndexChange" :interval="5000" :loop="true" :auto="true" :show-desc-mask="false" :dots-position="'center'" :dots-class="'home-dots'"></swiper>
        <div class="merchants">
          <p class="title"><i class="ico-supplier"></i>附近商家</p>
          <div class="mer-box" v-for="(mer,index) in merchantsList" :key="index" @click="jumpProDetail(mer.id,mer.merchType)">
            <img class="mer-img" :src="mer.logoPic" alt="">
            <div class="mer-detail">
              <p class="mer-name">{{mer.merchName}}</p>
              <div class="mer-sale">
                <p class="mer-evaluate">
                  <i :class="startClass(mer.praise)"></i>
                </p>
                <p class="mer-sale-num">月售{{mer.salesVolume}}单</p>
              </div>
            </div>
          </div>
        </div>
      </section>
      <NavBar :isShow="true"></NavBar>
      <!-- <PopWin ref="service" :isShow="false" :datas="serviceData"></PopWin> -->
      <!-- <CommingSon ref="comson" :imgUrl="imgurl"></CommingSon> -->
  </div>
</template>
<script>
import { mapGetters, mapActions, mapMutations } from "vuex";
import { Swiper } from "vux";
import PopWin from '@/components/common/PopWin';
import NavBar from '@/components/common/NavBar';
import CommingSon from '@/components/common/CommingSon';
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
      merchantsList:[]
    };
  },
  components: { NavBar, PopWin, CommingSon, Swiper },
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
        title: "欧罗巴聚财",
        desc: "欢迎您体验欧罗巴聚财",
        link: window.location.href,
        imgUrl: this.headPortrait,
        localUrl: url
      };
      // weixin.wxChat(this, param);
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
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    }
  },
  mounted() {
    this.getHomeBanner();
    this.getShopInfo();
    if(versions.ios&&versions.wx) {
      //IOS终端&微信
      // this.initWxChat();
    }
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
      border-bottom:1px solid #e3e3e3;/*no*/
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
      i{
        display: inline-block;
        width: 54px;
        height: 54px;
        vertical-align: middle;
        background-size: 100% 100%;
        margin-right: 15px;
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
    .merchants{
      padding: 10px 30px;
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
      .mer-box{
        padding: 30px 20px;
        overflow: hidden;
        background-color: #fff;
        margin-bottom: 20px;
        border-radius: 10px;
        .mer-img{
          width: 130px;
          height: 130px;
          border-radius: 50%;
          margin-right: 15px;
          display: block;
          float: left;
        }
        .mer-detail{
          float: left;
          padding: 10px 0;
          .mer-name{
            height: 58px;
            line-height: 58px;
            font-size: 30px;
            color: #333333; 
          }
          .mer-sale{
            line-height: 44px;
            font-size: 22px;
            color: #999999;
            p{
              float: left;
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
    }
  }
}
</style>