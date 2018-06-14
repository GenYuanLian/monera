<template>
  <div class="component_popup" v-show="showWin">
    <div class="component_popup_box">
      <div v-if="popObj.popTitle" class="pop_title">{{popObj.popTitle}}</div>
      <div class="pop_text" v-html="popObj.popText"></div>
      <div class="trueFn" v-if="popObj.popIndex == 1">
        <div @click="confirmFns">{{ popObj.popTrue?popObj.popTrue:'确定' }}</div>
      </div>
      <div  v-if="popObj.popIndex == 2" class="trueFnTwo">
        <div @click="confirmFns">{{ popObj.popTrue?popObj.popTrue:'确定' }}</div>
        <div @click="cancelFns">{{ popObj.popFalse?popObj.popFalse:'取消' }}</div>
      </div>

    </div>
  </div>
</template>
<script type="text/ECMAScript-6">
  import Vue from 'vue';
  export default{
    data: function () {
      return {
        showWin:false
      };
    },
    props:{
      popObj:{
        type:Object,
        default:function () {
          return {
            popTitle:{                  //标题
              type:String,
              default:""
            },
            popText:{                   //文字
              type:String,
              default:""
            },
            popIndex:{                  //1确定  2取消+确定
              type:Number,
              default:1
            },
            popTrue:{
              type:String,
              default:"确定"
            },
            popFalse:{
              type:String,
              default:"取消"
            },
            popSure:{                         //点击确定后的业务逻辑
              type:Function,
              default:function () {
                return function () {
                };
              }
            },
            popFnFalse:{                         //点击取消后的业务逻辑
              type:Function,
              default:function () {
                return function () {
                };
              }
            }
          };
        }
      },
      isShow: {
        type:Boolean,
        default:false
      }
    },
    computed:{
      popText:function() {
        return this.popObj.popText;
      }
    },
    watch:{
      popText(newValue, oldValue) {},
      isShow (newValue, oldValue) {
        this.showWin = newValue;
      }
    },
    methods:{
      closeWin:function() {
        //TODO 关闭弹窗
        this.showWin = false;
      },
      openWin:function() {
        //TODO 打开弹窗
        this.showWin = true;
      },
      cancelFns:function () {
        //关闭
        if(this.popObj.popFnFalse) {
          this.popObj.popFnFalse();
        }
        this.closeWin();
      },
      confirmFns:function () {
        if(this.popObj.popSure) {
          this.popObj.popSure();
        }
        this.closeWin();
      }
    },
    mounted: function() {
      this.showWin = this.isShow;
    }
  };
</script>
<style lang="less" scoped>
  .component_popup{
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, .6);
    position: fixed;
    top: 0;
    left: 0;
    z-index: 999;
    .component_popup_box{
      width: 550px;
      min-height: 360px;
      max-height: 400px;
      border-radius: 10px;
      background-color: #ffffff;
      box-sizing: border-box;
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%,-50%);
      /*transform: translateY(-50%);*/
      .trueFn{
        width: calc(~"100% - 350px");
        height:110px;
        padding: 0 175px;
        position: relative;
        bottom: 0;
        div{
          width: 100%;
          height: 70px;
          line-height: 70px;
          text-align: center;
          font-size: 30px;
          color: #317db9;
          border: 1px solid #317db9;/*no*/
          border-radius: 10px;
        }
      }
      .pop_title{
        font-size: 34px;
        line-height: 100px;
        text-align: center;
        color: #333;
        border-bottom: 1px solid #efefef;/*no*/
      }
      .pop_text{
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 15px 30px;
        min-height: 132px;
        max-height: 172;
        font-size: 30px;
        line-height: 48px;
        text-align: center;
        color: #191919;
        overflow: hidden;
      }
      .addClass{
        text-align: left;
        line-height: 25px;
      }
    }
  }
  .trueFnTwo{
    width: calc(~"100% - 100px");
    height:110px;
    padding: 0 50px;
    position: relative;
    bottom: 0;
    div{
      width: 200px;
      height: 70px;
      line-height: 70px;
      text-align: center;
      float: left;
      font-size: 30px;
      color: #ffa936;
      border: 1px solid #ffa936;/*no*/
      border-radius: 10px;
    }
    div:nth-of-type(1){
      float: right;
      color: #317db9;
      border: 1px solid #317db9;/*no*/
    }
  }
</style>



