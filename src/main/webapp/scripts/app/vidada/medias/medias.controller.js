
'use strict';

angular.module('vidadaApp')
    .controller('MediasController', function ($scope, Media, MediaInfinite) {

        $scope.query = "";
        $scope.tagExpression="";
        $scope.mediaService = new MediaInfinite("", "");

        $scope.dirty = {};


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

        function suggest_state(term) {

            var tokens = term.split(' ');
            term = tokens[tokens.length-1];

            var suggestions = findSuggestions(term);

            console.log("Found " + suggestions.length + " suggestions for '" + term + "'!");

            return suggestions;
        }



        $scope.autocomplete_options = {
            suggest: suggest_state
        };

    });
