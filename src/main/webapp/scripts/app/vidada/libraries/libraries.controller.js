
'use strict';

angular.module('vidadaApp')
    .controller('LibrariesController', function ($scope, $window, MediaLibrary, ngToast, ErrorHandler, $modal) {

        $scope.libraries = [];

        $scope.updateLibraries = function() {
            MediaLibrary.query().$promise.then(function (libraries) {
                $scope.libraries = libraries;
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


        $scope.deleteLibrary = function(library) {
            //if (popupService.showPopup('Really delete this?')) {
                library.$delete(function() {
                    ngToast.create('Successfully deleted media-library!');
                    $scope.updateLibraries();
                });
            //}
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
