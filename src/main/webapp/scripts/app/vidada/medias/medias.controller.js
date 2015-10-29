
'use strict';

angular.module('vidadaApp')
    .controller('MediasController', function ($scope, Media, MediaInfinite, ParseText, Tag) {

        $scope.selectedSuggestion = null;

        $scope.mediaQuery = {
            query: "",
            tagExpression: "",
            orderBy: {name: "Choose some"},
            reversed: false
        };

        $scope.mediaService = new MediaInfinite($scope.mediaQuery);
        $scope.tagExpressionCaret = 0; // Current Position of the caret in the tagExpression input
        $scope.availableOrderBy = [
            {id: "FILENAME", name: "Name"},
            {id: "OPENED", name: "Times opened"},
            {id: "ADDEDDATE", name: "Date added"},
            {id: "LASTACCESS", name: "Access Date"},
            {id: "RATING", name: "Rating"},
            {id: "SIZE", name: "File size"},
            {id: "DURATION", name: "Duration"},
            {id: "BITRATE", name: "Bitrate"},
        ];


        $scope.play = function(media){
            var mediaUrlArg = escape(media.streamUrl);
            window.open('mpv://' + mediaUrlArg);
        };

        $scope.updateMedias= function(){
            $scope.mediaService = new MediaInfinite($scope.mediaQuery);
            $scope.mediaService.nextPage();
        };


        $scope.setOrderBy = function(option){
            $scope.mediaQuery.orderBy = option;
            $scope.updateMedias();
        };


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
                oSel.moveStart ('character', -oField.value.length);

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

        $scope.knownTags = [];

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

            console.log("Current tag: " + word);

            var suggestions = findSuggestions(word.text);

            suggestions.forEach(function (s) {
                s.value = ParseText.replaceWord(tagExpression, word, s.value);
            });

            console.log("Found " + suggestions.length + " suggestions for '" + word.text + "'!");

            return suggestions;
        };




        $scope.updateTags();
    });
