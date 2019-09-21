package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.entity.Result;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.utils.CookieUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request,HttpServletResponse response) {
        List<Cart> cartList;
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //未登录
        String cartListStr = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if (cartListStr == null || cartListStr.equals("")) {
            cartList = new ArrayList();
        } else {
            cartList = JSON.parseArray(cartListStr, Cart.class);
        }
        if (username.equals("anonymousUser")) {
            return cartList;
        } else {
            //已经登录  从redis中读取
            List<Cart> redisCartList = cartService.findCartListFromRedis(username);
            if (cartList.size()>0){
                //合并数据
                redisCartList = cartService.mergeCartList(cartList,redisCartList);
                //清除cookie中的数据
                CookieUtil.deleteCookie(request,response,"cartList");
                //将rides中的数据进行更新
                cartService.addCartListToRedis(redisCartList,username);
            }
            return redisCartList;
        }

    }

    /**
     * 添加商品到购物车
     *
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response) {
        /*允许请求并携带参数*/
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录用户:" + username);
        try {
            //获得购物车列表
            List<Cart> cartList = findCartList(request,response);
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            if (username.equals("anonymousUser")) {
                //用户未登录
                CookieUtil.setCookie(request, response, "cartList",
                        JSON.toJSONString(cartList), 3600 * 24, "UTF-8");
                System.out.println("向cookie存入数据");
            } else {
                //用户登录
                cartService.addCartListToRedis(cartList,username);
                System.out.println("保存到redis中");
            }
            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }

}
