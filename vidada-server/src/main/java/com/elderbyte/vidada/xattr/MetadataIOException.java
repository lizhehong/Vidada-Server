package com.elderbyte.vidada.xattr;

import java.io.IOException;

/**
 * Thrown when metadata could not be read or written
 */
public class MetadataIOException extends IOException {

    public MetadataIOException(String message, Throwable cause){
        super(message, cause);
    }

    public MetadataIOException(String message){
        super(message);
    }

}
