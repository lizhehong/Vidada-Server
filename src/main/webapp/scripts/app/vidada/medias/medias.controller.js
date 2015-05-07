
'use strict';

angular.module('vidadaApp')
    .controller('MediasController', function ($scope, Media) {

        $scope.medias = [];

        $scope.updateMedias = function() {
            Media.get().$promise.then(function (mediaPage) {
                $scope.medias = mediaPage.pageItems;
            });
        };

        $scope.updateMedias();

    });
