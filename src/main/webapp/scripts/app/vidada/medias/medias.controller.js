
'use strict';

angular.module('vidadaApp')
    .controller('MediasController', function ($scope, Media, MediaInfinite, ParseText) {

        $scope.query = "";
        $scope.tagExpression="";
        $scope.mediaService = new MediaInfinite("", "");
        $scope.dirty = {};
        $scope.tagExpressionCaret = 0; // Current Position of the caret in the tagExpression input


        $scope.updateMedias= function(){
            $scope.mediaService = new MediaInfinite($scope.query, $scope.tagExpression);
            $scope.mediaService.nextPage();
        };

        $scope.escape = function(text) {
            return escape(text);
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



        // TODO Use tags from AJAX

        var genres = ['action', 'comedy', 'drama', 'comic', 'horror', '720p', '1080p' /* ... */ ];

        function findSuggestions(term){
            var q = term.toLowerCase().trim();
            var results = [];

            // Find first 10 states that start with `term`.
            for (var i = 0; i < genres.length && results.length < 10; i++) {
                var genre = genres[i];
                if (genre.toLowerCase().indexOf(q) === 0)
                    results.push({ label: genre, value: genre });
            }
            return results;
        }

        function suggest_tag(tagExpression) {

            var word = ParseText.findWordAt(tagExpression, $scope.tagExpressionCaret);

            console.log("Current tag: " + word);

            var suggestions = findSuggestions(word.text);

            suggestions.forEach(function (s) {
                s.value = ParseText.replaceWord(tagExpression, word, s.value);
            });

            console.log("Found " + suggestions.length + " suggestions for '" + word.text + "'!");

            return suggestions;
        }


        $scope.autocomplete_options = {
            suggest: suggest_tag
        };


    });
