package com.elderbyte.code.parser;

/**
 * Represents a token emitted by the scanner.
 * This class is immutable
 */
public class Token {

    private final TokenType type;
    private final String value;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new Token
     * @param type
     * @param value
     */
    public Token(TokenType type, String value){
        this.type = type;
        this.value= value;
    }

    /***************************************************************************
     *                                                                         *
     * Public Properties                                                       *
     *                                                                         *
     **************************************************************************/

    /**
     * Gets the token type
     * @return
     */
    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString(){
        return "[" + getValue() +" "+ getType() + "]";
    }
}
