'use strict';

angular.module('vidadaApp')
    .controller('MediasDetailController', function ($scope, $stateParams, Media) {

        $scope.mediaId = $stateParams.mediaId;
        $scope.media = null;
        $scope.enableWatch = false;


        $scope.mediaDump = {};

        $scope.loadMedia = function() {

            if (/\S/.test($scope.mediaId)) {
                Media.get({id : $scope.mediaId}).$promise.then(function (media) {
                    $scope.enableWatch = false;
                    $scope.media = media;
                    $scope.mediaDump = JSON.stringify(media, null, 2);
                    $scope.enableWatch = true;
                }, function(err){
                    console.log("Failed to fetch media " + $scope.mediaId);
                });
            }
        };


        $scope.$watch('media.thumbnailPosition', function (tmpStr)
        {
            if (!$scope.enableWatch || !tmpStr || tmpStr.length == 0)
                return 0;
            setTimeout(function() {

                // if thumbnailPosition is still the same..
                // go ahead and update thumbnailPosition
                if (tmpStr === $scope.media.thumbnailPosition)
                {
                    $scope.updateMedia();
                }
            }, 200);
        });

        /** Update the media on the server */
        $scope.updateMedia = function(){
            // Submit the new thumb position to the server
            Media.update($scope.media, function () {
                // Updated successfully
                console.log("Updated media successfully!");
                // Reload the media data to get the new thumb link
                $scope.loadMedia();
            }, function(err){
                console.log("Failed to update media!");
            });
        };

        $scope.loadMedia();

    });
