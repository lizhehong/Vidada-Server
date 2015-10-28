
'use strict';

angular.module('vidadaApp')
    .controller('LibrariesController', function ($scope, $window, MediaLibrary, ngToast, ErrorHandler, $modal, $mdDialog) {

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
                    ngToast.create('Successfully deleted media-library!');
                    $scope.updateLibraries();
                }, function(err){
                    console.log(err);
                    ngToast.create('Failed to delete media-library! ');
                });

            });
        };


        $scope.editLibrary = function(ev, currentLibrary){
            $mdDialog.show({
                controller: 'EditLibraryModelCtrl',
                templateUrl: 'EditLibrary.html',
                parent: angular.element(document.body),
                targetEvent: ev,
                clickOutsideToClose:true,
                locals : {
                    library : currentLibrary
                }
            })
            .then(function(library) {
                // User has accepted the dialog

                MediaLibrary.update(library, function () {
                    $scope.updateLibraries();
                    ngToast.create('Updated library: ' + library.name);
                }, function(err){
                    ErrorHandler.showToast('Failed to update: ' + library.name + ' with rootPath ' + library.rootPath, err);
                });
            }, function() {
                $scope.status = 'You cancelled the dialog.';
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

angular.module('vidadaApp').controller('EditLibraryModelCtrl', function ($scope, $mdDialog, library) {

    $scope.myLibrary = library;

    $scope.ok = function () {
        $mdDialog.hide($scope.myLibrary);
    };

    $scope.hide = function() {
        $mdDialog.hide();
    };
    $scope.cancel = function() {
        $mdDialog.cancel();
    };
});
