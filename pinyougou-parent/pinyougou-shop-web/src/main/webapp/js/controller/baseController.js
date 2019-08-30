app.controller("baseController",function ($scope) {
    $scope.paginationConf = {
        currentPage:1,
        totalItems:66,
        itemsPerPage:5,
        proPageOptions:[5,10,15,20,25,30],
        onChange:function (currPage,pageSize) {
            $scope.reloadList()
        }
    };
    //刷新列表
    $scope.reloadList = function () {
        // $scope.findByPage($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage)
    };

    // selectIds初始化
    $scope.selectIds = [];
    // 选中行添加
    $scope.updateSelection = function (event, id) {
        if (event.target.checked){
            $scope.selectIds.push(id);
        }else {
            var index = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(index);
        }
    };

    //传入的字符串转换成JSON
    $scope.formatJSON=function(jsonStr,key){
        var json = JSON.parse(jsonStr);
        var str = "";
        for (var i = 0; i < json.length; i++) {
            var values = json[i][key];
            if (i==json.length-1){
                str += values;
            } else {
                str += values + ","
            }
            // 	if(i>0){
            // 		str+= ",";
            // 	}
            // 	str += json[i][key];
        }
        return str;
    };

    //从集合中按照key查找
    $scope.searchObjByKey=function (list,key,value) {
        for (var i = 0; i < list.length; i++) {
            if ( list[i][key] == value){
                return list[i];
            }
        }
        return null;
    }
});