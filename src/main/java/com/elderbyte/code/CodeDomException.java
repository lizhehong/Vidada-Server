package com.elderbyte.code;

/**
 * Thrown when there was a problem in handling the code dome, such
 * as scanner or parse error, AST builder errors.
 *
 * THis is the base exception for all elderbyte.code.* Exception
 */
public class CodeDomException extends RuntimeException {


    public CodeDomException(String message){
        super(message);
    }

    public CodeDomException(String message, Exception cause){
        super(message, cause);

    }
}
