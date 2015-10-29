package com.elderbyte.vidada.web.servlets.streaming;

import archimedes.core.io.locations.ResourceLocation;

/**
 * Represents a resource to be streamed
 */
 public class StreamResource {

    private final String name;
    private final long length;
    private final long lastModified;
    private final ResourceLocation resource;
    private final String mimeType;

    public StreamResource(String name, long length, long lastModified, ResourceLocation resource, String mimeType) {
        this.name = name;
        this.length = length;
        this.lastModified = lastModified;
        this.resource = resource;
        this.mimeType = mimeType;
    }

    public String getName() {
        return name;
    }

    public long getLength() {
        return length;
    }

    public long getLastModified() {
        return lastModified;
    }

    public ResourceLocation getResource() {
        return resource;
    }

    public String getMimeType() {
        return mimeType;
    }
}
