'use strict';

angular.module('vidadaApp')
    .factory('Activate', function ($resource) {
        return $resource('api/oauth/activate', {}, {
            'get': { method: 'GET', params: {}, isArray: false}
        });
    });


