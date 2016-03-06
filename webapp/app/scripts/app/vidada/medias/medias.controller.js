
"use strict";

angular.module("vidadaApp")
    .controller("MediasController", function ($rootScope, $scope, $state, $stateParams, $anchorScroll, $timeout, $mdDialog,
                                              Media, MediaInfinite, ParseText, Tag, ExternalPlayer) {

        $scope.knownTags = [];
        $scope.selectedSuggestion = null;
        $scope.$state = $state;
        $scope.currentMedia = null;
        $scope.tagExpressionCaret = 0; // Current Position of the caret in the tagExpression input

        $scope.availableOrderBy = [
            {id: "TITLE", name: "Title"},
            {id: "OPENED", name: "Times opened"},
            {id: "ADDEDDATE", name: "Date added"},
            {id: "LASTACCESS", name: "Access Date"},
            {id: "RATING", name: "Rating"},
            {id: "SIZE", name: "File size"},
            {id: "DURATION", name: "Duration"},
            {id: "BITRATE", name: "Bitrate"}
        ];


        $scope.mediaQuery = {
            query: $stateParams.query,
            tagExpression: $stateParams.tagExpression,
            orderBy: $scope.availableOrderBy[2],
            reversed: ($stateParams.reversed === "true")
        };

        $scope.scrollToCurrent = function(){
            if($scope.currentMedia != null){
                console.log("Scrolling to " + $scope.currentMedia.id);
                $anchorScroll($scope.currentMedia.id);
            }
        };


        /**
         * Play media in embedded html5 player
         * @param ev Event
         * @param currentMedia MediaItem
         */
        $scope.play = function(ev, currentMedia){

            $scope.currentMedia = currentMedia;

            $mdDialog.show({
                controller: "MediaPlayerController",
                templateUrl: "/scripts/app/vidada/player/player_dialog.html",
                parent: angular.element(document.body),
                targetEvent: ev,
                clickOutsideToClose: true,
                locals : {
                    media : currentMedia
                }
            });

        };

        /**
         * Play media in external player
         * @param media
         */
        $scope.playExternal = function(media){

            $scope.currentMedia = media;

            if(media.mediaType.toLowerCase() == "movie"){
                // Open videos with the current external player using URL schemes
                var externalPlayUrl = ExternalPlayer.externPlayUrl(media.streamUrl);
                window.open(externalPlayUrl, "_self");
            }else{
                //window.open(media.streamUrl);
                $scope.play(null, media); // Fallback to embedded Player
            }
        };

        $scope.showDetail = function(ev, currentMedia){

            $scope.currentMedia = currentMedia;

            $mdDialog.show({
                controller: 'MediasDetailController',
                templateUrl: '/scripts/app/vidada/medias/medias.detail_dialog.html',
                parent: angular.element(document.body),
                targetEvent: ev,
                clickOutsideToClose:true,
                locals : {
                    myMedia : currentMedia
                }
            });
        };


        $scope.updateMedias= function(){

            console.log("Query: " + JSON.stringify($scope.mediaQuery));

            $scope.mediaService = new MediaInfinite($scope.mediaQuery);
            $scope.mediaService.nextPage();
        };


        $scope.setOrderBy = function(option){
            $scope.mediaQuery.orderBy = option;
            $scope.updateMedias();
        };

        $scope.$on('$viewContentLoaded', function(event){
            if($state.current.name === 'medias') {
                $timeout($scope.scrollToCurrent, 10);
            }
        });



        var getCaretPosition = function(oField) {

            // Initialize
            var iCaretPos = 0;

            // IE Support
            if (document.selection) {

                // Set focus on the element
                oField.focus ();

                // To get cursor position, get empty selection range
                var oSel = document.selection.createRange ();

                // Move selection start to 0 position
                oSel.moveStart ("character", -oField.value.length);

                // The caret position is selection length
                iCaretPos = oSel.text.length;
            }

            // Firefox support
            else if (oField.selectionStart || oField.selectionStart == '0')
                iCaretPos = oField.selectionStart;

            return iCaretPos;
        };


        $scope.updateCursorPos = function($event) {
            var myEl = $event.target;
            $scope.tagExpressionCaret = getCaretPosition(myEl);
        };



        $scope.updateTags = function() {
            Tag.query().$promise.then(function (tags) {
                $scope.knownTags = tags;
                console.log("Got " + tags.length + " known Tags!");
            });
        };


        function findSuggestions(term){
            var q = term.toLowerCase().trim();
            var results = [];

            for (var i = 0; i < $scope.knownTags.length && results.length < 10; i++) {
                var genre = $scope.knownTags[i].name;
                if (genre.toLowerCase().indexOf(q) === 0)
                    results.push({ label: genre, value: genre });
            }
            return results;
        }

        $scope.suggest_tags =  function(tagExpression) {

            var word = ParseText.findWordAt(tagExpression, $scope.tagExpressionCaret);

            console.log("Searching suggestions for word: '" + word + "' ...");

            var suggestions = findSuggestions(word.text);

            suggestions.forEach(function (s) {
                s.value = ParseText.replaceWord(tagExpression, word, s.value);
            });

            console.log("Found " + suggestions.length + " suggestions for  '" + word.text + "'!");

            return suggestions;
        };


        $scope.updateTags();
        $scope.updateMedias();
    });
