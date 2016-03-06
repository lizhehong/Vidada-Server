'use strict';

angular.module('vidadaApp')
    .factory('MediaLibrary', function ($resource) {
        return $resource('api/libraries/:libraryId', { libraryId: '@id' }, {
            update: {
                method: 'PUT'
            }
        });
    });
