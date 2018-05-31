<template>
  <div class="gyl-address">
    <div v-title>收货地址</div>
    <Header :border="true" :title="headTitle" :left="headLeft" ></Header>
    <section class="content">
      <div class="addr-row" v-for="(addr,index) in addressList" :key="index" @click="defaultClick(addr.id)">
        <div class="addr-top">
          <p class="addr-person"><span>{{addr.receiver}}</span><span>{{addr.mobile}}</span></p>
          <p class="addr-address">{{addr.areaName.replace(/-/g,' ')}} {{addr.address}}</p>
        </div>
        <div class="addr-bot">
          <span class="addr-check"><i class="ico-radio" :class="addr.check ? 'checked' : ''"></i></span>
          <span class="addr-edit" @click.stop="editClick(addr.id)"><i class="ico-edit"></i>编辑</span>
          <span class="addr-delete" @click.stop="deleteClick(addr.id)"><i class="ico-delete"></i>删除</span>
        </div>
      </div>
      <div class="no-card" v-if="addressList.length==0">还没有地址哦，快去添加吧~</div>
    </section>
    <footer class="foot">
      <input type="button" value="添加新地址" @click="jumpAdressEdit">
    </footer>
    <Popup ref="pop" :popObj="popObj"></Popup>
  </div>
</template>
<script>
import { mapActions, mapGetters } from "vuex";
import { showMsg, valid } from '@/utils/common.js';
import apiUrl from '@/config/apiUrl.js';
import Header from '@/components/common/Header';
import Popup from '@/components/popup';
import { md5 } from 'vux';
export default {
  data() {
    return {
      headTitle: "收货地址",
      headLeft: {
        label: "",
        className: "ico-back"
      },
      backUrl:'', //选择默认地址后跳回的页面
      addressList:[], // 地址列表
      addressId:'', // 要删除的地址id
      popObj:{
        popTitle:'提示',
        popText:'确定删除该收货地址？',
        popIndex:2,
        popSure:this.popDelete,
        popFnFalse:this.popCancle
      }
    };
  },
  components: {
    Header, Popup
  },
  computed:{
    ...mapGetters(["getDeliveryAddressBackUrl"])
  },
  methods: {
    ...mapActions({
      saveDeliveryAddress: "saveDeliveryAddress"
    }),
    getAddressList: function() {
      //TODO 查询地址
      this.$httpPost(apiUrl.getMemberAddressList, {}).then((res) => {
        if(res.status.code==0&&res.data) {
          this.addressList = res.data.result;
          this.addressList.forEach((item) => {
            this.$set(item, 'check', false);
          });
        }else{
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    defaultClick:function(id) {
      // TODO 设置默认地址
      this.addressList.forEach((ele) => {
        if(ele.id == id) {
          ele.check = true;
          window.localStorage.setItem('address', JSON.stringify(ele));
          this.saveDeliveryAddress(ele);
          this.backUrl = this.getDeliveryAddressBackUrl;
          if(this.backUrl) {
            this.$router.replace(this.backUrl);
          }
        } else {
          ele.check = false;
        }
      });
    },
    deleteClick:function(id) {
      // TODO 删除地址
      this.addressId = id;
      this.$refs.pop.openWin();
    },
    editClick:function(id) {
      // TODO 编辑地址
      this.$router.push({name:'address_edit', query:{from:'edit', id:id}});
    },
    popDelete:function() {
      // TODO 确定删除地址
      let param = {
        addressId: this.addressId
      };
      this.$httpPost(apiUrl.deleteMemberAddress, param).then((res) => {
        if(res.status.code==0&&res.data) {
          showMsg(res.status.message);
          this.getAddressList();
        }else{
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    popCancle:function() {
      // TODO 取消删除地址
      this.$refs.pop.closeWin();
    },
    jumpAdressEdit:function() {
      // TODO 跳转增加地址页面
      this.$router.push({name:'address_edit'});
    }
  },
  mounted() {
    this.getAddressList();
  }
};
</script>
<style lang="less">
html,body{
  height: 100%;
  overflow: hidden;
}

.gyl-address{
  height: 100%;
  overflow: hidden;
  .content{
    box-sizing:border-box;
    width:100%;
    height:calc(~"100% - 186px");
    overflow-x: hidden;
    overflow-y: auto;
    -webkit-overflow-scrolling: touch;
    background-color: #F3F4F6;
    .addr-row{
      margin-bottom: 20px;
      padding-left: 30px;
      background-color: #fff;
      .addr-top{
        padding: 24px 30px 24px 0;
        border-bottom: 1px solid #efefef; /*no*/
        p{
          font-size: 30px;
          color: #555;
        }
        .addr-person{
          line-height: 60px;
        }
        .addr-address{
          line-height: 32px;
        }
      }
      .addr-bot{
        padding: 23px 30px 42px 0;
        overflow: hidden;
        span{
          display: block;
          float: right;
          height: 40px;
          font-size: 24px;
          line-height: 40px;
          color: #555555;
          i{
            width: 40px;
            height: 40px;
            vertical-align: middle;
          }
        }
        .addr-check{
          float: left;
          width: 40px;
          height: 40px;
        }
        .addr-delete, .addr-edit{
          width: 102px;
          margin-left: 40px;
        }
        .ico-delete, .ico-edit{
          width: 30px;
          height: 40px;
          background-size: 100% auto;
          background-position: center;
          margin-top: -4px;
          margin-right: 12px;
        }
      }
    }
    .no-card{
      height: 80px;
      line-height: 80px;
      font-size: 20px;
      color: #cecece;
      text-align: center;
      letter-spacing: 2px;
    }
  }
  .foot{
    width: 100%;
    height: 98px;
    position: absolute;
    bottom: 0;
    input{
      display: block;
      width: 100%;
      height: 98px;
      line-height: 98px;
      text-align: center;
      font-size: 30px;
      color: #fff;
      background-color: #317db9;
      border-radius: 0;
    }
  }
}
</style>
