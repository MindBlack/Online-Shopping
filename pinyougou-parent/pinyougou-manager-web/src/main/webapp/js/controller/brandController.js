app.controller("brandController",function ($scope, $controller,brandService) {

    //继承
    $controller("baseController",{$scope:$scope});

    //查询所有
    $scope.findAll=function () {
        brandService.findAll().success(function (resposse) {
            $scope.list=resposse;
        })
    };

    //模糊条件分页
    $scope.searchEntity={};
    $scope.search=function(currPage,pageSize){
        brandService.search(currPage,pageSize,$scope.searchEntity).success(function (brandPage) {
            $scope.list=brandPage.rows;
            $scope.paginationConf.totalItems=brandPage.total;
        })
    };
    //分页
    $scope.findByPage = function (currPage, pageSize) {
        brandService.findByPage(currPage,pageSize).success(function (brandPage) {
            $scope.list=brandPage.rows;
            $scope.paginationConf.totalItems = brandPage.total;
        })
    };
    //页面数据添加或修改
    $scope.save = function () {
        var service;
        if ($scope.entity.id == null) {
            service=brandService.add($scope.entity);
        }else {
            service=brandService.update($scope.entity);
        }
        service.success(function (request) {
            if (request.success){
                $scope.reloadList();
            }else {
                alert(request.message);
            }
        })
    };
    //根据id查询一条数据
    $scope.findOne = function (id) {
        brandService.findOne(id).success(function (request) {
            $scope.entity=request;
        })
    };

    //删除选中
    $scope.drop=function () {
        brandService.drop($scope.selectIds).success(function (respones) {
            if (respones.success){
                $scope.reloadList();
            } else {
                alert(request.message);
            }
            $scope.selectIds=[];
        })
    }
});