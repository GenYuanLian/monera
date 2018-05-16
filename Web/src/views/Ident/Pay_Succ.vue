<template>
  <div class="gyl-pay-result">
    <div v-title>设置交易密码</div>
    <Header title="设置交易密码" :border="true"></Header>
    <section class="result">
      <div class="tip">
        <img src="../../assets/images/Ident/succeed.svg" alt="">
        <p class="tip-no">交易密码设置成功</p>
      </div>
      <div class="reinput">
        <input type="button" value="完成" @click="finishClick"/>
      </div>
    </section>
  </div>
</template>
<script>
import Header from "@/components/common/Header";
import { showMsg, loading, valid } from "@/utils/common.js";
export default {
  data() {
    return {};
  },
  components: {
    Header
  },
  methods: {
    finishClick: function() {
      this.$router.push("/");
    }
  },
  mounted () {
  }
};
</script>
<style lang="less">
.gyl-pay-result{
  .result{
    .tip{
      margin-top:120px;
      text-align: center;
      img{
        width:120px;
        height: 120px;
        font-size:0;
      }
      .tip-no{
        margin-top:40px;
        font-size: 40px;
        color: #F9615C;
      }
      .tip-re{
        margin-top:36px;
        font-size: 32px;
        color: #333646;
      }
    }
    .reinput{
      width:100%;
      height:90px;
      margin-top:324px;
      text-align: center;
      input[type='button']{
        width:550px;
        height: 90px;
        line-height:90px;
        background: #EBC97F;
        border-radius: 90px;
        font-size: 36px;
        color: #624621;
        margin:0 auto;
        &.btn-next{
          background: #E2E2E2;
          font-size: 36px;
          color: #6F7281;
        }
      }
    }
  }
}
</style>
