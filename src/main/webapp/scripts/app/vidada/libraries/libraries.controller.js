
'use strict';

angular.module('vidadaApp')
    .controller('LibrariesController', function ($scope, $window, MediaLibrary, ngToast, ErrorHandler, $modal) {

        $scope.libraries = [];

        $scope.updateLibraries = function() {
            MediaLibrary.query().$promise.then(function (libraries) {
                $scope.libraries = libraries;
            });
        };

        $scope.editLibrary = function(currentLibrary){

            var modalInstance = $modal.open({
                templateUrl: 'EditLibrary.html',
                controller: 'EditLibraryModelCtrl',
                resolve: {
                    library: function () {
                        return currentLibrary;
                    }
                }
            });

            modalInstance.result.then(function (library) {
                // User has accepted the dialog

                MediaLibrary.save(library, function () {
                    $scope.updateLibraries();
                    ngToast.create('Updated library: ' + library.name);
                }, function(err){
                    ErrorHandler.showToast('Failed to update: ' + library.name + ' with rootPath ' + library.rootPath, err);
                });

            }, function () {
                $scope.updateLibraries();
            });

        };


        $scope.newLibrary = function() {

            var modalInstance = $modal.open({
                templateUrl: 'AddNewLibrary.html',
                controller: 'NewLibraryModelCtrl'
            });

            modalInstance.result.then(function (library) {
                // User has accepted the dialog

                MediaLibrary.save(library, function () {
                    $scope.updateLibraries();
                    ngToast.create('Created library: ' + library.name);
                }, function(err){
                    ErrorHandler.showToast('Failed to create: ' + library.name + ' with rootPath ' + library.rootPath, err);
                });


            }, function () {
                // User has canceled the dialog
                ngToast.create('Modal dismissed at: ' + new Date());
            });

        };


        $scope.updateLibraries();

    });


angular.module('vidadaApp').controller('NewLibraryModelCtrl', function ($scope, $modalInstance) {


    $scope.myLibrary = {
        name : null,
        rootPath : null,
        ignoreMusic : true,
        ignoreVideos: false,
        ignoreImages: false
    };


    $scope.ok = function () {
        $modalInstance.close($scope.myLibrary);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});

angular.module('vidadaApp').controller('EditLibraryModelCtrl', function ($scope, ngToast, $modalInstance, library) {

    $scope.myLibrary = library;

    $scope.deleteLibrary = function() {
        $scope.myLibrary.$delete(function() {
            ngToast.create('Successfully deleted media-library!');
            $modalInstance.dismiss('deleted');
        });
    };

    $scope.ok = function () {
        $modalInstance.close($scope.myLibrary);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});
