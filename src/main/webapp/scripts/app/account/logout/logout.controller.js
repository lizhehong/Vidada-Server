'use strict';

angular.module('vidadaApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
