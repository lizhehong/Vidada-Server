'use strict';

angular.module('vidadaApp')
    .factory('MediaLibrary', function ($resource) {
        return $resource('api/libraries/:id', { id: '@_id' }, {
            update: {
                method: 'PUT'
            },

            delete: {
                method: 'DELETE',
                params: {id: 'id'}
            }

        });
    });
