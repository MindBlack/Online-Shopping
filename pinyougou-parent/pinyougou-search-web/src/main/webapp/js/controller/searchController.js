app.controller("searchController",function ($scope,searchService) {

    $scope.resultMap={};
    $scope.search=function () {
        searchService.search($scope.searchMap).success(function (response) {

            $scope.resultMap=response;

        })
    }
    
    
});