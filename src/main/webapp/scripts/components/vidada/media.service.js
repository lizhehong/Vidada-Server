'use strict';

angular.module('vidadaApp')
    .factory('Media', function ($resource) {
        return $resource('api/medias/:id', { id: '@_id' }, {
            update: {
                method: 'PUT'
            },
            'get': {
                method: 'GET',
                transformResponse: function (data) {return angular.fromJson(data)},
                isArray: false //since your list property is an array
            }

        });
    });

