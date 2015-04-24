'use strict';

angular.module('vidadaApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('vidada', {
                abstract: true,
                parent: 'site'
            });
    });
