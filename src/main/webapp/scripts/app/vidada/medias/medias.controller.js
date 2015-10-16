
'use strict';

angular.module('vidadaApp')
    .controller('MediasController', function ($scope, Media, MediaInfinite) {

        $scope.query = "";
        $scope.tagExpression="";
        $scope.mediaService = new MediaInfinite("", "");


        $scope.$watch("query", function(newValue, oldValue) {
            $scope.updateMedias();
        });

        $scope.$watch("tagExpression", function(newValue, oldValue) {
            $scope.updateMedias();
        });

        $scope.updateMedias= function(){
            $scope.mediaService = new MediaInfinite($scope.query, $scope.tagExpression);
            $scope.mediaService.nextPage();
        };

        $scope.escape = function(text) {
            return escape(text);
        };
    });
