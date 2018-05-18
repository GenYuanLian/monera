<template>
  <div class="gyl-order-detail_product">
    <div v-title>订单详情</div>
    <Header :border="true" :title="headTitle" :left="headLeft" ></Header>
    <section class="content" :class="orderMsg.status==1 || orderMsg.status==2 || orderMsg.status==8 ? 'no-footer' : ''">
      <div class="order-status" :class="orderStatusBg">
        <p class="order-detail-status">{{orderStatusHandle(orderMsg.status)}}</p>
        <p class="order-tip">{{orderMsg.status==0 ? '逾期未支付订单将自动取消' : (orderMsg.status==2 ? '30分钟内未完成支付，订单已自动取消' : '')}}</p>
      </div>
      <div class="order-schedule">
        <!--订单状态:0-未支付,1-未支付取消,2-支付过期，3-已支付，4-发货前退单，5-商家确认发货，6-买家确认收货，7-收货后退单,8-订单已完成-->
        <flow>
          <flow-state :title="'等待支付'" :is-done="!(orderMsg.status==0 || orderMsg.status==1 || orderMsg.status==2)"></flow-state>
          <flow-line :is-done="!(orderMsg.status==0 || orderMsg.status==1 || orderMsg.status==2)"></flow-line>
          <flow-state :title="'等待收货'" :is-done="orderMsg.status==6 || orderMsg.status==7 || orderMsg.status==8"></flow-state>
          <flow-line :is-done="orderMsg.status==6 || orderMsg.status==7 || orderMsg.status==8"></flow-line>
          <flow-state :title="'已完成'" :is-done="orderMsg.status==8 || orderMsg.status==6"></flow-state>
        </flow>
      </div>
      <div class="order-paied">
        <div class="paied-top">
          <img class="or-img" :src="orderMsg.description" alt="">
          <div class="or-pro">
            <p class="pro-detail"><span class="pro-name">{{orderMsg.merchantName}}</span><span class="pro-price fr">&yen;{{orderMsg.price}}</span></p>
            <p class="pro-paied"><span class="paied-card">{{orderMsg.commodityName}}</span><span class="paied-num fr">x{{orderMsg.saleCount}}</span></p>
          </div>
        </div>
        <div class="paied-bot">
          <span>送货费  0</span>
          <span class="paied-price">实付：{{orderMsg.amount}}BSTK</span>
        </div>
      </div>
      <div class="order-address">
        <h6>配送信息</h6>
        <div class="order-row" v-if="false">
          <div class="row-key">发送时间</div>
          <div class="row-val">{{new Date(addressMsg.arriveTime)|dateFormat('YYYY-MM-DD HH:mm')}}</div>
        </div>
        <div class="order-row">
          <div class="row-key">收货地址</div>
          <div class="row-val">
            {{addressMsg.receiver}}<br>  
            {{addressMsg.mobile}}<br> 
            {{addressMsg.areaName&&addressMsg.areaName.replace(/-/g,'') + addressMsg.address}}<br>
          </div>
        </div>
        <div class="order-row no-border-bot">
          <div class="row-key">配送方式</div>
          <div class="row-val">商家配送</div>
        </div>
      </div>
      <div class="order-message">
        <h6>订单信息</h6>
        <div class="order-row" v-if="false">
          <div class="row-key">发送时间</div>
          <div class="row-val">{{new Date(orderMsg.arriveTime)|dateFormat('YYYY-MM-DD HH:mm')}}</div>
        </div>
        <div class="order-row">
          <div class="row-key">订单号</div>
          <div class="row-val">{{orderMsg.orderNo}}</div>
        </div>
        <div class="order-row">
          <div class="row-key">支付方式</div>
          <div class="row-val">{{payType(orderMsg.payType)}}</div>
        </div>
        <div class="order-row no-border-bot">
          <div class="row-key">下单时间</div>
          <div class="row-val">{{new Date(orderMsg.createTime)|dateFormat('YYYY-MM-DD HH:mm')}}</div>
        </div>
      </div>
    </section>
    <footer class="foot" v-if="!(orderMsg.status==1 || orderMsg.status==2 || orderMsg.status==8)">
      <input v-if="orderMsg.status==0" class="cancle-btn" type="button" value="取消订单" @click="cancleOrder">
      <input v-if="orderMsg.status==0" class="buy-btn" type="button" :value="payTimeOut" @click="payOrder">
      <input v-if="orderMsg.status==6" class="cancle-btn" type="button" value="去评价" @click="judgeOrder">
      <input v-if="orderMsg.status==3 || orderMsg.status==6" class="buy-btn" type="button" value="再来一单" @click="buyAgain">
      <input v-if="orderMsg.status==3" class="cancle-btn" type="button" value="申请退款" @click="refundOrder">
    </footer>
  </div>
</template>
<script>
import { mapActions, mapGetters } from "vuex";
import { dateFormat, Flow, FlowState, FlowLine, md5 } from "vux";
import { showMsg, valid } from '@/utils/common.js';
import apiUrl from '@/config/apiUrl.js';
import Header from '@/components/common/Header';
export default {
  data() {
    return {
      headTitle: "订单详情",
      headLeft: {
        label: "",
        className: "ico-back"
      },
      orderNo:"",
      timerProduct:null,
      payTimeOut:'去支付',
      addressMsg:{},
      orderMsg:{} // 订单信息
    };
  },
  filters: {
    dateFormat
  },
  computed:{
    orderStatusBg:function() {
      let className = '';
      if(this.orderMsg.status==0) {
        className = 'order-wait-pay';
      } else if(this.orderMsg.status==1 || this.orderMsg.status==2) {
        className = 'order-wait-cancle';
      } else {
        className = 'order-wait-complete';
      }
      return className;
    }
  },
  components: {
    Header, Flow, FlowState, FlowLine
  },
  methods: {
    getOrderDetail: function(flag) {
      //TODO 查询订单详情
      let param = {
        orderNo: this.orderNo
      };
      this.$httpPost(apiUrl.getPuCardOrderDetail, param).then((res) => {
        if(res.status.code==0&&res.data) {
          let data = res.data;
          this.orderMsg = data.cardOrder;
          this.addressMsg = data.delivery;
          this.orderStatusHandle();
          if(this.orderMsg.status == 0) {
            // 待支付
            this.timeOut();
          }
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    orderStatusHandle:function(status) {
      // TODO 订单状态处理
      // 订单状态:0-未支付,1-未支付取消,2-支付过期，3-已支付，4-发货前退单，5-商家确认发货，6-买家确认收货，7-收货后退单,8-订单已完成，9-用户已删除
      let _status = Number(status);
      let text = '';
      switch (_status) {
      case 0:
        text = '等待支付';
        break;
      case 1:
      case 2:
        text = '订单已取消';
        break;
      case 3:
        text = '已支付';
        break;
      case 4:
      case 7:
        text = '已申请退单';
        break;
      case 6:
        text = '待评价';
        break;
      case 8:
      default:
        text = '已完成';
        break;
      }
      return text;
    },
    payType:function(type) {
      // TODO 支付方式
      let text = '';
      if(type==1) {
        text = '微信';
      } else if(type==2) {
        text = '支付宝';
      } else if(type==3) {
        text = '提货卡';
      } else {
        text = '提货卡';
      }
      return text;
    },
    timeOut:function() {
      //TODO 倒计时计算
      var _this = this;
      var time = this.orderMsg.surplusPayTime;
      var min, sec;
      if(this.timerProduct) {
        clearInterval(this.timerProduct);
      }
      this.timerProduct = setInterval(function() {
        if(time<0) {
          clearInterval(this.timerProduct);
          _this.payTimeOut=`去支付(还剩0秒)`;
          return;
        }
        min = Math.floor(time/60);
        sec = time%60;
        if(min==0) {
          _this.payTimeOut=`去支付(还剩${sec}秒)`;
        }else {
          _this.payTimeOut=`去支付(还剩${min}分${sec}秒)`;
        }
        time -= 1;
      }, 1000);
    },
    payOrder:function() {
      // TODO 去支付
      if(this.orderMsg.surplusPayTime>0) {
        this.$router.push({name:'pay_order', query:{orderNo:this.orderNo}});
      }
    },
    cancleOrder: function(orderNo) {
      //TODO 取消订单
      this.$router.push({name:"order_cancel", query: {orderNo:this.orderNo}});
    },
    buyAgain: function() {
      //TODO 再来一单
      this.$router.push({name:"order_place", query: {proId:this.orderMsg.commodityId, proType:this.orderMsg.commodityType}});
    },
    refundOrder:function() {
      // TODO 申请退款
    },
    judgeOrder:function() {
      // TODO 去评价
    }
  },
  mounted() {
    this.orderNo = this.$route.query.orderNo||"";
    if(this.orderNo!="") {
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

.gyl-order-detail_product{
  height: 100%;
  overflow: hidden;
  .content{
    box-sizing:border-box;
    width:100%;
    height:calc(~"100% - 186px");
    overflow-x: hidden;
    overflow-y: auto;
    -webkit-overflow-scrolling: touch;
    background-color: #F3F4F6;
    &.no-footer{
    height:calc(~"100% - 88px");
    }
    .order-status{
      padding: 50px 40px;
      color: #fff;
      &.order-wait-pay{
        background: url('../../assets/images/Bg/order-top-bg@2x.png') center no-repeat;
        background-size:100% 100%;
      }
      &.order-wait-cancle{
        background: url('../../assets/images/Bg/order-cancel-bg@2x.png') center no-repeat;
        background-size:100% 100%;
      }
      &.order-wait-complete{
        background: url('../../assets/images/Bg/order-complete-bg@2x.png') center no-repeat;
        background-size:100% 100%;
      }
      .order-detail-status{
        height: 56px;
        font-size: 36px;
        line-height: 56px;
      }
      .order-tip{
        height: 44px;
        line-height: 44px;
        font-size: 26px;
      }
    }
    .order-schedule{
      height: 155px;
      padding-top: 25px;
      background-color: #fff;
      .weui-wepay-flow__title-bottom{
        top: -45px;
      }
      .weui-wepay-flow__title-bottom{
        color: #999999;
      }
      .weui-wepay-flow__li_done .weui-wepay-flow__title-bottom{
        color: #333;
      }
      .weui-wepay-flow__state{
        border: 4px solid #317db9;
        background-color:#f3f4f6;
      }
      .weui-wepay-flow__li_done .weui-wepay-flow__state{
        border: 5px solid #317db9;
        background-color:#317db9;
      }
      .weui-wepay-flow__process{
        background-color:#317db9;
      }
      .weui-wepay-flow__line{
        background-color: #bbe0fd;
      }
    }
    .order-paied{
      margin-top: 20px;
      margin-bottom: 20px;
      background-color: #fff;
      padding-left:25px;
      .paied-top{
        padding: 20px 30px 20px 5px;
        border-bottom: 1px solid #efefef; /*no*/
        overflow: hidden;
        .or-img{
          display: block;
          float: left;
          width: 120px;
          height: 120px;
          border: 1px solid #efefef; /*no*/
          border-radius: 20px;
          margin-right: 22px;
        }
        .or-pro{
          float: right;
          width: calc(~"100% - 150px");
          .pro-detail{
            height: 50px;
            line-height: 50px;
            font-size: 30px;
            color: #333;
          }
          .pro-paied{
            height: 45px;
            line-height: 45px;
            font-size: 26px;
            color: #999999;
          }
        }
      }
      .paied-bot{
        font-size: 30px;
        color: #999;
        height: 90px;
        line-height: 90px;
        .paied-price{
          float: right;
          font-size: 34px;
          color: #ffa936;
          margin-right: 30px;
        }
      }
    }
    .order-message, .order-address{
      margin-bottom: 20px;
      padding-left: 30px;
      background-color: #fff;
      h6{
        height: 66px;
        line-height: 84px;
        font-size: 30px;
        color: #333;
      }
      .order-row{
        font-size: 26px;
        border-bottom: 1px solid #efefef; /*no*/
        overflow: hidden;
        &.no-border-bot{
          border: 0;
        }
      }
      .row-key{
        float: left;
        width: 128px;
        height: 72px;
        line-height: 72px;
        color: #999;
      }
      .row-val{
        float: left;
        width: calc(~"100% - 160px");
        padding: 15px 30px 15px 0;
        line-height: 44px;
        color: #555555;
        word-break: break-all;
      }
    }
  }
  .foot{
    position: absolute;
    bottom: 0;
    width: 100%;
    height: 98px;
    background: #fff;
    input{
      display: block;
      font-size: 30px;
      line-height: 98px;
      color: #fff;
      text-align: center;
      float: left;
      border-radius: 0;
    }
    .cancle-btn{
      width: 250px;
      font-size: 30px;
      background-color: #ffa936;
    }
    .buy-btn{
      width: calc(~"100% - 250px");
      font-size: 30px;
      background-color: #317db9;
    }
  }
}
</style>
