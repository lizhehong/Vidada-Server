package com.elderbyte.code.parser;

import com.elderbyte.code.CodeDomException;

/**
 * Thrown when parenthesis don't match, i.e. when an open one doesnt have its closing one.
 */
public class ParenthesisMismatchException extends CodeDomException {
    public ParenthesisMismatchException(String message){
        super(message);
    }
}
