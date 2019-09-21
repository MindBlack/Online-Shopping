//服务层
app.service('loginService',function($http){
	    	
	//获取登录的用户名
	this.showName=function () {
		return $http.get("user/loginName.do")
	}

});
