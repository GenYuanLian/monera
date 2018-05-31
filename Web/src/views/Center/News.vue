<template>
  <div class="gyl-news">
    <div v-title>通知中心</div>
    <Header title="通知中心" :border="true"></Header>
    <section class="content" v-show="newList&&newList.length>0">
      <Scroller lock-x height="100%" @on-pullup-loading="onPullup" ref="scroller" v-model="status" :use-pullup="usePullup" :pullup-config="pullupConfig">
        <div>
          <div class="new-row" :class="i==newList.length-1 ? 'no-bottom' : ''" v-for="(item,i) in newList" v-bind:key="i">
            <div class="title"><span>{{item.messageTitle}}</span><span class="new-date">{{item.createTime|format("MM-DD HH:mm")}}</span></div>
            <div class="info">{{item.messageContent}}</div>
          </div>
        </div>
      </Scroller>
    </section>
  </div>
</template>
<script>
import { mapActions, mapGetters } from "vuex";
import Header from "@/components/common/Header";
import { Scroller } from 'vux';
import { showMsg, loading, readFile, valid } from "@/utils/common.js";
import apiUrl from "@/config/apiUrl.js";
export default {
  data() {
    return {
      newList: [],
      pageSize: 10,
      pageIndex: 1,
      scrollTop: 0,
      pullupConfig:{
        content: '上拉加载更多',
        pullUpHeight: 60,
        height: 40,
        autoRefresh: false,
        downContent: '释放刷新',
        upContent: '上拉加载更多',
        loadingContent: '加载中...',
        clsPrefix: 'xs-plugin-pullup-'
      },
      usePullup: true,
      status: {
        pullupStatus: 'disabled'
      }
    };
  },
  components: {
    Header, Scroller
  },
  methods: {
    initNews: function() {
      //TODO 查询消息列表
      let param = {
        pageSize:this.pageSize,
        currentPage:this.pageIndex
      };
      this.$httpPost(apiUrl.queryMessage, param).then((res) => {
        if(res.data&&res.data.status==="1000") {
          this.totalPage = res.data.pages||0;
          let list = res.data.list||[];
          if(list.length < this.pageSize) {
            this.newList = this.newList.concat(list);
            setTimeout(() => {
              this.$refs.scroller.disablePullup();
            }, 10);
          }else {
            this.newList = this.newList.concat(list);
            this.$refs.scroller.donePullup();
          }
        } else {
          showMsg(res.data.msg);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    onPullup:function() {
      //TODO 上拉加载数据
      if(this.pageIndex<this.totalPage) {
        this.pageIndex += 1;
        this.initNews();
      } else {
        this.$refs.scroller.disablePullup();
      }
    }
  },
  mounted () {
    this.$nextTick(() => {
      this.initNews();
    });
  }
};
</script>
<style lang="less">
.gyl-news{
  height:100%;
  width:100%;
  overflow: hidden;
  background: #F3F4F6;
  .content{
    box-sizing:border-box;
    width:100%;
    height:100%;
    overflow-y:auto;
    -webkit-overflow-scrolling: touch;
    padding-bottom:110px;
    .new-row{
      padding:16px 30px 32px;
      background-color: #fff;
      border-bottom: 1px solid #efefef;/*no*/
      &.no-bottom{
        border-bottom: 0;
      }
      .title{
        font-size: 26px;
        line-height: 75px;
        color: #333333;
      }
      .new-date{
        font-size: 26px;
        color: #999999;
        float: right;
      }
      .info{
        line-height: 36px;
        font-size: 26px;
        color: #666666;
      }
    }
    .xs-plugin-pullup-container{
      line-height: 60px;
      font-size: 28px;
      color: #6F7281;
      background: #F3F4F6;;
    }
  }
}
</style>
