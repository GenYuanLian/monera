<template>
  <div class="gyl-pay-succ">
    <div v-title>支付成功</div>
    <Header :border="true" :title="headTitle" ></Header>
    <section class="content">
      <div class="succ-top">
        <img src="../../assets/images/Pay/pay-succ@2x.png" alt="">
        <p>恭喜支付成功！</p>
      </div>
      <div class="pay-opt">
        <input class="btn-backhome" type="button" @click="backHome" value="返回首页">
        <input class="btn-order" type="button" @click="goOrderDetail" value="查看订单">
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
      headTitle: "支付成功",
      headLift: {
        label: "",
        className: "ico-back"
      }
    };
  },
  components: {
    Header
  },
  methods: {
    backHome: function(flag) {
      //TODO 返回首页
      this.$route.push("home");
    },
    goOrderDetail: function(flag) {
      //TODO 查看订单列表
      this.$route.push("order_detail");
    }
  },
  mounted() {}
};
</script>
<style lang="less">
html,body{
  height: 100%;
  overflow: hidden;
}

.gyl-pay-succ{
  height: 100%;
  overflow: hidden;
  .content{
    box-sizing:border-box;
    width:100%;
    height:100%;
    padding-bottom:342px;
    overflow-x: hidden;
    overflow-y: auto;
    background-color: #f3f4f6;
    .succ-top{
      width:100%;
      height:305px;
      background-color: #fff;
      padding-top:80px;
      img{
        display:block;
        width:140px;
        height:140px;
        margin:0 auto;
      }
      p{
        height:45px;
        line-height:45px;
        font-size:48px;
        color:#282828;
        margin-top:40px;
        text-align: center;
      }
    }
    .pay-opt{
      margin-top:60px;
      padding:0 75px;
      input{
        display: block;
        margin-top:30px;
        width:100%;
        height:80px;
        line-height: 80px;
        text-align: center;
        font-size:34px;
        border-radius:10px;
      }
      .btn-backhome{
        color:#fff;
        background-color: #317db9;
      }
      .btn-order{
        color:#317db9;
        background-color: #f3f4f6;
        border:2px solid #317db9;
      }
    }
  }
}
</style>
