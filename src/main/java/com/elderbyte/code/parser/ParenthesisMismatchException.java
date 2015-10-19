package com.elderbyte.code.parser;

/**
 * Thrown when parenthesis don't match, i.e. when an open one doesnt have its closing one.
 */
public class ParenthesisMismatchException extends RuntimeException {
    public ParenthesisMismatchException(String message){
        super(message);
    }
}
