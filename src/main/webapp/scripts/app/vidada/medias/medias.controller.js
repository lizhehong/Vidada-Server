
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
        };

        // Handles scroll events
        var scrollHandler = function() {
            var isOnBottom = $(window).scrollTop() == ($(document).height() - $(window).height());
            if(isOnBottom){
                // We reached the bottom of the page ...
                $scope.$apply(function(){
                    // .. time to load more data!
                    $scope.mediaService.nextPage();
                });
            }
        };

        $(function(){
            $(document).scroll(scrollHandler);
        });

        $scope.$on('$destroy', function() {
            // Clean up resources here
            $(document).off("scroll", scrollHandler); // Remove the jquery scroll handler
        });

    });
