package com.elderbyte.code.parser;

import com.elderbyte.code.CodeDomException;

/**
 * Thrown when generating the AST has failed
 */
public class ASTGeneratorException extends CodeDomException {

    public ASTGeneratorException(String message){
        super(message);
    }
}
