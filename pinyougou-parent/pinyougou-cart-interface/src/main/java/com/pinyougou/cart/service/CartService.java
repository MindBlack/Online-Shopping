package com.pinyougou.cart.service;

import com.pinyougou.pojogroup.Cart;

import java.util.List;

/**
 * 购物车服务层接口
 */
public interface CartService {
    /**
     * 添加商品到购物车
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    public List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);

    /**
     * 从redis中查询购物车
     * @param username
     * @return
     */
    public List<Cart> findCartListFromRedis(String username);

    /**
     * 添加购物车到Redis中
     * @param cartList
     * @param username
     * @return
     */
    public void addCartListToRedis(List<Cart> cartList,String username);

    /**
     * 合并购物车
     * @param cookieCartList
     * @param redisCartList
     * @return
     */
    public List<Cart> mergeCartList(List<Cart> cookieCartList,List<Cart> redisCartList);

}
