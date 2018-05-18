import axios from 'axios';
import qs from 'qs';
import {base64, md5} from 'vux';
import Promise from 'es6-promise';
import store from '../store';
import * as types from '../store/mutation_types';
import { loading, showMsg, versions, objKeySort } from '@/utils/common.js';
import {getStore, setStore, removeStore} from '@/utils/storage.js';
Promise.polyfill();

let env = require('../../config/dev.env');
if (process.env.NODE_ENV === 'development') {
  env = require('../../config/dev.env');
} else if (process.env.NODE_ENV === 'testing') {
  env = require('../../config/test.env');
} else if (process.env.NODE_ENV === 'product') {
  env = require('../../config/prod.env');
} else {
  env.NODE_ENV = "production";
  env.API_SERVER = "http://shopapi.genyuanlian.com";//http://58.87.112.65:8081 http://shopapi.genyuanlian.com
}

// axios 配置
axios.defaults.timeout = 300000;
axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8';
axios.defaults.baseURL = env.API_SERVER;

// POST传参序列化
axios.interceptors.request.use((config) => {
  let oldParams = config.data;
  let device = {type: '', deviceModel: '', deviceName: ''};
  if(config.data&&config.data["formFile"]) {
    let param = new FormData();
    param.append('file', config.data["formFile"]);
    config.data = param;
    return config;
  }
  if(config.data&&config.data["file"]) {
    return config;
  }
  if (versions.gyl_android) {
    device.channel = "Android";
    device.channelVersion = "1.0.0";
    device.deviceModel = 'Android';
    device.deviceName = 'Android';
  } else if (versions.gyl_ios) {
    device.channel = "IOS";
    device.channelVersion = "1.0.0";
    device.deviceModel = 'IOS';
    device.deviceName = 'IOS';
  }else{
    device.channel = "H5";
    device.channelVersion = "1.0.0";
    device.deviceModel = 'H5';
    device.deviceName = 'Browser';
  }
  let user = {
    token:store.state.token,
    memberId:store.state.memberId
  };
  config.data = Object.assign({}, device, user, config.data);
  config.data = objKeySort(config.data);
  config.data.sign = getSign(config.data);
  config.data = Object.assign({}, config.data);
  if (config.method === 'post') {
    config.data = qs.stringify(config.data);
  }
  return config;
}, (error) => {
  showMsg('请求参数错误');
  return Promise.reject(error);
});

// 返回状态判断
axios.interceptors.response.use((res) => {
  // let baseData = res.data.data?JSON.parse(base64.decode(res.data.data)):"";
  // res.data.data = baseData;
  if (res.data.status.code === 0 ||(res.data.status.code > 0&&res.data.status.code !== 10003)) {
    return res;
  }
  if (res.data.status.code === 10003) {
    //登录过期
    store.commit(types.LOGOUT);
  }
  showMsg(res.data.status.message);
  return Promise.reject(res);
}, (error) => {
  // 提示
  showMsg("返回错误："+error.message);
  return Promise.reject(error);
});

function parseParam(param, key) {
  let paramStr="";
  if(typeof (param) == "string" || typeof (param) == "number" || typeof (param) == "boolean") {
    paramStr+="&" + key + "=" + param;
  } else {
    for(let i in param) {
      var k=key==null?i:key+(param instanceof Array ? "["+i+"]" : "."+i);
      paramStr += '&'+parseParam(param[i], k);
    }
  }
  return paramStr.substr(1);
};

function getSign(param, key) {
  let url = parseParam(param);
  let urlStr = url.split("&").sort().join("&");
  let urlParam = urlStr.split("&");
  let theRequest = [];
  let newStr = "";
  for(let i = 0; i < urlParam.length; i++) {
    newStr += urlParam[i].split("=")[1];
  }
  return md5(newStr);
}

function checkStatus (response) {
  // loading
  // 如果http状态码正常，则直接返回数据
  if (response && (response.status === 200 || response.status === 304 || response.status === 400)) {
    return response;
    // 如果不需要除了data之外的数据，可以直接 return response.data
  }
  // 异常状态下，把错误信息返回去
  return {
    status: -404,
    msg: '网络异常'
  };
}

function checkCode (res) {
  // 如果code异常(这里已经包括网络错误，服务器错误，后端抛出的错误)，可以弹出一个错误提示，告诉用户
  if (res.status === -404) {
    console.log(res.msg);
  }
  if (res.data && (!res.data.success)) {
    console.log(res.data.error_msg);
  }
  return res;
}

export default {
  get(url, params, options) {
    var option = {
      isLoading: true
    };
    options = Object.assign(option, options);
    return new Promise((resolve, reject) => {
      if(options.isLoading) loading(true);
      axios.get(url, params)
        .then(response => {
          if(options.isLoading) loading(false);
          resolve(response.data);
        }, (err) => {
          if(options.isLoading) loading(false);
          reject(err);
        })
        .catch((error) => {
          if(options.isLoading) loading(false);
          reject(error);
        });
    });
  },
  post(url, params, options) {
    var option = {
      isLoading: true
    };
    options = Object.assign(option, options);
    return new Promise((resolve, reject) => {
      if(options.isLoading) loading(true);
      axios.post(url, params)
        .then(response => {
          if(options.isLoading) loading(false);
          resolve(response.data);
        }, (err) => {
          if(options.isLoading) loading(false);
          reject(err);
        })
        .catch((error) => {
          if(options.isLoading) loading(false);
          reject(error);
        });
    });
  },
  install: function (Vue) {
    Object.defineProperty(Vue.prototype, '$httpGet', {
      value: this.get
    });
    Object.defineProperty(Vue.prototype, '$httpPost', {
      value: this.post
    });
    Object.defineProperty(Vue.prototype, '$axios', {
      value: axios
    });
  }
};