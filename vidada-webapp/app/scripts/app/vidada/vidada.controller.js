
'use strict';

angular.module('vidadaApp')
    .controller('VidadaController', function ($rootScope, $scope, $location, $state, $mdSidenav, $mdMedia,
                                              Auth, Principal, $mdToast, MediaSynchronisation, ErrorHandler) {

        $scope.$state = $state;

        /** Setting: If true, on desktops the side nav is displayed next to content  **/
        $rootScope.nextToContentOnDesktop = true;

        $rootScope.desktopOpen = true;

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
            return $rootScope.nextToContentOnDesktop && $mdMedia('gt-md') && $rootScope.desktopOpen;
        };

        /**
         * Occurs when the user clicks the hamburger menu
         * This should toggle the side navigation.
         */
        $scope.toggleLeftSideNav = function() {

            if($rootScope.nextToContentOnDesktop && $mdMedia('gt-md')){
                // We are on a desktop with a always visible nav
                // (so we have to toggle isLockedOpen)
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

