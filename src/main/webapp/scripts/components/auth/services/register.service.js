'use strict';

angular.module('vidadaApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


