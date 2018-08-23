<template>
    <div class="content">
        <div>
          <pre>倒计时：</pre>
          <clocker time="2018-08-01" v-if="flag">
            <span style="color:red">%D 天</span>
            <span style="color:green">%H 小时</span>
            <span style="color:blue">%M 分 %S 秒</span>
          </clocker>
        </div>
        <x-address title="选择地址" v-model="city" :list="addressData" placeholder="请选择地址" :show.sync="showAddress" @on-hide="getCity"></x-address>
        <div class="upload" v-show="false">
          <Uploader class="btn-upload" ref="upload" :maximum="1" @input="uploadImage"></Uploader>
        </div>
        <div @click="changeValue(list3)">啦啦啦啦啦啦</div>
        <Picker style="height:200px;"  ref="picker2" v-model="value3" :data="list3" :columns="4" @on-change="changes"></Picker >

        <div id="box" class="box">
          <p id="el">此处为公告栏，啦啦啦啦啦啦啦啦啦,此处为公告栏，啦啦啦啦啦啦啦啦啦!!!</p>
        </div>
        <msgScroll ref="msgS" :msg="'此处为公告栏2，啦啦啦啦啦啦啦啦啦,此处为公告栏，啦啦啦啦啦啦啦啦啦!!!'"></msgScroll>
        <div @click="clickSign">wwwwwwwwwww</div>
        <Actionsheet v-model="showSign" :menus="signList" @on-click-menu="chooseSign" ></Actionsheet>
        <QrCode ref="qrCode" :text="'http://www.baidu.com'" :width="150"></QrCode>
        <div>合并图片</div>
        <canvas id="imgCanvas"></canvas>
        <img :src="shareImg" crossOrigin="anonymous" alt="">
        <br>
        <br>
        <br>
        <div>此处为倒计时：<timeOut :overTime="overTime" @overFnc="overFnc"></timeOut></div>
        <br>
        <div class="height:40px;">提醒弹窗：<input type="button" value="提醒" @click="remaind" /><reminder ref="remind" :remindType="remindType" :remindSth="remindSth" ></reminder></div>
        <br>
        <br>
        <div class="module-content">
          <div class="box">
            <div style="position: relative;">
              <input id="btn_camera" type="file" accept="image/*" capture="camera" @change="onTake()" value="点击扫描"/>
            </div>
          </div>
        </div>
    </div>
</template>

<script type="text/ECMAScript-6">
  import Util from '../../assets/js/app-global';
  import {getUrl} from '../../assets/js/analyticCode';
  // import llqrcode from '../../assets/js/llqrcode';
  import Uploader from "vue-upload-component";
  import { Clocker, XAddress, ChinaAddressV4Data, Picker, Actionsheet } from 'vux';
  import apiUrl from '@/config/apiUrl.js';
  import msgScroll from '@/components/messageScroll/index';
  import QrCode from '@/components/common/QrCode';
  import timeOut from '@/components/timeOut';
  import reminder from '@/components/reminder';
  export default {
    data() {
      return {
        flag: true,
        headPortrait: "",
        showAddress:false,
        city:[],
        addressData: ChinaAddressV4Data,
        value2:[],
        list2:[],
        value3:[],
        list3: [{
          name: '美国',
          value: '1',
          parent: 0,
          id:1
        }, {
          name: '意大利',
          value: '2',
          parent: 0,
          id:2
        }, {
          name: '英国',
          value: '3',
          parent: 0,
          id:3
        }, {
          name: '法国',
          value: '4',
          parent: 0,
          id:4
        }],
        showSign:false, // 选择标签
        signList: {
          menu1: "家",
          menu2: "公司",
          menu3: "学校"
        },
        shareImg:"",
        overTime:1500,
        remindType:1,
        remindSth: {
          phone:"1355261305",
          activeId:"1",
          dateArr:["2018-07-20 15:30", "2018-07-20 15:30"]
        }
      };
    },
    components: {
      Clocker,
      Uploader,
      XAddress,
      Picker,
      msgScroll,
      Actionsheet,
      QrCode,
      timeOut,
      reminder
    },
    methods: {
      uploadImage(formData) {
        // 上传图片
        let params = {
          formFile: formData[0].file
        };
        this.$httpPost(apiUrl.imgFileUpload, params).then((res) => {
          if(res.data&&res.data.status==="1000") {
            let data = res.data;
            this.headPortrait = data.fileUrls;
          } else {
            showMsg(res.status.message);
          }
        }).catch((err) => {
          console.log(err);
        });
      },
      chooseCity:function() {
        this.showAddress = true;
      },
      getCity:function(res) {
        console.log(res);
        console.log(this.city);
      },
      getName (value) {
        let address = value2name(value, ChinaAddressV4Data);
        let pro = address.split(' ')[0];
        let city = address.split(' ')[1];
        let range = address.split(' ')[2];
        return value2name(value, ChinaAddressV4Data);
      },
      queryCity:function(val) {
        // TODO 城市选择器
        console.log(val);
      },
      change (value) {
        console.log('new Value', value);
        if(value[0] == 'USA') {
          var list = [{
            name: '美国1号',
            value: '1',
            parent: '0'
          }, {
            name: '美国2号',
            value: '2',
            parent: '0'
          }];
          this.list2.push(list);
        }
      },
      changes:function(value) {
        console.log(this.value3);
        console.log('new Value', value);
        console.log(this.$refs.picker2.getNameValues(value));
      },
      changeValue (value) {
        this.list2.push(value);
        this.value2.push(value[0]);
      },
      // mesLunbo:function() {
      //   var _boxWidth = document.getElementById("box").clientWidth;
      //   var ele = document.getElementById("el");
      //   var _width = ele.clientWidth;
      //   var _left = ele.offsetLeft;
      //   var _timers = null;
      //   _timers = setInterval(function() {
      //     _left -= 1;
      //     if(ele.offsetLeft < -_width) {
      //       ele.style.left = _boxWidth + 'px';
      //       _left = _boxWidth;
      //     } else {
      //       ele.style.left = _left + 'px';
      //     }
      //   }, 50);
      // },
      clickSign:function() {
        // TODO 弹出选择标签组件
        this.showSign = true;
      },
      chooseSign:function(key, item) {
        // TODO 选择标签
        console.log(item);
        this.showSign = false;
      },
      mergeShareImg: function() {
        //TODO 合并分享图片
        var _this = this;
        var cvs = document.getElementById("imgCanvas");
        var ctx = cvs.getContext('2d');
        var img = new Image();
        img.src="http://shoptestimage.genyuanlian.com/images//merchant/home-banner-001.jpg";
        var imgQr = new Image();
        imgQr.src = "http://shoptestimage.genyuanlian.com/images//merchant/pickuplog.jpg";
        var imgNew = new Image();
        imgNew.setAttribute("crossOrigin", 'Anonymous');
        img.onload = function() {
          ctx.drawImage(this, 0, 0);
          ctx.drawImage(imgQr, 50, 50, 60, 60);
          var base64 = cvs.toDataURL("image/jpg");
        };
      },
      overFnc:function() {
        alert("倒计时结束！");
      },
      remaind:function() {
        this.$refs.remind.openReminder();
      },
      onTake() {
        var regex = /^http(s)?:\/\/([\w-]+\.)+[\w-]+(\/[\w- ./?%&=]*)?$/;
        var file = document.getElementById("btn_camera").files[0];
        getUrl(file, function(url1) {
          console.log(url1);
          if(!regex.test(url1)) {
            alert('识别错误，请重新拍照');
          } else {
            window.location.href = url1;
          }
        });
      }
    },
    mounted:function() {
      // this.mesLunbo();
      // this.$refs.msgS.scroll();
      // this.mergeShareImg();
    }
  };
</script>

<style lang="less" rel="stylesheet/less">
    .content{
      margin-top:10px;
      height:100%;
      overflow-y: auto;
      .button{
          width: 10px;
          height:10px;
          border-radius: 100%;
          background: red;
          margin-left:10px;
      }
      .upload{
        display:block;
        width:100px;
        height:100px;
        border:1px solid #ccc;
      }
      .box{
        width: 270px;
        height: 80px;
        margin: 100px;
        overflow: hidden;
        background-color: antiquewhite;
        position: relative;
        padding: 0 15px;
        p{
          float: left;
          min-width: 200px;
          max-width: 500px;
          height: 80px;
          line-height: 80px;
          position: absolute;
          left: 0;
        }
      }
      .weui-mask{
        background-color: rgba(0, 0, 0, 0.2)
      }
      .vux-actionsheet-menu-default {
        color: #333;
      }
      .weui-actionsheet__cell{
        padding: 20px 0;
      }
      .gyl-qrcode{
        height:auto !important;
      }
    }
</style>

