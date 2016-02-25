'use strict';

angular.module('vidadaApp')
    .controller('MediaPlayerController', function ($scope, $stateParams, Media, media) {

        var type =  media.mediaType.toLowerCase();

        console.log("Playing media type " + type);

        media.isVideo = (type == 'movie');
        media.isImage = (type == 'image');
        media.isSound = (type == 'sound');

        $scope.media = media;
    });
