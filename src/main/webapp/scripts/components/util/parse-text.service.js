'use strict';

angular.module('vidadaApp')
    .service('ParseText', function () {

        /**
         * Replace in the given string the given word with the replacement
         * @param str
         * @param word
         * @param replacement
         * @returns {*}
         */
        this.replaceWord = function(str, word, replacement) {
            return str.slice(0, word.left) + replacement + str.slice(word.right, str.length);
        };

        /**
         * Finds the word in the given string at the given position
         * @param str
         * @param pos
         * @returns {{left: Number, right: Number, text: string}}
         */
        this.findWordAt = function (str, pos) {

            // Perform type conversions.
            str = String(str);
            pos = Number(pos) >>> 0;

            // Search for the word's beginning and end.
            var left = str.slice(0, pos + 1).search(/[\w|\.]+$/),
                right = str.slice(pos).search(/[^\w|\.]/);

            // The last word in the string is a special case.
            if (right < 0) {
                right = str.length;
            }else{
                right = right + pos;
            }

            // Return the word, using the located bounds to extract it from the string.
            return {
                left: left,
                right: right,
                text: str.slice(left, right)
            };
        };

    });
