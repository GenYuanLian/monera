<template>
  <div class="gyl-test">
  <x-header>with right link<a slot="right">...</a></x-header>
    <h1>测试页面</h1>
    <div>
      <file-upload class="upload"
        ref="upload"
        @input="updatetValue"
      >添加文件</file-upload>
    </div>
    <form action="http://10.68.79.42:8092/api/base/imgFileUpload" method="POST" enctype="multipart/form-data">
      <input type="file" name="file" id="file">
      <input type="submit" value="上传" />
    </form>
    <div style="padding:20px;">
      <input type="text" v-model="buyTime"><input type="button" value="获取收益时间" @click="incomeClick">
      <p v-text="getTime"></p>
    </div>
    <div @click="chooseCity">请输入地址{{getName(city)}}</div>
    <x-address style="display:none;" title="选择地址" v-model="city" :list="addressData" placeholder="请选择地址" :show.sync="showAddress" @on-hide="getCity"></x-address>
    
     <div>
      <input type="text" placeholder="请输入身份证号" v-model="idCard">
      <input type="button" value="校验" @click="checkIdCard">
    </div>
    <!-- 预览图片  详情：https://doc.vux.li/zh-CN/components/previewer.html -->
     <div>
      <img class="previewer-demo-img" v-for="(item, index) in previewerList" :key="index" :src="item.src" width="100" @click="showBig(index)">
      <div><!-- v-transfer-dom -->
        <previewer :list="previewerList" ref="previewer" :options="options" @on-index-change="logIndexChange"></previewer>
      </div>
    </div>
    <!-- <swiper :list="demo01_list" v-model="demo01_index" @on-index-change="demo01_onIndexChange" :loop="true" :auto="true"></swiper> -->
    <swiper :list="demo02_list" :loop="true" :auto="true" :show-dots="false"></swiper>
    <!-- 
      swiper 相关参数设置
      名字	                 类型	     默认值	          说明
      list	                  array		  轮播图片列表，如果有自定义样式需求，请使用 swiper-item(使用 swiper-item 时仅有2个的情况下不支持循环)	--
      direction	            string	  horizontal	  方向	--
      show-dots	            boolean	  true	        是否显示提示点	--
      show-desc-mask	      boolean	  true	        是否显示描述半透明遮罩	--
      dots-position	        string	  right	        提示点位置	--
      dots-class	          string		              提示className	--
      auto	                boolean	  false	        是否自动轮播	--
      loop	                boolean	  false	        是否循环	--
      interval	            number	  3000	        轮播停留时长	--
      threshold	            number	  50	          当滑动超过这个距离时才滑动	--
      duration	            number	  300	          切换动画时间	--
      height	              string	  180px	        高度值。如果为100%宽度并且知道宽高比，可以设置aspect-ratio自动计算高度	--
      aspect-ratio	        number		用以根据当前可用宽度计算高度值	--
      min-moving-distance	  number	  0	            超过这个距离时才滑动	--
      v-model	              number	  0	            index 绑定，使用v-model，一般不需要绑定
     -->
    <div style="with:100%;height:30px;overflow:hidden;position:relative;">
      <div style="height:30px;line-height:30px;position:absolute;left:0">公告：啦啦啦啦啦啦啦啦啦，我是买包的小行家，风吹雨打都不怕，哈哈哈</div>
    </div>
  </div>
</template>
<script>
import FileUpload from 'vue-upload-component';
import {XHeader, XAddress, ChinaAddressV4Data, Value2nameFilter as value2name, Swiper, Previewer} from 'vux';
import { showMsg, loading, readFile, valid, getQueryString } from "@/utils/common.js";
import apiUrl from "@/config/apiUrl.js";
import axios from "axios";
import { setInterval } from 'timers';
export default {
  data() {
    return {
      buyTime:'2018-01-05 10:59:28',
      getTime:'',
      city:[],
      showAddress:false,
      addressData: ChinaAddressV4Data,
      idCard: "",
      showImg:false,
      imgSrc:'',
      // demo02_list:[{title:'公告：啦啦啦啦啦啦啦啦啦，我是买包的小行家，风吹雨打都不怕，哈哈哈'}],
      demo01_index: 0,
      demo01_list:[{
        url: 'javascript:',
        img: 'https://ww1.sinaimg.cn/large/663d3650gy1fq66vvsr72j20p00gogo2.jpg',
        title: '送你一朵fua'
      }, {
        url: 'javascript:',
        img: 'https://ww1.sinaimg.cn/large/663d3650gy1fq66vw1k2wj20p00goq7n.jpg',
        title: '送你一辆车'
      }, {
        url: 'javascript:',
        img: 'https://static.vux.li/demo/5.jpg', // 404
        title: '送你一次旅行',
        fallbackImg: 'https://ww1.sinaimg.cn/large/663d3650gy1fq66vw50iwj20ff0aaaci.jpg'
      }],
      previewerList: [{
        msrc: 'http://ww1.sinaimg.cn/thumbnail/663d3650gy1fplwu9ze86j20m80b40t2.jpg',
        src: 'http://ww1.sinaimg.cn/large/663d3650gy1fplwu9ze86j20m80b40t2.jpg',
        w: 800,
        h: 400
      },
      {
        msrc: 'http://ww1.sinaimg.cn/thumbnail/663d3650gy1fplwvqwuoaj20xc0p0t9s.jpg',
        src: 'http://ww1.sinaimg.cn/large/663d3650gy1fplwvqwuoaj20xc0p0t9s.jpg',
        w: 1200,
        h: 900
      }, {
        msrc: 'http://ww1.sinaimg.cn/thumbnail/663d3650gy1fplwwcynw2j20p00b4js9.jpg',
        src: 'http://ww1.sinaimg.cn/large/663d3650gy1fplwwcynw2j20p00b4js9.jpg'
      }],
      options: {
        getThumbBoundsFn (index) {
          // find thumbnail element
          let thumbnail = document.querySelectorAll('.previewer-demo-img')[index];
          // get window scroll Y
          let pageYScroll = window.pageYOffset || document.documentElement.scrollTop;
          // optionally get horizontal scroll
          // get position of element relative to viewport
          let rect = thumbnail.getBoundingClientRect();
          // w = width
          return {x: rect.left, y: rect.top + pageYScroll, w: rect.width};
          // Good guide on how to get element coordinates:
          // http://javascript.info/tutorial/coordinates
        }
      }
    };
  },
  components: {XHeader, FileUpload, XAddress, Swiper, Previewer},
  methods: {
    uploadImage: function(formData) {
      readFile(formData.get("img"), (res) => {
        let params = {
          imgString: res
        };
        this.$httpPost(apiUrl.uploadImg, params).then((res) => {
          if(res.data&&res.data.status==="1000") {
            let data = res.data;
            console.log(data);
          } else {
            showMsg(res.data.msg);
          }
        }).catch((err) => {
          console.log(err);
        });
      });
    },
    updatetValue(value) {
      let param = new FormData();
      param.append('file', value[0].file);
      axios.post(apiUrl.imgFileUpload, param).then((res) => {
        if(res.data&&res.data.status==="1000") {
          let data = res.data;
          this.frontImg = data.fileUrl;
          this.frontImgUrl = data.fileUrls;
        } else {
          showMsg(res.data.msg);
        }
      }).catch((err) => {
        console.log(err);
      });
    },
    inputFile: function (newFile, oldFile) {
      if (newFile && oldFile && !newFile.active && oldFile.active) {
        console.log('response', newFile.response);
        if (newFile.xhr) {
          console.log('status', newFile.xhr.status);
        }
      }
    },
    incomeClick:function() {
      // 计算收益时间
      let today = this.buyTime;
      // this.getTime = Holiday.getHoliday(today);
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
    checkIdCard: function() {
      if(!valid.idCodeValid(this.idCard)) {
        console.log("身份证不正确");
      }
    },
    demo01_onIndexChange:function(index) {
      this.demo01_index = index;
    },
    logIndexChange:function(arg) {
      console.log(arg);
    },
    showBig:function(index) {
      this.$refs.previewer.show(index);
    }
  },
  mounted () {
    console.log(getQueryString()["name"]);
    // setInterval(function(){

    // },5000)
  }
};
</script>
<style lang="less">
.gyl-test{
  div{
    text-align: left;
  }
  .upload{
    width:200px;
    height:200px;
    text-align: left;
    border:1px solid #ccc;/*no*/
  }
  .file-uploads {
    overflow: hidden;
    position: relative;
    text-align: center;
    display: inline-block;
  }
  .file-uploads.file-uploads-html4 input[type="file"] {
    opacity: 0;
    font-size: 20em;
    z-index: 1;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    position: absolute;
    width: 100%;
    height: 100%;
  }
  .file-uploads.file-uploads-html5 input[type="file"] {
    overflow: hidden;
    position: fixed;
    width: 1px;
    height: 1px;
    z-index: -1;
    opacity: 0;
  }
}
</style>
