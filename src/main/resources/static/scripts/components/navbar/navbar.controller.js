'use strict';

angular.module('vidadaApp')
    .controller('NavbarController', function ($rootScope, $scope, $location, $state, $mdSidenav, $mdMedia, Auth, Principal, $mdToast, MediaSynchronisation, ErrorHandler) {

        $scope.$state = $state;
        $scope.mainMenuItems = [
            {
                name: "Media",
                state: "medias",
                icon: "ondemand_video"

            },
            {
                name: "Media Libraries",
                state: "libraries",
                icon: "video_library"
            },
            {
                name: "Tags",
                state: "tags",
                icon: "bookmark_border"
            },
            {
                name: "Settings",
                state: "settings",
                icon: "settings"
            }
        ];

        $rootScope.desktopOpen = true;


        Principal.identity().then(function(account) {
            $scope.account = account;
        });

        $scope.isAuthenticated = function(){
            return Principal.isAuthenticated();
        };


        $scope.logout = function () {
            Auth.logout();
            $state.go('home');
        };

        $scope.isLockedOpen = function(){
            return $mdMedia('gt-md') && $rootScope.desktopOpen;
        };

        $scope.toggleLeftSideNav = function() {

            if($mdMedia('gt-md')){
                // We are on a desktop
                $rootScope.desktopOpen = !$rootScope.desktopOpen;
            }else{
                $mdSidenav('left').toggle();
            }
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
