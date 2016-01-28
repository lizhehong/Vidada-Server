
'use strict';

angular.module('vidadaApp')
    .controller('EditLibraryController', function ($scope, $stateParams, MediaLibrary, $window, $mdToast, ErrorHandler, $modal, $mdDialog) {

        $scope.myLibraryId = $stateParams.libraryId;
        $scope.myLibrary = {};

        MediaLibrary.get({libraryId: $scope.myLibraryId}).$promise.then(function (library) {
            $scope.myLibrary = library;
        }, function (err) {
            console.log("Failed to fetch library " + $scope.myLibraryId);
        });


        $scope.saveLibrary = function(){
            // User has accepted the dialog
            var library = $scope.myLibrary;

            MediaLibrary.update(library, function () {
                //$scope.updateLibraries();
                $mdToast.show(
                    $mdToast.simple()
                        .content('Updated library: ' + library.name)
                        .hideDelay(2000)
                );
            }, function(err){
                ErrorHandler.showToast('Failed to update: ' + library.name + ' with rootPath ' + library.rootPath, err);
            });
        };

    });




