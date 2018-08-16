# Genyuanlian

> Genyuanlian shop h5 app

## Build Setup

``` bash
# install dependencies
npm install 或 cnpm install

# serve with dev env at localhost:9000
npm run dev

# serve with test env at localhost:9000
npm run testing

# serve with product env at localhost:9000
npm run prod

# build for production with minification
npm run build

# build for test with minification
npm run build:test

```
## Auto script

```安装淘宝镜像 cnpm
npm install -g cnpm --registry=https://registry.npm.taobao.org
cd GYLShop
cnpm install
npm run bulid
cp dist/ ./data/www (复制发布包到web服务器路径)