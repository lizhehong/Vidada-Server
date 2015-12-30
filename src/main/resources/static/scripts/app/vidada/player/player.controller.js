'use strict';

angular.module('vidadaApp')
    .controller('MediaPlayerController', function ($scope, $stateParams, Media, media) {
        $scope.media = media;
    });
