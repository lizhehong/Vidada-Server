'use strict';

angular.module('vidadaApp')
    .factory('Account', function Account($resource) {
        return $resource('/api/users/current', {}, {
            'get': { method: 'GET', params: {}, isArray: false  }
        });
    });
