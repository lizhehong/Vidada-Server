'use strict';

angular.module('vidadaApp')

    .factory('Media', function ($resource) {
        return $resource('api/medias/:mediaId', { mediaId: '@id' }, {
            update: {
                method: 'PUT'
            },
            'get': {
                method: 'GET',
                transformResponse: function (data) {return angular.fromJson(data)},
                isArray: false
            },
            getThumb: {
                url: 'api/medias/:mediaId/thumbnail',
                method: 'GET',
                transformResponse: function (data) {return angular.fromJson(data)},
                isArray: false
            }

        });
    })

    .factory('MediaPage', function ($resource) {
        return {
            load : function(page, pageSize, mediaQuery){
                return $resource('api/medias', {}, {
                    'get': {
                        method: 'GET',
                        transformResponse: function (data) {
                            return angular.fromJson(data)
                        },
                        isArray: false
                    }
                }).get({
                    page: page,
                    pageSize: pageSize,
                    query: mediaQuery.query ,
                    tagExpression: mediaQuery.tagExpression,
                    orderBy: mediaQuery.orderBy.id,
                    reverse: mediaQuery.reversed
                });
            }
        }
    })

    .factory('MediaInfinite', function (MediaPage, $mdToast) {

        var MediaInfinite = function(mediaQuery) {
            this.items = [];
            this.busy = false;
            this.after = '';
            this.itemCount = 0;
            this.lastLoadedPage = -1;
            this.itemsPerPage = 0;
            this.mediaQuery = mediaQuery;
        };

        /** Loads the next page */
        MediaInfinite.prototype.nextPage = function() {
            if (this.busy) return;
            this.busy = true;

            var alreadyLoaded = this.lastLoadedPage+1 * this.itemsPerPage;

            if(this.itemCount >=  alreadyLoaded){
                // Only load new data if we don't have all!
                var pageToLoad = this.lastLoadedPage + 1;

                console.log("Requesting page " + pageToLoad + "...");

                MediaPage.load(pageToLoad, 15, this.mediaQuery).$promise.then(function (data) {

                    this.itemCount = data.totalListSize;
                    this.itemsPerPage = data.maxPageSize;
                    this.lastLoadedPage = data.page;

                    var newItems = data.pageItems;

                    console.log("Got page " + data.page + " with " + newItems.length + " items!");

                    for (var i = 0; i < newItems.length; i++) {
                        this.addMedia(newItems[i]);
                    }
                    this.busy = false;
                }.bind(this), function(response) {
                    // Something went wrong
                    this.busy = false;
                    console.log("Failed to load media page!" + JSON.stringify(response));

                    $mdToast.show(
                        $mdToast.simple()
                            .content('Failed to load media page '+pageToLoad+'!')
                            .hideDelay(2000)
                    );

                }.bind(this));
            }else{
                console.log("Already all pages loaded, skipping nextPage() request!");
                this.busy = false;
            }
        };

        MediaInfinite.prototype.addMedia = function(media) {
            this.items.push(media);
        };

        return MediaInfinite;
    });

