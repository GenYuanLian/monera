<template>
  <div class="gyl-orders">
    <div v-title>订单</div>
    <Header :border="true" :title="headTitle" :left="headLeft" ></Header>
    <section class="content" v-show="ordersList&&ordersList.length>0">
      <Scroller ref="scroller" height="-80px" lock-x :scrollbar-y=false v-model="status" :pullup-config="pullupConfig" @on-pullup-loading="onPullup" :use-pullup="usePullup">
        <div class="order-row-box">
          <div class="order-row" v-for="(order,index) in ordersList" :key="index">
            <div class="or-top">
              <span class="or-time">{{new Date(order.createTime)|dateFormat('YYYY-MM-DD HH:mm')}}</span>
              <span class="or-status">{{orderStatusHandle(order.status)}}</span>
            </div>
            <div class="or-center" @click="jumpOrderDetail(order.commodityType, order.orderNo)">
              <img class="or-img" :src="order.description" alt="">
              <div class="or-pro">
                <p class="pro-detail"><span class="pro-name">{{order.merchantName}}</span><span class="pro-price fr">&yen;{{order.amount}}</span></p>
                <p class="pro-paied"><span class="paied-card">{{order.commodityName}}</span><span class="paied-num fr">x{{order.saleCount}}</span></p>
              </div>
            </div>
            <div class="or-btn" v-if="order.commodityType==1">
              <span v-if="order.status==0" class="or-tip" @timeOut="timeOut(order.surplusPayTime, order.timer)">{{order.timer}}</span>
              <input v-if="order.status==0" type="button" value="去支付" @click="goPay(order.orderNo)">
              <input v-if="order.status==0" type="button" value="取消订单" @click="cancelOrder(order.orderNo)">
              <input v-if="order.status==3||order.status==6 || order.status==8"  type="button" value="再来一单" @click="buyAgainClick(order.commodityId, order.commodityType, order.merchantId, order.merchType)">
              <input v-if="order.status==3||order.status==6" class="evalue-btn"  type="button" value="去评价" @click="evaluateClick(order.orderNo)">
              <input v-if="order.status==1||order.status==2" type="button" value="重新下单" @click="reorderClick(order.merchantId,order.merchType)">
              <input v-if="order.status==1||order.status==2" class="evalue-btn" type="button" value="删除订单" @click="deleteOrder(order.orderNo)">
              <!-- <input v-if="order.commodityType!=1&&order.status==3" type="button" value="申请退单"> -->
            </div>
            <div class="or-btn" v-else>
              <span v-if="order.status==0" class="or-tip" @timeOut="timeOut(order.surplusPayTime, order.timer)">{{order.timer}}</span>
              <input v-if="order.status==0" type="button" value="去支付" @click="goPay(order.orderNo)">
              <input v-if="order.status==0" type="button" value="取消订单" @click="cancelOrder(order.orderNo)">
              <input v-if="order.status==3" type="button" value="提醒发货">
              <input v-if="order.status==6 || order.status==8"  type="button" value="再来一单" @click="buyAgainClick(order.commodityId, order.commodityType, order.merchantId, order.merchType)">
              <input v-if="order.status==6" class="evalue-btn"  type="button" value="去评价" @click="evaluateClick(order.orderNo)">
              <input v-if="order.status==2" type="button" value="重新下单" @click="reorderClick(order.merchantId,order.merchType)">
              <input v-if="order.status==1||order.status==2" class="evalue-btn" type="button" value="删除订单" @click="deleteOrder(order.orderNo)">
              <!-- <input v-if="order.commodityType!=1&&order.status==3" type="button" value="申请退单"> -->
              <input v-if="order.status==5" class="btn-primary"  type="button" value="确认收货" @click="confirmReceipt(order.orderNo)">
            </div>
          </div>
        </div>
      </Scroller>
    </section>
    <section class="content" v-show="ordersList.length==0">
      <div class="no-card">还没有订单哦，快去购买吧~</div>
    </section>
    <NavBar :isShow="true"></NavBar>
  </div>
</template>
<script>
import { mapActions, mapGetters } from "vuex";
import { dateFormat, Scroller } from 'vux';
import { showMsg, valid } from '@/utils/common.js';
import apiUrl from '@/config/apiUrl.js';
import Header from '@/components/common/Header';
import NavBar from '@/components/common/NavBar';
export default {
  data() {
    return {
      headTitle: "订单",
      headLeft: {
        label: "",
        className: "ico-back"
      },
      pullupConfig: {
        pullUpHeight: 60,
        height: 40,
        autoRefresh: false,
        content: '上拉加载更多',
        downContent: '松开进行加载',
        upContent: '上拉加载更多',
        loadingContent: '加载中...',
        clsPrefix: 'xs-plugin-pullup-'
      },
      usePullup: true,
      status: {
        pullupStatus: 'disabled'
      },
      isShow: false,
      pageIndex:0,
      pageSize:10,
      hasNext: false,
      ordersList:[] // 订单列表
    };
  },
  filters: {
    dateFormat
  },
  components: {
    Header, NavBar, Scroller
  },
  computed:{
    ...mapGetters(["getLoginUser", "getUserInfo"])
  },
  methods: {
    clearClick: function(flag) {
      //TODO 清空
    },
    onPullup() {
      if(this.hasNext) {
        this.pageIndex++;
        this.getOrderList();
      }else{
        this.$refs.scroller.disablePullup();
      }
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
      case 5:
        text = '已发货';
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
    getOrderList:function() {
      // TODO获取订单列表信息
      let param = {
        pageIndex: this.pageIndex,
        pageSize: this.pageSize
      };
      this.$httpPost(apiUrl.getPuCardOrders, param).then((res) => {
        if(res.status.code==0&&res.data) {
          let list = res.data.orders||[];
          this.hasNext = res.data.hasNext;
          this.$set(this.ordersList, 'timer', '');
          if(this.hasNext) {
            this.ordersList = this.ordersList.concat(list);
            this.$nextTick(() => {
              this.$refs.scroller.donePullup();
            });
          }else{
            this.ordersList = this.ordersList.concat(list);
            this.$nextTick(() => {
              this.$refs.scroller.disablePullup();
            });
          }
        } else {
          showMsg(res.status.message);
          this.$nextTick(() => {
            this.$refs.scroller.disablePullup();
          });
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    timeOut:function(time, timeText) {
      //TODO 倒计时计算
      var _this = this;
      var min, sec;
      if(this.timer) {
        clearInterval(this.timer);
      }
      this.timer = setInterval(function() {
        if(time<0) {
          clearInterval(this.timer);
          timeText=`还剩0秒`;
          return;
        }
        min = Math.floor(time/60);
        sec = time%60;
        if(min==0) {
          timeText=`还剩${sec}秒`;
        }else{
          timeText=`还剩${min}分${sec}秒`;
        }
        time--;
      }, 1000);
    },
    jumpOrderDetail:function(commodityType, orderNo) {
      // TODO 跳转至订单详情页面
      if(commodityType == 1) {
        this.$router.push({name:'order_detail_card', query:{orderNo:orderNo}});
      } else {
        this.$router.push({name:'order_detail_product', query:{orderNo:orderNo}});
      }
    },
    goPay: function(orderNo) {
      //TODO 去支付
      this.$router.push({name:'pay_order', query:{orderNo:orderNo}});
    },
    cancelOrder: function(orderNo) {
      //TODO 取消订单
      this.$router.push({name:"order_cancel", query: {orderNo:orderNo}});
    },
    deleteOrder: function(orderNo) {
      //TODO 删除订单
      let param = {
        orderNo: orderNo
      };
      this.$httpPost(apiUrl.deleteOrder, param).then((res) => {
        if(res.status.code==0&&res.data) {
          showMsg(res.status.message, () => {
            this.ordersList = [];
            this.getOrderList();
          });
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    confirmReceipt:function(orderNo) {
      //TODO 确认收货
      let param = {
        orderNo: orderNo
      };
      this.$httpPost(apiUrl.buyerConfirmOrder, param).then((res) => {
        if(res.status.code==0&&res.data) {
          showMsg(res.status.message, () => {
            this.ordersList = [];
            this.getOrderList();
          });
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    buyAgainClick: function(proId, proType, id, type) {
      //TODO 再来一单
      if(proType==1) {
        this.$router.push({name:"merchant_info", query: {id:id, type:type}});
      } else {
        this.$router.push({name:"order_place", query: {proId:proId, proType:proType}});
      }
    },
    reorderClick: function(id, type) {
      //TODO 重新下单
      this.$router.push({name:"merchant_info", query: {id:id, type:type}});
    },
    evaluateClick: function(orderNo) {
      //TODO 去评价
      this.$router.push({name:'order_evaluate', query:{orderNo:orderNo}});
    }
  },
  mounted() {
    this.userId = this.getLoginUser?this.getLoginUser.id:"";
    if(this.userId) {
      this.$nextTick(() => {
        this.getOrderList();
      });
    }
  }
};
</script>
<style lang="less">
html,body{
  height: 100%;
  overflow: hidden;
}

.gyl-orders{
  height: 100%;
  width:100%;
  overflow: hidden;
  .content{
    box-sizing:border-box;
    width:100%;
    height: 100%;
    overflow-x: hidden;
    overflow-y: auto;
    -webkit-overflow-scrolling: touch;
    background-color: #f3f4f6;
    padding-bottom:180px;
    .no-card{
      height: 80px;
      line-height: 80px;
      font-size: 20px;
      color: #cecece;
      text-align: center;
      letter-spacing: 2px;
    }
    .order-row-box{
      width: 100%;
      height: 100%;
      .order-row{
        padding: 0 0 0 30px;
        background-color: #fff;
        margin-bottom: 20px;
        &:first-of-type{
          margin-top: 20px;
        }
        .or-top{
          height: 69px;
          line-height: 70px;
          padding-right:30px;
          border-bottom: 1px solid #efefef; /*no*/
          .or-time{
            font-size: 24px;
            color: #999;
          }
          .or-status{
            font-size: 30px;
            color: #317db9;
            float: right;
          }
        }
        .or-center{
          padding: 20px 0 20px 0;
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
            padding-right:30px;
            width: calc(~"100% - 180px");
            .pro-detail{
              height: 48px;
              line-height: 48px;
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
        .or-btn{
          height: 100px;
          line-height: 100px;
          .or-tip{
            color: #999;
            font-size: 24px;
          }
          input{
            width: 160px;
            height: 60px;
            line-height: 60px;
            font-size: 30px;
            color: #fff;
            margin: 20px 20px 0 0;
            border-radius: 10px;
            color: #ffa936;
            border:1px solid #ffa936;/*no*/
            float: right;
          }
          .evalue-btn{
            color: #317db9;
            border:1px solid #317db9;/*no*/
          }

          .btn-primary{
            color:#fff;
            background-color: #317db9;
            border:none;
          }
        }
      }
    }
    .xs-plugin-pullup-container{
      line-height: 60px;
      font-size: 28px;
      color: #6F7281;
      background: #F3F4F6;;
    }
  }
}
</style>
