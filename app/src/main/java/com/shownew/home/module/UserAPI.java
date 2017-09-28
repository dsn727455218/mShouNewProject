package com.shownew.home.module;

import android.text.TextUtils;

import com.shownew.home.Config;
import com.shownew.home.ShouNewApplication;
import com.wp.baselib.utils.DesUtil;
import com.wp.baselib.utils.Preference;
import com.wp.baselib.utils.TimeUtil;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * @author Jason
 * @version 1.0
 * @date 2017/4/10 0010
 */

public class UserAPI extends PublicApi {


    public UserAPI(ShouNewApplication mainApplication) {
        super(mainApplication);
    }


    /**
     * 用户登陆
     *
     * @param name
     * @param password
     * @param lis
     */
    public void Login(String name, String password, ShouNewApplication.ShouNewHttpCallBackLisener lis) {
        Map<String, String> hashMap = getHashMap(Config.LOGIN);
        hashMap.put("name", name);
        try {
            password = DesUtil.MD5(password + TimeUtil.getTime2String(System.currentTimeMillis() + Preference.getLong(mainApplication, "timestamp"), "yyyyMMddHHmm"));
            hashMap.put("pass", password);
            hashMap.put("method", "login");
            post(Config.LOGIN, hashMap, lis);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


    }

    /**
     * 发送验证码
     *
     * @param phone   用来发送的手机号码
     * @param lisener
     */
    public void sendMsgCode(String phone, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map hashMap = getHashMap(Config.SEND_MSG);
        hashMap.put("phone", phone);
        hashMap.put("method", "sendSMS");
        post(Config.SEND_MSG, hashMap, lisener);
    }

    /**
     * @param phone     手机号码
     * @param password  密码
     * @param phoneCode 验证码
     * @param tag       标识  是否是注册 和  重置密码
     * @param lisener
     */

    public void registerOrModifyPwdAccount(String phone, String password, String phoneCode, String tag, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map hashMap = getHashMap(Config.REGISTER);
        hashMap.put("phone", phone);
        hashMap.put("method", tag);
        if ("register".equals(tag)) {
            hashMap.put("pass", password);
        } else if ("upPass".equals(tag)) {
            hashMap.put("newPass", password);
        }
        hashMap.put("phoneCode", phoneCode);
        post(Config.REGISTER, hashMap, lisener);
    }


    /**
     * 上传图片
     *
     * @param filePath 用来发送的图片文件路径
     * @param lisener
     */
    public void uploadUserHeadIcon(File filePath, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map hashMap = getHashMap(Config.USER);
        hashMap.put("method", "uploadImg");
        postFile(Config.USER, hashMap, "img", filePath, lisener);
    }

    /**
     * 注销
     *
     * @param lisener
     */
    public void logout(ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map hashMap = getHashMap(Config.LOGINOUT);
        hashMap.put("method", "loginOut");
        post(Config.USER, hashMap, lisener);
    }

    /**
     * 更换手机号
     *
     * @param newPhone  新的手机号
     * @param phoneCode 手机验证码
     * @param pass      密码
     * @param lisener
     */
    public void changePhone(String newPhone, String phoneCode, String pass, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map hashMap = getHashMap(Config.USER);
        hashMap.put("method", "upPhone");
        hashMap.put("pass", pass);
        hashMap.put("newPhone", newPhone);
        hashMap.put("phoneCode", phoneCode);
        post(Config.USER, hashMap, lisener);
    }

    /**
     * 用户修改交易密码接口
     *
     * @param phone        手机号
     * @param pass         登陆密码
     * @param newTradePass 交易密码
     * @param phoneCode    验证码
     * @param lisener
     */
    public void changeTradePass(String phone, String pass, String newTradePass, String phoneCode, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> hashMap = getHashMap(Config.USER);
        hashMap.put("method", "upTradePass");
        hashMap.put("phone", phone);

        hashMap.put("pass", pass);
        hashMap.put("newTradePass", newTradePass);
        hashMap.put("phoneCode", phoneCode);
        post(Config.USER, hashMap, lisener);

    }

    /**
     * 修改用户昵称
     *
     * @param nicheng
     * @param lisener
     */
    public void modifyNicheng(String nicheng, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> map = getHashMap(Config.USER);
        map.put("nickName", nicheng);
        map.put("method", "upUserInfo");
        post(Config.USER, map, lisener);
    }

    /**
     * 获取用户信息
     *
     * @param lisener
     */
    public void getUserData(ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> map = getHashMap(Config.USER);
        map.put("method", "getUserInfo");
        get(createUrlFromParams(Config.USER, map), lisener);
    }

    /**
     * 获取资源文件，如多功能充电器图片、微客服、使用手册url。
     * （sType：0-微客服（图）      1-多功能充电器（图）      2-使用手册（html）      3-功能续费背景（图）      4-分享页面（html））
     *
     * @param lisener
     */
    public void getReource(ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> map = getHashMap(Config.USER);
        map.put("method", "getSource");
        get(createUrlFromParams(Config.USER, map), lisener);
    }


    /**
     * 修改用户默认收货地址接口
     *
     * @param address    地址
     * @param payAddress 详细地址
     * @param payPhone   电话
     * @param payName    名字
     * @param lisener
     */
    public void modifyAddress(String lId, String address, String payAddress, String payPhone, String payName, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> map = getHashMap(Config.USER);
        if (!TextUtils.isEmpty(lId)) {
            map.put("id", lId);
        }
        map.put("method", "upUserAddress");
        map.put("city", address);
        map.put("address", payAddress);
        map.put("phone", payPhone);
        map.put("name", payName);
        post(Config.USER, map, lisener);
    }

    /**
     * 获取用户默认收货地址接口
     *
     * @param lisener
     */
    public void getDefaultAddress(ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> map = getHashMap(Config.USER);
        map.put("method", "getDefaultAddress");
        get(createUrlFromParams(Config.USER, map), lisener);
    }


    //钱包相关

    /**
     * 获取功能续费额度
     */
    public void getRenewList(ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> map = getHashMap(Config.PAY);
        map.put("method", "getRenewList");
        get(createUrlFromParams(Config.PAY, map), lisener);
    }

    /**
     * 获取充值额度接口
     *
     * @param lisener
     */
    public void getPayList(ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> map = getHashMap(Config.PAY);
        map.put("method", "getPayList");
        get(createUrlFromParams(Config.PAY, map), lisener);
    }

    /**
     * 获取消费者记录
     *
     * @param page
     * @param lisener
     */
    public void getRecordList(int page, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> map = getHashMap(Config.PAY);
        map.put("method", "getRecordList");
        map.put("page", String.valueOf(page));
        get(createUrlFromParams(Config.PAY, map), lisener);
    }

    /**
     * 获取订单列表
     *
     * @param page
     * @param lisener
     */
    public void getOderMenuList(int page, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> map = getHashMap(Config.ODER_MENU);
        map.put("page", String.valueOf(page));
        map.put("method", "getOrderzList");
        get(createUrlFromParams(Config.ODER_MENU, map), lisener);
    }

    /**
     * 提交订单
     *
     * @param locationId
     * @param productId
     * @param color
     * @param num
     * @param lisener
     */
    public void submitOrderz(String locationId, String price, String note, String productId, String color, int num, int type, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> map = getHashMap(Config.ODER_MENU);
        map.put("method", "submitOrderz");
        map.put("locationId", locationId);
        map.put("productId", productId);
        map.put("color", color);
        map.put("price", price);
        map.put("type", String.valueOf(type));
        if (!TextUtils.isEmpty(note)) {
            map.put("note", note);
        }
        map.put("num", String.valueOf(num));
        post(Config.ODER_MENU, map, lisener);
    }

    /**
     * 提交订单
     *
     * @param locationId
     * @param lisener
     */
    public void submitOrderzShopCarts(String locationId, int type, String jsonData, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> map = getHashMap(Config.ODER_MENU);
        map.put("method", "submitBatchOrderz");
        map.put("locationId", locationId);
        map.put("data", jsonData);
        map.put("type", String.valueOf(type));
        post(Config.ODER_MENU, map, lisener);
    }


    /**
     * 提交订单
     *
     * @param locationId
     * @param productId
     * @param color
     * @param num
     * @param lisener
     */
    public void submitShopMallOrderz(String locationId, String price, String note, String productId, String color, int num, int type, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> map = getHashMap(Config.ODER_MENU);
        map.put("method", "submitOrderz");
        map.put("locationId", locationId);
        map.put("mallproductId", productId);
        map.put("color", color);
        map.put("price", price);
        map.put("type", String.valueOf(type));
        if (!TextUtils.isEmpty(note)) {
            map.put("note", note);
        }
        map.put("num", String.valueOf(num));
        post(Config.ODER_MENU, map, lisener);
    }

    /**
     * 删除订单
     */
    public void deleteOrderz(String id, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> map = getHashMap(Config.ODER_MENU);
        map.put("method", "delOrderz");
        map.put("id", id);
        post(Config.ODER_MENU, map, lisener);
    }

    /**
     * 获取订单详情
     */
    public void getOrderzInfo(String id, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> map = getHashMap(Config.ODER_MENU);
        map.put("method", "getOrderzInfo");
        map.put("id", id);
        get(createUrlFromParams(Config.ODER_MENU, map), lisener);
    }

    /**
     * 支付宝获取支付参数接口
     *
     * @param orderzNo
     * @param lisener
     */
    public void alipayCharge(String orderzNo, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> map = getHashMap(Config.PAY);
        map.put("method", "getAlipayInfo");
        map.put("orderzNo", orderzNo);
        get(createUrlFromParams(Config.PAY, map), lisener);

    }

    /**
     * 微信获取支付参数接口
     *
     * @param orderzNo
     * @param lisener
     */
    public void wxCharge(String orderzNo, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> map = getHashMap(Config.PAY);
        map.put("method", "getTenpayInfo");
        map.put("orderzNo", orderzNo);
        get(createUrlFromParams(Config.PAY, map), lisener);
    }

    /**
     * 获取余额、首次充值接口（已测试）
     * response     remain：用户余额       isFirst：0=首冲    1=非首冲       tradePass：0=未设置交易密码    1=已设置交易密码）
     *
     * @param lisener
     */
    public void getPayAboutInfo(ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> map = getHashMap(Config.USER);
        map.put("method", "getPayAboutInfo");
        get(createUrlFromParams(Config.USER, map), lisener);
    }

    /**
     * 充值、续费接口（已测试）
     * 充值金额	price	Double	是
     * 类别	from	int	是	0-钱包充值 1-功能续费
     * 充值类型	type	int	是	0-微信支付 1-支付宝支付 2-钱包支付（功能续费 没有钱包支付功能）
     * 是否首冲	isFirst	int	是	0-首冲 1-非首冲
     * 收货地址	locationId	int	否	当 isFirst=0 时才需要传入该值
     * 支付密码	pass	String	否	当选择 钱包支付 时才需要传入该值
     * 会话ID	jSessionId	String	是	验证登录
     * 当【isFirst=0】时才需要传入该值（汽车/电动车）
     */
    public void pay(String price, int from, int type, int isFirst, String locationId, String pass, String note, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> map = getHashMap(Config.PAY);
        map.put("method", "pay");
        map.put("price", price);
        map.put("from", String.valueOf(from));
        map.put("type", String.valueOf(type));
        if (0 == isFirst) {
            if (!TextUtils.isEmpty(locationId)) {
                map.put("locationId", locationId);
            }
            if (!TextUtils.isEmpty(note)) {
                map.put("note", note);
            }
        }
        map.put("isFirst", String.valueOf(isFirst));
        if (!TextUtils.isEmpty(pass)) {
            try {
                pass = DesUtil.MD5(pass + TimeUtil.getTime2String(System.currentTimeMillis() + Preference.getLong(mainApplication, "timestamp"), "yyyyMMddHHmm"));
                map.put("pass", pass);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        post(Config.PAY, map, lisener);
    }

    /**
     * 钱包支付
     *
     * @param oderNum
     * @param pass
     * @param lisener
     */
    public void walletPay(String oderNum, String pass, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> map = getHashMap(Config.PAY);
        map.put("orderzNo", oderNum);
        map.put("method", "walletPay");
        try {
            pass = DesUtil.MD5(pass + TimeUtil.getTime2String(System.currentTimeMillis() + Preference.getLong(mainApplication, "timestamp"), "yyyyMMddHHmm"));
            map.put("pass", pass);
            post(Config.PAY, map, lisener);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    /**
     * 查看微信支付订单状态接口
     *
     * @param orderzNo
     * @param lisener
     */
    public void checkTenpayOrderz(String orderzNo, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> map = getHashMap(Config.PAY);
        map.put("orderzNo", orderzNo);
        map.put("method", "checkTenpayOrderz");
        get(createUrlFromParams(Config.PAY, map), lisener);
    }


    /**
     * 查看支付包支付订单状态接口
     *
     * @param data    支付完成后支付宝返回的结果（应是一个json数据：对于iOS平台而言返回参数是一个NSDictionary对象，对于Android平台而言是一个map结构体）
     * @param lisener
     */
    public void checkAlipayOrderz(String data, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> map = getHashMap(Config.PAY);
        map.put("data", data);
        map.put("method", "checkAlipayOrderz");
        post(Config.PAY, map, lisener);
    }

    /**
     * 取消订单
     * @param oId
     * @param lisener
     */
    public void cancelOrderz(String oId, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> map = getHashMap(Config.ODER_MENU);
        map.put("method", "cannelOrderz");
        map.put("id", oId);
        post(Config.ODER_MENU, map, lisener);
    }

    /**
     * 确认收货
     * @param oId
     * @param lisener
     */
    public void confirmReceived(String oId, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> map = getHashMap(Config.ODER_MENU);
        map.put("method", "confirmReceived");
        map.put("id", oId);
        post(Config.ODER_MENU, map, lisener);
    }
}
