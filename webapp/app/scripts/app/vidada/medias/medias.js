'use strict';

angular.module('vidadaApp')
    .config(function ($stateProvider) {
        $stateProvider

            .state('medias', {
                parent: 'vidada',
                url: '/medias?query&tagExpression&sort&reversed',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'medias.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/vidada/medias/medias.html',
                        controller: 'MediasController'
                    }
                },
                params: {
                    query: {
                        value: '',
                        squash: true
                    },
                    tagExpression: {
                        value: '',
                        squash: true
                    },
                    sort: {
                        value: 'ADDEDDATE',
                    },
                    reversed: {
                        value: 'false',
                        squash: true
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('medias');
                        return $translate.refresh();
                    }]
                }
            })
            .state('medias_detail', {
                parent: 'medias',
                url: "/{mediaId}",
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'medias.title'
                },

                templateUrl: 'scripts/app/vidada/medias/medias.detail.html',
                controller: 'MediasDetailController'
            });
    });

