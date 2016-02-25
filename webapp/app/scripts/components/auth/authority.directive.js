'use strict';


angular.module('vidadaApp')
    .directive('hasAnyRole', ['Principal', function (Principal) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var setVisible = function () {
                        element.removeClass('hidden');
                    },
                    setHidden = function () {
                        element.addClass('hidden');
                    },
                    defineVisibility = function (reset) {
                        var result;
                        if (reset) {
                            setVisible();
                        }

                        result = Principal.isInAnyRole(roles);
                        if (result) {
                            setVisible();
                        } else {
                            setHidden();
                        }
                    },
                    roles = attrs.hasAnyRole.replace(/\s+/g, '').split(',');

                if (roles.length > 0) {
                    defineVisibility(true);
                }
            }
        };
    }])
    .directive('poop', function() {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {

                console.log("Hello directive myCustomer")
            }
        };
    })
    .directive('hasRole', ['Principal', function (Principal) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {

                var setVisible = function () {
                    element.removeClass('hidden');
                };
                var setHidden = function () {
                    element.addClass('hidden');
                };
                var defineVisibility = function (reset) {
                    var result;
                    if (reset) {
                        setVisible();
                    }

                    result = Principal.isInRole(role);
                    if (result) {
                        setVisible();
                    } else {
                        setHidden();
                    }
                };

                var role = attrs.hasRole.replace(/\s+/g, '');

                console.log("Executing hasRole Directive: -->'" + role + "' :: " + JSON.stringify(attrs));

                if (role.length > 0) {
                    defineVisibility(true);
                }
            }
        };
    }]);
