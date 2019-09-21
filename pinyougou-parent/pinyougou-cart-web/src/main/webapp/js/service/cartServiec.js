app.service("cartService",function ($http) {

    /**
     * 这是查询当前购物车
     * @returns {*}
     */
    this.findCartList=function () {
        return $http.get("cart/findCartList.do")
    }
    /**
     * 商品数量的改变与删除
     * @param itemId
     * @param num
     * @returns {*}
     */
    this.addGoodsToCartList=function (itemId, num) {
        return $http.get("cart/addGoodsToCartList.do?itemId="+itemId+"&num="+num)
    }
    
    this.sum=function (cartList) {
        var totalValue={totalNum:0,totalMoney:0}
        for (var i = 0; i < cartList.length; i++) {
            var cart = cartList[i];
            for (var j = 0; j < cart.orderItemList.length; j++) {
                var orderItem = cart.orderItemList[i];
                totalValue.totalNum=orderItem.num;
                totalValue.totalMoney=orderItem.totaoFee;
            }
        }
        return totalValue;
        
    }

});