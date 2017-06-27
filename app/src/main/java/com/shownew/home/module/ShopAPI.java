package com.shownew.home.module;

import android.text.TextUtils;

import com.shownew.home.Config;
import com.shownew.home.ShouNewApplication;

import java.util.Map;

/**
 * @author Jason
 * @version 1.0
 * @date 2017/5/4 0004
 */

public class ShopAPI extends PublicApi {
    public ShopAPI(ShouNewApplication mainApplication) {
        super(mainApplication);
    }

    /**
     * 获取商品列表接口
     *
     * @param type    商品类型
     * @param page    页码
     * @param lisener
     */
    private void getProductList(int type, int page, String id, String keyWord, String order, String method, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> hashMap = getHashMap(Config.SHOP);
        if (1000 != type) {
            hashMap.put("type", String.valueOf(type));
        }
        hashMap.put("page", String.valueOf(page));
        hashMap.put("method", method);
        if (!TextUtils.isEmpty(id)) {
            hashMap.put("id", id);
        }
        if (!TextUtils.isEmpty(keyWord)) {
            hashMap.put("keyWord", keyWord);
        }
        if (!TextUtils.isEmpty(order)) {
            hashMap.put("order", order);
        }
        get(createUrlFromParams(Config.SHOP, hashMap), lisener);
    }

    /**
     * 获取商品列表接口
     *
     * @param type    商品类型
     * @param page    页码
     * @param lisener
     */
    public void getProductList(int type, int page, String id, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        getProductList(type, page, id, "", "", lisener);
    }

    /**
     * 获取商品列表接口
     *
     * @param type    商品类型
     * @param page    页码
     * @param lisener
     */
    public void getProductList(int type, int page, String id, String keyWord, String order, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        getProductList(type, page, id, keyWord, order, "getProductList", lisener);
    }

    /**
     * 获取商品详情接口
     *
     * @param id      商品类型id
     * @param lisener
     */
    public void getProductInfo(String id, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> hashMap = getHashMap(Config.SHOP);
        hashMap.put("id", id);
        hashMap.put("method", "getProductInfo");
        get(createUrlFromParams(Config.SHOP, hashMap), lisener);
    }

    /**
     * 获取首牛商品详情接口
     *
     * @param id      商品类型id
     * @param lisener
     */
    public void getShopMallProductInfo(String id, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> hashMap = getHashMap(Config.SHOP);
        hashMap.put("id", id);
        hashMap.put("method", "getMallproductInfo");
        get(createUrlFromParams(Config.SHOP, hashMap), lisener);
    }
    //    /**
    //     * 获取商品详情接口
    //     *
    //     * @param id      商品类型id
    //     * @param lisener
    //     */
    //    public void getProductInfo(int id, int page, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
    //        Map<String, String> hashMap = getHashMap(Config.SHOP);
    //        hashMap.put("id", String.valueOf(id));
    //        hashMap.put("method", "getProductInfo");
    //        get(createUrlFromParams(Config.SHOP, hashMap), lisener);
    //    }

    /**
     * 商品搜索
     *
     * @param keyWord
     * @param page
     * @param lisener
     */
    public void searchShop(String keyWord, int page, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> map = getHashMap(Config.SHOP_SEARCH);
        map.put("method", "getProductBySearch");
        map.put("keyWord", keyWord);
        map.put("page", String.valueOf(page));
        get(createUrlFromParams(Config.SHOP_SEARCH, map), lisener);
    }

    /**
     * 商品搜索
     *
     * @param keyWord
     * @param page
     * @param lisener
     */
    public void searchShopMall(String keyWord, int page, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> map = getHashMap(Config.SHOP_SEARCH);
        map.put("method", "getMallproductBySearch");
        map.put("keyWord", keyWord);
        map.put("page", String.valueOf(page));
        get(createUrlFromParams(Config.SHOP_SEARCH, map), lisener);
    }

    /**
     * 获取当前绑定的车辆型号
     */
    public void getMyCarTypeList(ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> hashMap = getHashMap(Config.CAR);
        hashMap.put("method", "getMyCarTypeList");
        get(createUrlFromParams(Config.CAR, hashMap), lisener);
    }

    /**
     * 设置当前车辆品牌-配件超市（已测试）
     */
    public void upCarType4Mall(String mark, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> hashMap = getHashMap(Config.CAR);
        hashMap.put("method", "upCarType4Mall");
        hashMap.put("mark", mark);
        post(Config.CAR, hashMap, lisener);
    }

    /**
     * 获取商品类别
     */
    public void getTypeList(ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> map = getHashMap(Config.SHOP_SEARCH);
        map.put("method", "getTypeList");
        get(createUrlFromParams(Config.SHOP_SEARCH, map), lisener);
    }

    /**
     * 获取商城的类别
     *
     * @param lisener
     */
    public void getShopMallTypeList(ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        Map<String, String> map = getHashMap(Config.SHOP_SEARCH);
        map.put("method", "getMalltypeList");
        get(createUrlFromParams(Config.SHOP_SEARCH, map), lisener);
    }


    /**
     * 获取商品列表接口
     * @param type    商品类型
     * @param page    页码
     * @param lisener
     */
    public void getShopMallProductList(int type, int page, String id, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        getShopMallProductList(type, page, id, "", "", lisener);
    }

    /**
     * 获取商品列表接口
     *
     * @param type    商品类型
     * @param page    页码
     * @param lisener
     */
    public void getShopMallProductList(int type, int page, String id, String keyWord, String order, ShouNewApplication.ShouNewHttpCallBackLisener lisener) {
        getProductList(type, page, id, keyWord, order, "getMallproductList", lisener);
    }
}
