<template>
  <div class="gyl-card-detail">
    <div v-title>提货卡记账</div>
    <Header :border="true" :title="headTitle" :left="headLeft" ></Header>
    <section class="content">
      <div class="card-box">
        <div class="card">
          <p class="card-key">卡号</p>
          <p class="card-val">{{cardMsg.code}}</p>
          <div class="card-amount-box">
            <div class="card-amount fl">
              <p class="card-key">面值</p>
              <p class="card-val">{{cardMsg.totelValue}}BSTK</p>
            </div>
            <div class="card-amount fr">
              <p class="card-key">余额</p>
              <p class="card-val">{{cardMsg.balance}}BSTK</p>
            </div>
          </div>
        </div>
      </div>
      <div class="card-use-list">
        <div class="card-use-row" v-for="(record, index) in useList" :key="index">
          <div class="row-left fl">
            <p class="card-name">{{record.title}}</p>
            <p class="card-time">{{new Date(record.createTime)|dateFormat('YYYY-MM-DD HH:mm')}}</p>
          </div>
          <div class="row-right fr">
            <p class="card-turnover">{{record.amount<0 ? record.amount : '+' + record.amount}}BSTK</p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>
<script>
import { dateFormat } from 'vux';
import { showMsg, valid } from '@/utils/common.js';
import apiUrl from '@/config/apiUrl.js';
import Header from '@/components/common/Header';
export default {
  data() {
    return {
      headTitle: "提货卡记账",
      headLeft: {
        label: "",
        className: "ico-back"
      },
      puCardId:'', //卡id
      cardMsg:{},  //卡信息
      useList:[]   //卡使用历史记录
    };
  },
  filters: {
    dateFormat
  },
  components: {
    Header
  },
  methods: {
    getCardRecord:function() {
      // TODO 获取卡使用记录
      let param = {
        puCardId: this.puCardId
      };
      this.$httpPost(apiUrl.getPuCardTradeRecord, param)
      .then(res => {
        if (res.status.code==0&&res.data) {
          this.cardMsg = res.data.puCard;
          this.useList = res.data.records;
        } else {
          showMsg(res.status.message);
        }
      })
      .catch(err => {
        console.log(err);
      });
    }
  },
  mounted() {
    this.puCardId = this.$route.query.cardId;
    this.getCardRecord();
  }
};
</script>
<style lang="less">
html,body{
  height: 100%;
  overflow: hidden;
}

.gyl-card-detail{
  height: 100%;
  overflow: hidden;
  background-color: #F3F4F6;
  .content{
    height: calc(~"100% - 90px");
    overflow-y: auto;
    -webkit-overflow-scrolling: touch;
    .card-box{
      padding: 50px 50px 0;
      margin-bottom: 20px;
      background-color: #183c59;
      overflow: hidden;
      .card{
        border-top-left-radius: 40px;
        border-top-right-radius: 40px;
        padding: 45px 40px 0;
        color: #fff;
        background: url('../../assets/images/Bg/card-account-bg.png') center no-repeat #fff;
        background-size: 115% 130%;
        .card-key{
          line-height: 44px;
          font-size: 24px;
        }
        .card-val{
          line-height: 52px;
          font-size: 28px;
          margin-bottom: 60px;
        }
        .card-amount-box{
          overflow: hidden;
          .card-amount{
            width: 50%;
          }
        }
      }
    }
    .card-use-list{
        padding: 0 30px;
        background-color: #fff;
      .card-use-row{
        padding: 30px 0;
        border-bottom: 1px solid #efefef; /*no*/
        overflow: hidden;
        &.no-border-bottom{
          border-bottom: 0;
        }
        .row-left{
          max-width: 50%;
          .card-name{
            height: 50px;
            line-height: 48px;
            font-size: 26px;
            max-width: 100%;
            overflow: hidden;
            text-overflow:ellipsis;
            white-space: nowrap;
          }
          .card-time{
            height: 38px;
            line-height: 38px;
            font-size: 20px;
            color: #999;
          }
        }
        .card-turnover{
          height: 50px;
          line-height: 48px;
          font-size: 24px;
          color: #317db9;
        }
      }
    }
  }
}
</style>
