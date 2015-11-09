'use strict';

angular.module('vidadaApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('libraries', {
                parent: 'vidada',
                url: '/libraries',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'libraries.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/vidada/libraries/libraries.html',
                        controller: 'LibrariesController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('libraries');
                        return $translate.refresh();
                    }]
                }
            });
    });

