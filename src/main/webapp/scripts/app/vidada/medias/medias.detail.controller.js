'use strict';

angular.module('vidadaApp')
    .controller('MediasDetailController', function ($scope, $stateParams, Media) {

        $scope.mediaId = $stateParams.mediaId;
        $scope.media = null;
        $scope.mediaDump = {};

        $scope.loadMedia = function() {

            if (/\S/.test($scope.mediaId)) {
                Media.get({id : $scope.mediaId}).$promise.then(function (media) {

                    media.addedDate = new Date(media.addedDate);
                    //media.lastAccessedDate = new Date(media.lastAccessedDate);
                    media.sampleDateIso = new Date(media.sampleDateIso);


                    $scope.media = media;
                    $scope.mediaDump = JSON.stringify(media, null, 2);
                }, function(err){
                    console.log("Failed to fetch media " + $scope.mediaId);
                });
            }



        };

        $scope.loadMedia();

    });
