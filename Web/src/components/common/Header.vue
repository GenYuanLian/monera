<template>
  <div class="gyl-header" :class="[fixed?'fixed':'',bgCss]" v-show="show">
    <header class="head" :class="border?'border-b':''">
      <div class="head-l">
        <span v-if="left.className!=''" @click="backTo"><i :class="left.className"></i></span>
        <a href="javascript:;" v-text="left.label"></a>
      </div>
      <div class="head-title" v-text="title"></div>
      <div id="btnRight" class="head-r" @click="rightFn">
        <a href="javascript:;" v-text="right.label"></a>
        <span v-if="right.className!=''"><i :class="right.className"></i></span>
      </div>
    </header>
  </div>
</template>
<script>
export default {
  data() {
    return {};
  },
  props: {
    fixed: {
      type: Boolean,
      default: false
    },
    bgCss: {
      type: String,
      default: "white"
    },
    border: {
      type: Boolean,
      default: false
    },
    show: {
      type: Boolean,
      default: true
    },
    title: {
      type: String,
      default: ""
    },
    left: {
      type: Object,
      default: function() {
        return {
          label: "",
          className: "ico-back"
        };
      }
    },
    right: {
      type: Object,
      default: function() {
        return {
          label: "",
          className: ""
        };
      }
    },
    linkTo: {
      type: String,
      default: ""
    },
    backFun: {
      type: Function,
      default: null
    },
    rightFn:{
      type: Function,
      default: function() {
      }
    }
  },
  methods: {
    backTo: function() {
      // 返回功能
      if(this.$route.query.redirect) {
        this.$router.back();
        // this.$router.push({path:this.$route.query.redirect});
        return;
      }
      if (this.linkTo) {
        this.$router.push(this.linkTo);
      } else if (this.backFun) {
        this.backFun();
      }else{
        this.$router.back();
      }
    }
  },
  mounted() {}
};
</script>
<style lang="less" scoped>
.gyl-header {
  width: 100%;
  &.white{
    background-color: #fff;
  }
  &.fixed {
    position: fixed;
    z-index: 999;
  }
  .head {
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
    height: 88px;
    line-height: 88px;
    font-size: 34px;
    color: #191919;
    box-sizing: border-box;
    &.border-b {
      border-bottom: 1px solid #efefef;/*no*/
    }
    .head-l {
      width: 130px;
      padding-left: 30px;
      font-size: 32px;
      overflow: hidden;
      color: #333333;
      span {
        display: block;
        width: 50px;
        height: 88px;
        line-height: 88px;
        float: left;
      }
      i {
        font-size: 0;
        width:20px;
        height:36px;
        margin-bottom: -8px;
      }
      a {
        display: inline-block;
        text-decoration: none;
        height: 88px;
        line-height: 88px;
      }
    }
    .head-title {
      flex: 1;
      text-align: center;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      font-size: 34px;
    }
    .head-r {
      width: 130px;
      padding-right: 40px;
      font-size: 32px;
      overflow: hidden;
      color: #333333;
      text-align: right;
      span {
        display: block;
        width: 50px;
        height: 88px;
        line-height: 88px;
        float: right;
      }
      i {
        font-size: 0;
        margin-bottom: -8px;
      }
      a {
        display: inline-block;
        text-decoration: none;
        height: 88px;
        line-height: 88px;
        color: #333333;
        font-size: 26px;
      }
    }
  }
}
</style>
