<template>
  <div class="gyl-order-detail">
    <div v-title>确认订单</div>
    <Header :border="true" :title="headTitle" :left="headLeft" ></Header>
    <section class="content">
      <div v-if="merchant.addressRequire==1" class="order-address" @click="openAddress">
        <div class="person-infor">
          <div class="no-address" v-if="!deliver">添加收货地址</div>
          <div v-if="deliver">
            <p>{{deliver.addrName + '  ' +deliver.addrPhone}}</p>
            <p class="per-address">{{deliver.address}}</p>
          </div>
        </div>
      </div>
      <div v-if="productType==1||merchant.addressRequire==0" class="order-send">
        <div class="order-row no-border-bot">
          <div class="row-key">配送</div>
          <div class="row-val">无需配送</div>
        </div>
      </div>
      <div v-else class="order-send">
        <div class="order-row">
          <div class="row-key">配送</div>
          <div class="row-val">{{express.expressCom}}</div>
        </div>
        <div class="order-row">
          <div class="row-key">预计送达时间</div>
          <div class="row-val">{{express.arriveTime}}</div>
        </div>
        <div class="order-row no-border-bot">
          <div class="row-key">配送方式</div>
          <div class="row-val">{{express.distMode}}</div>
        </div>
      </div>
      <div class="order-pay-type">
        <div class="order-row">
          <div class="row-key">支付方式</div>
          <div class="row-val">{{productType==1?'微信/支付宝':'提货卡'}}</div>
        </div>
        <div class="order-row no-border-bot">
          <div class="row-key">优惠券抵扣</div>
          <div class="row-val">无</div>
        </div>
      </div>
      <div class="order-paied">
        <div class="paied-top">
          <div class="or-mer">
            <img class="or-img" :src="merchant.merchantLogo" alt="">
            <p class="mer-detail"><span class="mer-name" v-text="merchant.merchantName"></span></p>
          </div>
          <div class="or-pro" v-for="(item,i) in productList" v-bind:key="i">
            <div class="pro-img"><img :src="item.commodityLogo" alt=""></div>
            <div class="pro-detail">
              <p class="pro-title" v-text="item.commodityName"></p>
              <p class="pro-price">
                <span>{{item.commodityType==1?'&yen;':'源点'}}{{item.commodityPrice}}</span>
                <span class="pro-num"><inline-x-number :width="'50px'" button-style="round" :min="minNum" :max="item.inventoryQuantity" v-model="item.buyNum"></inline-x-number></span>
              </p>
            </div>
            <div class="pro-trace" v-if="item.traceSource">
              <span class="lab-title">溯源信息</span>
              <span class="trace-r"><a class="trace-link" :href="item.traceSource">点击查看溯源</a></span>
            </div>
          </div>
        </div>
        <div class="order-row">
          <div class="row-key">配送费</div>
          <div class="row-val" v-text="express.distFee"></div>
        </div>
        <div class="order-row" v-if="merchant.walletAddressRequire==1">
          <div class="row-key">钱包地址</div>
          <div class="row-val"><input type="text" v-model="walletAddr" placeholder="算力收益钱包地址"></div>
        </div>
        <div class="order-row no-border-bot">
          <div class="row-key">订单备注</div>
          <div class="row-val"><input type="text" v-model="remark" placeholder="输入订单备注信息"></div>
        </div>
      </div>
    </section>
    <footer class="foot">
      <p class="left-btn">待支付<strong>{{merchant.merchantType==2?'&yen;':'源点'}}{{totalMoney}}</strong></p>
      <input class="buy-btn" type="button" value="去支付" @click="checkBalance">
    </footer>
    <Popup ref="popWin" :popObj="popObj"></Popup>
  </div>
</template>
<script>
import { mapActions, mapGetters } from "vuex";
import { dateFormat, InlineXNumber } from "vux";
import { showMsg, valid } from '@/utils/common.js';
import apiUrl from '@/config/apiUrl.js';
import Header from '@/components/common/Header';
import Popup from '@/components/popup';
var moment = require('moment');
export default {
  data() {
    return {
      headTitle: "确认订单",
      headLeft: {
        label: "",
        className: "ico-back"
      },
      timer:null,
      minNum: 1,
      maxNum: 0,
      productId:"",
      productType: 0,
      productList: [],
      merchant: {},
      buyNum: 0,
      amount: 0,
      money: 0,
      totalMoney: 0,
      orderNo:"",
      remark: "",
      walletAddr:"",
      addressId:"",
      deliver: null,
      express:{
        expressCom:"顺丰快递",
        arriveTime: moment().add(3, 'd').format('YYYY-MM-DD HH:mm'),
        distMode:'商家配送',
        distFee:0
      },
      addressList:[],
      userId:"",
      sumBalance:0,
      referraCode:"",
      merchId:"",
      popObj:{
        popTitle:'提示',
        popText:'提货卡余额不足，请点确定去购买',
        popIndex:2,
        popSure:this.buyCardPath
      }
    };
  },
  components: {
    Header, InlineXNumber, Popup
  },
  computed:{
    ...mapGetters(["getLoginUser", "getUserInfo"])
  },
  methods: {
    ...mapActions({
      saveAddressBackUrl: "saveAddressBackUrl"
    }),
    openAddress: function() {
      //TODO 选择快递地址
      this.saveAddressBackUrl(this.$route.fullPath);
      this.$router.push({name:"address"});
    },
    getAddressList: function() {
      //TODO 查询地址
      this.$httpPost(apiUrl.getMemberAddressList, {}).then((res) => {
        if(res.status.code==0&&res.data) {
          this.addressList = res.data.result;
          if(this.addressList.length>0) {
            this.deliver = {};
            this.addressId = this.addressList[0].id;
            this.deliver.addrName = this.addressList[0].mobile;
            this.deliver.addrPhone = this.addressList[0].receiver;
            this.deliver.address = this.addressList[0].areaName+this.addressList[0].address;
          }
        }else{
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    computePrice: function() {
      //TODO 计算总价
      this.productList.forEach((item) => {
        this.$set(item, "buyNum", +this.buyNum);
        this.amount = item.commodityPrice;
        this.totalMoney += parseFloat(item.commodityPrice*Number(item.buyNum));
      });
      if(this.productType==1) {
        this.totalMoney = parseFloat(this.totalMoney + this.express.distFee);
      }else {
        this.totalMoney = parseFloat(this.totalMoney + this.express.distFee);
      }
    },
    getProductDetail: function(flag) {
      //TODO 查询商家和产品详情
      let param = {
        commodityId: this.productId,
        commodityType: this.productType
      };
      this.$httpPost(apiUrl.getOrderCommodity, param).then((res) => {
        if(res.status.code==0&&res.data) {
          this.merchant = res.data.result;
          this.productList = res.data.result.commodityList;
          this.computePrice();
          if(this.merchant.addressRequire==1) {
            this.loadAddress();
          }
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    placeOrder: function() {
      //TODO 下单
      this.productList.forEach((item) => {
        this.buyNum = item.buyNum;
      });
      if(this.merchant.addressRequire==1&&this.addressId=="") {
        showMsg("请添加收获地址信息");
        return;
      }
      if(this.buyNum<1) {
        showMsg("请选择数量");
        return;
      }
      if(this.merchant.walletAddressRequire==1&&this.walletAddr=="") {
        showMsg("请输入钱包地址");
        return;
      }
      if(this.merchant.walletAddressRequire==1&&!valid.walletAddress(this.walletAddr)) {
        showMsg("请输入正确的钱包地址");
        return;
      }
      let param = {
        commodityType: this.productType,
        commodityId: this.productId,
        saleCount: this.buyNum,
        amount: this.totalMoney,
        remark: this.remark,
        addressId: this.addressId,
        walletAddress: this.walletAddr,
        referraCode: this.referraCode
      };
      this.$httpPost(apiUrl.createOrder, param).then((res) => {
        if(res.status.code==0&&res.data) {
          this.orderNo = res.data.orderNo;
          localStorage.removeItem("address");
          this.$router.push({name:"pay_order", query: {orderNo: this.orderNo}});
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    loadAddress: function() {
      //TODO 初始化加载地址
      if(window.localStorage.getItem("address")) {
        let address = JSON.parse(localStorage.getItem("address"));
        if(address) {
          this.deliver = {};
          this.addressId = address?address.id:"";
          this.deliver.addrName = address?address.mobile:"";
          this.deliver.addrPhone = address?address.receiver:"";
          this.deliver.address = address?address.areaName+address.address:"";
        }
      }else {
        this.getAddressList();
      }
    },
    checkBalance: function() {
      //TODO 查询BSTK余额
      let param = {};
      this.$httpPost(apiUrl.getPucardBalance, param).then((res) => {
        if(res.status.code==0&&res.data) {
          this.sumBalance = res.data.sumBalance;
          this.merchId = res.data.puCardMerchId;
          if(this.productType == 1) {
            this.placeOrder();
          } else {
            if(this.totalMoney > this.sumBalance) {
              this.$refs.popWin.openWin();
            } else {
              this.placeOrder();
            }
          }
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    buyCardPath: function() {
      //TODO 购买提货卡页面
      if(this.merchId) {
        this.$router.push({name:"merchant_info", query:{id:this.merchId, type:2}});
      }
    }
  },
  mounted() {
    this.userId = this.getLoginUser?this.getLoginUser.id:"";
    this.productType = this.$route.query.proType||0;
    this.productId = this.$route.query.proId||"";
    this.buyNum = this.$route.query.num||1;
    this.referraCode = this.$route.query.code||"";
    if(this.userId=="") {
      this.$router.push("login");
      return;
    }
    if(this.productId!="") {
      this.getProductDetail();
    }
  },
  watch: {
    productList: {
      //重新计算金额
      handler(newValue, oldValue) {
        if(oldValue&&oldValue.length>0) {
          this.totalMoney = 0;
          for (let i = 0; i < newValue.length; i++) {
            this.totalMoney += parseFloat(oldValue[i].commodityPrice*oldValue[i].buyNum);
          }
        }
        if(this.productType==1) {
          this.totalMoney = parseFloat(this.totalMoney);
        }else {
          this.totalMoney = parseFloat(this.totalMoney);
        }
      },
      deep: true
    }
  }
};
</script>
<style lang="less">
html,body{
  height: 100%;
  overflow: hidden;
}

.gyl-order-detail{
  height: 100%;
  overflow: hidden;
  .content{
    box-sizing:border-box;
    width:100%;
    height:calc(~"100% - 186px");
    overflow-x: hidden;
    overflow-y: scroll;
    -webkit-overflow-scrolling: touch;
    background-color: #F3F4F6;
    .order-address{
      padding: 30px 30px 25px 30px;
      margin-bottom: 20px;
      overflow: hidden;
      background: url('../../assets/images/Svg/ico-more-r.svg') no-repeat right center #fff;
      background-size: 14px 24px;
      background-position-x: calc(~"100% - 30px");
      .person-infor{
        width: calc(~"100% - 45px");
        height: auto;
        float: left;
        .no-address{
          height:90px;
          line-height:90px;
          font-size: 30px;
        }
        p{
          line-height:45px;
          font-size: 30px;
          color: #555555;
          &.per-address{
            padding: 5px 0;
            line-height: 35px;
          }
        }
      }
    }
    .order-paied{
      margin-top: 20px;
      margin-bottom: 20px;
      background-color: #fff;
      padding-left:25px;
      .paied-top{
        padding: 0 0 5px;
        border-bottom: 1px solid #efefef; /*no*/
        overflow: hidden;
        .or-mer{
          border-bottom:1px solid #efefef;/*no*/
          padding:10px 30px 10px 0;
          .or-img{
            display: block;
            float: left;
            width: 40px;
            height: 40px;
            border-radius: 10px;
            margin-right: 22px;
            margin-top:20px;
          }
          .mer-detail{
            height:80px;
            line-height: 80px;
            font-size: 30px;
            color: #333;
            vertical-align: middle;
            .mer-r{
              float: right;
              font-size: 30px;
            }
            .mer-price{
              color:#ffa936;              
            }
          }
        }
        .or-pro{
          width:100%;
          overflow: hidden;
          padding:30px 0;
          .pro-img{
            display: block;
            width:120px;
            height:75px;
            float:left;
            border:1px solid #efefef;/*no*/
            border-radius:10px;
            padding:22px 0;
            img{
              display: block;
              width:95px;
              height:75px;
              margin:0 auto;
            }
          }
          .pro-detail{
            margin-left:140px;
            padding-right:30px;
            overflow: hidden;
            .pro-title{
              margin-top:10px;
              height:40px;
              line-height: 40px;
              color:#333;
              font-size:30px;
            }
            .pro-price{
              margin-top:25px;
              height:40px;
              line-height: 40px;
              color:#ffa936;
              font-size:26px;
              .pro-num{
                float:right;
                .vux-number-selector-sub{
                  border: 1px solid #666;/*no*/
                  color:#666;
                }
                .vux-number-selector svg{
                  fill:#666;
                }
                .vux-number-disabled,.vux-number-selector-plus{
                  border: 1px solid #999;/*no*/
                  color:#999;
                }
                .vux-number-disabled svg{
                  fill:#999;
                }
              }
            }
          }
          .pro-trace{
            height:40px;
            line-height: 40px;
            font-size:26px;
            margin-top:20px;
            .lab-title{
              display: inline-block;
              width:120px;
            }
            .trace-r{
              margin-right:30px;
              float:right;
              a{
                text-decoration: none;
                color:#317db9;
              }
            }
          }
        }
      }
    }
    .order-pay-type, .order-send, .order-paied{
      margin-bottom: 20px;
      padding-left: 30px;
      background-color: #fff;
      .order-row{
        font-size: 30px;
        border-bottom: 1px solid #efefef; /*no*/
        overflow: hidden;
        &.no-border-bot{
          border: 0;
        }
      }
      .row-key{
        float: left;
        width: 140px;
        height: 90px;
        line-height: 90px;
        color: #555;
      }
      .row-val{
        float: left;
        width: calc(~"100% - 170px");
        padding: 15px 30px 15px 0;
        line-height: 62px;
        text-align: right;
        color: #555555;
        word-break: break-all;
        input{
          display: block;
          float: right;
          width: 100%;
          height: 62px;
          font-size: 24px;
          line-height: 62px;
          color: #555;
          padding-left: 15px;
          border: 1px solid #efefef; /*no*/
          box-sizing: border-box;
          -moz-box-sizing: border-box;
          -webkit-box-sizing: border-box;
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
    input,p{
      display: block;
      font-size: 30px;
      line-height: 98px;
      color: #fff;
      text-align: center;
      float: left;
      border-radius: 0;
    }
    .left-btn{
      width: calc(~"100% - 250px");
      background-color: #ffa936;
      strong{
        margin-left:20px;
      }
    }
    .buy-btn{
      width: 250px;
      font-size: 30px;
      background-color: #317db9;
    }
  }
}
</style>
