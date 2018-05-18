const merge = require('webpack-merge')
const prodEnv = require('./prod.env')

module.exports = merge(prodEnv, {
  NODE_ENV: '"development"',
  API_SERVER:'http://shoptestapi.genyuanlian.com'
})
//http://10.10.12.92:8080