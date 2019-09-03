//服务层
app.service('contentService',function($http){

	//根据广告分类id查询数据
	this.findByContentId=function (categoryId) {
		return $http.get("../content/findByContentId.do?categoryId="+categoryId);
	}
});
