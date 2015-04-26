
'use strict';

angular.module('vidadaApp')
    .service('ErrorHandler', function (ngToast) {

        this.showToast = function (msg, err) {

            ngToast.create({
                className: 'danger',
                content: msg + ' - ' + err.data.error + ' - ' + err.data.exception
            });

        };
    })
    ;

