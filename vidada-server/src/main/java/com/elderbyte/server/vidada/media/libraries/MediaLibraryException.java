package com.elderbyte.server.vidada.media.libraries;

/**
 * Thrown when there was a problem with a media library
 */
public class MediaLibraryException extends RuntimeException {

    public MediaLibraryException(String message, Throwable exception){
        super(message, exception);
    }

    public MediaLibraryException(String message){
        super(message);
    }
}
