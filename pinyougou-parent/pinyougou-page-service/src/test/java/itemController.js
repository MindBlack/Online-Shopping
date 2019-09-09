 //控制层
app.controller('itemController' ,function($scope,itemService){

	//数量操作
	$scope.addNum=function (x) {
		$scope.num=$scope.num+x;
		if ($scope.num<1){
			$scope.num=1;
		}
	}

	//定义记录用户选择的数据
	$scope.specificationItems={};

	//用户选择规格
	$scope.selectSpecification=function (name,value) {
		$scope.specificationItems[name]=value;
		//读取sku
		$scope.searchSku();
	};
	//判断用户是否选择
	$scope.isSelected=function (name, value) {
		if ($scope.specificationItems[name]==value){
			return true;
		}else {
			return false;
		}
	}

	//加载默认的SKU
	$scope.loadSku=function () {
		$scope.sku=skuList[0];
		$scope.specificationItems = JSON.parse(JSON.stringify($scope.sku.spec))
	};

	//方法
	matchObjectEqalse=function(obj1,obj2){
		var length1 = Object.getOwnPropertyNames(obj1).length;
		var length2 = Object.getOwnPropertyNames(obj2).length;
		if (length1!=length2){
			return false;
		}
		for (var k in obj1){
			if (obj1[k]!=obj2[k]){
				return false;
			}
		}
		return true;
	}


	//查询SKU
	$scope.searchSku=function () {
		for (var i = 0; i < skuList.length; i++) {
			if (matchObjectEqalse(skuList[i].spec,$scope.specificationItems)){
				$scope.sku=skuList[i];
				return;
			}
		}
		//没有匹配到
		$scope.sku={id:0,title:'----',price:0}
	}

});
