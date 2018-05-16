<template>
  <div class="gyl-address-edit">
    <div v-title>{{headTitle}}</div>
    <Header :border="true" :title="headTitle" :left="headLift" ></Header>
    <section class="content">
      <div class="addr-edit-row">
        <div class="row-key">联系人</div>
        <div class="row-val">
          <div class="row-val-top"><input type="text" v-model="connect"></div>
          <div class="row-val-bot"><input class="ico-radio" :class="isMan ? 'checked' : ''" type="radio" id="1" @click="sexClick(1)"><label for="1">先生</label><input class="ico-radio" :class="isMan ? '' : 'checked'" type="radio" id="2" @click="sexClick(2)"><label for="2">女士</label></div>
        </div>
      </div>
      <div class="addr-edit-row">
        <div class="row-key">联系电话</div>
        <div class="row-val">
          <div class="row-val-top"><input type="text" v-model="phone"></div>
          <div class="row-val-bot"><input type="text" v-model="phone2" placeholder="备选电话"></div>
        </div>
      </div>
      <div class="addr-edit-row">
        <div class="row-key">收货地址</div>
        <div class="row-val">
          <div class="row-val-top">
            <input type="text" v-model="cityString" @click="clickCity" placeholder="请选择地址">
          </div>
          <div class="row-val-bot"><input type="text" v-model="addressDetail"></div>
        </div>
      </div>
      <div class="addr-edit-row">
        <div class="row-key">标签</div>
        <div class="row-val">
          <div class="row-val-top">
            <group class="choose-table">
              <selector title="" v-model="selectValue" :options="options"></selector>
            </group>
          </div>
        </div>
      </div>
      <div class="picker-city" v-show="showCity">
        <div class="picker-box">
          <p class="picker-btn"><span class="fl" @click="cancleClick">取消</span><span class="complete fr" @click="sureClick">确定</span></p>
          <Picker class="prcker-class" ref="picker" v-model="city" :data="addressList" :columns="4" :column-width="[1/4, 1/4, 1/4]"></Picker >
        </div>
      </div>
      <div class="foot">
        <input type="button" value="确定" @click="saveAddress">
      </div>
    </section>
  </div>
</template>
<script>
import { md5, Group, Selector, Picker } from 'vux';
import { showMsg, valid } from '@/utils/common.js';
import apiUrl from '@/config/apiUrl.js';
import Header from '@/components/common/Header';
export default {
  data() {
    return {
      headTitle: "添加地址",
      headLift: {
        label: "",
        className: "ico-back"
      },
      isEdit:false, //是否是修改地址
      addressId:0, // 如果是修改地址，则有addressId
      connect:'', // 联系人
      isMan:true, // 男士/女士
      phone:'',
      phone2:'', // 备选电话
      addressDetail:'', //详细地址
      showCity:false, // 是否显示城市组件
      cityString:'', // 选择城市字符串
      city:[],  // 选择的城市
      addressList:[], // 地址信息
      showAddress:false,
      selectValue:'家',
      options:["家", "公司", "学校"]
    };
  },
  components: {
    Header, Group, Selector, Picker
  },
  methods: {
    clickCity:function() {
      // TODO 弹出城市组件
      console.log(this.city);
      if(this.city.length==0) {
        this.getCityData();
      } else {
        this.showCity = true;
      }
    },
    cancleClick:function() {
      // TODO 取消城市选择
      this.showAddrshowCityess = false;
    },
    sureClick:function() {
      // TODO 确定城市选择
      this.cityString = this.$refs.picker.getNameValues(this.city);
      this.showCity = false;
    },
    getCityData:function() {
      // TODO 获取城市列表
      this.$httpPost(apiUrl.getAreas, {}).then((res) => {
        if(res.status.code==0&&res.data) {
          this.addressList = res.data.list;
          this.showCity = true;
        }else{
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    sexClick:function(sex) {
      // TODO 性别选择
      if(sex==2) {
        this.isMan = false;
      } else {
        this.isMan = true;
      }
    },
    getAddressInfo:function() {
      // TODO 修改信息时，获取信息
      let param = {
        addressId:this.addressId
      };
      this.$httpPost(apiUrl.getMemberAddress, param).then((res) => {
        if(res.status.code==0&&res.data) {
          this.connect = res.data.addr.receiver;
          this.phone = res.data.addr.mobile;
          this.phone2 = res.data.addr.tel;
          this.selectValue = res.data.addr.addressAlias;
          this.addressDetail = res.data.addr.address;
          this.cityString = res.data.addr.areaName.replace(/-/g, '');
          res.data.areas.forEach(element => {
            this.city.push(element.areaCode);
          });
          if(res.data.addr.gender == 1) {
            this.isMan = true;
          } else {
            this.isMan = false;
          }
        } else{
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    saveAddress:function() {
      // TODO 保存收货地址
      if(!this.connect) {
        showMsg('请填写联系人');
        return;
      }
      if(!valid.phone(this.phone)) {
        showMsg('请填写正确的联系电话');
        return;
      }
      if(!this.city.length) {
        showMsg('请选择城市');
        return;
      }
      if(!this.addressDetail) {
        showMsg('请填写详细地址');
        return;
      }

      let param = {
        receiver: this.connect,
        gender: this.isMan ? 1 : 2,
        areaCode: this.city[3], // 四级联动区域末级结点Id
        areaName: this.cityString,
        address: this.addressDetail,
        mobile: this.phone,
        tel: this.phone2,
        email:'',
        addressAlias: this.selectValue,
        remark: ''
      };
      if(this.isEdit) {
        param.id = this.addressId;
      };
      this.$httpPost(apiUrl.saveMemberAddress, param).then((res) => {
        if(res.status.code==0&&res.data) {
          let _this = this;
          showMsg(res.status.message, function() {
            _this.$router.replace('address');
          });
        }else{
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    }
  },
  mounted() {
      // TODO 判断是增加地址还是修改地址
    if(this.$route.query.from == 'edit') {
      this.headTitle = "修改地址";
      this.isEdit = true;
      this.addressId = this.$route.query.id;
      this.getAddressInfo();
    }
  }
};
</script>
<style lang="less">
html,body{
  height: 100%;
  overflow: hidden;
}

.gyl-address-edit{
  height: 100%;
  overflow: hidden;
  .content{
    box-sizing:border-box;
    width:100%;
    height:calc(~"100% - 88px");
    overflow-x: hidden;
    overflow-y: auto;
    -webkit-overflow-scrolling: touch;
    background-color: #efefef;
    .addr-edit-row{
      padding: 30px 40px 0 40px;
      background-color: #fff;
      color: #555555;
      overflow: hidden;
      .row-key{
        width: 140px;
        height: 90px;
        font-size: 28px;
        line-height: 90px;
        text-align: right;
        float: left;
        padding-right: 30px;
        letter-spacing: 1px; /*no*/
      }
      .row-val{
        float: left;
        width: calc(~"100% - 170px");
        height: auto;
        .row-val-top, .row-val-bot{
          padding: 10px 0;
          font-size: 24px;
          overflow: hidden;
          .weui-cell{
            padding: 0;
          }
          .weui-cells{
            margin: 0;
          }
          .weui-cells:before{
            border: 0;
          }
          .weui-cells:after{
            border: 0;
          }
          .vux-cell-value{
            color: #555;
          }
          &>input[type="text"], input[type="number"], .choose-address, .choose-table{
            width: 406px;
            height: 36px;
            font-size: 24px;
            line-height: 36px;
            padding: 15px 30px;
            border: 1px solid #efefef; /*no*/
            overflow: hidden;
          }
          .choose-table{
            padding: 0;
            height: 66px;
            margin-bottom: 30px;
          }
          &>input[type="radio"]{
            display: block;
            float: left;
            width: 40px;
            height: 40px;
            border-radius: 50%;
            margin-right: 20px;
          }
          &>label{
            display: block;
            float: left;
            margin-right: 60px;
            line-height: 40px;
          }
          select{
            font-size: 24px;
            line-height: 66px;
            height: 66px;
            color: #555;
          }
          option{
            text-align: center;
          }
        }
      }
    }
  }
  .foot{
    width: 100%;
    height: 98px;
    padding: 0 75px;
    box-sizing: border-box;
    margin-top: 60px;
    input{
      display: block;
      width: 100%;
      height: 98px;
      line-height: 98px;
      text-align: center;
      font-size: 28px;
      color: #fff;
      background-color: #317db9;
      border-radius: 10px;
    }
  }
  .picker-city{
    width: 100%;
    height: 100%;
    // height: 500px;
    position: absolute;
    bottom: 0;
    background-color: rgba(0,0,0,.2);
    z-index: 9;
    .picker-box{
      width: 100%;
    // height: 500px;
      background: #fff;
      position: absolute;
      bottom: 0;
      .picker-btn{
        width: 100%;
        height: 80px;
        overflow: hidden;
        background-color: #efefef;
        span{
          display: block;
          width: 120px;
          height: 80px;
          font-size: 28px;
          line-height: 80px;
          text-align: center;
          color: #999;
          &.complete{
            color: #317db9;
          }
        }
      }
      .prcker-class{
        width: 100%;
      // height: 500px;
        background: #fff;
        position: relative;
        bottom: 0;
      }
    }
  }
}
</style>
