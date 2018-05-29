<template>
  <div class="gyl-merchants">
    <div v-title>{{headTitle}}</div>
    <Header :border="true" :title="headTitle" :left="headLeft" :right="headRight" :rightFn="collection">
    </Header>
    <section class="content">
      <div class="merchant-bg">
        <img v-if="merchantBgImg!=''" class="img-bg" :src="merchantBgImg" alt="">
        <img v-else class="img-bg" src="../../assets/images/Bg/merchant-bg@2x.png" alt="">
        <div class="mer-logo" @click="openMerchant">
          <img class="img-logo" :src="merchant.logoPic" :alt="merchant.merchName">
          <div class="mer-name">
            <p v-text="merchant.merchName"></p>
            <p class="mer-desc" v-text="merchant.briefIntro"></p>
          </div>
        </div>
      </div>
      <div class="notice">
        <span class="i-notice fl"><i class="ico-notice"></i></span>
        <div class="title fl">公告</div>
        <div class="message fl" > 
          <msgScroll :id="'infoMsg'" ref="msgScroll" :msg="merchant.notice"></msgScroll>
        </div>
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
import msgScroll from '@/components/messageScroll/index';
import { md5 } from 'vux';
export default {
  data() {
    return {
      headTitle: "",
      headLeft: {
        label: "",
        className: "ico-back"
      },
      headRight: {
        label: "",
        className: "ico-collect"
      },
      isCollect:0, // 是否收藏（默认未收藏）
      merchantId: "",
      merchantType: 0,
      merchant: {},
      merchantProduct: null,
      cardMerchant: {},
      cardMerchantProduct: null,
      merchantBgImg:""
    };
  },
  computed:{
    ...mapGetters(["getLoginUser"])
  },
  components: {
    Header, msgScroll
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
          this.headTitle = res.data.merch.merchName;
          this.isCollect = res.data.isCollection ? res.data.isCollection : 0;
          this.collectChange();
          this.merchantBgImg = res.data.pics&&res.data.pics.length>0?res.data.pics[0].url:"";
          this.$refs.msgScroll.scroll();
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
          this.headTitle = res.data.merchant.merchName;
          this.cardMerchantProduct = res.data.puCardTypes;
          this.merchantBgImg = res.data.pics&&res.data.pics.length>0?res.data.pics[0].url:"";
          this.isCollect = res.data.isCollection ? res.data.isCollection : 0;
          this.collectChange();
          this.$refs.msgScroll.scroll();
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
    },
    collection:function() {
      // TODO 收藏按钮
      this.userId = this.getLoginUser?this.getLoginUser.id:"";
      if(!this.userId) {
        showMsg('请先登录');
        return;
      }
      let param = {
        collectionId: this.merchantId,
        collectionType:1
      };
      this.$httpPost(apiUrl.addCollection, param).then((res) => {
        if(res.status.code==0&&res.data) {
          this.isCollect = res.data.operate==1?1:0;
          this.collectChange();
          showMsg(res.status.message);
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    collectChange:function() {
      if(this.isCollect) {
        this.headRight.className = 'ico-collected';
      } else {
        this.headRight.className = 'ico-collect';
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
  },
  beforeDestroy() {
    this.$refs.msgScroll.destroyScroll();
  },
  activated() {
    this.$refs.msgScroll.scroll();
  }
};
</script>
<style lang="less">
html,body{
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.gyl-merchants{
  width: 100%;
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
      overflow: hidden;
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
          border-radius:20px;
          float:left;
        }
        .mer-name{
          display: block;
          width:520px;
          height:130px;
          padding:15px 0;
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
            line-height: 36px;
            word-wrap: break-word;
            color:#e9e8e8;
            margin-top: 10px;
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
      overflow: hidden;
      span{
        height:50px;
        line-height: 50px;
      }
      .i-notice{
        display: inline-block;
        width:64px;
        i{
          width:44px;
          height:36px;
          margin-bottom:-8px;
        }
      }
      .title{
        width:80px;
        height:48px;
        line-height: 48px;
        color:#317db9;
        margin-right:20px;
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
        width:200px;
        height:200px;
        float:left;
        border: 1px solid #efefef; /*no*/
        border-radius: 14px;
        overflow: hidden;
        margin-right: 20px;
        img{
          width:100%;
          height:100%;
        }
      }
      .pro-info{
        float: left;
        width: calc(~"100% - 225px");
        overflow: hidden;
        .mer-title{
          height:45px;
          line-height:45px;
          color:#333;
          font-size:30px;
          margin-top: 10px;
        }
        .pro-title{
          height:40px;
          line-height: 40px;
          color:#666;
          font-size:26px;
          overflow: hidden;
          white-space: nowrap;
          text-overflow: ellipsis;
        }
        .pro-count{
          display:block;
          width:280px;
          margin-top:10px;
          float:left;
          .pro-money{
            height:40px;
            line-height: 40px;
            color:#ffa936;
            font-size:30px;
            overflow: hidden;
            margin-bottom: 10px;
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
          margin-top:20px;
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
