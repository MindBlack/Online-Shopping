app.service("brandService",function ($http) {
    this.findAll=function () {
        return $http.get("../brand/findAll.do");
    };
    this.search=function (currPage,pageSize,searchEntity) {
        return $http.post("../brand/search.do?currPage="+currPage+"&pageSize="+pageSize,searchEntity);
    };
    this.findOne=function (id) {
        return $http.get("../brand/findOne.do?id="+id);
    };
    this.drop=function (selectIds) {
        return $http.get("../brand/delete.do?ids="+selectIds)
    };
    this.add=function (entity) {
        return $http.post("../brand/add.do",entity);
    };
    this.update=function (entity) {
        return $http.post("../brand/update.do",entity);
    };
    this.findByPage=function (currPage, pageSize) {
        return $http.post("../brand/findByPage.do?currPage="+currPage+"&pageSize="+pageSize);
    };
    this.selectBrandList=function () {
        return $http.post("../brand/selectBrandList.do");
    };

});