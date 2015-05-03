'use strict';

angular.module('vidadaApp')
    .factory('MediaSynchronisation', function ($http) {

        return {
            /**
             * Synchronizes the media libraries
             * @returns {HttpPromise}
             */
            sync: function methodThatDoesAThing() {
                return $http.post('/api/synchronisation');
            }
        };

    });
