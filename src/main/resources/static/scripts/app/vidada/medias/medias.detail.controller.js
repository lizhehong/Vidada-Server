'use strict';

angular.module('vidadaApp')
    .controller('MediasDetailController', function ($scope, $stateParams, Media, $mdDialog, myMedia) {

        $scope.media = null;
        $scope.thumbnailPositionEdit = 0;
        $scope.tagsEdit = [];
        $scope.mediaDump = {};


        $scope.loadMedia = function () {

            if (/\S/.test($scope.mediaId)) {
                Media.get({id: $scope.mediaId}).$promise.then(function (media) {
                    $scope.onMediaLoaded(media);
                }, function (err) {
                    console.log("Failed to fetch media " + $scope.mediaId);
                });
            }
        };

        $scope.onMediaLoaded = function (media) {
            $scope.enableWatch = false;
            $scope.media = media;

            $scope.thumbnailPositionEdit = media.thumbnailPosition;
            $scope.tagsEdit = media.tags;

            $scope.mediaDump = JSON.stringify(media, null, 2);
            $scope.enableWatch = true;
        };

        $scope.isFavorite = function () {
            return $scope.media && $scope.media.rating >= 5;
        };

        $scope.toggleFavorite = function () {
            if ($scope.isFavorite()) {
                $scope.media.rating = 0;
            } else {
                $scope.media.rating = 5;
            }

            Media.update($scope.media, function () {
                // Updated!
            });
        };


        $scope.$watch('thumbnailPositionEdit', function (tmpStr) {
            if (!$scope.enableWatch || !tmpStr)
                return 0;
            setTimeout(function () {

                // if thumbnailPosition is still the same..
                // go ahead and update thumbnailPosition
                if (tmpStr === $scope.thumbnailPositionEdit) {
                    $scope.media.thumbnailPosition = $scope.thumbnailPositionEdit;
                    $scope.media.thumbnailResource = null;
                    // Submit the new thumb position to the server
                    Media.update($scope.media, function () {
                        $scope.awaitNewThumbnail();
                    });
                }
            }, 200);
        });


        $scope.awaitNewThumbnail = function () {

            Media.get({id: $scope.mediaId}).$promise.then(function (media) {
                if (media.thumbnailResource.state == 'Ready') {
                    // New thumb ready
                    $scope.media.thumbnailResource = media.thumbnailResource;
                    //$scope.loadMedia();
                } else {

                    setTimeout(function () {

                        // Wait a sec and then try again
                        $scope.awaitNewThumbnail();
                    }, 100);
                }
            }, function () {
                console.log("Failed to fetch media for thumb update!");
            });
        };

        $scope.cancel = function () {
            $mdDialog.cancel();
        };


        if (myMedia) {
            $scope.mediaId = myMedia.Id;
            $scope.onMediaLoaded(myMedia);
        }else{
            $scope.mediaId = $stateParams.mediaId;
            $scope.loadMedia();
        }
    });
