package com.elderbyte.vidada.web.servlets;

import archimedes.core.io.locations.ResourceLocation;

    /**
     * Represents a resource to be streamed
     */
     public class StreamResource {

        private final String name;
        private final long length;
        private final long lastModified;
        private final ResourceLocation resource;

        public StreamResource(String name, long length, long lastModified, ResourceLocation resource) {
            this.name = name;
            this.length = length;
            this.lastModified = lastModified;
            this.resource = resource;
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
    }
