app.service('fileService',function($http){

    this.uploadFile=function () {
        var formData = new FormData();
        formData.append("upload",document.getElementById("file").files[0]);

        return $http({
            url:"../upload.do",
            method:"post",
            data:formData,
            headers:{"Content-Type":undefined},
            transformRequest:angular.identify
        })
    };
});