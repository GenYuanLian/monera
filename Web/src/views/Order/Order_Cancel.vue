<template>
  <div class="gyl-order-cancel">
    <div v-title>订单取消</div>
    <Header title="订单取消" :border="true"></Header>
    <section class="content">
      <div class="cancel order-cancel" >
        <p class="cancel-status">取消订单</p>
        <p class="cancel-tip">取消订单，请告诉我们原因，以帮助改进产品和服务</p>
      </div>
      <div class="cancel-reason">
        <div class="reason-row" v-for="(item,i) in reasonList" v-bind:key="i">
          <p @click="reasonClick(i)"><span class="title">{{item.title}}</span><span class="row-r"><i class="ico-radio" :class="item.isCheck?'checked':''"></i></span></p>
          <p v-if="item.id==4" v-show="item.isCheck"><textarea class="area-reason" name="" id="" cols="42" rows="3" v-model="reason" placeholder="请输入其它内容"></textarea></p>
        </div>
      </div>
      <div class="btn-confirm">
        <input type="button" value="提交" @click="cancelOrder">
      </div>
    </section>
  </div>
</template>
<script>
import { mapActions, mapGetters } from "vuex";
import Header from "@/components/common/Header";
import { Scroller } from 'vux';
import { showMsg, loading, readFile, valid } from "@/utils/common.js";
import apiUrl from "@/config/apiUrl.js";
export default {
  data() {
    return {
      reasonList: [{title:"选错产品了，重新下单", id:1, isCheck:false},
                {title:"信息填写有误，重新填", id:2, isCheck:false},
                {title:"我不想买了", id:3, isCheck:false},
                {title:"其他", id:4, isCheck:false}],
      orderNo:"",
      reason:"",
      showReason:false
    };
  },
  components: {
    Header
  },
  methods: {
    reasonClick:function(idx) {
      this.reasonList.forEach((item, i) => {
        if(i===idx) {
          item.isCheck=true;
          this.reason = "";
        }else{
          item.isCheck=false;
        }
      });
    },
    cancelOrder: function() {
      //TODO 取消订单
      this.reasonList.forEach((item, i) => {
        if(item.isCheck) {
          this.reason = item.title + "," + this.reason;
        }
      });
      let param = {
        orderNo:this.orderNo,
        reason:this.reason
      };
      this.$httpPost(apiUrl.cancelOrder, param).then((res) => {
        if(res.status.code==0&&res.data) {
          showMsg(res.status.message, () => {
            this.$router.push("orders");
          });
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    }
  },
  mounted () {
    this.orderNo = this.$route.query.orderNo||"";
  }
};
</script>
<style lang="less">
.gyl-order-cancel{
  height:100%;
  width:100%;
  overflow: hidden;
  background: #F3F4F6;;
  .content{
    box-sizing:border-box;
    height:auto;
    width:100%;
    height:100%;
    overflow-y:auto;
    -webkit-overflow-scrolling: touch;
    padding-bottom:90px;
    .cancel{
      padding: 50px 40px;
      color: #fff;
      &.order-cancel{
        background: url('../../assets/images/Bg/order-cancel-bg@2x.png') center no-repeat;
        background-size:100% 100%;
      }
      .cancel-status{
        height: 56px;
        font-size: 34px;
        line-height: 56px;
      }
      .cancel-tip{
        width:60%;
        height: 44px;
        line-height: 30px;
        font-size: 24px;
        word-wrap: break-word;
      }
    }
    .cancel-reason{
      height:auto;
      width:100%;
      background-color: #fff;
      .reason-row{
        line-height: 60px;
        padding: 30px;
        border-bottom:1px solid #f3f4f6;
        &:last-child{
          border-bottom:none;
        }
        .title{
          height:60px;
          line-height: 60px;
          font-size:30px;
          color:#555;
        }
        .row-r{
          display: block;
          width:60px;
          height:60px;
          float:right;
          i{
            width:40px;
            height:40px;
            margin-bottom:-10px;
          }
        }
        .area-reason{
          margin-top:30px;
          padding:20px 30px;
          background-color: #eee;
          color:#999;
          font-size:26px;
          border-radius:10px;
        }
      }
    }
    .btn-confirm{
      height:80px;
      line-height: 80px;
      padding:0 70px;
      margin-top:60px;
      input{
        display: inline-block;
        width:100%;
        height:80px;
        line-height: 80px;
        margin:0 auto;
        font-size:32px;
        background-color:#317db9;
        color:#fff;
        border-radius:10px;
      }
    }
  }
}
</style>
