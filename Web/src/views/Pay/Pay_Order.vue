<template>
  <div class="gyl-pay-order">
    <div v-title>订单支付</div>
    <Header :border="true" :title="headTitle" :left="headLeft" ></Header>
    <section class="content">
      <div class="order-info">
        <div class="pay-title">{{order.commodityType==1?'提货卡':'商品'}}购买</div>
        <div class="order-num">
          <span class="lab-title">订单号</span>
          <span class="num-r">{{orderNo}}</span>
        </div>
        <div class="address">
          <span class="lab-title">收货地址</span>
          <span class="add-r" @click="addressClick">
            <span>{{delivery.receiver}} {{delivery.mobile}} 地址信息</span>
            <span class="other"><i class="ico-arr-down" :class="showDetail?'arr-up':''"></i></span>
          </span>
        </div>
        <div class="detail" v-show="showDetail">
          <p>{{delivery.receiver}} {{delivery.mobile}}</p>
          <p>地址信息{{delivery.areaName+delivery.address}}</p>
          <p class="title">{{order.commodityName}}   x{{order.saleCount}}</p>
        </div>
        <div class="pay-money">
          <span class="lab-title">订单金额</span>
          <span class="money">{{order.commodityType==1?'&yen;':'BSTK'}}{{order.amount}}</span>
        </div>
      </div>
      <div class="paytype" v-if="order.commodityType==1">
        <div class="pay-title">选择支付方式</div>
        <div class="wxpay hide">
          <span class="ico-wx"><i class="ico-wxpay"></i></span>
          <span class="lab-title">微信支付</span>
          <span class="pay-radio" @click="payCheck(1)"><i class="ico-radio" :class="payType==1?'checked':''"></i></span>
        </div>
        <div class="alipay" v-show="showMore">
          <span class="ico-ali"><i class="ico-alipay"></i></span>
          <span class="lab-title">支付宝</span>
          <span class="pay-radio" @click="payCheck(2)"><i class="ico-radio" :class="payType==2?'checked':''"></i></span>
        </div>
        <div class="pay-more" @click="payMore">其它支付方式<i class="ico-arr-down" :class="showMore?'arr-up':''"></i></div>
      </div>
      <div class="paytype" v-if="order.commodityType!=1">
        <div class="pay-title">选择支付方式</div>
        <div class="card-pay">
          <span class="ico-card-pay"><i class="ico-cardpay"></i></span>
          <span class="lab-title">提货卡</span>
          <span class="pay-radio" @click="payCheck(3)"><i class="ico-radio" :class="payType==3?'checked':''"></i></span>
        </div>
      </div>
      <div class="btn-pay">
        <input class="pay-confirm" type="button" value="确认支付" @click="orderPay">
      </div>
    </section>
  </div>
</template>
<script>
import { mapActions, mapGetters } from "vuex";
import { showMsg, valid, versions } from '@/utils/common.js';
import apiUrl from '@/config/apiUrl.js';
import Header from '@/components/common/Header';
import { md5 } from 'vux';
export default {
  data() {
    return {
      headTitle: "订单支付",
      headLeft: {
        label: "",
        className: "ico-back"
      },
      showDetail: false,
      showMore: true,
      orderNo:0,
      payType:0,
      productType:0,
      order:{},
      delivery:{
        receiver:"",
        mobile:"",
        areaName:"",
        address:""
      },
      address:"",
      isWxPay:false
    };
  },
  components: {
    Header
  },
  methods: {
    addressClick: function() {
      //TODO 地址展开
      this.showDetail = !this.showDetail;
    },
    payCheck: function(val) {
      //TODO 支付选择
      this.payType = val;
    },
    payMore: function() {
      //TODO 更多支付方式
      this.showMore = !this.showMore;
    },
    getOrderDetail: function() {
      //TODO 查询订单详情
      let param = {
        orderNo: this.orderNo
      };
      this.$httpPost(apiUrl.getPuCardOrderDetail, param).then((res) => {
        if(res.status.code==0&&res.data) {
          this.order = res.data.cardOrder||{};
          this.delivery = res.data.delivery||this.delivery;
          this.productType = this.order.commodityType||0;
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    orderPay: function(flag) {
      //TODO 支付
      if(this.payType==0) {
        showMsg("请选择支付方式");
      }else {
        if(this.payType==1) {
          //微信支付
          this.wxPay();
        }
        if(this.payType==2) {
          //支付宝支付
          this.aliPay();
        }
        if(this.payType==3) {
          //提货卡支付
          this.cardPay();
        }
      }
    },
    wxPay: function() {
      //TODO 微信支付
    },
    aliPay: function() {
      //TODO 支付宝支付
      let param = {
        orderNo: this.orderNo
      };
      this.$httpPost(apiUrl.alipayH5, param).then((res) => {
        if(res.status.code==0&&res.data) {
          let data = res.data.payResult;
          const div = document.createElement('div');
          div.innerHTML = data;
          document.body.appendChild(div);
          document.forms[0].submit();
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    cardPay: function() {
      //TODO 提货卡支付
      let param = {
        orderNo: this.orderNo,
        commodityType: this.productType
      };
      this.$httpPost(apiUrl.orderPay, param).then((res) => {
        if(res.status.code==0&&res.data) {
          let data = res.data;
          this.$router.push({name:"pay_success", query: {orderNo:this.orderNo, proType:this.productType}});
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    }
  },
  mounted() {
    this.isWxPay = versions.wx;
    this.orderNo = this.$route.query.orderNo||0;
    if(this.orderNo!=0) {
      this.getOrderDetail();
    }
  }
};
</script>
<style lang="less">
html,body{
  height: 100%;
  overflow: hidden;
}

.gyl-pay-order{
  height: 100%;
  overflow: hidden;
  .content{
    box-sizing:border-box;
    width:100%;
    height:100%;
    overflow-x: hidden;
    overflow-y: auto;
    background-color: #f3f4f6;
    .pay-title{
      width:100%;
      height:80px;
      line-height: 80px;
      color:#333333;
      font-size:30px;
      background-color: #f3f4f6;
      padding:0 30px;
    }
    .order-info{
      background-color: #fff;
      .order-pro{
        padding-left:30px;
      }
      .order-num{
        height:80px;
        line-height: 80px;
        margin-left:30px;
        padding-right:30px;
        border-bottom:2px solid #efefef;
        overflow: hidden;
        .lab-title{
          width:120px;
          line-height: 80px;
          color:#999999;
          font-size:26px;
        }
        .num-r{
          float:right;
          color:#555555;
          font-size:26px;
        }
      }
      .address{
        height:80px;
        line-height: 80px;
        margin-left:30px;
        padding-right:30px;
        border-bottom:2px solid #efefef;
        overflow: hidden;
        .lab-title{
          width:120px;
          line-height: 80px;
          color:#999999;
          font-size:26px;
        }
        .add-r{
          float:right;
          color:#555555;
          font-size:26px;
        }
        .other{
          width:25px;
          height:80px;
          font-size:0;
          margin-left:20px;
          i{
            width:22px;
            height:14px;
          }
        }
      }
      .detail{
        background-color: #F3F4F6;
        color:#999999;
        font-size:26px;
        padding:30px 25px;
        overflow: hidden;
        p{
          height:auto;
          line-height: 35px;
        }
        .title{
          margin-top:30px;
        }
      }
      .pay-money{
        height:80px;
        line-height: 80px;
        margin-left:30px;
        padding-right:30px;
        overflow: hidden;
        .lab-title{
          width:120px;
          line-height: 80px;
          color:#999999;
          font-size:26px;
        }
        .money{
          float:right;
          color:#555555;
          font-size:26px;
        }
      }
    }
    .paytype{
      background-color: #fff;
      .wxpay,.alipay,.card-pay{
        height:80px;
        line-height: 80px;
        margin-left:30px;
        padding-right:30px;
        border-bottom:2px solid #efefef;
        .lab-title{
          margin-left:25px;
          line-height: 80px;
          color:#555555;
          font-size:26px;
        }
        .num-r{
          float:right;
          color:#555555;
          font-size:26px;
        }
        .pay-radio{
          display: block;
          float:right;
          width:42px;
          height:80px;
          i{
            width:40px;
            height:40px;
            margin-bottom:-10px;
          }
        }
      }
      .ico-wx{
        display:inline-block;
        width:43px;
        height:80px;
        i{
          width:43px;
          height:38px;
          margin-bottom:-5px;
        }
      }
      .ico-ali{
        display:inline-block;
        width:43px;
        height:80px;
        i{
          width:43px;
          height:32px;
          margin-bottom:-5px;
        }
      }
      .ico-card-pay{
        display:inline-block;
        width:43px;
        height:80px;
        i{
          width:43px;
          height:33px;
          margin-bottom:-5px;
        }
      }
      .pay-more{
        display: inline-block;
        width:100%;
        height:60px;
        padding:20px 0;
        line-height: 60px;
        text-align: center;
        color:#999999;
        font-size:26px;
        i{
          width:22px;
          height:14px;
          margin-left:10px;
        }
      }
    }
    .btn-pay{
      height:80px;
      line-height: 80px;
      padding:0 75px;
      margin-top:80px;
      .pay-confirm{
        width:100%;
        height:80px;
        line-height: 80px;
        text-align: center;
        font-size:30px;
        color:#fff;
        background-color: #317db9;
        border-radius:10px;
      }
    }
  }
}
</style>
