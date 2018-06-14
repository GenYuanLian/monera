<template>
  <div class="wx-share-tip" v-if="show" @click="closeWin">
    <img class="img-tip" src="../../assets/images/Bg/share-tip-bg@2x.png" alt="">
  </div>
</template>
<script>
export default {
  data() {
    return {
      show: false
    };
  },
  props: {
    isShow: {
      type: Boolean,
      default: false
    }
  },
  methods: {
    openWin: function() {
      //TODO 打开
      this.show = true;
    },
    closeWin: function() {
      //TODO 关闭
      this.show = false;
    }
  },
  mounted () {
    this.show = this.isShow;
  }
};
</script>
<style lang="less">
.wx-share-tip{
  position: absolute;
  top:0;
  left:0;
  right:0;
  bottom:0;
  width:100%;
  height:100%;
  overflow: hidden;
  z-index: 999;
  opacity: 0.6;
  background: #000000;
  .img-tip{
    float:right;
    right:0;
    width:537px;
    height:448px;
    font-size: 0px;
    margin-top:10px;
    margin-right: 70px;
    overflow: hidden;
  }
}
</style>

