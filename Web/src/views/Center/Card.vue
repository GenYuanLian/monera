<template>
  <div class="gyl-card">
    <div v-title>{{headTitle}}</div>
    <Header :border="true" :title="headTitle" :left="headLeft" ></Header>
    <section class="content">
      <div class="card-info">
        <div class="card-left fl">
          <p>有效提货卡：{{cardCount}}张</p>
          <p>余额合计：{{sumBalance}}BSTK</p>
        </div>
        <div v-show="false" class="card-right fr" @click="jumpCardActive">提货卡激活</div>
        <div class="card-bot fl" @click="jumpCardHistory">历史提货卡<i class="ico-more"></i></div>
      </div>
      <Scroller class="card-scroll" height="-175" ref="scrollerCard" v-model="status" :pullup-config="pullUpConfig" @on-pullup-loading="pullUpHandle" lock-x :scrollbar-y=false :use-pullup="true">
        <div class="card-box">
          <div class="card-row" v-for="(card, index) in cardList" :key="index" @click="jumpCardDetail(card.id)">
            <div class="card-amount">
              <img v-if="card.picUrl&&card.picUrl!=''" :src="card.picUrl" alt="">
              <img v-else src="../../assets/images/Bg/card-bg.png" alt="">
              <p>{{card.bstkValue}}BSTK</p>
            </div>
            <div class="card-row-right">
              <p class="card-number">卡号&nbsp;&nbsp;&nbsp;{{card.code}}</p>
              <p class="card-leave">余额&nbsp;&nbsp;&nbsp;{{card.balance}}</p>
              <p class="card-time-line">{{new Date(card.createTime)|dateFormat('YYYY-MM-DD HH:mm')}} 至 {{new Date(card.validDate)|dateFormat('YYYY-MM-DD HH:mm')}}</p>
            </div>
          </div>
          <div class="no-card" v-if="cardList.length==0">暂无可用提货卡，快去加购吧~</div>
        </div>
      </Scroller>
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
      headLeft: {
        label: "",
        className: "ico-back"
      },
      pageIndex:0, // 当前页
      pageSize:10, // 当前页面显示条数
      pullUpConfig: { // 上拉组件配置
        content: '上拉加载更多',
        downContent: '松开进行加载',
        upContent: '上拉加载更多',
        loadingContent: '加载中...',
        pullUpHeight: 60,
        height: 40,
        autoRefresh: false,
        clsPrefix: 'xs-plugin-pullup-'
      },
      status: {
        pullupStatus: 'disabled'
      },
      hasNext:false, // 是否有下一页
      sumBalance:'',  // 提货卡余额
      cardCount:'',  // 提货卡数量
      cardList:[] // 提货卡列表信息
    };
  },
  filters: {
    dateFormat
  },
  components: { Header, Scroller },
  methods: {
    getCardInfo: function() {
      //TODO 查询提货卡信息
      let param = {
        isValid:1,
        pageIndex: this.pageIndex,
        pageSize: this.pageSize
      };
      this.$httpPost(apiUrl.myPuCards, param).then((res) => {
        if(res.status.code==0&&res.data) {
          let list = res.data.list||[];
          this.sumBalance = res.data.sumBalance||0;
          this.cardCount = res.data.cardCount;
          this.hasNext = res.data.hasNext;
          if(this.hasNext) {
            this.cardList = this.cardList.concat(list);
            this.$nextTick(() => {
              this.$refs.scrollerCard.donePullup();
            });
          }else{
            this.cardList = this.cardList.concat(list);
            this.$nextTick(() => {
              this.$refs.scrollerCard.disablePullup();
            });
          }
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    pullUpHandle:function() {
      // TODO 处理上拉加载数据
      if(this.hasNext) {
        this.pageIndex++;
        this.getCardInfo();
      }else{
        this.$refs.scrollerCard.disablePullup();
      }
    },
    jumpCardHistory:function() {
      // TODO 跳转历史提货卡页面
      this.$router.push('card_history');
    },
    jumpCardActive:function() {
      // TODO 跳转提货卡激活页面
      this.$router.push('card_active');
    },
    jumpCardDetail:function(id) {
      // TODO 跳转提货卡使用详情页面
      this.$router.push({name:'card_detail', query:{cardId:id}});
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.getCardInfo();
    });
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
    .card-info{
      background-color: #fff;
      overflow: hidden;
      margin-bottom: 20px;
      .card-left{
        padding: 25px 0 25px 30px;
        width: calc(~"100% - 260px");
        height: 116px;
        p{
          height: 58px;
          line-height: 58px;
          font-size: 28px;
          color: #333;
        }
      }
      .card-right{
        width: 200px;
        height: 70px;
        font-size: 26px;
        line-height: 66px;
        text-align: center;
        color: #317db9;
        border: 2px solid #317db9;
        box-sizing: border-box;
        border-radius: 10px;
        margin: 54px 30px 0 0;
      }
      .card-bot{
        width: 100%;
        height: 70px;
        line-height: 70px;
        font-size: 24px;
        color: #555;
        text-align: center;
        border-top: 1px solid #efefef; /*no*/
        i{
          display: inline-block;
          width: 14px;
          height: 24px;
          margin-left: 20px;
          margin-top: -5px;
          vertical-align: middle;
        }
      }
    }
    .no-card{
      height: 80px;
      line-height: 80px;
      font-size: 20px;
      color: #cecece;
      text-align: center;
      letter-spacing: 2px;
    }
    .card-box{
      // height: calc(~"100% - 280px");
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
          width: 176px;
          height: 130px;
          border: 1px solid #efefef;/*no*/
          border-radius: 10px;
          font-size: 24px;
          text-align: center;
          line-height: 130px;
          color: #fff;
          margin-right: 20px;
          float: left;
          img{
            width: 100%;
            height: 100%;
            border-radius: 10px;
            float:left;
          }
          p{
            position: absolute;
            width: 178px;
            height: 130px;
            line-height: 130px;
          }
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
    .xs-plugin-pullup-container{
      line-height: 60px;
      font-size: 28px;
      color: #6F7281;
      background: #F3F4F6;;
    }
  }
}
</style>
