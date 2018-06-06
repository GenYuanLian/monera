<template>
  <div class="gyl-card-history">
    <div v-title>{{headTitle}}</div>
    <Header :border="true" :title="headTitle" :left="headLeft" ></Header>
    <section class="content">
      <Scroller ref="scroller" height="100%" lock-x :scrollbar-y=false v-model="status" :pullup-config="pullUpConfig" @on-pullup-loading="onPullup" :use-pullup="usePullup">
        <div class="card-box">
          <div class="card-row used" :class="classHandle(card.status)"  v-for="(card, index) in cardList" :key="index" @click="jumpCardDetail(card.id)">
            <p class="card-amount">{{card.bstkValue}}BSTK</p>
            <div class="card-row-right">
              <p class="card-number">卡号&nbsp;&nbsp;&nbsp;{{card.code}}</p>
              <p class="card-leave">余额&nbsp;&nbsp;&nbsp;{{card.balance}}</p>
              <p class="card-time-line">{{new Date(card.createTime)|dateFormat('YYYY-MM-DD HH:mm')}} 至 {{new Date(card.validDate)|dateFormat('YYYY-MM-DD HH:mm')}}</p>
            </div>
          </div>
          <div class="no-card" v-if="cardList.length==0">暂无历史提货卡</div>
        </div>
      </Scroller>
    </section>
  </div>
</template>
<script>
import { showMsg, loading, valid } from "@/utils/common.js";
import { dateFormat, Scroller } from 'vux';
import apiUrl from "@/config/apiUrl.js";
import Header from '@/components/common/Header';
export default {
  data() {
    return {
      headTitle: "历史提货卡",
      headLeft: {
        label: "",
        className: "ico-back"
      },
      cardList:[], // 提货卡列表
      pullUpConfig: { // 上拉组件配置
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
      pageIndex:0,
      pageSize:10,
      status: {
        pullupStatus: 'disabled'
      },
      hasNext: false
    };
  },
  filters: {
    dateFormat
  },
  components: {Header, Scroller},
  methods: {
    getCardHistory: function() {
      // TODO 查询提货卡列表信息
      let param = {
        isValid:0,
        pageIndex: this.pageIndex,
        pageSize: this.pageSize
      };
      this.$httpPost(apiUrl.myPuCards, param).then((res) => {
        if(res.status.code==0&&res.data) {
          let list = res.data.list||[];
          this.hasNext = res.data.hasNext;
          if(this.hasNext) {
            this.cardList = this.cardList.concat(list);
            this.$nextTick(() => {
              this.$refs.scroller.donePullup();
            });
          }else{
            this.cardList = this.cardList.concat(list);
            this.$nextTick(() => {
              this.$refs.scroller.disablePullup();
            });
          }
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    onPullup() {
      // TODO 上拉加载调取事件
      if(this.hasNext) {
        this.pageIndex++;
        this.getCardHistory();
      }else{
        this.$refs.scroller.disablePullup();
      }
    },
    classHandle:function(status) {
      // TODO 判断是已过期还是已使用提货卡
      // 0-未售、1-已锁定、2-售出、3-激活、4-部分使用、5-全部使用、6-已过期、7-已作废
      var _class = '';
      if(status==5) {
        // 已使用
        _class = 'used';
      } else if(status==6) {
        // 已过期
        _class = 'over-time';
      } else if(status==7) {
        // 已作废
        _class = 'destroy';
      }
      return _class;
    },
    jumpCardDetail:function(id) {
      // TODO 跳转提货卡使用详情页面
      this.$router.push({name:'card_detail', query:{cardId:id}});
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.getCardHistory();
    });
  }
};
</script>
<style lang="less">
html,body{
  height: 100%;
}
.gyl-card-history{
  box-sizing: border-box;
  width:100%;
  height: 100%;
  background-color:  #f3f4f6;
  .content{
    height: calc(~"100% - 90px");
    .card-box{
      height: 100%;
      overflow-y: auto;
      -webkit-overflow-scrolling: touch;
      &>div:first-child{
        margin-top: 40px;
      }
      .card-row{
        width: calc(~"100% - 104px");
        margin: 0 30px 20px 30px;
        padding: 40px 22px;
        overflow: hidden;
        border-radius: 10px;
        &.used{
          background: url('../../assets/images/Bg/used-bg.png') calc(~"100% - 20px") 80px no-repeat #fff;
          background-size: auto 55%;
        }
        &.over-time{
          background: url('../../assets/images/Bg/over-time-bg.png') calc(~"100% - 20px") 80px no-repeat #fff;
          background-size: auto 55%;
        }
        &.destroy{
          background: url('../../assets/images/Bg/destroy-bg.png') calc(~"100% - 20px") 80px no-repeat #fff;
          background-size: auto 55%;
        }
        .card-amount{
          width: 180px;
          height: 130px;
          border-radius: 10px;
          font-size: 24px;
          text-align: center;
          line-height: 130px;
          color: #fff;
          background-color: #cecece;
          margin-right: 20px;
          float: left;
        }
        .card-row-right{
          float: right;
          width: calc(~"100% - 200px");
          p{
            color: #cecece;
          }
          .card-number{
            height: 48px;
            line-height: 50px;
            overflow: hidden;
            font-size: 28px;
          }
          .card-leave{
            height: 44px;
            line-height: 44px;
            font-size: 22px;
          }
          .card-time-line{
            height: 38px;
            line-height: 438px4px;
            font-size: 20px;
          }
        }
      }
    }
    .no-card{
      height: 80px;
      line-height: 80px;
      font-size: 24px;
      color: #cecece;
      text-align: center;
      letter-spacing: 2px;
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
