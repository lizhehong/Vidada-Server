'use strict';

angular.module('vidadaApp')
    .filter('characters', function () {
        return function (input, chars, breakOnWord) {
            if (isNaN(chars)) {
                return input;
            }
            if (chars <= 0) {
                return '';
            }
            if (input && input.length > chars) {
                input = input.substring(0, chars);

                if (!breakOnWord) {
                    var lastspace = input.lastIndexOf(' ');
                    // Get last space
                    if (lastspace !== -1) {
                        input = input.substr(0, lastspace);
                    }
                } else {
                    while (input.charAt(input.length-1) === ' ') {
                        input = input.substr(0, input.length - 1);
                    }
                }
                return input + '...';
            }
            return input;
        };
    })
    .filter('words', function () {
        return function (input, words) {
            if (isNaN(words)) {
                return input;
            }
            if (words <= 0) {
                return '';
            }
            if (input) {
                var inputWords = input.split(/\s+/);
                if (inputWords.length > words) {
                    input = inputWords.slice(0, words).join(' ') + '...';
                }
            }
            return input;
        };
    })
    .filter('bytes', [function () {
        return function(bytes, precision) {
            if (typeof bytes !== 'number') {
                bytes = parseFloat(bytes);
            }

            if (bytes === 0) {
                return '0 B';
            } else if (isNaN(bytes) || !isFinite(bytes)) {
                return '-';
            }

            var isNegative = bytes < 0;
            if (isNegative) {
                bytes = -bytes;
            }

            if (typeof precision !== 'number') {
                precision = parseFloat(precision);
            }

            if (isNaN(precision) || !isFinite(precision)) {
                precision = 1;
            }

            var units = ['B', 'kB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
            var exponent = Math.min(Math.floor(Math.log(bytes) / Math.log(1024)), units.length - 1);
            var number = (bytes / Math.pow(1024, Math.floor(exponent))).toFixed(precision);

            return (isNegative ? '-' : '') +  number +  ' ' + units[exponent];
        };
    }])
    .filter('fullDateTime', function($filter) {
        var angularDateFilter = $filter('date');
        return function(theDate) {
            return angularDateFilter(theDate, 'dd.MM.yyyy HH:mm');
        }
    })

    .filter('secondsToDuration', [function () {
        return function(secondsAll, precision) {
            if (typeof secondsAll !== 'number') {
                secondsAll = parseFloat(secondsAll);
            }

            if (secondsAll === 0) {
                return 'No Duration';
            } else if (isNaN(secondsAll) || !isFinite(secondsAll)) {
                return '-';
            }

            var hours   = Math.floor(secondsAll / 3600);
            var minutes = Math.floor((secondsAll - (hours * 3600)) / 60);
            var seconds = secondsAll - (hours * 3600) - (minutes * 60);

            var duration = hours+'h '+minutes+'min';
            return duration;
        };
    }])

;

