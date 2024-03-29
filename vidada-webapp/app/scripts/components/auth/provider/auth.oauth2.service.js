'use strict';

angular.module('vidadaApp')
    .factory('AuthServerProvider', function loginService($http, localStorageService) {
        return {
            login: function(credentials) {

                return $http.post('/api/oauth/login', credentials).then(function(response) {

                    var jwt = response.data.token;

                    console.log('Login success, got token: ' + jwt);

                    localStorageService.set('token', {
                        access_token: jwt,
                        expires_at: null
                    });

                    return jwt;

                }, function(error) {
                    console.log('Login Failed: ' + error);
                });
            },
            logout: function() {
                // logout from the server

                console.log('Logging out, clearing local JWT!');

                $http.post('/api/oauth/logout').then(function() {
                    localStorageService.clearAll();
                }, function(){
                    localStorageService.clearAll();
                });
            },
            getToken: function () {
                return localStorageService.get('token');
            },
            hasValidToken: function () {
                var token = this.getToken();
                //return token && token.expires_at && token.expires_at > new Date().getTime();
                return token && true;
            }
        };
    });

