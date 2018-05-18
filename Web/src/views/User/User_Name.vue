<template>
  <div class="gyl-username">
    <div v-title>设置用户名</div>
    <Header title="设置用户名" :border="true"></Header>
    <section class="content">
      <div class="txt-name">
        <input type="text" placeholder="取一个登录的用户名吧，4-16个字符" minlength="4" maxlength="16" v-model="userName">
      </div>
      <div class="btn-modify">
        <input type="button" value="确认修改" @click="saveUserName">
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
      userId:0,
      userName:'' // 用户名
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
    saveUserName() {
      // 设置用户名
      if(this.userName.length<4 || this.userName.length>16) {
        showMsg("请输入4-16个字符");
        return false;
      }
      let param = {
        loginName: this.userName
      };
      this.$httpPost(apiUrl.setLoginName, param).then(res => {
        if (res.status.code==0&&res.data) {
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
    this.userId = this.$route.query.id||0;
    this.userName = this.$route.query.userName||"";
  }
};
</script>
  
<style lang="less">
.gyl-username {
  height: 100%;
  background: #F3F4F6;;
  header {
    background: #fff;
  }
  .content {
    margin-top: 20px;
    .txt-name {
      margin-top: 20px;
      background: #fff;
      input {
        display: block;
        width: 100%;
        padding: 20px 0 20px 30px;
        line-height: 40px;
        font-size: 40px;
        color: #222;
        font-size:30px;
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
