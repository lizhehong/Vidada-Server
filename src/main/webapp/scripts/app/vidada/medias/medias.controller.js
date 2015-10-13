
'use strict';

angular.module('vidadaApp')
    .controller('MediasController', function ($scope, Media, MediaInfinite) {

        $scope.query = "";
        $scope.someText = "huhu";
        $scope.mediaService = new MediaInfinite("");


        $scope.$watch("query", function(newValue, oldValue) {
            $scope.mediaService = new MediaInfinite(newValue);
            $scope.someText = "Updated media " + newValue;
            $scope.mediaService.nextPage();
        });

        $scope.escape = function(text) {
            return escape(text);
        }

    });
