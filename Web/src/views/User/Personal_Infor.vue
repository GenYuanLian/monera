<template>
  <div class="gyl-personalInfor">
    <div v-title>个人信息</div>
    <Header title="个人信息" :border="true" :backFun="headBack"></Header>
    <section class="content">
      <div class="menue" @click="uploadHeader">
        <div class="header-row">
          <p>头像</p>
          <span class="icon-box"><i class="ico-more"></i></span>
          <span class="img-box"><img v-if="headPortrait!=''" :src="headPortrait" alt=""><img v-else src="../../assets/images/Bg/head-default.png" alt=""><uploader class="btn-upload" ref="upload" :maximum="1" @input="uploadImage"></uploader></span>
        </div>
      </div>
      <div class="menue mg-b">
        <div @click="changeNickName">
          <p>昵称</p><span class="icon-box"><i class="ico-more"></i></span><p class="text-box">{{nickName ? nickName : '未设置'}}</p>
        </div>
        <div class="no-border" @click="changeUserName">
          <p>用户名</p><span class="icon-box"><i class="ico-more"></i></span><p class="text-box">{{userName ? userName : '未设置'}}</p>
        </div>
      </div>
      <div class="menue">
        <div class="no-border">
          <p class="row-title">账号绑定</p>
        </div>
      </div>
      <div class="menue">
        <div>
          <p>手机</p><span class="icon-box"><i class="ico-more"></i></span><p class="text-box">{{mobile ? mobile : '未绑定'}}</p>
        </div>
      </div>
      <div class="menue">
        <div>
          <p>微信</p><span class="icon-box"><i class="ico-more"></i></span><p class="text-box">{{wechat ? wechat : '未绑定'}}</p>
        </div>
      </div>
      <div class="menue mg-b">
        <div class="no-border">
          <p>QQ</p><span class="icon-box"><i class="ico-more"></i></span><p class="text-box">{{qq ? qq : '未绑定'}}</p>
        </div>
      </div>
      <div class="menue">
        <div @click="setPayPwd">
          <p>交易密码</p><span class="icon-box"><i class="ico-more"></i></span><p class="text-box">{{hasPayPwd ? '已设置' : '未设置'}}</p>
        </div>
      </div>
      <div class="menue">
        <div class="no-border" @click="setLoginPwd">
          <p>登录密码</p><span class="icon-box"><i class="ico-more"></i></span><p class="text-box">{{hasPwd ? '已设置' : '未设置'}}</p>
        </div>
      </div>
    </section>
    <!-- <transition name="fade" mode="out-in" appear>
      <div class="updImg" v-show="uploadShow" @click="closeMask">
        <section class="updImg-mask" @click.stop="stopPop">
          <div class="pic-box">
            <span><i class="ico-pic"></i></span><span>照片库</span>
            <uploader class="btn-upload" ref="upload" :maximum="1" @input="uploadImage"></uploader>
          </div>
          <div @click="closeMask">取消</div>
        </section>
      </div>
    </transition> -->
    <!-- <uploadImg @getUploadImg="getImg" ref="uoloadImg"></uploadImg> -->
  </div>
</template>
<script>
import { mapGetters } from "vuex";
import Header from "@/components/common/Header";
import Uploader from "vue-upload-component";
import { showMsg, loading, valid, readFile } from "@/utils/common.js";
import apiUrl from "@/config/apiUrl.js";
export default {
  data() {
    return {
      headPortrait: "", // 头像
      headPortraitUp:'',
      nickName: "", // 昵称
      userName: "",
      mobile: "", // 手机号
      wechat:"", // 微信
      qq:"", // QQ
      password:"", // 密码
      hasPwd:"",
      hasPayPwd:false,
      uploadShow: false // 上传图片蒙版
    };
  },
  components: {
    Header,
    Uploader
  },
  computed: {
    ...mapGetters({
      getLoginUser: "getLoginUser"
    })
  },
  methods: {
    headBack: function() {
      // 返回
      this.$router.replace("mine");
    },
    getUserInfor() {
      // 获取用户信息
      let param = {};
      this.$httpPost(apiUrl.getMemberInfo, param).then(res => {
        if (res.status.code==0&&res.data) {
          this.headPortrait = res.data.info.headPortrait||"";
          this.nickName = res.data.info.nickName||"";
          this.userName = res.data.info.userName||"";
          this.mobile = res.data.info.mobile||"";
          this.wechat = "";
          this.qq = "";
          this.hasPwd = res.data.info.hasPwd;
          this.hasPayPwd = res.data.info.hasPayPwd;
        } else {
          showMsg(res.status.message);
        }
      }).catch(err => {
        console.log(err);
      });
    },
    uploadHeader() {
      // 上传头像
      this.uploadShow = true;
    },
    saveheadPortrait() {
      // 保存头像接口
      let param = {
        imageUrl: this.headPortraitUp
      };
      this.$httpPost(apiUrl.uploadHeadPortrait, param).then(res => {
        if (res.status.code==0&&res.data) {
          showMsg(res.status.message);
        } else {
          showMsg(res.status.message);
        }
      }).catch(err => {
        console.log(err);
      });
    },
    uploadImage(formData) {
      // 上传图片
      let params = {
        formFile: formData[0].file
      };
      this.$httpPost(apiUrl.imgFileUpload, params).then((res) => {
        if(res.status.code==0&&res.data) {
          let data = res.data.content;
          this.headPortrait = data[0].url;
          this.headPortraitUp = data[0].path;
          this.saveheadPortrait();
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    changeNickName() {
      //TODO 修改昵称
      this.$router.push({path:'nick_name', query:{nickName:this.nickName}});
    },
    changeUserName() {
      //TODO 修改昵称
      this.$router.push({path:'user_name', query:{userName:this.userName}});
    },
    setLoginPwd:function() {
      //TODO 设置登录密码
      this.$router.push("loginpwd_edit");
    },
    setPayPwd:function() {
      //TODO 设置交易密码
      this.$router.push("paypwd_edit");
    },
    stopPop() {
      // 阻止冒泡
    }
  },
  mounted() {
    this.getUserInfor();
  }
};
</script>
  
<style lang="less">
html,
body,
.gyl-personalInfor {
  width: 100%;
  height: 100%;
}
.gyl-personalInfor {
  background: #F3F4F6;;
  header {
    background: #fff;
  }
  .content {
    .menue {
      padding-left: 30px;
      background: #fff;
      &.mg-b{
        margin-bottom: 20px;
      }
      div {
        padding: 10px 30px 10px 0;
        border-bottom: 1px solid #efefef; /*no*/
        overflow: hidden;
        &.no-border {
          border-bottom: 0;
        }
        &.header-row {
          padding: 20px 30px 20px 0;
          height: 90px;
          p{
            line-height: 90px;
          }
          .icon-box{
            height: 90px;
            i{
              width:14px;
              height:24px;
              margin-top: 36px;
            }
          }
          .btn-upload{
            top:-88px;
            outline:none;
          }
        }
        p {
          float: left;
          line-height: 66px;
          font-size: 30px;
          color: #333646;
          &.row-title{
            font-size: 30px;
            color: #999999;
            overflow: hidden;
          }
        }
        span {
          display: block;
          float: right;
        }
        input {
          display: block;
          float: right;
          text-align: right;
        }
        ::-webkit-input-placeholder { /* WebKit browsers */
          color:#9FA2AE;
        }
　　    :-moz-placeholder { /* Mozilla Firefox 4 to 18 */
          color:#9FA2AE;
        }
        ::-moz-placeholder { /* Mozilla Firefox 19+ */
          color:#9FA2AE;
        }
        :-ms-input-placeholder { /* Internet Explorer 10+ */
          color:#9FA2AE;
        }
        .text-box {
          font-size: 26px;
          color: #9fa2ae;
          float: right;
          &.col-tips {
            color: #f9615c;
          }
        }
        .img-box {
          width: 88px;
          height: 88px;
          border-radius: 50%;
          border: 1px solid #efefef; /*no*/
          overflow: hidden;
        }
        img {
          display: block;
          width: 88px;
          height: 88px;
          float:left;
        }
        .icon-box {
          width: 42px;
          height: 66px;
          text-align: right;
          i {
            display: inline-block;
            width: 14px;
            height: 24px;
            vertical-align: middle;
            margin-top: 21px;
          }
        }
      }
    }
  }
  .updImg {
    width: 100%;
    height: 100%;
    position: fixed;
    top: 0;
    left: 0;
    z-index: 10;
    background-color: rgba(0,0,0,.5);
    .updImg-mask{
      width: 100%;
      height: 220px;
      position: absolute;
      bottom: 0;
      background: #F3F4F6;;
      div{
        height: 100px;
        line-height: 100px;
        text-align: center;
        font-size: 30px;
        color: #F9615C;
        margin-bottom: 20px;
        background: #fff;
        &.pic-box{
          color: #282D43;
          position: relative;
          .uploader{
            width: 100%;
            height: 100%;
            position: absolute;
            top: 0;
            opacity: 0;
            .weui-uploader__input-box,input{
              display: block;
              width: 100%;
              height: 100%;
            }
          }
        }

        span{
          width: 32px;
          height: 26px;
          margin-right: 20px;
          i{
            width: 32px;
            height: 26px;
          }
        }
      }
    }
  }
}
</style>
