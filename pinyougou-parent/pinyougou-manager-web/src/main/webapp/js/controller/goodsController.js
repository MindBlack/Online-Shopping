 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,goodsService,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){
		var id = $location.search()["id"];
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				//向富文本编辑器回传数据
				editor.html($scope.entity.goodsDesc.introduction);
				//数据回显时图片处理
				$scope.entity.goodsDesc.itemImages=JSON.parse(response.goodsDesc.itemImages);
				//扩展属性回显
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse(response.goodsDesc.customAttributeItems);
				//回显的specificationItems转json
				$scope.entity.goodsDesc.specificationItems=JSON.parse(response.goodsDesc.specificationItems);
				//回显规格属性转json
				var skuList = $scope.entity.itemList;
				for (var i = 0; i < skuList.length; i++) {
					var sku = skuList[i];
					sku.spec = JSON.parse(sku.spec);
				}

			}
		);				
	}

	//定义entity
	$scope.entity = {goods: {}, goodsDesc: {itemImages: [], specificationItems: []}, itemList: {}};
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	$scope.categoryId=[];
	$scope.findItemCat=function(){
		itemCatService.findAll().success(function (response) {
			for (var i = 0; i < response.length; i++) {
				var itemCat = response[i];
				var id = itemCat.id;
				var name = itemCat.name;
				$scope.categoryId[id]=name;
			}
		})
	};

	$scope.status=["未审核","已审核","审核未通过","关闭"];

	/**
	 * 根据id页面跳转的方法
	 * @param id
	 */
	$scope.jumpHtml=function (id) {
		alert(id);
		location.href="goods_edit.html#?id="+id;
	};


	//一级分类查询
	$scope.selectItemCat1List = function () {
		itemCatService.findByParentId(0).success(function (response) {
			$scope.itemCat1 = response;
		})
	};

	//二级分类查询
	$scope.$watch("entity.goods.category1Id", function (newValue, oldvalue) {
		itemCatService.findByParentId(newValue).success(function (response) {
			$scope.itemCat2 = response;
		})
	});
	//三级分类查询
	$scope.$watch("entity.goods.category2Id", function (newValue, oldvalue) {
		itemCatService.findByParentId(newValue).success(function (response) {
			$scope.itemCat3 = response;
		})
	});
	//四级分类查询
	$scope.$watch("entity.goods.category3Id", function (newValue, oldvalue) {
		itemCatService.findOne(newValue).success(function (response) {
			$scope.entity.goods.typeTemplateId = response.typeId;
		})
	});

	//当模板id改变时发生的联动
	$scope.$watch("entity.goods.typeTemplateId", function (newValue, oldvalue) {
		typeTemplateService.findOne(newValue).success(function (response) {
			$scope.typeTemplate = response;   //模板对象
			$scope.typeTemplate.brandIds = JSON.parse(response.brandIds);
			/**
			 * 获取模板扩展属性
			 * 解决方案,判断是否是更新操作
			 * 如果不是执行以下代码
			 * @type {any}
			 */
			var id = $location.search().id;
			if (id==null){
				$scope.entity.goodsDesc.customAttributeItems = JSON.parse(response.customAttributeItems);
			}
		});
		typeTemplateService.findSpecByTypeId(newValue).success(function (response) {
			$scope.specList = response;
		});
	});

	//勾选回显处理
	$scope.updateChecked=function (specName, optionName) {
		var obj = $scope.searchObjByKey($scope.entity.goodsDesc.specificationItems,"attributeName",specName);
		if (obj!=null){
			var attributeValue = obj.attributeValue;
			var index = attributeValue.indexOf(optionName);
			return index>=0;
		}
		return false;
	}

	//审核
	$scope.updateStatus=function (auditStatus) {
		goodsService.updateStatus($scope.selectIds,auditStatus).success(function (response) {
			if (response.success){
				$scope.reloadList();//刷新列表
				$scope.selectIds=[];
			} 	else {
				alert(response.message);
			}

		})
	}


});	
