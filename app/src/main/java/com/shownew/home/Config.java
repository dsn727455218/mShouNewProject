package com.shownew.home;

/**
 * Api配置
 *
 * @author Jason
 * @version 1.0
 * @date 2017/3/31 0031
 */

public class Config extends com.wp.baselib.Config {
    //广告活动
    public static final  String ACTION_ADV="1";
    public static final  String ACTION="6";
    public static final  String ACTION_WEB="0";

    //订单号
    public static final String ORDER = "order";

    //微信支付状态
    public static final String WX_STATE = "WX_STATE";
    //支付类型
    public static final String FLAG = "flag";
    /**
     * 是否 第一次进入app
     */
    public static final String ISFIRST_ENTER = "isfirst_enter_app";
    /**
     * //  1  超市 2  钱包 续费
     */
    public static final String SHOP_TYPE = "shop_type";
    public static final String USERINFO = "userinfo";
    public static final String ROURCES_DATA = "rources";
    // 用于保存SharedPreferences的文件
    public static final String FILE = "saveUserNamePwd";


    /**
     * api接口跟路径
     */
    private static final String WEB_ROOT = "http://www.shounew.cn:8090/Shou6Control/";
//    private static final String WEB_ROOT = "http://www.shounew.cn/Shou6Control/";
    //================================================================用户相关==

    public static final String USER = WEB_ROOT + "user.do";
    /**
     * App用户登录接口
     */
    public static final String LOGIN = USER;
    /**
     * App用户发送短信接口（已测试）
     */
    public static final String SEND_MSG = USER;
    /**
     * 注册
     */
    public static final String REGISTER = USER;
    /**
     * 注销
     */
    public static final String LOGINOUT = USER;
    /**
     * 上传用户头像
     */
    public static final String UPLOAD_HEAD_ICON = USER;
    //=======================================================================云控设备

    public static final String DEVICE = WEB_ROOT + "userCtl.do";
    /**
     * 设置振动灵敏度接口（已测试）
     */
    public static final String UP_MOVE_LEVEL = DEVICE;
    /**
     * 获取最新GPS数据接口
     */
    public static final String GET_NEW_GPS = DEVICE;
    /**
     * 设置振动报警接口
     */
    public static final String UP_MOVE_WARN = DEVICE;
    /**
     * 控制与锁定接口
     */
    public static final String UP_CONTROL = DEVICE;
    /**
     * 设置最高速度限制接口
     */
    public static final String UP_LIMIT_SPEED = DEVICE;

    /**
     * 充电器开关接口（已测试）
     */
    public static final String UP_CHARGER = WEB_ROOT + "userCtl.do";
    /**
     * 获取电池最新电量值接口（已测试）http://[ip]:[port]/[app]/userCtl.do?method=getElectricity
     */
    public static final String GET_ELECTRICITY = WEB_ROOT + "userCtl.do";


    /**
     * 获取硬件最新数据接口（已测试）
     */

    public static final String DEVICE_NEW_DATA = WEB_ROOT + "userCtl.do";
    //====================================================================================
    //活动消息接口

    /**
     * 用户获取最新消息列表接口
     */
    public static final String GET_MSG_LIST = WEB_ROOT + "message.do";

    //===============================================================================

    /**
     * 车辆接口
     */
    public static final String CAR = WEB_ROOT + "car.do";

    //========================服务接口

    public static final String SERICE = WEB_ROOT + "server.do";


    /**
     * 商城
     */
    public static final String SHOP = WEB_ROOT + "product.do";

    /**
     * 商品搜索
     */
    public static final String SHOP_SEARCH = WEB_ROOT + "search.do";

    /**
     * 钱包充值接口
     */
    public static final String PAY = WEB_ROOT + "pay.do";
    /**
     * 订单
     */
    public static final String ODER_MENU = WEB_ROOT + "orderz.do";

    /**
     * 微信支付
     */
    public static final java.lang.String WXPAY = "wx18e68dc827ac8cc7";
    public static final String SN = "SN";
}
