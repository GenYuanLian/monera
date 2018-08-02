import Vue from "vue";
import VueRouter from "vue-router";
import NProgress from "nprogress";
import "nprogress/nprogress.css";
import store from "../store";
import { getStore, setStore, removeStore } from "@/utils/storage.js";
import * as types from "../store/mutation_types";
NProgress.configure({ showSpinner: false });
const Test = resolve => require(["@/views/Test/test"], resolve);
const Demo = resolve => require(["@/views/Test/demo"], resolve);
const NotFound = resolve => require(["@/views/Error/NotFound"], resolve);
const Home = resolve => require(["@/views/Home"], resolve);
const Login = resolve => require(["@/views/Login"], resolve);
const Register = resolve => require(["@/views/Register/Register"], resolve);
const ForgetPwd = resolve => require(["@/views/Register/ForgetPwd"], resolve);
const RegisterSucc = resolve => require(["@/views/Register/Register_Succ"], resolve);
const PayPwd = resolve => require(["@/views/Ident/Pay_Pwd"], resolve);
const PaySucc = resolve => require(["@/views/Ident/Pay_Succ"], resolve);
const Mine = resolve => require(["@/views/Center/Mine"], resolve);
const News = resolve => require(["@/views/Center/News"], resolve);
const Address = resolve => require(["@/views/Center/Address"], resolve);
const AddressEdit = resolve => require(["@/views/Center/Address_Edit"], resolve);
const Invite = resolve => require(["@/views/Center/Invite"], resolve);
const InviteShare = resolve => require(["@/views/Center/Invite_Share"], resolve);
const Card = resolve => require(["@/views/Center/Card"], resolve);
const CardHistory = resolve => require(["@/views/Center/Card_History"], resolve);
const CardActive = resolve => require(["@/views/Center/Card_Active"], resolve);
const CardDetail = resolve => require(["@/views/Center/Card_Detail"], resolve);
const Collection = resolve => require(["@/views/Center/Collection"], resolve);
const ComputingServer = resolve => require(["@/views/Center/Computing_Server"], resolve);
const ComSerDetail = resolve => require(["@/views/Center/ComSer_Detail"], resolve);
const MineShare = resolve => require(["@/views/Center/Mine_Share"], resolve);
const MineInvite = resolve => require(["@/views/Center/Mine_Invite"], resolve);
const InviteUser = resolve => require(["@/views/Center/Invite_User"], resolve);
const InviteProduct = resolve => require(["@/views/Center/Invite_Product"], resolve);
const InviteProDetail = resolve => require(["@/views/Center/InvitePro_Detail"], resolve);
const PersonalInfor = resolve => require(["@/views/User/Personal_Infor"], resolve);
const NickName = resolve => require(["@/views/User/Nick_Name"], resolve);
const UserName = resolve => require(["@/views/User/User_Name"], resolve);
const Setting = resolve => require(["@/views/SetUp/Setting"], resolve);
const LoginPwdEdit = resolve => require(["@/views/SetUp/LoginPwd_Edit"], resolve);
const PayPwdBack = resolve => require(["@/views/SetUp/PayPwd_Back"], resolve);
const PayPwdBackNext = resolve => require(["@/views/SetUp/PayPwd_Back_Next"], resolve);
const PayPwdEdit = resolve => require(["@/views/SetUp/PayPwd_Edit"], resolve);
const GesturePwd = resolve => require(["@/views/SetUp/Gesture_Pwd"], resolve);
const ProductDetail = resolve => require(["@/views/Goods/Product_Detail"], resolve);
const ProductEvaluate = resolve => require(["@/views/Goods/Product_Evaluate"], resolve);
const Orders = resolve => require(["@/views/Order/Orders"], resolve);
const OrderPlace = resolve => require(["@/views/Order/Order_Place"], resolve);
const OrderCancel = resolve => require(["@/views/Order/Order_Cancel"], resolve);
const OrderDetailCard = resolve => require(["@/views/Order/Order_Detail_Card"], resolve);
const OrderDetailProduct = resolve => require(["@/views/Order/Order_Detail_Product"], resolve);
const OrderEvaluate = resolve => require(["@/views/Order/Order_Evaluate"], resolve);
const PayOrder = resolve => require(["@/views/Pay/Pay_Order"], resolve);
const PaySuccess = resolve => require(["@/views/Pay/Pay_Success"], resolve);
const PayWx = resolve => require(["@/views/Pay/Pay_Wx"], resolve);
const MerchantInfo = resolve => require(["@/views/Merchant/Merchant_Info"], resolve);
const MerchantDetail = resolve => require(["@/views/Merchant/Merchant_Detail"], resolve);
const MerchantEval = resolve => require(["@/views/Merchant/Merchant_Evaluate"], resolve);
const ActiveCard = resolve => require(["@/views/Other/Active_Card"], resolve);
const Download = resolve => require(["@/views/Other/Download"], resolve);
const UserWallet = resolve => require(["@/views/Other/User_Wallet"], resolve);
const Wallet = resolve => require(["@/views/Other/Wallet"], resolve);
const ConfirmPay = resolve => require(["@/views/Admin/Confirm_Pay"], resolve);
const About = resolve => require(["@/views/SetUp/About"], resolve);
const AuctionBlock = resolve => require(["@/views/Auction/Auction_Block"], resolve);
const AuctionHome = resolve => require(["@/views/Auction/Auction_Home"], resolve);
const AuctionList = resolve => require(["@/views/Auction/Auction_List"], resolve);
const AuctionBail = resolve => require(["@/views/Auction/Auction_Bail"], resolve);
const AuctionRemind = resolve => require(["@/views/Auction/Auction_Remind"], resolve);
const AuctionPayBail = resolve => require(["@/views/Auction/Auction_Pay_Bail"], resolve);
const AuctionProduct = resolve => require(["@/views/Auction/Auction_Product"], resolve);
const AuctionBidlist = resolve => require(["@/views/Auction/Auction_Bidlist"], resolve);
const SnatchBlock = resolve => require(["@/views/Snatch/Snatch_Block"], resolve);
const SnatchHome = resolve => require(["@/views/Snatch/Snatch_Home"], resolve);
const SnatchList = resolve => require(["@/views/Snatch/Snatch_List"], resolve);
const SnatchBail = resolve => require(["@/views/Snatch/Snatch_Bail"], resolve);
const SnatchRemind = resolve => require(["@/views/Snatch/Snatch_Remind"], resolve);
const SnatchPayBail = resolve => require(["@/views/Snatch/Snatch_Pay_Bail"], resolve);
const SnatchPay = resolve => require(["@/views/Snatch/Snatch_Pay"], resolve);
const SnatchProduct = resolve => require(["@/views/Snatch/Snatch_Product"], resolve);
const WalletMine = resolve => require(["@/views/Wallet/Wallet_Mine"], resolve);
const WalletReceive = resolve => require(["@/views/Wallet/Wallet_Receive"], resolve);
const WalletRecordDetail = resolve => require(["@/views/Wallet/Wallet_Record_Detail"], resolve);
const WalletRecord = resolve => require(["@/views/Wallet/Wallet_Record"], resolve);
const WalletSend = resolve => require(["@/views/Wallet/Wallet_Send"], resolve);
Vue.use(VueRouter);
const routes = [{
  path:"*",
  component:NotFound
},
{
  path: "/test",
  name: "test",
  component: Test
},
{
  path: "/demo",
  name: "demo",
  component: Demo
},
{
  path: "/",
  name: "index",
  component: Home
},
{
  path: "/home",
  name: "home",
  component: Home
},
{
  path: "/login",
  name: "login",
  component: Login
},
{
  path: "/register",
  name: "register",
  component: Register
},
{
  path: "/forget_pwd",
  name: "forget_pwd",
  component: ForgetPwd
},
{
  path: "/register_succ",
  name: "register_succ",
  component: RegisterSucc
},
{
  path: "/pay_pwd",
  name: "pay_pwd",
  component: PayPwd,
  meta: { requiresAuth: true }
},
{
  path: "/pay_succ",
  name: "pay_succ",
  component: PaySucc
},
{
  path: "/mine",
  name: "mine",
  component: Mine
},
{
  path: "/news",
  name: "news",
  component: News
},
{
  path: "/address",
  name: "address",
  component: Address,
  meta: { requiresAuth: true }
},
{
  path: "/address_edit",
  name: "address_edit",
  component: AddressEdit
},
{
  path: "/invite",
  name: "invite",
  component: Invite,
  meta: { requiresAuth: true }
},
{
  path: "/invite_share",
  name: "invite_share",
  component: InviteShare
},
{
  path: "/card_detail",
  name: "card_detail",
  component: CardDetail
},
{
  path: "/collection",
  name: "collection",
  component: Collection
},
{
  path: "/computing_server",
  name: "computing_server",
  component: ComputingServer
},
{
  path: "/comSer_detail",
  name: "comSer_detail",
  component: ComSerDetail
},
{
  path: "/card_active",
  name: "card_active",
  component: CardActive
},
{
  path: "/card",
  name: "card",
  component: Card
},
{
  path: "/card_history",
  name: "card_history",
  component: CardHistory
},
{
  path: "/personal_infor",
  name: "personal_infor",
  component: PersonalInfor,
  meta: { requiresAuth: true }
},
{
  path: "/nick_name",
  name: "nick_name",
  component: NickName,
  meta: { requiresAuth: true }
},
{
  path: "/user_name",
  name: "user_name",
  component: UserName,
  meta: { requiresAuth: true }
},
{
  path: "/setting",
  name: "setting",
  component: Setting,
  meta: { requiresAuth: true }
},
{
  path: "/loginpwd_edit",
  name: "loginpwd_edit",
  component: LoginPwdEdit,
  meta: { requiresAuth: true }
},
{
  path: "/paypwd_back",
  name: "paypwd_back",
  component: PayPwdBack,
  meta: { requiresAuth: true }
},
{
  path: "/paypwd_back_next",
  name: "paypwd_back_next",
  component: PayPwdBackNext,
  meta: { requiresAuth: true }
},
{
  path: "/paypwd_edit",
  name: "paypwd_edit",
  component: PayPwdEdit,
  meta: { requiresAuth: true }
},
{
  path: "/gesture_pwd",
  name: "gesture_pwd",
  component: GesturePwd,
  meta: { requiresAuth: true }
},
{
  path: "/product_detail",
  name: "product_detail",
  component: ProductDetail
},
{
  path: "/product_evaluate",
  name: "product_evaluate",
  component: ProductEvaluate
},
{
  path: "/orders",
  name: "orders",
  component: Orders,
  meta: { keepAlive: false }
},
{
  path: "/order_place",
  name: "order_place",
  component: OrderPlace,
  meta: { requiresAuth: true }
},
{
  path: "/order_cancel",
  name: "order_cancel",
  component: OrderCancel
},
{
  path: "/order_detail_card",
  name: "order_detail_card",
  component: OrderDetailCard
},
{
  path: "/order_detail_product",
  name: "order_detail_product",
  component: OrderDetailProduct
},
{
  path: "/order_evaluate",
  name: "order_evaluate",
  component: OrderEvaluate
},
{
  path: "/pay_order",
  name: "pay_order",
  component: PayOrder,
  meta: { requiresAuth: true }
},
{
  path: "/pay/pay_wx",
  name: "pay_wx",
  component: PayWx
},
{
  path: "/pay_success",
  name: "pay_success",
  component: PaySuccess
},
{
  path: "/merchant_info",
  name: "merchant_info",
  component: MerchantInfo
},
{
  path: "/merchant_detail",
  name: "merchant_detail",
  component: MerchantDetail
},
{
  path: "/merchant_evaluate",
  name: "merchant_evaluate",
  component: MerchantEval
},
{
  path: "/active_card",
  name: "active_card",
  component: ActiveCard
},
{
  path: "/download",
  name: "download",
  component: Download
},
{
  path: "/confirm_pay",
  name: "confirm_pay",
  component: ConfirmPay
},
{
  path: "/invite_user",
  name: "invite_user",
  component: InviteUser
},
{
  path: "/mine_share",
  name: "mine_share",
  component: MineShare
},
{
  path: "/mine_invite",
  name: "mine_invite",
  component: MineInvite
},
{
  path: "/invite_product",
  name: "invite_product",
  component: InviteProduct
},
{
  path: "/invitePro_detail",
  name: "invitePro_detail",
  component: InviteProDetail
},
{
  path: "/about",
  name: "about",
  component: About
},
{
  path: "/user_wallet",
  name: "user_wallet",
  component: UserWallet
},
{
  path: "/wallet",
  name: "wallet",
  component: Wallet
},
{
  path: "/auction_block",
  name: "auction_block",
  component: AuctionBlock
},
{
  path: "/auction_home",
  name: "auction_home",
  component: AuctionHome
},
{
  path: "/auction_list",
  name: "auction_list",
  component: AuctionList
},
{
  path: "/auction_bail",
  name: "auction_bail",
  component: AuctionBail
},
{
  path: "/auction_remind",
  name: "auction_remind",
  component: AuctionRemind
},
{
  path: "/auction_product",
  name: "auction_product",
  component: AuctionProduct
},
{
  path: "/auction_pay_bail",
  name: "auction_pay_bail",
  component: AuctionPayBail
},
{
  path: "/auction_bidlist",
  name: "auction_bidlist",
  component: AuctionBidlist
},
{
  path: "/snatch_block",
  name: "snatch_block",
  component: SnatchBlock
},
{
  path: "/snatch_home",
  name: "snatch_home",
  component: SnatchHome
},
{
  path: "/snatch_list",
  name: "snatch_list",
  component: SnatchList
},
{
  path: "/snatch_bail",
  name: "snatch_bail",
  component: SnatchBail
},
{
  path: "/snatch_remind",
  name: "snatch_remind",
  component: SnatchRemind
},
{
  path: "/snatch_product",
  name: "snatch_product",
  component: SnatchProduct
},
{
  path: "/snatch_pay",
  name: "snatch_pay",
  component: SnatchPay
},
{
  path: "/snatch_pay_bail",
  name: "snatch_pay_bail",
  component: SnatchPayBail
},
{
  path: "/wallet_mine",
  name: "wallet_mine",
  component: WalletMine
},
{
  path: "/wallet_receive",
  name: "wallet_receive",
  component: WalletReceive
},
{
  path: "/wallet_record_detail",
  name: "wallet_record_detail",
  component: WalletRecordDetail
},
{
  path: "/wallet_record",
  name: "wallet_record",
  component: WalletRecord
},
{
  path: "/wallet_send",
  name: "wallet_send",
  component: WalletSend
}
];
// 页面刷新时，重新赋值token
if (window.localStorage && window.localStorage.getItem("Token")) {
  let id = window.localStorage.getItem("MemberId");
  let token = window.localStorage.getItem("Token");
  store.commit(types.SET_TOKEN, { id: id, token: token });
  store.commit(types.SET_USER, window.localStorage.getItem("User"));
  store.commit(types.SET_LOCAL_URL, document.URL);
} else {
  console.log("不支持本地存储，没有Token");
}

const router = new VueRouter({
  mode: "hash",
  routes
});

router.beforeEach((to, from, next) => {
  //保存当前URL
  store.commit(types.SET_LOCAL_URL, document.URL);
  if (to.matched.some(r => r.meta.requiresAuth)) {
    if (store.state.token) {
      NProgress.start();
      next();
    } else {
      next({path: "/login", query: { redirect: to.fullPath }});
    }
  } else {
    NProgress.start();
    if(from.name=="orders" && (to.name=="order_detail_product"||to.name=="order_detail_card")) {
      from.meta.keepAlive = true;
    } else {
      from.meta.keepAlive = false;
    }
    next();
  }
});

router.afterEach((to, from) => {
  NProgress.done();
});

export default router;
