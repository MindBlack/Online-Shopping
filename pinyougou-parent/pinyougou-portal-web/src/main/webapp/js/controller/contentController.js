 //控制层 
app.controller('contentController' ,function($scope,contentService){


	$scope.contentList=[];
	$scope.findByContentId=function (categoryId) {
		contentService.findByContentId(categoryId).success(function (resopnse) {
			$scope.contentList[categoryId]=resopnse;
		})
	}


});	
