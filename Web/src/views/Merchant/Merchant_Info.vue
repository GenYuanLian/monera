<template>
  <div class="gyl-merchants">
    <div v-title>商家信息</div>
    <Header :border="true" :title="headTitle" :left="headLift" :right="headRight" >
    </Header>
    <section class="content">
      <div class="merchant-bg">
        <img class="img-bg" src="../../assets/images/Bg/merchant-bg@2x.png" alt="">
        <div class="mer-logo" @click="openMerchant">
          <img class="img-logo" :src="merchant.logoPic" :alt="merchant.merchName">
          <div class="mer-name">
            <p v-text="merchant.merchName"></p>
            <p class="mer-desc" v-text="merchant.briefIntro"></p>
          </div>
        </div>
      </div>
      <div class="notice">
        <span class="i-notice"><i class="ico-notice"></i></span>
        <span class="title">公告</span>
        <span class="message" v-text="merchant.notice"></span>
      </div>
      <div class="mer-pro" v-show="merchantType==1">
        <div class="product" v-for="(item,idx) in merchantProduct" v-bind:key="idx">
          <div class="pro-img"><img :src="item.logo" :alt="item.title"></div>
          <div class="pro-info">
            <p class="mer-title" v-text="item.title"></p>
            <p class="pro-title" v-text="item.briefIntro"></p>
            <span class="pro-count">
              <p class="pro-money">BSTK{{item.price}}</p>
              <p class="pro-sale">月售{{item.saleQuantity}}份</p>
            </span>
            <span class="pro-buy">
              <input class="btn-buy" type="button" value="购买" @click="buyClick(item.id, item.commodityType)">
            </span>
          </div>
        </div>
      </div>
      <div class="mer-block" v-show="merchantType==2">
        <div class="product" v-for="(item,idx) in cardMerchantProduct" v-bind:key="idx">
          <div class="pro-img"><img :src="item.pic" :alt="item.title"></div>
          <div class="pro-info">
            <p class="mer-title" v-text="item.title"></p>
            <p class="pro-title" v-text="item.remark"></p>
            <span class="pro-count">
              <p class="pro-money">&yen;{{item.price}}</p>
              <p class="pro-sale">月售{{item.salesVolume}}份</p>
            </span>
            <span class="pro-buy">
              <input class="btn-buy" type="button" value="购买" @click="buyClick(item.id, item.commodityType)">
            </span>
          </div>
        </div>
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
      headTitle: "商家列表",
      headLift: {
        label: "",
        className: "ico-back"
      },
      headRight: {
        label: "",
        className: "ico-collect"
      },
      merchantId: "",
      merchantType: 0,
      merchant: {},
      merchantProduct: null,
      cardMerchant: {},
      cardMerchantProduct: null
    };
  },
  components: {
    Header
  },
  methods: {
    openMerchant: function() {
      //TODO 跳转商户详情
      this.$router.push({name:"merchant_detail", query: {id:this.merchantId}});
    },
    getMerchant: function() {
      //TODO 查询其它商家和产品信息
      let param = {
        merchantId: this.merchantId
      };
      this.$httpPost(apiUrl.getCommodityList, param).then((res) => {
        if(res.status.code==0&&res.data) {
          this.merchant = res.data.merch;
          this.merchantProduct = res.data.commoditys;
        }else{
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    getCardMerchant: function() {
      //TODO 查询提货卡商家和产品信息
      let param = {
        merchantId: this.merchantId
      };
      this.$httpPost(apiUrl.getPuCardTypes, param).then((res) => {
        if(res.status.code==0&&res.data) {
          this.merchant = res.data.merchant;
          this.cardMerchantProduct = res.data.puCardTypes;
        }else{
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    buyClick: function(id, type) {
      //商品购买
      if(this.merchantType==1) {
        this.$router.push({name: "product_detail", query: {proId:id, proType:type}});
      }
      if(this.merchantType==2) {
        this.$router.push({name: "order_place", query: {proId:id, proType:type}});
      }
    }
  },
  mounted() {
    this.merchantId = this.$route.query.id||"";
    this.merchantType = this.$route.query.type||0;
    if(this.merchantType==1) {
      this.getMerchant();
    }
    if(this.merchantType==2) {
      this.getCardMerchant();
    }
  }
};
</script>
<style lang="less">
html,body{
  height: 100%;
  overflow: hidden;
}

.gyl-merchants{
  height: 100%;
  overflow: hidden;
  .head-r span>i{
    width:40px;
    height:40px;
  }
  .content{
    box-sizing:border-box;
    width:100%;
    height:100%;
    overflow-x: hidden;
    overflow-y: hidden;
    background-color: #f3f4f6;
    padding-bottom:90px;
    .merchant-bg{
      width:100%;
      height:300px;
      img{
        width:100%;
        height:300px;
      }
      .mer-logo{
        position: absolute;
        top:158px;
        padding:0 30px;
        .img-logo{
          display: block;
          width:160px;
          height:160px;
          border-radius:100%;
          float:left;
        }
        .mer-name{
          display: block;
          width:520px;
          height:130px;
          padding-top:30px;
          margin-left:180px;
          overflow: hidden;
          p{
            height:45px;
            line-height: 45px;
            color:#fff;
            font-size:34px;
          }
          .mer-desc{
            max-height:50px;
            font-size:26px;
            word-wrap: break-word;
            color:#e9e8e8;
          }
        }
      }
    }
    .notice{
      height:50px;
      line-height:50px;
      padding:20px 30px;
      background-color:#fff;
      font-size:24px;
      span{
        height:50px;
        line-height: 50px;
      }
      .i-notice{
        display: inline-block;
        width:65px;
        i{
          width:44px;
          height:36px;
          margin-bottom:-8px;
        }
      }
      .title{
        display: inline-block;
        width:80px;
        height:48px;
        line-height: 48px;
        text-align: center;
        border:2px solid #317db9;
        color:#317db9;
        margin-right:35px;
        border-radius:10px;
      }
      .message{
        color:#333;
        font-size:24px;
        overflow: hidden;
      }
    }
    .mer-pro,.mer-block{
      box-sizing:border-box;
      width:100%;
      height:100%;
      overflow-x: hidden;
      overflow-y: auto;
      -webkit-overflow-scrolling: touch;
      padding-bottom:400px;
    }
    .product{
      margin-top:20px;
      padding:30px;
      background-color: #fff;
      overflow: hidden;
      .pro-img{
        display: block;
        width:180px;
        height:200px;
        float:left;
        img{
          width:100%;
          height:100%;
        }
      }
      .pro-info{
        margin-left:220px;
        overflow: hidden;
        .mer-title{
          height:45px;
          line-height:45px;
          color:#333;
          font-size:30px;
        }
        .pro-title{
          height:40px;
          line-height: 40px;
          color:#666;
          font-size:26px;
          overflow: hidden;
        }
        .pro-count{
          display:block;
          width:280px;
          margin-top:30px;
          float:left;
          .pro-money{
            height:40px;
            line-height: 40px;
            color:#dd3e3e;
            font-size:30px;
            overflow: hidden;
          }
          .pro-sale{
            height:30px;
            line-height: 30px;
            color:#999;
            font-size:24px;
            overflow: hidden;
          }
        }
        .pro-buy{
          position: relative;
          display: block;
          float:right;
          height:70px;
          line-height: 70px;
          margin-top:30px;
          .btn-buy{
            width:160px;
            height:60px;
            line-height: 60px;
            text-align:center;
            background-color: #ffa936;
            color:#fff;
            font-size:30px;
            border-radius: 10px;
          }
        }
      }
    }
  }
}
</style>
