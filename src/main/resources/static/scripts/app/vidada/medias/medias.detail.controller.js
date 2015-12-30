'use strict';

angular.module('vidadaApp')
    .controller('MediasDetailController', function ($scope, $stateParams, Media, $mdDialog, myMedia) {

        $scope.enableWatch = false;
        $scope.media = null;
        $scope.thumbnailPositionEdit = 0;
        $scope.tagsEdit = [];
        $scope.mediaDump = {};
        $scope.thumbnailResource = null;


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

            var type =  media.mediaType.toLowerCase();
            $scope.media.isVideo = (type == 'movie');
            $scope.media.isImage = (type == 'image');
            $scope.media.isSound = (type == 'sound');

            $scope.thumbnailPositionEdit = media.thumbnailPosition;
            $scope.tagsEdit = media.tags;
            $scope.thumbnailResource = media.thumbnailResource;

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

                    console.log("Updating thumb position to " + $scope.thumbnailPositionEdit);

                    $scope.media.thumbnailPosition = $scope.thumbnailPositionEdit;
                    $scope.thumbnailResource = null;
                    // Submit the new thumb position to the server
                    Media.update($scope.media, function () {
                        $scope.awaitNewThumbnail();
                    },function(error){
                        // Failed to update position
                        console.log("Failed to update thumb position! " + JSON.stringify(error));
                    });
                }
            }, 200);
        });


        /**
         * Waits until a thumbnail is available.
         */
        $scope.awaitNewThumbnail = function (timeoutAfterFail) {


            if(!$scope.mediaId) {
                console.log("Cant await thumbnails since id of this media is unknown!")
                return;
            }

            if (timeoutAfterFail === undefined) timeoutAfterFail = 100;

            Media.get({id: $scope.mediaId}).$promise.then(function (media) {

                if (media.thumbnailResource && media.thumbnailResource.state == 'Ready') {
                    // New thumb ready
                    $scope.media.thumbnailResource = media.thumbnailResource;
                    $scope.thumbnailResource = media.thumbnailResource;
                } else {
                    // The thumbnail is not yet ready
                    console.log("The thumbnail is not yet ready. " + JSON.stringify(media));
                    setTimeout(function () {
                        // Wait a moment and then try again
                        $scope.awaitNewThumbnail(timeoutAfterFail + 50);
                    }, timeoutAfterFail);
                }
            }, function (error) {
                console.log("Failed to fetch media for thumb update! " + JSON.stringify(error));
            });
        };

        $scope.cancel = function () {
            $mdDialog.cancel();
        };


        if (myMedia) {
            $scope.mediaId = myMedia.id;
            $scope.onMediaLoaded(myMedia);
        }else{
            $scope.mediaId = $stateParams.mediaId;
            $scope.loadMedia();
        }
    });
