(function(w) {
  let HandLock = function(option) {
    this.el = option.el || w.document.body;
    this.n = option.n || 3;
    this.n = (this.n >= 2 && this.n <= 5) ? this.n : 3; // n 太大，你能记得住吗
    this.circles = []; // 用来存储 n*n 个 circle 的位置
    this.touchCircles = []; // 用来存储已经触摸到的所有 circle
    this.restCircles = []; // 还未触到的 circle
    this.touchFlag = false; // 用于判断是否 touch 到 circle
    this.model = option.model; // 1-验证密码  2-第一次输入密码  3-确认密码
    this.reDraw = false; //表示是否需要重绘
    this.tempPass = []; // 临时密码
    this.verifyPass = option.verify; // 验证密码函数
    this.savePass = option.save; // 保存密码函数
    this.passLen = option.passLen; // 设置密码最小长度
    this.showMsg = option.showMsg; // 提示信息
    this.color = option.color; // 画圆的颜色
  };
  HandLock.prototype = {
    init: function() { // 函数入口
      this.createCanvas();
      this.createCircles();
      this.createListener();
    },

    createCanvas: function() { // 创建 canvas
      let width, elRect;
      elRect = this.el.getBoundingClientRect();
      width = elRect.width < 300 ? 300 : elRect.width;
      let canvas = document.createElement('canvas');
      canvas.width = canvas.height = width;
      this.el.appendChild(canvas);

      let canvas2 = canvas.cloneNode(canvas, true);
      canvas2.style.position = 'absolute';
      canvas2.style.top = '0';
      canvas2.style.left = '0';
      this.el.appendChild(canvas2);

      this.ctx = canvas.getContext('2d');
      this.canvas = canvas;
      this.width = width;

      this.ctx2 = canvas2.getContext('2d');
      this.ctx2.strokeStyle = this.color || '#ffa726';
      this.canvas2 = canvas2;
    },

    createCircles: function() { // 画圆
      let n = this.n;
      this.r = Math.floor(this.width / (2 + 4 * n)); // 这里是参考的，感觉这种画圆的方式挺合理的，方方圆圆
      r = this.r;
      for (let i = 0; i < n; i++) {
        for (let j = 0; j < n; j++) {
          let p = {
            x: j * 4 * r + 3 * r,
            y: i * 4 * r + 3 * r,
            id: i * 3 + j
          };
          this.circles.push(p);
          this.restCircles.push(p);
        }
      }
      this.drawCircles();
    },

    createListener: function() { // 创建监听事件
      let self = this;
      let temp;
      let r = this.r;
      let over = false;
      this.canvas2.addEventListener('touchstart', function(e) {
        //self.do = 0;
        //self.wantdo = 0;
        let p = self.getTouchPos(e);
        self.restCircles = self.restCircles.concat(self.touchCircles.splice(0));
        self.judgePos(p);
      }, false);
      let t = this.throttle(function(e) {
        //this.do ++;
        let p = this.getTouchPos(e);
        if (this.touchFlag) {
          this.update(p);
        } else {
          this.judgePos(p);
        }
      }, 16, 16);
      this.canvas2.addEventListener('touchmove', t, false);
      this.canvas2.addEventListener('touchend', function(e) {
        //console.log('do: ' + self.do);
        //console.log('wantdo: ' + self.wantdo);
        if (self.touchFlag) {
          self.touchFlag = false;
          self.checkPass();
          self.restCircles = self.restCircles.concat(self.touchCircles.splice(0)); // 将touchCircle 清空
          self.ctx2.clearRect(0, 0, this.width, this.width); // 提前做情况操作
          setTimeout(function() {
            self.reset();
          }, 400);
        }
      }, false);
    },

    update: function(p) { // 更新 touchmove
      this.judgePos(p);
      this.drawLine2TouchPos(p);
      if (this.reDraw) {
        this.reDraw = false;
        this.drawPoints();
        this.drawLine();
      }
    },

    checkPass: function() { // 判断当前 model 和检查密码
      let succ;
      let model = this.model;
      if (model == 2) { // 设置密码
        if (this.touchCircles.length < this.passLen) { // 验证密码长度
          succ = false;
          this.showMsg('密码长度至少为 ' + this.passLen + '！');
        } else {
          succ = true;
          this.tempPass = []; // 将密码放到临时区存储
          for (let i = 0; i < this.touchCircles.length; i++) {
            this.tempPass.push(this.touchCircles[i].id);
          }
          this.model = 3;
          this.showMsg('请再次输入密码');
        }
      } else if (model == 3) { // 确认密码
        let flag = true;
        // 先要验证密码是否正确
        if (this.touchCircles.length == this.tempPass.length) {
          let tc = this.touchCircles;
          let lt = this.tempPass;
          for (let i = 0; i < tc.length; i++) {
            if (tc[i].id != lt[i]) {
              flag = false;
            }
          }
        } else {
          flag = false;
        }
        if (!flag) {
          succ = false;
          this.showMsg('两次密码不一致，请重新输入');
          this.model = 2; // 由于密码不正确，重新回到 model 2
        } else {
          succ = true; // 密码正确,调用保存方法
          this.savePass(this.tempPass.join('-'));
          this.model = 1;
        }
        delete this.tempPass; // 很重要，一定要删掉，bug
      } else if (model == 1) { // 验证密码
        let tc = '';
        this.touchCircles.forEach(element => {
          tc += element.id + '-';
        });
        tc = tc.slice(0, tc.length - 1);
        succ = this.verifyPass(tc); // 验证密码函数
      }
      if (succ) {
        this.drawEndCircles('#2CFF26'); // 绿色
      } else {
        this.drawEndCircles('red'); // 红色
      }
    },

    drawCircle: function(x, y, color) { // 画圆
      this.ctx.strokeStyle = color || this.color || '#ffa726';
      this.ctx.lineWidth = 2;
      this.ctx.beginPath();
      this.ctx.arc(x, y, this.r, 0, Math.PI * 2, true);
      this.ctx.closePath();
      this.ctx.stroke();
    },

    drawCircles: function() { // 画所有圆
      this.ctx.clearRect(0, 0, this.width, this.width); // 为了防止重复画
      for (let i = 0; i < this.circles.length; i++) {
        this.drawCircle(this.circles[i].x, this.circles[i].y);
      }
    },

    drawEndCircles: function(color) { // end 时重绘已经 touch 的圆
      for (let i = 0; i < this.touchCircles.length; i++) {
        this.drawCircle(this.touchCircles[i].x, this.touchCircles[i].y, color);
      }
    },

    drawLine: function() { // 画折线
      let len = this.touchCircles.length;
      if (len >= 2) {
        this.ctx.beginPath();
        this.ctx.lineWidth = 3;
        this.ctx.moveTo(this.touchCircles[len - 2].x, this.touchCircles[len - 2].y);
        this.ctx.lineTo(this.touchCircles[len - 1].x, this.touchCircles[len - 1].y);
        this.ctx.stroke();
        this.ctx.closePath();
      }
    },

    drawLine2TouchPos: function(p) {
      let len = this.touchCircles.length;
      if (len >= 1) {
        this.ctx2.clearRect(0, 0, this.width, this.width); // 先清空
        this.ctx2.beginPath();
        this.ctx2.lineWidth = 3;
        this.ctx2.moveTo(this.touchCircles[len - 1].x, this.touchCircles[len - 1].y);
        this.ctx2.lineTo(p.x, p.y);
        this.ctx2.stroke();
        this.ctx2.closePath();
      }
    },

    drawPoints: function() { // 画实心圆(点)
      let i = this.touchCircles.length - 1;
      if (i >= 0) {
        this.ctx.fillStyle = this.color || '#ffa726';
        this.ctx.beginPath();
        this.ctx.arc(this.touchCircles[i].x, this.touchCircles[i].y, this.r / 2, 0, Math.PI * 2, true);
        this.ctx.closePath();
        this.ctx.fill();
      }
    },

    getTouchPos: function(e) { // 获得触摸点的相对位置
      let rect = e.target.getBoundingClientRect();
      let p = { // 相对坐标
        x: e.touches[0].clientX - rect.left,
        y: e.touches[0].clientY - rect.top
      };
      return p;
    },

    judgePos: function(p) { // 判断 触点 是否在 circle 內
      for (let i = 0; i < this.restCircles.length; i++) {
        temp = this.restCircles[i];
        if (Math.abs(p.x - temp.x) < r && Math.abs(p.y - temp.y) < r) {
          this.touchCircles.push(temp);
          this.restCircles.splice(i, 1);
          this.touchFlag = true;
          this.reDraw = true;
          break;
        }
      }
    },

    reset: function() { // 重置 canvas
      this.drawCircles();
      this.ctx2.clearRect(0, 0, this.width, this.width); // 先清空
    },

    throttle: function(func, delay, mustRun) { // 节流函数
      let timer;
      let startTime = new Date();
      let self = this;
      return function(e) {
        //self.wantdo ++;

        /* 修复一个 bug，由于延迟导致的 preventDefault 失效 */
        if (e) {
          if (e.preventDefault) {
            e.preventDefault();
          }
          if (e.stopPropagation) {
            e.stopPropagation();
          }
        }
        let curTime = new Date();
        let args = arguments;
        clearTimeout(timer);
        if (curTime - startTime >= mustRun) {
          startTime = curTime;
          func.apply(self, args);
        } else {
          timer = setTimeout(function() {
            func.apply(self, args);
          }, delay);
        }
      };
    }
  };

  w.HandLock = HandLock; // 赋给全局 window
})(window);