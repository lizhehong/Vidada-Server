
'use strict';

angular.module('vidadaApp')
    .controller('MediasController', function ($scope, Media, MediaInfinite) {

        $scope.query = "";
        $scope.tagExpression="";
        $scope.mediaService = new MediaInfinite("", "");

        $scope.dirty = {};

        $scope.tagExpressionCaret = 0;


        $scope.$watch("query", function(newValue, oldValue) {
            $scope.updateMedias();
        });

        $scope.$watch("tagExpression", function(newValue, oldValue) {
            $scope.updateMedias();
        });

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

            var word = findWordAt(tagExpression, $scope.tagExpressionCaret);

            console.log("Current tag: " + word);

            var suggestions = findSuggestions(word.text);

            suggestions.forEach(function (s) {
                s.value = replaceWord(tagExpression, word, s.value);
            });

            console.log("Found " + suggestions.length + " suggestions for '" + word.text + "'!");

            return suggestions;
        }


        $scope.autocomplete_options = {
            suggest: suggest_tag
        };


        /**
         *
         * @param str
         * @param word
         * @param replacement
         * @returns {*}
         */
        function replaceWord(str, word, replacement) {
            return str.slice(0, word.left) + replacement + str.slice(word.right, str.length);
        }

        /**
         * Finds the word in the given string at the given position
         * @param str
         * @param pos
         * @returns {{left: Number, right: Number, text: string}}
         */
        function findWordAt(str, pos) {

            // Perform type conversions.
            str = String(str);
            pos = Number(pos) >>> 0;

            // Search for the word's beginning and end.
            var left = str.slice(0, pos + 1).search(/[\w]+$/),
                right = str.slice(pos).search(/[^\w]/);

            // The last word in the string is a special case.
            if (right < 0) {
                right = str.length;
            }else{
                right = right + pos;
            }

            // Return the word, using the located bounds to extract it from the string.
            return {
                left: left,
                right: right,
                text: str.slice(left, right)
            };
        }

    });
