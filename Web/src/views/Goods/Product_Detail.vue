<template>
  <div class="gyl-product-detail">
    <div v-title>{{headTitle}}</div>
    <Header :left="headLeft" :title="headTitle"></Header>
    <section class="content">
      <swiper class="pro-swiper" :list="proImgs" v-model="proImgIdx" :loop="true" :auto="true" :show-desc-mask="false" :dots-position="'center'" :dots-class="'pro-dots'"></swiper>
      <div class="pro-detail">
        <p class="pro-name" v-text="commodity.title"></p>
        <p class="pro-sale">BSTK {{commodity.price}}<span class="pro-sale-num">月售{{commodity.saleQuantity}}份</span></p>
      </div>
      <div class="pro-intro">
        <p>商品简介</p>
        <div class="intro-box" v-html="product.description"></div>
      </div>
      <div class="pro-features">
        <p>产品特点</p>
        <div class="features-box" v-html="product.feature"></div>
      </div>
      <div class="pro-standard">
        <p>产品规格</p>
        <div class="standard-box" v-html="product.specification"></div>
      </div>
      <div class="pro-evaluate" v-if="evalList.length>0">
        <p>产品评价</p>
        <div class="evaluate-box">
          <div class="eval-detail" v-for="(eva,i) in evalList" v-bind:key="i">
            <div class="eval-user">
              <img v-if="eva.headPortrait&&eva.headPortrait!=''" :src="eva.headPortrait">
              <img v-else src="../../assets/images/Bg/head-default.png" alt="">
              <div class="user-r fl">
                <p class="fl">{{eva.memberName}}</p>
                <p class="fr">{{new Date(eva.createTime)|dateFormat('YYYY-MM-DD HH:mm')}}</p>
              </div>
              <div class="eval-score fl" :class="'ico-star-' + eva.score"></div>
            </div>
            <div class="eval-judge">{{eva.comment ? eva.comment : '默认好评'}}</div>
          </div>
          <div class="eval-more" @click="evalMore">查看更多 <i class="ico-more"></i></div>
        </div>
      </div>
    </section>
    <footer class="foot">
      <input class="buy-btn" type="button" value="立即购买" @click="clickBuy">
      <div class="number-box">
        <inline-x-number :width="'50px'" button-style="round" :min="minNum" :max="maxNum" v-model="buyNum"></inline-x-number>
      </div>
    </footer>
  </div>
</template>
<script>
import { mapGetters, mapActions, mapMutations } from "vuex";
import { Swiper, InlineXNumber, dateFormat } from "vux";
import Header from '@/components/common/Header';
import { showMsg, loading, valid, versions } from '@/utils/common.js';
import apiUrl from '@/config/apiUrl.js';
var moment = require("moment");
export default {
  data() {
    return {
      headLeft: {
        label: "",
        className: "ico-back"
      },
      headTitle:"",
      proImgIdx: 0,
      proImgs: [],
      productId: 0,
      productType: 0,
      commodity: {},
      product: {},
      buyNum: 1,
      minNum: 1,
      maxNum: 0,
      evalList:[] // 产品评价列表
    };
  },
  filters: {
    dateFormat
  },
  components: { Swiper, Header, InlineXNumber },
  computed: {
    ...mapGetters({
      getToken: 'getToken',
      getLoginUser: 'getLoginUser'
    })
  },
  methods: {
    ...mapActions({}),
    login: function() {
      //TODO 登录/注册
      this.$router.push("login");
    },
    getProduct:function() {
      // TODO 获取商品信息
      let param = {
        commodityId: this.productId
      };
      this.$httpPost(apiUrl.getCommodityInfo, param).then((res) => {
        if(res.status.code==0&&res.data) {
          this.commodity = res.data.commodity;
          this.product = res.data.product;
          this.maxNum = res.data.commodity.inventoryQuantity;
          this.headTitle = res.data.commodity.title;
          if(res.data.pics) {
            res.data.pics.forEach(item => {
              this.proImgs.push({url: 'javascript:', img: item.url, title: item.title});
            });
          }
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    clickBuy:function() {
      // TODO 点击购买
      if(this.buyNum<=0) {
        showMsg('请选择购买数量');
        return;
      }
      this.$router.push({name:"order_place", query: {proId: this.productId, proType: this.commodity.commodityType, num: this.buyNum}});
    },
    getProductEvaluate: function(flag) {
      //TODO 查询商家怕评价列表
      let param = {
        commodityId: this.productId,
        commodityType: this.productType,
        pageIndex:0,
        pageSize:3
      };
      this.$httpPost(apiUrl.getCommodityCommens, param).then((res) => {
        if(res.status.code==0&&res.data) {
          this.evalList = res.data.comments;
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    evalMore:function() {
      // TODO 产看更多商家评价
      this.$router.push({name:'product_evaluate', query:{id:this.productId, proType:this.productType}});
    }
  },
  mounted() {
    this.productId = this.$route.query.proId||"";
    this.productType = this.$route.query.proType||"";
    if(this.productId!="") {
      this.getProduct();
      this.getProductEvaluate();
    }
  }
};
</script>
<style lang="less">
html,body{
  height: 100%;
  overflow: hidden;
}
.gyl-product-detail{
  height: 100%;
  overflow: hidden;
  .content{
    box-sizing:border-box;
    position: relative;
    width:100%;
    height: calc(~"100% - 180px");
    background-color: #F3F4F6;
    overflow-y: auto;
    -webkit-overflow-scrolling: touch;
    .pro-swiper{
      width: 100%;
      height: 550px;
      .vux-swiper{
        width: 100%;
        height: 100% !important;
      }
    }
    .pro-dots{
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
    .pro-detail{
      padding: 35px 30px 30px 30px;
      background-color: #fff;
      border-bottom: 20px solid #f6f6f6;
      .pro-name{
        line-height: 55px;
        font-size: 34px;
        font-weight: 600;
        color: #333333;
      }
      .pro-sale{
        line-height: 66px;
        font-size: 26px;
        color: #ffa936;
        font-weight: 600;
        overflow: hidden;
        span{
          float: right;
          font-size: 22px;
          color: #999999;
          font-weight: 500;
        }
      }
    }
    .pro-intro, .pro-features, .pro-standard, .pro-evaluate{
      min-height: 160px;
      padding: 30px;
      background-color: #fff;
      border-bottom: 20px solid #f6f6f6;
      &>p{
        font-size: 30px;
        line-height: 30px;
        height: 30px;
        border-left:4px solid #317db9;
        padding-left: 10px;
        color: #666666;
        margin-bottom: 30px;
      }
      .intro-box,.features-box,.standard-box{
        line-height: 40px;
      }
    }
    .pro-evaluate{
      padding-bottom: 0;
      .evaluate-box{
        .eval-detail{
          padding: 20px 0;
          border-bottom: 1px solid #efefef; /*no*/
          .eval-user{
            padding: 15px 0;
            overflow: hidden;
            img{
              display: block;
              float: left;
              width: 100px;
              height: 100px;
              margin-right: 20px;
              border-radius: 50%;
            }
            .user-r{
              width: calc(~"100% - 120px");
              height: 60px;
              overflow: hidden;
              p{
                font-size: 24px;
                color: #999;
                line-height: 60px;
              }
            }
            .eval-score{
              display: block;
              width: 60%;
              height: 20px;
              margin: 10px 0;
              background-size: 31% auto;
            }
          }
          .eval-judge{
            line-height: 40px;
            font-size: 30px;
            color: #555;
            padding: 10px 0;
          }
        }
        .eval-more{
          height: 80px;
          line-height: 80px;
          font-size: 26px;
          color: #999;
          text-align: center;
          i{
            width: 14px;
            height: 24px;
            vertical-align: middle;
            margin-top: -5px;
          }
        }
      }
    }
  }
  .foot{
    position: absolute;
    bottom: 0;
    width: 100%;
    height: 98px;
    background: #fff;
    .buy-btn{
      width: 375px;
      font-size: 30px;
      line-height: 98px;
      color: #fff;
      background-color: #317db9;
      text-align: center;
      display: block;
      float: left;
      border-radius: 0;
    }
    .number-box {
      width: 375px;
      height: 70px;
      float: left;
      text-align: center;
      padding-top: 25px;
      .vux-number-round .vux-number-selector-plus {
        border: 1px solid #999999;
        background-color: #999999;
      }
      .vux-number-selector-plus svg {
        fill: #fff;
      }
      .vux-number-selector-sub svg, .vux-number-round .vux-number-selector.vux-number-disabled svg  {
        fill: #999;
      }
      .vux-number-round .vux-number-selector.vux-number-disabled, .vux-number-round .vux-number-selector-sub{
        border-color: #999999;
      }
    }
  }
}
</style>