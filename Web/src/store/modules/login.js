import Vue from 'vue';
import {getStore, setStore, removeStore} from '../../utils/storage.js';
import {
  LOGOUT,
  USER_LOGIN,
  USER_LOGIN_OUT,
  SET_TOKEN,
  SET_USER
} from '../mutation_types.js';

function handleUser(data) {
  let user = {};
  user.id = data.memberId||data.memberId;
  user.memberId = data.memberId||data.memberId;
  user.mobile = data.mobile||data.mobile;
  user.loginName = data.loginName||data.loginName;
  user.nickName = data.nickName||data.nickName;
  // user.referraCode = data.referraCode||data.referraCode;
  user.invitationCode = data.invitationCode||data.invitationCode;
  user.status = data.status||data.status;
  return user;
}

const state = {
  userInfo: [],
  loginInfo: []
};

const getters = {
  loginInfo: state => state.loginInfo,
  userInfo: state => state.userInfo
};

const actions = {
  login({commit, state}, data) {
    let user = handleUser(data);
    commit(USER_LOGIN, data);
    commit(SET_TOKEN, {id:user.id, token:data.token});
    commit(SET_USER, user);
  },
  loginOut({commit, state, rootState}, data) {
    commit(LOGOUT, data);
  }
};

const mutations = {
  // 登录
  [USER_LOGIN](state, data) {
    let user = handleUser(data);
    state.userInfo = user;
    state.loginInfo = data;
    setStore("MemberId", data.memberId);
    setStore("Token", data.token);
    setStore("User", JSON.stringify(user));
  },
  // 注销
  [USER_LOGIN_OUT](state, data ) {
    removeStore("MemberId");
    removeStore("Token");
    removeStore("User");
  }
};

export default {
  state,
  getters,
  actions,
  mutations
};