<template>
  <div class="gyl-nick-name">
    <div v-title>修改昵称</div>
    <Header title="修改昵称" :border="true"></Header>
    <section class="content">
      <div class="txt-name">
        <input type="text" placeholder="取一个你喜欢的昵称吧，2-8个字" v-model="nickName">
      </div>
      <div class="btn-modify">
        <input type="button" value="确认修改" @click="saveNickName">
      </div>
    </section>
  </div>
</template>
<script>
import { mapGetters } from "vuex";
import Header from "@/components/common/Header";
import { showMsg, loading } from "@/utils/common.js";
import apiUrl from "@/config/apiUrl.js";
export default {
  data() {
    return {
      nickName:'' // 昵称
    };
  },
  components: {
    Header
  },
  computed: {
    ...mapGetters({
      getLoginUser: "getLoginUser"
    })
  },
  methods: {
    saveNickName() {
      // 保存昵称
      if(this.nickName == undefined || this.nickName == undefined || this.nickName == '') {
        return false;
      }else if(this.nickName.length<2 || this.nickName.length>8) {
        showMsg("请输入2-8个字的昵称");
        return false;
      }
      let param = {
        nickName: this.nickName
      };
      this.$httpPost(apiUrl.setNickname, param).then(res => {
        if(res.status.code==0&&res.data) {
          showMsg(res.status.message, () => {
            this.$router.replace("personal_infor");
          });
        } else {
          showMsg(res.status.message);
        }
      }).catch(err => {
        console.log(err);
      });
    }
  },
  mounted() {
    this.nickName = this.$route.query.nickName;
  }
};
</script>
  
<style lang="less">
.gyl-nick-name {
  height: 100%;
  background: #F3F4F6;;
  header {
    background: #fff;
  }
  .content {
    margin-top: 20px;
    .txt-name {
      margin-top: 20px;
      padding-left: 30px;
      background: #fff;
      input {
        display: block;
        width: 100%;
        padding: 33px 0;
        line-height: 32px;
        font-size: 32px;
        color: #222;
        border-bottom: 1px solid #efefef; /*no*/
      }
      ::-webkit-input-placeholder { /* WebKit browsers */
        color:#aaa;
      }
　　    :-moz-placeholder { /* Mozilla Firefox 4 to 18 */
        color:#aaa;
      }
      ::-moz-placeholder { /* Mozilla Firefox 19+ */
        color:#aaa;
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
