package com.shownew.home.module;

import android.text.TextUtils;

import com.shownew.home.Config;
import com.shownew.home.ShouNewApplication;

import java.io.File;
import java.util.ArrayList;
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
     *
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

    /**
     * 商品收藏
     *
     * @param type                       0  首牛商城    1   车配超市
     * @param shopId
     * @param collect
     * @param shouNewHttpCallBackLisener
     */
    public void collection(int type, String shopId, String collect, ShouNewApplication.ShouNewHttpCallBackLisener shouNewHttpCallBackLisener) {
        Map<String, String> map = getHashMap(Config.SHOP_COLLECT);
        map.put("method", collect);
        map.put(0 == type ? "mallproductId" : "productId", shopId);
        post(Config.SHOP_COLLECT, map, shouNewHttpCallBackLisener);
    }

    /**
     * 获取收藏的商品
     *
     * @param shouNewHttpCallBackLisener
     */
    public void getCollectData(int page, ShouNewApplication.ShouNewHttpCallBackLisener shouNewHttpCallBackLisener) {
        Map<String, String> map = getHashMap(Config.SHOP_COLLECT);
        map.put("method", "getCollectionList");
        map.put("page", String.valueOf(page));
        get(createUrlFromParams(Config.SHOP_COLLECT, map), shouNewHttpCallBackLisener);
    }

    /**
     * 商品评论
     *
     * @param dicussContent
     * @param dPdgrade
     * @param dSvgrade
     * @param dLsgrade
     * @param files
     * @param shouNewHttpCallBackLisener
     */
    public void discuss(String id, String dicussContent, int dPdgrade, int dSvgrade, int dLsgrade, String files, ShouNewApplication.ShouNewHttpCallBackLisener shouNewHttpCallBackLisener) {
        Map<String, String> map = getHashMap(Config.Shop_DISCUSS);
        map.put("method", "discuss");
        map.put("dPdgrade", String.valueOf(dPdgrade));
        map.put("dSvgrade", String.valueOf(dSvgrade));
        map.put("dLsgrade", String.valueOf(dLsgrade));
        map.put("dText", dicussContent);
        map.put("id", id);
        map.put("dImg", files);
        post(Config.Shop_DISCUSS, map, shouNewHttpCallBackLisener);
    }

    /**
     * 获取上次评价的内容
     *
     * @param oId
     * @param shouNewHttpCallBackLisener
     */
    public void zuijiaEvaluete(String oId, ShouNewApplication.ShouNewHttpCallBackLisener shouNewHttpCallBackLisener) {
        Map<String, String> map = getHashMap(Config.Shop_DISCUSS);
        map.put("method", "getRootDiscuss");
        map.put("id", oId);
        get(createUrlFromParams(Config.Shop_DISCUSS, map), shouNewHttpCallBackLisener);
    }

    /**
     * 上传评论图片
     *
     * @param files
     * @param shouNewHttpCallBackLisener
     */
    public void upTalkImg(ArrayList<File> files, ShouNewApplication.ShouNewHttpCallBackLisener shouNewHttpCallBackLisener) {
        Map<String, String> map = getHashMap(Config.Shop_DISCUSS);
        map.put("method", "uploadImg");
        postFiles(Config.Shop_DISCUSS, map, "img", files, shouNewHttpCallBackLisener);
    }

    /**
     * 再次评价
     *
     * @param did
     * @param againTalkStr
     * @param dImg
     * @param shouNewHttpCallBackLisener
     */
    public void againEvaluetes(String did, String againTalkStr, String dImg, ShouNewApplication.ShouNewHttpCallBackLisener shouNewHttpCallBackLisener) {
        Map<String, String> map = getHashMap(Config.Shop_DISCUSS);
        map.put("method", "addDiscuss");
        map.put("id", did);
        map.put("dText", againTalkStr);
        map.put("dImg", dImg);
        post(Config.Shop_DISCUSS, map, shouNewHttpCallBackLisener);
    }

    /**
     * 获取 评价列表
     * @param producttype
     * @param productId
     * @param type
     * @param page
     * @param shouNewHttpCallBackLisener
     */
    public void getDiscussList(String producttype, String productId, String type, int page, ShouNewApplication.ShouNewHttpCallBackLisener shouNewHttpCallBackLisener) {
        Map<String, String> map = getHashMap(Config.Shop_DISCUSS);
        map.put("method", "getDiscussList");
        map.put(producttype, productId);
        map.put("type", String.valueOf(type));
        map.put("page", String.valueOf(page));
        get(createUrlFromParams(Config.Shop_DISCUSS, map), shouNewHttpCallBackLisener);

    }

    /**
     * 点赞借口
     * @param dId
     * @param shouNewHttpCallBackLisener
     */
    public void fabulous(long dId, ShouNewApplication.ShouNewHttpCallBackLisener shouNewHttpCallBackLisener) {
        Map<String, String> map = getHashMap(Config.Shop_DISCUSS);
        map.put("method", "upNiceCount");
        map.put("id", String.valueOf(dId));
        post(Config.Shop_DISCUSS, map, shouNewHttpCallBackLisener);
    }

    /**
     * 更新购物车
     * @param data
     * @param shouNewHttpCallBackLisener
     */
    public void updateShopCar(String data, ShouNewApplication.ShouNewHttpCallBackLisener shouNewHttpCallBackLisener) {

        Map<String, String> map = getHashMap(Config.SHOP_CAR);
        map.put("method", "insert");
        map.put("data", data);
        post(Config.SHOP_CAR, map, shouNewHttpCallBackLisener);
    }

    /**
     * 获取购物车列表
     * @param shouNewHttpCallBackLisener
     */
    public void getShopcarList( ShouNewApplication.ShouNewHttpCallBackLisener shouNewHttpCallBackLisener) {
        Map<String, String> map = getHashMap(Config.SHOP_CAR);
        map.put("method", "getShopcarList");
        get(createUrlFromParams(Config.SHOP_CAR, map), shouNewHttpCallBackLisener);
    }

    /**
     * 收藏夹
     * @param data
     * @param shouNewHttpCallBackLisener
     */
    public void updateShopCart(String data, ShouNewApplication.ShouNewHttpCallBackLisener shouNewHttpCallBackLisener) {
        Map<String, String> map = getHashMap(Config.SHOP_CAR);
        map.put("method", "update");
        map.put("data", data);
        post(Config.SHOP_CAR, map, shouNewHttpCallBackLisener);
    }

    /**
     * 删除购物车
     * @param shId
     * @param shouNewHttpCallBackLisener
     */
    public void deleteShopCar(String shId, ShouNewApplication.ShouNewHttpCallBackLisener shouNewHttpCallBackLisener) {
        Map<String, String> map = getHashMap(Config.SHOP_CAR);
        map.put("method", "delete");
        map.put("data", shId);
        post(Config.SHOP_CAR, map, shouNewHttpCallBackLisener);
    }
}
