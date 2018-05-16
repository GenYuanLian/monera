<template>
  <div class="gyl-order-detail_card">
    <div v-title>订单详情</div>
    <Header :border="true" :title="headTitle" :left="headLift" ></Header>
    <section class="content">
      <div class="order-status" :class="orderStatusBg">
        <p class="order-detail-status">等待支付</p>
        <p class="order-tip">逾期未支付订单将自动取消</p>
      </div>
      <div class="order-paied">
        <div class="paied-top">
          <img class="or-img" :src="cardOrderMsg.description" alt="">
          <div class="or-pro">
            <p class="pro-detail"><span class="pro-name">{{cardOrderMsg.merchantName}}</span><span class="pro-price">￥{{cardOrderMsg.price}}</span></p>
            <p class="pro-paied"><span class="paied-card">{{cardOrderMsg.commodityName}}</span><span class="paied-num">x{{cardOrderMsg.saleCount}}</span></p>
          </div>
        </div>
        <div class="paied-bot">
          <span class="paied-price">实付：&yen;{{cardOrderMsg.amount}}</span>
        </div>
      </div>
      <div class="order-message">
        <h6>订单信息</h6>
        <div class="order-row">
          <div class="row-key">订单号</div>
          <div class="row-val">{{cardOrderMsg.orderNo}}</div>
        </div>
        <div class="order-row">
          <div class="row-key">支付方式</div>
          <div class="row-val">{{cardOrderMsg.paiedType}}</div>
        </div>
        <div class="order-row no-border-bot">
          <div class="row-key">下单时间</div>
          <div class="row-val">{{new Date(cardOrderMsg.createTime)|dateFormat('YYYY-MM-DD HH:mm')}}</div>
        </div>
      </div>
    </section>
    <footer class="foot">
      <input class="cancle-btn" type="button" value="取消订单" @click="cancleOrder">
      <input class="buy-btn" type="button" :value="payTimeOut" @click="payOrder">
    </footer>
  </div>
</template>
<script>
import { mapActions, mapGetters } from "vuex";
import { dateFormat, Flow, FlowState, FlowLine, md5 } from "vux";
import { showMsg, valid } from '@/utils/common.js';
import apiUrl from '@/config/apiUrl.js';
import Header from '@/components/common/Header';
import { clearInterval, setInterval } from 'timers';
export default {
  data() {
    return {
      headTitle: "订单详情",
      headLift: {
        label: "",
        className: "ico-back"
      },
      orderNo:"",
      timer:null,
      payTimeOut:'去支付',
      cardOrderMsg:{}
    };
  },
  filters: {
    dateFormat
  },
  computed:{
    orderStatusBg:function() {
      let className = '';
      if(this.cardOrderMsg.status==0) {
        className = 'order-wait-pay';
      } else {
        className = 'order-wait-complete';
      }
      return className;
    }
  },
  components: {
    Header, Flow, FlowState, FlowLine, dateFormat
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
          this.cardOrderMsg = data.cardOrder;
          this.cardOrderMsg.status = 0;
          this.orderStatusHandle();
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    orderStatusHandle:function() {
      // TODO 处理不同的订单状态...
      if(this.cardOrderMsg.status==0) {
        // 待支付
        this.timeOut();
      }
    },
    timeOut:function() {
      //TODO 倒计时计算
      var _this = this;
      var time = this.cardOrderMsg.surplusPayTime;
      var min, sec;
      if(this.timer) {
        clearInterval(this.timer);
      }
      this.timer = setInterval(function() {
        if(time<0) {
          clearInterval(this.timer);
          _this.payTimeOut=`去支付(还剩0秒)`;
          return;
        }
        min = Math.floor(time/60);
        sec = time%60;
        if(min==0) {
          _this.payTimeOut=`去支付(还剩${sec}秒)`;
        }else{
          _this.payTimeOut=`去支付(还剩${min}分${sec}秒)`;
        }
        time--;
      }, 1000);
    },
    cancleOrder:function() {
      // TODO 取消订单
      let param = {
        orderNo: 'puc201805111933169733'
      };
      this.$httpPost(apiUrl.getPuCardOrderDetail, param).then((res) => {
        if(res.status.code==0&&res.data) {
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    payOrder:function() {
      // TODO 支付订单
      let param = {
        orderNo: this.orderNo
      };
      this.$httpPost(apiUrl.getPuCardOrderDetail, param).then((res) => {
        if(res.status.code==0&&res.data) {
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
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

.gyl-order-detail_card{
  height: 100%;
  overflow: hidden;
  .content{
    box-sizing:border-box;
    width:100%;
    height:calc(~"100% - 186px");
    overflow-x: hidden;
    overflow-y: auto;
    -webkit-overflow-scrolling: touch;
    background-color: #efefef;
    .order-status{
      padding: 50px 40px;
      color: #fff;
      &.order-wait-pay{
        background: url('../../assets/images/Order/order-top-bg@2x.png') center no-repeat;
        background-size:100% 100%;
      }
      &.order-wait-complete{
        background: url('../../assets/images/Order/order-top-bg@2x.png') center no-repeat;
        background-size:100% 100%;
      }
      .order-detail-status{
        height: 56px;
        font-size: 34px;
        line-height: 56px;
      }
      .order-tip{
        height: 44px;
        line-height: 44px;
        font-size: 24px;
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
            font-size: 28px;
            color: #333;
            .pro-price{
              font-size: 24px;
              float: right;
            }
          }
          .pro-paied{
            height: 45px;
            line-height: 45px;
            font-size: 22px;
            color: #999999;
            .paied-num{
              font-size: 20px;
              float: right;
            }
          }
        }
      }
      .paied-bot{
        font-size: 24px;
        height: 90px;
        line-height: 90px;
        .paied-price{
          float: right;
          font-size: 30px;
          color: #ffa936;
          margin-right: 30px;
        }
      }
    }
    .order-message{
      margin-bottom: 20px;
      padding-left: 30px;
      background-color: #fff;
      h6{
        height: 66px;
        line-height: 84px;
        font-size: 28px;
        color: #333;
      }
      .order-row{
        font-size: 24px;
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
        width: calc(~"100% - 130px");
        padding: 15px 0;
        line-height: 44px;
        color: #555555;
        word-break: break-all;
      }
    }
  }
  .foot{
    position: absolute;
    bottom: 0;
    width: 750px;
    height: 98px;
    background: #fff;
    input{
      display: block;
      font-size: 26px;
      line-height: 98px;
      color: #fff;
      text-align: center;
      float: left;
      border-radius: 0;
    }
    .cancle-btn{
      width: 250px;
      background-color: #ffa936;
    }
    .buy-btn{
      width: calc(~"100% - 250px");
      font-size: 26px;
      background-color: #317db9;
    }
  }
}
</style>
