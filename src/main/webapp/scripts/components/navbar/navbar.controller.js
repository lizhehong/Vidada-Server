'use strict';

angular.module('vidadaApp')
    .controller('NavbarController', function ($scope, $location, $state, Auth, Principal, ngToast, MediaSynchronisation, ErrorHandler) {

        Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
        });


        $scope.$state = $state;

        $scope.logout = function () {
            Auth.logout();
            $state.go('home');
        };

        $scope.syncAll = function () {

            MediaSynchronisation.sync()
                .success(function() {
                    ngToast.create('Synchronisation started ...');
                }).error(function(data, status) {
                    ErrorHandler.showToast('Failed to start synchronisation', data + ' - ' + status);
                });

        };


    });
