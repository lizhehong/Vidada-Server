'use strict';

angular.module('vidadaApp')

    .factory('Tag', function ($resource) {
        return $resource('api/tags/:id', { id: '@_id' }, {
            update: {
                method: 'PUT'
            },

            delete: {
                method: 'DELETE',
                params: {id: 'id'}
            }

        });
    });
