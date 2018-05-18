<template>
  <div class="gyl-setting">
    <div v-title>设置</div>
    <Header title="设置" :border="true" :backFun="headBack"></Header>
    <section class="content">
      <div class="con-row"><span class="title">消息推送</span><span class="con-r"><label class="gyl-label"><input class="mui-switch" type="checkbox"></label></span></div>
      <div class="con-row"><span class="title">清除图片缓存</span><span class="con-r">0.0MB</span></div>
      <div class="con-row"><span class="title">非WIFI下图片质量</span><span class="con-r">普通</span></div>
      <div class="con-row"><span class="title">当前版本</span><span class="con-r"><p v-if="isVerTip" class="ver-tip">有新版本更新</p><p :class="!isVerTip?'no-tip':'version'">v1.0.0</p><i class="ico-more"></i></span></div>
    </section>
    <div class="con-about">
      <span>关于我们</span>
    </div>
    <footer class="gyl-footer">
      <input class="eru-btn" type="button" @click="loginOutClick" value="安全退出">
    </footer>
  </div>
</template>
<script>
import { mapActions, mapGetters } from "vuex";
import Header from "@/components/common/Header";
import { showMsg, loading, valid } from "@/utils/common.js";
import apiUrl from "@/config/apiUrl.js";
export default {
  data() {
    return {
      popPayPwdShow: false,
      isPayPwd: false,
      isVerTip: false
    };
  },
  components: {
    Header
  },
  computed:{
    ...mapGetters(["getLoginUser", "getUserInfo"])
  },
  methods: {
    ...mapActions({
      loginOut: "loginOut"
    }),
    headBack: function() {
      this.$router.replace("mine");
    },
    loginPwdClick: function() {
      //TODO 登录密码
      this.$router.push("loginpwd_edit");
    },
    loginOutClick: function() {
      //TODO 退出登录
      let param = {};
      this.$httpPost(apiUrl.loginOut, param).then((res) => {
        if(res.status.code==0&&res.data) {
          this.loginOut(res.data);
          showMsg(res.status.message, () => {
            this.$router.replace("/");
          });
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    initUserInfo:function() {
      //TODO 查询用户信息
      let _id = this.getLoginUser.id;
      let param = {
        id: _id
      };
      this.$httpPost(apiUrl.selectByPrimaryKey, param).then((res) => {
        if(res.data&&res.data.status==="1000") {
          this.isPayPwd = res.data.isPayPwd;
        } else {
          showMsg(res.data.msg);
        }
      }).catch((err) => {
        console.log(err);
      });
    }
  },
  mounted () {
    if(this.getUserInfo) {
      this.isPayPwd = this.getUserInfo?this.getUserInfo.isPayPwd:false;
    } else {
      this.initUserInfo();
    }
  }
};
</script>
<style lang="less">
html,body{
  height: 100%;
  overflow: hidden;
}
.gyl-setting{
  height:100%;
  overflow-y: auto;
  background-color: #F3F4F6;
  .content{
    background-color: #fff;
    margin-top:20px;
    padding:0 30px;
    .con-row{
      height: 100px;
      line-height: 100px;
      border-bottom: 1px solid  #efefef;/*no*/
      text-align: left;
      .title{
        font-size: 32px;
        color: #333646;
      }
      .con-r{
        position: relative;
        display: inline-block;
        float:right;
        margin-right:40px;
        .ver-tip{
          font-size: 24px;
          color: #F9615C;
          margin-right:30px;
          float: left;
        }
        .version{
          position: absolute;
          height: 24px;
          line-height: 24px;
          bottom:0px;
          right:60px;
        }
        .no-tip{
          margin-right: 0.4rem;
          float: left;
        }
        label{
          margin-top:19px;
        }
        i{
          width:14px;
          height:24px;
          margin-top:-4px;
          vertical-align: middle;
        }
      }
      &:last-child{
        border-bottom: 0px solid  #efefef;/*no*/
      }
    }
  }
  .con-about{
    margin-top:20px;
    height:90px;
    line-height: 90px;
    padding:0 30px;
    background-color: #fff;
    span{
      display: block;
      width:100%;
      color:#333;
      font-size:32px;
    }
  }
  .gyl-footer{
    margin-top:20px;
    text-align: center;
    .eru-btn{
      width:100%;
      height:90px;
      line-height: 90px;
      border:0px solid #fff;
      font-size: 32px;
      color: #317db9;
      background-color: #fff;
      text-align: center;
    }
  }
}
</style>
