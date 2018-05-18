const merge = require('webpack-merge')
const devEnv = require('./dev.env')

module.exports = merge(devEnv, {
  NODE_ENV: '"testing"',
  API_SERVER:'http://58.87.112.65:8081'
})
// http://10.70.208.143:8093
