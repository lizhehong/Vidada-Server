
'use strict';

angular.module('vidadaApp')
    .controller('LibrariesController', function ($scope, $window, $mdToast, MediaLibrary, ErrorHandler, $mdDialog) {

        $scope.libraries = [];

        $scope.status = "";

        $scope.updateLibraries = function() {
            MediaLibrary.query().$promise.then(function (libraries) {
                $scope.libraries = libraries;
            });
        };

        $scope.deleteLibrary = function(ev, currentLibrary){
            // Appending dialog to document.body to cover sidenav in docs app
            var confirm = $mdDialog.confirm()
                .title('Delete media library?')
                .content('Are you sure you want to delete your media library ' + currentLibrary.name)
                .targetEvent(ev)
                .ok('Delete it!')
                .cancel('Lets keep it.');
            $mdDialog.show(confirm).then(function() {
                // User wants to delete it!

                currentLibrary.$delete(function() {
                    $mdToast.show(
                        $mdToast.simple()
                            .content('Successfully deleted media-library!')
                            .hideDelay(2000)
                    );
                    $scope.updateLibraries();
                }, function(err){
                    console.log(err);
                    $mdToast.show(
                        $mdToast.simple()
                            .content('Failed to delete media-library!')
                            .hideDelay(2000)
                    );
                });

            });
        };

        $scope.newLibrary = function(ev) {

            $mdDialog.show({
                controller: NewLibraryModelCtrl,
                templateUrl: 'AddNewLibrary.html',
                parent: angular.element(document.body),
                targetEvent: ev,
                clickOutsideToClose: true
            })
            .then(function(library) {
                // User has accepted the dialog
                MediaLibrary.save(library, function () {
                    $scope.updateLibraries();
                    $mdToast.show(
                        $mdToast.simple()
                            .content('Created library: ' + library.name)
                            .hideDelay(2000)
                    );
                }, function(err){
                    ErrorHandler.showToast('Failed to create: ' + library.name + ' with rootPath ' + library.rootPath, err);
                });
            });
        };

        $scope.updateLibraries();


        function NewLibraryModelCtrl($scope, $mdDialog){

            $scope.myLibrary = {
                name : null,
                rootPath : null,
                ignoreMusic : true,
                ignoreVideos: false,
                ignoreImages: false
            };


            $scope.ok = function () {
                $mdDialog.hide($scope.myLibrary);
            };

            $scope.hide = function() {
                $mdDialog.hide();
            };

            $scope.cancel = function () {
                $mdDialog.cancel();
            };
        }


    });




