<template>
  <div class="gyl-gesture-pwd">
    <div id="handlock" class="handlock"></div>
  </div>
</template>
<script>
import { showMsg, loading } from "@/utils/common.js";
import apiUrl from "@/config/apiUrl.js";
export {handlock} from '../../utils/handlock.js';
export default {
  data() {
    return {};
  },
  methods: {
    initCanvas:function() {
      let _this = this;
      let el = document.getElementById("handlock");
      new HandLock({
        el: el,
        n: 3,
        model:2,  // 1-验证密码  2-第一次输入密码  3-确认密码
        color:'#3f3f3f',
        verify:_this.verify,
        save:_this.savePass,
        passLen:6,  // 密码长度为6
        showMsg:_this.showText
      }).init();
    },
    verify:function(pass) {
      // 验证密码
      console.log(pass);
      let flg = true;
      let param = {
        value:pass
      };
      this.$httpPost(apiUrl.checkGesturesPwd, param).then((res) => {
        if(res.data&&res.data.status==="1000") {
          showMsg(res.data.msg);
          this.$router.push('/');
          flg = true;
        } else {
          showMsg(res.data.msg);
          flg = false;
        }
      }).catch((err) => {
        console.log(err);
        flg = false;
      });
      return flg;
    },
    savePass:function(pass) {
      // 保存密码
      let param = {
        gesturesPwd:pass
      };
      this.$httpPost(apiUrl.saveGesturesPwd, param).then((res) => {
        if(res.data&&res.data.status==="1000") {
          showMsg(res.data.msg);
          this.$router.push('/');
        } else {
          showMsg(res.data.msg);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    showText:function(text) {
      showMsg(text);
    }
  },
  mounted() {
    this.initCanvas();
  }
};
</script>
<style lang="less">
html,
body {
  width: 100%;
  height: 100%;
}
.gyl-gesture-pwd{
  width: 100%;
  height: 100%;
  .handlock {
    width: 100%;
    height: 100%;
    max-width: 500px;
    min-width: 300px;
    margin: 0 auto;
    position: relative;
  }
}
</style>


