<template>
  <div class="gyl-card-active">
    <div v-title>提货卡激活</div>
    <Header title="提货卡激活" :border="true"></Header>
    <section class="content">
      <p class="card-title">激活码</p>
      <div class="txt-code">
        <input type="text" placeholder="请输入提货卡上的激活码" minlength="12" maxlength="12" v-model="activeCode">
      </div>
      <div class="btn-modify">
        <input type="button" value="激活" @click="activeCard">
      </div>
    </section>
    <Popup ref="pop" :popObj="popObj"></Popup>
  </div>
</template>
<script>
import { mapGetters } from "vuex";
import Header from "@/components/common/Header";
import Popup from '@/components/popup';
import { showMsg, loading } from "@/utils/common.js";
import apiUrl from "@/config/apiUrl.js";
export default {
  data() {
    return {
      activeCode:'', // 激活码
      popObj:{
        popTitle:'提示',
        popText:'',
        popIndex:1
      }
    };
  },
  components: {
    Header, Popup
  },
  computed: {
    ...mapGetters({
      getLoginUser: "getLoginUser"
    })
  },
  methods: {
    activeCard() {
      // 激活提货卡
      var _this = this;
      if(this.activeCode.length!=12) {
        this.popObj.popText = '请输入正确的激活码';
        this.$refs.pop.openWin();
        return false;
      }
      let param = {
        activationCode: this.activeCode
      };
      this.$httpPost(apiUrl.activationPuCard, param)
        .then(res => {
          if (res.status.code==0&&res.data) {
            showMsg(res.status.message, () => {
              this.$router.replace("card");
            });
          } else {
            this.popObj.popText = res.status.message;
          }
        })
        .catch(err => {
          console.log(err);
        });
    }
  },
  mounted() {}
};
</script>
  
<style lang="less">
.gyl-card-active {
  height: 100%;
  background: #F3F4F6;
  .content {
    margin-top: 20px;
    .card-title{
      line-height: 86px;
      font-size: 28px;
      color: #333;
      padding-left: 32px;
    }
    .txt-code {
      background: #fff;
      input {
        display: block;
        width: 100%;
        padding: 20px 0 20px 30px;
        line-height: 40px;
        font-size: 40px;
        color: #333;
        font-size:28px;
      }
      ::-webkit-input-placeholder { /* WebKit browsers */
        color:#999999;
      }
　　    :-moz-placeholder { /* Mozilla Firefox 4 to 18 */
        color:#999999;
      }
      ::-moz-placeholder { /* Mozilla Firefox 19+ */
        color:#999999;
      }
      :-ms-input-placeholder { /* Internet Explorer 10+ */
        color:#aaa;
      }
    }
    .btn-modify{
      height:80px;
      line-height: 80px;
      padding:0 75px;
      margin-top:80px;
      input[type="button"]{
        display: block;
        width:100%;
        height:80px;
        line-height: 80px;
        margin:0 auto;
        color:#fff;
        background-color: #317db9;
        border-radius: 10px;
        font-size:30px;
      }
    }
  }
}
</style>
