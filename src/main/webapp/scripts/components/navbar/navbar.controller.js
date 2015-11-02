'use strict';

angular.module('vidadaApp')
    .controller('NavbarController', function ($scope, $location, $state, $mdSidenav, Auth, Principal, $mdToast, MediaSynchronisation, ErrorHandler) {

        Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
        });


        $scope.$state = $state;

        $scope.logout = function () {
            Auth.logout();
            $state.go('home');
        };

        $scope.toggleLeftSideNav = function() {
            $mdSidenav('left').toggle();
        };

        $scope.syncAll = function () {

            MediaSynchronisation.sync()
                .success(function() {
                    $mdToast.show(
                        $mdToast.simple()
                            .content('Synchronisation started ...')
                            .hideDelay(2000)
                    );
                }).error(function(data, status) {
                    ErrorHandler.showToast('Failed to start synchronisation', data + ' - ' + status);
                });

        };

        $scope.openMenu = function($mdOpenMenu, ev) {
            $mdOpenMenu(ev);
        };


    });
