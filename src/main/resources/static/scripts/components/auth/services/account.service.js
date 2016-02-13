'use strict';

angular.module('vidadaApp')
    .factory('Account', function Account($resource) {
        return $resource('/api/oauth/users/current', {}, {
            'get': { method: 'GET', params: {}, isArray: false  }
        });
    });
