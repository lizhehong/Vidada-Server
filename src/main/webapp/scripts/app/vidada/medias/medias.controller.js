
'use strict';

angular.module('vidadaApp')
    .controller('MediasController', function ($scope, Media, MediaInfinite) {


        $scope.mediaService = new MediaInfinite();


        /*
        $scope.mediaIndex = 0;
        $scope.medias = [];

        $scope.updateMedias = function() {
            Media.get().$promise.then(function (mediaPage) {
                $scope.medias = mediaPage.pageItems;
            });
        };

        $scope.updateMedias();
        */

    });
