'use strict';

angular.module('vidadaApp')
    .factory('Media', function ($resource) {
        return $resource('api/medias/:id', { id: '@_id' }, {
            update: {
                method: 'PUT'
            },
            'get': {
                method: 'GET',
                transformResponse: function (data) {return angular.fromJson(data)},
                isArray: false
            }

        });
    })

    .factory('MediaPage', function ($resource) {
        return {
            load : function(page, pageSize, query){
                return $resource('api/medias', {}, {
                    'get': {
                        method: 'GET',
                        transformResponse: function (data) {return angular.fromJson(data)},
                        isArray: false
                    }
                }).get({ page: page, pageSize: pageSize, query: query });
            }
        }
    })

    .factory('MediaInfinite', function (MediaPage) {

        var MediaInfinite = function(query) {
            this.items = [];
            this.busy = false;
            this.after = '';
            this.itemCount = 0;
            this.lastLoadedPage = -1;
            this.itemsPerPage = 0;
            this.query = query;
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

                MediaPage.load(pageToLoad, 15, this.query).$promise.then(function (data) {

                    this.itemCount = data.totalListSize;
                    this.itemsPerPage = data.maxPageSize;
                    this.lastLoadedPage = data.page;

                    var newItems = data.pageItems;

                    console.log("Got page " + data.page + " with " + newItems.length + " items!");

                    for (var i = 0; i < newItems.length; i++) {
                        this.items.push(newItems[i]);
                    }
                    this.busy = false;
                }.bind(this));
            }else{
                console.log("Already all pages loaded, skipping nextPage() request!");
                this.busy = false;
            }
        };

        return MediaInfinite;
    });

