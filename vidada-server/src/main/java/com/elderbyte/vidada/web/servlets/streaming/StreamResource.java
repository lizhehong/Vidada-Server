package com.elderbyte.vidada.web.servlets.streaming;

import java.io.InputStream;
import java.util.function.Supplier;

/**
 * Represents a resource to be streamed
 */
 public class StreamResource {

    private final String name;
    private final long length;
    private final long lastModified;
    private final Supplier<InputStream> resourceStreamProvider;
    private final String mimeType;

    /**
     *
     * @param name The display name of the resource
     * @param length The total length in bytes
     * @param lastModified Last modified time-stamp
     * @param resourceStreamProvider The inputstream provider which delivers the data
     * @param mimeType The mime-type of this resource
     */
    public StreamResource(String name, long length, long lastModified, Supplier<InputStream> resourceStreamProvider, String mimeType) {
        this.name = name;
        this.length = length;
        this.lastModified = lastModified;
        this.resourceStreamProvider = resourceStreamProvider;
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

    public InputStream openInputStream() {
        return resourceStreamProvider.get();
    }

    public String getMimeType() {
        return mimeType;
    }
}
