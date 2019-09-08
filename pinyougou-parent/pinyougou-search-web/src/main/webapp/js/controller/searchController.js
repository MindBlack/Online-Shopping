app.controller("searchController",function ($scope,$location,searchService) {

    // 查询方法,传递查询套件
    $scope.resultMap={};
    $scope.search=function () {
        $scope.searchMap.pageNo=parseInt($scope.searchMap.pageNo);
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap=response;
            //无用,只是显示总记录数和总页数
            $scope.searchMap.total=response.total;
            $scope.searchMap.totalPages=response.totalPages;

            $scope.buildLabel();
        })
    };

    $scope.searchMap={"total":"","totalPages":"","keywords":"","category":"","brand":"","spec":{},"price":
            "","pageNo":1,"pageSize":20,"sort":"","sortFiled":""};

    //添加到面包屑
    $scope.addSearchItem=function (key, value) {
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchMap[key]=value;
        }else {
           $scope.searchMap.spec[key]=value;
        }
        $scope.search(); //执行搜索
    };

    //移除面包屑里面的数据
    $scope.removeSearchItem=function (key) {
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchMap[key]="";
        }else {
            delete $scope.searchMap.spec[key];
        }
        $scope.search(); // 执行搜索
    };

    //构建分页
    $scope.buildLabel=function () {
        $scope.pageLable=[];
        var firstPage=1;
        var lastPage=$scope.resultMap.totalPages;
        $scope.firstDot=true;  //设置有...
        $scope.lastDot=true;   //设置有...
        if (lastPage>5){
            if ($scope.searchMap.pageNo<=3){
                lastPage=5;
                $scope.firstDot=false; //取消...
            }else if ($scope.searchMap.pageNo+2>=lastPage){
                firstPage=lastPage-4;
                $scope.lastDot=false;  //取消...
            }else {
                firstPage=$scope.searchMap.pageNo-2;
                lastPage=$scope.searchMap.pageNo+2;
            }
        }else {
            $scope.firstDot=false;
            $scope.lastDot=false;
        }
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLable.push(i);
        }
    };
    //导航条页码跳转
    $scope.queryByPage=function (pageNo) {
        if (pageNo<1||pageNo>$scope.resultMap.totalPages) {
            return;
        }
        $scope.searchMap.pageNo=pageNo;
        $scope.search();
    };

    //价格排序
    $scope.searchSort=function (sort,sortFiled) {
        $scope.searchMap.sort=sort;
        $scope.searchMap.sortFiled=sortFiled;
        $scope.search();
    };

    //品牌过滤
    $scope.keywordsIsBrand=function () {
        var brands = $scope.resultMap.brandList;
        for (var i = 0; i < brands.length; i++) {
            var brand = brands[i];
            var brandName = brand.text;
            if ($scope.searchMap.keywords.indexOf(brandName)>=0){
                return true;
            }
        }
        return false;
    }

    //首页搜索后的跳转接收参数
    $scope.loadKeywords=function () {
        $scope.searchMap.keywords=$location.search()["keywords"];
        $scope.search()
    }




});