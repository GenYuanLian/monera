<template>
  <div class="gyl-address-edit">
    <div v-title>{{headTitle}}</div>
    <Header :border="true" :title="headTitle" :left="headLeft" ></Header>
    <section class="content">
      <div class="addr-edit-row">
        <div class="row-key">联系人</div>
        <div class="row-val">
          <div class="row-val-top"><input type="text" v-model="contact" placeholder="请输入联系人姓名"></div>
          <div class="row-val-bot"><input class="ico-radio" :class="isMan ? 'checked' : ''" type="radio" id="1" @click="sexClick(1)"><label for="1">先生</label><input class="ico-radio" :class="isMan ? '' : 'checked'" type="radio" id="2" @click="sexClick(2)"><label for="2">女士</label></div>
        </div>
      </div>
      <div class="addr-edit-row">
        <div class="row-key">联系电话</div>
        <div class="row-val">
          <div class="row-val-top"><input type="text" v-model="phone" placeholder="请输入联系人电话"></div>
          <div class="row-val-bot"><input type="text" v-model="phone2" placeholder="请输入备选电话"></div>
        </div>
      </div>
      <div class="addr-edit-row">
        <div class="row-key">收货地址</div>
        <div class="row-val">
          <div class="row-val-top">
            <input type="text" v-model="cityName" @click="clickCity" readonly placeholder="请选择地址省市区/县">
          </div>
        </div>
      </div>
      <div class="addr-edit-row">
        <div class="row-key">街道信息</div>
        <div class="row-val">
          <div class="row-val-top">
            <input type="text" v-model="streetName" @click="clickStreet" readonly placeholder="请选择地址街道">
          </div>
        </div>
      </div>
      <div class="addr-edit-row">
        <div class="row-key">详细地址</div>
        <div class="row-val">
          <div class="row-val-top">
            <input type="text" v-model="addressDetail" placeholder="请填写详细地址">
          </div>
        </div>
      </div>
      <div class="addr-edit-row pd_b">
        <div class="row-key">标签</div>
        <div class="row-val">
          <div class="row-val-top">
            <input type="text" v-model="selectValue" @click="clickSign" readonly placeholder="请选择标签">
          </div>
        </div>
      </div>
      <div class="picker-city" v-show="showCity">
        <div class="picker-box">
          <p class="picker-btn"><span class="fl" @click="cancleClick">取消</span><span class="complete fr" @click="cityOkClick">确定</span></p>
          <Picker class="prcker-class" ref="picker" v-model="cityVal" :data="addressList" :columns="3" :column-width="[1/3, 1/3, 1/3]"></Picker >
        </div>
      </div>
      <div class="picker-city" v-show="showStreet">
        <div class="picker-box">
          <p class="picker-btn"><span class="fl" @click="streetCancel">取消</span><span class="complete fr" @click="streetOkClick">确定</span></p>
          <Picker ref="popPicker" class="prcker-class" v-model="streetVal" :data="streetList" ></Picker>
        </div>
      </div>
      <div class="foot">
        <input type="button" value="确定" @click="saveAddress">
      </div>
    </section>
    <Actionsheet v-model="showSign" :menus="signList" @on-click-menu="chooseSign" ></Actionsheet>
  </div>
</template>
<script>
import { md5, Group, Picker, Actionsheet, PopupPicker } from 'vux';
import { showMsg, valid } from '@/utils/common.js';
import apiUrl from '@/config/apiUrl.js';
import Header from '@/components/common/Header';
export default {
  data() {
    return {
      headTitle: "添加地址",
      headLeft: {
        label: "",
        className: "ico-back"
      },
      backUrl:"",
      isEdit:false, //是否是修改地址
      addressId:0, // 如果是修改地址，则有addressId
      contact:'', // 联系人
      isMan:true, // 男士/女士
      phone:'',
      phone2:'', // 备选电话
      addressDetail:'', //详细地址
      showCity:false, // 是否显示城市组件
      showStreet:false,
      cityVal:[], // 选择城市字符串
      cityName:[],
      cityNameStr:"",
      addressList:[], // 地址信息
      selectValue:'',
      showSign:false, // 选择标签
      signList: { // 标签信息
        menu1: "家",
        menu2: "公司",
        menu3: "学校"
      },
      parentCode:0,
      streetVal: [],
      streetName:[],
      streetNameStr:"",
      streetList: []
    };
  },
  components: {
    Header, Group, Picker, Actionsheet
  },
  methods: {
    clickCity:function() {
      // TODO 弹出城市组件
      if(this.addressList.length==0) {
        this.getCityData();
      } else {
        this.showCity = true;
      }
    },
    clickStreet:function() {
      //TODO 弹出街道选择
      if(this.cityName.length>0) {
        this.parentCode = this.cityVal[2];
        this.getCityList();
        this.showStreet = true;
      } else {
        showMsg("请先选择地址");
      }
    },
    clickSign:function() {
      // TODO 弹出选择标签组件
      this.showSign = true;
    },
    chooseSign:function(key, item) {
      // TODO 选择标签
      this.selectValue = item;
      this.showSign = false;
    },
    cancleClick:function() {
      // TODO 取消城市选择
      this.showCity = false;
    },
    cityOkClick:function() {
      // TODO 确定城市选择
      this.cityName = this.$refs.picker.getNameValues(this.cityVal);
      this.cityNameStr = this.cityName.replace(' ', '');
      this.showCity = false;
    },
    streetCancel:function() {
      //TODO 街道取消
      this.showStreet = false;
    },
    streetOkClick:function() {
      // TODO 确定街道选择
      this.streetName = this.$refs.popPicker.getNameValues(this.streetVal);
      this.streetNameStr = this.streetName;
      this.showStreet = false;
    },
    getCityList:function() {
      // TODO 根据城市code，分级获取
      this.streetList = [];
      let arr = [];
      let param = {
        parentCode: this.parentCode
      };
      this.$httpPost(apiUrl.getAreasByParentCode, param).then((res) => {
        if(res.status.code==0&&res.data) {
          let data = res.data.result;
          data.forEach((item, i) => {
            arr.push({name:item.name, value:item.value});
          });
          this.streetList.push(arr);
          this.showStreet = true;
        }else{
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
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
          this.contact = res.data.addr.receiver;
          this.phone = res.data.addr.mobile;
          this.phone2 = res.data.addr.tel;
          this.selectValue = res.data.addr.addressAlias;
          this.addressDetail = res.data.addr.address;
          this.isMan = res.data.addr.gender == 1 ? 1 : 0;
          if(res.data.addr.areaName&&res.data.addr.areaName.split("-").length==4) {
            this.cityName = res.data.addr.areaName.substr(0, res.data.addr.areaName.lastIndexOf("-")).replace(/-/g, ' ');
            this.streetName = res.data.addr.areaName.split("-")[3];
          }
          res.data.areas.forEach((item, i) => {
            if(i<3) {
              this.cityVal.push(item.areaCode+"");
            }else {
              this.streetVal.push(item.areaCode+"");
            }
          });
          console.log(this.cityVal);
        } else{
          showMsg(res.status.message);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    saveAddress:function() {
      // TODO 保存收货地址
      if(!this.contact) {
        showMsg('请填写联系人');
        return;
      }
      if(!valid.phone(this.phone)) {
        showMsg('请填写正确的联系电话');
        return;
      }
      if(!this.cityVal.length) {
        showMsg('请选择城市');
        return;
      }
      if(!this.addressDetail) {
        showMsg('请填写详细地址');
        return;
      }
      let param = {
        receiver: this.contact,
        gender: this.isMan ? 1 : 2,
        areaCode: this.streetVal[0], // 四级联动区域末级结点Id
        areaName: this.cityName+" "+this.streetName,
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
    overflow-y: scroll;
    -webkit-overflow-scrolling: touch;
    background-color: #F3F4F6;
    .addr-edit-row{
      padding: 30px 40px 0 40px;
      background-color: #fff;
      color: #555555;
      overflow: hidden;
      &.pd_b{
        padding-bottom: 20px;
      }
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
          &>input[type="text"], input[type="number"], .choose-address{
            width: 406px;
            height: 36px;
            font-size: 24px;
            line-height: 36px;
            padding: 15px 30px;
            border: 1px solid #efefef; /*no*/
            overflow: hidden;
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
    height: 80px;
    padding: 0 75px;
    box-sizing: border-box;
    margin-top: 80px;
    input{
      display: block;
      width: 100%;
      height: 80px;
      line-height: 80px;
      text-align: center;
      font-size: 30px;
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
    background-color: rgba(0,0,0,.5);
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
        background-color: #F3F4F6;
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
  .weui-mask{
    background-color: rgba(0, 0, 0, 0.5);
    .vux-actionsheet-menu-default {
      color: #333;
    }
    .weui-actionsheet__cell{
      padding: 20px 0;
    }
  }
}
</style>
