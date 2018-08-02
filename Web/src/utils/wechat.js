import wx from 'weixin-js-sdk';
import apiUrl from "@/config/apiUrl.js";
import { showMsg } from '@/utils/common.js';
export default {
  wxChat : ($vue, param) => {
    let appId = "";
    let timestamp="";
    let nonceStr = "";
    let signature = "";
    let options = {
      title: "",
      desc: "",
      link: "",
      imgUrl: "",
      type: "link",
      dataUrl: "",
      localUrl: ""
    };
    options = Object.assign({}, options, param);
    options.localUrl = options.localUrl.split('#')[0];
    $vue.$httpPost(apiUrl.shareConfig, {url: options.localUrl}, {isLoading:false}).then((res) => {
      if(res.status.code==0&&res.data) {
        wx.config({
          debug: false,
          appId: res.data.appId,
          timestamp: res.data.timestamp, // 必填，生成签名的时间戳
          nonceStr: res.data.nonceStr, // 必填，生成签名的随机串
          signature: res.data.signature, // 必填，签名
          jsApiList: [
            'onMenuShareAppMessage', 'onMenuShareTimeline',
            'onMenuShareQQ', 'onMenuShareQZone', 'scanQRCode'
          ]
        });
        wx.error(function (res) {
          console.log('验证失败返回的信息:', res);
        });
        wx.ready(function () {
          //分享到朋友圈
          wx.onMenuShareTimeline({
            title: options.title, // 分享标题
            link: options.link, // 分享链接
            imgUrl: options.imgUrl, // 分享图标
            success: function (res) {
              // 用户确认分享后执行的回调函数
              console.log("分享到朋友圈成功返回的信息为:", res);
              showMsg("分享成功!");
            },
            cancel: function (res) {
              // 用户取消分享后执行的回调函数
              console.log("取消分享到朋友圈返回的信息为:", res);
            }
          });
          //分享给朋友
          wx.onMenuShareAppMessage({
            title: options.title,
            desc: options.desc,
            link: options.link,
            imgUrl: options.imgUrl,
            type: options.type, // 分享类型,music、video或link，不填默认为link
            dataUrl: options.dataUrl, // 如果type是music或video，则要提供数据链接，默认为空
            success: function (res) {
              // 用户确认分享后执行的回调函数
              console.log("分享给朋友成功返回的信息为:", res);
              showMsg("分享成功!");
            },
            cancel: function (res) {
              // 用户取消分享后执行的回调函数
              console.log("取消分享给朋友返回的信息为:", res);
            }
          });
          //分享到QQ
          wx.onMenuShareQQ({
            title: options.title,
            desc: options.desc,
            link: options.link,
            imgUrl: options.imgUrl,
            success: function (res) {
              // 用户确认分享后执行的回调函数
              console.log("分享到QQ好友成功返回的信息为:", res);
              showMsg("分享成功!");
            },
            cancel: function (res) {
              // 用户取消分享后执行的回调函数
              console.log("取消分享给QQ好友返回的信息为:", res);
            }
          });
          //分享到QQ空间
          wx.onMenuShareQZone({
            title: options.title,
            desc: options.desc,
            link: options.link,
            imgUrl: options.imgUrl,
            success: function (res) {
              // 用户确认分享后执行的回调函数
              console.log("分享到QQ空间成功返回的信息为:", res);
              showMsg("分享成功!");
            },
            cancel: function (res) {
              // 用户取消分享后执行的回调函数
              console.log("取消分享到QQ空间返回的信息为:", res);
            }
          });
        });
      } else {
        showMsg(res.data.msg);
      }
    }).catch((err) => {
      console.log(err);
    });
  },
  wxScanQr: function(callFun) {
    wx.scanQRCode({
      needResult: 1, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
      scanType: ["qrCode"], // 可以指定扫二维码还是一维码，默认二者都有
      success: function (res) {
        var result = res.resultStr;
        console.log("扫描结果："+result);
        if(callFun) callFun(result);
      }
    });
  },
  wxJsPay: function($vue, param = {}, success, fail) {
    //TODO 获取微信config值
    $vue.$httpPost(apiUrl.weixinpayH5, param).then((res) => {
      if(res.status.code==0&&res.data) {
        let config = res.data.weixinPayData;
        wx.config({
          debug: false,
          appId: config.appId, // 和获取Ticke的必须一样------必填，公众号的唯一标识
          timestamp: config.timestamp, // 必填，生成签名的时间戳
          nonceStr: config.nonceStr, // 必填，生成签名的随机串
          signature: config.paySign, // 必填，签名
          jsApiList: [ 'chooseWXPay' ]
        });
        //处理验证失败的信息
        wx.error(function (res) {
          console.log('验证失败返回的信息:', res);
        });
        //处理验证成功的信息
        wx.ready(function () {
          wx.chooseWXPay({
            timestamp: config.timestamp,
            nonceStr: config.nonceStr,
            package: config.packageStr,
            signType: config.signType,
            paySign: config.paySign,
            success: function (res) {
              // 支付成功后的回调函数
              if(success) success();
            }
          });
        });
      }else {
        if(fail) fail(res);
      }
    }).catch((err) => {
      console.log(err);
    });
  },
  wxChatPay: function(param={}, success, fail) {
    if (typeof WeixinJSBridge == "undefined") {
      if( document.addEventListener ) {
        document.addEventListener('WeixinJSBridgeReady', onBridgeReady(param, success, fail), false);
      }else if (document.attachEvent) {
        document.attachEvent('WeixinJSBridgeReady', onBridgeReady(param, success, fail));
        document.attachEvent('onWeixinJSBridgeReady', onBridgeReady(param, success, fail));
      }
    }else{
      onBridgeReady(param, success, fail);
    }
    function onBridgeReady(param, success, fail) {
      WeixinJSBridge.invoke(
        'getBrandWCPayRequest', {
          "appId":param.appId,
          "timeStamp":param.timestamp,
          "nonceStr":param.nonceStr,
          "package":param.packageStr,
          "signType":param.signType,
          "paySign":param.paySign
        },
        function(res) {
          if(res.err_msg == "get_brand_wcpay_request:ok" ) {
            if(success) success();
          }else if(res.err_msg == "get_brand_wcpay_request:cancel") {
            if(fail) fail();
          }
        }
      );
    }
  }
};