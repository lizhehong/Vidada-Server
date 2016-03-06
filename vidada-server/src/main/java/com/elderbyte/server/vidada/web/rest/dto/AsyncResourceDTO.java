package com.elderbyte.server.vidada.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Represents an resource which is (probably) not yet ready
 */
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AsyncResourceDTO {

    /** Link to the resource if available **/
    private String resourceUrl = null;
    private AsyncResourceState state;

    /**
     * Constant which represents a currently processing resource
     */
    public static final AsyncResourceDTO Processing = new AsyncResourceDTO(null, AsyncResourceState.Processing);

    public static AsyncResourceDTO ofResource(String resourceUrl){
        return new AsyncResourceDTO(resourceUrl, AsyncResourceState.Ready);
    }


    protected AsyncResourceDTO(){ }

    private AsyncResourceDTO(String resource, AsyncResourceState state){
        this.resourceUrl = resource;
        this.state = state;
    }

    private enum AsyncResourceState {

        /**
         * The requested resource is not ready, still being created / processed.
         */
        Processing,

        /**
         * The resource is ready and available.
         */
        Ready
    }


}
