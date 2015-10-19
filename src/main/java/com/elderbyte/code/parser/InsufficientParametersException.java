package com.elderbyte.code.parser;

/**
 * Created by IsNull on 18.10.15.
 */
public class InsufficientParametersException extends ASTGeneratorException {

    public InsufficientParametersException(String message){
        super(message);
    }
}
