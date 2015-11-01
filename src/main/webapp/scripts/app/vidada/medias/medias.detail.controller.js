'use strict';

angular.module('vidadaApp')
    .controller('MediasDetailController', function ($scope, $stateParams, Media) {

        $scope.mediaId = $stateParams.mediaId;
        $scope.media = null;
        $scope.mediaDump = {};

        $scope.loadMedia = function() {
            Media.get({id : $scope.mediaId}).$promise.then(function (media) {
                $scope.media = media;
                $scope.mediaDump = JSON.stringify(media, null, 2);
            }, function(err){
                console.log("Failed to fetch media " + $scope.mediaId);
            });
        };

        $scope.loadMedia();

    });
