app.controller("cartController",function ($scope,cartService) {

    /**
     * 查询购物车,当页面初始化的时候
     */
    $scope.findCartList=function () {
        cartService.findCartList().success(function (resoponse) {
            $scope.cartList=resoponse;
            $scope.totalValue = cartService.sum($scope.cartList);
        })
    };
    /**
     * 商品数量的添加与删除
     * @param itemId
     * @param num
     */
    $scope.addGoodsToCartList=function (itemId, num) {
        cartService.addGoodsToCartList(itemId,num).success(function (response) {
            if (response.success){
                //调用方法刷新页面数据
                $scope.findCartList();
            }else{
                //弹出提示信息
                alert(response.message);
            }
        })
    }



});