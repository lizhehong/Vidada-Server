
'use strict';

angular.module('vidadaApp')
    .controller('MediasController', function ($scope, Media, MediaInfinite) {

        $scope.mediaService = new MediaInfinite();

        $scope.escape = function(text) {
            return escape(text);
        }

    });
