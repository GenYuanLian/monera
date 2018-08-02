<template>
  <div class="gyl-mer-detail">
    <div v-title>{{merchant.merchName}}</div>
    <Header :border="true" :title="headTitle" :left="headLeft" ></Header>
    <section class="content">
      <div class="mer-shop">
        <img :src="merchant.logoPic" alt="">
        <p class="mer-title" v-text="merchant.merchName"></p>
        <p class="mer-star"><i :class="getStarCss(merchant.praise)"></i></p>
      </div>
      <div class="notice">
        <span class="i-notice fl"><i class="ico-notice"></i></span>
        <div class="title fl">公告</div>
        <div class="message fl" > 
          <msgScroll :id="'detaileMsg'" ref="msgScroll" :msg="merchant.notice"></msgScroll>
        </div>
      </div>
      <div class="mer-adv">
        <swiper class="pro-swiper" height="150px" :list="proImgs" v-model="proImgIdx" :loop="true" :auto="true" :show-desc-mask="false" :dots-position="'center'" :dots-class="'pro-dots'"></swiper>
      </div>
      <div class="mer-scene">
        <div class="scene-title">商家实景</div>
        <div class="scene-img">
          <div class="scene-con" :style="{width:sceneWidth}">
            <div :ref="'scenePic'+i" v-for="(item,i) in merchantPic" v-bind:key="i">
              <img :src="item.url" :alt="item.title">
            </div>
          </div>
          <!-- <Scroller lock-y scrollbar-x :bounce=false>
            <div class="scene-con">
              <div v-for="(item,i) in merchantPic" v-bind:key="i">
                <img :src="item.url" :alt="item.title">
              </div>
            </div>
          </Scroller> -->
        </div>
      </div>
      <div class="mer-info">
        <div class="info-title">商家信息</div>
        <div class="info-detail">
          <div><span class="info-lab">商家简介</span><span class="info-con" v-text="merchant.briefIntro"></span></div>
          <div><span class="info-lab">分类</span><span class="info-con" v-text="merchant.category"></span></div>
          <div><span class="info-lab">地址</span><span class="info-con" v-text="merchant.areaName+merchant.address"></span></div>
          <div class="no-bb"><span class="info-lab">联系方式</span><span class="info-con" v-html="merchant.contact+'<br />'+merchant.tel"></span></div>
        </div>
      </div>
      <div class="mer-eval" v-if="evalList.length>0">
        <div class="eval-title">商家评价</div>
        <div class="eval-detail" v-for="(eva,i) in evalList" v-bind:key="i">
          <div class="eval-user">
            <img v-if="eva.headPortrait&&eva.headPortrait!=''" :src="eva.headPortrait">
            <img v-else src="../../assets/images/Bg/head-default.png" alt="">
            <div class="user-r fl">
              <p class="fl">{{eva.memberName}}</p>
              <p class="fr">{{new Date(eva.createTime)|dateFormat('YYYY-MM-DD HH:mm')}}</p>
            </div>
            <div class="eval-score fl" :class="'ico-star-' + eva.score"></div>
          </div>
          <div class="eval-judge">{{eva.comment ? eva.comment : '默认好评'}}</div>
        </div>
        <div class="eval-more" @click="evalMore">查看更多 <i class="ico-more"></i></div>
      </div>
    </section>
  </div>
</template>
<script>
import { mapActions, mapGetters } from "vuex";
import { showMsg, valid } from '@/utils/common.js';
import apiUrl from '@/config/apiUrl.js';
import Header from '@/components/common/Header';
import msgScroll from '@/components/messageScroll/index';
import { Swiper, Scroller, dateFormat } from 'vux';
export default {
  data() {
    return {
      headTitle: "商家详情",
      headLeft: {
        label: "",
        className: "ico-back"
      },
      sceneWidth:0,
      proImgIdx: 0,
      proImgs: [],
      merchantId:0,
      merchant:{},
      merchantPic:[],
      advertistPic:[],
      evalList:[] // 评价列表
    };
  },
  filters: {
    dateFormat
  },
  components: {
    Header, Swiper, Scroller, msgScroll
  },
  methods: {
    getStarCss: function(val) {
      return "ico-star-"+val;
    },
    computeScene:function() {
      //计算实景图片宽度
      this.$nextTick(() => {
        if(this.merchantPic.length>0) {
          let width = this.$refs.scenePic0[0].offsetWidth+this.$refs.scenePic0[0].offsetLeft;
          this.sceneWidth = this.merchantPic.length*width+"px";
        }
      });
    },
    getMerchantDetail: function(flag) {
      //TODO 查询商家详情
      let param = {
        merchantId: this.merchantId
      };
      this.$httpPost(apiUrl.getMerchantInfo, param).then((res) => {
        if(res.status.code==0&&res.data) {
          this.merchant = res.data.merch;
          this.merchantPic = res.data.pics;
          this.advertistPic = res.data.advertPics;
          if(res.data.advertPics) {
            res.data.advertPics.forEach(item => {
              this.proImgs.push({url: 'javascript:', img: item.url, title: item.title});
            });
          }
          this.headTitle = res.data.merch.merchName;
          this.$refs.msgScroll.scroll();
          this.computeScene();
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    getMerchantEvaluate: function(flag) {
      //TODO 查询商家评价列表
      let param = {
        merchId: this.merchantId,
        pageIndex:0,
        pageSize:3
      };
      this.$httpPost(apiUrl.getMerchCommens, param).then((res) => {
        if(res.status.code==0&&res.data) {
          this.evalList = res.data.comments;
        } else {
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    evalMore:function() {
      // TODO 产看更多商家评价
      this.$router.push({name:'merchant_evaluate', query:{id:this.merchantId}});
    }
  },
  mounted() {
    this.merchantId = this.$route.query.id||"";
    if(this.merchantId!="") {
      this.getMerchantDetail();
      this.getMerchantEvaluate();
    }
  },
  beforeDestroy() {
    // this.$refs.msgScroll.destroyScroll();
  }
};
</script>
<style lang="less">
html,body{
  height: 100%;
  overflow: hidden;
}

.gyl-mer-detail{
  height: 100%;
  overflow: hidden;
  .content{
    box-sizing:border-box;
    width:100%;
    height:100%;
    overflow-x: hidden;
    overflow-y: scroll;
    background-color: #f3f4f6;
    padding-bottom:90px;
    -webkit-overflow-scrolling: touch;
    .mer-shop{
      width:100%;
      height:285px;
      padding-top:60px;
      background-color: #fff;
      img{
        display:block;
        width:165px;
        height:165px;
        margin:0 auto;
        border-radius:100%;
      }
      .mer-title{
        height:55px;
        line-height:55px;
        text-align: center;
        font-size:34px;
        color:#333;
        margin-top:20px;
      }
      .mer-star{
        height:25px;
        line-height:25px;
        text-align: center;
        i{
          vertical-align: middle;
          margin-top: -10px;
          width: 125px;
          height: 20px;
        }
      }
    }
    .notice{
      height:50px;
      line-height:50px;
      padding:20px 30px;
      background-color:#fff;
      font-size:24px;
      span{
        height:50px;
        line-height: 50px;
      }
      .i-notice{
        display: inline-block;
        width:64px;
        i{
          width:44px;
          height:36px;
          margin-bottom:-8px;
        }
      }
      .title{
        display: inline-block;
        width:80px;
        height:48px;
        line-height: 48px;
        color:#317db9;
        margin-right:20px;
        border-radius:10px;
      }
      .message{
        color:#333;
        font-size:24px;
        overflow: hidden;
      }
    }
    .mer-adv{
      margin-top:20px;
      width:100%;
      background-color: #fff;
    }
    .mer-scene{
      padding-bottom:40px;
      margin-top:20px;
      background-color: #fff;
      overflow: hidden;
      .scene-title{
        padding: 0 30px;
        height:90px;
        line-height: 90px;
        color:#333;
        font-size:30px;
        margin-top: 10px;
      }
      .scene-img{
        padding-left: 30px;
        width:100%;
        height:185px;
        line-height:185px;
        overflow-y: hidden;
        overflow-x: scroll;
        box-sizing: border-box;
        -webkit-overflow-scrolling: touch;
        .scene-con{
          width:auto;
          height:185px;
          white-space:nowrap;
          div{
            width:182px;
            height:182px;
            margin-right:25px;
            border-radius:10px;
            float:left;
            img{
              width:100%;
              height:100%;
            }
          }
        }
      }
    }
    .mer-info{
      padding-left: 30px;
      margin:20px 0;
      background-color: #fff;
      overflow: hidden;
      .info-title{
        height:80px;
        line-height: 80px;
        color:#333;
        font-size:30px;
      }
      .info-detail{
        width:100%;
        div{
          display: inline-block;
          width:calc(~"100% - 30px");
          padding-right: 30px;
          margin-top:20px;
          overflow: hidden;
          vertical-align: middle;
          padding-bottom: 20px;
          border-bottom: 1px solid #efefef; /*no*/
          &.no-bb{
            border-bottom: 0;
          }
          .info-lab{
            display: block;
            width:128px;
            height:45px;
            line-height:45px;
            color:#999;
            border-radius:10px;
            float:left;
            font-size: 26px;
          }
          .info-con{
            display: block;
            line-height:45px;
            margin-left:128px;
            word-wrap: break-word;
            color:#666;
            font-size:26px;
          }
        }
      }
    }
    .mer-eval{
      padding: 0 30px;
      margin:20px 0;
      background-color: #fff;
      overflow: hidden;
      .eval-title{
        height:80px;
        line-height: 80px;
        color:#333;
        font-size:30px;
      }
      .eval-detail{
        padding: 20px 0;
        border-bottom: 1px solid #efefef; /*no*/
        .eval-user{
          padding: 15px 0;
          overflow: hidden;
          img{
            display: block;
            float: left;
            width: 100px;
            height: 100px;
            margin-right: 20px;
            border-radius: 50%;
          }
          .user-r{
            width: calc(~"100% - 120px");
            height: 60px;
            overflow: hidden;
            p{
              font-size: 24px;
              color: #999;
              line-height: 60px;
            }
          }
          .eval-score{
            display: block;
            width: 60%;
            height: 20px;
            margin: 10px 0;
            background-size: 30% auto;
          }
        }
        .eval-judge{
          line-height: 40px;
          font-size: 30px;
          color: #555;
          padding: 10px 0;
        }
      }
      .eval-more{
        height: 80px;
        line-height: 80px;
        font-size: 26px;
        color: #999;
        text-align: center;
        i{
          width: 14px;
          height: 24px;
          vertical-align: middle;
          margin-top: -5px;
        }
      }
    }
  }
}
</style>
