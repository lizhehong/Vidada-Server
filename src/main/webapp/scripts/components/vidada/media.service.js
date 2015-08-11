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
            load : function(page, pageSize){
                return $resource('api/medias', {}, {
                    'get': {
                        method: 'GET',
                        transformResponse: function (data) {return angular.fromJson(data)},
                        isArray: false
                    }
                }).get({ page: page, pageSize: pageSize });
            }
        }
    })

    .factory('MediaInfinite', function (MediaPage) {

        var MediaInfinite = function() {
            this.items = [];
            this.busy = false;
            this.after = '';
            this.itemCount = 0;
            this.lastLoadedPage = -1;
            this.itemsPerPage = 0;
        };

        MediaInfinite.prototype.nextPage = function() {
            if (this.busy) return;
            this.busy = true;

            //if(this.itemCount <=  this.lastLoadedPage * this.itemsPerPage){
                // Only load new data if we don't have all!
                var pageToLoad = this.lastLoadedPage + 1;

                MediaPage.load(pageToLoad, 5).$promise.then(function (data) {

                    this.itemCount = data.totalListSize;
                    this.itemsPerPage = data.maxPageSize;
                    this.lastLoadedPage = data.page;

                    var newItems = data.pageItems;
                    for (var i = 0; i < newItems.length; i++) {
                        this.items.push(newItems[i]);
                    }
                    this.busy = false;
                }.bind(this));
            //}
        };

        return MediaInfinite;
    });
