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
          <span class="lab-title">订单详情</span>
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
        <div class="pay-bank" v-if="payExplain||payExplain!=''">
          <div class="pay-tip"><i class="ico-pay-info"></i>支付提示</div>
          <div class="pay-content" v-html="payExplain"></div>
          <!-- <div class="pay-balance">你需要线下向如下任一账户支付余款： &yen;<span>1000000</span></div>
          <div class="pay-banks">
            <p>工商银行账户：8888 8888 8888 8888</p>
            <p>工商银行账户：8888 8888 8888 8888</p>
          </div>
          <div class="pay-info">汇款时请备注你在本系统注册的手机号码</div> -->
        </div>
      </div>
      <div class="paytype" v-if="order.commodityType==1">
        <div class="pay-title">选择支付方式</div>
        <div class="wxpay">
          <span class="ico-wx"><i class="ico-wxpay"></i></span>
          <span class="lab-title">微信支付</span>
          <span class="pay-radio" @click="payCheck(1)"><i class="ico-radio" :class="payType==1?'checked':''"></i></span>
        </div>
        <div class="alipay">
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
    <div class="pay-result-mask" v-show="payResult">
      <div class="pay-result">
        <p class="p-r-title">请确认微信支付是否已完成</p>
        <p class="p-r-cen" @click="payOver">已完成支付</p>
        <p class="p-r-bot" @click="rePay">支付遇到问题，重新支付</p>
      </div>
    </div>
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
      orderNo:"",
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
      payExplain:"", //支付说明
      isWxPay:false,
      payResult:false, // 支付结果弹窗
      mask:0
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
          this.payExplain = res.data.payExplain||"";
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
          if(versions.wx) {
            this.wxJsPay();
          }else {
            this.payResult = true;
            this.wxWebPay();
          }
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
    wxJsPay: function() {
      //TODO 微信内支付
      let redirect = location.href.split("#")[0]+"?#/pay/pay_wx?orderNo=" + this.orderNo;
      // let redirect = "http://service.genyuanlian.com/gylshoptest/?#/pay/pay_wx?orderNo=" + this.orderNo;
      let param = {
        calBackUrl: redirect
      };
      this.$httpPost(apiUrl.getWeixinCode, param).then((res) => {
        if(res.status.code==0&&res.data) {
          location.href = res.data.redirectUrl;
          // console.log(res.data.redirectUrl);
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    wxWebPay: function() {
      //TODO 网页微信支付
      let param = {
        orderNo: this.orderNo
      };
      this.$httpPost(apiUrl.weixinpayH5, param).then((res) => {
        if(res.status.code==0&&res.data) {
          let data = res.data.weixinPayData;
          location.href = data.mwebUrl;
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
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
    },
    payOver:function() {
      // TODO 支付完成
      this.payResult = false;
      this.$router.push({name:"pay_success", query: {orderNo:this.orderNo, proType:this.productType}});
    },
    rePay:function() {
      // TODO 重新支付
      this.payResult = false;
    }
  },
  mounted() {
    this.isWxPay = versions.wx;
    this.orderNo = this.$route.query.orderNo||"";
    this.mask = this.$route.query.mask||0;
    if(this.orderNo!="") {
      this.getOrderDetail();
    }
    if(this.mask == 1) {
      this.payResult = true;
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
      .pay-bank{
        margin-left:30px;
        padding:30px 30px 30px 0;
        border-top: 1px solid #e8e8e8;/*no*/
        .pay-tip{
          height:50px;
          line-height: 50px;
          font-size:26px;
          color:#ffa936;
          i{
            width:30px;
            height:30px;
            margin-bottom:-4px;
            margin-right:20px;
          }
        }
        .pay-content{
          font-size:26px;
          p{
            height:40px;
            line-height: 40px;
          }
        }
        .pay-balance{
          height:50px;
          line-height: 50px;
          font-size:26px;
          color:#333333;
          font-weight: bold;
        }
        .pay-banks{
          margin-top:10px;
          border:1px solid #e8e8e8;/*no*/
          border-radius: 10px;
          padding:10px 100px;
          p{
            margin-top:5px;
            height:40px;
            line-height:40px;
            text-align: left;
            font-size:26px;
            color:#999;
          }
        }
        .pay-info{
          height:50px;
          line-height: 50px;
          font-size:26px;
          color:#ffa936;
          margin-top:10px;
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
        display: none;
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
  .pay-result-mask{
    width: 100%;
    height: 100%;
    background-color: rgba(0,0,0,.5);
    position: absolute;
    top: 0;
    z-index: 39;
    .pay-result{
      width: 550px;
      height: 264px;
      border-radius: 14px;
      background-color: #fff;
      position: relative;
      top: 35%;
      margin: auto;
      p{
        color: #333;
        text-align: center;
        &.p-r-bot, &.p-r-cen{
          height: 80px;
          font-size: 30px;
          line-height: 80px;
          border-top: 1px solid #efefef; /*no*/
        }
        &.p-r-cen{
          color: #317db9;
        }
        &.p-r-title{
          height: 100px;
          font-size: 34px;
          line-height: 100px;
        }
      }
    }
  }
}
</style>
