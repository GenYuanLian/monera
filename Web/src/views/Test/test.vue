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
    </div>
</template>

<script type="text/ECMAScript-6">
  import Util from '../../assets/js/app-global';
  import Uploader from "vue-upload-component";
  import { Clocker, XAddress, ChinaAddressV4Data, Picker } from 'vux';
  import apiUrl from '@/config/apiUrl.js';
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
        }]
      };
    },
    components: {
      Clocker,
      Uploader,
      XAddress,
      Picker
    },
    mounted:function() {
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
      }
    }
  };
</script>

<style lang="less" rel="stylesheet/less">
    .content{
        margin-top:10px;
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
    }
</style>

