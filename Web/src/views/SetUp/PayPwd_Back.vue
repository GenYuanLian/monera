<template>
  <div class="gyl-paypwd-back">
    <div v-title>找回交易密码</div>
    <Header title="找回交易密码" :border="true"></Header>
    <section class="content">
      <div v-show="cardList && cardList.length>0">
        <div class="tip">请选择验证以下的任一银行卡信息，以确保本人操作</div>
        <div class="con-row" v-for="(bank,i) in cardList" v-bind:key="i" @click="bankClick(bank)">
          <span class="con-l"><i v-if="!bank.bankImg" class="ico-bank-def"></i><img else :src="bank.bankImg"><p>{{bank.bankName}} {{bank.cardType|bankTypeFilter}}（{{bank.cardNo|bankNoFilter}}）</p></span><span class="con-r"><i class="ico-arrow-r"></i></span>
        </div>
      </div>
      <div v-show="!cardList">
        <div class="error">请添加银行卡后在找回交易密码</div>
      </div>
    </section>
  </div>
</template>
<script>
import Header from "@/components/common/Header";
import GetVerifyCode from "@/components/GetVerificationCode";
import { showMsg, loading, valid } from "@/utils/common.js";
import apiUrl from "@/config/apiUrl.js";
export default {
  data() {
    return {
      cardList: []
    };
  },
  components: {
    Header, GetVerifyCode
  },
  methods: {
    initBanks: function() {
      //TODO 查询绑定的银行卡
      let param = {};
      this.$httpPost(apiUrl.queryCardList, param).then((res) => {
        if(res.data&&res.data.status==="1000") {
          this.cardList = res.data.catdlist;
        } else {
          showMsg(res.data.msg);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    bankClick: function(bank) {
      //TODO 选择银行
      var param = {
        bankName: bank.bankName,
        bindMobile: bank.bindMobile,
        cardNo: bank.cardNo,
        cardType: bank.cardType
      };
      this.$router.push({path: "paypwd_back_next", query: param});
    }
  },
  mounted () {
    this.initBanks();
  }
};
</script>
<style lang="less">
html,body{
  height: 100%;
  overflow: hidden;
}
.gyl-paypwd-back{
  background-color: #F3F4F6;
  height:100%;
  .content{
    background-color: #fff;
    .tip{
      height: 80px;
      line-height: 80px;
      padding: 0 30px;
      font-size: 24px;
      color: #6F7281;
      background-color: #F3F4F6;
    }
    .con-row{
      height: 100px;
      line-height: 100px;
      margin: 0 30px;
      border-bottom:1px solid #E2E2E2;/*no*/
      text-align: left;
      .con-l{
        display: inline-block;
        height: 100px;
        line-height: 100px;
        i,img{
          width:66px;
          height:66px;
          margin-bottom:-22px;
        }
        p{
          display: inline-block;
          margin-left:30px;
          font-size: 28px;
          color: #333646;
        }
      }
      .con-r{
        display: block;
        width:40px;
        height: 100px;
        line-height: 100px;
        float: right;
        margin-right:40px;
        i{
          width:13px;
          height: 26px;
          margin-bottom:-6px;
        }
      }
    }
    :last-child{
      border-bottom:none;
    }
    .error{
      height: 80px;
      line-height: 80px;
      padding: 0 30px;
      font-size: 24px;
      color: #6F7281;
      background-color: #F3F4F6;
      text-align: center
    }
  }
}
</style>
