'use strict';

angular.module('vidadaApp')
    .directive('updateTitle', function($rootScope, $timeout, $translate) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {


                console.log('Setting up updateTitle directive, listening to $stateChangeSuccess');

                $rootScope.$on('$stateChangeSuccess',
                    function(event, toState, toParams, fromState, fromParams){

                        var title = 'Vidada';
                        if (toState.data && toState.data.pageTitle){
                            var translationKey = toState.data.pageTitle;
                            $translate(translationKey).then(function( translation ){
                                title = translation;
                                $timeout(function() {
                                    element.text(title);
                                }, 0, false);
                            });
                        }
                    });
            }
        };
    })
    ;
