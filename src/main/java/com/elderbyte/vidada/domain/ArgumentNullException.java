package com.elderbyte.vidada.domain;

/**
 * Thrown when a method argument which is required (@NonNull) was Null!
 */
public class ArgumentNullException extends IllegalArgumentException {


    public ArgumentNullException(String argumentName) {
        super("The argument '" + argumentName + "' was NULL!");
    }


    public ArgumentNullException(String argumentName, Throwable cause) {
        super("The argument '" + argumentName + "' was NULL!", cause);
    }
}

