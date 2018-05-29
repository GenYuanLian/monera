<template>
  <div class="gyl-navbar fixed" v-show="isShow">
    <div :class="currPath=='home'||currPath=='index'?'active':''" @click="homeClick">
      <p class="icon"><i class="ico-home"></i></p>
      <p class="title">首页</p>
    </div>
    <div :class="currPath=='orders'?'active':''" @click="ordersClick">
      <p class="icon"><i class="ico-order"></i></p>
      <p class="title">订单</p>
    </div>
    <div :class="currPath=='mine'?'active':''" @click="mineClick">
      <p class="icon"><i class="ico-my"></i></p>
      <p class="title">我的</p>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      currPath: ''
    };
  },
  components: {},
  props: {
    isShow: {
      type: Boolean,
      default: false
    },
    isLogin: {
      type: Boolean,
      default: false
    }
  },
  methods: {
    homeClick: function() {
      //TODO 首页
      this.$router.replace("/");
    },
    ordersClick: function() {
      //TODO 订单列表
      if(!this.isLogin) {
        this.$router.push('login');
      } else {
        this.$router.push("orders");
      }
    },
    mineClick: function() {
      //TODO 我的
      this.$router.push("mine");
    }
  },
  mounted() {
    this.currPath = this.$route.name;
  }
};
</script>
<style lang="less">
.ico-home{
  display: inline-block;
  background-image: url('../../assets/images/Icon/ico-home@2x.png');
  background-size:70px 70px;
  width:42px;
  height:42px;
}
.ico-order{
  display: inline-block;
  background-image: url('../../assets/images/Icon/ico-order@2x.png');
  background-size:70px 70px;
  width:38px;
  height:42px;
}
.ico-my{
  display: inline-block;
  background-image: url('../../assets/images/Icon/ico-mine@2x.png');
  background-size:70px 70px;
  width:38px;
  height:42px;
}
.gyl-navbar {
  display: box;
  display: -webkit-box;
  display: -moz-box;
  display: -ms-flexbox;
  display: -webkit-flex;
  display: flex;
  -webkit-flex-direction: row;
  -moz-flex-direction: row;
  -ms-flex-direction: row;
  -o-flex-direction: row;
  flex-direction: row;
  -webkit-flex-wrap: wrap;
  -moz-flex-wrap: wrap;
  -ms-flex-wrap: wrap;
  -o-flex-wrap: wrap;
  flex-wrap: wrap;
  justify-content: space-around;
  align-items: stretch;
  width: 100%;
  padding: 0 30px;
  box-sizing: border-box;
  -webkit-box-sizing: border-box;
  height: 96px;
  line-height: 96px;
  background-color: #FFFFFF;
  box-sizing: border-box;
  border-top: 1px solid #efefef; /*no*/
  // -moz-box-shadow: 0 -10px 10px rgba(0,0,0,0.05); /* 老的 Firefox */
  // box-shadow: 0 -10px 10px rgba(0,0,0,0.05);  
  &.fixed{
    position: fixed;
    bottom:0;
    left: 0;
    right:0;
    z-index: 99;
  }
  div {
    width: 33.3333%;
    height: 98px;
    flex: 1;
    text-align: center;
    &.active{
      .ico-home{
        background-image: url('../../assets/images/Icon/ico-home-active@2x.png');
      }
      .ico-order{
        background-image: url('../../assets/images/Icon/ico-order-active@2x.png');
      }
      .ico-my{
        background-image: url('../../assets/images/Icon/ico-mine-active@2x.png');
      }
      .title{
        color:#317db9;
      }
    }
  }
  .icon{
    height: 42px;
    line-height: 42px;
    margin-top:16px;
    font-size:0;
    i{
      background-size: auto 100%;
    }
  }
  .title{
    color: #999999;
    font-size: 20px;
    line-height: 40px;
  }
}
</style>
