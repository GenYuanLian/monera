<template>
  <div class="gyl-card">
    <div v-title>{{headTitle}}</div>
    <Header :border="true" :title="headTitle" :left="headLift" ></Header>
    <section class="content">
      <div class="card-bg"></div>
      <div class="card-info">
        <div class="card-info-row"><span class="img-box"><i class="ico-cards"></i></span><span class="card-title">提货卡</span><span class="card-content">{{cardCount}}张</span><span class="card-next" @click="jumpCardActive">提货卡激活</span><span class="card-next-arrow" @click="jumpCardActive"><i class="ico-more-blue"></i></span></div>
        <div class="card-info-row"><span class="img-box"><i class="ico-balance"></i></span><span class="card-title">余额合计</span><span class="card-content">{{sumBalance}}BSTK</span><span class="card-next" @click="jumpCardHistory">历史提货卡</span><span class="card-next-arrow" @click="jumpCardHistory"><i class="ico-more-blue"></i></span></div>
      </div>
      <scroller v-if="cardList.length>0" ref="scroll" use-pullup :pullup-config="pullUpConfig" @on-pullup-loading="pullUpHandle">
        <div class="card-box">
          <div class="card-row" v-for="(card, index) in cardList" :key="index">
            <p class="card-amount">{{card.bstkValue}}BSTK</p>
            <div class="card-row-right">
              <p class="card-number">卡号&nbsp;&nbsp;&nbsp;{{card.code}}</p>
              <p class="card-leave">余额&nbsp;&nbsp;&nbsp;{{card.balance}}</p>
              <p class="card-time-line">{{new Date(card.createTime)|dateFormat('YYYY-MM-DD HH:mm')}} 至 {{new Date(card.validDate)|dateFormat('YYYY-MM-DD HH:mm')}}</p>
            </div>
          </div>
        </div>
      </scroller>
    </section>
  </div>
</template>
<script>
import { dateFormat, Scroller } from 'vux';
import { showMsg, loading, valid } from "@/utils/common.js";
import apiUrl from "@/config/apiUrl.js";
import Header from '@/components/common/Header';
export default {
  data() {
    return {
      headTitle: "我的提货卡",
      headLift: {
        label: "",
        className: "ico-back"
      },
      pageIndex:0, // 当前页
      pullUpConfig: { // 上拉组件配置
        content: '上拉加载更多',
        downContent: '松开进行加载',
        upContent: '上拉加载更多',
        loadingContent: '加载中...'
      },
      sumBalance:'',  // 提货卡余额
      cardCount:'',  // 提货卡数量
      cardList:[] // 提货卡列表信息
    };
  },
  components: { dateFormat, Header, Scroller },
  methods: {
    getCardInfo: function() {
      //TODO 查询提货卡信息
      let param = {
        isValid:1,
        pageIndex:this.pageIndex,
        pageSize:10
      };
      this.$httpPost(apiUrl.myPuCards, param).then((res) => {
        if(res.status.code==0&&res.data) {
          this.sumBalance = res.data.sumBalance;
          this.cardCount = res.data.cardCount;
          this.cardList = res.data.list;
          this.pageIndex += 1;
          if(this.cardList.length > 0) {
            this.$nextTick(() => {
              this.$refs.scroller.reset();
            });
          }
        }else{
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    pullUpHandle:function() {
      // TODO 处理上拉加载数据
      let param = {
        isValid:1,
        pageIndex:this.pageIndex,
        pageSize:10
      };
      this.$httpPost(apiUrl.myPuCards, param).then((res) => {
        if(res.status.code==0&&res.data) {
          this.cardList.push(res.data.list);
          this.pageIndex += 1;
          this.$nextTick(() => {
            this.$refs.scroller.reset();
          });
          if(res.data.hasNext==0) {
            this.$refs.scroller.disablePullup();
          }
        }else{
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    jumpCardHistory:function() {
      // TODO 跳转历史提货卡页面
      this.$router.push('card_history');
    },
    jumpCardActive:function() {
      // TODO 跳转提货卡激活页面
      this.$router.push('card_active');
    }
  },
  mounted() {
    this.getCardInfo();
  }
};
</script>
<style lang="less">
html,body{
  height: 100%;
}
.gyl-card{
  box-sizing: border-box;
  width:100%;
  height: 100%;
  background-color:  #f3f4f6;
  .content{
    height: calc(~"100% - 90px");
    .card-bg{
      width: 100%;
      height: 230px;
      background: url('../../assets/images/Bg/card-top-bg.png') center no-repeat;
      background-size: 100% 100%;
    }
    .card-info{
      width: calc(~"100% - 110px");
      margin: -130px 30px 24px 30px;
      padding: 0 25px 60px 25px;
      overflow: hidden;
      background-color: #fff;
      border-radius: 10px;
      .card-info-row{
        height: 36px;
        margin-top: 48px;
        span{
          display: block;
          float: left;
          margin-right: 20px;
          &.img-box{
            width: 40px;
            height: 36px;
          }
          &.card-title{
            width: 120px;
            line-height: 36px;
            font-size: 28px;
            color: #333333;
          }
          &.card-content{
            width: calc(~"100% - 385px");
            line-height: 36px;
            font-size: 24px;
            color: #ffa936;
          }
          &.card-next{
            width: 130px;
            line-height: 36px;
            font-size: 24px;
            color: #3781bb;
          }
          &.card-next-arrow{
            width: 14px;
            height: 40px;
            line-height: 40px;
            margin-right: 0;
          }
          .ico-cards{
            width: 40px;
            height: 36px;
            background-size: 100% auto;
            background-position: center;
          }
          .ico-balance{
            width: 40px;
            height: 36px;
            background-size: auto 100%;
            background-position: center;
          }
          .ico-more-blue{
            width: 14px;
            height: 36px;
            background-position: center;
          }
        }
      }
    }
    .card-box{
      height: calc(~"100% - 352px");
      overflow-y: auto;
      -webkit-overflow-scrolling: touch;
      .card-row{
        width: calc(~"100% - 104px");
        margin: 0 30px 20px 30px;
        padding: 40px 22px;
        overflow: hidden;
        background: url('../../assets/images/Bg/card-row-bg.png') center no-repeat;
        background-size: 100% 100%;
        border-radius: 10px;
        .card-amount{
          width: 180px;
          height: 130px;
          border-radius: 10px;
          font-size: 24px;
          text-align: center;
          line-height: 130px;
          color: #fff;
          background: url('../../assets/images/Bg/card-bg.png') center no-repeat;
          background-size: 100% 100%;
          margin-right: 20px;
          float: left;
        }
        .card-row-right{
          float: right;
          width: calc(~"100% - 200px");
          .card-number{
            height: 48px;
            line-height: 50px;
            overflow: hidden;
            font-size: 28px;
            color: #333;
          }
          .card-leave{
            height: 44px;
            line-height: 44px;
            font-size: 22px;
            color: #999;
          }
          .card-time-line{
            height: 38px;
            line-height: 438px4px;
            font-size: 20px;
            color: #999;
          }
        }
      }
    }
  }
}
</style>
