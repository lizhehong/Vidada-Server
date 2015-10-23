
'use strict';

angular.module('vidadaApp')
    .controller('TagsController', function ($scope, $window, Tag) {

        $scope.tags = [];

        $scope.updateTags = function() {
            Tag.query().$promise.then(function (tags) {
                $scope.tags = tags;
            });
        };

        $scope.updateTags();

    });

