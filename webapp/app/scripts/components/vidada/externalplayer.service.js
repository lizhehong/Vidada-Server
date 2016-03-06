"use strict";

angular.module("vidadaApp")

    .factory("ExternalPlayer", function () {

        function ExternalPlayer() {

            this._externalPlayers = [
                {
                    app: "vlc",
                    makePlayUrl: function (url){ return "vlc://" + url }
                }, {
                    app: "mpv",
                    makePlayUrl: function (url){ return "mpv://" + escape(url) }
                }
            ];

            this.currentPlayer = this._externalPlayers[1];

            this.externPlayUrl = function(streamUrl) {
                return this.currentPlayer.makePlayUrl(streamUrl);
            };
        }


        return ExternalPlayer;

    });

