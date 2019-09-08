 //控制层 
app.controller('contentController' ,function($scope,$location,contentService){


	$scope.contentList=[];
	$scope.findByContentId=function (categoryId) {
		contentService.findByContentId(categoryId).success(function (resopnse) {
			$scope.contentList[categoryId]=resopnse;
		})
	}

	//页面跳转方法
	$scope.search=function () {
		location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
	}

});	
