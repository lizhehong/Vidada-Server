'use strict';

angular.module('vidadaApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('tags', {
                parent: 'vidada',
                url: '/tags',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'tags.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/vidada/tags/tags.html',
                        controller: 'TagsController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tags');
                        return $translate.refresh();
                    }]
                }
            });
    });
