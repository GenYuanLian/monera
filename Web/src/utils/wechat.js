import wx from 'weixin-js-sdk';
import apiUrl from "@/config/apiUrl.js";
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
    //
    //http://10.68.79.42:8091/api/weChat/weChatShare
    $vue.$httpPost(apiUrl.weChatShare, {shareLink: options.localUrl}).then((res) => {
      if(res.data&&res.data.status==="1000") {
        wx.config({
          debug: false,
          appId: res.data.appId, // 和获取Ticke的必须一样------必填，公众号的唯一标识
          timestamp: res.data.timestamp, // 必填，生成签名的时间戳
          nonceStr: res.data.nonceStr, // 必填，生成签名的随机串
          signature: res.data.signature, // 必填，签名
          jsApiList: [
            'onMenuShareAppMessage', 'onMenuShareTimeline',
            'onMenuShareQQ', 'onMenuShareQZone'
          ]
        });
        //处理验证失败的信息
        wx.error(function (res) {
          console.log('验证失败返回的信息:', res);
        });
        //处理验证成功的信息
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
              showMsg(JSON.stringify(options));
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
  }
};