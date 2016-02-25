
'use strict';

angular.module('vidadaApp')
    .service('ErrorHandler', function ($mdToast) {

        this.showToast = function (msg, err) {
            $mdToast.show(
                $mdToast.simple()
                    .content('Error: ' + msg + ' - ' + err.data.error + ' - ' + err.data.exception)
                    .position('top')
                    .hideDelay(2000)
            );
        };
    });

